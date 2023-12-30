package com.trulyao.spawn.models;

import java.util.List;
import java.util.stream.Collectors;

public class DocumentsContainer {
	private List<Document> documents;

	public DocumentsContainer() {
		this.documents = List.of();
	}

	public DocumentsContainer(List<Document> documents) {
		this.documents = documents;
	}

	public List<Document> getDocuments() {
		return this.documents;
	}

	public void append(Document document) {
		this.documents.add(document);
	}

	public void remove(Document document) {
		this.documents.remove(document);
	}

	public List<Document> search(String query) {
		if (query.isBlank()) {
			return this.documents;
		}

		return documents
		.stream()
		.filter(document -> {
			return (document.getTitle().isPresent() && document.getTitle().get().contains(query)) 
			|| document.getHtmlContent().contains(query) 
			|| document.getPath().contains(query)
			|| document.getName().contains(query);
		})
		.collect(Collectors.toList());
	}
}
