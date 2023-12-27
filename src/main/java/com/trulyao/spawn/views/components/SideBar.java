package com.trulyao.spawn.views.components;

import com.trulyao.spawn.utils.AppConstants;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

public class SideBar {
	private VBox pane;

	public SideBar() {
		this.pane = new VBox();
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

		header.getChildren().addAll(searchField, newFileButton);
		return header;
	}
}
