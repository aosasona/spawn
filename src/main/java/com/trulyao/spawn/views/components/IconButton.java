package com.trulyao.spawn.views.components;

import org.kordamp.ikonli.ionicons4.Ionicons4IOS;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class IconButton extends Button {
	private int DEFAULT_ICON_SIZE = 16;
	private int DEFAULT_BUTTON_HEIGHT = 30;

	public IconButton(Ionicons4IOS iconName) {
		FontIcon icon = new FontIcon(iconName);
		icon.setIconSize(DEFAULT_ICON_SIZE);

		this.setDefaults(icon);
	}

	public IconButton(Ionicons4IOS iconName, EventHandler<ActionEvent> handler, int size) {
		FontIcon icon = new FontIcon(iconName);
		icon.setIconSize(size);

		this.setDefaults(icon);
		this.setOnAction(handler);
	}

	public IconButton(Ionicons4IOS iconName, EventHandler<ActionEvent> handler) {
		FontIcon icon = new FontIcon(iconName);
		icon.setIconSize(DEFAULT_ICON_SIZE);

		this.setDefaults(icon);
		this.setOnAction(handler);
	}

	private void setDefaults(FontIcon icon) {
		this.setGraphic(icon);
		this.setPrefHeight(DEFAULT_BUTTON_HEIGHT);
		this.setStyle("-fx-background-color: transparent;");
		this.setOnMouseEntered(e -> this.setStyle("-fx-background-color: #e4e4e4;"));
		this.setOnMouseExited(e -> this.setStyle("-fx-background-color: transparent;"));
	}
}
