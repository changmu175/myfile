package com.xdja.presenter_mainframe.receiver;

import android.os.AsyncTask;

import com.xdja.comm.ckms.CkmsGpEnDecryptManager;

/**
 * Created by tangsha on 2016/7/27.
 */
public class ParseThirdEnPushMsgTask extends AsyncTask<Void, Void, Void>{
        String msgContent;
        String currentAccount;

        ParseThirdEnPushMsgTask(String content,String account){
            msgContent = content;
            currentAccount = account;
        }

        @SuppressWarnings("ReturnOfNull")
        @Override
        protected Void doInBackground(Void... params) {
            CkmsGpEnDecryptManager.parseThirdEnPushInfo(msgContent,currentAccount);
            return null;
        }

}
