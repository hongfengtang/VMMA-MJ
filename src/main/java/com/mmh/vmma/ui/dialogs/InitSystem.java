package com.mmh.vmma.ui.dialogs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mmh.vmma.controlcenter.RestfulDeviceInit;
import com.mmh.vmma.controlcenter.model.request.ReqDeviceInit;
import com.mmh.vmma.controlcenter.model.response.ResDeviceInit;
import com.mmh.vmma.jpa.entities.SystemConfig;
import com.mmh.vmma.jpa.entities.SystemConfig.SystemConfigId;
import com.mmh.vmma.jpa.repositories.SystemConfigRepository;
import com.mmh.vmma.mina.socket.signage.SignageIFClient;
import com.mmh.vmma.systemvalue.CenterControl;
import com.mmh.vmma.systemvalue.VMConfiguration;
import com.mmh.vmma.ui.common.CODES;
import com.mmh.vmma.ui.common.GlobalData;
import com.mmh.vmma.ui.common.Settings;
import com.mmh.vmma.ui.templates.JCommonLabel;
import com.mmh.vmma.utils.CommonUtils;
import com.mmh.vmma.utils.FastJsonUtils;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


@Component
public class InitSystem extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2913133874198131157L;
	
	private final Logger logger = LoggerFactory.getLogger(InitSystem.class);
	
	private final Rectangle BOUNDS = new Rectangle(50, 100, 350, 80);
	
	private final Font LBL_FONT = new Font("楷体", Font.PLAIN, 20);
	private final Rectangle LBL_BOUNDS = new Rectangle(0, 0, 350, 49);
	
	private final Color PROGRESSBAR_BG = new Color(255, 240, 245);
	private final Rectangle PROGRESSBAR_BOUNDS = new Rectangle(10, 59, 330, 14);
	
	/**
	 * Launch the application.
	 */
//	@Autowired
//	MainWindow mainWindow;
	
	@Autowired
	SystemConfigRepository sysRepo;
	
	@Autowired
	SignageIFClient signageClient;
	
	@Autowired
	GlobalData gData;
	
	@Autowired
	RestfulDeviceInit restClient;
	
	private JProgressBar pbInitSsytem;
	private JCommonLabel lblInformation;
	private int result = CODES.SUCCESS;

	/**
	 * Create the dialog.
	 */
	public InitSystem() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
		  		char ekey = e.getKeyChar();
		  		logger.debug("{}", ekey);
		  		
		  		if(ekey == 27) {
		  			if(JOptionPane.showConfirmDialog(null,  "您確定要退出嗎？","警告",
		  					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION) {
		  				return;
		  			}
		  			System.exit(0);
		  		}
			}
		});
		getContentPane().setBackground(Settings.BACKGROUND);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				//進度條顯示
				new Thread(new Runnable() {
					public void run() {
						initSetting();
						dispose();
//						mainWindow.setVisible(true);
					}
				}).start();
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
//				lblInformation.setForeground(Color.BLACK);
//				new Thread(new Runnable() {
//					public void run() {
//						initSetting();
//						dispose();
//						mainWindow.setVisible(true);
//					}
//				}).start();
			}
		});
		
		setBounds(BOUNDS);
