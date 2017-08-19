package com.xdja.presenter_mainframe.enc3rd.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import com.xdja.comm.data.QuickOpenAppBean;
import com.xdja.comm.data.QuickOpenThirdAppListBean;
import com.xdja.comm.encrypt.EncryptAppBean;
import com.xdja.presenter_mainframe.R;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * Created by geyao on 2015/11/6.
 */
public class ListUtil {

    /**
     * 初始化快速开启第三方应用设置列表数据
     *
     * @param context          上下文句柄
     * @param encryptListBeans 支持的应用列表数据
     * @return 第三方应用列表
     */
    public static List<QuickOpenThirdAppListBean> initQuickOpenThirdAppListData(
            Context context, List<EncryptAppBean> encryptListBeans) {
        //结果数据集合
        List<QuickOpenThirdAppListBean> result = new ArrayList<>();
        List<EncryptAppBean> encryptList = new ArrayList<>();
        if (encryptListBeans != null) {
            QuickOpenThirdAppListBean resuleBean;
            //循环移除未安装的应用数据
            for (int i = 0; i < encryptListBeans.size(); i++) {
                EncryptAppBean encryptListBean = encryptListBeans.get(i);
                String pkgName = encryptListBean.getPackageName();
                boolean b = ListUtil.checkPackage(context, pkgName);
                if (b) {
                    encryptList.add(encryptListBean);
                }
            }
            //对已安装的应用进行排序
            //顺序 微信 > QQ > 钉钉 > 陌陌 > 原生短信 >go短信 > hello短信 > youni短信 > 微信通讯录 > 飞信
            //1.实例化一个含有10个空数据的集合
            List<EncryptAppBean> list = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                list.add(null);
            }
            //2.循环匹配数据 符合匹配条件的替换第1步集合对应下标的数据
            for (int i = 0; i < encryptList.size(); i++) {
                String pkgName = encryptList.get(i).getPackageName();
                if (pkgName.equals("com.tencent.mm")) {//匹配微信
                    list.set(0, encryptList.get(i));
                } else if (pkgName.equals("com.tencent.mobileqq")) {//匹配QQ
                    list.set(1, encryptList.get(i));
                } else if (pkgName.equals("com.alibaba.android.rimet")) {//匹配钉钉
                    list.set(2, encryptList.get(i));
                } else if (pkgName.equals("com.immomo.momo")) {//匹配陌陌
                    list.set(3, encryptList.get(i));
                }

//                modify by thz 2016-6-23 适配第三方手机
//                        else if (pkgName.equals("com.android.mms")) {//匹配原生短信
//                            list.set(4, encryptList.get(i));
//                        }

                else if (ThirdEncAppProperty.mmsHash.containsKey(pkgName)) {//匹配go短信
                    list.set(4, encryptList.get(i));
                }

                else if (pkgName.equals("com.jb.gosms")) {//匹配go短信
                    list.set(5, encryptList.get(i));
                } else if (pkgName.equals("com.hellotext.hello")) {//匹配hello短信
                    list.set(6, encryptList.get(i));
                } else if (pkgName.equals("com.snda.youni")) {//匹配youni短信
                    list.set(7, encryptList.get(i));
                } else if (pkgName.equals("com.tencent.pb")) {//匹配微信通讯录
                    list.set(8, encryptList.get(i));
                } else if (pkgName.equals("cn.com.fetion")) {//匹配飞信
                    list.set(9, encryptList.get(i));
                }
            }
            //3.判断集合长度是否超出5 0~5为已显示应用 6~10为未显示应用 5个以内入库信息 且添加入结果集合
            QuickOpenAppBean quickOpenAppBean;
            for (int i = 0; i < list.size(); i++) {
                EncryptAppBean bean = list.get(i);
                if (bean != null) {
                    if (result.size() < 5) {
                        quickOpenAppBean = new QuickOpenAppBean();
                        quickOpenAppBean.setSort(result.size());
                        quickOpenAppBean.setAppName(bean.getAppName());
                        quickOpenAppBean.setPackageName(bean.getPackageName());
                        quickOpenAppBean.setType(QuickOpenAppBean.TYPT_SHOW);
                        resuleBean = new QuickOpenThirdAppListBean();
                        resuleBean.setType(QuickOpenAppBean.TYPT_SHOW);
                        resuleBean.setQuickOpenAppBean(quickOpenAppBean);
                        result.add(resuleBean);
                    } else {
                        quickOpenAppBean = new QuickOpenAppBean();
                        quickOpenAppBean.setSort(result.size());
                        quickOpenAppBean.setAppName(bean.getAppName());
                        quickOpenAppBean.setPackageName(bean.getPackageName());
                        quickOpenAppBean.setType(QuickOpenAppBean.TYPT_NOT_SHOW);
                        resuleBean = new QuickOpenThirdAppListBean();
                        resuleBean.setType(QuickOpenAppBean.TYPT_NOT_SHOW);
                        resuleBean.setQuickOpenAppBean(quickOpenAppBean);
                        result.add(resuleBean);
                    }
                }
            }
            //添加未显示应用标题
            if (result.size() <= 5) {
                quickOpenAppBean = new QuickOpenAppBean();
                quickOpenAppBean.setSort(result.size());
                quickOpenAppBean.setAppName(context.getResources().getString(R.string.no_show_app));
                quickOpenAppBean.setType(QuickOpenAppBean.TYPT_TITLE);
                resuleBean = new QuickOpenThirdAppListBean();
                resuleBean.setType(QuickOpenAppBean.TYPT_TITLE);
                resuleBean.setTitle(context.getResources().getString(R.string.no_show_app));
                resuleBean.setQuickOpenAppBean(quickOpenAppBean);
                result.add(resuleBean);
            } else {
                quickOpenAppBean = new QuickOpenAppBean();
                quickOpenAppBean.setSort(result.size());
                quickOpenAppBean.setAppName(context.getResources().getString(R.string.no_show_app));
                quickOpenAppBean.setType(QuickOpenAppBean.TYPT_TITLE);
                resuleBean = new QuickOpenThirdAppListBean();
                resuleBean.setType(QuickOpenAppBean.TYPT_TITLE);
                resuleBean.setTitle(context.getResources().getString(R.string.no_show_app));
                resuleBean.setQuickOpenAppBean(quickOpenAppBean);
                result.add(5, resuleBean);
            }
            //重新排序
            for (int i = 0; i < result.size(); i++) {
                result.get(i).getQuickOpenAppBean().setSort(i);
            }
        }
        return result;
    }

    /**
     * 判断包名对应的应用是否存在
     *
     * @param context     上下文句柄
     * @param packageName 包名
     * @return 是否存在
     */
    public static boolean checkPackage(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            context.getPackageManager().getApplicationInfo(packageName
                    , PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
