package com.mmh.vmma.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mmh.vmma.jpa.entities.SystemConfig;
import com.mmh.vmma.jpa.entities.SystemConfig.SystemConfigId;
import com.mmh.vmma.jpa.repositories.SystemConfigRepository;
import com.mmh.vmma.systemvalue.CenterControl;
import com.mmh.vmma.systemvalue.PortMode;
import com.mmh.vmma.systemvalue.VMConfiguration;
import com.mmh.vmma.ui.common.CODES;
import com.mmh.vmma.ui.common.Settings;
import com.mmh.vmma.ui.dialogs.InitSystem;
import com.mmh.vmma.ui.frames.MainWindow;
import com.mmh.vmma.ui.templates.JCommonLabel;
import com.mmh.vmma.ui.templates.JCommonPanel;
import com.mmh.vmma.ui.templates.JCommonTable;
import com.mmh.vmma.ui.templates.JCommonTextField;
import com.mmh.vmma.ui.templates.JPlaintButton;
import com.mmh.vmma.utils.CommonUtils;
import com.mmh.vmma.utils.FastJsonUtils;

import java.awt.CardLayout;
import javax.swing.UIManager;
import java.awt.GridLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.SwingConstants;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Component
public class JPVMConfig extends JCommonPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5241620202296984732L;
	private static final Logger logger = LoggerFactory.getLogger(JPVMConfig.class);
	
	private  final int PM_PT = 0;
	private  final int PM_NAME = 1;
	private  final int PM_BR = 2;
	private  final int PM_DB = 3;
	private  final int PM_PAR = 4;
//	private  final int PM_SB = 5;
	private  final int PM_EB = 5;
	private  final int PM_FC = 6;
	private  final int PM_ADDR = 7;
	private  final int PM_PORT = 8;
	private  final int PM_TYPE = 9;
	
	private final String DEFAULT_IP="127.0.0.1";
	private final String DEFAULT_PORT="20000";
	
	/**
	 * Create the panel.
	 */
	private enum ACTION {add, update, delete}
	
	private final String[] pmHeader = {"端口類型", "串口名", "波特率", "數據位", "檢驗位", //"起始位",
			"停止位", "流控", "服務端地址", "端口號", "portType"};
	

	private  final String VIEW_TEMP = "temp";
	private  final String VIEW_PORT_SERIAL = "serialport";
	private  final String VIEW_PORT_TCP = "tcpudpport";

	@Autowired 
	MainWindow mainWindow;
	
	@Autowired
	InitSystem initSystem;
	@Autowired
	SystemConfigRepository sysRepo;
	
	private JCommonPanel jpTemp;

	private JCommonPanel jpTitle;
	private JCommonLabel lblTitle;
	
	private JCommonPanel jpContent;
	private JCommonPanel jpTerminalSetting;
	private GridBagLayout gblTerminalSetting;
	private GridBagConstraints gbcTerminalSetting;
	private JCommonLabel lblCustomerName;
	private JCommonTextField txtCustomerName;
	private JCommonLabel lblTerminalId;
	private JCommonTextField txtTerminalId;

	private JCommonPanel jpPortConnectionsList;
	private JScrollPane spPortConnections;
	private JCommonTable tblPortConnectionList;
	
	private JCommonPanel jpControlBoardInfo;
	private JCommonPanel jpCBBaseSelection;
	private JCommonPanel jpCBBaseSelection1;
	private JCommonPanel jpCBBaseSelection2;
	private JCommonLabel lblPortType;
	private JComboBox<String> cbPortType;
	private JCommonLabel lblNewPort;
	private JToggleButton tbNewPort;
	private JCommonPanel jpPortSetting;
	private CardLayout portCardView;
	private JCommonPanel jpSerialPort;
	private JCommonLabel lblCBPort;
	private JComboBox<String> cbCBPort;
	private JCommonLabel lblBR;
	private JComboBox<String> cbBR;
	private JCommonLabel lblDB;
	private JComboBox<String> cbDB;
	private JCommonLabel lblParity;
	private JComboBox<String> cbParity;
