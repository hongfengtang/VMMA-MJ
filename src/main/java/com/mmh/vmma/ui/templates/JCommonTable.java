/**
 * 
 */
package com.mmh.vmma.ui.templates;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

/**
 * @author hongftan
 *
 */
public class JCommonTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2064457516515797376L;

	public JCommonTable(){
        getTableHeader().setPreferredSize(new Dimension(0, 50));
        getTableHeader().setBackground(new Color(135, 206,250));
        setFont(new Font("ו", Font.PLAIN, 20));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setResizingAllowed(true);
        getTableHeader().setFont(new Font("ו", Font.BOLD, 20));
        setRowHeight(50);
        setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        
        setAutoCreateRowSorter(true);

	}
	/* (non-Javadoc)
	 * @see javax.swing.JTable#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}


}
