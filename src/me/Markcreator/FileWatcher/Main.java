package me.Markcreator.FileWatcher;

public class Main {

	public static void main(String[] args) {
		WatchManager.getInstance().setDebug(true);
		FileWatcherTest.test("F:/Files", true);
	}
	
}