//	private JCommonLabel lblSB;
//	private JComboBox<String> cbSB;
	private JCommonLabel lblEB;
	private JComboBox<String> cbEB;
	private JCommonLabel lblFC;
	private JComboBox<String> cbFC;
	private JCommonPanel jpTCPUDPPort;
	private JCommonLabel lblSC;
	private JComboBox<String> cbSC;
	private JCommonLabel lblServerIP;
	private JCommonTextField txtServerIP;
	private JCommonLabel lblPort;
	private JCommonTextField txtPort;
	
	private JCommonPanel jpCenterControl;
	private JCommonLabel lblCenterControlURL;
	private JCommonTextField txtCenterControlURL;
	

	private JCommonPanel jpCBButton;
	private JPlaintButton btnCBDelete;
	private JPlaintButton btnCBSave;
	private JPlaintButton btnExit;
	
	public JPVMConfig() {
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated method stub
				resetPortMode();
				btnCBDelete.setEnabled(false);
				jpPortConnectionsList.setVisible(false);
				int width = jpPortConnectionsList.getWidth();
				jpControlBoardInfo.setPreferredSize(new Dimension(width / 3, 0));
				readConfig();
				jpPortConnectionsList.setVisible(true);
			}
		});
		
		//0. 全局layout设置
		setLayout(new BorderLayout(0, 0));
		jpTitle = new JCommonPanel();
		jpTitle.setLayout(new BorderLayout(0,0));
		add(jpTitle, BorderLayout.NORTH);
		jpTitle.setPreferredSize(new Dimension(0, 80));
		lblTitle = new JCommonLabel("--請設置連接端口訊息--");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("黑體", Font.BOLD, 40));
		jpTitle.add(lblTitle, BorderLayout.CENTER);
		
		
		jpContent = new JCommonPanel();
		jpContent.setLayout(new BorderLayout(0,0));
		add(jpContent, BorderLayout.CENTER);
		
		//1. 端口设置
		jpTerminalSetting = new JCommonPanel();
		jpTerminalSetting.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "終端設置", 
				TitledBorder.LEADING, TitledBorder.TOP, new Font("黑體", Font.PLAIN, 23), new Color(0, 0, 0)));
		jpTerminalSetting.setPreferredSize(new Dimension(0, 120));
		jpContent.add(jpTerminalSetting, BorderLayout.NORTH);
		gblTerminalSetting = new GridBagLayout();
		gbcTerminalSetting = new GridBagConstraints();
		jpTerminalSetting.setLayout(gblTerminalSetting);
		
		//如果组件所在的区域比组件本身要大时的显示情况 
		gbcTerminalSetting.fill = GridBagConstraints.BOTH;
		gbcTerminalSetting.insets = new Insets(3, 3, 3, 3);
		
		//1.1设置用户名称
		lblCustomerName = new JCommonLabel("*用戶名稱:");
		lblCustomerName.setFont(new Font("黑體", Font.PLAIN, 20));
		
		gbcTerminalSetting.gridwidth = 1;
		gbcTerminalSetting.weightx = 0;
		gbcTerminalSetting.weighty = 1;
		gblTerminalSetting.setConstraints(lblCustomerName, gbcTerminalSetting);
		jpTerminalSetting.add(lblCustomerName);
		
		txtCustomerName = new JCommonTextField();
		txtCustomerName.setFont(new Font("黑體", Font.PLAIN, 20));
		txtCustomerName.setColumns(100);
		gbcTerminalSetting.gridwidth = GridBagConstraints.REMAINDER;
		gbcTerminalSetting.weightx = 1;
		gbcTerminalSetting.weighty = 1;
		gblTerminalSetting.setConstraints(txtCustomerName, gbcTerminalSetting);
		jpTerminalSetting.add(txtCustomerName);
			
		//1.2 设置终端编号
		lblTerminalId = new JCommonLabel("*終端編號:");
		lblTerminalId.setFont(new Font("黑體", Font.PLAIN, 20));
		gbcTerminalSetting.gridwidth = 1;
		gbcTerminalSetting.weightx = 0;
		gbcTerminalSetting.weighty = 0;
		gblTerminalSetting.setConstraints(lblTerminalId, gbcTerminalSetting);
		jpTerminalSetting.add(lblTerminalId);

		
		txtTerminalId = new JCommonTextField();
		txtTerminalId.setFont(new Font("黑體", Font.PLAIN, 20));
		gbcTerminalSetting.gridwidth = GridBagConstraints.REMAINDER;
		gbcTerminalSetting.weightx = 1;
		gbcTerminalSetting.weighty = 1;
		gblTerminalSetting.setConstraints(txtTerminalId, gbcTerminalSetting);
		jpTerminalSetting.add(txtTerminalId);
		
		//2. 端口列表
		jpPortConnectionsList = new JCommonPanel();
		jpContent.add(jpPortConnectionsList);
		jpPortConnectionsList.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "連接端口", 
				TitledBorder.LEADING, TitledBorder.TOP, new Font("黑體", Font.PLAIN, 23), new Color(0, 0, 0)));
		jpPortConnectionsList.setLayout(new BorderLayout(0, 0));

		//2.1 连接端口设置詳細信息
		jpControlBoardInfo = new JCommonPanel();
		jpPortConnectionsList.add(jpControlBoardInfo, BorderLayout.WEST);
		jpControlBoardInfo.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "端口屬性", 
				TitledBorder.LEADING, TitledBorder.TOP, 
				new Font("黑體", Font.PLAIN, 20), new Color(0, 0, 0)));
		jpControlBoardInfo.setLayout(new BorderLayout(0, 0));
		
		//2.2.1 公共部分设置
		jpCBBaseSelection = new JCommonPanel();
		jpControlBoardInfo.add(jpCBBaseSelection, BorderLayout.NORTH);
		jpCBBaseSelection.setLayout(new BorderLayout(0, 0));
		
		jpCBBaseSelection1 = new JCommonPanel();
		jpCBBaseSelection.add(jpCBBaseSelection1, BorderLayout.NORTH);
		jpCBBaseSelection1.setPreferredSize(new Dimension(0, 40));
		GridBagLayout gblCBBaseSelection = new GridBagLayout();
		jpCBBaseSelection1.setLayout(gblCBBaseSelection);
		GridBagConstraints gbcCBBaseSelection = new GridBagConstraints();
		
		gbcCBBaseSelection.fill =GridBagConstraints.BOTH;
		gbcCBBaseSelection.insets = new Insets(3, 3, 3, 3);

		lblPortType = new JCommonLabel("端口類型：");
		lblPortType.setFont(new Font("黑体", Font.PLAIN, 20));
		jpCBBaseSelection1.add(lblPortType);
		gbcCBBaseSelection.gridwidth = 1;
		gbcCBBaseSelection.weightx = 0;
		gbcCBBaseSelection.weighty = 1;
		gblCBBaseSelection.setConstraints(lblPortType, gbcCBBaseSelection);
		
		cbPortType = new JComboBox<String>();
		cbPortType.setModel(new DefaultComboBoxModel<String>((new String[] {"TCP/UDP", "RS232C(串口)"})));
		cbPortType.setFont(new Font("Dialog", Font.PLAIN, 20));
		cbPortType.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					if(cbPortType.getSelectedItem().equals("TCP/UDP")){
						portCardView.show(jpPortSetting, VIEW_TEMP);
						portCardView.show(jpPortSetting, VIEW_PORT_TCP);
					}else{
						portCardView.show(jpPortSetting, VIEW_TEMP);
						portCardView.show(jpPortSetting, VIEW_PORT_SERIAL);
					}
				}
			}
		});
		jpCBBaseSelection1.add(cbPortType);
		gbcCBBaseSelection.gridwidth = GridBagConstraints.REMAINDER;
		gbcCBBaseSelection.weightx = 1;
		gbcCBBaseSelection.weighty = 1;
		gblCBBaseSelection.setConstraints(cbPortType, gbcCBBaseSelection);
		
		jpCBBaseSelection2 = new JCommonPanel();
		jpCBBaseSelection.add(jpCBBaseSelection2, BorderLayout.SOUTH);
		jpCBBaseSelection2.setPreferredSize(new Dimension(0, 40));
		jpCBBaseSelection2.setLayout(gblCBBaseSelection);
		lblNewPort = new JCommonLabel("新增端口：");
		lblNewPort.setFont(new Font("黑體", Font.PLAIN, 20));
		jpCBBaseSelection2.add(lblNewPort);
		gbcCBBaseSelection.gridwidth = 1;
		gbcCBBaseSelection.weightx = 0;
		gbcCBBaseSelection.weighty = 1;
		gblCBBaseSelection.setConstraints(lblNewPort, gbcCBBaseSelection);
		
		tbNewPort = new JToggleButton();
		tbNewPort.setBorderPainted(false);
		tbNewPort.setContentAreaFilled(false);
		tbNewPort.setBackground(new Color(0xf0,0xf0,0xf0));
		tbNewPort.setSelectedIcon(new ImageIcon("images/toggle_on.png"));
		tbNewPort.setIcon(new ImageIcon("images/toggle_off.png"));
		jpCBBaseSelection2.add(tbNewPort);
		gbcCBBaseSelection.gridwidth = 1;
		gbcCBBaseSelection.weightx = 0;
		gbcCBBaseSelection.weighty = 1;
		gblCBBaseSelection.setConstraints(tbNewPort, gbcCBBaseSelection);

		JCommonPanel jpFilled = new JCommonPanel();
