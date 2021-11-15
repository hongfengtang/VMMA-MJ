/**
 * 
 */
package com.mmh.vmma.ui.templates;

import java.awt.Font;

import javax.swing.JTextField;

/**
 * @author hongftan
 *
 */
public class JCommonTextField extends JTextField {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3181461536719240084L;
	private static final Font TF_FONT = new Font("ו", Font.BOLD, 20);

	public JCommonTextField(){
		initComponent();
	}
	
	public JCommonTextField(String text){
		initComponent();
		setText(text);
	}
	
	private void initComponent(){
		this.setFont(TF_FONT);
	}
	
}
