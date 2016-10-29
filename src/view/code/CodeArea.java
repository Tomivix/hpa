package view.code;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;

import static javax.swing.SpringLayout.*;

public class CodeArea extends JPanel {
	private static final long serialVersionUID = 1L;

	public CodeArea(String label, JTextPane textPane, JPanel numHeader){
		super();
		SpringLayout layout = new SpringLayout();
		super.setLayout(layout);
		TitlePanel tPanel = new TitlePanel(label, textPane);
		super.add(tPanel);
		layout.putConstraint(NORTH, tPanel, 0, NORTH, this);
		layout.putConstraint(WEST, tPanel, 0, WEST, this);
		layout.putConstraint(EAST, tPanel, 0, EAST, this);
		layout.putConstraint(SOUTH, tPanel, 20, NORTH, tPanel);
		JScrollPane textScroll = new JScrollPane(textPane);
		textScroll.setRowHeaderView(numHeader);
		super.add(textScroll);
		layout.putConstraint(NORTH, textScroll, 0, SOUTH, tPanel);
		layout.putConstraint(WEST, textScroll, 0, WEST, this);
		layout.putConstraint(EAST, textScroll, 0, EAST, this);
		layout.putConstraint(SOUTH, textScroll, 0, SOUTH, this);
	}
}
