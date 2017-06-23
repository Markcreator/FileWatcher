package me.Markcreator.FileWatcher;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;

public class WatchManager {

	private static WatchManager watchManager = new WatchManager();
	private WatchService watcher;
	private ArrayList<FileListener> listeners = new ArrayList<>();

	private WatchManager() {
		watcher = null;
		try {
			watcher = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			e.printStackTrace();
		}

		startListener();
	}

	@SuppressWarnings("unchecked")
	public void startListener() {
		Thread t = new Thread(() -> {
			for (;;) {

				// Wait a key to be signaled
				WatchKey key;
				try {
					key = watcher.take();
				} catch (InterruptedException x) {
					return;
				}
				FileListener fl = getFileListener(key);

				if (fl != null) {
					for (WatchEvent<?> event : key.pollEvents()) {
						WatchEvent.Kind<?> kind = event.kind();

						// This key is registered only
						// for ENTRY_CREATE events,
						// but an OVERFLOW event can
						// occur regardless if events
						// are lost or discarded
						if (kind == OVERFLOW) {
							continue;
						}

						// The filename is the context of the event
						WatchEvent<Path> ev = (WatchEvent<Path>) event;
						Path filename = ev.context();
						File file = fl.getDir().resolve(filename).toFile();

						// Call the event
						fl.onFileEvent(new FileEvent(file, kind));

						// Register new listener(s) if new file is a folder
						if (fl.isRecursive() && file.isDirectory() && kind == ENTRY_CREATE) {
							new FileListener(file.toPath(), true) {
								public void onFileEvent(FileEvent e) {
									fl.onFileEvent(e);
								}
							};
						}

						// Remove listener if file is folder and is deleted
						if (kind == ENTRY_DELETE && fl.isRecursive()) {
							FileListener deletedListener = getFileListener(file.getAbsolutePath());
							if (deletedListener != null) {
								removeFileListener(deletedListener);
							}
						}
					}

					// Reset the key -- this step is critical if you want to
					// receive further watch events. If the key is no longer
					// valid,
					// the directory is inaccessible so remove the listener
					boolean valid = key.reset();
					if (!valid) {
						removeFileListener(fl);
					}
				}
			}
		});
		t.start();
	}

	public static WatchManager getInstance() {
		return watchManager;
	}

	public WatchService getWatcher() {
		return watcher;
	}

	public void addFileListener(FileListener fl) {
		listeners.add(fl);
		System.out.println("[FileWatcher] Registered " + fl.getDir().toString());
	}

	public void removeFileListener(FileListener fl) {
		listeners.remove(fl);
		fl.getKey().cancel();
		System.out.println("[FileWatcher] Unregistered " + fl.getDir().toString());

		// Remove all child listeners
		if (fl.isRecursive()) {
			for (FileListener all : getFileListeners()) {
				if (all.getDir().startsWith(fl.getDir())) {
					removeFileListener(all);
				}
			}
		}
	}

	public FileListener getFileListener(WatchKey key) {
		for (FileListener fl : listeners) {
			if (fl.getKey().equals(key)) {
				return fl;
			}
		}
		return null;
	}

	public FileListener getFileListener(String path) {
		for (FileListener fl : listeners) {
			if (fl.getDir().toString().equals(path)) {
				return fl;
			}
		}
		return null;
	}

	public ArrayList<FileListener> getFileListeners() {
		return listeners;
	}
}

// https://docs.oracle.com/javase/tutorial/essential/io/notification.html
