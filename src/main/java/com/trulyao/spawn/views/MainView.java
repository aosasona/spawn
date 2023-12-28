package com.trulyao.spawn.views;

import com.trulyao.spawn.views.components.Layout;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainView {
	private HBox pane;
	private Stage mainStage;

	public MainView(Stage mainStage) {
		this.pane = new HBox();
		this.mainStage = mainStage;
	}
	
	public AnchorPane render() {
		var text = new Text("Hello, world!");
		pane.getChildren().add(text);

		Layout layout = new Layout(mainStage);
		return layout.render(pane);
	}
}
