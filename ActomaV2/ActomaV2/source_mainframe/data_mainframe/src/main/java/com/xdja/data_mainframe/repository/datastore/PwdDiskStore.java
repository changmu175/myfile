package com.xdja.data_mainframe.repository.datastore;

import android.support.annotation.NonNull;

import com.xdja.domain_mainframe.model.MultiResult;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.data_mainframe.repository.datastore</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/21</p>
 * <p>Time:20:19</p>
 */
@SuppressWarnings("ReturnOfNull")
public class PwdDiskStore implements PwdStore {

    @Inject
    public PwdDiskStore() {

    }

    @Override
    public Observable<Response<Map<String, String>>> checkRestPwdAuthCode(@NonNull String mobile,
                                                                          @NonNull String authCode,
                                                                          @NonNull String innerAuthCode) {
        return null;
    }

    @Override
    public Observable<Response<Void>> restPwdByAuthCode(@NonNull String mobile,
                                                          @NonNull String innerAuthCode,
                                                          @NonNull String passwd) {
        return null;
    }

    @Override
    public Observable<Response<Void>> restPwdByFriendMobiles(@NonNull String account,
                                                               @NonNull String innerAuthCode,
                                                               @NonNull String passwd) {
        return null;
    }

    @Override
    public Observable<Response<MultiResult<Object>>> authFriendPhone(@NonNull String account, @NonNull List<String> mobiles) {
        return null;
    }
}
