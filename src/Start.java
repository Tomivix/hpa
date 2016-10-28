import javax.swing.SwingUtilities;

import view.View;

public class Start{

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				View v = new View();
				v.setRegisters(new int[]{
						0, 2, 432, 23, 13, 32, 235, 52, 1, 41, 124, 451, 12, 123, 5, -3
				});
				v.setMemoryCells(new int[]{
						1, 124, 57, -1245, 12, 335, 3251, 436, 26, -141
				});
				v.updateValues(10, 4, View.RM);
			}
		});
	}

}
