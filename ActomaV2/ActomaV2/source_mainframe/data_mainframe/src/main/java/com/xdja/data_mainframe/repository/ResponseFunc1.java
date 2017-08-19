package com.xdja.data_mainframe.repository;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.xdja.comm.server.ActomaController;
import com.xdja.data_mainframe.R;
import com.xdja.data_mainframe.di.PreRepositoryModule;
import com.xdja.dependence.exeptions.ClientException;
import com.xdja.dependence.exeptions.OkException;
import com.xdja.dependence.exeptions.ServerException;
import com.xdja.frame.data.bean.RespErrorBean;

import java.io.InputStreamReader;
import java.util.Set;

import javax.inject.Named;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.functions.Func1;

/**
 * <p>Summary:扩展的RxJava转换类，可以解析服务端响应的Http请求</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.frame.data.repository</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/15</p>
 * <p>Time:12:00</p>
 */
public class ResponseFunc1<T> implements Func1<Response<T>, T> {

    private Set<Integer> customStatus;

    private Set<Integer> errorStatus;

    private Gson gson;

    public ResponseFunc1(@Named(PreRepositoryModule.NAMED_ERRORSTATUS) Set<Integer> errorStatus, Gson gson) {
        this.errorStatus = errorStatus;
        this.gson = gson;
    }

    public ResponseFunc1<T> setCustomStatus(Set<Integer> customStatus) {
        this.customStatus = customStatus;
        return this;
    }

    @Override
    public T call(Response<T> response) {

        if (response.isSuccessful()) {
            return response.body();
        } else {
            //wangchao for 2131 (null exception)
            ResponseBody responseBody = response.errorBody();
            if (responseBody!= null && (errorStatus.contains(response.code())
                    || customStatus != null && customStatus.contains(response.code()))) {

                OkException exception = null;
                try {
                    RespErrorBean respErrorBean = gson.fromJson(
                            new InputStreamReader(responseBody.byteStream()),
                            RespErrorBean.class
                    );
                    if (respErrorBean != null) {
                        exception = new ServerException(
                                respErrorBean.getMessage(),
                                respErrorBean.getErrCode()
                        );

                    }
                } catch (JsonIOException e) {
                    exception = new ClientException(e.getMessage(),
                            ClientException.CODE_UNKOWN_EXCEPTION);
                } catch (JsonSyntaxException e) {
                    exception = new ClientException(e.getMessage(),
                            ClientException.CODE_UNKOWN_EXCEPTION);
                } finally {
                    throw exception;
                }
            }
            ServerException exception = new ServerException(
                    ActomaController.getApp().getString(R.string.unknow_server_ex),
                    ServerException.CODE_UNKNOW_SERVER_EXCEPTION
            );
            throw exception;
        }
    }
}