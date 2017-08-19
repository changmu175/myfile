package com.xdja.domain_mainframe.xposeconfig;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.xdja.comm.uitl.UniversalUtil;
import com.xdja.dependence.uitls.LogUtil;

import java.io.FileDescriptor;

/**
 * Created by alexlee on 15-8-25.
 *
 */
public class ModuleWriterProxy implements IModuleWriter{
    static final String DESCRIPTOR = "de.robv.android.xposed.IXposedService";
    //add for xdjaposed. by gbc 2016-12-16
    static final String DESCRIPTOR_XDJA = "de.robv.android.xdjaposed.IXdjaposedService";

    private IBinder mRemote;

    public ModuleWriterProxy(IBinder mRemote) {
        this.mRemote = mRemote;
    }

//    public String getInterfaceDescriptor() {
//        return DESCRIPTOR;
//    }

/*    @Override
    public void writeModule(String apkpath) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();

        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeString(apkpath);
            mRemote.transact(TRANSACTION_writeModule, data, reply, 0);
            reply.readException();

        } finally {
            reply.recycle();
            data.recycle();
        }

    }*/

    @Override
    public IBinder asBinder() {
        return mRemote;
    }

    @Override
    public void writeModule(FileDescriptor fileDescriptor, String currentXposedName) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();

        try {
            //add for xdjaposed. by gbc 2016-12-16. begin
            if (UniversalUtil.XDJA_XPOSED_SERVICE_NAME.equalsIgnoreCase(currentXposedName)) {
                data.writeInterfaceToken(DESCRIPTOR_XDJA);
                data.writeFileDescriptor(fileDescriptor);
            } else if (UniversalUtil.XPOSED_SERVICE_NAME.equalsIgnoreCase(currentXposedName)) {
                data.writeInterfaceToken(DESCRIPTOR);
                data.writeFileDescriptor(fileDescriptor);
            } else {
                data.writeInterfaceToken(DESCRIPTOR);
                data.writeFileDescriptor(fileDescriptor);
            }
            //add for xdjaposed. by gbc 2016-12-16. end
            mRemote.transact(TRANSACTION_writeModule, data, reply, 0);
            reply.readException();
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    @Override
    public String getVersionCode(String currentXposedName) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();

        try {
            String ret;
            //add for xdjaposed. by gbc 2016-12-16. begin
            if (UniversalUtil.XDJA_XPOSED_SERVICE_NAME.equalsIgnoreCase(currentXposedName)) {
                data.writeInterfaceToken(DESCRIPTOR_XDJA);
            } else if (UniversalUtil.XPOSED_SERVICE_NAME.equalsIgnoreCase(currentXposedName)) {
                data.writeInterfaceToken(DESCRIPTOR);
            } else {
                data.writeInterfaceToken(DESCRIPTOR);
            }
            //add for xdjaposed. by gbc 2016-12-16. end
            mRemote.transact(TRANSACTION_getVersionCode, data, reply, 0);
            reply.readException();
            int err = reply.readInt();
            String errmsg = reply.readString();
            String versionCode = reply.readString();
            switch (err) {
                case 0:
                    ret = versionCode;
                    LogUtil.getUtils("ModuleWriterProxy").d("current actoma module version: " + ret);
                    break;
                default:
                    LogUtil.getUtils("ModuleWriterProxy").e("get version code fail | " + errmsg);
                    ret = null;
                    break;
            }
            return ret;
        } finally {
            reply.recycle();
            data.recycle();
        }

    }

}
