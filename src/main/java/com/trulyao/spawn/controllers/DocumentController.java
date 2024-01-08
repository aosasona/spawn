package com.trulyao.spawn.controllers;

import java.util.Optional;

import org.controlsfx.control.Notifications;

import com.trulyao.spawn.models.Document;
import com.trulyao.spawn.utils.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class DocumentController {
	private final MainController mainController;

	@FunctionalInterface
	public interface DocumentHotReloader {
		void reload();
	}

	public DocumentController(MainController mainController) {
		this.mainController = mainController;
	}

	public MainController getMainController() {
		return this.mainController;
	}

	public EventHandler<ActionEvent> handleSave() {
		Optional<Document> document = this.mainController.getCurrentDocument();
		return event -> {
            if (document.isEmpty()) {
                return;
            }

            Document doc = document.get();
            Logger.getSharedInstance().debug("Saving document");
            if (!doc.save()) {
                Logger.getSharedInstance().error("Failed to save document");
                return;
            }

            String title = doc.getTitle().isPresent() ? doc.getTitle().get() : doc.getFileName();
            Notifications.create()
                    .title("Document saved")
                    .text(title + " has been saved")
                    .showInformation();
        };
	}

	public void reloadHtmlBody() {
		Optional<Document> document = this.mainController.getCurrentDocument();
		if (document.isEmpty()) { return; }

		Document doc = document.get();
		Logger.getSharedInstance().debug("Reloading HTML body");
		doc.loadBody();
	}

	public void subscribe(DocumentHotReloader hotReloader) {
		this.mainController.subscribe((change) -> {
			if(change.next()) {
				// We actually only _really_ care if it was replaced, that is the only time we do not want to save before switching
				// This is because it is only added the first time we open a file
				if(change.wasReplaced()) {
					// Save the current document before switching
					if (this.mainController.getCurrentDocument().isPresent()) {
						Logger.getSharedInstance().debug("Saving current document before switching");
						if(!this.mainController.getCurrentDocument().get().save()) {
							Logger.getSharedInstance().fatal("Unable to save current document, aborting switch");
							return;
						}
					}
				}

				// Force the new document into the view
				hotReloader.reload();
			}
		});
	}
}
