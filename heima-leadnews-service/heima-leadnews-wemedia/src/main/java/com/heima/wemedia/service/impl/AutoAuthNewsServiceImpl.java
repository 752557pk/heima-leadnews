package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.aliyun.service.AliyunService;
import com.heima.feign.article.ArticleFeignClient;
import com.heima.feign.schedule.ScheduleFeignClient;
import com.heima.minio.service.impl.MinIOFileStorageService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.service.AutoAuthNewsService;
import com.heima.wemedia.service.WmChannelService;
import com.heima.wemedia.service.WmNewsService;
import com.heima.wemedia.service.WmUserService;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AutoAuthNewsServiceImpl implements AutoAuthNewsService {

    @Autowired
    WmNewsService wmNewsService;
    @Autowired
    AliyunService aliyunService;
    @Autowired
    MinIOFileStorageService minIOFileStorageService;
    @Autowired
    ArticleFeignClient articleFeignClient;
    @Autowired
    WmUserService wmUserService;
    @Autowired
    WmChannelService wmChannelService;
    @Autowired
    ScheduleFeignClient scheduleFeignClient;

    /**
     * 审核文章
     *
     * @param articleId
     * @return
     */
    @Async
    @Override
    public ResponseResult autoAuthNews(Long articleId) throws Exception {
        Thread.sleep(3000);
        WmNews wmNews = wmNewsService.getById(articleId);
        if(wmNews==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        // 抽取文章中的内容
        Map<String, Object> data = exectContent(wmNews);
        // 自定义敏感词审核
        String content = (String) data.get("text");
        Map<String, Integer> map = SensitiveWordUtil.matchWords(content);
        if(!map.isEmpty()){
            // 审核失败，更新文章内容为2
            ResponseResult responseResult = updateResultStatus("block", wmNews);
            if(responseResult!=null){
                return responseResult;
            }
        }
        // 审核文章中的文本——整篇文章中的文本（内容中的文本、标题）
        content = (String) data.get("text");
        String result = aliyunService.scanText(content);
        ResponseResult responseResult = updateResultStatus(result, wmNews);
        if(responseResult!=null){
            return responseResult;
        }
        // 审核文章中的图片——整篇文章中的图片（内容中的图片、封面）
        Set<byte[]> images = (Set<byte[]>) data.get("images");
        for (byte[] image : images) {
            // http://192.168.200.130:9000/leadnews/2021/4/20210418/c7c3d36d25504cf6aecdcd5710261773.jpg
            result = aliyunService.scanImage(image);
            responseResult = updateResultStatus(result, wmNews);
            if(responseResult!=null){
                return responseResult;
            }
        }
        // 如果文章设置定时发布的时间
        if(wmNews.getPublishTime().getTime()>System.currentTimeMillis()){
            // 未来发布的文章—— 把当前文章做成一个延迟发布任务，添加到延迟任务中
            Taskinfo taskinfo = new Taskinfo();
            taskinfo.setTaskId(wmNews.getId());
            taskinfo.setTaskType(100);// 类似是100，则为文章延迟发布任务
            taskinfo.setPriority(100);// 优先级
            taskinfo.setExecuteTime(wmNews.getPublishTime());
            responseResult = scheduleFeignClient.addTask(taskinfo);
            if(responseResult.getCode()==200){
                // 修改文章为8
                wmNews.setStatus(8);
                wmNewsService.updateById(wmNews);
            }else{
                return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR,"添加延迟发布失败");
            }
        }else{
            // 立即发布
            responseResult = saveToApp(wmNews);
        }

        return responseResult;
    }

    /**
     * 发布文章到APP中
     * @param wmNews
     * @return
     */
    public ResponseResult saveToApp(WmNews wmNews){
        // 保存内容到APP
        ArticleDto articleDto = new ArticleDto();
        BeanUtils.copyProperties(wmNews,articleDto);
        articleDto.setId(wmNews.getArticleId());//重新设置ID
        // 作者信息
        WmUser wmUser = wmUserService.getById(wmNews.getUserId());
        articleDto.setAuthorId(wmUser.getApAuthorId());
        articleDto.setAuthorName(wmUser.getName());
        // 设置频道名称
        WmChannel wmChannel = wmChannelService.getById(wmNews.getChannelId());
        articleDto.setChannelName(wmChannel.getName());
        articleDto.setFlag(0);//普通文章标记
        articleDto.setLayout(wmNews.getType());
        ResponseResult responseResult = articleFeignClient.saveOrUpdate(articleDto);
        if(responseResult.getCode()==200){
            Long appArticleId = (Long) responseResult.getData();
            wmNews.setArticleId(appArticleId);
            wmNews.setStatus(9);//设置成已发布
            wmNewsService.updateById(wmNews);
            return ResponseResult.okResult("审核成功");
        }else{
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR,"同步文章到app失败");
        }
    }

    /**
     * 判断审核结果
     * @param result
     * @param wmNews
     * @return
     */
    public ResponseResult updateResultStatus(String result,WmNews wmNews){
        if(result==null){//审核接口调用是失败
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR,"图片审核调用失败");
        }
        if(!"pass".equals(result)){
            if("review".equals(result)){
                wmNews.setStatus(3);
            }
            if("block".equals(result)){
                wmNews.setStatus(2);
            }
            wmNewsService.updateById(wmNews);
            return ResponseResult.okResult("审核完成");
        }
        return null;
    }

    /**
     * 抽取正文中的文本
     * @param wmNews
     * @return
     */
    public Map<String,Object> exectContent(WmNews wmNews){
        String content = wmNews.getContent();
        //  添加标题文本
        StringBuilder stringBuilder = new StringBuilder(wmNews.getTitle());
        Set<String> images = new HashSet<>();//去重内容中和封面中的图片
        // 添加封面图片
        if(StringUtils.isNotEmpty(wmNews.getImages())){
            String[] split = wmNews.getImages().split(",");
            images.addAll(Arrays.asList(split));
        }
        List<HashMap> list = JSON.parseArray(content, HashMap.class);
        for (HashMap hashMap : list) {
            String type = (String) hashMap.get("type");
            String value = (String) hashMap.get("value");
            if("text".equals(type)){
                stringBuilder.append(value);
            }
            if("image".equals(type)){
                images.add(value);
            }
        }
        Set<byte[]> sets = new HashSet<>();
        // 提取图片上的文字，并存放到content
        for (String image : images) {
            byte[] bytes = minIOFileStorageService.downLoadFile(image);
            sets.add(bytes);
            try {
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));
                String ocr = ocr(bufferedImage);
                stringBuilder.append(ocr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Map<String,Object> data = new HashMap<>();
        data.put("images",sets);
        data.put("text",stringBuilder.toString());
        return data;
    }

    /**
     * 识别图片上的文字
     * @param bufferedImage
     * @return
     */
    public String ocr(BufferedImage bufferedImage){
        Tesseract tesseract = new Tesseract();
        // 设置识别模型库的文件目录
        tesseract.setDatapath("D:\\tess4j");
        // 设置识别语言
        tesseract.setLanguage("chi_sim");
        try {
           return tesseract.doOCR(bufferedImage);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return "";
    }

}
