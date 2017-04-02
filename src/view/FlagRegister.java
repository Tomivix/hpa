package view;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class FlagRegister extends JPanel {
	private static final long serialVersionUID = 1L;

	public static enum STATE{
		N("N"), Z("Z"), P("P"), UNDEFINED("~");
		String text;
		STATE(String s){
			text = s;
		}
	}

	private JLabel currentStateLabel;
	public FlagRegister(){
		super();
		super.setLayout(new BorderLayout());
		JLabel titleLabel = new JLabel("Program State Register", SwingConstants.CENTER);
		titleLabel.setFont(titleLabel.getFont().deriveFont(12f));
		currentStateLabel = new JLabel("~", SwingConstants.CENTER);
		currentStateLabel.setFont(currentStateLabel.getFont().deriveFont(30f));
		super.add(titleLabel, BorderLayout.PAGE_START);
		super.add(currentStateLabel, BorderLayout.CENTER);
	}

	public void setState(STATE s){
		currentStateLabel.setText(s.text);
	}

}
