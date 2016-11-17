package view.buttons;

import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class ChangeableImageButton extends ImageButton {
	private static final long serialVersionUID = 1L;

	private byte currImg = 1;
	private Image disImg1, enImg1, hovImg1, disImg2, enImg2, hovImg2;
	public ChangeableImageButton(String name1, String name2, ActionListener clickListener) {
		super(name1, clickListener);
		disImg1 = disImg;
		enImg1 = enImg;
		hovImg1 = hovImg;
		URL disUrl = getClass().getResource("/res/"+name2+"_disabled.png");
		URL enUrl = getClass().getResource("/res/"+name2+"_enabled.png");
		URL hovUrl = getClass().getResource("/res/"+name2+"_hovered.png");
		try {
			disImg2 = ImageIO.read(disUrl);
			enImg2 = ImageIO.read(enUrl);
			hovImg2 = ImageIO.read(hovUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void swapButton() {
		setImage(currImg == 2 ? 1 : 2);
	}
	
	public void setImage(int index){
		if(index == 2){
			currImg = 2;
			disImg = disImg2;
			enImg = enImg2;
			hovImg = hovImg2;
		}else{
			currImg = 1;
			disImg = disImg1;
			enImg = enImg1;
			hovImg = hovImg1;
		}
		repaint();
	}
}
