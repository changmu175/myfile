package com.xdja.contact.http.request.group;

import com.alibaba.fastjson.JSON;
import com.xdja.comm.data.CommonHeadBitmap;
import com.xdja.contact.http.request.RequestBody;

/**
 * Created by XDJA_XA on 2015/7/23.
 *
 * modify wanghao 上传头像请求体
 * 上传头像功能接口变更
 * “avatarId”:”98798s-44dsf-sdf3234.png”,//头像Id
 * “thumbnailId”:” 98798s-44dsf-sdf3234.png”//头像缩略Id
 */
public class UploadGroupAvatarBody extends RequestBody {


    private String avatarId;

    private String thumbnailId;




    /*private String groupAvatar;//群头像BASE64

    private String groupThumbnail;//群头像缩略base 64

    private String suffix; //头像文件扩展名：--png、jpg*/

    /*public UploadGroupAvatarBody(CommonHeadBitmap headBitmap) throws ATUploadGroupAvatarException {
        try {
            setSuffix("png");
            //获取裁剪后的图片bitmap
            Bitmap bitmap = headBitmap.getBm();
            //对原图进行压缩 压缩至200k
            byte[] bitmapArray = new BitmapUtil().getBigmapZoomBitmap(bitmap, 200 * 1024);
            //原图Base64转码
            String avatar = Base64.encodeBytes(bitmapArray, Base64.DONT_BREAK_LINES);
            //对原图进行压缩 压缩至10k
            byte[] cut = new BitmapUtil().getZoomBitmap(bitmap, 56 * 3, 56 * 3, 10 * 1024);
            //原图缩略图Base64转码
            String thumbnail = Base64.encodeBytes(cut, Base64.DONT_BREAK_LINES);

            setGroupAvatar(avatar);
            setGroupThumbnail(thumbnail);
        }catch (Exception e){
            throw new ATUploadGroupAvatarException(e);
        }
    }*/

    public UploadGroupAvatarBody(CommonHeadBitmap headBitmap) {
        setAvatarId(headBitmap.getAvatarId());
        setThumbnailId(headBitmap.getThumbnailId());
    }

    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    public String getThumbnailId() {
        return thumbnailId;
    }

    public void setThumbnailId(String thumbnailId) {
        this.thumbnailId = thumbnailId;
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
