package com.mmh.vmma.ui.panels;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mmh.vmma.controlcenter.ResponseCode;
import com.mmh.vmma.controlcenter.RestfulLogout;
import com.mmh.vmma.controlcenter.RestfulProvideQuery;
import com.mmh.vmma.controlcenter.RestfulTMMedicines;
import com.mmh.vmma.controlcenter.model.request.ReqLogout;
import com.mmh.vmma.controlcenter.model.request.ReqProvideQuery;
import com.mmh.vmma.controlcenter.model.request.ReqTMedicines;
import com.mmh.vmma.controlcenter.model.response.ResProvideQuery;
import com.mmh.vmma.controlcenter.model.response.ResTMedicines;
import com.mmh.vmma.mina.socket.signage.ISignageCallBack;
import com.mmh.vmma.ui.common.GlobalData;
import com.mmh.vmma.ui.common.ResponseMessage;
import com.mmh.vmma.ui.common.Settings;
import com.mmh.vmma.ui.dialogs.DlgGeneralProviding;
import com.mmh.vmma.ui.frames.MainWindow;
import com.mmh.vmma.ui.templates.JCommonLabel;
import com.mmh.vmma.ui.templates.JCommonPanel;
import com.mmh.vmma.ui.templates.JCommonTable;
import com.mmh.vmma.ui.templates.JCommonTextField;
import com.mmh.vmma.ui.templates.JPlaintButton;
import com.mmh.vmma.ui.templates.JRoundButton;
import com.mmh.vmma.utils.CommonUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

@Component
public class JPMKGeneralProvide extends JCommonPanel 
implements ISignageCallBack{

	private static final long serialVersionUID = 4174994557349879486L;
	private static final Logger logger = LogManager.getLogger(JPMKGeneralProvide.class);
	
	private static final int ROW_HEIGHT = 30;
	
	private final String[] alarmDrugsHeader = {"藥物編號", "藥物名稱", "警示量", "現存總量", "isAlarm"};
	private final int[] alarmDrugsHeaderSize = {50, 618, 50, 50, 0};

	private List<ResTMedicines.DataField> lsIsAlarmShow = new ArrayList<ResTMedicines.DataField>();
	
	@Autowired
	GlobalData globalData;

	@Autowired
	private MainWindow mainWindow;
	
	@Autowired
	DlgGeneralProviding dlgGeneralProviding;

	@Autowired
	private RestfulTMMedicines restTerminalMedicines;
	
	@Autowired
	private RestfulProvideQuery restProvideQuery;
	
	@Autowired
	private RestfulLogout restLogout;

	//自定義變量
	private int totalRows = 0; 				//記錄共有多少條
	private int rowsOfOnePage = 0; 			//每頁可顯示記錄數
	private int pages = 0; 					//共有頁數
	private String nowPageNo = "1";
	
	private boolean isReading = false;

	/**
	 * 輸入藥品barcode窗口
	 */
	private String title = "調劑 - ";
	private String barcodeNo1 = "";
	private String barcodeNo2 = "";
	
//	GetDrugInfoResponse resDrugInfo = new GetDrugInfoResponse();
	

	//控件變量
	private JCommonPanel jpTitle;
	private JCommonPanel jpOptionBar;
	private JCommonLabel lblUserId;
	private JCommonLabel lblShowMessage;
	private JRoundButton btnBack;
	
	//barcode輸入區
	private JCommonPanel jpBarcodeInput;
	private JCommonLabel lblBarcodeNo1;
	private JCommonTextField txtBarcodeNo1;
	private JCommonLabel lblBarcodeNo2;
	private JCommonTextField txtBarcodeNo2;
	private JRoundButton btnConfirm;
	private JRoundButton btnClear;
	private JRoundButton btnTerminalDrugsStatus;
	
	//藥品庫存量告警顯示區
	private JCommonPanel jpAlarmDrugs;
	private JScrollPane  jspAlarmDrugsList;
	private JCommonTable tblAlarmDrugs;
	
	//翻頁欄
	private JCommonPanel jpNextList;		//上下頁操作區
	private JPlaintButton btn1stPage;
	private JPlaintButton btnPreviousPage;
	private JPlaintButton btnNextPage;
	private JPlaintButton btnLastPage;
	private JPlaintButton btnJumpToPage;
	private JTextField txtPageNo;
	private JCommonLabel lblRecordsPages;

	public JPMKGeneralProvide() {
		initWindow();
	}


	private void initWindow(){
		setLayout(new BorderLayout(0, 0));

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int height = getHeight();
				jpTitle.setPreferredSize(new Dimension(0, height / 7));
				jpAlarmDrugs.setPreferredSize(new Dimension(0, height  * 7 / 15));
			}
			@Override
			public void componentShown(ComponentEvent e) {
				System.out.println("員工號: " + mainWindow.getUserId() + "; 員工名: " + mainWindow.getUserName());
				lblUserId.setText("員工號: " + mainWindow.getUserId() + "; 員工名: " + mainWindow.getUserName());
//				resDrugInfo.clear();
				txtBarcodeNo1.setText("");
				txtBarcodeNo2.setText("");
				txtBarcodeNo1.requestFocus();
				getAlarmDrugsList();

			}
			@Override
			public void componentHidden(ComponentEvent e) {
				initTables();
			}
			
		});
		
		jpTitle = new JCommonPanel();
		jpTitle.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
