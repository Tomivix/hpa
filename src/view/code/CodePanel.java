package view.code;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

import view.View;
import view.code.documents.DirStyledDocument;
import view.code.documents.OrderStyledDocument;

public class CodePanel extends JPanel{
	private static final long serialVersionUID = 1L;

	JTextPane dirPane, ordersPane;
	public CodePanel(){
		super();
		super.setLayout(new GridLayout(1, 0));
		NumHeader dirNumHeader = new NumHeader();
		DirStyledDocument dirSDoc = new DirStyledDocument(dirNumHeader);
		dirPane = new JTextPane(dirSDoc);
		CodeArea dirArea = new CodeArea("Directives:", dirPane, dirNumHeader);
		
		NumHeader orderNumHeader = new NumHeader();
		OrderStyledDocument orderSDoc = new OrderStyledDocument(orderNumHeader);
		ordersPane = new JTextPane(orderSDoc);
		CodeArea orderArea = new CodeArea("Orders:", ordersPane, orderNumHeader);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dirArea, orderArea);
		splitPane.setDividerLocation(View.DEFAULT_DIR_PANE_HEIGHT);
		add(splitPane);
	}
}
