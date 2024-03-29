package com.trulyao.spawn.views.components;


import org.kordamp.ikonli.ionicons4.Ionicons4IOS;

import com.trulyao.spawn.controllers.MainController;
import com.trulyao.spawn.controllers.SidebarController;
import com.trulyao.spawn.models.Document;
import com.trulyao.spawn.utils.Common;
import com.trulyao.spawn.utils.Logger;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Callback;

public class SideBar {
	private final VBox pane;
	private TextInputDialog newFileDialog;
	private final ListView<Document> fileList;

	private final MainController mainController;
	private final SidebarController controller;

	public SideBar(SidebarController sideBarController, MainController mainController) {
		this.pane = new VBox();
		this.controller = sideBarController;
		this.mainController = mainController;
		this.makeNewFileDialog();
		this.fileList = new ListView<>(controller.getObservableArraylist());
	}

	public VBox buildView() {
		this.initialize();
		return this.pane;
	}

	private void initialize() {
		this.handleSizing();

		pane.getChildren().addAll(this.makeHeader(), this.makeFileList());
	}

	private void handleSizing() {
		pane.setMinWidth(256);
		pane.setMaxWidth(352);
		pane.setPrefWidth(256);
		pane.setStyle("-fx-border-color: #e4e4e4;");

		VBox.setVgrow(fileList, Priority.ALWAYS);
	}

	private void makeNewFileDialog() {
		this.newFileDialog = new TextInputDialog();
		this.newFileDialog.setTitle("New file");
		this.newFileDialog.setHeaderText("Create a new file");
	}

	private ContextMenu makeFileListContextMenu() {
		ContextMenu contextMenu = new ContextMenu();

		MenuItem renameMenuItem = new MenuItem("Rename");
		renameMenuItem.setOnAction(event -> controller.renameDocument(fileList.getSelectionModel().getSelectedItem()));
		renameMenuItem.disableProperty().bind(Bindings.isEmpty(fileList.getSelectionModel().getSelectedItems()));

		MenuItem deleteMenuItem = new MenuItem("Delete");
		deleteMenuItem.setOnAction(event -> controller.deleteDocument(fileList.getSelectionModel().getSelectedItem()));
		deleteMenuItem.disableProperty().bind(Bindings.isEmpty(fileList.getSelectionModel().getSelectedItems()));

		boolean isMacOS = Common.getOperatingSystem() == Common.OperatingSystem.MAC;
		MenuItem openInFinder = new MenuItem("Open in " + (isMacOS ? "Finder" : "Explorer"));
		openInFinder.setOnAction(event -> controller.openInFileManager(fileList.getSelectionModel().getSelectedItem()));
		openInFinder.disableProperty().bind(Bindings.isEmpty(fileList.getSelectionModel().getSelectedItems()));

		contextMenu.getItems().addAll(renameMenuItem, deleteMenuItem, openInFinder);
		return contextMenu;
	}


	private ListView<Document> makeFileList() {
		this.fileList.setContextMenu(this.makeFileListContextMenu());

		this.fileList.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Document> call(ListView<Document> list) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Document document, boolean empty) {
                        super.updateItem(document, empty);
                        if (document == null || empty) {
                            setText(null);
                        } else {
                            setText(document.getTitle().orElse(""));
                        }
                    }
                };
            }
        });

		this.fileList.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				Document selectedDocument = fileList.getSelectionModel().getSelectedItem();
				this.mainController.setCurrentDocument(selectedDocument);
			}
		});

		return this.fileList;
	}

	private HBox makeHeader() {
		HBox header = new HBox();
		header.setMaxHeight(30);
		header.setSpacing(7.5);
		header.setPadding(new Insets(10, 10, 10, 10));

		TextField searchField = new TextField();
		searchField.setPromptText("Search");
		searchField.setPrefHeight(30);
		searchField.textProperty().addListener((observable, oldValue, newValue) -> controller.handleSearch(newValue)); // Handle change events in the search field for a responsive search
		HBox.setHgrow(searchField, Priority.ALWAYS); // This is needed to make sure that the search field grows or shrinks as the pane is resized

		IconButton newFileButton = new IconButton(Ionicons4IOS.ADD, controller.handleCreateNewFile(this.newFileDialog), 22);
		newFileButton.setTooltip(new Tooltip("Create a new file"));
		Logger.getSharedInstance().info("New file button created");

		IconButton reloadButton = new IconButton(Ionicons4IOS.REFRESH, controller.handleReload());
		reloadButton.setTooltip(new Tooltip("Reload documents"));
		Logger.getSharedInstance().info("Reload button created");

		header.getChildren().addAll(searchField, reloadButton, newFileButton);
		Logger.getSharedInstance().info("All items added to header");

		return header;
	}

}
