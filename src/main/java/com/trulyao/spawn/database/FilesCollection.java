package com.trulyao.spawn.database;

public class FilesCollection extends BaseCollection {
	protected String fileId;
	protected final String __type = "files";

	@Override
	protected String getCollectionName() {
		return "files";
	}

	@Override
	protected String getCollectionIndexKey() {
		return "fileId";
	}
	
}
