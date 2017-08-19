package com.xdja.imp.presenter.command;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.presenter.activity.AnTongTeamDetailPresenter;

/**
 * Created by cxp on 2015/8/7.
 */
public interface IAnTongTeamOperationCommand extends Command {


    /**
     * 关闭当前界面
     */
    void finishCurrentActivity();


    /**
     * 获得网络状态
     * @return
     */
    boolean getNetWorkState();

    /**
     * 初始化WebView数据
     * @return
     */
    AnTongTeamMessage initWebViewData();

    /**
     * 重新加载URL
     */
    void refreshUrl();

    public class AnTongTeamMessage {

        public final Context myContext;
        public String urlStr;

        public String getUrlStr() {
            return urlStr;
        }

        public void setUrlStr(String urlStr) {
            this.urlStr = urlStr;
        }

        public AnTongTeamMessage(Context context) {
            myContext = context;

        }

        @JavascriptInterface
        public void openQuestionInfoWindow(String id, String url, String title) {//modify by xnn for Actoma Team click problems @20170301

            Intent intent = new Intent(myContext, AnTongTeamDetailPresenter.class);
            intent.putExtra(ConstDef.ACTIVITY_TITLE, title);
            intent.putExtra(ConstDef.AN_TONG_NOTIFICATION_URL, urlStr + url);
            myContext.startActivity(intent);

        }
    }
}
