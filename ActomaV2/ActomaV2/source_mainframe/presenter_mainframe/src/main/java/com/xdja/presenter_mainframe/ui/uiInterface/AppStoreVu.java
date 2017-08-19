package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.FragmentVu;
import com.xdja.presenter_mainframe.cmd.AppStoreCommand;

/**
 * Created by chenbing on 2015/7/21.
 */
public interface AppStoreVu extends FragmentVu<AppStoreCommand> {
        /**
         * 设置webview
         *
         * @param url 要加载内容的url
         */
        void loadUrl(String url);

        /**
         * 刷新页面
         */
        void refresh();
        //[S]add by lixiaolong on 20160912. fix bug 3789. review by wangchao1.
        void showErrorView();

        void showCheckUrlView();

        void hideCheckUrlView();
        //[E]add by lixiaolong on 20160912. fix bug 3789. review by wangchao1.
}
