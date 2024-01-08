package com.trulyao.spawn.views.components;

import com.trulyao.spawn.controllers.MainController;
import com.trulyao.spawn.controllers.SidebarController;

import javafx.stage.Stage;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;

public class Layout {
	private final SplitPane layout;
	private final Stage mainStage;

	private final MainController mainController;

	public Layout(Stage mainStage, MainController mainController) {
		this.layout = new SplitPane();
		this.mainStage = mainStage;
		this.mainController = mainController;
	}

	// Construct the default layout (split pane and sidebar) and render the child view in the right-hand pane
	public SplitPane buildView(Pane child) {
		layout.getItems().addAll(this.makeSidebar(), this.makeMainArea(child));
		layout.setDividerPositions(0.2f, 0.8f);
		return layout;
	}

	private Pane makeSidebar() {
		SidebarController sidebarController = new SidebarController(mainStage, this.mainController);
        return new SideBar(sidebarController, this.mainController).buildView();
	}

	private VBox makeMainArea(Pane child) {
		VBox mainArea = new VBox();
		mainArea.getChildren().add(child);
		return mainArea;
	}
}
