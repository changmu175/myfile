package com.xdja.imp.util;

import android.text.TextUtils;

import com.xdja.imp.data.utils.ToolUtil;
import com.xdja.imp.domain.model.LocalPictureInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

 /**
 * 项目名称：ActomaV2
 * 类描述：分享转发中，获取本地图片信息工具类
 * 创建人：ycm
 * 创建时间：2016/11/1 17:11
 * 修改人：ycm
 * 修改时间：2016/11/1 17:11
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public class LocalPictureInfoUtil {

    /**
     * 获取图片信息List
     *
     * @param paths 图片路径
     * @return
     */
    public static List<LocalPictureInfo> getLocalPictureInfoList(ArrayList<String> paths) {
        if (paths == null) {
            return null;
        }
        List<LocalPictureInfo> localPictureInfos = new ArrayList<>();
        for (int i = 0; i < paths.size(); i++) {
            LocalPictureInfo localPictureInfo = generateLocalPictureInfo(paths.get(i));
            localPictureInfos.add(localPictureInfo);
        }
        return localPictureInfos;
    }

    /**
     * 将图片路径转换成图片信息实体
     *
     * @param filePath
     * @return
     */
    private static LocalPictureInfo generateLocalPictureInfo(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        String fileName = ToolUtil.getLastString(filePath, "/");
        return new LocalPictureInfo(fileName, filePath, file.length());
    }
}
