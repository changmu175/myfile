package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;

import com.xdja.comm.uitl.UniversalUtil;
import com.xdja.presenter_mainframe.cmd.ChoiceLanguageCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.activity.MainFramePresenter;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.ChoiceLanguageView;
import com.xdja.presenter_mainframe.ui.uiInterface.ChoiceLanguageVu;

/**
 * Created by xdjaxa on 2016/10/11.
 */
public class ChoiceLanguagePresenter extends PresenterActivity<ChoiceLanguageCommand, ChoiceLanguageVu> implements ChoiceLanguageCommand{
    @NonNull
    @Override
    protected Class<? extends ChoiceLanguageVu> getVuClass() {
        return ChoiceLanguageView.class;
    }

    @NonNull
    @Override
    protected ChoiceLanguageCommand getCommand() {
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    /*[S]tangsha add@20161012 for multi language*/
    @Override
    public void setLanguage(int index) {
        doSetLanguage(index);
        finish();
    }


    private void doSetLanguage(int type){
        int current = UniversalUtil.getLanguage(this);
        if(current != type) {
            UniversalUtil.setLanguage(this,type);
            //android.os.Process.killProcess(android.os.Process.myPid());
            UniversalUtil.changeLanguageConfig(this);
          //  ActivityStack.getInstanse().exitApp();
            sendBroadcast(new Intent(ACTION_ACTOMA_SET_LANGUAGE));
            //        System.exit(0);
            startActivity(Navigator.generateIntent(MainFramePresenter.class));
        }
    }
    /*[E]tangsha add@20161012 for multi language*/
}
