package com.xdja.domain_mainframe.usecase.userInfo;

import com.xdja.domain_mainframe.model.Account;
import com.xdja.domain_mainframe.repository.UserInfoRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext0UseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by ldy on 16/5/4.
 * 获取当前账户信息用例,如果当前账户不存在,则为空
 */
public class GetCurrentAccountInfoUseCase extends Ext0UseCase<Account> {
    private final UserInfoRepository userInfoRepository;

    @Inject
    public GetCurrentAccountInfoUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                                        UserInfoRepository userInfoRepository) {
        super(threadExecutor, postExecutionThread);
        this.userInfoRepository = userInfoRepository;
    }

    /**
     * 构建业务处理事件流
     *
     * @return 目标事件流
     */
    @Override
    public Observable<Account> buildUseCaseObservable() {
        return userInfoRepository.getCurrentAccountInfo();
//        Account account = new Account();
//        account.setAccount("ldy");
//        account.setNickName("EndSmile");
//        account.setAvatarId("http://b.hiphotos.baidu.com/image/h%3D200/sign=0afb9ebc4c36acaf46e091fc4cd88d03/bd3eb13533fa828b670a4066fa1f4134970a5a0e.jpg");
//        account.setThumbnailId("http://b.hiphotos.baidu.com/image/h%3D200/sign=0afb9ebc4c36acaf46e091fc4cd88d03/bd3eb13533fa828b670a4066fa1f4134970a5a0e.jpg");
//        List<String> phones = new ArrayList<>();
//        phones.add("15093389641");
//        account.setMobiles(phones);
//        return Observable.just(account);
    }
}
