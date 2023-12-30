package com.trulyao.spawn.views.components;

import org.kordamp.ikonli.ionicons4.Ionicons4IOS;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class IconButton extends Button {
	public IconButton(Ionicons4IOS iconName) {
		FontIcon icon = new FontIcon(iconName);
		icon.setIconSize(16);

		this.setGraphic(icon);
		this.setPrefHeight(30);
		this.setStyle("-fx-background-color: transparent;");
		this.setOnMouseEntered(e -> {
			this.setStyle("-fx-background-color: #e4e4e4;");
		});
		this.setOnMouseExited(e -> {
			this.setStyle("-fx-background-color: transparent;");
		});
	}

	public IconButton(Ionicons4IOS iconName, EventHandler<ActionEvent> handler, int size) {
		FontIcon icon = new FontIcon(iconName);
		icon.setIconSize(size);

		this.setGraphic(icon);
		this.setPrefHeight(30);
		this.setStyle("-fx-background-color: transparent;");
		this.setOnMouseEntered(e -> {
			this.setStyle("-fx-background-color: #e4e4e4;");
		});
		this.setOnMouseExited(e -> {
			this.setStyle("-fx-background-color: transparent;");
		});
		this.setOnAction(handler);
	}

	public IconButton(Ionicons4IOS iconName, EventHandler<ActionEvent> handler) {
		FontIcon icon = new FontIcon(iconName);
		icon.setIconSize(16);

		this.setGraphic(icon);
		this.setPrefHeight(30);
		this.setStyle("-fx-background-color: transparent;");
		this.setOnMouseEntered(e -> {
			this.setStyle("-fx-background-color: #e4e4e4;");
		});
		this.setOnMouseExited(e -> {
			this.setStyle("-fx-background-color: transparent;");
		});
		this.setOnAction(handler);
	}
}
