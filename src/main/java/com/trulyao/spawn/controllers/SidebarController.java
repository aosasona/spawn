package com.trulyao.spawn.controllers;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.trulyao.spawn.utils.exceptions.AppException;
import com.trulyao.spawn.utils.exceptions.ExceptionHandler;

import javafx.stage.Stage;

import com.trulyao.spawn.models.Document;
import com.trulyao.spawn.utils.AppConstants;
import com.trulyao.spawn.utils.Common;
import com.trulyao.spawn.utils.Logger;

public class SidebarController {
	private Stage mainStage;

	public SidebarController(Stage mainStage) {
		this.mainStage = mainStage;
	}

	public void handleException(Exception e) {
		ExceptionHandler.handle(this.mainStage, e);
	}

	public String handleNewFile(String name) throws AppException {
		String filename = Common.slugify(name) + ".md";
		String fullPath = AppConstants.makeFilename(filename);

		if (this.fileAlreadyExists(fullPath)) {
			throw new AppException("A file with that name already exists.");
		}

		this.createFileOnDisk(fullPath);
		return fullPath;
	}

	private Boolean fileAlreadyExists(String fullPath) {
		File file = new File(fullPath);
		return file.exists();
	}

	private void createFileOnDisk(String fullPath) {
		// check if the parent directory exists
		// if it doesn't, create it
		File baseDir = new File(AppConstants.getPath(AppConstants.PathKey.DATA_DIR));
		if (!baseDir.exists()) {
			if(!baseDir.mkdirs()) {
				Logger.getSharedInstance().fatal("Could not create the data directory.");
			}
		}

		File file = new File(fullPath);
		try {
			if (!file.createNewFile()) {
				Logger.getSharedInstance().fatal("Could not create the file.");
			}
			Document.getRecentDocuments();
		} catch (Exception e) {
			var meta = new HashMap<String, String>();
			meta.put("originalError", e.getMessage());
			Logger.getSharedInstance().fatal("Something went wrong while creating the file.", meta);
		}
	}
}
