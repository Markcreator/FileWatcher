package me.Markcreator.FileWatcher;

import java.nio.file.Paths;

public class Main {

	public static void main(String[] args) {
		new FileListener(Paths.get("F:/Projects/Plugins"), true) {
			public void onFileEvent(FileEvent e) {
				System.out.println(e.getKind().name() + " " + e.getFile().getAbsolutePath());
			}
		};
		
		System.out.println(WatchManager.getInstance().getFileListeners().size() + " folders are being watched");
	}
}
