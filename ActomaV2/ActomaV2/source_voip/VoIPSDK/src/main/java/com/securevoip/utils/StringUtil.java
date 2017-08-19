package com.securevoip.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * Created by xjq on 16-2-27.
 */
public class StringUtil {
    /**
     * 字符串按行解析到list
     * @param s  要解析的字符串
     * @param lines 存储解析结果
     * @return void
     */
    public static void stringToLines(String s, List<String> lines){
        BufferedReader bf = new BufferedReader(new StringReader(s));
        String tmp = null;
        try {
            while((tmp = bf.readLine()) != null) {
                lines.add(tmp);
            }
            bf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
