/**
 * 
 */
package com.mmh.vmma.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mmh.vmma.controlcenter.ResponseCode;
import com.mmh.vmma.controlcenter.RestfulLogin;
import com.mmh.vmma.controlcenter.model.request.ReqLogin;
import com.mmh.vmma.controlcenter.model.response.ResLogin;
import com.mmh.vmma.ui.common.CODES;
import com.mmh.vmma.ui.common.Settings;
import com.mmh.vmma.ui.frames.MainWindow;
import com.mmh.vmma.ui.templates.JCommonLabel;
import com.mmh.vmma.ui.templates.JCommonPanel;
import com.mmh.vmma.ui.templates.JCommonTextField;
import com.mmh.vmma.ui.templates.JPKeyboard;
import com.mmh.vmma.ui.templates.JPlaintButton;
import com.mmh.vmma.ui.templates.JRoundButton;

/**
 * @author hongftan
 *
 */
@Component
public class JPUserLogin extends JCommonPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(JPUserLogin.class);

	@Autowired
	private MainWindow mainWindow;
	
	@Autowired
	private RestfulLogin restLogin;
	/**
	 * 用戶登陸頁面窗口
	 */
	
	//一般變量定義
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	//设置日期格式

	private String userID = "";		//用戶輸入或指紋接口返回
	private String empLoginDateTime = "";
	
	//用戶ID登錄窗口
	private JCommonPanel jpUerIdTitle;
	private JCommonPanel jpOptionBar;
	private JPlaintButton btnExit;
	private JCommonLabel lblUserIdShowMessage;
	private JCommonPanel jpLoginInfo;
	private JCommonLabel lblUserID;
	private JCommonTextField txtUserID;
	private JRoundButton btnConfirm;
	
	private JPKeyboard jpKeyboard;

	public JPUserLogin() {
		addComponentListener(new ComponentAdapter() {
			//主窗口顯示時
			@Override
			public void componentShown(ComponentEvent e) {
				resetAttributes();
				txtUserID.requestFocus();
//				try {
//					logger.error("啟動Windows軟鍵盤.");
//					Runtime.getRuntime().exec("cmd /C osk.exe");
//				} catch (IOException e1) {
//					logger.error("啟動Windows軟鍵盤失敗.", e1);
//				}
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
				txtUserID.setText("");
//				try {
//					Runtime.getRuntime().exec("cmd /C taskkill /F /IM osk.exe");
//				} catch (IOException e1) {
//					logger.error("關閉Windows軟鍵盤失敗.", e1);
//				}
			}

			@Override
			public void componentResized(ComponentEvent e) {
				int height = getHeight();
				jpUerIdTitle.setPreferredSize(new Dimension(0, height / 6));
				jpKeyboard.setPreferredSize(new Dimension(0, height / 2));
			}
		});
		
		setLayout(new BorderLayout(0, 0));
		jpUerIdTitle = new JCommonPanel();
		add(jpUerIdTitle, BorderLayout.NORTH);
		jpUerIdTitle.setLayout(new BorderLayout(0, 0));
		
		jpOptionBar = new  JCommonPanel();
		jpOptionBar.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int width = jpOptionBar.getWidth();
				int height = jpOptionBar.getHeight();
				btnExit.setBounds(width - height, 0, height, height);
			}
		});
		jpOptionBar.setLayout(null);
		jpOptionBar.setPreferredSize(new Dimension(0, 30));
		jpUerIdTitle.add(jpOptionBar, BorderLayout.NORTH);
		//退出按鈕選項
		btnExit = new JPlaintButton(""); //退出
		btnExit.setBounds(406, 5, 72, 32);
		btnExit.setForeground(Color.WHITE);
		btnExit.setFont(new Font("黑体", Font.PLAIN, 20));
		btnExit.setBackground(new Color(181, 223, 226));
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
		jpOptionBar.add(btnExit);
		
		lblUserIdShowMessage = new JCommonLabel("請輸入員工編號");
		lblUserIdShowMessage.setFont(new Font("楷体", Font.BOLD, 70));
		lblUserIdShowMessage.setHorizontalAlignment(SwingConstants.CENTER);
		jpUerIdTitle.add(lblUserIdShowMessage, BorderLayout.CENTER);
		
		//用戶名密碼登錄窗口

		
		jpLoginInfo = new JCommonPanel();
		add(jpLoginInfo, BorderLayout.CENTER);
		jpLoginInfo.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int width = jpLoginInfo.getWidth();
				int height = jpLoginInfo.getHeight();
				int x = (width - 700) / 2;
				int y = height / 5 + height / 10 - 32;
				lblUserID.setBounds(x, y, 64, 64);
				x += 100;
				txtUserID.setBounds(x, y, 600, 64);
				txtUserID.setText("");
				txtUserID.requestFocus();
				
				int btnHeight = height * 3 / 10; 
				int btnWidth = 300;
				x += 600 - btnWidth;
				y = height * 3 / 5;
				btnConfirm.setBounds(x, y, btnWidth, btnHeight);

			}
		});
				
		jpLoginInfo.setLayout(null);
		
		lblUserID = new JCommonLabel("");
		lblUserID.setBounds(106, 72, 29, 22);
		lblUserID.setIcon(new ImageIcon("images/account.png"));
		jpLoginInfo.add(lblUserID);
					
		txtUserID = new JCommonTextField();
		txtUserID.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					userID = txtUserID.getText();
					if((userID == null) || (userID.length() <= 0)){
						txtUserID.requestFocus();
						txtUserID.selectAll();
						return;
					}
					//輸入員工號后登陸
	/************************************************************************************************/
					if(!userIDLogin()){
						txtUserID.requestFocus();
						txtUserID.selectAll();
						logger.debug("登入失敗");
						return;
					}
	/************************************************************************************************/