//		jpFilled.setBackground(Color.RED);
		jpCBBaseSelection2.add(jpFilled);
		gbcCBBaseSelection.gridwidth = GridBagConstraints.REMAINDER;
		gbcCBBaseSelection.weightx = 1;
		gbcCBBaseSelection.weighty = 1;
		gblCBBaseSelection.setConstraints(jpFilled, gbcCBBaseSelection);
		
		//2.2.2 端口设置
		portCardView = new CardLayout();
		jpPortSetting = new JCommonPanel();
		jpControlBoardInfo.add(jpPortSetting, BorderLayout.CENTER);
		jpPortSetting.setLayout(portCardView);
		
		jpTemp = new JCommonPanel();
		jpPortSetting.add(jpTemp, VIEW_TEMP);
		//2.2.2.1 串口设置
		jpSerialPort = new JCommonPanel();
		jpPortSetting.add(jpSerialPort, VIEW_PORT_SERIAL);
		jpSerialPort.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "串口設置", 
				TitledBorder.LEADING, TitledBorder.TOP, new Font("黑體", Font.PLAIN, 20), new Color(0, 0, 0)));
		
		GridBagLayout gblSerialPort = new GridBagLayout();
		jpSerialPort.setLayout(gblSerialPort);
		GridBagConstraints gbcSerialPort = new GridBagConstraints();
		gbcSerialPort.fill = GridBagConstraints.BOTH;
		gbcSerialPort.insets = new Insets(3, 3, 3, 3);
		lblCBPort = new JCommonLabel("串口編號：");
		jpSerialPort.add(lblCBPort);
		lblCBPort.setFont(new Font("黑体", Font.PLAIN, 20));
		gbcSerialPort.gridwidth = 1;
		gbcSerialPort.weightx = 0;
		gbcSerialPort.weighty = 1;
		gblSerialPort.setConstraints(lblCBPort, gbcSerialPort);
		
		cbCBPort = new JComboBox<String>();
		jpSerialPort.add(cbCBPort);
		cbCBPort.setFont(new Font("黑体", Font.PLAIN, 20));
		cbCBPort.setModel(new DefaultComboBoxModel<String>(new String[]{"COM1", "COM2", 
				"COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "TCP/UDP"}));
		gbcSerialPort.gridwidth = GridBagConstraints.REMAINDER;
		gbcSerialPort.weightx = 1;
		gbcSerialPort.weighty = 1;
		gblSerialPort.setConstraints(cbCBPort, gbcSerialPort);
		
		lblBR = new JCommonLabel("波特率：");
		lblBR.setFont(new Font("黑体", Font.PLAIN, 20));
		lblBR.setToolTipText("Baud rate");
		jpSerialPort.add(lblBR);
		gbcSerialPort.gridwidth = 1;
		gbcSerialPort.weightx = 0;
		gbcSerialPort.weighty = 1;
		gblSerialPort.setConstraints(lblBR, gbcSerialPort);
		
		cbBR = new JComboBox<String>();
		cbBR.setFont(new Font("黑体", Font.PLAIN, 20));
		cbBR.setModel(new DefaultComboBoxModel<String>((new String[]{"9600", "19200", 
				"38400", "57600", "115200"})));
		jpSerialPort.add(cbBR);
		gbcSerialPort.gridwidth = GridBagConstraints.REMAINDER;
		gbcSerialPort.weightx = 1;
		gbcSerialPort.weighty = 1;
		gblSerialPort.setConstraints(cbBR, gbcSerialPort);
		
		lblDB = new JCommonLabel("數據位：");
		lblDB.setFont(new Font("黑体", Font.PLAIN, 20));
		lblDB.setToolTipText("data bits");
		jpSerialPort.add(lblDB);
		gbcSerialPort.gridwidth = 1;
		gbcSerialPort.weightx = 0;
		gbcSerialPort.weighty = 1;
		gblSerialPort.setConstraints(lblDB, gbcSerialPort);
		
		cbDB = new JComboBox<String>();
		cbDB.setFont(new Font("黑体", Font.PLAIN, 20));
		cbDB.setModel(new DefaultComboBoxModel<String>((new String[]{"5", "6", 
				"7", "8"})));
		jpSerialPort.add(cbDB);
		gbcSerialPort.gridwidth = GridBagConstraints.REMAINDER;
		gbcSerialPort.weightx = 1;
		gbcSerialPort.weighty = 1;
		gblSerialPort.setConstraints(cbDB, gbcSerialPort);

		lblParity = new JCommonLabel("校驗位：");
		lblParity.setFont(new Font("黑体", Font.PLAIN, 20));
		lblParity.setToolTipText("parity");
		jpSerialPort.add(lblParity);
		gbcSerialPort.gridwidth = 1;
		gbcSerialPort.weightx = 0;
		gbcSerialPort.weighty = 1;
		gblSerialPort.setConstraints(lblParity, gbcSerialPort);
		
		cbParity = new JComboBox<String>();
		cbParity.setFont(new Font("黑体", Font.PLAIN, 20));
		cbParity.setModel(new DefaultComboBoxModel<String>((new String[]{"None", "Even", 
				"Odd", "Mark", "Space"})));
		jpSerialPort.add(cbParity);
		gbcSerialPort.gridwidth = GridBagConstraints.REMAINDER;
		gbcSerialPort.weightx = 1;
		gbcSerialPort.weighty = 1;
		gblSerialPort.setConstraints(cbParity, gbcSerialPort);
		
//		lblSB = new JCommonLabel("起始位：");
//		lblSB.setFont(new Font("黑體", Font.PLAIN, 20));
//		lblSB.setToolTipText("start bit");
//		jpSerialPort.add(lblSB);
//		gbcSerialPort.gridwidth = 1;
//		gbcSerialPort.weightx = 0;
//		gbcSerialPort.weighty = 1;
//		gblSerialPort.setConstraints(lblSB, gbcSerialPort);
//		
//		cbSB = new JComboBox<String>();
//		cbSB.setFont(new Font("黑体", Font.PLAIN, 20));
//		cbSB.setBounds(92, 94, 140, 21);
//		cbSB.setModel(new DefaultComboBoxModel<String>((new String[]{"1", "1.5", "2"})));
//		jpSerialPort.add(cbSB);
//		gbcSerialPort.gridwidth = GridBagConstraints.REMAINDER;
//		gbcSerialPort.weightx = 1;
//		gbcSerialPort.weighty = 1;
//		gblSerialPort.setConstraints(cbSB, gbcSerialPort);

		lblEB = new JCommonLabel("停止位：");
		lblEB.setFont(new Font("黑體", Font.PLAIN, 20));
		lblEB.setToolTipText("stop bit");
		jpSerialPort.add(lblEB);
		gbcSerialPort.gridwidth = 1;
		gbcSerialPort.weightx = 0;
		gbcSerialPort.weighty = 1;
		gblSerialPort.setConstraints(lblEB, gbcSerialPort);
		
		cbEB = new JComboBox<String>();
		cbEB.setFont(new Font("黑体", Font.PLAIN, 20));
		cbEB.setBounds(92, 94, 140, 21);
		cbEB.setModel(new DefaultComboBoxModel<String>((new String[]{"1", "1.5", "2"})));
		jpSerialPort.add(cbEB);
		gbcSerialPort.gridwidth = GridBagConstraints.REMAINDER;
		gbcSerialPort.weightx = 1;
		gbcSerialPort.weighty = 1;
		gblSerialPort.setConstraints(cbEB, gbcSerialPort);

		lblFC = new JCommonLabel("流  控：");
		lblFC.setFont(new Font("黑体", Font.PLAIN, 20));
		lblFC.setToolTipText("flow control");
		jpSerialPort.add(lblFC);
		gbcSerialPort.gridwidth = 1;
		gbcSerialPort.weightx = 0;
		gbcSerialPort.weighty = 1;
		gblSerialPort.setConstraints(lblFC, gbcSerialPort);
		
		cbFC = new JComboBox<String>();
		cbFC.setFont(new Font("黑体", Font.PLAIN, 20));
		cbFC.setBounds(92, 121, 140, 21);
		cbFC.setModel(new DefaultComboBoxModel<String>((new String[]{"None", "RTS/CTS", "XON/XOFF"})));
		jpSerialPort.add(cbFC);
		gbcSerialPort.gridwidth = GridBagConstraints.REMAINDER;
		gbcSerialPort.weightx = 1;
		gbcSerialPort.weighty = 1;
		gblSerialPort.setConstraints(cbFC, gbcSerialPort);
		
		//3.2.2 TCP/UDP端口设置
		jpTCPUDPPort = new JCommonPanel();
		jpTCPUDPPort.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "TCP/IP設置", 
				TitledBorder.LEADING, TitledBorder.TOP, new Font("黑體", Font.PLAIN, 20), new Color(0, 0, 0)));
		jpPortSetting.add(jpTCPUDPPort,VIEW_PORT_TCP);
		GridBagLayout gblTCPUDPPort = new GridBagLayout();
		jpTCPUDPPort.setLayout(gblTCPUDPPort);
		GridBagConstraints gbcTCPUDPPort = new GridBagConstraints();
		gbcTCPUDPPort.fill = GridBagConstraints.BOTH;
		gbcTCPUDPPort.insets = new Insets(5, 5, 5, 5);
		lblSC = new JCommonLabel("連接方式:");
		lblSC.setFont(new Font("黑体", Font.PLAIN, 25));
		lblSC.setToolTipText("Server or Client");
		lblSC.setBounds(0, 0, 100, 50);
		jpTCPUDPPort.add(lblSC);
		gbcTCPUDPPort.gridwidth = 1;
		gbcTCPUDPPort.weightx = 0;
		gbcTCPUDPPort.weighty = 1;
		gblTCPUDPPort.setConstraints(lblSC, gbcTCPUDPPort);
		
		cbSC = new JComboBox<String>();
		cbSC.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(cbSC.getSelectedIndex() == 1){
					txtServerIP.setText("");
					txtServerIP.setBackground(Color.LIGHT_GRAY);
					txtServerIP.setEnabled(false);
					txtPort.selectAll();
					txtPort.requestFocus();
				}else{
					txtServerIP.setBackground(Color.WHITE);
					txtServerIP.setText("127.0.0.1");
					txtServerIP.selectAll();
					txtServerIP.requestFocus();
					txtServerIP.setEnabled(true);
				}
			}
		});
		cbSC.setFont(new Font("黑体", Font.PLAIN, 25));
		cbSC.setModel(new DefaultComboBoxModel<String>((new String[]{"TCP/UDP Client", "TCP/UDP Server"})));
		cbSC.setBounds(0, 0, 100, 50);
		jpTCPUDPPort.add(cbSC);
		gbcTCPUDPPort.gridwidth = GridBagConstraints.REMAINDER;
		gbcTCPUDPPort.weightx = 1;
		gbcTCPUDPPort.weighty = 1;
		gblTCPUDPPort.setConstraints(cbSC, gbcTCPUDPPort);

		lblServerIP = new JCommonLabel("服務IP:");
		lblServerIP.setFont(new Font("黑体", Font.PLAIN, 25));
		lblServerIP.setBounds(0, 0, 100, 50);
		jpTCPUDPPort.add(lblServerIP);
		gbcTCPUDPPort.gridwidth = 1;
		gbcTCPUDPPort.weightx = 0;
		gbcTCPUDPPort.weighty = 1;
		gblTCPUDPPort.setConstraints(lblServerIP, gbcTCPUDPPort);
		
		txtServerIP = new JCommonTextField();
		txtServerIP.setFont(new Font("黑体", Font.PLAIN, 25));
		txtServerIP.setBounds(0, 0, 100, 50);
		txtServerIP.setText("127.0.0.1");
		jpTCPUDPPort.add(txtServerIP);
		gbcTCPUDPPort.gridwidth = GridBagConstraints.REMAINDER;
		gbcTCPUDPPort.weightx = 1;
		gbcTCPUDPPort.weighty = 1;
		gblTCPUDPPort.setConstraints(txtServerIP, gbcTCPUDPPort);
		txtServerIP.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
		  		char ekey = e.getKeyChar();
		  		if((ekey < '0' || ekey > '9') &&( ekey != '.') ){
		  			e.setKeyChar('\0');
		  			return;
		  		}
		  		String ipAddr = txtServerIP.getText().trim();
		  		if(!checkIpAddress(ipAddr, ekey)) {
		  			e.setKeyChar('\0');
		  			return;
		  		};
