package com.xdja.domain_mainframe;

import android.text.TextUtils;

import com.xdja.comm_mainframe.error.BusinessException;
import com.xdja.contactcommon.ContactModuleProxy;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.repository.ChipRepository;
import com.xdja.domain_mainframe.repository.UserInfoRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext0UseCase;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by tangsha on 2016/8/17.
 */
public class LauncherGetUserInfoUsecase extends Ext0UseCase<MultiResult<Object>> {
    /**
     * 检测通过（直接进入主界面）
     */
    public static final int RESULT_PASSED = 1;
    /**
     * 检测未通过
     */
    public static final int RESULT_NOT_PASSED = 0;

    private UserInfoRepository.PreUserInfoRepository userInfoRepository;
    private ChipRepository chipRepository;

    @Inject
    public LauncherGetUserInfoUsecase(ThreadExecutor threadExecutor,
                                      PostExecutionThread postExecutionThread,
                                      ChipRepository chipRepository,
                                      UserInfoRepository.PreUserInfoRepository userInfoRepository) {
        super(threadExecutor, postExecutionThread);
        this.userInfoRepository = userInfoRepository;
        this.chipRepository = chipRepository;
    }


    @Override
    public Observable<MultiResult<Object>> buildUseCaseObservable() {
        return Observable.just(true).flatMap(
                new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        return userInfoRepository.queryServerConfigsAndSave();
                    }
                }
              ).doOnNext(
                        new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if (!aBoolean) {
                                    BusinessException businessException = new BusinessException(
                                            BusinessException.ERROR_OBTAIN_SAVE_CONFIG_FAILD
                                    );
                                    throw businessException;
                                }
                            }
                        }
                ).flatMap(
                        new Func1<Boolean, Observable<Boolean>>() {
                            @Override
                            public Observable<Boolean> call(Boolean aBoolean) {
                                return chipRepository.isChangedChip();
                            }
                        }
                ).flatMap(new Func1<Boolean, Observable<MultiResult<Object>>>() {
                    @Override
                    public Observable<MultiResult<Object>> call(Boolean aBoolean) {
                        final MultiResult<Object> result = new MultiResult<>();
                        if (aBoolean) {
                            result.setResultStatus(RESULT_PASSED);
                            return Observable.just(result);
                        }
                       return   userInfoRepository.queryTicketAtLocal()
                                .zipWith(
                                        userInfoRepository.queryAccountAtLocal(),
                                        new Func2<String, Account, MultiResult<Object>>() {
                                            @Override
                                            public MultiResult<Object> call(String ticket, Account account) {
                                                if (TextUtils.isEmpty(ticket)  == false &&  account != null && account.isOnLine()) {
                                                    Map<String, Object> info = new HashMap<>();
                                                    info.put("ticket", ticket);
                                                    info.put("account", account);
                                                    result.setInfo(info);
                                                }
                                                result.setResultStatus(RESULT_PASSED);
                                                return result;

                                            }
                                        });
                    }
                }).flatMap(new Func1<MultiResult<Object>, Observable<MultiResult<Object>>>() {
                   @Override
                   public Observable<MultiResult<Object>> call(MultiResult<Object> result) {
                       final MultiResult<Object> resultWithCompanyCode = result;
                       if(result != null && result.getResultStatus() == RESULT_PASSED){
                           Map<String, Object> info = result.getInfo();
                           if(info != null && info.get("account") != null){
                               Account account = (Account) info.get("account");
                               if(ContactModuleProxy.compareCompanyCodeServerToLocal(account.getAccount())){
                                   return Observable.just(result);
                               }
                           }
                       }
                       if(result != null) {
                           //get companyCode fail, setInfo to null, so go to login
                           LogUtil.getUtils().e("0328 LauncherGetUserInfoUsecase companyCode change to need to login");
                           result.setInfo(null);
                       }
                       return Observable.just(result);
                  }
                });
    }
}
