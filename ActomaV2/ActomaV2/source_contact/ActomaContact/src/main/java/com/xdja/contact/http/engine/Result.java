package com.xdja.contact.http.engine;

import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.https.Property.HttpResultSate;

/**
 * 下载线程返回数据结果
 */
public class Result {

	//这里针对的是thz里面的数据格式
	private HttpResultSate result = HttpResultSate.SUCCESS;
	//如果Property.HttpResultSate FAIL 调用当前对象结果
	private HttpErrorBean httpErrorBean;
	//如果请求数据正常返回拿到的数据
	private String body;

	public boolean isError(){
		if(result == HttpResultSate.FAIL)
			return true;
		return false;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public HttpErrorBean getHttpErrorBean() {
		return httpErrorBean;
	}

	public void setHttpErrorBean(HttpErrorBean httpErrorBean) {
		this.httpErrorBean = httpErrorBean;
	}

	public HttpResultSate getResult() {
		return result;
	}

	public void setResult(HttpResultSate result) {
		this.result = result;
	}
}