//				int width = jpTitle.getWidth();
//				int height = jpTitle.getHeight();
				btnBack.setPreferredSize(new Dimension(150, 0));

			}
			
		});
		jpTitle.setLayout(new BorderLayout(0, 0));
		add(jpTitle, BorderLayout.NORTH);
		
		
		jpOptionBar = new  JCommonPanel();
		jpOptionBar.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int width = jpOptionBar.getWidth();
				int height = jpOptionBar.getHeight();
				lblUserId.setBounds(0, 0, width, height);
			}
		});
		jpOptionBar.setLayout(null);
		jpOptionBar.setPreferredSize(new Dimension(0, 30));
		jpTitle.add(jpOptionBar, BorderLayout.NORTH);
		
		lblUserId = new JCommonLabel("");
		lblUserId.setFont(new Font("黑體", Font.BOLD, 20));
		lblUserId.setBounds(0, 0, 200, 30);
		jpOptionBar.add(lblUserId);
		
		lblShowMessage = new JCommonLabel(title + "請掃描藥袋下方條碼");
		lblShowMessage.setFont(new Font("楷体", Font.BOLD, 60));
		lblShowMessage.setHorizontalAlignment(SwingConstants.CENTER);
		jpTitle.add(lblShowMessage, BorderLayout.CENTER);

		btnBack = new JRoundButton("退出");
		btnBack.setFont(new Font("黑体", Font.BOLD, 23));
		btnBack.setIcon(new ImageIcon("images/back.png"));
		btnBack.setForeground(Color.WHITE);
		btnBack.setBounds(291, 382, 143, 88);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logout();
				mainWindow.checkOptions(Settings.OPTIION_LOGIN, "登錄");
			}
		});
		jpTitle.add(btnBack, BorderLayout.EAST);

		jpBarcodeInput = new JCommonPanel();
		jpBarcodeInput.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int width = jpBarcodeInput.getWidth();
				int height = jpBarcodeInput.getHeight();
				
				//两个标签占jpLoginInfo的3/5, 两个标签之间距离为80,宽度为64
				//标签长度为270,输入框长度为500
				int x = (width - 870) / 2;
				int y = height / 5 - 32;		
				lblBarcodeNo1.setBounds(x, y, 270, 64); //控件64高
				x += 270;
				txtBarcodeNo1.setBounds(x, y, 600, 64);
				txtBarcodeNo1.setText("");
				txtBarcodeNo1.requestFocus();
				
				x = (width - 870) / 2;
				y = height / 2 - 32;
				lblBarcodeNo2.setBounds(x, y, 270, 64); //控件64高
				x += 270;
				txtBarcodeNo2.setBounds(x, y, 600, 64);
				txtBarcodeNo2.setText("");
				txtBarcodeNo2.requestFocus();

				int gap = (width - 900) / 4;
				x = gap;
				int btnHeight = height * 3 / 10;
				int btnWidth = 300;
				
				y = height * 2 / 3; 				
				btnTerminalDrugsStatus.setBounds(x, y, btnWidth, btnHeight);
				
				x += btnWidth;
				x+= gap;
				btnClear.setBounds(x, y, btnWidth, btnHeight);
				
				x += btnWidth;
				x+=gap;
				btnConfirm.setBounds(x, y, btnWidth, btnHeight);

			}
		});
		add(jpBarcodeInput, BorderLayout.CENTER);		
	
		jpBarcodeInput.setLayout(null);
		
		lblBarcodeNo1 = new JCommonLabel("藥袋下方條碼：");
		lblBarcodeNo1.setBounds(61, 10, 270, 41);
		lblBarcodeNo1.setFont(new Font("黑体", Font.BOLD, 35));
