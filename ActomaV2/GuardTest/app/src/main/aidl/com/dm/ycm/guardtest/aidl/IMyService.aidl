// IMyService.aidl
package com.dm.ycm.guardtest.aidl;

// Declare any non-default types here with import statements

interface IMyService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int sendMSG(String topic,String message);
    void isRunningNPCPackage(String packageName);
}
