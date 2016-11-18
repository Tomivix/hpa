package view.buttons;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import view.View;

public class ImageButton extends JComponent implements MouseListener {
	private static final long serialVersionUID = 1L;
	
	private final static byte ENABLED = 0, DISABLED = 1, HOVERED = 2;
	
	private int width = 10, height = 10;
	protected Image disImg, enImg, hovImg;
	private byte currState = ENABLED;
	private AffineTransform scaleTransform;
	private ActionListener clickListener;
	private String name;
	public ImageButton(String name, ActionListener clickListener) {
		this.name = name;
		this.clickListener = clickListener;
		scaleTransform = AffineTransform.getScaleInstance(View.IMAGE_BUTTON_SCALE, View.IMAGE_BUTTON_SCALE);
		URL disUrl = getClass().getResource("/res/"+name+"_disabled.png");
		URL enUrl = getClass().getResource("/res/"+name+"_enabled.png");
		URL hovUrl = getClass().getResource("/res/"+name+"_hovered.png");
		try {
			disImg = ImageIO.read(disUrl);
			enImg = ImageIO.read(enUrl);
			hovImg = ImageIO.read(hovUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
		width = (int) (disImg.getWidth(null) * View.IMAGE_BUTTON_SCALE);
		height = (int) (disImg.getHeight(null) * View.IMAGE_BUTTON_SCALE);
		super.addMouseListener(this);
		super.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(width, height);
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		
		Image imgToDraw = disImg;
		switch(currState){
		case ENABLED:
			imgToDraw = enImg;
			break;
		case DISABLED:
			imgToDraw = disImg;
			break;
		case HOVERED:
			imgToDraw = hovImg;
			break;
		}
		g2.drawImage(imgToDraw, scaleTransform, null);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(currState != DISABLED){
			clickListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, name));
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if(currState != DISABLED){
			currState = HOVERED;
		}
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if(currState != DISABLED){
			currState = ENABLED;
		}
		repaint();
	}
	
	public void setEnabled(boolean enabled){
		currState = enabled ? ENABLED : DISABLED;
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
	
}
