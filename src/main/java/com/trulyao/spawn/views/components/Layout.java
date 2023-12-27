package com.trulyao.spawn.views.components;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.*;

public class Layout {
	private AnchorPane layout;

	public Layout() {
		this.layout = new AnchorPane();
	}

	public AnchorPane render(Pane child) {
		HBox mainArea = new HBox();
		mainArea.getChildren().add(new SideBar().getPane());
		mainArea.getChildren().add(child);

		var v = new VBox();
		v.getChildren().add(mainArea);;

		layout.getChildren().add(v);
		return layout;
	}
}