//		  		ipAddr = ipAddr.substring(0, txtServerIP.getSelectionStart()) + ekey 
//		  				+ ipAddr.substring(txtServerIP.getSelectionEnd());
//	  			if(!inputIP(ipAddr)) {
//	  				e.setKeyChar('\0');
//	  			}
			}
		});

		lblPort = new JCommonLabel("連接端口:");
		lblPort.setFont(new Font("黑体", Font.PLAIN, 25));
		lblPort.setToolTipText("Port number");
		lblPort.setBounds(0, 0, 100, 50);
		jpTCPUDPPort.add(lblPort);
		gbcTCPUDPPort.gridwidth = 1;
		gbcTCPUDPPort.weightx = 0;
		gbcTCPUDPPort.weighty = 1;
		gblTCPUDPPort.setConstraints(lblPort, gbcTCPUDPPort);
		
		txtPort = new JCommonTextField();
		txtPort.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
		  		char ekey = e.getKeyChar();
		  		if(ekey < '0' || ekey > '9'){
		  			e.setKeyChar('\0');
		  		}
			}
		});
		txtPort.setFont(new Font("黑体", Font.PLAIN, 25));
		txtPort.setBounds(0, 0, 100, 50);
		jpTCPUDPPort.add(txtPort);
		gbcTCPUDPPort.gridwidth = GridBagConstraints.REMAINDER;
		gbcTCPUDPPort.weightx = 1;
		gbcTCPUDPPort.weighty = 1;
		gblTCPUDPPort.setConstraints(txtPort, gbcTCPUDPPort);
		portCardView.show(jpPortSetting, VIEW_TEMP);
		portCardView.show(jpPortSetting, VIEW_PORT_TCP);
		
		//2.2 端口表格
		spPortConnections = new JScrollPane();
		spPortConnections.setBackground(new Color(181, 223, 226));
		jpPortConnectionsList.add(spPortConnections, BorderLayout.CENTER);
		
		tblPortConnectionList = new JCommonTable();
		tblPortConnectionList.getTableHeader().setPreferredSize(new Dimension(0, 50));
		tblPortConnectionList.getTableHeader().setBackground(new Color(135, 206,250));
		tblPortConnectionList.setFont(new Font("黑體", Font.PLAIN, 17));
		tblPortConnectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblPortConnectionList.getTableHeader().setReorderingAllowed(false);
		tblPortConnectionList.getTableHeader().setResizingAllowed(true);
		tblPortConnectionList.getTableHeader().setFont(new Font("黑體", Font.BOLD, 17));
		tblPortConnectionList.setRowHeight(50);
		tblPortConnectionList.setAutoResizeMode(JCommonTable.AUTO_RESIZE_NEXT_COLUMN);
		tblPortConnectionList.setAutoCreateRowSorter(true);
		spPortConnections.add(tblPortConnectionList);
		spPortConnections.setViewportView(tblPortConnectionList);
		spPortConnections.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (tblPortConnectionList.rowAtPoint(e.getPoint()) == -1) {
					tblPortConnectionList.clearSelection();
					resetPortMode();
					btnCBDelete.setEnabled(false);
				}
			}
		});
		
		tblPortConnectionList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (tblPortConnectionList.rowAtPoint(e.getPoint()) == -1) {
					tblPortConnectionList.clearSelection();

					}
			}
		});

		tblPortConnectionList.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getSource() == tblPortConnectionList.getSelectionModel()){
			  		if(tblPortConnectionList.getSelectedRow() >= 0){
			  			int row = tblPortConnectionList.getSelectedRow();
			  			int mode = (int)tblPortConnectionList.getValueAt(row, PM_TYPE);
			  			if(mode == 0) {
			  				cbPortType.setSelectedIndex(1);
			  			}else {
			  				cbPortType.setSelectedIndex(0);
			  				if(mode == 1) {
			  					cbSC.setSelectedIndex(0);
			  				}else {
			  					cbSC.setSelectedIndex(1);
			  				}
			  				
			  			}
			  			cbCBPort.setSelectedItem(tblPortConnectionList.getValueAt(row, PM_NAME));
			  			cbBR.setSelectedItem(tblPortConnectionList.getValueAt(row, PM_BR));
			  			cbDB.setSelectedItem(tblPortConnectionList.getValueAt(row, PM_DB));
			  			cbParity.setSelectedItem(tblPortConnectionList.getValueAt(row, PM_PAR));
//			  			cbSB.setSelectedItem(tblPortConnectionList.getValueAt(row, PM_SB));
			  			cbEB.setSelectedItem(tblPortConnectionList.getValueAt(row, PM_EB));
			  			cbFC.setSelectedItem(tblPortConnectionList.getValueAt(row, PM_FC));
			  			txtServerIP.setText(tblPortConnectionList.getValueAt(row, PM_ADDR).toString());
			  			txtPort.setText(tblPortConnectionList.getValueAt(row, PM_PORT).toString());
			  			btnCBDelete.setEnabled(true);
			  		}else{
			  			btnCBDelete.setEnabled(false);
			  			tblPortConnectionList.clearSelection();
						resetPortMode();
			  		}
	
				}
				
			}
			
		});
		
		//2.3 中央控管URL管理
		jpCenterControl = new JCommonPanel();
		jpPortConnectionsList.add(jpCenterControl, BorderLayout.SOUTH);
		jpCenterControl.setPreferredSize(new Dimension(0, 50));
		GridBagLayout gblCCURLSetting = new GridBagLayout();
		jpCenterControl.setLayout(gblCCURLSetting);
		GridBagConstraints gbcCCURLSetting = new GridBagConstraints();
		
		gbcCCURLSetting.fill =GridBagConstraints.BOTH;
		gbcCCURLSetting.insets = new Insets(3, 3, 3, 3);

		lblCenterControlURL = new JCommonLabel("*中央控管URL：");
		lblCenterControlURL.setFont(new Font("黑體", Font.PLAIN, 20));
		jpCenterControl.add(lblCenterControlURL);
		gbcCCURLSetting.gridwidth = 1;
		gbcCCURLSetting.weightx = 0;
		gbcCCURLSetting.weighty = 1;
		gblCCURLSetting.setConstraints(jpCenterControl, gbcCCURLSetting);
		
		txtCenterControlURL = new JCommonTextField();
		txtCenterControlURL.setFont(new Font("黑體", Font.PLAIN, 20));
		jpCenterControl.add(txtCenterControlURL);
		gbcCCURLSetting.gridwidth = GridBagConstraints.REMAINDER;
		gbcCCURLSetting.weightx = 1;
		gbcCCURLSetting.weighty = 1;
		gblCCURLSetting.setConstraints(txtCenterControlURL, gbcCCURLSetting);
		
		//4. Control Board操作按鈕
		jpCBButton = new JCommonPanel();
		jpContent.add(jpCBButton, BorderLayout.SOUTH);
		jpCBButton.setPreferredSize(new Dimension(0, 70));
		jpCBButton.setLayout(new GridLayout(0, 3, 0, 0));
		
