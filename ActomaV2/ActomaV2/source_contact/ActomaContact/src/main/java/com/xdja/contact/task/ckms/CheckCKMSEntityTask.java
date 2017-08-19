package com.xdja.contact.task.ckms;

import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.comm.contacttask.ContactAsyncTask;
import com.xdja.contact.util.ContactCkmsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by wal@xdja.com on 2016/8/8.
 */
public class CheckCKMSEntityTask extends ContactAsyncTask<Void, Void, Void>  {
    private ArrayList<String> selectAccountList;
    private List<String> noSecEntityAccounts;
    private CreateSGroupResultCallback createSGroupResultCallback;
    private int flagCode = RESULT_OK;
    public static final int RESULT_OK = 0;
    public static final int RESULT_FAIL = -1;
    private String TAG = "ActomaContact CheckCKMSEntityTask";

    public CheckCKMSEntityTask(ArrayList<String> accountList, CreateSGroupResultCallback callback) {
        selectAccountList = accountList;
        createSGroupResultCallback = callback;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        createSGroupResultCallback.onTaskPreExec();
    }


    @Override
    protected Void doInBackground(Void... params) {
        publishProgress();
        Map<String, Object> fitResultMap = ContactCkmsUtils.getNoSecEntityAccounts(selectAccountList);
        if (CkmsGpEnDecryptManager.CKMS_SUCC_CODE == (int) fitResultMap.get(ContactCkmsUtils.RESULT_CODE_TAG)) {
            if (fitResultMap.get(CkmsGpEnDecryptManager.RESULT_INFO_TAG) != null) {
                noSecEntityAccounts = (List<String>) fitResultMap.get(ContactCkmsUtils.RESULT_INFO_TAG);
                LogUtil.getUtils().e(TAG+" no SecEntity "+noSecEntityAccounts.toString());
                selectAccountList.removeAll(noSecEntityAccounts);
            }
        }else{
            flagCode = RESULT_FAIL;
        }
        createSGroupResultCallback.onTaskBackgroundOk(flagCode,noSecEntityAccounts,null,null);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        createSGroupResultCallback.onTaskPostExec(flagCode,null);
    }

}
