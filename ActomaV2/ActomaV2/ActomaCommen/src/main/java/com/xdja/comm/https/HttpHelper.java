package com.xdja.comm.https;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.comm.https.Property.HttpProperty;
import com.xdja.dependence.uitls.LogUtil;

//import com.xdja.cipherkey.utils.Log;

/**
 * Created by THZ on 2015/5/4.
 * 网络请求的封装
 */
public class HttpHelper {

    /**
     * 请求的方式
     */
    private HttpMethod method;

    /**
     * URL扩展
     */
    private String urlPath;

    /**
     * url
     */
    private String url;

    /**
     * 协议body
     */
    private String body;


    /**
     * 上下文
     */
    private Context context;

    private HttpRequestBase httpEntitiy = null;
    /**
     * 构造方法
     * @param context 上下文
     * @param method 请求的方式 get，post，put，delete等
     * @param url 请求的url
     * @param urlPath url扩展路径
     */
    public HttpHelper(Context context, HttpMethod method, String url, String urlPath) {
        this.method = method;
        this.urlPath = urlPath;
        this.url = url;
        this.context = context;
        httpClient();
    }

    /**
     * 请求数据
     * @param body 协议body
     * @return 请求返回数据
     */
    public XHttpResponse receive(String body) {

        HttpResponse httpResponse = null;
        IOException ioException=null;//add by lwl
        try {
            if (method == HttpMethod.POST) {
                setData((HttpPost) httpEntitiy, body);
                this.body = body;
            } else if (method == HttpMethod.PUT) {
                setData((HttpPut) httpEntitiy, body);
                this.body = body;
            }
//            if (TextUtils.isEmpty(body)) {
//            	httpEntitiy.addHeader("Content-Length", "0");
//			} else {
//				httpEntitiy.addHeader("Content-Length", "" + body.length());
//			}
//           
            HttpClient httpClient = HttpsClient.getSpecialKeyStoreClient(context);
            HttpParams params = httpClient.getParams();
//            BasicNameValuePair valuePair = new BasicNameValuePair("Content-Length", "100");
//            httpEntitiy.setParams(valuePair);
            HttpConnectionParams.setConnectionTimeout(params, HttpProperty.TIME_OUT);
            HttpConnectionParams.setSoTimeout(params, HttpProperty.TIME_OUT);
            httpResponse = httpClient.execute(httpEntitiy);
        } catch (IOException e) {
            e.printStackTrace();
            ioException=e;
        }

        return new XHttpResponse(httpResponse,ioException);
    }

    /**
     * 添加Header
     * @param authBean 验证签名的数据结构
     */
    public void addHeader(AuthBean authBean) {
        if (authBean != null) {
            ArrayList<Header> headers = authBean.toHeads();
            addHeader(headers);
        }
    }

    /**
     * 添加Header
     * @param header header
     */
    public void addHeader(Header header) {
        httpEntitiy.addHeader(header);
    }

    /**
     * 添加Headers
     * @param headers header数组
     */
    public void addHeader(ArrayList<Header> headers) {
        if (headers != null) {
            for (Header header : headers) {
                if (header != null) {
                    httpEntitiy.addHeader(header);
                }
            }
        }
    }
    /**
     * httpClient请求数据
     */
    private void httpClient() {
        String url = this.url + this.urlPath;
        
        //modify by 唐会增   2015-8-23
        //对url的空格进行过滤\回车、换行
        url = url.replaceAll(" ", "");
        url = url.replaceAll("\\n", "");
        url = url.replaceAll("\\r", "");
        LogUtil.getUtils().i("HttpHelper thz httpClient request url : " + url);
        if (method == HttpMethod.GET) {
            httpEntitiy = new HttpGet(url);
        } else if (method == HttpMethod.POST) {
            httpEntitiy = new HttpPost(url);
        } else if (method == HttpMethod.PUT) {
            httpEntitiy = new HttpPut(url);
        } else if (method == HttpMethod.DELETE) {
            httpEntitiy = new HttpDelete(url);
        }
//         httpEntitiy.setHeader("Accept", "text/json");
//         httpEntitiy.setHeader("Content-Type", "text/json");
    }


    /**
     * 设置body数据
     * @param requestBase 请求的request
     * @param body 请求的数据body
     */
    private void setData(HttpEntityEnclosingRequestBase requestBase, String body) {
        try {
            // 设置字符集
            if (!TextUtils.isEmpty(body)) {
                ByteArrayEntity entity = new ByteArrayEntity(body.getBytes());
                entity.setContentType("application/json");
                Log.i("thz", "entity .body : " + body);
                requestBase.setEntity(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 返回所有的Header
     * @return 所有协议头header
     */
    public Header[] getAllHeaders() {
        return httpEntitiy.getAllHeaders();
    }


    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