//		setAlwaysOnTop(true);
		setUndecorated(true);
		setType(Type.UTILITY);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		lblInformation = new JCommonLabel("正在初始化，請稍後……");
		lblInformation.setHorizontalAlignment(SwingConstants.CENTER);
		lblInformation.setFont(LBL_FONT);
		lblInformation.setBounds(LBL_BOUNDS);
		getContentPane().add(lblInformation);
		
		pbInitSsytem = new JProgressBar();
		pbInitSsytem.setBackground(PROGRESSBAR_BG);
		pbInitSsytem.setStringPainted(true);
		pbInitSsytem.setBounds(PROGRESSBAR_BOUNDS);
		getContentPane().add(pbInitSsytem);
	}
	
	/**
	 * 初始化系统，读数据库配置信息，并初始化
	 * @throws InterruptedException 
	 * */
	private void initSetting() {
		
		int nJBPercent = 0;
		pbInitSsytem.setValue(nJBPercent);
		logger.info("讀取藥品櫃配置數據……");
		lblInformation.setText("讀取藥品櫃配置數據……");
		//1. 讀取數據庫配置信息
		Optional<SystemConfig> cabinetConfig = Optional.ofNullable(sysRepo.findById(SystemConfigId.cabinet.toString())).orElse(null);
		nJBPercent = nJBPercent + 5;
		pbInitSsytem.setValue(nJBPercent);
		//系統沒有初始化
		if(!cabinetConfig.isPresent()) {
			logger.warn("讀取藥品櫃配置錯誤，系統沒有初始化。");
			lblInformation.setText("讀取藥品櫃配置錯誤，系統沒有初始化。");
			lblInformation.setForeground(Color.RED);
//			JOptionPane.showMessageDialog(null,  "讀取藥品櫃配置錯誤，系統沒有初始化。","錯誤",
//					JOptionPane.ERROR_MESSAGE);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				logger.error("Thread sleep error: ", e);
				e.printStackTrace();
			}
			setResult(CODES.ERR_NO_CABINET);
			return;
		}

		lblInformation.setText("讀取中央控管配置數據……");
		Optional<SystemConfig> ccConfig = Optional.ofNullable(sysRepo.findById(SystemConfigId.control_center.toString())).orElse(null);
		nJBPercent = nJBPercent + 5;
		pbInitSsytem.setValue(nJBPercent);
		//中央控管連接信息沒有配置
		if(!ccConfig.isPresent()) {
			logger.warn("讀取中央控管配置錯誤，訊息沒有配置。");
			lblInformation.setText("讀取中央控管配置錯誤，訊息沒有配置。");
			lblInformation.setForeground(Color.RED);
//			JOptionPane.showMessageDialog(null,  "讀取中央控管配置錯誤，信息沒有配置。","錯誤",
//					JOptionPane.ERROR_MESSAGE);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				logger.error("Thread sleep error: ", e);
				e.printStackTrace();
			}
			setResult(CODES.ERR_NO_CENTER_CONTROL);
			return;
		}

		//2. 解析連接販賣機信息
		lblInformation.setText("解析藥品櫃配置數據……");
		nJBPercent = nJBPercent + 5;
		pbInitSsytem.setValue(nJBPercent);
		VMConfiguration vmConfigurations = FastJsonUtils.deserializeObject(cabinetConfig.get().getValue(), VMConfiguration.class);
		gData.setCustoerName(vmConfigurations.getCustomer());
		gData.setTerminalId(vmConfigurations.getTerminalid());
		
		vmConfigurations.getPortModes().forEach(portMode -> {
			int nTmpJBPercent = 0;
			switch (portMode.getMode()) {
				case Settings.SERIAL:
					logger.warn("串口端口類型正在建設中。");
					break;
					
				case Settings.TCP_CLIENT:
					logger.info("TCP/IP客戶端連接類型。");
					signageClient.connect(portMode.getAddress(), Integer.valueOf(portMode.getPortNumber()));
					int time = 0;
					while(signageClient.getStatus() == CODES.TCP_CLIENT_STATUS.CONNECTING) {
						pbInitSsytem.setValue(nTmpJBPercent);
						lblInformation.setText("正在連接Signage......");
						try {
							nTmpJBPercent = nTmpJBPercent > 95 ? nTmpJBPercent : nTmpJBPercent + 1;
							time++;
							if(time > 120) {
								setResult(CODES.ERR_CONNECT_VM);
								return;
							}
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					while (nTmpJBPercent <= 100) {
						lblInformation.setText("連接Signage成功！");
						pbInitSsytem.setValue(nTmpJBPercent);
						nTmpJBPercent++;
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					break;
					
				case Settings.TCP_SERVER:
					logger.warn("TCP/IP服務端正在建設中。");
					break;
					
				default:
					logger.error("不支持的端口類型！{}", portMode.getMode());
					lblInformation.setText("不支持的端口類型！");
					lblInformation.setForeground(Color.RED);
//					JOptionPane.showMessageDialog(null,  "不支持的端口類型！","錯誤",
//							JOptionPane.ERROR_MESSAGE);
					break;
			}
		});
		
		lblInformation.setText("解析中央控管配置數據……");
		nJBPercent = nJBPercent + 35;
		pbInitSsytem.setValue(nJBPercent);
		//3. 註冊中央控管服務器，并取得access token
		CenterControl ccConfigurations = FastJsonUtils.deserializeObject(ccConfig.get().getValue(), CenterControl.class);
		gData.setCcBaseUrl(ccConfigurations.getAddress());
		//init device
		try {
			logger.debug("Mac address: [{}]", CommonUtils.getMacAdd());
			ReqDeviceInit reqDeviceInit = new ReqDeviceInit();
			reqDeviceInit.getData().setTerminalId(gData.getTerminalId());
			reqDeviceInit.getData().setTermType(CODES.TERM_TYPE_VM);
			reqDeviceInit.getData().setMacAddress(CommonUtils.getMacAdd());
			reqDeviceInit.getData().setIpAddress("");
			ResDeviceInit resDeviceInit = restClient.doPost(reqDeviceInit);
			
			logger.info("終端配置返回結果: {}", resDeviceInit.getResult());
			gData.setSystemToken(resDeviceInit.getData().getToken());
			gData.setDeviceInit(resDeviceInit);
			
		} catch (Throwable e) {
			logger.error("註冊終端[{}]錯誤！", gData.getTerminalId(), e);
			lblInformation.setText("註冊終端[" + gData.getTerminalId() +"]錯誤！！");
			lblInformation.setForeground(Color.RED);
//			JOptionPane.showMessageDialog(null,  "註冊終端[" + gData.getTerminalId() +"]錯誤！！","錯誤",
//					JOptionPane.ERROR_MESSAGE);
			//@TODO only for debug, will be delete
			//***********************************************************************************//
//			setResult(CODES.ERR_CONNECT_CENTER_CONTROL);
			//***********************************************************************************//
			return;
		}
		
		lblInformation.setText("系統啟動成功！");
		while (nJBPercent <= 100) {
			pbInitSsytem.setValue(nJBPercent);
			nJBPercent+=5;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setResult(int result) {
		this.result = result;
	}
	
	public int getResult() {
		return result;
	}

}
