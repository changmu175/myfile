package com.xdja.presenter_mainframe.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.comm.ckms.IActomaAidlInterface;
import com.xdja.comm.encrypt.IEncryptUtils;
import com.xdja.contact.util.ContactUtils;

import java.util.Map;

/**
 * Created by tangsha on 2016/7/20.
 */
public class CkmsService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ActomaAidlImp();
    }

    public class ActomaAidlImp extends IActomaAidlInterface.Stub {
        @Override
        public byte[] decryptSecKey(Map map) throws RemoteException {
            String encryData = (String)map.get(IEncryptUtils.THIRD_SEC_KEY);
            //[S]tangsha@xdja.com 2016-08-19 modify. for ckms modify return type. review by self.
            byte[] decryptKey = null;
            Map<String,Object> decryptInfo = CkmsGpEnDecryptManager.decryptDataToByte(ContactUtils.getCurrentAccount(),encryData);
            if(decryptInfo != null && (int)decryptInfo.get(CkmsGpEnDecryptManager.RESULT_CODE_TAG) == CkmsGpEnDecryptManager.CKMS_SUCC_CODE){
                decryptKey = (byte[])decryptInfo.get(CkmsGpEnDecryptManager.RESULT_INFO_TAG);
            }
            return decryptKey;
            //[E]tangsha@xdja.com 2016-08-19 modify. for ckms modify return type. review by self.
        }
    }

}
