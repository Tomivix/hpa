package view.code;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import view.View;
import view.code.documents.DirStyledDocument;
import view.code.documents.OrderStyledDocument;

public class CodePanel extends JPanel{
	private static final long serialVersionUID = 1L;

	JTextPane dirPane, ordersPane;
	public CodePanel(){
		super();
		super.setLayout(new GridLayout(1, 0));
		dirPane = new JTextPane();
		NumHeader dirNumHeader = new NumHeader(dirPane);
		DirStyledDocument dirSDoc = new DirStyledDocument(dirNumHeader);
		dirPane.setStyledDocument(dirSDoc);
		CodeArea dirArea = new CodeArea("Directives:", dirPane, dirNumHeader);
		
		ordersPane = new JTextPane();
		NumHeader orderNumHeader = new NumHeader(ordersPane);
		OrderStyledDocument orderSDoc = new OrderStyledDocument(orderNumHeader);
		ordersPane.setStyledDocument(orderSDoc);
		CodeArea orderArea = new CodeArea("Orders:", ordersPane, orderNumHeader);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dirArea, orderArea);
		splitPane.setDividerLocation(View.DEFAULT_DIR_PANE_HEIGHT);
		splitPane.setOneTouchExpandable(true);
		add(splitPane);
	}
	
	public String getDirectives(){
		Document d = dirPane.getDocument();
		String out = "";
		try{
			out = d.getText(0, d.getLength());
		}catch(BadLocationException e){
			e.printStackTrace();
		}
		return out;
	}
	
	public String getOrders(){
		Document d = ordersPane.getDocument();
		String out = "";
		try{
			out = d.getText(0, d.getLength());
		}catch(BadLocationException e){
			e.printStackTrace();
		}
		return out;
	}
}
