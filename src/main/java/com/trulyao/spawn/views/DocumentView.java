package com.trulyao.spawn.views;

import org.kordamp.ikonli.ionicons4.Ionicons4IOS;

import com.trulyao.spawn.controllers.DocumentController;
import com.trulyao.spawn.controllers.DocumentController.DocumentHotReloader;
import com.trulyao.spawn.utils.Logger;
import com.trulyao.spawn.views.components.IconButton;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.scene.text.TextAlignment;

public class DocumentView {
	private DocumentController controller;

	private VBox pane;

	private Boolean isPreviewMode = false;
	private final int HEADER_HEIGHT = 55;
	private final String WEBVIEW_CSS = """
		body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; line-height: 1.42857143;}
		blockquote { color: #1a1a1a; padding: 6px 8px; margin: 0 0 20px; font-size: 14px; border-left: 4px solid #eee; background-color: #f5f5f5; }
		""";

	public DocumentView(DocumentController documentController) {
		this.controller = documentController;
		this.controller.subscribe(this.hotReload());
	}

	public VBox render() {
		if (this.controller.getMainController().getCurrentDocument().isEmpty()) {
			this.pane = DocumentView.makeEmptyDocumentView();
			return this.pane;
		}

		this.loadDocumentIntoView();
		return this.pane;
	}

	private void loadDocumentIntoView() {
		this.pane = new VBox();
		this.pane.getChildren().addAll(this.makeHeader(), this.makeEditorArea());
	}

	private VBox makeEditorArea() {
		VBox editorArea = new VBox();
		VBox.setVgrow(editorArea, Priority.ALWAYS);

		if (this.isPreviewMode) {
			controller.reloadHtmlBody();

			WebView webView = new WebView();
			webView.setContextMenuEnabled(false);
			VBox.setVgrow(webView, Priority.ALWAYS);
			HBox.setHgrow(webView, Priority.ALWAYS);

			WebEngine webEngine = webView.getEngine();
			var optContent = this.controller.getMainController().getCurrentDocument().get().getHtmlContent();
			String content = optContent.isPresent() ? optContent.get() : "";
			webEngine.loadContent(content);
			webEngine.setUserStyleSheetLocation("data:text/css," + WEBVIEW_CSS);
			editorArea.getChildren().addAll(webView);
		} else {
			TextArea editor = new TextArea();
			VBox.setVgrow(editor, Priority.ALWAYS);
			HBox.setHgrow(editor, Priority.ALWAYS);

			editor.setWrapText(true);
			String content = "";
			if (this.controller.getMainController().getCurrentDocument().isPresent()) {
				content = this.controller.getMainController().getCurrentDocument().get().getBodyAsString();
			}
			editor.setText(content);
			editor.textProperty().addListener((observable, oldValue, newValue) -> {
				if (this.controller.getMainController().getCurrentDocument().isPresent()) {
					this.controller.getMainController().getCurrentDocument().get().setBody(newValue);
				}
			});
			editorArea.getChildren().addAll(editor);
		}

		return editorArea;
	}

	private HBox makeHeader() {
		HBox header = new HBox();
		HBox.setHgrow(header, Priority.ALWAYS);
		header.setPrefHeight(HEADER_HEIGHT);
		header.setSpacing(7.5);
		header.setStyle("-fx-border-color: #e4e4e4;");
		header.setPadding(new Insets(10, 12, 10, 12));
		// justify to the end i.e place all items on the right
		header.setAlignment(Pos.CENTER_RIGHT);

		IconButton previewButton = new IconButton(this.isPreviewMode ? Ionicons4IOS.CODE : Ionicons4IOS.EYE);
		previewButton.setTooltip(new Tooltip("Toggle preview mode"));
		previewButton.setOnAction((event) -> {
			this.isPreviewMode = !this.isPreviewMode;
			this.hotReload().reload();
		});

		IconButton saveButton = new IconButton(Ionicons4IOS.SAVE);
		saveButton.setTooltip(new Tooltip("Save document"));
		saveButton.setOnAction(this.controller.handleSave());

		header.getChildren().addAll(previewButton, saveButton);

		return header;
	}

	private static VBox makeEmptyDocumentView() {
		VBox documentView = new VBox();
		documentView.setAlignment(Pos.CENTER);
		VBox.setVgrow(documentView, Priority.ALWAYS);

		Text emptyDocumentText = new Text("No document selected");
		emptyDocumentText.setTextAlignment(TextAlignment.CENTER);
		emptyDocumentText.setStyle("-fx-fill: #4a4a4a;");
		emptyDocumentText.setWrappingWidth(300);
		VBox.setVgrow(emptyDocumentText, Priority.ALWAYS);
		HBox.setHgrow(emptyDocumentText, Priority.ALWAYS);

		documentView.getChildren().addAll(emptyDocumentText);

		return documentView;
	}

	private DocumentHotReloader hotReload() {
		return new DocumentHotReloader() {
			@Override
			public void reload() {
				Logger.getSharedInstance().debug("Reloading document view");
				pane.getChildren().setAll(makeHeader(), makeEditorArea());
			}
		};
	}

}
