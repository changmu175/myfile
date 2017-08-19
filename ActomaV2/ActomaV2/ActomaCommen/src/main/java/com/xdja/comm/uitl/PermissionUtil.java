package com.xdja.comm.uitl;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.xdja.comm.R;
import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guorong on 2016/8/3.
 * 开启android6.0中的危险权限工具类
 */
public class PermissionUtil {
    //开启权限失败
    public static final int REQUEST_FAILED = -1;
    //申请成功
    public static final int REQUEST_SUCCESSED = 0;
    //录音设备被占用
    public static final int ILLEGALSTATE_AUDIO = -2;
    //权限已经全部获取到
    public static final int ALL_PERMISSION_OBTAINED = 1;
    //申请的权限被拒绝开启或者在设置中关闭过
    public static final int PERMISSION_HAS_REFUSED = 2;
    //打电话权限请求码
    public static final int DAIL_PERMISSION_REQUEST_CODE = 45;

    //获取手机信息
    public static final int READ_PHONE_PERMISSION_REQUEST_CODE = 46;

    /**
     * @param ctx 上下文对象,必须为activity
     * @param requestCode 权限请求时候的请求码，用于在activity的权限申请回调中区分不同的请求
     * @param permissions 需要请求获取的权限
     *
     * @return -1 : 开启权限失败
     * @return  0 : 申请开启成功，需要在activity的回调申请回调中做相应的操作
     * @return  1 : 权限已经获取到，可直接做相应的操作
     * @return  2 : 申请的权限被拒绝开启或者手动在设置中关闭过，提示手动开启
     * */
    public static int requestPermissions(Activity ctx , int requestCode , String... permissions){
        if(ctx == null || permissions == null || permissions.length == 0){
            LogUtil.getUtils().e("PermissionUtil requestPermissions parameter error, permissions "+ Arrays.toString(permissions)); // modified by ycm for lint 2017/02/16
            return REQUEST_FAILED;
        }
       Map<String , String> permissionMap = checkPermissions(permissions);
        if(permissionMap.size() == 0){
            LogUtil.getUtils().e("PermissionUtil requestPermissions parameter error, permissionMap size is 0 ");
            return REQUEST_FAILED;
        }
        //需要处理的权限个数
        int permissionCount = permissionMap.size();
        List<String> needTorequest = new ArrayList<>();
        List<String> cannotTorequest = new ArrayList<>();
        List<String> notNeedTorequest = new ArrayList<>();
        for(String per : permissionMap.keySet()){
            //如果该权限没有开启
            if (ContextCompat.checkSelfPermission(ctx.getApplicationContext(), per) != PackageManager
                    .PERMISSION_GRANTED) {
                //申请该权限
                needTorequest.add(per);
            } else {
                notNeedTorequest.add(per);
            }
        }
        if(needTorequest.size() > 0){
            String[] perStrs = new String[]{};
            ActivityCompat.requestPermissions(ctx , permissionMap.keySet().toArray(perStrs) , requestCode);
            return REQUEST_SUCCESSED;
        }

        if(notNeedTorequest.size() == permissionCount){
            return ALL_PERMISSION_OBTAINED;
        }

        if(cannotTorequest.size() > 0){
            return PERMISSION_HAS_REFUSED;
        }
        return REQUEST_FAILED;
    }

