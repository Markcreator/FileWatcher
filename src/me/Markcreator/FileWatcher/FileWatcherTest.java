package me.Markcreator.FileWatcher;

import java.nio.file.Paths;
import java.util.Date;

public class FileWatcherTest {
	
	public static void test(String path, boolean recursive) {
		Date start = new Date();
		
		System.out.println("Loading folders in '" + path + "'" + (recursive ? " recursively" : "") + "...");
		
		new FileListener(Paths.get(path), recursive) {
			public void onFileEvent(FileEvent e) {
				System.out.println(e.getKind().name() + " " + e.getFile().getAbsolutePath());
			}
		};
		
		Date end = new Date();
		
		System.out.println(WatchManager.getInstance().getFileListeners().size() + " folders are being watched (Took " + (end.getTime()-start.getTime()) + "ms)");
	}
}
