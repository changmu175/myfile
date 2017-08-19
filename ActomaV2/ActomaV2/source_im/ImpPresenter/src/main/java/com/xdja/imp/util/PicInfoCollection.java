package com.xdja.imp.util;

import com.xdja.imp.domain.model.LocalPictureInfo;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by guorong on 2016/7/4.
 * 该类用于会话中图片发送与接收相关的功能涉及到的图片的选择记录
 */
public class PicInfoCollection {
    private static Map<String , LocalPictureInfo> localPicInfo ;
    public static synchronized Map<String , LocalPictureInfo> getLocalPicInfo(){
        if(localPicInfo == null){
            localPicInfo = new LinkedHashMap<>();
        }
        return localPicInfo;
    }
    /*private static Set<String> originSelectedPicList;
    private static List<LocalPictureInfo> selectedPicList;

    *//**选择原图的图片集合*//*
    public static synchronized Set<String> getOriginPicSelectedSet(){
        if(originSelectedPicList == null){
            originSelectedPicList = new HashSet<>();
        }
        return originSelectedPicList;
    }

    *//**已选图片集合*//*
    public static synchronized List<LocalPictureInfo> getSelectedPicSelectList(){
        if(selectedPicList == null){
            selectedPicList = new ArrayList<>();
        }
        return selectedPicList;
    }*/
}
