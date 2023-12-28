package com.trulyao.spawn.utils;

public class Common {
	public static String getHomeDir() {
		return System.getProperty("user.home");
	}

	public static String slugify(String text) {
		String result = text
		.trim()
		.toLowerCase()
		.replaceAll("[^a-z0-9\\s]", "")
		.replaceAll("\\s+", "-");

		return result;
	}
}
