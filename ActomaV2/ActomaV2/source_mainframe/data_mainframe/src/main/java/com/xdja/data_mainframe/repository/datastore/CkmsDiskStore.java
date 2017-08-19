package com.xdja.data_mainframe.repository.datastore;

import android.support.annotation.NonNull;

import java.util.Map;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;

/**
 * Created by tangsha on 2016/7/4.
 */
public class CkmsDiskStore implements CkmsStore{

    @Inject
    public CkmsDiskStore() {

    }
    @SuppressWarnings("ReturnOfNull")
    @Override
    public Observable<Response<Map<String, String>>> ckmsOperSign(@NonNull String ckmsOperStr) {
        return null;
    }
}
