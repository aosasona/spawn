package com.trulyao.spawn.views.components;

import java.util.Date;
import java.util.Optional;

import com.trulyao.spawn.controllers.SidebarController;
import com.trulyao.spawn.utils.AppConstants;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class SideBar {
	private VBox pane;
	private TextInputDialog newFileDialog;

	private final SidebarController controller;

	public SideBar(SidebarController controller) {
		this.pane = new VBox();
		this.newFileDialog = new TextInputDialog();
		this.newFileDialog.setTitle("New file");
		this.newFileDialog.setHeaderText("Create a new file");
		this.controller = controller;
	}

	public VBox getPane() {
		this.initialize();
		return this.pane;
	}

	private void initialize() {
		this.handleSizing();

		HBox header = this.makeHeader();
		pane.getChildren().add(header);
	}

	private void handleSizing() {
		final double width = AppConstants.WIDTH * 0.2;
		pane.setMinSize(width, AppConstants.HEIGHT);
		pane.setMaxSize(width, AppConstants.HEIGHT);
		pane.setPrefSize(width, AppConstants.HEIGHT);
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
					controller.handleNewFile(value.get());
				} catch (Exception e) {
					controller.handleException(e);
				}
			}
		};
	}
}