//		lblBarcodeNo1.setIcon(new ImageIcon("images/account.png"));
		jpBarcodeInput.add(lblBarcodeNo1);
		
		txtBarcodeNo1 = new JCommonTextField();
		txtBarcodeNo1.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				lblShowMessage.setText(title + "請掃描藥袋下方條碼");
				txtBarcodeNo1.selectAll();
			}
		});
		txtBarcodeNo1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					if(txtBarcodeNo1.getText().length() > 0){
						barcodeNo1 = txtBarcodeNo1.getText();
						txtBarcodeNo2.setText("");
						txtBarcodeNo2.requestFocus();
					}else{
						txtBarcodeNo1.selectAll();
						txtBarcodeNo1.requestFocus();
					}
				}
			}
		});
		
		txtBarcodeNo1.setFont(new Font("黑体", Font.BOLD, 40));
		txtBarcodeNo1.setBounds(27, 64, 500, 64);
		jpBarcodeInput.add(txtBarcodeNo1);
		txtBarcodeNo1.setColumns(10);

		lblBarcodeNo2 = new JCommonLabel("藥袋上方條碼：");
		lblBarcodeNo2.setBounds(61, 138, 270, 50);
		lblBarcodeNo2.setFont(new Font("黑体", Font.BOLD, 35));
		jpBarcodeInput.add(lblBarcodeNo2);
		
		txtBarcodeNo2 = new JCommonTextField();
		txtBarcodeNo2.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				lblShowMessage.setText(title + "請掃描藥袋上方條碼");
				txtBarcodeNo2.selectAll();
			}
		});
		txtBarcodeNo2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					if(txtBarcodeNo2.getText().length() > 0){
						barcodeNo2 = txtBarcodeNo2.getText();
						if(!provideDrug()){
							txtBarcodeNo1.requestFocus();
							txtBarcodeNo1.selectAll();
							return;
						}

						mainWindow.checkOptions(Settings.OPTION_MKGENERAL_DRUG, "調劑");
					}else{
						txtBarcodeNo2.selectAll();
						txtBarcodeNo2.requestFocus();
					}
				}
			}
		});

		txtBarcodeNo2.setFont(new Font("黑体", Font.BOLD, 40));
		txtBarcodeNo2.setBounds(27, 64, 500, 64);
		jpBarcodeInput.add(txtBarcodeNo2);
		txtBarcodeNo2.setColumns(10);

		btnConfirm = new JRoundButton("確認");
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				barcodeNo1 = txtBarcodeNo1.getText();
				if((barcodeNo1 == null) || (barcodeNo1.length() <= 0)){
					JOptionPane.showMessageDialog(null,  "請輸入藥品條碼！", "錯誤", JOptionPane.ERROR_MESSAGE);
					txtBarcodeNo1.requestFocus();
					txtBarcodeNo1.selectAll();
					return;
				}
				barcodeNo2 = txtBarcodeNo2.getText();
				if((barcodeNo2 == null) || (barcodeNo2.length() <= 0)){
					JOptionPane.showMessageDialog(null,  "請輸入藥品條碼！", "錯誤", JOptionPane.ERROR_MESSAGE);
					txtBarcodeNo2.requestFocus();
					txtBarcodeNo2.selectAll();
					return;
				}
				//輸入員工號后登陸
				if(!provideDrug()){
					txtBarcodeNo1.requestFocus();
					txtBarcodeNo1.selectAll();
					return;
				}
				mainWindow.checkOptions(Settings.OPTION_MKGENERAL_DRUG, "調劑");
			}
		});
		btnConfirm.setFont(new Font("黑体", Font.BOLD, 30));
		btnConfirm.setIcon(new ImageIcon("images/check.png"));
		btnConfirm.setForeground(Color.WHITE);
		btnConfirm.setBounds(106, 382, 300, 100);
		jpBarcodeInput.add(btnConfirm);

		btnClear = new JRoundButton("清除");
		btnClear.setFont(new Font("黑体", Font.BOLD, 30));
		btnClear.setIcon(new ImageIcon("images/clear.png"));
		btnClear.setForeground(Color.WHITE);
		btnClear.setBounds(291, 382, 143, 88);
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtBarcodeNo1.setText("");
				txtBarcodeNo2.setText("");
				txtBarcodeNo1.requestFocus();
			}
		});
		jpBarcodeInput.add(btnClear);
		
		btnTerminalDrugsStatus = new JRoundButton("即時藥存量");
		btnTerminalDrugsStatus.setFont(new Font("黑体", Font.BOLD, 30));
		btnTerminalDrugsStatus.setIcon(new ImageIcon("images/medicines.png"));
		btnTerminalDrugsStatus.setForeground(Color.WHITE);
		btnTerminalDrugsStatus.setBounds(291, 382, 143, 88);
		btnTerminalDrugsStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainWindow.checkOptions(Settings.OPTION_MKDRUGS_STATUS, "藥品狀態");
			}
		});
		jpBarcodeInput.add(btnTerminalDrugsStatus);
		
		
		jpAlarmDrugs = new JCommonPanel();
		add(jpAlarmDrugs, BorderLayout.SOUTH);
		jpAlarmDrugs.setLayout(new BorderLayout(0,0));
		jspAlarmDrugsList = new JScrollPane();
		jspAlarmDrugsList.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "藥品補藥提示列表", 
				TitledBorder.LEFT, TitledBorder.TOP, new Font("黑体", Font.BOLD, 25), new Color(255, 0, 0)));

		jspAlarmDrugsList.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		jspAlarmDrugsList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jspAlarmDrugsList.setBackground(new Color(181, 223, 226));
		jpAlarmDrugs.add(jspAlarmDrugsList, BorderLayout.CENTER);
		tblAlarmDrugs = new JCommonTable();
		tblAlarmDrugs.setBackground(new Color(181, 223, 226));
		tblAlarmDrugs.getTableHeader().setPreferredSize(new Dimension(0, ROW_HEIGHT));
		tblAlarmDrugs.getTableHeader().setBackground(new Color(135, 206,250));
		tblAlarmDrugs.setFont(new Font("黑体", Font.PLAIN, 17));
		tblAlarmDrugs.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		tblAlarmDrugs.getTableHeader().setReorderingAllowed(false);
		tblAlarmDrugs.getTableHeader().setResizingAllowed(true);
		tblAlarmDrugs.getTableHeader().setFont(new Font("黑体", Font.BOLD, 20));
		tblAlarmDrugs.setFont(new Font("黑体", Font.BOLD, 20));
		tblAlarmDrugs.setRowHeight(ROW_HEIGHT);
		tblAlarmDrugs.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		tblAlarmDrugs.setAutoCreateRowSorter(true);
		jspAlarmDrugsList.setViewportView(tblAlarmDrugs);
		
		initTables();
		setChangePageArea();
		
	}
	
	/**設置翻頁欄*/
	private void setChangePageArea(){
		//翻頁欄
		jpNextList = new JCommonPanel();
		jpNextList.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int width = jpNextList.getWidth();
		  		int height = jpNextList.getHeight();
		  		int btnWidth = 70;
		  		int txtWidth = 70;
		  		int lblWidth = 70;
		  		int xPos = width - btnWidth * 5 - btnWidth - lblWidth;
				btn1stPage.setBounds(xPos, 0, btnWidth, height);
				xPos += btnWidth;
				btnPreviousPage.setBounds(xPos, 0, btnWidth, height);
				xPos += btnWidth;
				btnNextPage.setBounds(xPos, 0, btnWidth, height);
				xPos += btnWidth;
				btnLastPage.setBounds(xPos, 0, btnWidth, height);
				xPos += btnWidth;
				txtPageNo.setBounds(xPos, 0, txtWidth, height);
				xPos += txtWidth;
				lblRecordsPages.setBounds(xPos, 0, lblWidth, height);
				xPos += lblWidth;
				btnJumpToPage.setBounds(width - 70, 0, btnWidth, height);
			}
		});
		jpNextList.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jpAlarmDrugs.add(jpNextList, BorderLayout.SOUTH);
		jpNextList.setLayout(null);
		jpNextList.setPreferredSize(new Dimension(0, 50));
		
		btn1stPage = new JPlaintButton("|<<");
		btn1stPage.setFont(new Font("黑体", Font.PLAIN, 17));
		btn1stPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtPageNo.setText("1");
				nowPageNo = txtPageNo.getText().trim();
				refreshMedicineList();
			}
		});
		btn1stPage.setToolTipText("第一頁");
		jpNextList.add(btn1stPage);
		
		btnPreviousPage = new JPlaintButton("<");
		btnPreviousPage.setFont(new Font("黑体", Font.PLAIN, 17));
		btnPreviousPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int currentPage = Integer.valueOf(txtPageNo.getText());
				if (currentPage <= 1)
					return;
				
				txtPageNo.setText(String.valueOf(--currentPage));
				nowPageNo = txtPageNo.getText().trim();
				refreshMedicineList();

			}
		});
		btnPreviousPage.setToolTipText("前一頁");
		jpNextList.add(btnPreviousPage);
		
		btnNextPage = new JPlaintButton(">");
		btnNextPage.setFont(new Font("黑体", Font.PLAIN, 17));
		btnNextPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int currentPage = Integer.valueOf(txtPageNo.getText());
				if (currentPage >= pages)
					return;
				
				txtPageNo.setText(String.valueOf(++currentPage));
				nowPageNo = txtPageNo.getText().trim();
				refreshMedicineList();
			}
		});
		btnNextPage.setToolTipText("後一頁");
		jpNextList.add(btnNextPage);
		
		btnLastPage = new JPlaintButton(">>|");
		btnLastPage.setFont(new Font("黑体", Font.PLAIN, 17));
		btnLastPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtPageNo.setText(String.valueOf(pages));
				nowPageNo = txtPageNo.getText().trim();
				refreshMedicineList();
			}
		});
		btnLastPage.setToolTipText("最後頁");
		jpNextList.add(btnLastPage);
		
		txtPageNo = new JTextField();
		txtPageNo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
		  		char ekey = e.getKeyChar();
		  		if(ekey < '0' || ekey > '9'){
		  			e.setKeyChar('\0');
		  		}
			}
		});
		txtPageNo.setFont(new Font("黑体", Font.BOLD, 17));
		jpNextList.add(txtPageNo);
		
		lblRecordsPages = new JCommonLabel("/50頁");
		lblRecordsPages.setFont(new Font("黑体", Font.BOLD, 17));
		jpNextList.add(lblRecordsPages);
		
		btnJumpToPage = new JPlaintButton("Go");
		btnJumpToPage.setFont(new Font("黑体", Font.PLAIN, 17));
		btnJumpToPage.setToolTipText("跳轉至");
		btnJumpToPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshMedicineList();
			}
		});
		btnJumpToPage.setBounds(484, 0, 70, 50);
		jpNextList.add(btnJumpToPage);
		
	}
	
	private boolean provideDrug() {
		
		barcodeNo1 = txtBarcodeNo1.getText();
		barcodeNo2 = txtBarcodeNo2.getText();
		isReading = false;
		new Thread(new Runnable() {
			public void run() {
				int i = 0;
				String showText = title + "正在讀取藥訊息";
				while (!isReading){
					i++;
					try{
						if(i >= 10) {
							i = 0;
							showText = title + "正在讀取藥訊息";
						}
						showText += ".";
						lblShowMessage.setText(showText);
						Thread.sleep(500);
					}catch(Exception e){
						
					}
				}
			 }
		}).start();
		
		ResProvideQuery resProvideQuery = getDrugInfo();
		isReading = true;
		
		if (resProvideQuery == null) {
			lblShowMessage.setText(title + "讀取取藥訊息錯誤。");
			return false;
		}
		
		ResProvideQuery.DataField data = resProvideQuery.getData();
		
		lblShowMessage.setText(title + "準備取藥......");
		if (data.getMedicinedata().size() <= 0) {
			lblShowMessage.setText(title + "沒有待取藥物列表。");
			return false;
		}
		txtBarcodeNo1.setText("");
		txtBarcodeNo2.setText("");
		dlgGeneralProviding.setMedicine(data);
		dlgGeneralProviding.setModal(true);
		dlgGeneralProviding.setVisible(true);
		
		return true;
	}
	/**
	 * 獲取藥品信息
	 * */
	private ResProvideQuery getDrugInfo(){

		try {
			
			ReqProvideQuery reqProvideQuery = new ReqProvideQuery();
			reqProvideQuery.setToken(mainWindow.getUserToken());
			reqProvideQuery.getData().setTerminalId(globalData.getTerminalId());
			reqProvideQuery.getData().setChartNo("");
			reqProvideQuery.getData().setPhrOrderNo("");
			reqProvideQuery.getData().setBarcodeNo1(barcodeNo1);
			reqProvideQuery.getData().setBarcodeNo2(barcodeNo2);
			reqProvideQuery.getData().getMedicinedata();
			ResProvideQuery resProvideQuery = restProvideQuery.doPost(reqProvideQuery);
			
			
			
			if(ResponseCode.OK != Integer.valueOf(resProvideQuery.getResult())) {
				logger.error("讀取藥品列表錯誤, 原因: {}", resProvideQuery.getMessage());
				JOptionPane.showMessageDialog(null,  resProvideQuery.getMessage() ,"錯誤",
						JOptionPane.ERROR_MESSAGE);
				return null;
			}
			
			return resProvideQuery;
			
		}catch(Throwable e) {
			logger.error("讀取藥品列表錯誤!", e);
			return null;
		}
	}


	@Override
	public void update(ResponseMessage message) {
		
	}
	
	private void initTables() {
		txtBarcodeNo1.setText("");
		txtBarcodeNo2.setText("");
		tblAlarmDrugs.removeAll();
		DefaultTableModel mdMedicines = new DefaultTableModel();

		mdMedicines.setColumnCount(alarmDrugsHeader.length);
		mdMedicines.setColumnIdentifiers(alarmDrugsHeader);
		
		
		//"藥物編號", "藥物名稱", "警示量", "現存總量", "isAlarm"

		CommonUtils.setTable(tblAlarmDrugs, mdMedicines, alarmDrugsHeaderSize);
		jspAlarmDrugsList.validate();
		tblAlarmDrugs.validate();
		jpAlarmDrugs.validate();

	}

	//獲取告警量藥品ID列表
	private void getAlarmDrugsList() {
		try {
			lsIsAlarmShow.clear();
			List<ResTMedicines.DataField> lsMedicines = null;
			ReqTMedicines reqTerminalMedicines = new ReqTMedicines();
			reqTerminalMedicines.setToken(mainWindow.getUserToken());
			reqTerminalMedicines.getData().setTerminalId(globalData.getTerminalId());
			reqTerminalMedicines.getData().setMedicineId("");
			reqTerminalMedicines.getData().setPhoto(false);
			
			ResTMedicines resTerminalMedicines = restTerminalMedicines.doPost(reqTerminalMedicines);
			if(ResponseCode.OK != Integer.valueOf(resTerminalMedicines.getResult())) {
				logger.error("讀取藥品列表錯誤, 原因: {}", resTerminalMedicines.getMessage());
				lsMedicines = null;
				JOptionPane.showMessageDialog(null,  "讀取藥品列表錯誤，請聯繫管理員！！","錯誤",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			
			lsMedicines = resTerminalMedicines.getData();
			
			for(ResTMedicines.DataField medicine : lsMedicines){
				if(medicine.isAlarm()){
					lsIsAlarmShow.add(medicine);
				}
			}

			totalRows = lsIsAlarmShow.size();
			
			rowsOfOnePage = Math.round(jspAlarmDrugsList.getHeight() / ROW_HEIGHT) - 2;
			pages = (int)(Math.ceil((double)totalRows/rowsOfOnePage));
			txtPageNo.setText("1");
			lblRecordsPages.setText("/"+String.valueOf(pages)+"頁");
			
			refreshMedicineList();
		}catch(Throwable e) {
			logger.error("讀取藥品列表錯誤!", e);
			return;
		}
		
	}
	
	/**刷新列表*/
	private void refreshMedicineList(){

		DefaultTableModel mdMedicines = new DefaultTableModel();

		mdMedicines.setColumnCount(alarmDrugsHeader.length);
		mdMedicines.setColumnIdentifiers(alarmDrugsHeader);
		
		
//"藥物編號", "藥物名稱", "警示量", "現存總量", "isAlarm"
        if (rowsOfOnePage >= totalRows){		//一頁顯示
			for(ResTMedicines.DataField medicine : lsIsAlarmShow){
				mdMedicines.addRow(new Object[]{medicine.getMedicineId(), medicine.getMedicineName(), 
						String.valueOf(medicine.getAlarmQty()), String.valueOf(medicine.getQuantity()), medicine.isAlarm()});
			}
		
        }else{								//分頁顯示
        	int startRow = Integer.valueOf(txtPageNo.getText());
        	int endRow = 0;
        	if (startRow <= 1){
        		startRow = 0;
        	}
        	else{
        		startRow = rowsOfOnePage * (startRow - 1);
        	}
        	endRow = startRow + rowsOfOnePage;
        	if(endRow >= totalRows){
        		endRow = totalRows;
        	}
			for (int i = startRow; i < endRow; i++){
				ResTMedicines.DataField medicine = lsIsAlarmShow.get(i);
				mdMedicines.addRow(new Object[]{medicine.getMedicineId(), medicine.getMedicineName(), 
						String.valueOf(medicine.getAlarmQty()), String.valueOf(medicine.getQuantity()), medicine.isAlarm()});
 			}
        }
        
        tblAlarmDrugs.setModel(mdMedicines);
        
		//{"藥物編號", "藥名", "現存藥量", "功效", "是否達警告量"}
		CommonUtils.setTable(tblAlarmDrugs, mdMedicines, alarmDrugsHeaderSize);
        setOneRowBackgroundColor(Color.RED, Color.WHITE);
        validate();
	
	}

	//修改單元格顏色
    private void setOneRowBackgroundColor(Color colorBG, Color colorFG) { 
    	
    	//"藥物編號", "藥名", "現存藥量", "功效", "是否達警告量"
        try {
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {
  
				private static final long serialVersionUID = -7406394429567520302L;

				public java.awt.Component getTableCellRendererComponent(JTable table,  Object value, 
						boolean isSelected, boolean hasFocus, int row, int column) {

					Boolean isAlarm = (Boolean)table.getModel().getValueAt(row, 4);
					
					if(isAlarm){
                        setBackground(colorBG);  
                        setForeground(colorFG);
					}else{
                        setBackground(new Color(181, 223, 226));  
                        setForeground(Color.BLACK);  
					}
					
                    return super.getTableCellRendererComponent(table, value,  
                            isSelected, hasFocus, row, column);  
                }  
            };
            int columnCount = tblAlarmDrugs.getColumnCount();  
            for (int i = 0; i < columnCount; i++) {
            	tblAlarmDrugs.getColumn(tblAlarmDrugs.getColumnName(i)).setCellRenderer(tcr);
            }  
        } catch (Exception ex) {  
            ex.printStackTrace();
            logger.error("System Error", ex);
        }  
    }
    
    private void logout() {
		try {
			ReqLogout reqLogout = new ReqLogout();
			reqLogout.setToken(mainWindow.getUserToken());
			
			restLogout.doPost(reqLogout);
			
			
		}catch(Throwable e) {
			logger.error("登出錯誤!", e);
			return;
		}

    }
	
}
