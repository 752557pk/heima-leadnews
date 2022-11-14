package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.minio.service.FileStorageService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleListDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.searhc.pojo.SearchArticleVo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ap_article Service 接口实现
 * </p>
 *
 * @author lenovo
 * @since 2022-10-29 11:29:44
 */
@Service
@Transactional
@Slf4j
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    @Autowired
    Configuration configuration;
    @Autowired
    FileStorageService fileStorageService;
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private ApArticleMapper apArticleMapper;
    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;
    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    /**
     * @param articleListDto
     * @param type           0= load 1=loadNew 2= loadMore
     * @return
     */
    @Override
    public ResponseResult load(ArticleListDto articleListDto, int type) {
        if (type == 0) {
            articleListDto.setMinBehotTime(new Date());
        }
        if (type == 1) {
            articleListDto.setMinBehotTime(null);
        } else {
            articleListDto.setMaxBehotTime(null);
        }
        List<ApArticle> data = apArticleMapper.load(articleListDto);
        return ResponseResult.okResult(data);
    }

    /**
     * 要新增也要修改
     *
     * @param articleDto
     * @return
     */
    @Override
    public ResponseResult saveOrUpdate(ArticleDto articleDto) throws Exception {
        // 保存到ap_aritcle：判断是否存在，如果是则修改，如果不是则添加
        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(articleDto, apArticle);
        if (articleDto.getId() == null) {// 新增
            apArticleMapper.insert(apArticle);
            // 保存到ap_article_config
            ApArticleConfig apArticleConfig = new ApArticleConfig();
            apArticleConfig.setIsDelete(0);
            apArticleConfig.setIsDown(0);
            apArticleConfig.setIsForward(1);
            apArticleConfig.setIsComment(1);
            apArticleConfig.setArticleId(apArticle.getId());
            apArticleConfigMapper.insert(apArticleConfig);
            // 保存到ap_article_content
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setContent(articleDto.getContent());
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContentMapper.insert(apArticleContent);
            // 回填
            articleDto.setId(apArticle.getId());
        } else {// 修改
            apArticleMapper.updateById(apArticle);
            // 修改内容
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setContent(articleDto.getContent());
            // 1。更新的对象；2.更新的条件
            LambdaQueryWrapper<ApArticleContent> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ApArticleContent::getArticleId, articleDto.getId());
            apArticleContentMapper.update(apArticleContent, wrapper);
        }
        // 生成静态页面
        genHtml(apArticle,articleDto);


        return ResponseResult.okResult(apArticle.getId());
    }

    /**
     * 生成静态详情并上传minio
     *
     * @param articleDto
     * @throws Exception
     */
    @Async
    public void genHtml(ApArticle apArticle , ArticleDto articleDto) throws Exception {
        // 获取模板，并传入数据，生成静态页面保存到d盘
        Template template = configuration.getTemplate("article.ftl");
        String content = articleDto.getContent();
        List<HashMap> list = JSON.parseArray(content, HashMap.class);
        Map<String, Object> data = new HashMap<>();
        data.put("content", list);
        // 生成结果
        StringWriter stringWriter = new StringWriter();
        template.process(data, stringWriter);

        // 上传minio
        ByteArrayInputStream inputStream = new ByteArrayInputStream(stringWriter.toString().getBytes());
        String path = fileStorageService.uploadHtmlFile("", articleDto.getId() + ".html", inputStream);
        System.out.println(path);
        // 回填地址数据
        ApArticle temp = new ApArticle();
        temp.setStaticUrl(path);
        temp.setId(articleDto.getId());
        apArticleMapper.updateById(temp);

        //发送kafka对es索引更新或修改
        SearchArticleVo searchArticleVo = new SearchArticleVo();
        BeanUtils.copyProperties(apArticle, searchArticleVo);
        searchArticleVo.setLayout(articleDto.getLayout());
        searchArticleVo.setContent(articleDto.getContent());

        kafkaTemplate.send("es_index_update", JSON.toJSONString(searchArticleVo));
    }


}
