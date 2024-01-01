package com.trulyao.spawn.views.components;

import com.trulyao.spawn.controllers.MainController;
import com.trulyao.spawn.controllers.SidebarController;

import javafx.stage.Stage;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;

public class Layout {
	private SplitPane layout;
	private Stage mainStage;

	private final MainController mainController;

	public Layout(Stage mainStage, MainController mainController) {
		this.layout = new SplitPane();
		this.mainStage = mainStage;
		this.mainController = mainController;
	}

	public SplitPane render(Pane child) {
		layout
		.getItems()
		.addAll(this.makeSidebar(), this.makeMainArea(child));

		layout.setDividerPositions(0.2f, 0.8f);
		return layout;
	}

	private Pane makeSidebar() {
		SidebarController sidebarController = new SidebarController(mainStage);
		Pane sidebar = new SideBar(sidebarController, this.mainController).getPane();

		return sidebar;
	}

	private VBox makeMainArea(Pane child) {
		VBox mainArea = new VBox();
		mainArea.getChildren().addAll(this.makeHeader(), child);
		return mainArea;
	}

	private VBox makeHeader() {
		VBox header = new VBox();
		header.setMaxHeight(30);
		header.setSpacing(7.5);

		return header;
	}
}
