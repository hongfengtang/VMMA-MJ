/**
 * 
 */
package com.mmh.vmma.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mmh.vmma.ui.common.Settings;
import com.mmh.vmma.ui.frames.MainWindow;
import com.mmh.vmma.ui.templates.JCommonLabel;
import com.mmh.vmma.ui.templates.JCommonPanel;
import com.mmh.vmma.ui.templates.JPlaintButton;
import com.mmh.vmma.ui.templates.JRoundButton;

/**
 * @author hongftan
 *
 */
@Component
public class JPFunctions extends JCommonPanel {

	/**
	 * 
	 */
	private static final Logger logger = LoggerFactory.getLogger(JPFunctions.class);
	private static final long serialVersionUID = 1L;
	
	private static final Dimension titleSize = new Dimension(0, 200);
	
	private static final int GAP = 100;
	
	@Autowired 
	MainWindow mainWindow;
	
	@Value("${server.title}")
	private String appName;
	
	private String optionType = "";
	
	//用戶選擇操作類型 1 - 一般取藥, 2 - 退藥, 3 - 緊急取藥, 4 - 補藥, 5 - 盤點, 11 - 管理

	
	private JCommonPanel jpTitle;
	private JCommonPanel jpHeader;
	private JPlaintButton btnExit;				//退出
	private JPlaintButton btnMini;				//最小化窗口按鈕
	private JCommonLabel lblSignOut;			//登出按鈕

	private JCommonLabel lblAppName;
	
	private JCommonPanel jpContent;
	
	private JCommonPanel jpButtons;
	private JRoundButton btnDrugGeneral;		//一般取藥
	private JRoundButton btnDrugEmergency;		//緊急取藥
	private JRoundButton btnDrugTemporary;			//補藥
	private JRoundButton btnDrugManage;			//管理


	public JPFunctions() {
		setBackground(new Color(181, 223, 226));
		setLayout(new BorderLayout(0, 0));
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				mainWindow.resetCounter();
			}
			
		});
		jpTitle = new JCommonPanel();
		jpTitle.setLayout(new BorderLayout(0, 0));
		jpTitle.setPreferredSize(titleSize);
		
		//功能頁面 - 頁頭 - 按鈕
		jpHeader = new JCommonPanel();
		jpHeader.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int width = jpHeader.getWidth();
				int height = jpHeader.getHeight();
				btnExit.setBounds(50, 0, height, height);

				lblSignOut.setBounds(width - lblSignOut.getWidth() - 5, 0, 
						lblSignOut.getWidth(), height);
			}
		});
		jpHeader.setPreferredSize(new Dimension(0, 30));
		jpHeader.setLayout(null);
		jpTitle.add(jpHeader, BorderLayout.NORTH);
		
		btnMini = new JPlaintButton("");
		btnMini.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainWindow.setExtendedState(JFrame.ICONIFIED);
			}
		});
		btnMini.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btnMini.setIcon(new ImageIcon("images/minimize.png"));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				btnMini.setIcon(new ImageIcon(""));
			}
		});
		btnMini.setForeground(Color.WHITE);
		btnMini.setFont(new Font("黑体", Font.PLAIN, 20));
		btnMini.setBackground(new Color(181, 223, 226));
		btnMini.setBounds(0, 0, 30, 30);
		jpHeader.add(btnMini);
		
		lblSignOut = new JCommonLabel("<html>登出</html>");
		lblSignOut.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				lblSignOut.setText("<html><U>登出</U></html>");
			}
			@Override
			public void mouseExited(MouseEvent e) {
				lblSignOut.setText("<html>登出</html>");
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				mainWindow.userLogout();
			}
		});
		lblSignOut.setForeground(Color.BLUE);
		lblSignOut.setHorizontalAlignment(SwingConstants.CENTER);
		lblSignOut.setFont(new Font("黑体", Font.PLAIN, 20));
		lblSignOut.setBounds(0, 0, 60, 30);
		jpHeader.add(lblSignOut);
		
		//退出按鈕選項
		btnExit = new JPlaintButton(""); //退出
		btnExit.setBounds(406, 5, 72, 32);
		btnExit.setForeground(Color.WHITE);
		btnExit.setFont(new Font("黑体", Font.PLAIN, 20));
		btnExit.setBackground(new Color(181, 223, 226));
		jpHeader.add(btnExit);
		btnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btnExit.setIcon(new ImageIcon("images/exitsys.png"));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				btnExit.setIcon(new ImageIcon(""));
			}
		});
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("用戶選擇【退出】按鈕。");
				
				mainWindow.exitSystem();
			}
		});

		
		lblAppName = new JCommonLabel(appName); //Settings.APP_NAME);
		lblAppName.setHorizontalAlignment(SwingConstants.CENTER);
		lblAppName.setForeground(Color.BLACK);
		lblAppName.setFont(new Font("楷体", Font.BOLD, 50));
		
		jpTitle.add(lblAppName);
		add(jpTitle, BorderLayout.NORTH);
		
		jpContent = new JCommonPanel();
		jpContent.setLayout(new BorderLayout(0,0));
		add(jpContent, BorderLayout.CENTER);
		
		JCommonPanel jpEast = new JCommonPanel();
		jpEast.setPreferredSize(new Dimension(GAP, 0));
		jpContent.add(jpEast, BorderLayout.EAST);
		
		JCommonPanel jpWest = new JCommonPanel();
		jpWest.setPreferredSize(new Dimension(GAP, 0));
		jpContent.add(jpWest, BorderLayout.WEST);
		
		JCommonPanel jpSouth = new JCommonPanel();
		jpSouth.setPreferredSize(new Dimension(0, GAP));
		jpContent.add(jpSouth, BorderLayout.SOUTH);
