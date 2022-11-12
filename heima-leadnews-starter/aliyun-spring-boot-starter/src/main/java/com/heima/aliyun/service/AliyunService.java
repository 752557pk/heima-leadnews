package com.heima.aliyun.service;

import java.io.UnsupportedEncodingException;

public interface AliyunService {

    /**
     * 审核文本的内容
     * @param content
     * @return
     */
    public String scanText(String content) throws UnsupportedEncodingException;

    /**
     * 审核图片的内容
     * @param image
     * @return
     */
    public String scanImage(byte[] image);

}
