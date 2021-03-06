/**
 * 
 */
package com.mmh.vmma.utils;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * @author hongftan
 *
 */
public class CommonUtils {
	
	public static String getMacAdd() throws Exception {
		NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
		byte[] macAddr = network.getHardwareAddress();
		String macHexAddr = "";
		for (byte b: macAddr) {
			macHexAddr += toHexString(b);
		}
		return macHexAddr.toUpperCase();
	}
	
	public static String getIPAddress() throws Exception {
		return InetAddress.getLocalHost().getHostAddress();
	}
	public static boolean isIPAddress(String ipaddr) {
		Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
		return pattern.matcher(ipaddr).matches();
		}
	
	public static String toHexString(int integer) {
		String str = Integer.toHexString((int) (integer & 0xff));
		if (str.length() == 1) {
			str = "0" + str;
		}
		return str;
	}
	
	public static String getDateTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		return df.format(new Date());
	}
	
	public static byte[] shortToByteArray(short s) {
	    byte[] targets = new byte[2]; 
	    for (int i = 0; i < 2; i++) { 
	      int offset = (targets.length - 1 - i) * 8; 
	      targets[i] = (byte) ((s >>> offset) & 0xff); 
	    } 
	    return targets; 
	  }

	public static byte[] intByteArray(int res) {
		byte[] targets = new byte[4]; 
	    for (int i = 0; i < 4; i++) { 
	    	int offset = (targets.length - 1 - i) * 8; 
	    	targets[i] = (byte) ((res >>> offset) & 0xff); 
	    } 
	    return targets; 
	}
	public static short bytesToShortLE(byte[] bytes) {
		short number = 0;
	    for(int i = 0; i < 2 ; i++){
	    	number += bytes[i] << i*8;
	    }
	    return number;
	}
	
	public static short bytesToShortBE(byte[] bytes) {
		short number = 0;
	    for(int i = 0; i < 2 ; i++){
	    	number += bytes[i] << (1-i)*8;
	    }
	    return number;
	}
	
	public static int bytesToIntLE(byte[] bytes) {
		int number = 0;
	    for(int i = 0; i < 4 ; i++){
	    	number += bytes[i] << i*8;
	    }
	    return number;
	}
	
	public static int bytesToIntBE(byte[] bytes) {
		int number = 0;
	    for(int i = 0; i < 4 ; i++){
	    	number += bytes[i] << (3-i)*8;
	    }
	    return number;
	}
	 
	@SuppressWarnings("unchecked")
	public static <T> T[] concatAll(T[] first, T[]... rest) {
		int totalLength = first.length;
		
		for (T[] array : rest) {
			totalLength += array.length;
		}
		
		T[] result = Arrays.copyOf(first, totalLength);
		
		int offset = first.length;
		for (T[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		
		return result;
	}

	public static byte[] concatAll(byte[] first, byte[]... rest) {
		int totalLength = first.length;
		
		for (byte[] array : rest) {
			totalLength += array.length;
		}
		
		byte[] result = Arrays.copyOf(first, totalLength);
		
		int offset = first.length;
		for (byte[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		
		return result;
	}
	
    public static ImageIcon createAutoAdjustIcon(Image image, boolean constrained) {
        ImageIcon icon = new ImageIcon(image) {
			private static final long serialVersionUID = 1L;

			@Override
            public synchronized void paintIcon(java.awt.Component cmp, Graphics g, int x, int y) {
                //???????????????
                Point startPoint = new Point(0, 0);//??????????????????
                Dimension cmpSize = cmp.getSize();//??????????????????
                Dimension imgSize = new Dimension(getIconWidth(), getIconHeight());//??????????????????
                
                //???????????????????????????
                if(constrained) {//???????????????
                    //????????????????????????
                    double ratio = 1.0*imgSize.width/imgSize.height;
                    //???????????????????????????????????????
                    imgSize.width = (int) Math.min(cmpSize.width, ratio*cmpSize.height);
                    imgSize.height = (int) (imgSize.width/ratio);
                    //??????????????????
                    startPoint.x = (int) 
                            (cmp.getAlignmentX()*(cmpSize.width - imgSize.width));
                    startPoint.y = (int) 
                            (cmp.getAlignmentY()*(cmpSize.height - imgSize.height));
                } else {//????????????
                    imgSize = cmpSize;
                }
                
                //???????????????????????????????????????
                if(getImageObserver() == null) {
                    g.drawImage(getImage(), startPoint.x, startPoint.y,
                            imgSize.width, imgSize.height, cmp);
                 } else {
                    g.drawImage(getImage(), startPoint.x, startPoint.y,
                            imgSize.width, imgSize.height, getImageObserver());
                 }
            };
        };
        return icon;
    }
    
    public static void setTable(JTable table, DefaultTableModel tblModel, int[] colWidth) {
    	
    	table.setModel(tblModel);
    	for (int i = 0; i < colWidth.length; i++) {
    		if(colWidth[i] <= 0) {
    			table.getTableHeader().getColumnModel().getColumn(i).setMinWidth(0);
    			table.getTableHeader().getColumnModel().getColumn(i).setMaxWidth(0);
    		}else {
    			table.getColumnModel().getColumn(i).setPreferredWidth(colWidth[i]);
    		}
    	}
    	
    }
}