//		
//		JCommonPanel jpNorth = new JCommonPanel();
//		jpNorth.setPreferredSize(new Dimension(0, GAP));
//		jpContent.add(jpNorth, BorderLayout.NORTH);
		
		jpButtons =  new JCommonPanel();
		jpButtons.setLayout(new GridLayout(0, 2, GAP, GAP));
		jpContent.add(jpButtons, BorderLayout.CENTER);
		
		//功能頁面 - 按鈕區 - 一般取藥按鈕選項
		btnDrugGeneral = new JRoundButton("一般取藥");
		btnDrugGeneral.setIcon(new ImageIcon("images/medicines.png"));
		btnDrugGeneral.setForeground(Color.WHITE);
		btnDrugGeneral.setFont(new Font("黑体", Font.PLAIN, 25));
		btnDrugGeneral.setBackground(new Color(0, 145,58));
		btnDrugGeneral.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("用戶選擇【一般取藥】按鈕。");
				optionType = "一般取藥";
				mainWindow.checkOptions(Settings.OPTION_GENERAL_DRUG, optionType);
			}
		});
		jpButtons.add(btnDrugGeneral);

		//功能頁面 - 按鈕區 - 緊急取藥按鈕選項
		btnDrugEmergency = new JRoundButton("緊急取藥");
		btnDrugEmergency.setIcon(new ImageIcon("images/dispensing.png"));
		btnDrugEmergency.setForeground(Color.WHITE);
		btnDrugEmergency.setFont(new Font("黑体", Font.PLAIN, 25));
		btnDrugEmergency.setBackground(new Color(0, 145,58));
		btnDrugEmergency.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("用戶選擇【緊急取藥】按鈕。");
				optionType = "緊急取藥";
				mainWindow.checkOptions(Settings.OPTION_EMERGENCY_DRUG, optionType);
			}
		});
		jpButtons.add(btnDrugEmergency);
		
		//功能頁面 - 按鈕區 - 臨時取藥按鈕選項
		btnDrugTemporary = new JRoundButton("臨時取藥");
		btnDrugTemporary.setIcon(new ImageIcon("images/dispensing.png"));
		btnDrugTemporary.setForeground(Color.WHITE);
		btnDrugTemporary.setFont(new Font("黑体", Font.PLAIN, 25));
		btnDrugTemporary.setBackground(new Color(0, 145,58));
		btnDrugTemporary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("用戶選擇【臨時取藥】按鈕。");
				optionType = "臨時取藥";
				mainWindow.checkOptions(Settings.OPTION_TEMPORARY_DRUG, optionType);
			}
		});
		jpButtons.add(btnDrugTemporary);

		//功能頁面 - 按鈕區 - 管理按鈕選項
		btnDrugManage = new JRoundButton("管理");
		btnDrugManage.setIcon(new ImageIcon("images/manager.png"));
		btnDrugManage.setForeground(Color.WHITE);
		btnDrugManage.setFont(new Font("黑体", Font.PLAIN, 25));
		btnDrugManage.setBackground(new Color(0, 145,58));
		btnDrugManage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("用戶選擇【管理】按鈕。");
				optionType = "管理";
				mainWindow.checkOptions(Settings.OPTION_MANAGE, optionType);
			}
		});
		jpButtons.add(btnDrugManage);

	}
}
