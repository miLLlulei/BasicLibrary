package com.mill.utils.sdcard;

import java.util.Locale;

public class MimeUtils {

	public static final String APK_MIMETPYE_PREFIX = "application/vnd.android.package-archive";

	public static String normalizeMimeType(String type) {
		if (type == null) {
			return null;
		}

		type = type.trim().toLowerCase(Locale.ROOT);

		final int semicolonIndex = type.indexOf(';');
		if (semicolonIndex != -1) {
			type = type.substring(0, semicolonIndex);
		}
		return type;
	}
}
