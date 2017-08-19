package com.xdja.data_mainframe.db.encrypt;

import com.xdja.comm.encrypt.EncryptAppBean;
import com.xdja.dependence.uitls.LogUtil;

import java.util.List;

/**
 * Created by geyao on 2015/11/18.
 * 重构-第三方加密应用列表数据库对外提供的操作类
 */
public class EncryptHelper {

    /**
     * 查找全部的第三方应用信息数据
     *
     * @return 查找结果
     */
    public static List<EncryptAppBean> queryEncryptApps() {
        EncryptAppsDao dao = null;
        try {
            dao = EncryptAppsDao.instance().open();
            return dao.queryAll();
        } catch (Exception ex) {
            LogUtil.getUtils(ex.getMessage());
            return null;
        } finally {
            if (dao != null) {
                dao.close();
            }
        }
    }

    /**
     * 插入第三方应用信息数据
     *
     * @param bean    第三方应用信息数据
     * @return 插入结果
     */
    public static boolean saveEncryptApp(EncryptAppBean bean) {
        if (bean == null) {
            LogUtil.getUtils().i("参数不合法");
            return false;
        }
        EncryptAppsDao dao = null;
        try {
            dao = EncryptAppsDao.instance().open();
            return dao.insert(bean);
        } catch (Exception ex) {
            LogUtil.getUtils(ex.getMessage());
            return false;
        } finally {
            if (dao != null) {
                dao.close();
            }
        }
    }

    /**
     * 删除指定的第三方应用信息
     *
     * @param field     条件字段
     * @param condition 条件数值
     * @return 删除结果
     */
    public static boolean deleteEncryptApp(@EncryptAppsDao.ENCDEC_FIELD String field,
                                           String condition) {
        if (field == null || condition == null) {
            LogUtil.getUtils().i("参数不合法");
            return false;
        }
        EncryptAppsDao dao = null;
        try {
            dao = EncryptAppsDao.instance().open();
            return dao.delete(field, condition);
        } catch (Exception ex) {
            LogUtil.getUtils(ex.getMessage());
            return false;
        } finally {
            if (dao != null) {
                dao.close();
            }
        }
    }

//    /**
//     * 修改第三方应用信息数据
//     *
//     * @param bean    第三方应用信息数据
//     * @return 插入结果
//     */
//    public static boolean updateEncryptApp(@NonNull EncryptAppBean bean) {
//        Context context = CommonApplication.getApplication().getApplicationContext();
//        if (context == null || bean == null) {
//            LogUtil.getUtils().i("上下文不能为空");
//            return false;
//        }
//        EncryptAppsDao EncryptAppsDao = null;
//        try {
//            EncryptAppsDao = EncryptAppsDao.instance().open(context);
//            return EncryptAppsDao.update(EncryptAppsDao.FIELD_PACKAGENAME,
//                    bean.getPackageName(), bean);
//        } catch (Exception ex) {
//            LogUtil.getUtils(ex.getMessage());
//            return false;
//        } finally {
//            if (EncryptAppsDao != null) {
//                EncryptAppsDao.close();
//            }
//        }
//    }
//
//    /**
//     * 查找对应字段条件的第三方应用信息数据
//     *
//     * @param context   上下文句柄
//     * @param field     条件字段
//     * @param condition 条件值
//     * @return 查找结果
//     */
//    public static EncryptAppBean queryEncryptListData(@NonNull Context context,
//                                                      @EncryptAppsDao.ENCDEC_FIELD String field,
//                                                      @NonNull String condition) {
//        if (context == null) {
//            LogUtil.getUtils().i("上下文不能为空");
//            return null;
//        }
//        EncryptAppsDao EncryptAppsDao = null;
//        try {
//            EncryptAppsDao = EncryptAppsDao.instance().open(context);
//            EncryptAppBean result = EncryptAppsDao.query(field, condition);
//            return result;
//        } catch (Exception ex) {
//            LogUtil.getUtils(ex.getMessage());
//            return null;
//        } finally {
//            if (EncryptAppsDao != null) {
//                EncryptAppsDao.close();
//            }
//        }
//    }
//
//    /**
//     * 删除第三方应用信息
//     *
//     * @return 删除结果
//     */
//    public static boolean clear() {
//        Context context = CommonApplication.getApplication().getApplicationContext();
//        if (context == null) {
//            LogUtil.getUtils().i("参数不能为空");
//            return false;
//        }
//        EncryptAppsDao EncryptAppsDao = null;
//        try {
//            EncryptAppsDao = EncryptAppsDao.instance().open(context);
//            return EncryptAppsDao.clear();
//        } catch (Exception ex) {
//            LogUtil.getUtils(ex.getMessage());
//            return false;
//        } finally {
//            if (EncryptAppsDao != null) {
//                EncryptAppsDao.close();
//            }
//        }
//    }
}
