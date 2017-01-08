package view;

import java.awt.Desktop;
import java.awt.Dialog.ModalityType;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class HelpDialog {
	public static void show(){
		JDialog dialog = new JDialog();
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setModalityType(ModalityType.MODELESS);
		dialog.setTitle(View.APP_NAME + " - about");
		JEditorPane helpText = new JEditorPane();
		helpText.setEditable(false);
		helpText.setOpaque(false);
		helpText.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
		helpText.setText(View.HELP_TEXT);
		helpText.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		            if (Desktop.isDesktopSupported()) {
		                try {
		                    Desktop.getDesktop().browse(e.getURL().toURI());
		                } catch (Exception ex) {
		                    ex.printStackTrace();
		                }
		            }
		        }
			}
		});
		dialog.setResizable(false);
		dialog.add(helpText);
		dialog.pack();
		dialog.setLocationRelativeTo(View.Instance.getMainFrame());
		dialog.setVisible(true);
	}
}
