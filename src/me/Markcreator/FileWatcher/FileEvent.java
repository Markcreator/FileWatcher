package me.Markcreator.FileWatcher;

import java.io.File;
import java.nio.file.WatchEvent.Kind;

public class FileEvent {

	private File file;
	private Kind<?> kind;
	
	public FileEvent(File file, Kind<?> kind) {
		this.file = file;
		this.kind = kind;
	}
	
	public File getFile() {
		return file;
	}
	
	public Kind<?> getKind() {
		return kind;
	}
}
