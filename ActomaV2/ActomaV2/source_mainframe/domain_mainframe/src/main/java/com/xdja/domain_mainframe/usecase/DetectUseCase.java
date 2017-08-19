package com.xdja.domain_mainframe.usecase;


import com.xdja.comm.cust.CustInfo;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm_mainframe.error.BusinessException;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.repository.ChipRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext0UseCase;
import com.xdja.soc.certupload.CertUploadErrorCode;
import com.xdja.soc.certupload.CertUploadManager;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * <p>Summary:初始化检测流程的用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.domain_mainframe.usecase</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/5/3</p>
 * <p>Time:11:53</p>
 */
public class DetectUseCase extends Ext0UseCase<MultiResult<Object>> {


    /**
     * 检测通过（直接进入主界面）
     */
    public static final int RESULT_PASSED = 1;
    /**
     * 检测未通过
     */
    public static final int RESULT_NOT_PASSED = 0;
	
	/*[S]modify by xienana @20161010 for security chip driver detection (rummager : tangsha)*/
    public static final int CKMS_UNINSTALL = 0;
    public static final int CKMS_UPDATE = 1;
    public static final int CKMS_ALREADY_INSTALL = 2;
    public static final int CKMS_INSTALL_FAIL = 3;
    /*[E]modify by xienana @20161010 for security chip driver detection (rummager : tangsha)*/

    private ChipRepository chipRepository;

    public static final String ERROR_CODE_SEP = "$";

    @Inject
    public DetectUseCase(ThreadExecutor threadExecutor,
                         PostExecutionThread postExecutionThread,
                         ChipRepository chipRepository
                        ) {
        super(threadExecutor, postExecutionThread);
        this.chipRepository = chipRepository;
    }

    /*[S]modify by xienana @20160721 for security chip detection initialize process(rummager : tangsha)*/
    @Override
    public Observable<MultiResult<Object>> buildUseCaseObservable() {
        return chipRepository.checkDriverExist()
                .doOnNext(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                if (integer == CKMS_UNINSTALL) {
                                    //chip guanjia not install
                                    BusinessException businessException = new BusinessException(
                                            BusinessException.ERROR_DRIVER_NOT_EXIST
                                    );
                                    throw businessException;
                                } else if(integer == CKMS_UPDATE) {
                                    //chip guanjia need update
                                    BusinessException businessException = new BusinessException(
                                            BusinessException.ERROR_DRIVER_NEED_UPDATE
                                    );
                                    throw businessException;
                                }else if(integer == CKMS_INSTALL_FAIL){
                                    //chip guanjia install fail
                                    BusinessException businessException = new BusinessException(
                                            BusinessException.ERROR_DRIVER_INSTALL_FAIL
                                    );
                                    throw businessException;
                                }
                            }
                        }
                )
                .flatMap(new Func1<Integer, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Integer integer) {
                        return chipRepository.checkChipExist();
                    }
                })
                .doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (!aBoolean) {
                            BusinessException businessException = new BusinessException(
                                    BusinessException.ERROR_CHIP_NOT_EXIST
                            );
                            throw businessException;
                        }
                    }
                })
                .flatMap(
                        new Func1<Boolean, Observable<Boolean>>() {
                            @Override
                            public Observable<Boolean> call(Boolean aBoolean) {
                                return chipRepository.isChipActived();
                            }
                        }
                )
                .flatMap(
                        new Func1<Boolean, Observable<Boolean>>() {
                            @Override
                            public Observable<Boolean> call(Boolean aBoolean) {
                                if (!aBoolean) {
                                    return chipRepository.activeChip();
                                }
                                return Observable.just(Boolean.TRUE);
                            }
                        }
                )
                .doOnNext(
                        new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if (!aBoolean) {
                                    BusinessException businessException = new BusinessException(
                                            BusinessException.ERROR_CHIP_ACTIVIE_FAILD
                                    );
                                    throw businessException;
                                }
                            }
                        }
                ).flatMap(new Func1<Boolean, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Boolean aBoolean) {
                        int uploadRes = -1;
                        if (aBoolean) {
                            if (CustInfo.isTelcom()) {
                                CertUploadManager manager = new CertUploadManager.Builder().build(ActomaController.getApp().getApplicationContext());
                                uploadRes = manager.uploadCert();
                                LogUtil.getUtils().w("DetectUsecase CertUploadManager uploadCert return " + uploadRes);
                            } else {
                                uploadRes = CertUploadErrorCode.OK;
                            }
                        }
                        return Observable.just(uploadRes);
                    }
                }).doOnNext(
                        new Action1<Integer>() {
                            @Override
                            public void call(Integer execRes) {
                                if (execRes != CertUploadErrorCode.OK) {
                                    BusinessException businessException = new BusinessException(
                                            BusinessException.ERROR_CKMS_UPLOAD_INFO_FAIL+ERROR_CODE_SEP+execRes
                                    );
                                    throw businessException;
                                }
                            }
                        }
                ).flatMap(
                        new Func1<Integer, Observable<MultiResult<Object>>>() {
                            @Override
                            public Observable<MultiResult<Object>> call(Integer execRes) {
                                final MultiResult<Object> result = new MultiResult<>();
                                if (execRes == CertUploadErrorCode.OK) {
                                    result.setResultStatus(RESULT_PASSED);
                                }else{
                                    result.setResultStatus(RESULT_NOT_PASSED);
                                }
                                return Observable.just(result);
                            }
                        }
                );

    }
}
