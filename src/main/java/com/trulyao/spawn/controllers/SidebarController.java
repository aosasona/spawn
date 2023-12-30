package com.trulyao.spawn.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.trulyao.spawn.utils.exceptions.AppException;
import com.trulyao.spawn.utils.exceptions.ExceptionHandler;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import com.trulyao.spawn.models.Document;
import com.trulyao.spawn.models.DocumentsContainer;
import com.trulyao.spawn.utils.AppConstants;
import com.trulyao.spawn.utils.Common;
import com.trulyao.spawn.utils.Logger;

public class SidebarController {
	private Stage mainStage;
	private DocumentsContainer documents;
	private ObservableList<Document> observableList;

	public SidebarController(Stage mainStage) {
		this.mainStage = mainStage;
	}

	public ObservableList<Document> getObservableArraylist() {
		// Lazy load the documents the first time it is requested
		if (this.documents == null) {
			this.documents = this.getDocuments();
			Logger.getSharedInstance().debug("Loaded documents - first load");
		}

		if (this.observableList == null || this.observableList.isEmpty()) {
			this.observableList = FXCollections.observableArrayList(documents.getDocuments());
			Logger.getSharedInstance().debug("Loaded observable list - this should ideally happen only once");
		}
		return this.observableList;
	}

	public void handleException(Exception e) {
		ExceptionHandler.handle(this.mainStage, e);
	}

	public String handleNewFile(String title) throws AppException {
		String filename = Common.slugify(title) + ".md";
		String fullPath = AppConstants.makeFilename(filename);

		if (this.fileAlreadyExists(fullPath)) {
			throw new AppException("A file with that name already exists.");
		}

		this.createFileOnDisk(fullPath, title);
		return fullPath;
	}

	public void handleSearch(String query) {
		List<Document> result = documents.search(query);

		// If the query is blank, show all documents instead
		if(query.isBlank()) {
			this.observableList.setAll(documents.getDocuments());
			return;
		}
		this.observableList.setAll(result);
	}

	private void reloadDocuments() {
		this.documents = this.getDocuments();
		this.observableList.setAll(documents.getDocuments());
	}

	public EventHandler<ActionEvent> handleReload() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				reloadDocuments();
			}
		};
	}

	public EventHandler<ActionEvent> handleCreateNewFile(TextInputDialog dialog) {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					String defaultFileName = "Untitled " + new Date().toString();
					dialog.setContentText("Enter a name for the new file:");
					dialog.getEditor().setText(defaultFileName);

					// Disable button to enforce user input
					Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
					var editor = dialog.getEditor();
					BooleanBinding disableButton = Bindings.createBooleanBinding(() -> editor.getText().trim().isEmpty(), editor.textProperty());
					okButton.disableProperty().bind(disableButton);

					// Handle user input
					Optional<String> value = dialog.showAndWait();
					if (!value.isPresent()) {
						return;
					}
					handleNewFile(value.get());
				} catch (Exception e) {
					handleException(e);
				}
			}
		};
	}

	private DocumentsContainer getDocuments() {
		try {
			return Document.getAll();
		} catch(IOException e) {
			var meta = new HashMap<String, String>();
			meta.put("originalError", e.getMessage());

			Logger.getSharedInstance().fatal("Something went wrong while getting the documents.", meta);
			return new DocumentsContainer();
		}
	}


	private Boolean fileAlreadyExists(String fullPath) {
		File file = new File(fullPath);
		return file.exists();
	}

	private void createFileOnDisk(String fullPath, String title) {
		try {
			// check if the parent directory exists
			// if it doesn't, create it
			File baseDir = new File(AppConstants.getPath(AppConstants.PathKey.DATA_DIR));
			if (!baseDir.exists()) {
				if(!baseDir.mkdirs()) {
					Logger.getSharedInstance().fatal("Could not create the data directory.");
				}
			}

			File file = new File(fullPath);
			if (!file.createNewFile()) {
				Logger.getSharedInstance().fatal("Could not create the file.");
			}
			this.writeDefaultFrontMatter(file, title);

			// Append the new file to the observable list and lazily reload the document container
			// We could also just reload the document container, but that would be less efficient
			Document document = Document.toDocument(file.toPath());
			this.documents.append(document);
			this.documents.toSortedList();
			this.observableList.setAll(documents.getDocuments());
		} catch (Exception e) {
			var meta = new HashMap<String, String>();
			meta.put("originalError", e.getMessage());
			Logger.getSharedInstance().fatal("Something went wrong while creating the file.", meta);
		}
	}

	private void writeDefaultFrontMatter(File file, String title) {
		var frontMatter = "---\ntitle: " + title + "\n---\n";
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(frontMatter);
			writer.close();
		} catch (Exception e) {
			var meta = new HashMap<String, String>();
			meta.put("originalError", e.getMessage());
			Logger.getSharedInstance().fatal("Something went wrong while writing the front matter.", meta);

			// delete the file if it was created
			if(file.exists()) { file.delete(); }
		}
	}
}