//					try {
//						Runtime.getRuntime().exec("cmd /C taskkill /F /IM osk.exe");
//					} catch (IOException e1) {
//						logger.error("關閉Windows軟鍵盤失敗.", e1);
//					}

					
					logger.debug("输入的用户ID为: {}", userID);
					logger.debug("用戶名密碼登陸成功。");
					mainWindow.checkOptions(Settings.OPTION_MKGENERAL_DRUG, "調劑");
				}
			}
		});
		txtUserID.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				txtUserID.requestFocus();
			}

			@Override
			public void focusGained(FocusEvent e) {
//				txtUserID.selectAll();
			}
		});
		
		txtUserID.setFont(new Font("黑体", Font.BOLD, 40));
		txtUserID.setBounds(27, 64, 500, 64);
		jpLoginInfo.add(txtUserID);
		txtUserID.setColumns(10);
		
		//確認按鈕
		btnConfirm = new JRoundButton("確認");
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!userIDLogin()){
					txtUserID.requestFocus();
					txtUserID.selectAll();
					logger.debug("登入失敗");
					return;
				}
/************************************************************************************************/
//				try {
//					Runtime.getRuntime().exec("cmd /C taskkill /F /IM osk.exe");
//				} catch (IOException e1) {
//					logger.error("關閉Windows軟鍵盤失敗.", e1);
//				}

				logger.debug("用戶名密碼登陸成功。");
				mainWindow.checkOptions(Settings.OPTION_MKGENERAL_DRUG, "調劑");
			}
		});
							
		btnConfirm.setFont(new Font("黑体", Font.BOLD, 30));
		btnConfirm.setIcon(new ImageIcon("images/check.png"));
		btnConfirm.setForeground(Color.WHITE);
		btnConfirm.setBounds(106, 382, 300, 100);
		jpLoginInfo.add(btnConfirm);
		
		jpKeyboard = new JPKeyboard();
		add(jpKeyboard, BorderLayout.SOUTH);
		
	}

	//重設變量
	private void resetAttributes(){
		userID = "";
		empLoginDateTime = "";
		mainWindow.setLoginUser(null);
		lblUserIdShowMessage.setText("請輸入員工編號");
		lblUserIdShowMessage.setForeground(Color.BLACK);
		txtUserID.setText("");
	}
	
	/**
	 * 用户login
	 * */
	private boolean userIDLogin(){

		lblUserIdShowMessage.setForeground(Color.BLACK);
		userID = txtUserID.getText();
		
		if((userID == null) || (userID.length() <= 0)){
			lblUserIdShowMessage.setText("用戶名不能為空");
			lblUserIdShowMessage.setForeground(Color.RED);
			txtUserID.requestFocus();
			txtUserID.selectAll();
			return false;
		}		
		/**后門程序*******************************************************/
		else if((txtUserID.getText() != null) 
				&& (txtUserID.getText().equalsIgnoreCase("onebackdooruser"))){
			userID = "onebackdooruser";
			empLoginDateTime = df.format(new Date());
			return true;
		}
		/****************************************************************/
		logger.info("输入的用户ID为: {}", userID);
		
/************************************************************************************************/
		//登陸
		ReqLogin reqLogin = new ReqLogin();
		reqLogin.getData().setUserName(userID);
		reqLogin.getData().setEncPassword("");
		reqLogin.getData().setLoginType(CODES.LOGIN_TYPE_MK);
		
		try {
			ResLogin resLogin = restLogin.doPost(reqLogin);
			if(ResponseCode.OK != Integer.valueOf(resLogin.getResult())) {
				logger.error("用戶名密碼登錄錯誤, 原因: {}", resLogin.getMessage());
				lblUserIdShowMessage.setText(resLogin.getMessage());
				lblUserIdShowMessage.setForeground(Color.RED);
				return false;
			}
			mainWindow.setLoginUser(resLogin);
			
		}catch(Throwable e) {
			logger.error("用戶名密碼登錄錯誤，userName: [{}]！", userID, e);
			return false;
		}

		
		logger.info("用戶 【{}】登陸成功！", userID);
		
		return true;
	}

	public String getLoginDateTime(){
		return empLoginDateTime;
	}

}
