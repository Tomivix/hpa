package view.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import core.Engine;
import view.View;

public class ButtonPanel extends JPanel implements ChangeListener{
	private static final long serialVersionUID = 1L;

	private ChangeableImageButton runButton;
	ImageButton stepButton;
	private JSlider timeSlider;
	private JLabel runInfoLabel;
	public ButtonPanel(){
		//@mrwasp
		ImageButton saveButton = new ImageButton("save", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO save
			}
		});
		ImageButton loadButton = new ImageButton("load", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO load
			}
		});
		
		
		
		
		ImageButton buildButton = new ImageButton("build", new ActionListener() {
			
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
					Engine.current.run();
				}else{
					Engine.current.pause();
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
		
		stepButton = new ImageButton("step", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Engine.current.step();
			}
		});
		
		//@mrwasp
		ImageButton backStepButton = new ImageButton("backstep", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Engine.current.backStep();
			}
		});
		
		super.add(saveButton);
		super.add(loadButton);
		super.add(buildButton);
		super.add(timeSlider);
		super.add(runInfoLabel);
		super.add(runButton);
		super.add(stepButton);
		super.add(backStepButton);
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		Engine.current.setRunInterval(timeSlider.getValue());
		runInfoLabel.setText("Step every " + Math.round((float)timeSlider.getValue()/(float)View.SLIDER_MIN_VAL)*View.SLIDER_MIN_VAL + "ms");
	}
	
	public void setRunStepButtonsEnabled(boolean e){
		runButton.setEnabled(e);
		stepButton.setEnabled(e);
	}
	
	public void setRunButtonImg(int index){
		runButton.setImage(index);
	}
}
