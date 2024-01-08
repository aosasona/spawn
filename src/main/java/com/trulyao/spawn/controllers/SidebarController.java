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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import com.trulyao.spawn.models.Document;
import com.trulyao.spawn.models.DocumentsContainer;
import com.trulyao.spawn.utils.AppConstants;
import com.trulyao.spawn.utils.Common;
import com.trulyao.spawn.utils.Logger;

public class SidebarController {
	private final Stage mainStage;
	private final MainController mainController;

	private DocumentsContainer documents;
	private ObservableList<Document> observableList;

	public SidebarController(Stage mainStage, MainController mainController) {
		this.mainStage = mainStage;
		this.mainController = mainController;
	}

	public ObservableList<Document> getObservableArraylist() {
		// Lazy load the documents the first time it is requested
		if (this.documents == null) {
			this.documents = this.getDocuments();
			Logger.getSharedInstance().debug("Loaded documents - first load, this log message should never show up again");
		}

		// Create a new observable list so that we can update the sidebar view in realtime
		if (this.observableList == null || this.observableList.isEmpty()) {
			this.observableList = FXCollections.observableArrayList(documents.getDocuments());
			Logger.getSharedInstance().debug("Loaded observable list - this should ideally happen only once");
		}

		return this.observableList;
	}

	public void handleException(Exception e) {
		ExceptionHandler.handle(this.mainStage, e);
	}

	public void handleNewFile(String title) throws AppException {
		String filename = Common.slugify(title) + ".md";
		String fullPath = AppConstants.getFullFilePath(filename);

		if (this.fileAlreadyExists(fullPath)) {
			throw new AppException("A file with that name already exists.");
		}

		this.createFileOnDisk(fullPath, title);
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
		Logger.getSharedInstance().debug("Reloaded documents.");
	}

	public EventHandler<ActionEvent> handleReload() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				reloadDocuments();

				// If the current document is not in the list of documents, clear the editor
				//
				// Since objects are compared by reference, it will always be false since we will have "different" objects in memory if we try to do a direct comparison
				// we need to compare the current document with the documents in the list by using the document name
				if (mainController.getCurrentDocument().isPresent()) {
					boolean documentStillExists = documents
					.getDocuments()
					.stream()
					.anyMatch(document -> document.getFileName().equals(mainController.getCurrentDocument().get().getFileName()));

					if (documentStillExists) { return; }

					Logger.getSharedInstance().debug("Current document no longer exists, clearing the editor state.");
					mainController.removeCurrentDocument();
				}
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
					TextField editor = dialog.getEditor();
					BooleanBinding disableButton = Bindings.createBooleanBinding(() -> editor.getText().trim().isEmpty(), editor.textProperty());
					okButton.disableProperty().bind(disableButton);

