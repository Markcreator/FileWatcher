package me.Markcreator.FileWatcher;

import java.nio.file.Paths;
import java.util.Date;

public class Main {

	public static void main(String[] args) {
		Date start = new Date();
		
		System.out.println("Loading folders...");
		
		new FileListener(Paths.get("F:/Files"), true) {
			public void onFileEvent(FileEvent e) {
				System.out.println(e.getKind().name() + " " + e.getFile().getAbsolutePath());
			}
		};
		
		Date end = new Date();
		
		System.out.println(WatchManager.getInstance().getFileListeners().size() + " folders are being watched (Took " + (end.getTime()-start.getTime()) + "ms)");
	}
}
