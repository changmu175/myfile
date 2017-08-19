package com.xdja.contact.util;

import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.dependence.uitls.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xdjaxa on 2016/8/25.
 */
public class ContactCkmsUtils {
    private static final String TAG = "ContactCkmsUtils";
    //ckms相关操作成功
    public static final int CKMS_SUCC_CODE = 0;
    //ckms相关操作失败
    public static final int CKMS_FAIL_CODE = 1;

    public static final String RESULT_CODE_TAG = "resultCode";
    public static final String RESULT_INFO_TAG = "resultInfo";

    public static Map<String,Object> getNoSecEntityAccounts(List<String> accountList) {
        List<String> noSecEntityAccounts=new ArrayList<String>();
        Map<String,Object> returnMap= CkmsGpEnDecryptManager.getNoSecEntityAccounts(accountList);
        if (returnMap != null) {
            if (CkmsGpEnDecryptManager.CKMS_SUCC_CODE==(int)returnMap.get(CkmsGpEnDecryptManager.RESULT_CODE_TAG)){
                if (returnMap.get(CkmsGpEnDecryptManager.RESULT_INFO_TAG) != null) {
                    String[] ret = (String[]) returnMap.get(CkmsGpEnDecryptManager.RESULT_INFO_TAG);
                    if (ret != null) {
                        for (int i = 0; i < ret.length; i++) {
                            noSecEntityAccounts.add( ret[i]);
                            LogUtil.getUtils().d(TAG+ "ContactCkmsUtils getNoSecEntityAccounts " + i + " ret " + ret[i]);
                        }
                        returnMap.put(RESULT_INFO_TAG,noSecEntityAccounts);
                    }
                }
            }
        }
        return returnMap;
    }
}