					// Handle user input
					Optional<String> value = dialog.showAndWait();
					if (value.isEmpty()) {
						return;
					}
					handleNewFile(value.get());
				} catch (Exception e) {
					handleException(e);
				}
			}
		};
	}

	public void renameDocument(Document document) {
		try {
			String newTitle = this.promptForNewTitle(document).orElse(null);
			if (newTitle == null) { return; }

			String newFileName = Common.slugify(newTitle) + ".md";
			if (this.fileAlreadyExists(Common.getFullDocumentPathFromFilename(newFileName))) {
				newFileName = Common.slugify(newTitle + " " + new Date().getTime()) + ".md";
				Logger.getSharedInstance().debug("File with that name already exists, appending timestamp to new filename. New filename: " + newFileName);
			}

			String fullFilePath = Common.getFullDocumentPathFromFilename(newFileName);
			this.createDataDirIfNotExists();

			// Create the new file
			File file = new File(fullFilePath);
			if (!file.createNewFile()) {
				Logger.getSharedInstance().error("Could not create the new file.");
				throw new AppException("Failed to rename the file.");
			}

			// Write the new file with the new title and the old body
			FileWriter writer = new FileWriter(file);
			String content = Document.makeMetaString(newTitle) + document.getBodyAsString();
			writer.write(content);
			writer.close();

			// Remove the old file
			if(!document.delete()) {
				Logger.getSharedInstance().error("Could not delete the old file.");
				throw new AppException("Failed to rename the file.");
			}

			// Handle the case where the renamed file was the current document
			Document newDocument = Document.toDocument(file.toPath());
			if (this.mainController.getCurrentDocument().isPresent() && this.mainController.getCurrentDocument().get().equals(document)) {
				Logger.getSharedInstance().debug("Renamed file was the current document, replacing it with the new document.");
				this.mainController.setCurrentDocument(newDocument);
			}

			this.reloadDocuments();
		} catch (Exception e) {
			handleException(e);
		}
	}

	public void openInFileManager(Document targetDocument) {
		try {
			if (targetDocument == null) { return; }
			String fullPath = targetDocument.getPath();
			String currentRuntime = System.getProperty("os.name").toLowerCase();

			HashMap<String, String> meta = new HashMap<>();
			meta.put("fileName", targetDocument.getFileName());
			meta.put("currentRuntime", currentRuntime);
			Logger.getSharedInstance().info("Opening file in default file manager.", meta);

			switch (Common.getOperatingSystem()) {
				case Common.OperatingSystem.MAC -> new ProcessBuilder("open", "-R", fullPath).start();
				case Common.OperatingSystem.WINDOWS -> new ProcessBuilder("explorer.exe", "/select,", fullPath).start();
				default -> new ProcessBuilder("xdg-open", fullPath).start();
			}
		} catch (Exception e) {
			handleException(e);
		}
	}

	public void deleteDocument(Document targetDocument) {
		try {
			if (targetDocument == null) { return; }

			Logger.getSharedInstance().warning("Requesting to delete file: " + targetDocument.getFileName());

			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Delete file");
			alert.setHeaderText("Confirm deletion");
			alert.setContentText("Are you sure you want to delete " + targetDocument.getTitle() + "?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				if (!targetDocument.delete()) {
					Logger.getSharedInstance().error("Failed to delete file: " + targetDocument.getFileName());
					return;
				}
				// If the deleted file was the current document, clear the editor
				if (this.mainController.getCurrentDocument().isPresent() && this.mainController.getCurrentDocument().get().equals(targetDocument)) {
					Logger.getSharedInstance().info("Deleted file was the current document, clearing editor.");
					this.mainController.removeCurrentDocument();
				}
				this.reloadDocuments();
			} else {
				return;
			}
		} catch (Exception e) {
			handleException(e);
		}
	}

	private Optional<String> promptForNewTitle(Document document) {
		TextInputDialog renameDocumentDialog = new TextInputDialog();
		renameDocumentDialog.setTitle("Rename file");
		renameDocumentDialog.setHeaderText("Rename " + document.getTitle().orElse("file"));
		renameDocumentDialog.setContentText("Enter a new name for the file:");

		TextField editor = renameDocumentDialog.getEditor();
		editor.setText(document.getTitle().orElse(document.getFileName()));

		// Disable button to enforce user input
		Button okButton = (Button) renameDocumentDialog.getDialogPane().lookupButton(ButtonType.OK);
		BooleanBinding disableButton = Bindings.createBooleanBinding(() -> editor.getText().trim().isEmpty(), editor.textProperty());
		okButton.disableProperty().bind(disableButton);

		// Handle user input
        return renameDocumentDialog.showAndWait();
	}

	private DocumentsContainer getDocuments() {
		try {
			return Document.getAll();
		} catch(IOException e) {
			HashMap<String, String> meta = new HashMap<>();
			meta.put("originalError", e.getMessage());

			Logger.getSharedInstance().fatal("Something went wrong while getting the documents.", meta);
			return new DocumentsContainer();
		}
	}


	private Boolean fileAlreadyExists(String fullPath) {
		File file = new File(fullPath);
		return file.exists();
	}

	private void createDataDirIfNotExists() {
		// check if the parent directory exists
		// if it doesn't, create it
		File baseDir = new File(AppConstants.getPath(AppConstants.PathKey.DATA_DIR));
		if (!baseDir.exists()) {
			if(!baseDir.mkdirs()) {
				Logger.getSharedInstance().fatal("Could not create the data directory.");
			}
		}
	}

	private void createFileOnDisk(String fullPath, String title) {
		try {
			this.createDataDirIfNotExists();

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
			HashMap<String, String> meta = new HashMap<>();
			meta.put("originalError", e.getMessage());
			Logger.getSharedInstance().fatal("Something went wrong while creating the file.", meta);
		}
	}

	private void writeDefaultFrontMatter(File file, String title) {
		String frontMatter = Document.makeMetaString(title);
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(frontMatter);
			writer.close();
		} catch (Exception e) {
			HashMap<String, String> meta = new HashMap<>();
			meta.put("originalError", e.getMessage());
			Logger.getSharedInstance().fatal("Something went wrong while writing the front matter.", meta);

			// delete the file if it was created
			if(file.exists()) {
                if(!file.delete()) {
                    Logger.getSharedInstance().warning("Failed to delete the file.");
                }
            }
		}
	}
}
