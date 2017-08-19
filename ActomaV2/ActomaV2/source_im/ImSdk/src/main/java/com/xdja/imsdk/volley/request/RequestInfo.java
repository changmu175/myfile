package com.xdja.imsdk.volley.request;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.xdja.http.HttpEntity;

public class RequestInfo {
	
	public String boundary = String.valueOf(System.currentTimeMillis());
	
	public String url;
	public String tag;
    public String path;
	public boolean needRetry;
    public boolean isSupportRange;
	public Map<String,String> params = new HashMap<String, String>() ;
	public Map<String, String> headers = new HashMap<String, String>();
	public Map<String, File> fileParams = new HashMap<String, File>();
	public HttpEntity httpEntity;
	
    public RequestInfo() {
    }

    public RequestInfo(String url, Map<String, String> params) {
        this.url = url;
        this.params = params;
    }
    

    public String getFullUrl() {
        if (url != null && params != null) {
            StringBuilder sb = new StringBuilder();
            if (!url.contains("?")) {
                url = url + "?";
            } else {
                if (!url.endsWith("?")) {
                    url = url + "&";
                }
            }
            Iterator<String> iterotor = params.keySet().iterator();
            try {
                while (iterotor.hasNext()) {
                    String key = (String) iterotor.next();
                    if (key != null) {
                        if (params.get(key) != null) {
                            sb.append(URLEncoder.encode(key, "utf-8")).append("=")
                                    .append(URLEncoder.encode(params.get(key), "utf-8")).append("&");
                        }
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (sb.length() > 0 && sb.lastIndexOf("&") == sb.length() - 1) {
                sb.deleteCharAt(sb.length() - 1);
            }
            return url + sb.toString();
        }
        return url;
    }
    
    public String getUrl() {
        return url;
    }

    public String getPath(){
        return  path;
    }
    
    public String getTag(){
    	return tag;
    }

    public Map<String, String> getParams() {
        return params;
    }
    
    public Map<String, File> getFileParams() {
        return fileParams;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
    
    public void setHttpEntity(HttpEntity entity){
    	httpEntity = entity;
    }
    
    public HttpEntity getHttpEntity(){
    	return httpEntity;
    }
	
    public void put(String key, String value) {
    	params.put(key, value);
    }
    
    public void putHeader(String key, String value){
    	headers.put(key, value);
    }

    public void putHeader(Map<String, String> headers){
        this.headers.putAll(headers);
    }
    
    public void put(String key, File file) {
    	if (fileParams.containsKey(key)) {
    		fileParams.put(key + boundary + fileParams.size(), file);
    	} else {
    		fileParams.put(key, file);
    	}
    }
    
    public void putFile(String key, String path) {
    	if (fileParams.containsKey(key)) {
    		fileParams.put(key + boundary + fileParams.size(), new File(path));
    	} else {
    		fileParams.put(key, new File(path));
    	}
    }
    
	public void putAllParams(Map<String, Object> objectParams) {
		for (String key : objectParams.keySet()) {
			Object value = objectParams.get(key);
			if (value instanceof String) {
				put(key, (String) value);
			} else if (value instanceof File) {
				put(key, (File) value);
			}
		}
	}
}
