package com.mmh.vmma.ui.frames;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mmh.vmma.controlcenter.ResponseCode;
import com.mmh.vmma.controlcenter.RestfulLogout;
import com.mmh.vmma.controlcenter.model.request.ReqLogout;
import com.mmh.vmma.controlcenter.model.response.ResLogin;
import com.mmh.vmma.controlcenter.model.response.ResLogout;
import com.mmh.vmma.ui.common.CODES;
import com.mmh.vmma.ui.common.Settings;
import com.mmh.vmma.ui.dialogs.InitSystem;
import com.mmh.vmma.ui.panels.JPMKDrugsStatus;
import com.mmh.vmma.ui.panels.JPMKGeneralProvide;
import com.mmh.vmma.ui.panels.JPUserLogin;
import com.mmh.vmma.ui.panels.JPVMConfig;
import com.mmh.vmma.ui.templates.JCommonLabel;
import com.mmh.vmma.ui.templates.JCommonPanel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

@Component
public class MainWindow extends JFrame {
	
	/**
	 * 
	 */
	private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);
	private static final long serialVersionUID = 1L;
	
	@Autowired
	RestfulLogout restLogout;
	@Autowired
	private InitSystem initSystem;
	
	@Autowired
	private JPUserLogin jpLoginPage;				//登錄窗口
	@Autowired
	private JPVMConfig jpVMConfigPage;				//配置窗口
	@Autowired
	private JPMKGeneralProvide jpMKGeneralProvide;  //馬偕調劑窗口
	@Autowired
	private JPMKDrugsStatus jpMKDrugsStatus;		//馬偕藥品狀態窗口
	
	@Value("${server.type}")
	private String svrType;
	@Value("${server.title}")
	private String title;
	
	private ResLogin loginUser = null;

	private JCommonPanel contentPane;
	
	//窗口操作區
	private JCommonPanel cardPanel;
	private CardLayout card;
	private JCommonPanel jpTmp;					//用於保證頁面顯示時，調用show PAGE_TMP
	//窗口狀態欄
	private JCommonPanel jpStatus;
	private JCommonLabel lblLoginID;
	private JCommonLabel lblLoginTime;
	private JCommonLabel lblCurrentTime;

	private int countSignoutTime = 0;
	private boolean signoutCounter = false;
	
	private boolean isExiting = false;


	/**
	 * Create the frame.
	 */
	
	public MainWindow() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				
				cardPanel.add(Settings.PAGE_MANAGE, jpVMConfigPage);
				cardPanel.add(Settings.PAGE_LOGIN, jpLoginPage);
				cardPanel.add(Settings.PAGE_MKGENERAL_DRUG, jpMKGeneralProvide);
				cardPanel.add(Settings.PAGE_MKDRUGS_STATUS, jpMKDrugsStatus);
				pagesReturn();
				
				if(initSystem.getResult() == CODES.SUCCESS) {
					jpStatus.setPreferredSize(new Dimension(0, 0));
					card.show(cardPanel, Settings.PAGE_TMP);
					card.show(cardPanel, Settings.PAGE_LOGIN);
				}else {
					jpStatus.setPreferredSize(new Dimension(0, Settings.STATUS_HEIGHT));
					card.show(cardPanel, Settings.PAGE_TMP);
					card.show(cardPanel, Settings.PAGE_MANAGE);
				}
				validate();
			}
		});
		
		setTitle(title);
//		setIconImage(Toolkit.getDefaultToolkit().getImage(logo));
		
		/*******初始化窗口*******/
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);//EXIT_ON_CLOSE);
		
//		setAlwaysOnTop(true);
		
		//是否在任务栏显示窗口
		setType(Type.UTILITY);
		
		setResizable(false);
		
		//获取当前显示器分辨率
		Dimension   sc = Toolkit.getDefaultToolkit().getScreenSize();
		//设置窗口全屏
		
		setBounds(0, 0, sc.width, sc.height);
		setBounds(0, 0, 1280, 800);
		
		//设置窗口无边框
		setUndecorated(true);

		//窗口居中显示
		setLocationRelativeTo(null);

		contentPane = new JCommonPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		//設置顯示區，使用CardLayout方法 - Center
		cardPanel = new JCommonPanel();
		contentPane.add(cardPanel, BorderLayout.CENTER);
		card =  new CardLayout();
		cardPanel.setLayout(card);

		//用於功能頁面顯示頁
		jpTmp = new JCommonPanel();
		cardPanel.add(Settings.PAGE_TMP, jpTmp);
		

		//狀態欄定義 - south
		jpStatus = new JCommonPanel();