//		btnCBAdd = new JPlaintButton("新增");
//		btnCBAdd.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				saveVMConfig(ACTION.add);
//				saveCCConfig();
//			}
//		});
//		jpCBButton.add(btnCBAdd);
		
		btnCBSave = new JPlaintButton("保存");
		btnCBSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ACTION action;
				if(tbNewPort.isSelected()){
					action = ACTION.add;
				}else if(tblPortConnectionList.getSelectedRow() >= 0) {
					action = ACTION.update;
				}else {
					action = ACTION.add;
				}
				if(saveVMConfig(action)) {
					saveCCConfig();
					btnCBDelete.setEnabled(false);
		  			tblPortConnectionList.clearSelection();
					resetPortMode();
					readConfig();
				}

			}
		});
		jpCBButton.add(btnCBSave);
		
		btnCBDelete = new JPlaintButton("刪除");
		btnCBDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveVMConfig(ACTION.delete);
				btnCBDelete.setEnabled(false);
	  			tblPortConnectionList.clearSelection();
				resetPortMode();
				readConfig();

			}
		});
		jpCBButton.add(btnCBDelete);
		
		btnExit = new JPlaintButton("退出");
		btnExit.setToolTipText("退出");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(initSystem.getResult() == CODES.SUCCESS) {
					mainWindow.showFunctionPage();
					return;
				}
				JOptionPane.showMessageDialog(null,  "配置修改保存后，請重啟系統!","警告",
						JOptionPane.WARNING_MESSAGE);
				System.exit(0);
