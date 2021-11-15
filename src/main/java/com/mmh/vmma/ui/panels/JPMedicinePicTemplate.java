/**
 * 
 */
package com.mmh.vmma.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import com.mmh.vmma.ui.templates.JCommonLabel;
import com.mmh.vmma.ui.templates.JCommonPanel;

/**
 * @author hongftan
 *
 */
public class JPMedicinePicTemplate extends JCommonPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int NAME_HEIGHT = 30;
	
	private JCommonLabel lblMedicinePhoto;
	private JCommonLabel lblMedicineName;
	
	public JPMedicinePicTemplate() {
		
		addComponentListener(new ComponentAdapter(){

			@Override
			public void componentResized(ComponentEvent e) {
				lblMedicineName.setPreferredSize(new Dimension(0, NAME_HEIGHT));
				
			}
			
		});
		setLayout(new BorderLayout(0, 0));
		
		lblMedicinePhoto = new JCommonLabel();
		lblMedicinePhoto.setAlignmentX(0.5F);
		lblMedicinePhoto.setAlignmentY(0.5F);
		add(lblMedicinePhoto, BorderLayout.CENTER);
		
		lblMedicineName = new JCommonLabel("tst");
		lblMedicineName.setHorizontalAlignment(SwingConstants.CENTER);
		lblMedicineName.setForeground(new Color(220, 20, 60));
		lblMedicineName.setFont(new Font("楷体", Font.PLAIN, 20));
		lblMedicineName.setOpaque(false);
		lblMedicineName.setPreferredSize(new Dimension(0, 100));
		add(lblMedicineName, BorderLayout.SOUTH);
	}
	
	public void setPhoto(ImageIcon photo) {
		lblMedicinePhoto.setIcon(createAutoAdjustIcon(photo.getImage(), true));
	}
	
	public void setPhoto(String photo) {
		lblMedicinePhoto.setIcon(createAutoAdjustIcon(photo, true));
	}

	public void setPhoto(URL photo) {
		lblMedicinePhoto.setIcon(createAutoAdjustIcon(photo, true));
	}

	public void setName(String name) {
		lblMedicineName.setText(name);
	}

    private ImageIcon createAutoAdjustIcon(Image image, boolean constrained) {
        ImageIcon icon = new ImageIcon(image) {
			private static final long serialVersionUID = 1L;

			@Override
            public synchronized void paintIcon(java.awt.Component cmp, Graphics g, int x, int y) {
                //初始化参数
                Point startPoint = new Point(0, 0);//默认绘制起点
                Dimension cmpSize = cmp.getSize();//获取组件大小
                Dimension imgSize = new Dimension(getIconWidth(), getIconHeight());//获取图像大小
                
                //计算绘制起点和区域
                if(constrained) {//等比例缩放
                    //计算图像宽高比例
                    double ratio = 1.0*imgSize.width/imgSize.height;
                    //计算等比例缩放后的区域大小
                    imgSize.width = (int) Math.min(cmpSize.width, ratio*cmpSize.height);
                    imgSize.height = (int) (imgSize.width/ratio);
                    //计算绘制起点
                    startPoint.x = (int) 
                            (cmp.getAlignmentX()*(cmpSize.width - imgSize.width));
                    startPoint.y = (int) 
                            (cmp.getAlignmentY()*(cmpSize.height - imgSize.height));
                } else {//完全填充
                    imgSize = cmpSize;
                }
                
                //根据起点和区域大小进行绘制
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
    
    /**创建一个可以自适应组件大小的Icon对象
     **/
    private ImageIcon createAutoAdjustIcon(String filename, boolean constrained) {
        return createAutoAdjustIcon(new ImageIcon(filename).getImage(), constrained);
    }
    
    /**创建一个可以自适应组件大小的ImageIcon对象
     **/
    private ImageIcon createAutoAdjustIcon(URL url, boolean constrained) {
        return createAutoAdjustIcon(new ImageIcon(url).getImage(), constrained);
    }
}
