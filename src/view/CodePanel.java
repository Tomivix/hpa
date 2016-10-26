package view;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

public class CodePanel extends JPanel{
	private static final long serialVersionUID = 1L;

	JTextPane dirPane, ordersPane;
	public CodePanel(){
		super();
		super.setLayout(new GridLayout(1, 0));
		DirStyledDocument dirSDoc = new DirStyledDocument();
		dirPane = new JTextPane(dirSDoc);

		JScrollPane dirScroll = new JScrollPane(dirPane);
		
		OrderStyledDocument orderSDoc = new OrderStyledDocument();
		ordersPane = new JTextPane(orderSDoc);
		JScrollPane ordersScroll = new JScrollPane(ordersPane);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dirScroll, ordersScroll);
		splitPane.setDividerLocation(View.DEFAULT_DIR_PANE_HEIGHT);
		add(splitPane);
	}
}
