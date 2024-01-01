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
	private String name;
	private String path;
	private String rawContent = "";
	private Optional<String> title = Optional.empty();
	private Optional<String> htmlContent = Optional.empty();
	private Map<String, List<String>> metadata = new HashMap<String, List<String>>();
	private Optional<Date> lastModifiedAt = Optional.empty();

	public Document(Path path) {
		this.name = path.getFileName().toString();
		this.path = path.toString();
		this.lastModifiedAt = Optional.of(new Date(path.toFile().lastModified()));
	}

	/**
	 * Get the raw file name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get the full path to the file
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * Get the title of the document, when this is absent, the name of the file should be used
	 */
	public Optional<String> getTitle() {
		return this.title;
	}

	/**
	 * Get the parsed HTML version of the document's body
	 */
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

	public Optional<Date> getLastModifiedAt() {
		return this.lastModifiedAt;
	}

	public void setRawContent(String rawContent) {
		this.rawContent = rawContent;
	}

	public void setBody(String body) {
		this.rawContent = this.getMetaAsString() + body;
	}

	public Document loadBody() {
		this.parseBody();
		return this;
	}

	public String getMetaAsString() {
		String metadata = "---\n";
		if (this.title.isPresent()) {
			metadata += "title: " + this.title.get() + "\n";
		} else {
			metadata += "title: " + this.getName() + "\n";
		}
		metadata += "---\n";
		return metadata;
	}

	public String getBodyAsString() {
		String meta = this.getMetaAsString();
		String body = this.rawContent.replace(meta, "");
		return body;
	}

	public Boolean save() {
		try {
			String content = this.getMetaAsString();
			content += this.getBodyAsString();
			Files.write(Paths.get(this.path), content.getBytes());
			return true;
		} catch (Exception e) {
			var meta = new HashMap<String, String>();
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
		List<Document> documents = new ArrayList<Document>();
		String dataDir = AppConstants.getPath(AppConstants.PathKey.DATA_DIR);

		Files.walk(Paths.get(dataDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".md")) {
				try {
					Document item = Document.toDocument(filePath);
					documents.add(item);
				} catch (Exception e) {
					var meta = new HashMap<String, String>();
					meta.put("originalError", e.getMessage());
					Logger.getSharedInstance().error("Failed to process file: " + filePath.toString(), meta);
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

	public static Document toDocument(String fullPath) throws IOException {
		return Document.toDocument(Paths.get(fullPath));
	}

	
	private static String readFile(String path) throws IOException {
		BufferedReader reader = Files.newBufferedReader(Paths.get(path));
		StringBuilder stringBuilder = new StringBuilder();

		String line = reader.readLine();
		while (line != null) {
			stringBuilder.append(line);
			stringBuilder.append(System.lineSeparator());
			line = reader.readLine();
		}

		reader.close();
		return stringBuilder.toString();
	}
	
	private static Parser makeParser() {
		List<Extension> extensions = Arrays.asList(YamlFrontMatterExtension.create());
		Parser parser = Parser.builder().extensions(extensions).build();
		return parser;
	}

	// Since this parser is used in two different places, we need to be able to parse the body or not because of performance reasons 
	// plus we don't need the body when we're just listing the documents
	private void parseMeta() {
		Parser parser = Document.makeParser();
		YamlFrontMatterVisitor visitor = new YamlFrontMatterVisitor();
		Node node = parser.parse(this.getMetaAsString());
		node.accept(visitor);

		// Parse the YAML front matter
		Map<String, List<String>> data = visitor.getData();
		if (data.containsKey("title") && data.get("title").size() > 0) {
			this.title = Optional.of(data.get("title").get(0));
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
