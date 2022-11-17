package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.minio.service.FileStorageService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleListDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.vos.HotArticleVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.mess.ArticleVisitStreamMess;
import com.heima.model.searhc.pojo.SearchArticleVo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

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
    ApArticleMapper apArticleMapper;
    @Autowired
    ApArticleConfigMapper apArticleConfigMapper;
    @Autowired
    ApArticleContentMapper apArticleContentMapper;
    @Autowired
    CacheService cacheService;

    /**
     * @param articleListDto
     * @param type           0= load 1=loadNew 2= loadMore
     * @return
     */
    @Override
    public ResponseResult load(ArticleListDto articleListDto, int type) {
        if (type == 0) {
            //直接从redis里面先查询
            String json = cacheService.get("APP_LIST_CACHE_" + articleListDto.getTag());
            if (json != null) {
                List<HotArticleVo> hotArticleVos = JSON.parseArray(json, HotArticleVo.class);
                return ResponseResult.okResult(hotArticleVos);
            }
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
        genHtml(apArticle, articleDto);


        return ResponseResult.okResult(apArticle.getId());
    }

    /**
     * 给每个频道缓存30条热点数据
     */
    @Override
    public void cacheArticle() {
        //查询5天前的文章数据
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.DAY_OF_MONTH, -5);
        //不要时分秒
        instance.set(Calendar.HOUR, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);

        List<ApArticle> apArticles = apArticleMapper.selectList(Wrappers
                .lambdaQuery(ApArticle.class)
                .gt(ApArticle::getPublishTime, instance.getTime()));


        //计算每个文章的分值
        List<HotArticleVo> hotArticleVoList = scoreArticle(apArticles);

        //缓存每个频道
        //把list按频道分组
        HashMap<Long, List<HotArticleVo>> group = new HashMap<>();
        hotArticleVoList.forEach(hotArticleVo -> {
            List<HotArticleVo> hotArticleVos;
            hotArticleVos = group.get(hotArticleVo.getChannelId());
            hotArticleVos.add(hotArticleVo);
            group.put(hotArticleVo.getChannelId(), hotArticleVos);
        });

        for (Long channelId : group.keySet()) {
            List<HotArticleVo> hotArticleVos = group.get(channelId);
        }

        group.forEach((channelId, apArticleVos) -> {
            //缓存每个频道
            cacheToRedis(apArticleVos, "APP_LIST_CACHE_" + channelId);
        });


        //缓存推荐频道
        cacheToRedis(hotArticleVoList, "APP_LIST_CACHE_" + "__all__");
    }

    /**
     * 更新文章的分值  同时更新缓存中的热点文章数据
     *
     * @param mess
     */
    @Override
    public void updateScore(ArticleVisitStreamMess mess) {
        //1.更新文章的阅读、点赞、收藏、评论的数量
        ApArticle apArticle = updateArticle(mess);
        //2.计算文章的分值
        Integer score = computeScore(apArticle);
        score = score * 3;

        //3.替换当前文章对应频道的热点数据
        replaceDataToRedis(apArticle, score, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + apArticle.getChannelId());

        //4.替换推荐对应的热点数据
        replaceDataToRedis(apArticle, score, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + ArticleConstants.DEFAULT_TAG);

    }


    /**
     * 替换数据并且存入到redis
     *
     * @param apArticle
     * @param score
     * @param s
     */
    private void replaceDataToRedis(ApArticle apArticle, Integer score, String s) {
        String articleListStr = cacheService.get(s);
        if (StringUtils.isNotBlank(articleListStr)) {
            List<HotArticleVo> hotArticleVoList = JSON.parseArray(articleListStr, HotArticleVo.class);

            boolean flag = true;

            //如果缓存中存在该文章，只更新分值
            for (HotArticleVo hotArticleVo : hotArticleVoList) {
                if (hotArticleVo.getId().equals(apArticle.getId())) {
                    hotArticleVo.setScore(score);
                    flag = false;
                    break;
                }
            }

            //如果缓存中不存在，查询缓存中分值最小的一条数据，进行分值的比较，如果当前文章的分值大于缓存中的数据，就替换
            if (flag) {
                if (hotArticleVoList.size() >= 30) {
                    hotArticleVoList = hotArticleVoList.stream()
                            .sorted(Comparator.comparing(HotArticleVo::getScore).reversed())
                            .collect(Collectors.toList());

                    HotArticleVo lastHot = hotArticleVoList.get(hotArticleVoList.size() - 1);
                    if (lastHot.getScore() < score) {
                        hotArticleVoList.remove(lastHot);
                        HotArticleVo hot = new HotArticleVo();
                        BeanUtils.copyProperties(apArticle, hot);
                        hot.setScore(score);
                        hotArticleVoList.add(hot);
                    }


                } else {
                    HotArticleVo hot = new HotArticleVo();
                    BeanUtils.copyProperties(apArticle, hot);
                    hot.setScore(score);
                    hotArticleVoList.add(hot);
                }
            }
            //缓存到redis
            hotArticleVoList = hotArticleVoList.stream().sorted(Comparator.comparing(HotArticleVo::getScore).reversed()).collect(Collectors.toList());
            cacheService.set(s, JSON.toJSONString(hotArticleVoList));

        }
    }

    /**
     * 更新文章行为数量
     *
     * @param mess
     */
    private ApArticle updateArticle(ArticleVisitStreamMess mess) {
        ApArticle apArticle = getById(mess.getArticleId());
        apArticle.setCollection(apArticle.getCollection() == null ? 0 : apArticle.getCollection() + mess.getCollect());
        apArticle.setComment(apArticle.getComment() == null ? 0 : apArticle.getComment() + mess.getComment());
        apArticle.setLikes(apArticle.getLikes() == null ? 0 : apArticle.getLikes() + mess.getLike());
        apArticle.setViews(apArticle.getViews() == null ? 0 : apArticle.getViews() + mess.getView());
        updateById(apArticle);
        return apArticle;

    }

    /**
     * 计算文章的具体分值
     *
     * @param apArticle
     * @return
     */
    private Integer computeScore(ApArticle apArticle) {
        Integer score = 0;
        if (apArticle.getLikes() != null) {
            score += apArticle.getLikes() * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }
        if (apArticle.getViews() != null) {
            score += apArticle.getViews();
        }
        if (apArticle.getComment() != null) {
            score += apArticle.getComment() * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT;
        }
        if (apArticle.getCollection() != null) {
            score += apArticle.getCollection() * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
        }

        return score;
    }

    private void cacheToRedis(List<HotArticleVo> hotArticleVos, String channelId) {
        //缓存频道
        hotArticleVos = hotArticleVos.stream()
                .sorted(Comparator.comparing(HotArticleVo::getScore).reversed())
                .collect(Collectors.toList());
        //截取30条信息
        hotArticleVos = hotArticleVos.stream().limit(30).collect(Collectors.toList());

        //缓存到redis
        cacheService.set(channelId, JSON.toJSONString(hotArticleVos));
    }

    /**
     * 分值计算
     *
     * @param apArticles
     * @return
     */
    private List<HotArticleVo> scoreArticle(List<ApArticle> apArticles) {
        List<HotArticleVo> hotArticleVoArrayList;
        hotArticleVoArrayList = new ArrayList<>();
        //计算分值
        apArticles.forEach(apArticle -> {
            int score = 0;
            if (apArticle.getViews() != null) {
                score += apArticle.getViews();
            }
            if (apArticle.getLikes() != null) {
                score += apArticle.getLikes() * 3;
            }
            if (apArticle.getComment() != null) {
                score += apArticle.getComment() * 5;
            }
            if (apArticle.getCollection() != null) {
                score += apArticle.getCollection() * 8;
            }
            HotArticleVo hotArticleVo = new HotArticleVo();
            BeanUtils.copyProperties(apArticle, hotArticleVo);
            hotArticleVo.setScore(score);
            hotArticleVoArrayList.add(hotArticleVo);
        });
        return hotArticleVoArrayList;
    }

    /**
     * 生成静态详情并上传minio
     *
     * @param articleDto
     * @throws Exception
     */
    @Async
    public void genHtml(ApArticle apArticle, ArticleDto articleDto) throws Exception {
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
