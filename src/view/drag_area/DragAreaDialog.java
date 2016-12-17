package view.drag_area;

import java.awt.Color;

import javax.swing.JDialog;

public class DragAreaDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public DragAreaDialog(){
		super();
		super.getContentPane().setBackground(Color.cyan);
		super.setModalityType(ModalityType.MODELESS);
		super.setFocusableWindowState(false);
		super.setUndecorated(true);
	}
}
