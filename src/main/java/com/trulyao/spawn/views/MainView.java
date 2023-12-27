package com.trulyao.spawn.views;

import com.trulyao.spawn.views.components.Layout;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class MainView {
	private HBox pane;

	public MainView() {
		this.pane = new HBox();
	}
	
	public AnchorPane render() {
		var text = new Text("Hello, world!");
		pane.getChildren().add(text);

		Layout layout = new Layout();
		return layout.render(pane);
	}
}
