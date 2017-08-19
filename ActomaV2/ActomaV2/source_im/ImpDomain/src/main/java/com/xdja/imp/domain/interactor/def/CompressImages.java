package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.WebPageInfo;

import java.util.List;

/**
 * 项目名称：ActomaV2
 * 类描述：压缩网页文件
 * 创建人：yuchangmu
 * 创建时间：2017/3/10.
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public interface CompressImages extends Interactor<List<WebPageInfo>> {

    CompressImages compressFile(List<WebPageInfo> fileInfoList);
}