//		jpStatus.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		jpStatus.setPreferredSize(new Dimension(0, Settings.STATUS_HEIGHT));
		jpStatus.setLayout(null);
		
		lblLoginID = new JCommonLabel(""); 
		lblLoginID.setVisible(false);
		lblLoginID.setBounds(5, 2, 250, 26);
		lblLoginID.setFont(new Font("宋体", Font.PLAIN, 12));
		jpStatus.add(lblLoginID);
		
		lblLoginTime = new JCommonLabel(""); 
		lblLoginTime.setVisible(false);
		lblLoginTime.setBounds(260, 2, 250, 26);
		lblLoginTime.setFont(new Font("宋体", Font.PLAIN, 12));
		jpStatus.add(lblLoginTime);
		
		lblCurrentTime = new JCommonLabel("當前時間：");
		lblCurrentTime.setVisible(false);;
		lblCurrentTime.setBounds(600, 2, 250, 26);
		lblCurrentTime.setFont(new Font("宋体", Font.PLAIN, 12));
		jpStatus.add(lblCurrentTime);
		add(jpStatus, BorderLayout.SOUTH);
		
		jpStatus.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int nHight = jpStatus.getSize().height;
				int nWidth = jpStatus.getSize().width;
				lblLoginID.setBounds(5, 2, 250, nHight -4);
				lblLoginTime.setBounds(260, 2, 250, nHight -4);
				lblCurrentTime.setBounds(nWidth - 200, 2, 200, nHight -4);
			}
		});
		
		card.show(cardPanel, Settings.PAGE_TMP);
	}
	
	private void pagesReturn() {
		//登錄頁面完成
		jpLoginPage.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentHidden(ComponentEvent e) {
//				if(loginUser != null) {
//					card.show(cardPanel, Settings.PAGE_TMP);
//					card.show(cardPanel, Settings.PAGE_MKGENERAL_DRUG);
//				}
			}
		
		});
	}

	/**
	 * 功能按鈕選擇功能時 - 處理用戶選擇的選項
	 * */
	public void checkOptions(int option, String optionType){
		switch (option){
		case Settings.OPTIION_LOGIN:
			loginUser= null;
			jpStatus.setPreferredSize(new Dimension(0, 0));
			card.show(cardPanel,  Settings.PAGE_TMP);
			card.show(cardPanel,  Settings.PAGE_LOGIN);
			break;
		//管理
		case Settings.OPTION_MANAGE:
///***************************************************************************/
//			if(!(userAuth.equalsIgnoreCase("9999") 
//					|| userAuth.equalsIgnoreCase("1001"))){
//				JOptionPane.showMessageDialog(null,  "您當前權限等級不足,無權補藥.", 
//						"錯誤", JOptionPane.ERROR_MESSAGE);
//				break;
//			}
///***************************************************************************/
//			jpBatchSupply.setTitle(" 補藥 - ");;
//			jpBatchSupply.setEmpNo(userID);
//			jpBatchSupply.setEmpName(userName);
////			jpBatchSupply.setEmpAuth(String.valueOf(auth));
//			jpBatchSupply.setMedType(optionType);
//			jpBatchSupply.setOption(String.valueOf(option));
//			jpBatchSupply.setOptionKey(optionKey, optionKeySeqNo);
//			card.show(cardPanel, PAGE_DRUGINFO);
			card.show(cardPanel, Settings.PAGE_TMP);
			card.show(cardPanel, Settings.PAGE_MANAGE);
			break;
			
		//馬偕調劑
		case Settings.OPTION_MKGENERAL_DRUG:
			card.show(cardPanel, Settings.PAGE_TMP);
			card.show(cardPanel, Settings.PAGE_MKGENERAL_DRUG);
			break;
		
		//馬偕藥品狀態
		case Settings.OPTION_MKDRUGS_STATUS:
			card.show(cardPanel, Settings.PAGE_TMP);
			card.show(cardPanel, Settings.PAGE_MKDRUGS_STATUS);
			break;
			
		//退出
		case 90:
///***************************************************************************/
//			if(!(userAuth.equalsIgnoreCase("9999") 
//					|| userAuth.equalsIgnoreCase("1001")
//					|| userAuth.equalsIgnoreCase("1002"))){
//				JOptionPane.showMessageDialog(null,  "您當前權限等級不足,無法退出系統.", 
//						"錯誤", JOptionPane.ERROR_MESSAGE);
//				break;
//			}
///***************************************************************************/
			exitSystem();
			break;
		default:
			break;
		}
	}
	
	public void showFunctionPage() {
		card.show(cardPanel, Settings.PAGE_TMP);
		card.show(cardPanel, Settings.PAGE_FUNCTIONS);
	}
	
	public void resetCounter(){
		countSignoutTime = 0;
		signoutCounter = true;
	}
	
	public void stopCounter(){
		signoutCounter = false;
	}
	
	public void setLoginUser(ResLogin loginUser) {
		this.loginUser = loginUser;
	}
	
	public String getUserName() {
		return loginUser.getData().getUserName() == null ? "" : loginUser.getData().getUserName().trim();
	}
	
	public String getUserId() {
		return loginUser.getData().getUserId().trim();
	}
	
	public String getUserToken() {
		return loginUser.getData().getToken();
	}
	
	public void userLogout() {
		jpStatus.setPreferredSize(new Dimension(0, 0));
		card.show(cardPanel, Settings.PAGE_TMP);
		card.show(cardPanel, Settings.PAGE_LOGIN);
		stopCounter();
		ReqLogout reqLogout = new ReqLogout();
		reqLogout.setToken(loginUser.getData().getToken());;
		try {
			ResLogout resLout = restLogout.doPost(reqLogout);
			if(ResponseCode.OK != Integer.valueOf(resLout.getResult())) {
				logger.error("用戶[{}]登出系統錯誤, 原因: {}",loginUser.getData().getUserId(), resLout.getMessage());
			}
		}catch(Throwable e) {
			logger.error("用戶[{}]登出系統錯誤!",loginUser.getData().getUserName(), e);
			return;
		}
		logger.info("用戶 【{}】登出系統成功！",loginUser.getData().getUserId());
		
	}
	
	public void exitSystem() {
		if(loginUser != null) {
			if(JOptionPane.showConfirmDialog(null,  "確認退出系統？", "提示", 
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION){
				return;
			}
			userLogout();
			isExiting = true;
		}
		System.exit(0);
	}

}
