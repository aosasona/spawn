package com.trulyao.spawn.models;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import com.trulyao.spawn.utils.AppConstants;

public final class Document {
	private String name;
	private Optional<String> title;
	private String path;

	public Document(String name, String path) {
		this.name = name;
		this.path = path;
	}

	public String getName() {
		return this.name;
	}

	public String getPath() {
		return this.path;
	}

	public Optional<String> getTitle() {
		return this.title;
	}

	public static Document[] getRecentDocuments() throws IOException {
		Document[] recentDocuments = new Document[10];
		String dataDir = AppConstants.getPath(AppConstants.PathKey.DATA_DIR);
		Files.walk(Paths.get(dataDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".md")) {
				System.out.println(filePath);
			}
		});

		return recentDocuments;
	}
}
