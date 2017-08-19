package com.xdja.domain_mainframe.usecase.thirdencrypt;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.ServiceManager;
import android.text.TextUtils;

import com.xdja.comm.uitl.UniversalUtil;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.xposeconfig.IModuleWriter;
import com.xdja.domain_mainframe.xposeconfig.ModuleWriterProxy;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext1UseCase;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Summary:拦截模块升级用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.domain</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/8/28</p>
 * <p>Time:14:39</p>
 */
public class HookUpdateUseCase extends Ext1UseCase<Context, Boolean> {

    @Inject
    public HookUpdateUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
    }

    //[S]modify by tangsha@20161214 for HookService update strage change
    private final String HOOK_NAME = "HookService_xposed.apk";
    private final String HOOK_VERSION_SUFFIX = "B";
    private final String XDJA_HOOK_NAME = "HookService_xdjaposed.apk";
    private final String XDJA_HOOK_VERSION_SUFFIX = "A";

    @Override
    public Observable<Boolean> buildUseCaseObservable() {
        return Observable.just(p).flatMap(new Func1<Context, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(Context context) {
                String currentXposedName = UniversalUtil.getCurrentXposeServiceName();
                if (TextUtils.isEmpty(currentXposedName) == false) {
                    IBinder binder = ServiceManager.getService(currentXposedName);
                    IModuleWriter moduleWriter = new ModuleWriterProxy(binder);
                    try {
                        String versionStr = moduleWriter.getVersionCode(currentXposedName);
                        int localVersionCode = -1;
                        if (TextUtils.isEmpty(versionStr) || versionStr.equalsIgnoreCase("noversion")) {
//                        localVersionCode = 100000;
                            //modify by thz  修改默认值为1，当版本号是空时，认为当前没有钩子需要进行钩子的升级
                            localVersionCode = 1;
                        } else {
                            String versionStrRep = versionStr.replace(".", "");
                            int versionType = versionStr.indexOf("_");
                            if(versionType != -1){
                                localVersionCode = Integer.parseInt(versionStrRep.substring(0,versionType));
                            }else {
                                localVersionCode = Integer.parseInt(versionStrRep);
                            }
                        }
                        LogUtil.getUtils().i("从本地服务获取到的Hook版本为：" + localVersionCode+" versionStr "+versionStr);
                        String hookName;
                        if(currentXposedName.compareTo(UniversalUtil.XPOSED_SERVICE_NAME) == 0) {
                            //Xposed框架，老版本
                            hookName = HOOK_NAME;
                        }else{
                            //xdjaposed框架，新版本
                            hookName = XDJA_HOOK_NAME;
                        }
                        copyArchive(context, hookName);
                        LogUtil.getUtils().i("拷贝Hook到私有目录");
                        PackageManager pm = context.getPackageManager();
                        PackageInfo pkgInfo = pm.getPackageArchiveInfo(
                                context.getFilesDir() + "/" + hookName, PackageManager.GET_ACTIVITIES);
                        int versionCode = pkgInfo.versionCode;
                        LogUtil.getUtils().i("获取到的待安装Hook版本为：" + versionCode);
                        if (localVersionCode > 0 && versionCode > 0) {
                            boolean xdjaNotMatch = hookName.compareTo(XDJA_HOOK_NAME) == 0 && versionStr.endsWith(XDJA_HOOK_VERSION_SUFFIX) == false;
                            boolean orgNotMatch = hookName.compareTo(HOOK_NAME) == 0 && versionStr.endsWith(HOOK_VERSION_SUFFIX) == false;
                            LogUtil.getUtils().i("HookUpdateUseCase xdjaNotMatch "+xdjaNotMatch+" orgNotMatch "+orgNotMatch);
                            if (localVersionCode < versionCode || xdjaNotMatch || orgNotMatch) {
                                FileInputStream inputStream = context.openFileInput(hookName);
                                //[S]modify by lixiaolong on 20161008.
                                FileDescriptor fileDescriptor = inputStream.getFD();
                                //writeModule(fileDescriptor);
                                moduleWriter.writeModule(fileDescriptor, currentXposedName);
                                //[E]modify by lixiaolong on 20161008.
                                LogUtil.getUtils().i("调用writeModule完成");
                                return Observable.just(Boolean.TRUE);
                            }
                        }
                        return Observable.just(Boolean.FALSE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Observable.error(e);
                    }
                }else{
                    LogUtil.getUtils().e("HookUpdateUseCase currentXposedName is empty!!! "+currentXposedName);
                }
                return Observable.just(Boolean.FALSE);
            }
        });
    }
	//[E]modify by tangsha@20161214 for HookService update strage change

//[S]removed by lixiaolong on 20161008.
//    /**
//     * 通知本地服务进行文件拷贝处理
//     *
//     * @param fileDescriptor FileDescriptor
//     * @throws RemoteException
//     */
//    private void writeModule(FileDescriptor fileDescriptor) throws RemoteException {
//        IBinder binder = ServiceManager.getService(XPOSED_SERVICE_NAME);
//        IModuleWriter moduleWriter = new ModuleWriterProxy(binder);
//        // [Start] Modify by LiXiaolong<mailTo: lxl@xdja.com> on 2016-08-16. Review by WangChao1.
//        moduleWriter.writeModule(fileDescriptor);
////        try {
////            moduleWriter.writeModule(fileDescriptor);
////        } catch (RemoteException e) {
////            e.printStackTrace();
////        }
//        // [End] Modify by LiXiaolong<mailTo: lxl@xdja.com> on 2016-08-16. Review by WangChao1.
//    }
//[E]removed by lixiaolong on 20161008.

    /**
     * 将Assets下的APK包复制到程序私有目录
     *
     * @param context  上下文
     * @param fileName 文件名
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void copyArchive(Context context, String fileName) throws Exception {
        //[S]modify by lixiaolong on 20161008.
        InputStream in = context.getResources().getAssets().open(fileName);
        File f = new File(context.getFilesDir() + "/" + fileName);
        if (f.exists()) {
            f.delete();
        }
        FileOutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        byte[] buffer = new byte[8192];
        int count;
        while ((count = in.read(buffer)) > 0) {
            out.write(buffer, 0, count);
        }
        in.close();
        out.close();
//        InputStream in;
//        FileOutputStream out;
//        try {
//            in = context.getResources().getAssets().open(fileName);
//            File f = new File(context.getFilesDir() + "/" + fileName);
//            if (f.exists()) {
//                f.delete();
//            }
//            out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
//            byte[] buffer = new byte[8192];
//            int count;
//            while ((count = in.read(buffer)) > 0) {
//                out.write(buffer, 0, count);
//            }
//        } catch (Exception ioe) {
//            ioe.printStackTrace();
//            return;
//        }
//
//        try {
//            in.close();
//            out.close();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
        //[E]modify by lixiaolong on 20161008.
    }
}
