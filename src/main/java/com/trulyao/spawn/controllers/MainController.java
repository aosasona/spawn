package com.trulyao.spawn.controllers;

import java.util.Optional;

import com.trulyao.spawn.models.Document;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class MainController {
	// We want to be able to subscribe to changes in the current document, but since the Observable class has been deprecated,
	// we'll have to use ObservableList that only allows for one item
	private ObservableList<Document> documents;

	public MainController() {
		this.documents = FXCollections.observableArrayList();
	}

	public Optional<Document> getCurrentDocument() {
		if (this.documents.isEmpty()) { return Optional.empty(); }

		Document currentDocument = this.documents.get(0);
		if (currentDocument == null) { return Optional.empty(); }

		return Optional.of(currentDocument);
	}

	public void setCurrentDocument(Document source) {
		Document document = source.loadBody();
		if (this.documents.isEmpty()) {
			this.documents.add(document);
			return;
		}

		this.documents.set(0, document);
	}

	public void subscribe(ListChangeListener<Document> listener) {
		this.documents.addListener(listener);
	}
}
