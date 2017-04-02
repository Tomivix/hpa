package core;

import javax.swing.SwingUtilities;

import view.View;

public class Start{

	public static void main(String[] args) {
		new Engine();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				View v = new View();
				v.setRegisters();
			}
		});
	}

}
