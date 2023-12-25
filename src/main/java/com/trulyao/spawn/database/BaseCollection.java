package com.trulyao.spawn.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import com.trulyao.spawn.utils.AppConstants;
import com.trulyao.spawn.utils.Logger;

/**
 * A collection is a group of data that is stored in a file, basically raw JSON with this class acting as a smoother abstraction.
 */

abstract public class BaseCollection {
	private static Class<? extends BaseCollection> instance;

	private HashMap<String, Class<? extends BaseCollection>> data;

	private BaseCollection() {
		this.data = new HashMap<String, Class<? extends BaseCollection>>();

		if (!this.exists()) { this.init(); }
		this.load();
	}

	public static Class<? extends BaseCollection> getInstance() {
		synchronized (BaseCollection.class) {
			if (instance == null) {
				instance = BaseCollection.class;
			}
		}

		return instance;
	}

	// The name of the collection is used for storage and retrieval of the data. - provided by the extending class
	abstract protected String getCollectionName();

	// The index name itself is not used for storage at all (for flexibility) but whatever index is chosen serves as the key for the data HashMap which provides blazing fast access to the data. - provided by the extending class
	abstract protected String getCollectionIndexKey();

	// This should only be called when the class is first initialized.
	protected void load() {
		try {
			File file = new File(this.getCollectionPath());
			var content = new String(Files.readAllBytes(file.toPath()));

			// parse the JSON into its raw state 
			// `data` - this holds the actual data in the JSON
			// `meta` - this holds the metadata for the collection e.g. the name of the collection, the index key, etc.
		} catch (IOException e) {
			var meta = new HashMap<String, String>();
			meta.put("collectionName", this.getCollectionName());
			meta.put("originalError", e.getMessage());
			meta.put("originalStackTrace", e.getStackTrace().toString());
			Logger.getSharedInstance().fatal("Failed to load collection file: " + this.getCollectionPath(), meta);
		}
	}

	protected Boolean exists() {
		File file = new File(this.getCollectionPath());
		return file.exists();
	}

	protected void init() {
		try {
			File file = new File(this.getCollectionPath());
			file.getParentFile().mkdirs();
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
	protected String normalizeCollectionName(String collectionName) {
		return collectionName.replaceAll("[^a-zA-Z0-9_]", "");
	}

	private String getCollectionPath() {
		return AppConstants.getStorePath(this.getCollectionName());
	}
}
