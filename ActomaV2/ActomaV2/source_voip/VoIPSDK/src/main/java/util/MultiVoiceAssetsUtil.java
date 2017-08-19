package util;

import android.content.Context;

import com.xdja.comm.uitl.UniversalUtil;

/**
 * 多语言语音文件工具类
 * Created by MengBo on 2016/12/5.
 */
public class MultiVoiceAssetsUtil {

    public final static int FILE_BUSY_HERE = 1;
    public final static int FILE_CONNECT_ERR = 2;
    public final static int FILE_NO_ANSWER = 3;

    private final static String FILE_CN_BUSY_HERE = "busy_here.mp3";
    private final static String FILE_CN_CONNECT_ERR = "connect_err.mp3";
    private final static String FILE_CN_NO_ANSWER = "no_answer.mp3";

    private final static String FILE_EN_BUSY_HERE = "en_busy_here.mp3";
    private final static String FILE_EN_CONNECT_ERR = "en_connect_err.mp3";
    private final static String FILE_EN_NO_ANSWER = "en_no_answer.mp3";

    /**
     * 获取多语言语音文件
     * @param context
     * @param file
     * @return
     */
    public static String getMultiVoiceFileString(Context context, int file){
        int languageType = UniversalUtil.getLanguageType(context);
        if(languageType == UniversalUtil.LANGUAGE_CH_SIMPLE){
            return getVoiceFileCN(file);
        }else{
            return getVoiceFileEN(file);
        }
    }

    /**
     * 获取中文语音文件
     * @param file
     * @return
     */
    private static String getVoiceFileCN(int file){
        String voiceFileResult = "";
        switch(file){
            case FILE_BUSY_HERE:
                voiceFileResult = FILE_CN_BUSY_HERE;
                break;
            case FILE_CONNECT_ERR:
                voiceFileResult = FILE_CN_CONNECT_ERR;
                break;
            case FILE_NO_ANSWER:
                voiceFileResult = FILE_CN_NO_ANSWER;
                break;
        }
        return voiceFileResult;
    }

    /**
     * 获取英文语音文件
     * @param file
     * @return
     */
    private static String getVoiceFileEN(int file){
        String voiceFileResult = "";
        switch(file){
            case FILE_BUSY_HERE:
                voiceFileResult = FILE_EN_BUSY_HERE;
                break;
            case FILE_CONNECT_ERR:
                voiceFileResult = FILE_EN_CONNECT_ERR;
                break;
            case FILE_NO_ANSWER:
                voiceFileResult = FILE_EN_NO_ANSWER;
                break;
        }
        return voiceFileResult;
    }
}