//				mainWindow.setVisible(false);
//				initSystem.setModal(true);
//				initSystem.setVisible(true);
			}
		});
		jpCBButton.add(btnExit);
	}


	/**
	 * action: 1 - 新增; 2 - 修改; 3 - 刪除;
	 * */
	private boolean saveVMConfig(ACTION action) {
		if(tbNewPort.isSelected()) {
			if(cbPortType.getSelectedIndex() == 0) {
				if(cbSC.getSelectedIndex() == 0) { //TCP/IP client
					if(Optional.ofNullable(txtServerIP.getText()).orElse("").trim().length() <= 0) {
						logger.warn("服務端IP地址為空。");
						JOptionPane.showMessageDialog(null,  "服務端IP地址不能為空！","錯誤",
								JOptionPane.ERROR_MESSAGE);
						return false;
					}
					if(Optional.ofNullable(txtPort.getText()).orElse("").trim().length() <= 0) {
						logger.warn("端口為空。");
						JOptionPane.showMessageDialog(null,  "連接端口不能為空！","錯誤",
								JOptionPane.ERROR_MESSAGE);
						return false;
					}
					if(!(CommonUtils.isIPAddress(Optional.ofNullable(txtServerIP.getText()).orElse("")))) {
						logger.warn("服務端IP地址格式錯誤。");
						JOptionPane.showMessageDialog(null,  "服務端IP地址格式錯誤！","錯誤",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {	//TCP/IP Server
					if(Optional.ofNullable(txtPort.getText()).orElse("").trim().length() <= 0) {
						logger.warn("端口為空。");
						JOptionPane.showMessageDialog(null,  "端口不能為空！","錯誤",
								JOptionPane.ERROR_MESSAGE);
						return false;
					}
				}
			}
		}
		if(Optional.ofNullable(txtCustomerName.getText()).orElse("").trim().length() <= 0) {
			txtCustomerName.requestFocus();
			logger.warn("客戶名稱為空。");
			JOptionPane.showMessageDialog(null,  "客戶名稱不能為空！","錯誤",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if(Optional.ofNullable(txtTerminalId.getText()).orElse("").trim().length() <= 0) {
			txtTerminalId.requestFocus();
			logger.warn("終端ID為空。");
			JOptionPane.showMessageDialog(null,  "終端編號不能為空！","錯誤",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		VMConfiguration vmConfiguration = null;
		PortMode pm = null;
		//1. 讀取數據庫配置信息
		Optional<SystemConfig> cabinetConfig = Optional.ofNullable(sysRepo.findById(SystemConfigId.cabinet.toString())).orElse(null);
		
		//系統沒有初始化
		if(!cabinetConfig.isPresent()) {
			logger.info("沒有藥品櫃配置數據。");
			cabinetConfig = Optional.of(new SystemConfig());
			vmConfiguration = new VMConfiguration();
		}else {
			logger.info("找到藥品櫃配置數據。");
			vmConfiguration = FastJsonUtils.deserializeObject(cabinetConfig.get().getValue(), VMConfiguration.class);
		}
		
		logger.debug("txtCustomerName = [{}]", txtCustomerName.getText());
		vmConfiguration.setCustomer(txtCustomerName.getText());
		vmConfiguration.setTerminalid(txtTerminalId.getText());
		switch(action) {
			case add:
				vmConfiguration.getPortModes();
				if(tbNewPort.isSelected()) {
					pm = new PortMode();
					setPortMode(pm);
					vmConfiguration.getPortModes().add(pm);
				}
				break;
			case update:
				pm = vmConfiguration.getPortModes().get(tblPortConnectionList.getSelectedRow());
				setPortMode(pm);
				break;
			case delete:
				if(JOptionPane.showConfirmDialog(null, "確定刪除這個端口嗎？", "警告", 
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.CANCEL_OPTION) {
					resetPortMode();
					return false;
				}
				if(tblPortConnectionList.getSelectedRow()>= 0) {
					vmConfiguration.getPortModes().remove(tblPortConnectionList.getSelectedRow());
				}
				break;
			default:
				logger.error("沒有該操作類型！");
				return false;
		}
				
		cabinetConfig.get().setKey(SystemConfigId.cabinet.toString());
		cabinetConfig.get().setValue(FastJsonUtils.serialize(vmConfiguration));
		sysRepo.saveAndFlush(cabinetConfig.get());
		return true;
	}
	
	private void setPortMode(PortMode pm) {
		switch (cbPortType.getSelectedIndex()) {
		case 0:
			pm.setMode(cbSC.getSelectedIndex() + 1);
			pm.setAddress(txtServerIP.getText());
			pm.setPortNumber(txtPort.getText());
			pm.setName("");
			pm.setBaudRate("");
			pm.setDataBites("");
			pm.setParity("");
			pm.setStartBits("");
			pm.setStopBits("");
			pm.setFlowControl("");

			break;
		case 1:
			pm.setMode(0);
			pm.setName(cbCBPort.getSelectedItem().toString());
			pm.setBaudRate(cbBR.getSelectedItem().toString());
			pm.setDataBites(cbDB.getSelectedItem().toString());
			pm.setParity(cbParity.getSelectedItem().toString());
			pm.setStartBits("");
			pm.setStopBits(cbEB.getSelectedItem().toString());
			pm.setFlowControl(cbFC.getSelectedItem().toString());
			pm.setAddress("");
			pm.setPortNumber("");
			break;
		default:
			break;
		}
	}

	private void resetPortMode() {
		txtCustomerName.requestFocus();
		cbPortType.setSelectedIndex(0);
		cbSC.setSelectedIndex(0);
		cbCBPort.setSelectedIndex(0);
		cbBR.setSelectedIndex(0);
		cbDB.setSelectedIndex(0);
		cbParity.setSelectedIndex(0);
//		cbSB.setSelectedItem(tblPortConnectionList.getValueAt(row, PM_SB));
		cbEB.setSelectedIndex(0);
		cbFC.setSelectedIndex(0);
		txtServerIP.setText(DEFAULT_IP);
		txtPort.setText(DEFAULT_PORT);
		tbNewPort.setSelected(false);
	}

	private void saveCCConfig() {
		logger.debug("txtCenterControlURL = {}",txtCenterControlURL.getText());
		String ccUrl = (Optional.ofNullable(txtCenterControlURL.getText()).orElse("")).trim();
		if(ccUrl.isEmpty()) {
			logger.warn("中央控管URL為空！");
			JOptionPane.showMessageDialog(null,  "中央控管URL不能為空！","錯誤",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			URI uri = new URI(ccUrl);
			if(uri.getHost() == null) {
				logger.error("中央控管URL不正確。");
				JOptionPane.showMessageDialog(null,  "中央控管URL不正確！","錯誤",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}catch(URISyntaxException e) {
			logger.error("中央控管URL不正確：", e);
			JOptionPane.showMessageDialog(null,  "中央控管URL不正確！","錯誤",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		CenterControl ccConfiguration = null;
		Optional<SystemConfig> ccConfig = Optional.ofNullable(sysRepo.findById(SystemConfigId.control_center.toString())).orElse(null);
		if(!ccConfig.isPresent()) {
			logger.warn("沒有中央控管配置數據。");
			ccConfig = Optional.of(new SystemConfig());
			ccConfiguration = new CenterControl();
		}else {
			ccConfiguration = FastJsonUtils.deserializeObject(ccConfig.get().getValue(), CenterControl.class);
		}
		ccConfiguration.setAddress(ccUrl);
		ccConfig.get().setKey(SystemConfigId.control_center.toString());
		ccConfig.get().setValue(FastJsonUtils.serialize(ccConfiguration));
		sysRepo.saveAndFlush(ccConfig.get());
	}
	
	private void readConfig() {
		//1. 讀取數據庫配置信息
		Optional<SystemConfig> cabinetConfig = Optional.ofNullable(sysRepo.findById(SystemConfigId.cabinet.toString())).orElse(null);
		
		//系統沒有初始化
		if(!cabinetConfig.isPresent()) {
			logger.warn("沒有藥品櫃配置數據。");
			txtCustomerName.setText("");
			txtTerminalId.setText("");
			txtCustomerName.requestFocus();
		}else {
			VMConfiguration vmConfiguration = FastJsonUtils.deserializeObject(cabinetConfig.get().getValue(), VMConfiguration.class);
			logger.debug("customer name {}", vmConfiguration.getCustomer());
			txtCustomerName.setText(vmConfiguration.getCustomer());
			txtTerminalId.setText(vmConfiguration.getTerminalid());
			setPortModeList(vmConfiguration.getPortModes());
			
		}

		Optional<SystemConfig> ccConfig = Optional.ofNullable(sysRepo.findById(SystemConfigId.control_center.toString())).orElse(null);
		
		//中央控管連接信息沒有配置
		if(!ccConfig.isPresent()) {
			logger.warn("沒有中央控管配置數據。");
			txtCenterControlURL.setText("");
		}else {
			CenterControl ccConfiguration = FastJsonUtils.deserializeObject(ccConfig.get().getValue(), CenterControl.class);
			txtCenterControlURL.setText(ccConfiguration.getAddress());
			txtCenterControlURL.selectAll();
		}
		validate();
	}
	
	private void setPortModeList(List<PortMode> lsPortModes) {
		DefaultTableModel model=new DefaultTableModel();
      model.setColumnCount(pmHeader.length);
      model.setColumnIdentifiers(pmHeader);
//	"端口類型", "波特率", "數據位", "檢驗位",  "停止位", "流控", "服務端地址", "端口號" , "portType"
      if(lsPortModes == null) {
    	  return;
      }
      
      for (PortMode pm : lsPortModes){
      	String portType = "";
      	switch(pm.getMode()){
      	case Settings.SERIAL:
      		portType = "串口";
      		break;
      	case Settings.TCP_CLIENT:
      		portType = "網路客戶端";
      		break;
      	case Settings.TCP_SERVER:
      		portType = "網路服務端";
      		break;
  		default:
  			portType = "錯誤連接口";
  			break;
      	}
			model.addRow(new Object[]{
							portType,														
							pm.getName() == null ? "" : pm.getName(),						
							pm.getBaudRate() == null ? "" : pm.getBaudRate(),				
							pm.getDataBites() == null ? "" : pm.getDataBites(),			    
							pm.getParity() == null ? "" : pm.getParity(),					
//							pm.getStartBits() == null ? "" : pm.getStartBits(),				
							pm.getStopBits() == null ? "" : pm.getStopBits(),				
							pm.getFlowControl() == null ? "" : pm.getFlowControl(),			
							pm.getAddress() == null ? "" : pm.getAddress(),					
							pm.getPortNumber() == null ? "" : pm.getPortNumber(),			
							pm.getMode()													
						});
 		}
      tblPortConnectionList.setModel(model);
      tblPortConnectionList.getColumnModel().getColumn(PM_PT).setPreferredWidth(100);
      tblPortConnectionList.getColumnModel().getColumn(PM_NAME).setPreferredWidth(100);
      tblPortConnectionList.getColumnModel().getColumn(PM_BR).setPreferredWidth(50);
      tblPortConnectionList.getColumnModel().getColumn(PM_DB).setPreferredWidth(50);
      tblPortConnectionList.getColumnModel().getColumn(PM_PAR).setPreferredWidth(50);
      tblPortConnectionList.getColumnModel().getColumn(PM_EB).setPreferredWidth(50);
      tblPortConnectionList.getColumnModel().getColumn(PM_FC).setPreferredWidth(80);
      tblPortConnectionList.getColumnModel().getColumn(PM_ADDR).setPreferredWidth(120);
      tblPortConnectionList.getColumnModel().getColumn(PM_PORT).setPreferredWidth(60);
      tblPortConnectionList.getTableHeader().getColumnModel().getColumn(PM_TYPE).setMinWidth(0);
      tblPortConnectionList.getTableHeader().getColumnModel().getColumn(PM_TYPE).setMaxWidth(0);
		validate();
		
	}
	
	private boolean inputIP(String value) {
		if(Optional.ofNullable(value).orElse("").length() <= 0)
			return true;
		String[] strValue = value.split("\\.");
		for(String str : strValue) {
		int ipValue = Integer.valueOf(str);
		if((ipValue < 0) || (ipValue > 255))
			return false;
		}
		return true;
	}
	
	private boolean checkIpAddress(String ip, char ekey) {
  		if(ip.length() <=0 && (ekey == '.' || ekey == '0')) {	//第一位不能為.和0
  			return false;
  		}
  		
  		String[] ipParts = ip.split("\\.");
  		if((ekey == '.') && ((ip.endsWith(".")) || (ipParts.length >= 4))) { //如果已經輸入4位地址，不能再輸入.
  			return false;
  		}
  		
  		if ((ekey != '.') && ipParts.length >= 1 && (!ip.endsWith("."))){ //判斷輸入地址是否合理
  			String lastPart = ipParts[ipParts.length - 1] + ekey;
  			if(Integer.valueOf(lastPart) > 255) {
	  			return false;
  			}
  		}
  		
  		if((ip.endsWith(".")) && (ekey == '0')) {
  			return false;
  		}
  		
  		return true;
	
	}

}
