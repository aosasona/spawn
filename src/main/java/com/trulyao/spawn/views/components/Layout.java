package com.trulyao.spawn.views.components;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import com.trulyao.spawn.controllers.SidebarController;

import javafx.scene.layout.*;

public class Layout {
	private AnchorPane layout;
	private Stage mainStage;

	public Layout(Stage mainStage) {
		this.layout = new AnchorPane();
		this.mainStage = mainStage;
	}

	public AnchorPane render(Pane child) {
		HBox mainArea = new HBox();
		var sidebarController = new SidebarController(mainStage);
		mainArea.getChildren().add(new SideBar(sidebarController).getPane());
		mainArea.getChildren().add(child);

		var v = new VBox();
		v.getChildren().add(mainArea);;

		layout.getChildren().add(v);
		return layout;
	}
}
