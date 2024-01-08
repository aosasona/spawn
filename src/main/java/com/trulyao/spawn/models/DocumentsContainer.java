package com.trulyao.spawn.models;

import java.util.List;
import java.util.stream.Collectors;

public class DocumentsContainer {
	private final List<Document> documents;

	public DocumentsContainer() {
		this.documents = List.of();
	}

	public DocumentsContainer(List<Document> docs) {
		this.documents = docs;
		this.toSortedList();
	}

	public List<Document> getDocuments() {
		if (this.documents == null) {
			return List.of();
		}

		return this.documents;
	}

	public void toSortedList() {
		this.documents.sort((a, b) -> {
			String titleA = a.getTitle().orElse(a.getFileName()).toLowerCase();
			String titleB = b.getTitle().orElse(b.getFileName()).toLowerCase();
			return titleA.compareTo(titleB);
		});
	}

	public void append(Document document) {
		this.documents.add(document);
	}

	public void remove(Document document) {
		this.documents.remove(document);
	}

	public List<Document> search(String searchQuery) {
		String query = searchQuery.toLowerCase().trim();
		if (query.isBlank()) { return this.documents; }

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
		System.out.println("\tdocuments:");
		for (Document document : this.documents) {
			System.out.println("\t\t- name: " + document.getFileName());
			System.out.println("\t\tpath: " + document.getPath());
			System.out.println("\t\ttitle: " + document.getTitle().orElse("[null]"));
			System.out.println("\t\thtmlContent: " + document.getHtmlContent());
			System.out.println("\t\tmetadata: " + document.getMetadata());
		}
	}
}
