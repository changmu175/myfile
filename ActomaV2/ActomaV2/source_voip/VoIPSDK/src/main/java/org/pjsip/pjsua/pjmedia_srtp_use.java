/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua;

public enum pjmedia_srtp_use {
  PJMEDIA_SRTP_DISABLED,
  PJMEDIA_SRTP_OPTIONAL,
  PJMEDIA_SRTP_MANDATORY;

  public final int swigValue() {
    return swigValue;
  }

  public static pjmedia_srtp_use swigToEnum(int swigValue) {
    pjmedia_srtp_use[] swigValues = pjmedia_srtp_use.class.getEnumConstants();
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (pjmedia_srtp_use swigEnum : swigValues)
      if (swigEnum.swigValue == swigValue)
        return swigEnum;
    throw new IllegalArgumentException("No enum " + pjmedia_srtp_use.class + " with value " + swigValue);
  }

  @SuppressWarnings("unused")
  pjmedia_srtp_use() {
    this.swigValue = SwigNext.next++;
  }

  @SuppressWarnings("unused")
  pjmedia_srtp_use(int swigValue) {
    this.swigValue = swigValue;
    SwigNext.next = swigValue+1;
  }

  @SuppressWarnings("unused")
  pjmedia_srtp_use(pjmedia_srtp_use swigEnum) {
    this.swigValue = swigEnum.swigValue;
    SwigNext.next = this.swigValue+1;
  }

  private final int swigValue;

  private static class SwigNext {
    private static int next = 0;
  }
}

