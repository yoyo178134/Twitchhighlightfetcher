package HighlightMaker;

import java.awt.EventQueue;

public class Main {
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable(){
			public void run() {
				HighlightMakerFrame myFrame = new HighlightMakerFrame();
				myFrame.setVisible(true);
				
			}
		});
	}
}
