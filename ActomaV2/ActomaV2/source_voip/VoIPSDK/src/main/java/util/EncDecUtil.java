package util;

import com.csipsimple.utils.Base64;
import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.dependence.uitls.LogUtil;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

/**
 * Created by MengBo on 2016/7/29.
 */
public class EncDecUtil {

    final private static String TAG = "EncDecUtil";

    //语音流加密密钥
    public final static byte[] SECRET_KEY_EMPTY = new byte[]{
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    //语音流加密密钥
    private static byte[] secretkey;

    //语音流加密密钥
    private static String ckmsSecretKeyString = "";

    /** 20161011-mengbo-start: 修改此方法为返回生成是否成功，在缓冲中存放ckmsKey **/
    //生成用CKMS加密语音流密钥的密钥
    public static boolean generateCKMSKey(String currentAccount, String friend){

        //生成语音流加密密钥
        byte[] keyBytes = generateKeyByte();

        //生成用CKMS用到的GROUPID
        String groupId = CkmsGpEnDecryptManager.getGroupIdWithFriend(currentAccount, friend);
        //用CKMS加密语音流密钥
        //mengbo@xdja.com 2016-08-29 start add. encryptByteToByteSoft
        //[S]tangsha@xdja.com 2016-08-19 modify. for ckms modify return type. review by self.
        byte[] CKMSKeyBytes = null;
        Map<String,Object> CKMSKeyInfo = CkmsGpEnDecryptManager.encryptByteToByte(currentAccount, groupId, keyBytes);
        if(CKMSKeyInfo != null && (int)CKMSKeyInfo.get(CkmsGpEnDecryptManager.RESULT_CODE_TAG) == CkmsGpEnDecryptManager.CKMS_SUCC_CODE){
            CKMSKeyBytes = (byte[])CKMSKeyInfo.get(CkmsGpEnDecryptManager.RESULT_INFO_TAG);

            LogUtil.getUtils(TAG).d("EncDecUtil--generateCKMSKey--CKMSKey--RESULT_CODE_TAG:" + (int) CKMSKeyInfo.get(CkmsGpEnDecryptManager.RESULT_CODE_TAG));
        }else{
            Map<String,Object> softKeyInfo = CkmsGpEnDecryptManager.encryptByteToByteSoft(groupId, keyBytes);
            if(softKeyInfo != null && (int)softKeyInfo.get(CkmsGpEnDecryptManager.RESULT_CODE_TAG) == CkmsGpEnDecryptManager.CKMS_SUCC_CODE){
                CKMSKeyBytes = (byte[])softKeyInfo.get(CkmsGpEnDecryptManager.RESULT_INFO_TAG);
            }

            LogUtil.getUtils(TAG).d("EncDecUtil--generateCKMSKey--softKey--RESULT_CODE_TAG:" + (int) CKMSKeyInfo.get(CkmsGpEnDecryptManager.RESULT_CODE_TAG));
        }
        //[E]tangsha@xdja.com 2016-08-19 modify. for ckms modify return type. review by self.
        //mengbo@xdja.com 2016-08-29 end

        String CKMSKeyString = "";
        if(CKMSKeyBytes != null){
            CKMSKeyString = Base64.encodeBytes(CKMSKeyBytes);
        }

        if(CKMSKeyString != null && !CKMSKeyString.equals("")){
            ckmsSecretKeyString = CKMSKeyString;
            return true;
        }else{
            ckmsSecretKeyString = "";
            return false;
        }
    }
    /** 20161011-mengbo-end **/

    //提取加密语音流密钥
    public static boolean extractCKMSSecretKeyString(String CKMSKey, String currentAccount, String friend){

        byte[] keyBytes = null;

        if(CKMSKey != null && !CKMSKey.equals("")){
            try{
                byte[] CKMSKeyBytes = Base64.decode(CKMSKey);
                //用CKMS解密语音流密钥
                //mengbo@xdja.com 2016-08-29 start add. decryptByteSoft
                //[S]tangsha@xdja.com 2016-08-19 modify. for ckms modify return type. review by self.
                Map<String,Object> CKMSKeyInfo = CkmsGpEnDecryptManager.decryptByteToByte(currentAccount, CKMSKeyBytes);
                if(CKMSKeyInfo != null && (int)CKMSKeyInfo.get(CkmsGpEnDecryptManager.RESULT_CODE_TAG) == CkmsGpEnDecryptManager.CKMS_SUCC_CODE){
                    keyBytes = (byte[])CKMSKeyInfo.get(CkmsGpEnDecryptManager.RESULT_INFO_TAG);

                    LogUtil.getUtils(TAG).d("EncDecUtil--extractCKMSSecretKeyString--CKMSKey--RESULT_CODE_TAG:" + (int) CKMSKeyInfo.get(CkmsGpEnDecryptManager.RESULT_CODE_TAG));

                }else{
                    //生成用CKMS用到的GROUPID
                    String groupId = CkmsGpEnDecryptManager.getGroupIdWithFriend(currentAccount, friend);
                    Map<String,Object> softKeyInfo = CkmsGpEnDecryptManager.decryptByteSoft(groupId, CKMSKeyBytes);
                    if(softKeyInfo != null && (int)softKeyInfo.get(CkmsGpEnDecryptManager.RESULT_CODE_TAG) == CkmsGpEnDecryptManager.CKMS_SUCC_CODE){
                        keyBytes = (byte[])softKeyInfo.get(CkmsGpEnDecryptManager.RESULT_INFO_TAG);
                    }

                    LogUtil.getUtils(TAG).d("EncDecUtil--extractCKMSSecretKeyString--softKey--RESULT_CODE_TAG:" + (int) CKMSKeyInfo.get(CkmsGpEnDecryptManager.RESULT_CODE_TAG));
                }
                //[E]tangsha@xdja.com 2016-08-19 modify. for ckms modify return type. review by self.
                //mengbo@xdja.com 2016-08-29 end

            }catch(IOException e){
                e.printStackTrace();
            }
        }

        setSecretkey(keyBytes);

        return (keyBytes != null);
    }

    public static String getCkmsSecretKeyString() {
        return ckmsSecretKeyString;
    }

    public static byte[] getSecretkey() {
        return secretkey;
    }

    public static void setSecretkey(byte[] secretkey) {
        EncDecUtil.secretkey = secretkey;
    }

    //生成语音流加密密钥
    private static byte[] generateKeyByte(){
        Random random = new Random();
        byte[] bytes = new byte[16];
        for(int i=0; i<bytes.length; i++){
            bytes[i] = (byte)random.nextInt(256);
        }

        setSecretkey(bytes);
        return bytes;
    }

}
