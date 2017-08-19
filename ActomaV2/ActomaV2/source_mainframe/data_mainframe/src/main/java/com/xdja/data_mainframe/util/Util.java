package com.xdja.data_mainframe.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ldy on 16/4/22.
 */
public class Util {

    private static final int COMPRESS_QUALITY = 75;
    private static final int F0xD = 0xD;
    private static final int F0x20 = 0x20;
    private static final int F0xD7FF = 0xD7FF;
    private static final int F0xE000 = 0xE000;
    private static final int F0xFFFD = 0xFFFD;
    private static final int F0x10000 = 0x10000;
    private static final int F0x10FFFF = 0x10FFFF;

    public static File compressBitmap2jpg(Context context,Bitmap bitmap, String fileName) {
        File file = new File(context.getExternalCacheDir().getPath() + "/" + fileName);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, fOut);//yangshaopeng change from 100 to 75
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static String generateFullAccountUrl(@NonNull String originalUrl){
        if (!originalUrl.endsWith("/")) {
            originalUrl += "/";
        }

        final String SERVER_AIP_VERSION = "v1";

        originalUrl = originalUrl + SERVER_AIP_VERSION + "/";

        return originalUrl;
    }

    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { // 如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return codePoint == 0x0 || codePoint == 0x9 || codePoint == 0xA || codePoint == F0xD || codePoint >=
                F0x20 && codePoint <= F0xD7FF || codePoint >= F0xE000 && codePoint <= F0xFFFD || codePoint >=
                F0x10000 && codePoint <= F0x10FFFF;
    }

}
