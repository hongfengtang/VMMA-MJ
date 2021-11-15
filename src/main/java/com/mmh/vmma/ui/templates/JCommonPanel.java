package com.mmh.vmma.ui.templates;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class JCommonPanel extends JPanel {

	private static final long serialVersionUID = -1897919523476704827L;
	/**
	 * Create the panel.
	 */
	private ImageIcon img = null;
	
	public JCommonPanel() {
		setBackground(new Color(181, 223, 226));
		setOpaque(true);
	}
	public void setBackPicture() {
		img = null;
		setBackground(new Color(181, 223, 226));
		setOpaque(true);
		repaint();
	}
	
	public void setBackPicture(String imgPath){
		File f = new File(imgPath);
		if(f.exists()){
			setOpaque(false);
			img = new ImageIcon(imgPath);
		}
		repaint();
	}
	
	public void setBackPicture(byte[] imgBytes){
		if(imgBytes.length > 0) {
			setOpaque(false);
			img = new ImageIcon(imgBytes);
		}
		repaint();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
//		ImageIcon img = new ImageIcon("images/bg_login.png");//new ImageIcon("Images/Lighthouse.jpg");//new ImageIcon("images/bg_login.png");
		if(img != null)
			g.drawImage(img.getImage(),0, 0, getWidth(), getHeight(), img.getImageObserver());

	}

}
