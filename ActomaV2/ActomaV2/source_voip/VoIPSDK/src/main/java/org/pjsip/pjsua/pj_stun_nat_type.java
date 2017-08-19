/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua;

public enum pj_stun_nat_type {
  PJ_STUN_NAT_TYPE_UNKNOWN,
  PJ_STUN_NAT_TYPE_ERR_UNKNOWN,
  PJ_STUN_NAT_TYPE_OPEN,
  PJ_STUN_NAT_TYPE_BLOCKED,
  PJ_STUN_NAT_TYPE_SYMMETRIC_UDP,
  PJ_STUN_NAT_TYPE_FULL_CONE,
  PJ_STUN_NAT_TYPE_SYMMETRIC,
  PJ_STUN_NAT_TYPE_RESTRICTED,
  PJ_STUN_NAT_TYPE_PORT_RESTRICTED;

  public final int swigValue() {
    return swigValue;
  }

  public static pj_stun_nat_type swigToEnum(int swigValue) {
    pj_stun_nat_type[] swigValues = pj_stun_nat_type.class.getEnumConstants();
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (pj_stun_nat_type swigEnum : swigValues)
      if (swigEnum.swigValue == swigValue)
        return swigEnum;
    throw new IllegalArgumentException("No enum " + pj_stun_nat_type.class + " with value " + swigValue);
  }

  @SuppressWarnings("unused")
  pj_stun_nat_type() {
    this.swigValue = SwigNext.next++;
  }

  @SuppressWarnings("unused")
  pj_stun_nat_type(int swigValue) {
    this.swigValue = swigValue;
    SwigNext.next = swigValue+1;
  }

  @SuppressWarnings("unused")
  pj_stun_nat_type(pj_stun_nat_type swigEnum) {
    this.swigValue = swigEnum.swigValue;
    SwigNext.next = this.swigValue+1;
  }

  private final int swigValue;

  private static class SwigNext {
    private static int next = 0;
  }
}
