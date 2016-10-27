package view.code;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;

import static javax.swing.SpringLayout.*;

public class TitlePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	protected JLabel label;
	protected JButton selectAllButton; 
	public TitlePanel(String labelString, JTextPane textPane){
		super();
		SpringLayout layout = new SpringLayout();
		super.setLayout(layout);
		label = new JLabel(labelString);
		final JTextPane text = textPane;
		super.add(label);
		layout.putConstraint(NORTH, label, 0, NORTH, this);
		layout.putConstraint(SOUTH, label, 0, SOUTH, this);
		layout.putConstraint(WEST, label, 0, WEST, this);
		layout.putConstraint(EAST, label, 100, WEST, label);
		selectAllButton = new JButton("Select all");
		selectAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				text.requestFocusInWindow();
				text.selectAll();
			}
		});
		super.add(selectAllButton);
		layout.putConstraint(NORTH, selectAllButton, 0, NORTH, this);
		layout.putConstraint(SOUTH, selectAllButton, 0, SOUTH, this);
		layout.putConstraint(EAST, selectAllButton, 0, EAST, this);
		layout.putConstraint(WEST, selectAllButton, -100, EAST, selectAllButton);
	}
}
