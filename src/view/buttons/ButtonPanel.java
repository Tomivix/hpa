package view.buttons;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import core.Engine;
import view.FlagRegister;
import view.HelpDialog;
import view.View;
import view.View.Button;

public class ButtonPanel extends JPanel implements ChangeListener{
	private static final long serialVersionUID = 1L;

	private FlagRegister flagRegister;
	private ChangeableImageButton runButton;
	ImageButton stepButton, backstepButton, saveButton, loadButton, buildButton, calculateButton;
	private JSlider timeSlider;
	private JLabel runInfoLabel;
	public ButtonPanel(){
		super.setLayout(new BorderLayout());
		//@mrwasp
		saveButton = new ImageButton("save", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					View.Instance.save();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});

		loadButton = new ImageButton("load", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					View.Instance.load();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});


		buildButton = new ImageButton("build", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				View.Instance.build();
			}
		});

		runButton = new ChangeableImageButton("run", "pause", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				View.Instance.setRunning(!View.Instance.isRunning());
				if(View.Instance.isRunning()){
					View.Instance.run();
				}else{
					View.Instance.pause();
				}
			}
		});

		timeSlider = new JSlider(JSlider.HORIZONTAL, 0, View.SLIDER_MAX_VAL, View.SLIDER_MIN_VAL);
		timeSlider.setMinorTickSpacing(View.SLIDER_MIN_VAL);
		timeSlider.setMajorTickSpacing(10 * View.SLIDER_MIN_VAL);
		timeSlider.setPaintTicks(true);
		timeSlider.setSnapToTicks(true);
		timeSlider.setPaintLabels(true);
		timeSlider.addChangeListener(this);

		runInfoLabel = new JLabel("Step every " + timeSlider.getValue() + "ms");
		runInfoLabel.setAlignmentX(CENTER_ALIGNMENT);

		stepButton = new ImageButton("step", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Engine.current.step();
			}
		});

		//@mrwasp
		backstepButton = new ImageButton("backstep", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Engine.current.backStep();
			}
		});
		
		calculateButton = new ImageButton("calculate", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				View.Instance.calculate();
			}
		});

		ImageButton helpButton = new ImageButton("help", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				HelpDialog.show();
			}
		});

		JPanel leftPane = new JPanel();
		JPanel centerPane = new JPanel();
		centerPane.setLayout(new BoxLayout(centerPane, BoxLayout.PAGE_AXIS));
		JPanel rightPane = new JPanel();

		leftPane.add(helpButton);
		leftPane.add(saveButton);
		leftPane.add(loadButton);
		leftPane.add(buildButton);

		centerPane.add(timeSlider);
		centerPane.add(runInfoLabel);

		rightPane.add(flagRegister = new FlagRegister());
		rightPane.add(backstepButton);
		rightPane.add(runButton);
		rightPane.add(stepButton);
		rightPane.add(calculateButton);

		super.add(leftPane, BorderLayout.LINE_START);
		super.add(centerPane, BorderLayout.CENTER);
		super.add(rightPane, BorderLayout.LINE_END);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		View.Instance.setRunInterval(timeSlider.getValue());
		runInfoLabel.setText("Step every " +
				Math.round((float)timeSlider.getValue()/(float)View.SLIDER_MIN_VAL)*View.SLIDER_MIN_VAL +
				"ms");
	}

	public void setRunButtonImg(int index){
		runButton.setImage(index);
	}

	public void setButtonState(Button button, boolean state){
		switch(button){
		case LOAD:
			loadButton.setEnabled(state);
			break;
		case SAVE:
			saveButton.setEnabled(state);
			break;
		case BUILD:
			buildButton.setEnabled(state);
			break;
		case STEP:
			stepButton.setEnabled(state);
			break;
		case RUN:
			runButton.setEnabled(state);
			break;
		case BACKSTEP:
			backstepButton.setEnabled(state);
			break;
		case CALCULATE:
			calculateButton.setEnabled(state);
			break;
			
		}
	}

	public void setFlagRegisterState(FlagRegister.STATE state){
		flagRegister.setState(state);
	}
}
