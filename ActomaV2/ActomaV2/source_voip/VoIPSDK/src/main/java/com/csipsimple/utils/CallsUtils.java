package com.csipsimple.utils;

import com.csipsimple.api.SipUri;

public class CallsUtils {

	public static String parseAccountFromNumUrl(String numUrl) {
		return SipUri.parseSipContact(numUrl).userName;
	}

	public static String parseAccountFromSipCallSession(SipUri.ParsedSipContactInfos psci) {
		return psci.userName;
	}

}
