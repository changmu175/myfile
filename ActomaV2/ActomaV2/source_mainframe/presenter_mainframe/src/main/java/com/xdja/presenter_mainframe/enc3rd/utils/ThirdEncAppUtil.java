package com.xdja.presenter_mainframe.enc3rd.utils;

import android.content.Context;
import android.text.TextUtils;

import com.xdja.comm.encrypt.EncryptAppBean;

import java.util.List;

/**
 * Created by THZ on 2016/6/21.
 * 关于第三方加密工具类
 */
public class ThirdEncAppUtil {



    /**
     * 过滤相关短信的应用，对list进行处理
     * @param context 上下文
     * @param listData 整理原数据文件
     */
    public static void resetList(Context context, List<EncryptAppBean> listData) {
        if (listData == null || listData.isEmpty()) {
            return;
        }
        int size = listData.size();
        for (int i = size -1; i >= 0; i--) {
            EncryptAppBean bean = listData.get(i);
            String appName = bean.getPackageName();
            if (!TextUtils.isEmpty(appName)) {
                if (ThirdEncAppProperty.mmsHash.containsKey(appName)) {
                    boolean result = ListUtil.checkPackage(context, appName);
                    if (!result) {
                        listData.remove(i);
                    }
                }
            }
        }
    }


}
