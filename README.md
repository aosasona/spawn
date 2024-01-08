# Spawn

A simple Markdown-based note-taking app in JavaFX; a second-year university project.

## Features
- [x] Markdown support
- [x] Markdown preview
- [x] File-based; no database required
- [x] Deep search (searches in all notes)

## Running

To run the app, you need OpenJDK 21 or higher. You can download it [here](https://adoptopenjdk.net/).

```sh
mvn clean javafx:run
```

> This application also contains a full logging system, however debug logs are disabled by default. To enable them, set the `DEBUG` environment variable to `1` (or anything really), like this: `DEBUG=1 mvn clean javafx:run`
