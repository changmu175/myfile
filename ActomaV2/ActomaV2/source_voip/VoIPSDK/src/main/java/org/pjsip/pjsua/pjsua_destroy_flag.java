/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua;

public enum pjsua_destroy_flag {
  PJSUA_DESTROY_NO_RX_MSG(pjsuaJNI.PJSUA_DESTROY_NO_RX_MSG_get()),
  PJSUA_DESTROY_NO_TX_MSG(pjsuaJNI.PJSUA_DESTROY_NO_TX_MSG_get()),
  PJSUA_DESTROY_NO_NETWORK(pjsuaJNI.PJSUA_DESTROY_NO_NETWORK_get());

  public final int swigValue() {
    return swigValue;
  }

  public static pjsua_destroy_flag swigToEnum(int swigValue) {
    pjsua_destroy_flag[] swigValues = pjsua_destroy_flag.class.getEnumConstants();
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (pjsua_destroy_flag swigEnum : swigValues)
      if (swigEnum.swigValue == swigValue)
        return swigEnum;
    throw new IllegalArgumentException("No enum " + pjsua_destroy_flag.class + " with value " + swigValue);
  }

  @SuppressWarnings("unused")
  pjsua_destroy_flag() {
    this.swigValue = SwigNext.next++;
  }

  @SuppressWarnings("unused")
  pjsua_destroy_flag(int swigValue) {
    this.swigValue = swigValue;
    SwigNext.next = swigValue+1;
  }

  @SuppressWarnings("unused")
  pjsua_destroy_flag(pjsua_destroy_flag swigEnum) {
    this.swigValue = swigEnum.swigValue;
    SwigNext.next = this.swigValue+1;
  }

  private final int swigValue;

  private static class SwigNext {
    private static int next = 0;
  }
}

