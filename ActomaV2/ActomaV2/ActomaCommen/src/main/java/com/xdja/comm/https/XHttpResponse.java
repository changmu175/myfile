package com.xdja.comm.https;

import org.apache.http.HttpResponse;

import java.io.IOException;

/**
 * Created by LIUWANGLE on 2016/8/12.
 */
public class XHttpResponse{
    private HttpResponse httpResponse;
    private IOException ioException;

    public XHttpResponse(HttpResponse httpResponse, IOException ioException) {
        this.httpResponse=httpResponse;
        this.ioException=ioException;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public IOException getIoException() {
        return ioException;
    }

    public void setIoException(IOException ioException) {
        this.ioException = ioException;
    }
}