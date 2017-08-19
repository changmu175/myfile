package com.xdja.contact.ui.def;

import com.xdja.contact.bean.dto.CommonDetailDto;
import com.xdja.contact.presenter.command.ICommonDetailCommand;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * Created by wanghao on 2015/10/23.
 */
public interface ICommonDetailVu extends ActivityVu<ICommonDetailCommand> {

    void setDetailData(CommonDetailDto parcelable);

    void loadingDialogController(boolean open, String msg);
}
