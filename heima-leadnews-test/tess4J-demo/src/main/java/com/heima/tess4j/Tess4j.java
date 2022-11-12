package com.heima.tess4j;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class Tess4j {

    public static void main(String[] args) throws TesseractException {
        Tesseract tesseract = new Tesseract();
        // 设置识别模型库的文件目录
        tesseract.setDatapath("D:\\tess4j");
        // 设置识别语言
        tesseract.setLanguage("chi_sim");
        String result = tesseract.doOCR(new File("D:\\tess4j\\2.png"));
        System.out.println(result);
    }


}
