package com.xdja.imp.ui.vu;

import android.support.v4.view.PagerAdapter;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.view.FragmentVu;

/**
 * <p>Author: xdjaxa         </br>
 * <p>Date: 2016/12/5 9:51   </br>
 * <p>Package: com.xdja.imp.ui.vu</br>
 * <p>Description:            </br>
 */
public interface ILocalFileListVu<P extends Command> extends FragmentVu<P> {


    /**
     * 设置适配器
     *
     * @param adapter 适配器
     */
    void setFragmentAdapter(PagerAdapter adapter);

}
