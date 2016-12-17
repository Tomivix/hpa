package view.code;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import view.View;
import view.code.documents.DirStyledDocument;
import view.code.documents.OrderStyledDocument;

import static javax.swing.SpringLayout.*;

public class CodePanel extends JPanel{
	private static final long serialVersionUID = 1L;

	JTextPane dirPane, ordersPane;
	OrderStyledDocument orderSDoc;
	JButton dragAreaButton;
	public CodePanel(){
		super();
		SpringLayout layout;
		super.setLayout(layout = new SpringLayout());

		dirPane = new JTextPane();
		NumHeader dirNumHeader = new NumHeader(dirPane);
		DirStyledDocument dirSDoc = new DirStyledDocument(dirNumHeader);
		dirPane.setStyledDocument(dirSDoc);
		CodeArea dirArea = new CodeArea("Directives:", dirPane, dirNumHeader);

		ordersPane = new JTextPane();
		NumHeader orderNumHeader = new NumHeader(ordersPane);
		orderSDoc = new OrderStyledDocument(orderNumHeader);
		ordersPane.setStyledDocument(orderSDoc);
		CodeArea orderArea = new CodeArea("Orders:", ordersPane, orderNumHeader);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dirArea, orderArea);
		splitPane.setDividerLocation(View.DEFAULT_DIR_PANE_HEIGHT);
		splitPane.setOneTouchExpandable(true);

		dragAreaButton = new JButton("\u2190");

		dragAreaButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(View.Instance.isDragDialogShown()){
					View.Instance.hideDragDialog();
				}else{
					View.Instance.showDragDialog();
				}
			}
		});

		add(dragAreaButton);
		add(splitPane);

		layout.putConstraint(WEST, dragAreaButton, 0, WEST, this);
		layout.putConstraint(EAST, dragAreaButton, View.DRAG_AREA_BUTTON_WIDTH, WEST, dragAreaButton);
		layout.putConstraint(NORTH, dragAreaButton, 0, NORTH, this);
		layout.putConstraint(SOUTH, dragAreaButton, 0, SOUTH, this);

		layout.putConstraint(WEST, splitPane, 0, EAST, dragAreaButton);
		layout.putConstraint(EAST, splitPane, 0, EAST, this);
		layout.putConstraint(NORTH, splitPane, 0, NORTH, this);
		layout.putConstraint(SOUTH, splitPane, 0, SOUTH, this);
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

	public void setDirectives(String code) {
		dirPane.setText(code);
	}

	public void setOrders(String code) {
		ordersPane.setText(code);
	}

	public void highlightLine(int index){
		orderSDoc.highlightLine(index);
	}

	public void setCodeAreasEnabled(boolean b){
		dirPane.setEditable(b);
		ordersPane.setEditable(b);
	}

	public void setDragAreaButtonText(boolean left){
		dragAreaButton.setText(left ? "\u2190" : "\u2192");
	}
}
