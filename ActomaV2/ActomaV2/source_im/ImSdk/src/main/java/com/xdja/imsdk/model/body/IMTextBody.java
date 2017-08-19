package com.xdja.imsdk.model.body;

/**
 * 项目名称：ImSdk             <br>
 * 类描述  ：文本类型消息内容体   <br>
 * 创建时间：2016/11/21 16:13  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class IMTextBody extends IMMessageBody {
	/**
	 * 文本内容
	 */
    private String content;

    public IMTextBody(String content) {
        this.content = content;
    }

    

    /**
	 * 获取文本内容
	 * @return the content 文本内容
	 */
	public String getContent() {
		return content;
	}



	/**
	 * 设置 文本内容
	 * @param content 文本内容
	 */
	public void setContent(String content) {
		this.content = content;
	}



	@Override
    public String toString() {
        return "IMTextBody{" +
                "content='" + content + '\'' +
                '}';
    }
}
