package view.reg_mem;

import java.awt.Dimension;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import core.Engine;
import view.View;

import static javax.swing.SpringLayout.*;

public class ValueEditor {
	private static final int DIALOG_HEIGHT = 200, DIALOG_WIDTH = 300;
	private static final int VERT_PAD = 15, HORIZON_PAD = 5;

	private static JTextField[] textFields; //0 - binary, 1 - decimal, 2 - hexadecimal
	private static boolean userModified = false;
	private static JButton acceptButton;
	private static JTextField decTextField;

	static{
		textFields = new JTextField[3];
	}

	public static void displayCellValue(int index, String label){
		displayValue(false, index, label);
	}

	public static void displayRegisterValue(int index){
		displayValue(true, index, null);
	}

	private static void displayValue(final boolean isRegister, final int index, String label ){
		final JDialog valueFrame = new JDialog();
		valueFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		String title = "Edit: " + (isRegister ? "Register" : "Memory Cell") + " " + index;
		if(label != null){
			title += " (" + label + ")";
		}

		SpringLayout layout;
		valueFrame.setLayout(layout = new SpringLayout());

		JLabel binLabel = new JLabel("Binary:");
		valueFrame.add(binLabel);
		layout.putConstraint(NORTH, binLabel, VERT_PAD, NORTH, valueFrame.getContentPane());
		layout.putConstraint(WEST, binLabel, HORIZON_PAD, WEST, valueFrame.getContentPane());

		JLabel decLabel = new JLabel("Decimal:");
		valueFrame.add(decLabel);
		layout.putConstraint(NORTH, decLabel, VERT_PAD, SOUTH, binLabel);
		layout.putConstraint(WEST, decLabel, HORIZON_PAD, WEST, valueFrame.getContentPane());

		JLabel hexLabel = new JLabel("Hexdecimal:");
		valueFrame.add(hexLabel);
		layout.putConstraint(NORTH, hexLabel, VERT_PAD, SOUTH, decLabel);
		layout.putConstraint(WEST, hexLabel, HORIZON_PAD, WEST, valueFrame.getContentPane());

		int westConstr = Math.max(Math.max(layout.getConstraints(binLabel).getWidth().getValue(), layout.getConstraints(decLabel).getWidth().getValue()), layout.getConstraints(hexLabel).getWidth().getValue());

		JTextField binTextField = new JTextField();
		textFields[0] = binTextField;
		AbstractDocument binDocument = new RestrictedInputDocument(new char[]{
				'0', '1'
		});
		binDocument.addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateLabels();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateLabels();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateLabels();
			}

