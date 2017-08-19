package com.xdja.presenter_mainframe.chooseImg;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by geyao on 2015/7/10.
 */
public class FindImgUtils {

    /**
     * 获取手机内图片信息集合
     *
     * @param context 上下文句柄
     * @return 图片信息集合
     */
    public static ArrayList<ImageRelInfoBean> getImgData(Context context) {
        Cursor cursor = null;
        //返回的图片信息集合
        ArrayList<ImageRelInfoBean> result = new ArrayList<ImageRelInfoBean>();
        ImageRelInfoBean imginfo;
        //获取图片缩略图集合信息
        Map<Integer, ImageInfoBean> list1 = new HashMap<>();
        ImageInfoBean info;
        //查询字段
//        String[] projection = {MediaStore.Images.Thumbnails._ID,
//                MediaStore.Images.Thumbnails.IMAGE_ID,
//                MediaStore.Images.Thumbnails.DATA};
        //alh@xdja.com<mailto://alh@xdja.com> 2017-02-22 add. fix bug 8116 . review by wangchao1. Start
        String[] projection = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT};
        //alh@xdja.com<mailto://alh@xdja.com> 2017-02-22 add. fix bug 8116 . review by wangchao1. End
        //查询系统数据库
        try {
            cursor = context.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
                    String img = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                    info = new ImageInfoBean(id, img , -1);
                    list1.put(id, info);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        //获取图片原图集合信息
        ArrayList<ImageInfoBean> list2 = new ArrayList<ImageInfoBean>();

        try {
            cursor = context.getContentResolver().query
                    (MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, "date_modified DESC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                    String img = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                    long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE));
                    info = new ImageInfoBean(id, img, fileSize);
                    list2.add(info);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        //获取正确的图片信息
        for (int i = 0; i < list2.size(); i++) {//原图集合
            //排除gif图片
            if (!list2.get(i).getImage_url().contains(".gif")) {
                //判断缩略图集合内是否包含原图所需缩略图
                if (!list1.isEmpty() && list1.get(list2.get(i).getImage_id()) != null) {
                    imginfo = new ImageRelInfoBean(list2.get(i).getImage_id(),
                            list1.get(list2.get(i).getImage_id()).getImage_url(), list2.get(i).getImage_url() , list2.get(i).getFile_size());
                    result.add(imginfo);
                } else {
                    imginfo = new ImageRelInfoBean(list2.get(i).getImage_id(), list2.get(i).getImage_url(), list2.get
                            (i).getImage_url(), list2.get(i).getFile_size());
                    result.add(imginfo);
                }
            }
        }
        //添加拍摄照相的占位bean
        imginfo = new ImageRelInfoBean(0, "", "" , 0);
        result.add(0, imginfo);

        return result;
    }
}
