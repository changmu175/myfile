package com.xdja.presenter_mainframe.receiver;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.PreUseCaseModule;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.usecase.CkmsInitUseCase;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.di.components.pre.PreUseCaseComponent;
import com.xdja.safekeyservice.jarv2.SecuritySDKManager;
import com.xdja.safekeyservice.jarv2.bean.IVerifyPinResult;

import org.json.JSONObject;

import java.util.Map;

import rx.Subscriber;

/**
 * Created by tangsha on 2016/8/10.
 */
public class CkmsRefreshTask {
    private String TAG = "CkmsNewRefreshTask";
    private static final int ONE_MINUTE = 60 * 1000;
    private static final int INIT_FAIL = 50008;
    private Context mContext;
    private boolean isExpired = false;
    private static CkmsRefreshTask instance;
    public static CkmsRefreshTask getInstance(Context context, boolean expired) {
        if(instance == null) {
            synchronized (CkmsRefreshTask.class) {
                if (instance == null) {
                    instance = new CkmsRefreshTask(context);
                }
            }
        }
        instance.setExpired(expired);
        return instance;
    }

    CkmsRefreshTask(Context context){
        mContext = context;
        isExpired = false;
    }

    private void setExpired(boolean expired){
        isExpired = expired;
    }

    RefreshTask refreshTask = null;
    public void execute(){
        if(refreshTask == null || refreshTask.isFinished){
            refreshTask = new RefreshTask();
            refreshTask.execute();
        }else{
            Log.e(TAG,"exec preTask not finished!!!");
        }
    }

   class RefreshTask extends AsyncTask<Void, Void, Void>{
       public boolean isFinished = false;

       @Override
       protected void onPostExecute(Void aVoid) {
           super.onPostExecute(aVoid);
           isFinished = true;
       }

       @SuppressWarnings("ReturnOfNull")
       @Override
       protected Void doInBackground(Void... params) {
           isFinished = false;
           int ret = CkmsGpEnDecryptManager.CKMS_EXPIRED_TO_INIT;
           if(isExpired == false){
               ret = CkmsGpEnDecryptManager.ckmsRefresh();
           }
           LogUtil.getUtils().d(TAG+"RefreshTask doInBackground--------- ret "+ret+" isExpired "+isExpired);
           if(ret == CkmsGpEnDecryptManager.CKMS_SUCC_CODE){
               CkmsGpEnDecryptManager.ckmsRefreshTask(mContext);
           }else if(ret == CkmsGpEnDecryptManager.CKMS_FAIL_CODE){
               CkmsGpEnDecryptManager.ckmsRefreshTaskDelayTime(mContext,5*ONE_MINUTE);
           }else if(ret == CkmsGpEnDecryptManager.CKMS_EXPIRED_TO_INIT){
		   /*[S] modify by tangsha@20160913 for new version of safekey change init flow, review by self*/
               initCkms();
           }
           return null;
       }

       private void initCkms(){
           PreUseCaseComponent preUseCaseComponent = ((ActomaApplication) ActomaApplication.getInstance()).getAppComponent().plus(new PreUseCaseModule());
           preUseCaseComponent.ckmsInitUseCase().fill(true).execute(new Subscriber<MultiResult<Object>>() {
               @Override
               public void onCompleted() {
               }

               @Override
               public void onError(Throwable e) {
               }

               @Override
               public void onNext(MultiResult<Object> result) {
                   int status = result.getResultStatus();
                   Map<String, Object> ckmsInfo = result.getInfo();
                   LogUtil.getUtils().d(TAG+"ckmsInit onNext " + result.getResultStatus() + " ckmsInfo is " + ckmsInfo);
                   if (status == CkmsInitUseCase.INIT_OK) {
                       if(CkmsGpEnDecryptManager.getCkmsIsOpen() && ckmsInfo != null) {
                           int validTime = (int) ckmsInfo.get(CkmsInitUseCase.VALID_HOUR);
                           CkmsGpEnDecryptManager.setCkmsValidTime(validTime);
                           CkmsGpEnDecryptManager.ckmsRefreshTask(mContext);
                       }
                   }else if(ckmsInfo != null && ckmsInfo.containsKey(CkmsInitUseCase.INIT_FAIL_CODE_TAG)
                               && (int)ckmsInfo.get(CkmsInitUseCase.INIT_FAIL_CODE_TAG) == INIT_FAIL){
                           JSONObject json = SecuritySDKManager.getInstance().startVerifyPinActivity(mContext, new IVerifyPinResult() {
                               @Override
                               public void onResult(int i, String s) {
                                   if(i == 0){
                                        initCkms();
                                   }else{
                                       Log.e(TAG,"startVerifyPinActivity onResult "+i+" s "+s);
                                   }
                               }
                           });
                           LogUtil.getUtils().d(TAG+"ckmsInit fail no pin, json "+json);
                       }
               }
           });
		   /*[E] modify by tangsha@20160913 for new version of safekey change init flow, review by self*/
       }
   }


}
