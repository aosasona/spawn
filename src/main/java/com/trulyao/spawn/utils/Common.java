package com.trulyao.spawn.utils;

public class Common {
	public enum OperatingSystem {
		WINDOWS,
		MAC,
		LINUX,
		OTHER
	}

    public static String getFullDocumentPathFromFilename(String filename) {
		return AppConstants.getPath(AppConstants.PathKey.DATA_DIR)	+ "/" + filename;
	}

	public static String slugify(String text) {
        return text
		.trim()
		.toLowerCase()
		.replaceAll("[^a-z0-9\\s]", "")
		.replaceAll("\\s+", "-");
	}

	public static OperatingSystem getOperatingSystem() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("windows")) {
			return OperatingSystem.WINDOWS;
		} else if (osName.contains("mac")) {
			return OperatingSystem.MAC;
		} else if (osName.contains("linux")) {
			return OperatingSystem.LINUX;
		} else {
			return OperatingSystem.OTHER;
		}
	}
}
