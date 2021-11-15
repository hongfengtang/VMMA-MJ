package com.mmh.vmma.ui.templates;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

public class JPlaintButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4030987110389780868L;
	private static final Font BT_FONT = new Font("ו", Font.BOLD, 20);
	private static final Color BT_BG_COLOR = new Color(0xf0,0xf0,0xf0);
	
	public JPlaintButton(){
		initButton();
	}
	public JPlaintButton(String strText){
		setText(strText);
		initButton();
	}
	private void initButton(){
		setBorderPainted(false);
		setContentAreaFilled(false);
		setFont(BT_FONT);
		setToolTipText(getText());
		setBackground(BT_BG_COLOR);
		
		addMouseListener(new MouseAdapter() {
			// TODO Auto-generated method stub
			public void mouseEntered(MouseEvent e) {

//				setContentAreaFilled(true);
				setBorderPainted(true);
			}

			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
//				setContentAreaFilled(false);
				setBorderPainted(false);
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



	
}
