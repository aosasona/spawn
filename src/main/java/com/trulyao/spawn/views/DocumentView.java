package com.trulyao.spawn.views;

import java.util.Optional;

import com.trulyao.spawn.models.Document;

public class DocumentView {
	private Optional<Document> currentDocument;

	public DocumentView(Document document) {
		this.currentDocument = Optional.of(document);
	}

	public DocumentView() {
		this.currentDocument = Optional.empty();
	}
}
