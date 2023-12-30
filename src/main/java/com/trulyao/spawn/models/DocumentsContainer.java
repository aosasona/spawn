package com.trulyao.spawn.models;

import java.util.List;
import java.util.stream.Collectors;

public class DocumentsContainer {
	private List<Document> documents;

	public DocumentsContainer() {
		this.documents = List.of();
	}

	public DocumentsContainer(List<Document> docs) {
		this.documents = docs;
		this.documents.sort((a, b) -> a.getTitle().orElse(a.getName()).compareTo(b.getTitle().orElse(b.getName())));
	}

	public List<Document> getDocuments() {
		if (this.documents == null) {
			return List.of();
		}

		return this.documents;
	}

	public void toSortedList() {
		this.documents.sort((a, b) -> a.getTitle().orElse(a.getName()).compareTo(b.getTitle().orElse(b.getName())));
	}

	public void append(Document document) {
		this.documents.add(document);
	}

	public void remove(Document document) {
		this.documents.remove(document);
	}

	public List<Document> search(String originalQuery) {
		var query = originalQuery.toLowerCase();

		if (query.isBlank()) {
			return this.documents;
		}

		return documents
		.stream()
		.filter(document -> {
			// technically, if we are looking at the raw content, we already have all the searchable data we need (the raw content also contains the title)
			return (document.getTitle().isPresent() && document.getTitle().get().toLowerCase().contains(query)) 
			|| document.getRawContent().toLowerCase().contains(query) 
			|| document.getPath().toLowerCase().contains(query);
		})
		.collect(Collectors.toList());
	}

	public void print() {
		System.out.println("DocumentsContainer:");
		System.out.println("  documents:");
		for (Document document : this.documents) {
			System.out.println("    - name: " + document.getName());
			System.out.println("      path: " + document.getPath());
			System.out.println("      title: " + document.getTitle().orElse(""));
			System.out.println("      htmlContent: " + document.getHtmlContent());
			System.out.println("      metadata: " + document.getMetadata());
		}
	}
}
