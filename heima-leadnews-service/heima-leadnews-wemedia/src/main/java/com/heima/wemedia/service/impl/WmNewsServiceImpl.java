package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.DownOrUpDto;
import com.heima.model.wemedia.dtos.NewsListDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.utils.common.UserThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.AutoAuthNewsService;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
* <p>
* wm_news Service 接口实现
* </p>
*
* @author lenovo
* @since 2022-10-31 17:03:37
*/
@Service
@Transactional
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Autowired
    private WmNewsMapper wmNewsMapper;
    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Autowired
    private WmMaterialMapper wmMaterialMapper;
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    private AutoAuthNewsService autoAuthNewsService;

    @Override
    public ResponseResult list(NewsListDto newsListDto) {
        // 检验参数
        if(newsListDto==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        newsListDto.checkParam();
        // 业务实现 —— 分页+条件查询
        LambdaQueryWrapper<WmNews> wapper = new LambdaQueryWrapper<>();
        // 自己只能看自己
        wapper.eq(WmNews::getUserId, UserThreadLocalUtil.get());

        if(newsListDto.getBeginPubDate()!=null&&newsListDto.getEndPubDate()!=null){//发布时间
            wapper.between(WmNews::getPublishTime,newsListDto.getBeginPubDate(),newsListDto.getEndPubDate());
        }

        if(newsListDto.getChannelId()!=null&&newsListDto.getChannelId()>0){
            wapper.eq(WmNews::getChannelId,newsListDto.getChannelId());
        }

        if(newsListDto.getStatus()!=null&&newsListDto.getStatus()>=0){
            wapper.eq(WmNews::getStatus,newsListDto.getStatus());
        }

        if(StringUtils.isNotEmpty(newsListDto.getKeyword())){
            wapper.like(WmNews::getTitle,newsListDto.getKeyword());
        }

        IPage<WmNews> page = Page.of(newsListDto.getPage(), newsListDto.getSize());
        wmNewsMapper.selectPage(page,wapper);

        // 封装结果
        PageResponseResult pageResponseResult = new PageResponseResult(newsListDto.getPage(), newsListDto.getSize(),page.getTotal());
        pageResponseResult.setData(page.getRecords());
        return pageResponseResult;
    }

    /**
     * 保存方法
     * @param dto
     * @return
     */
    @Override
    public ResponseResult addOrUpdateNews(WmNewsDto dto) {
        // 检查参数
        ResponseResult responseResult = checkParm(dto);
        if(responseResult!=null){
            return responseResult;
        }
        // 业务实现
        WmNews wmNews = saveNews(dto);
        // 是否是提交审核，如果是则保存素材关系
        if(dto.getStatus()==1){// 是
            responseResult = saveNM(dto, wmNews);
            if(responseResult!=null){
                return responseResult;
            }else{
                // 提交成功，则自动审核文章
                try {
                    autoAuthNewsService.autoAuthNews(wmNews.getId());
                } catch (Exception e) {
                   e.printStackTrace();
                }
            }
        }
        // 结果封装
        return ResponseResult.okResult("操作成功");
    }

    @Override
    public ResponseResult downOrUp(DownOrUpDto dto) {
        WmNews wmNews = wmNewsMapper.selectById(dto.getId());
        if(wmNews==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        if(wmNews.getStatus()!=9){
            return ResponseResult.errorResult(AppHttpCodeEnum.STATUS_ERROR);
        }
        wmNews.setEnable(dto.getEnable());
        wmNewsMapper.updateById(wmNews);
        // 发送kafka
        Map<String,String> data = new HashMap<>();
        data.put("articleId",wmNews.getArticleId()+"");
        data.put("enable",dto.getEnable()+"");
        kafkaTemplate.send("article_down_or_up",JSON.toJSONString(data));
        return ResponseResult.okResult("");
    }

    /**
     * 保存内容、封面与素材的关系
     * @param dto
     * @param wmNews
     * @return
     */
    public ResponseResult saveNM(WmNewsDto dto,WmNews wmNews){
        // 关联文章内容图片与素材的关系
        Long newId = dto.getId();
        // 先拿到内容中的图片地址
        String content = dto.getContent();
        Set<String> urls = new HashSet<>();
        List<HashMap> list = JSON.parseArray(content, HashMap.class);
        for (HashMap hashMap : list) {
            String type = (String) hashMap.get("type");
            if("image".equals(type)){
                String url = (String) hashMap.get("value");
                urls.add(url);
            }
        }
        ResponseResult responseResult = saveNewsMetrial(urls, newId, 0);
        if(responseResult!=null){
            return responseResult;
        }

        // 关联文章封面图片与素材的关系
        // 如果是自动则先要生成封面图片
        if(dto.getType()==-1){// 自动生成封面
            // 生成规则：内容中如果有3张及以上则生成3图；内容中有1或2张则生成单图，否则无图
            if(urls.size()>=3){
                wmNews.setType(3);
                StringJoiner joiner = new StringJoiner(",");
                List<String> collect = urls.stream().limit(3).collect(Collectors.toList());
                for (String image : collect) {
                    joiner.add(image);
                }
                wmNews.setImages(joiner.toString());
            }else if(urls.size()>=1){
                wmNews.setType(1);
                wmNews.setImages(urls.iterator().next());
            }else{
                wmNews.setType(0);
            }
            // 更新数据库
            wmNewsMapper.updateById(wmNews);
        }
        // 拿封面中的图片
        String images = wmNews.getImages();
        String[] split = images.split(",");
        Set<String> collect = Arrays.stream(split).collect((Collectors.toSet()));
        responseResult = saveNewsMetrial(collect, newId, 1);
        if(responseResult!=null){
            return responseResult;
        }
        return null;
    }

    /**
     * 保存文章与素材的关系
     * @param urls
     * @param newId
     * @param type
     * @return
     */
    public ResponseResult saveNewsMetrial(Set<String> urls,Long newId,int type){
        LambdaQueryWrapper<WmMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(WmMaterial::getUrl,urls);
        List<WmMaterial> wmMaterials = wmMaterialMapper.selectList(wrapper);
        if(urls.size()!=wmMaterials.size()){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"文章中不能引用素材库之外的图片");
        }


        // 保存关系
        List<WmNewsMaterial> wLists = new ArrayList<>();
        for (int i = 0; i < wmMaterials.size(); i++) {
            WmMaterial wmMaterial = wmMaterials.get(i);
            WmNewsMaterial wnm = new WmNewsMaterial();
            wnm.setNewsId(newId);
            wnm.setMaterialId(wmMaterial.getId());
            wnm.setOrd(i);
            wnm.setType(type);
            wLists.add(wnm);
        }
        wmNewsMaterialMapper.insertBatch(wLists);
        return null;
    }


    /**
     * 保存内容
     * @param dto
     */
    public WmNews saveNews(WmNewsDto dto){
        // 判断是否是修改
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(dto,wmNews);
        if(dto.getImages()!=null) {
            StringJoiner joiner = new StringJoiner(",");
            for (String image : dto.getImages()) {
                joiner.add(image);
            }
            wmNews.setImages(joiner.toString());
        }
        if(dto.getStatus()==1){// 设置提交时间
            wmNews.setSubmitedTime(LocalDateTime.now());
        }
        if(dto.getId()!=null){// 修改
            // 删除素材与图片的关系
            LambdaQueryWrapper<WmNewsMaterial> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WmNewsMaterial::getNewsId,dto.getId());
            wmNewsMaterialMapper.delete(wrapper);
            // 修改文章

            wmNewsMapper.updateById(wmNews);
        }else{//新增
            wmNews.setUserId(UserThreadLocalUtil.get());
            wmNews.setCreatedTime(LocalDateTime.now());
            wmNews.setEnable(1);
            wmNewsMapper.insert(wmNews);
            dto.setId(wmNews.getId());
        }
        return wmNews;
    }


    /**
     * 参数校验
     * @param dto
     * @return
     */
    public ResponseResult checkParm(WmNewsDto dto){
        if(dto==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 如果是草稿只检查标题不能为空
        if(dto.getStatus()==0){
            if(StringUtils.isEmpty(dto.getTitle())){
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"标题不能为空");
            }
        }else{
            // 如果是提交审核-验证所有的项目-除定时时间之外
            if(StringUtils.isEmpty(dto.getTitle())){
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"标题不能为空");
            }
            if(StringUtils.isEmpty(dto.getContent())){
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"内容不能为空");
            }
            if(StringUtils.isEmpty(dto.getLabels())){
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"标签不能为空");
            }
            if(dto.getChannelId()==null||dto.getChannelId()<1){
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"频道不能为空");
            }
            if(dto.getType()==null||dto.getChannelId()<-1){
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"类型不能为空");
            }
            if(dto.getType()>0&&dto.getImages()==null){
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"封面不能为空");
            }
        }
        return  null;
    }
}