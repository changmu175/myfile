package com.xdja.domain_mainframe.xposeconfig;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import java.io.FileDescriptor;

/**
 * Created by alexlee on 15-8-25.
 */
public interface IModuleWriter extends IInterface {
    /*static final String DESCRIPTOR = "de.robv.android.xposed.IXposedService";
    //add for xdjaposed. by gbc 2016-12-16
    static final String DESCRIPTOR_XDJA = "de.robv.android.xdjaposed.IXdjaposedService";*/

    //void writeModule(String apkpath ) throws RemoteException;
    void writeModule(FileDescriptor fileDescriptor, String currentXposedName) throws RemoteException;
    String getVersionCode(String currentXposedName) throws RemoteException;


    static final int TRANSACTION_writeModule = IBinder.FIRST_CALL_TRANSACTION + 5;
    static final int TRANSACTION_getVersionCode = IBinder.FIRST_CALL_TRANSACTION + 6;

}
