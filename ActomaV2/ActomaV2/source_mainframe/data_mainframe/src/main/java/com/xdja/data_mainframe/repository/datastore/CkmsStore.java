package com.xdja.data_mainframe.repository.datastore;

import android.support.annotation.NonNull;

import java.util.Map;

import retrofit2.Response;
import rx.Observable;

/**
 * Created by tangsha on 2016/7/4.
 */
public interface CkmsStore {
    Observable<Response<Map<String, String>>> ckmsOperSign(@NonNull String ckmsOperStr);
}
