package com.mmh.vmma.ui.templates;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JButton;

public class JRoundButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4030987110389780868L;
	private static final Font BT_FONT = new Font("ו", Font.BOLD, 20);
	private static final Color BT_BG_COLOR = new Color(0, 145,58); //Color(0xf0,0xf0,0xf0);
	
    public static final Color BUTTON_COLOR1 = new Color(205, 255, 205);  
    public static final Color BUTTON_COLOR2 = new Color(51, 154, 47);  
    public static final Color BUTTON_FOREGROUND_COLOR = Color.WHITE;  
    private boolean hover = false;  

    public JRoundButton(){
		initButton();
	}
	public JRoundButton(String strText){
		setText(strText);
		initButton();
	}
	private void initButton(){
		setBorderPainted(false);
		setFocusPainted(false);
		setContentAreaFilled(false);
		setFont(BT_FONT);
		setToolTipText(getText());
		setBackground(BT_BG_COLOR);
		
		addMouseListener(new MouseAdapter() {
			// TODO Auto-generated method stub
			public void mouseEntered(MouseEvent e) {

//				setContentAreaFilled(true);
//				setBorderPainted(true);
                setForeground(BUTTON_FOREGROUND_COLOR);  
                hover = true;  
                repaint();  
			}

			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
//				setContentAreaFilled(false);
//				setBorderPainted(false);
                setForeground(BUTTON_FOREGROUND_COLOR);  
                hover = false;  
                repaint();  
			}

			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				setFocusable(false);
			}
			
		});
	}

    @Override  
    protected void paintComponent(Graphics g) {  
        Graphics2D g2d = (Graphics2D) g.create();  
        int h = getHeight();  
        int w = getWidth();  
        float tran = 1F;  
        if (!hover) {  
            tran = 0.3F;  
        }  
  
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
                RenderingHints.VALUE_ANTIALIAS_ON);  
        GradientPaint p1;  
        GradientPaint p2;  
        if (getModel().isPressed()) {  
            p1 = new GradientPaint(0, 0, new Color(0, 0, 0), 0, h - 1,  
                    new Color(100, 100, 100));  
            p2 = new GradientPaint(0, 1, new Color(0, 0, 0, 50), 0, h - 3,  
                    new Color(255, 255, 255, 100));  
        } else {  
            p1 = new GradientPaint(0, 0, new Color(100, 100, 100), 0, h - 1,  
                    new Color(0, 0, 0));  
            p2 = new GradientPaint(0, 1, new Color(255, 255, 255, 100), 0,  
                    h - 3, new Color(0, 0, 0, 50));  
        }  
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,  
                tran));  
        RoundRectangle2D.Float r2d = new RoundRectangle2D.Float(0, 0, w - 1,  
                h - 1, 20, 20);  
        Shape clip = g2d.getClip();  
        g2d.clip(r2d);  
        GradientPaint gp = new GradientPaint(0.0F, 0.0F, BT_BG_COLOR, 0.0F,  
                0, BT_BG_COLOR, true);  
        g2d.setPaint(gp);  
        g2d.fillRect(0, 0, w, h);  
        g2d.setClip(clip);  
        g2d.setPaint(p1);  
        g2d.drawRoundRect(0, 0, w - 1, h - 1, 20, 20);  
        g2d.setPaint(p2);  
        g2d.drawRoundRect(1, 1, w - 3, h - 3, 18, 18);  
        g2d.dispose();  
        super.paintComponent(g);  
    }  
}
