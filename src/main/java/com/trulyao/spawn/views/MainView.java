package com.trulyao.spawn.views;

import com.trulyao.spawn.controllers.DocumentController;
import com.trulyao.spawn.controllers.MainController;
import com.trulyao.spawn.views.components.Layout;

import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

public class MainView {
	private final Stage mainStage;
	private final MainController controller = new MainController();

	public MainView(Stage mainStage) {
		this.mainStage = mainStage;
	}

	// Construct the main view and its dependencies
	public SplitPane buildView() {
		DocumentController documentController = new DocumentController(this.controller);
		DocumentView documentView = new DocumentView(documentController);

		Layout layout = new Layout(mainStage, this.controller);
		return layout.buildView(documentView.buildView());
	}
}
