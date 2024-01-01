package com.trulyao.spawn.views;

import com.trulyao.spawn.controllers.DocumentController;
import com.trulyao.spawn.controllers.MainController;
import com.trulyao.spawn.views.components.Layout;

import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

public class MainView {
	private Stage mainStage;
	private MainController controller;

	public MainView(Stage mainStage) {
		this.mainStage = mainStage;
		this.controller = new MainController();
	}
	
	public SplitPane render() {
		var documentController = new DocumentController(this.controller);
		var documentView = new DocumentView(documentController);

		Layout layout = new Layout(mainStage, this.controller);
		return layout.render(documentView.render());
	}
}
