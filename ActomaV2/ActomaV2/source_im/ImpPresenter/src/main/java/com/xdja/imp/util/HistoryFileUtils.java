package com.xdja.imp.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.R;
import com.xdja.imp.data.utils.IMFileUtils;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.TalkMessageBean;

import java.io.File;

/**
 * 项目名称：ActomaV2
 * 类描述：
 * 创建人：xdjaxa
 * 创建时间：2016/12/16 14:02
 * 修改人：xdjaxa
 * 修改时间：2016/12/16 14:02
 * 修改备注：
 */
public class HistoryFileUtils {

    /**
     * add by zya 20161228
     * 对应路径文件是否存在
     * @param path 检测的文件路径
     * @return
     */
    public static boolean isFileExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        File file = new File(path);
        return file.exists();
    }

    /**
     * 判断是否为某种类型
     * @param suffix 需要匹配的类型
     * @param args 类型集合
     * @return true
     */
    private static boolean isFileType(String suffix, String[] args){
        for(String arg : args){
            if(arg.equals("." + suffix)){
                return true;
            }
        }
        return false;
    }

    public static boolean isPicture(){
        return false;
    }

    /**
     * 根据后缀获取默认图标
     * @param talkMessageBean
     * @return
     */
    public static int getIconWithSuffix(TalkMessageBean talkMessageBean){

        FileInfo fileInfo = talkMessageBean.getFileInfo();
        if(fileInfo == null){
            return R.drawable.ic_others;
        }

        int resId;
        String suffix = fileInfo.getSuffix();
        //String path = fileInfo.getFilePath();
        if(TextUtils.isEmpty(suffix)){
            //suffix = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
            return R.drawable.ic_others;
        }
        suffix = suffix.toLowerCase();

        if(isFileType(suffix,new String[]{".pdf"})){
            resId = R.drawable.ic_pdf;
        } else if(isFileType(suffix,new String[]{".doc",".docx"})){
            resId = R.drawable.ic_doc;
        } else if(isFileType(suffix,new String[]{".xls",".xlsx"})){
            resId = R.drawable.ic_excel;
        } else if(isFileType(suffix,new String[]{".ppt",".pptx"})){
            resId = R.drawable.ic_ppt;
        }  else if(isFileType(suffix,new String[]{".txt"})){
            resId = R.drawable.ic_text;
        } else if(isFileType(suffix,IMFileUtils.mVideoSuffix)){
            resId = R.drawable.ic_video;
        } else if(isFileType(suffix,IMFileUtils.mVoiceSuffix)){
            resId = R.drawable.ic_music;
        } else if(isFileType(suffix,IMFileUtils.mImageSuffix)){
            resId = R.drawable.ic_jpg;
        } else if(isFileType(suffix,IMFileUtils.mDocSuffix)){
            resId = R.drawable.ic_doc;
        } else if(isFileType(suffix,IMFileUtils.mApkSuffix)){
            resId = R.drawable.ic_apk;
        } else {
            resId = R.drawable.ic_others;
        }
        return resId;
    }


    /**
     * 根据filePath或者suffix或者打开这种扩展名的文件的intent
     * @param filePath
     * @param suffix
     * @return
     */
    public static void intentBuilder(Activity activity, String filePath, String suffix){
        if(TextUtils.isEmpty(filePath)){
            Toast.makeText(activity,activity.getString(R.string.history_send_file_not_exist),Toast.LENGTH_SHORT).show();
            return ;
        }

        //String extension = suffix;
        Intent intent =  new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        //获取对应的mimetype
        //添加转换为小写。
        String type;
        if(TextUtils.isEmpty(suffix)){
            type = "*/*";
        } else {
            type = MimeUtils.getMimeTypeWithExtension(suffix.toLowerCase());
            if(TextUtils.isEmpty(type)){
                type = "*/*";
            }
        }

        if(!TextUtils.isEmpty(type) && !"*/*".equals(type)){
            intent.setDataAndType(Uri.fromFile(new File(filePath)),type);
            try {
                activity.startActivity(intent);
            } catch (ActivityNotFoundException e){
                LogUtil.getUtils().d("Not found exception;");
                Toast.makeText(activity,activity.getString(R.string.history_file_none_tool),
                        Toast.LENGTH_SHORT).show();
            }//end by zya
        } else if("*/*".equals(type)){
            //add by zya 20170103
            Toast.makeText(activity,activity.getString(R.string.history_file_none_tool),
                    Toast.LENGTH_SHORT).show();
            //end by zya
        }
    }
}
