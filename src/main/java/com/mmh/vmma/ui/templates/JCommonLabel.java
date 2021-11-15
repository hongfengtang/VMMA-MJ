/**
 * 
 */
package com.mmh.vmma.ui.templates;

import java.awt.Font;

import javax.swing.JLabel;

/**
 * @author hongftan
 *
 */
public class JCommonLabel extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8590042973592161024L;
	private static final Font LBL_FONT = new Font("黑體", Font.BOLD, 20);
	
	public JCommonLabel(){
		initComponent();
	}
	public JCommonLabel(String text){
		setText(text);
		initComponent();
	}
	
	private void initComponent(){
		setFont(LBL_FONT);
	}
	

}
