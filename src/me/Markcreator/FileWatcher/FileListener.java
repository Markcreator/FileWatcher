package me.Markcreator.FileWatcher;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchKey;

public abstract class FileListener {

	private WatchKey key;
	private Path dir;
	private boolean recursive;
	
	public FileListener(Path path, boolean recursive) {
		dir = path;
		this.recursive = recursive;
		
		register(path);

		if (recursive) {
			try {
				registerRecursive(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public WatchKey getKey() {
		return key;
	}
	
	public Path getDir() {
		return dir;
	}

	public boolean isRecursive() {
		return recursive;
	}
	
	public abstract void onFileEvent(FileEvent event);

	private void register(Path path) {
		WatchManager wm = WatchManager.getInstance();

		try {
			key = path.register(wm.getWatcher(), ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			wm.addFileListener(this);

		} catch (IOException x) {
			System.err.println(x);
		}
	}
	
	private void registerRecursive(final Path root) throws IOException {
		FileListener master = this;

		// Register all subfolders		
		File[] directories = root.toFile().listFiles(File::isDirectory);
		for(File directory : directories) {
			new FileListener(directory.toPath(), true) {
				public void onFileEvent(FileEvent event) {
					master.onFileEvent(event);
				}
			};
		}
	}
}
