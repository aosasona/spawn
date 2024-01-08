package com.trulyao.spawn.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import com.trulyao.spawn.utils.AppConstants;
import com.trulyao.spawn.utils.Logger;

public final class Document {
	private final String name;
	private final String path;
	private String rawContent = "";
	private Optional<String> title = Optional.empty();
	private Optional<String> htmlContent = Optional.empty();
	private Map<String, List<String>> metadata = new HashMap<>();
	private final Optional<Date> lastModifiedAt;

	public Document(Path path) {
		this.name = path.getFileName().toString();
		this.path = path.toString();
		this.lastModifiedAt = Optional.of(new Date(path.toFile().lastModified()));
	}

	public String toString() {
		return String.format(
			"Document[name=%s , path=%s, title=%s, lastModifiedAt=%s]",
			this.name, this.path, this.title.orElse("null"), this.lastModifiedAt);
	}

	// Get the filename with .md extension
	public String getFileName() {
		return this.name;
	}

	// Get the absolute path to the file
	public String getPath() {
		return this.path;
	}

	// Get the title of the document - `getFileName` should probably be used as an alternative if this function returns empty
	public Optional<String> getTitle() {
		return this.title;
	}

	// Get the parsed HTML version of the document's body
	public Optional<String> getHtmlContent() {
		return this.htmlContent;
	}

	public String getRawContent() {
		if (this.rawContent == null) {
			return "";
		}

		return this.rawContent;
	}

	public Map<String, List<String>> getMetadata() {
		return this.metadata;
	}

    // Update the whole content of the document (including front matter)
	public void setRawContent(String rawContent) {
		this.rawContent = rawContent;
	}

	// Set the content of the document without the front matter
	public void setBody(String body) {
		this.rawContent = this.getMetaAsString() + body;
	}

	public Document loadBody() {
		this.parseBody();
		return this;
	}

	public static String makeMetaString(String title) {
		String metadata = "---" + System.lineSeparator();
		metadata += "title: " + title + System.lineSeparator();
		metadata += "---" + System.lineSeparator();
		return metadata;
	}

	public String getMetaAsString() {
		return Document.makeMetaString(this.getTitle().orElse(this.getFileName()));
	}

	public String getBodyAsString() {
		String meta = this.getMetaAsString();
        return this.rawContent.replace(meta, "");
	}

	public Boolean save() {
		try {
			String content = this.getMetaAsString();
			content += this.getBodyAsString();
			Files.write(Paths.get(this.path), content.getBytes());
			return true;
		} catch (Exception e) {
			HashMap<String, String> meta = new HashMap<>();
			meta.put("originalError", e.getMessage());
			Logger.getSharedInstance().error("Failed to save document", meta);
			return false;
		}
	}

	public Boolean delete() {
		try {
			Files.delete(Paths.get(this.path));
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static DocumentsContainer getAll() throws IOException {
		List<Document> documents = new ArrayList<>();
		String dataDir = AppConstants.getPath(AppConstants.PathKey.DATA_DIR);

		Files.walk(Paths.get(dataDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".md")) {
				try {
					Document item = Document.toDocument(filePath);
					documents.add(item);
				} catch (Exception e) {
					HashMap<String, String> meta = new HashMap<>();
					meta.put("originalError", e.getMessage());
					Logger.getSharedInstance().error("Failed to process file: " + filePath, meta);
				}
			}
		});

		return new DocumentsContainer(documents);
	}

	public static Document toDocument(Path fullPath) throws IOException {
		Document document = new Document(fullPath);
		document.setRawContent(Document.readFile(fullPath.toString()));
		document.parseMeta();
		return document;
	}


    private static String readFile(String path) throws IOException {
		BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(path));
		StringBuilder stringBuilder = new StringBuilder();

		// Read document line-by-line; due to differences between Windows and other systems, we cannot just throw a `\n` in there
		String line = bufferedReader.readLine();
		while (line != null) {
			stringBuilder.append(line);
			stringBuilder.append(System.lineSeparator());
			line = bufferedReader.readLine();
		}
		bufferedReader.close();

		return stringBuilder.toString();
	}

	private static Parser makeParser() {
		List<Extension> extensions = List.of(YamlFrontMatterExtension.create());
        return Parser.builder().extensions(extensions).build();
	}

	// Since this parser is used in two different places, we need to be able to parse the body or not because of performance reasons
	// plus we don't need the body when we're just listing the documents
	private void parseMeta() {
		Parser parser = Document.makeParser();
		YamlFrontMatterVisitor visitor = new YamlFrontMatterVisitor();
		Node node = parser.parse(this.rawContent);
		node.accept(visitor);

		// Parse the YAML front matter
		Map<String, List<String>> data = visitor.getData();
		if (data.containsKey("title") && !data.get("title").isEmpty()) {
			this.title = Optional.of(data.get("title").getFirst());
		} else {
			this.title = Optional.empty();
		}
		this.metadata = data;
	}

	private void parseBody() {
		Parser parser = Document.makeParser();
		Node node = parser.parse(this.getBodyAsString());

		// Parse the markdown content
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		this.htmlContent = Optional.of(renderer.render(node));
	}
}
