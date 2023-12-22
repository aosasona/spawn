package com.trulyao.spawn.database;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import com.trulyao.spawn.utils.AppConstants;
import com.trulyao.spawn.utils.Logger;

/**
 * A collection is a group of data that is stored in a file, basically raw JSON with this class acting as a smoother abstraction.
 */

abstract public class BaseCollection {
	// The name of the collection is used for storage and retrieval of the data.
	private String collectionName;

	// The index name itself is not used for storage at all (for flexibility) but whatever index is chosen serves as the key for the data HashMap which provides blazing fast access to the data.
	private String collectionIndexKey;

	private HashMap<String, Class<? extends BaseCollection>> data;

	public BaseCollection(String collectionName, String collectionIndexKey) {
		this.collectionName = this.normalizeCollectionName(collectionName);
		this.collectionIndexKey = collectionIndexKey;
		this.data = new HashMap<String, Class<? extends BaseCollection>>();
	}

	// This should only be called when the class is first initialized.
	protected void load() {
	}

	protected void init() {
		File file = new File(this.getCollectionPath());
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e) {
			var meta = new HashMap<String, String>();
			meta.put("collectionName", this.getCollectionName());
			meta.put("originalError", e.getMessage());
			meta.put("originalStackTrace", e.getStackTrace().toString());
			Logger.getSharedInstance().fatal("Failed to create collection file: " + this.getCollectionPath(), meta);
		}
	}

	// This only keeps a-z, A-Z, 0-9, and _ in the collection name as the collection name is used as the actual filename
	private String normalizeCollectionName(String collectionName) {
		return collectionName.replaceAll("[^a-zA-Z0-9_]", "");
	}

	private String getCollectionName() {
		return this.collectionName;
	}

	private String getCollectionPath() {
		return AppConstants.getStorePath(this.getCollectionName());
	}
}
