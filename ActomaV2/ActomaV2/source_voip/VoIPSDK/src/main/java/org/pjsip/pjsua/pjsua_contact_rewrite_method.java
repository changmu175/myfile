/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua;

public enum pjsua_contact_rewrite_method {
  PJSUA_CONTACT_REWRITE_UNREGISTER(pjsuaJNI.PJSUA_CONTACT_REWRITE_UNREGISTER_get()),
  PJSUA_CONTACT_REWRITE_NO_UNREG(pjsuaJNI.PJSUA_CONTACT_REWRITE_NO_UNREG_get()),
  PJSUA_CONTACT_REWRITE_ALWAYS_UPDATE(pjsuaJNI.PJSUA_CONTACT_REWRITE_ALWAYS_UPDATE_get());

  public final int swigValue() {
    return swigValue;
  }

  public static pjsua_contact_rewrite_method swigToEnum(int swigValue) {
    pjsua_contact_rewrite_method[] swigValues = pjsua_contact_rewrite_method.class.getEnumConstants();
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (pjsua_contact_rewrite_method swigEnum : swigValues)
      if (swigEnum.swigValue == swigValue)
        return swigEnum;
    throw new IllegalArgumentException("No enum " + pjsua_contact_rewrite_method.class + " with value " + swigValue);
  }

  @SuppressWarnings("unused")
  pjsua_contact_rewrite_method() {
    this.swigValue = SwigNext.next++;
  }

  @SuppressWarnings("unused")
  pjsua_contact_rewrite_method(int swigValue) {
    this.swigValue = swigValue;
    SwigNext.next = swigValue+1;
  }

  @SuppressWarnings("unused")
  pjsua_contact_rewrite_method(pjsua_contact_rewrite_method swigEnum) {
    this.swigValue = swigEnum.swigValue;
    SwigNext.next = this.swigValue+1;
  }

  private final int swigValue;

  private static class SwigNext {
    private static int next = 0;
  }
}