			private void updateLabels(){
				if(userModified == false){
					userModified = true;
					recalculateFields(0);
				}
			}
		});
		binTextField.setDocument(binDocument);
		valueFrame.add(binTextField);
		layout.putConstraint(VERTICAL_CENTER, binTextField, 0, VERTICAL_CENTER, binLabel);
		layout.putConstraint(EAST, binTextField, -HORIZON_PAD, EAST, valueFrame.getContentPane());
		layout.putConstraint(WEST, binTextField, westConstr + HORIZON_PAD*2, WEST, valueFrame.getContentPane());

		decTextField = new JTextField();
		textFields[1] = decTextField;
		AbstractDocument decDocument = new RestrictedInputDocument(new char[]{
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'
		});
		decDocument.addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateLabels();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateLabels();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateLabels();
			}

			private void updateLabels(){
				if(userModified == false){
					userModified = true;
					recalculateFields(1);
				}
			}
		});
		decTextField.setDocument(decDocument);
		valueFrame.add(decTextField);
		layout.putConstraint(VERTICAL_CENTER, decTextField, 0, VERTICAL_CENTER, decLabel);
		layout.putConstraint(EAST, decTextField, -HORIZON_PAD, EAST, valueFrame.getContentPane());
		layout.putConstraint(WEST, decTextField, westConstr + HORIZON_PAD*2, WEST, valueFrame.getContentPane());

		JTextField hexTextField = new JTextField();
		textFields[2] = hexTextField;
		AbstractDocument hexDocument = new RestrictedInputDocument(new char[]{
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', '-'
		});
		hexDocument.addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateLabels();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateLabels();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateLabels();
			}

			private void updateLabels(){
				if(userModified == false){
					userModified = true;
					recalculateFields(2);
				}
			}
		});
		hexTextField.setDocument(hexDocument);
		valueFrame.add(hexTextField);
		layout.putConstraint(VERTICAL_CENTER, hexTextField, 0, VERTICAL_CENTER, hexLabel);
		layout.putConstraint(EAST, hexTextField, -HORIZON_PAD, EAST, valueFrame.getContentPane());
		layout.putConstraint(WEST, hexTextField, westConstr + HORIZON_PAD*2, WEST, valueFrame.getContentPane());

		acceptButton = new JButton("Accept");
		acceptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(isRegister){
					Engine.current.setReg(index, Integer.parseInt(textFields[1].getText()));
				}else{
					Engine.current.setVar(index, Integer.parseInt(textFields[1].getText()));
				}
				valueFrame.dispose();
			}
		});
		valueFrame.add(acceptButton);
		layout.putConstraint(SOUTH, acceptButton, -VERT_PAD, SOUTH, valueFrame.getContentPane());
		layout.putConstraint(HORIZONTAL_CENTER, acceptButton, DIALOG_WIDTH/4, WEST, valueFrame.getContentPane());

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				valueFrame.dispose();
			}
		});
		valueFrame.add(cancelButton);
		layout.putConstraint(SOUTH, cancelButton, -VERT_PAD, SOUTH, valueFrame.getContentPane());
		layout.putConstraint(HORIZONTAL_CENTER, cancelButton, DIALOG_WIDTH*3/4, WEST, valueFrame.getContentPane());

		decTextField.setText((isRegister ? Engine.current.getReg(index) : Engine.current.getVar(index)) + "");

		valueFrame.setTitle(title);
		Dimension minSize = new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT);
		valueFrame.setMinimumSize(minSize);
		valueFrame.setSize(minSize);
		valueFrame.setLocationRelativeTo(View.Instance.getMainFrame());
		valueFrame.setVisible(true);
		focusDecField();
		valueFrame.setModalityType(ModalityType.APPLICATION_MODAL);
		valueFrame.setVisible(false);
		valueFrame.setVisible(true); //FIXME Works for now, but it doesn't look right hiding and showing a window
	}

	protected static void recalculateFields(int originIndex){ //TODO: Allow to input only valid numbers
		String s = textFields[originIndex].getText().length() == 0 ? "0" : textFields[originIndex].getText();;
		int i;
		switch(originIndex){
		case 0:
			try{
				i = Integer.parseInt(s, 2);
				acceptButton.setEnabled(true);
			}catch(NumberFormatException e){
				acceptButton.setEnabled(false);
				i = 0;
			}
			textFields[1].setText(i + "");
			textFields[2].setText(Integer.toHexString(i));
			break;
		case 1:
			try{
				i = Integer.parseInt(s);
				acceptButton.setEnabled(true);
			}catch(NumberFormatException e){
				i = 0;
				acceptButton.setEnabled(false);
			}
			textFields[0].setText(Integer.toBinaryString(i));
			textFields[2].setText(Integer.toHexString(i));
			break;
		case 2:
			try{
				i = Integer.parseInt(s, 16);
				acceptButton.setEnabled(true);
			}catch(NumberFormatException e){
				i = 0;
				acceptButton.setEnabled(false);
			}
			textFields[0].setText(Integer.toBinaryString(i));
			textFields[1].setText(i + "");
			break;
		}
		userModified = false;
	}

	public static void focusDecField(){
		decTextField.requestFocus();
		decTextField.selectAll();
	}
}
