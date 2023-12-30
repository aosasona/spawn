package com.trulyao.spawn.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import com.trulyao.spawn.utils.AppConstants;
import com.trulyao.spawn.utils.Logger;

// TODO: create new container class as a nested class to hold a list of documents - this will have methods attached for things like searching, filtering etc
// TODO: include lastModifiedAt and createdAt in the document model
public final class Document {
	private String name;
	private String path;
	private String rawContent;
	private Optional<String> title;
	private Optional<String> htmlContent;
	private Map<String, List<String>> metadata;

	public Document(String name, String path) {
		this.name = name;
		this.path = path;
	}

	public String getName() {
		return this.name;
	}

	public String getPath() {
		return this.path;
	}

	public Optional<String> getTitle() {
		return this.title;
	}

	public String getHtmlContent() {
		return this.htmlContent.orElse("");
	}

	public Map<String, List<String>> getMetadata() {
		return this.metadata;
	}

	public void setRawContent(String rawContent) {
		this.rawContent = rawContent;
	}

	public static DocumentsContainer getAll() throws IOException {
		DocumentsContainer documents = new DocumentsContainer();
		String dataDir = AppConstants.getPath(AppConstants.PathKey.DATA_DIR);

		Files.walk(Paths.get(dataDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".md")) {
				try {
					Document item = Document.toDocument(filePath);
					documents.append(item);
				} catch (IOException e) {
					Logger.getSharedInstance().error("Could not read file: " + filePath.toString());
				}
			}
		});

		return documents;
	}

	private static Document toDocument(Path fullPath) throws IOException {
		Document document = new Document(fullPath.getFileName().toString(), fullPath.toString());
		document.setRawContent(Document.readFile(fullPath.toString()));
		document.parse(false);
		return document;
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
	private void parse(Boolean parseBody) {
		Parser parser = Document.makeParser();
		YamlFrontMatterVisitor visitor = new YamlFrontMatterVisitor();
		Node node = parser.parse(this.rawContent);
		node.accept(visitor);

		// Parse the YAML front matter
		Map<String, List<String>> data = visitor.getData();
		this.title = Optional.of(data.get("title").get(0));
		this.metadata = data;

		if (!parseBody) { return; }

		// Parse the markdown content
		HtmlRenderer renderer = HtmlRenderer
		.builder()
		.extensions(Arrays.asList(YamlFrontMatterExtension.create()))
		.build();

		this.htmlContent = Optional.of(renderer.render(node));
	}
}
