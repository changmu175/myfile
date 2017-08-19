// IActomaAidlInterface.aidl
package com.xdja.comm.ckms;

// Declare any non-default types here with import statements

interface IActomaAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     *  void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                   double aDouble, String aString);
     */
      /*parameter——
        map: secKey,need decrypt string;
        return——
         decrypt bytes;
       */
      byte[] decryptSecKey (in Map map);
}