    /**
     * @param permissions 需要检查的权限，应该是Android6.0中的危险权限
     * */
    private static Map<String , String> checkPermissions(String ... permissions){
        Map<String , String> permissionList = new HashMap<>();
        Map<String , String> dangerousPermissions = new HashMap<>();
        dangerousPermissions.put(Manifest.permission.WRITE_CONTACTS , ActomaController.getApp().getString(R.string.write_contact));
        dangerousPermissions.put(Manifest.permission.GET_ACCOUNTS , ActomaController.getApp().getString(R.string.get_account));
        dangerousPermissions.put(Manifest.permission.READ_CONTACTS , ActomaController.getApp().getString(R.string.read_contact));

        dangerousPermissions.put(Manifest.permission.READ_CALL_LOG , "");
        dangerousPermissions.put(Manifest.permission.READ_PHONE_STATE, "");
        dangerousPermissions.put(Manifest.permission.CALL_PHONE, ActomaController.getApp().getString(R.string.telephone));
        dangerousPermissions.put(Manifest.permission.WRITE_CALL_LOG, "");
        dangerousPermissions.put(Manifest.permission.USE_SIP, "");
        dangerousPermissions.put(Manifest.permission.PROCESS_OUTGOING_CALLS, "");

        dangerousPermissions.put(Manifest.permission.READ_CALENDAR, "");
        dangerousPermissions.put(Manifest.permission.WRITE_CALENDAR, "");

        dangerousPermissions.put(Manifest.permission.CAMERA, ActomaController.getApp().getString(R.string.camera));

//        dangerousPermissions.put(Manifest.permission.BODY_SENSORS, "");// delete by ycm for lint 2017/02/10 和郭荣讨论此处可删除

        dangerousPermissions.put(Manifest.permission.ACCESS_FINE_LOCATION, "");
        dangerousPermissions.put(Manifest.permission.ACCESS_COARSE_LOCATION, "");

        dangerousPermissions.put(Manifest.permission.READ_EXTERNAL_STORAGE, ActomaController.getApp().getString(R.string.read_SDCard));
        dangerousPermissions.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, ActomaController.getApp().getString(R.string.write_SDCard));

        dangerousPermissions.put(Manifest.permission.RECORD_AUDIO, ActomaController.getApp().getString(R.string.record));

        dangerousPermissions.put(Manifest.permission.READ_SMS, "");
        dangerousPermissions.put(Manifest.permission.RECEIVE_WAP_PUSH, "");
        dangerousPermissions.put(Manifest.permission.RECEIVE_MMS, "");
        dangerousPermissions.put(Manifest.permission.RECEIVE_SMS, "");

        //过滤掉参数中传递的非危险权限
        for (String permission : permissions) {// modified by ycm for lint 2017/02/13
            for (String dangerousPer : dangerousPermissions.keySet()) {
                if (permission.equals(dangerousPer)) {
                    permissionList.put(dangerousPer, dangerousPermissions.get(dangerousPer));
                    break;
                }
            }
        }
        return permissionList;
    }

    /**
     * 检查权限
     * @param permission 要检查的权限，目前支持照相机和录音权限
     * @return 0拥有权限，-1权限禁止
     */
    public static int checkPermission(String permission) {
        if (Manifest.permission.CAMERA.equalsIgnoreCase(permission)) {
            return checkCameraPermission();
        } else if (Manifest.permission.RECORD_AUDIO.equalsIgnoreCase(permission)) {
            return checkMediaRecorderPermission();
        }
        return PermissionUtil.REQUEST_FAILED;
    }

    /**
     * 版本小于23时检测照相机权限
     * @return 0拥有权限，-1权限禁止
     */
    @SuppressWarnings("deprecation")
    private static int checkCameraPermission() {
        Camera camera = null;
        try {

            camera = Camera.open();
            return PermissionUtil.ALL_PERMISSION_OBTAINED;
        } catch (RuntimeException e) {
            return PermissionUtil.REQUEST_FAILED;
        } finally {
            if (null != camera) {
                camera.release();
                camera = null;
            }
        }
    }

    /**
     * 版本小于23,进行录音权限的判断。
     *
     * @return 0拥有权限，-1权限禁止
     */
    private static int checkMediaRecorderPermission(){

        MediaRecorder mediaRecorder = new MediaRecorder();
        File file = new File(Environment.getExternalStorageDirectory(),"x_per");
        try {

            // 设置输出文件
            mediaRecorder.setOutputFile(file.getAbsolutePath());
            // 设置MediaRecorder的音频源为麦克风
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置音频格式
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            // 设置音频编码
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            //设置最大录音时长
            mediaRecorder.setMaxDuration(200);

            long beginTime = System.currentTimeMillis();

            // 准备录音
            mediaRecorder.prepare();
            mediaRecorder.start();

        } catch (IllegalStateException e) {
            return ILLEGALSTATE_AUDIO;
        } catch (IOException e) {
            return REQUEST_FAILED;
        } catch (Exception e) {
            return REQUEST_FAILED;
        } finally {
            mediaRecorder.release();
            mediaRecorder = null;
            if (file.exists()) {
                file.delete();
            }
        }
        return REQUEST_SUCCESSED;
    }

}
