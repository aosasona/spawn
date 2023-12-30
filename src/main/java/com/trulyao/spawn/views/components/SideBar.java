package com.trulyao.spawn.views.components;

import java.util.Date;
import java.util.Optional;

import com.trulyao.spawn.controllers.SidebarController;
import com.trulyao.spawn.models.Document;
import com.trulyao.spawn.models.DocumentsContainer;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class SideBar {
	private VBox pane;
	private TextInputDialog newFileDialog;

	// This is useful to keep track of the documents so we can search and filter and all without having to reach into the file system over and over
	private DocumentsContainer documentsContainer;

	private ObservableList<Document> documents;

	private final SidebarController controller;

	public SideBar(SidebarController sideBarController) {
		this.pane = new VBox();
		this.newFileDialog = new TextInputDialog();
		this.newFileDialog.setTitle("New file");
		this.newFileDialog.setHeaderText("Create a new file");
		this.controller = sideBarController;
	}

	public VBox getPane() {
		this.initialize();
		return this.pane;
	}

	private void initialize() {
		this.handleSizing();
		this.loadDocuments();

		HBox header = this.makeHeader();
		pane.getChildren().add(header);
	}

	private void loadDocuments() {
		try {
			this.documentsContainer = this.controller.getDocuments();
			this.documents = FXCollections.observableArrayList(this.documentsContainer.getDocuments());
		} catch (Exception e) {
			controller.handleException(e);
		}
	}

	private void handleSizing() {
		pane.setMinWidth(256);
		pane.setMaxWidth(352);
		pane.setPrefWidth(256);
		pane.setStyle("-fx-border-color: #e4e4e4;");
	}

	private HBox makeHeader() {
		HBox header = new HBox();
		header.setMaxHeight(30);
		header.setSpacing(7.5);
		header.setPadding(new Insets(10, 10, 10, 10));

		TextField searchField = new TextField();
		searchField.setPromptText("Search");
		searchField.setPrefHeight(30);

		Button newFileButton = new Button("New file");
		newFileButton.setPrefHeight(30);
		newFileButton.setOnAction(this.handleNewFile());

		header.getChildren().addAll(searchField, newFileButton);
		return header;
	}

	private ScrollPane makeFileList() {
		ScrollPane fileList = new ScrollPane();
		fileList.setFitToWidth(true);
		fileList.setFitToHeight(true);
		return fileList;
	}

	private EventHandler<ActionEvent> handleNewFile() {
		var dialog = this.newFileDialog;
		var controller = this.controller;

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
					controller.handleNewFile(value.get());
				} catch (Exception e) {
					controller.handleException(e);
				}
			}
		};
	}
}
