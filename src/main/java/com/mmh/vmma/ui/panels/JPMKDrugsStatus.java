package com.mmh.vmma.ui.panels;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mmh.vmma.controlcenter.ResponseCode;
import com.mmh.vmma.controlcenter.RestfulTMMedicines;
import com.mmh.vmma.controlcenter.model.VMMedicine;
import com.mmh.vmma.controlcenter.model.request.ReqTMedicines;
import com.mmh.vmma.controlcenter.model.response.ResTMedicines;
import com.mmh.vmma.ui.common.GlobalData;
import com.mmh.vmma.ui.common.Settings;
import com.mmh.vmma.ui.dialogs.DlgBatchProvide;
import com.mmh.vmma.ui.dialogs.DlgMKDrugStatus;
import com.mmh.vmma.ui.frames.MainWindow;
import com.mmh.vmma.ui.templates.JCommonLabel;
import com.mmh.vmma.ui.templates.JCommonPanel;
import com.mmh.vmma.ui.templates.JCommonTable;
import com.mmh.vmma.ui.templates.JPlaintButton;
import com.mmh.vmma.utils.CommonUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.event.MouseAdapter;
import java.math.BigDecimal;

@Component
public class JPMKDrugsStatus extends JCommonPanel {

	private static final long serialVersionUID = 4174994557349879486L;
	private static final Logger logger = LogManager.getLogger(JPMKDrugsStatus.class);
	
	private final int topBottomHeight = 50;

	private final String[] medicineHeader = {"藥物編號", "藥名", "現存總藥量", "儲位現存量", "是否達警告量", "儲位號"};
	private final int[] medicineHeaderSize = {50, 618, 50, 50, 0, 30};
	
	@Autowired
	GlobalData globalData;

	@Autowired
	private MainWindow mainWindow;
	
	@Autowired
	private RestfulTMMedicines restTerminalMedicines;
	
	@Autowired
	private DlgBatchProvide dlgMKBtchProvide;
	
	//自定義變量
	private int totalRows = 0; 				//記錄共有多少條
	private int rowsOfOnePage = 0; 			//每頁可顯示記錄數
	private int pages = 0; 					//共有頁數
	private String nowPageNo = "1";
	
	private List<ResTMedicines.DataField> lsMedicines;
	private List<VMMedicine> lsVMMedicines = new ArrayList<VMMedicine>();
	private List<VMMedicine> lsSearchShow = new ArrayList<VMMedicine>();
	
	//控件變量
	//表頭區
	private JCommonPanel jpTitle;
	private JCommonLabel lblShowMessage;

	//藥品顯示區
	private JCommonPanel jpMedicines;
	private JCommonPanel jpSearch;			//搜索區
	private JCommonLabel lblSearchCondition;
	private JComboBox<String> cbField;
	private JComboBox<String> cbCondition;
	private JTextField txtValue;
	private JPlaintButton btnSearch;
//	private JCommonLabel lblPatientInfo;
	
	private JScrollPane srpMedicines;		//藥品列表
	private JCommonTable tblMedicines;

	//翻頁欄
	private JCommonPanel jpNextList;		//上下頁操作區
	private JPlaintButton btn1stPage;
	private JPlaintButton btnPreviousPage;
	private JPlaintButton btnNextPage;
	private JPlaintButton btnLastPage;
	private JPlaintButton btnJumpToPage;
	private JTextField txtPageNo;
	private JCommonLabel lblRecordsPages;

	//按鈕區
	private JCommonPanel jpFunctionButton;
	private JPlaintButton btnShowDetail;
	private JPlaintButton btnBatchProvide;
	private JPlaintButton btnBack;

	
	public JPMKDrugsStatus() {
		setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		int nFunctionButtonWidth = 70;
		
		//窗口事件
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				getMedicineList();
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				initTables();
			}
			
		});
		
		setLayout(new BorderLayout(0, 0));
		
		//信息顯示區
		jpTitle = new JCommonPanel();
		jpTitle.setBorder(new MatteBorder(0, 0, 1, 0, (Color) Color.RED));
		jpTitle.setPreferredSize(new Dimension(0, 50));
		jpTitle.setLayout(new BorderLayout(0, 0));
		add(jpTitle, BorderLayout.NORTH);
		
		lblShowMessage = new JCommonLabel("馬偕醫院 - 即時藥存量列表");
		lblShowMessage.setHorizontalAlignment(SwingConstants.CENTER);
		lblShowMessage.setFont(new Font("楷体", Font.BOLD, 40));
		jpTitle.add(lblShowMessage);
				
		//藥物頁面
		jpMedicines = new JCommonPanel();
		jpMedicines.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				if(tblMedicines.getSelectedRow() >= 0){
					btnShowDetail.setEnabled(true);
					btnBatchProvide.setEnabled(true);
				}else{
					btnShowDetail.setEnabled(false);
					btnBatchProvide.setEnabled(false);
				}
			}
		});
		jpMedicines.setLayout(new BorderLayout(0, 0));
		add(jpMedicines, BorderLayout.CENTER);
		
		//搜索區
		jpSearch = new JCommonPanel();
		jpSearch.setPreferredSize(new Dimension(0, 0));
		jpMedicines.add(jpSearch, BorderLayout.NORTH);
		jpSearch.setLayout(null);
		
		lblSearchCondition = new JCommonLabel("搜索條件");
		lblSearchCondition.setBounds(5, 0, 100, 50);
		jpSearch.add(lblSearchCondition);
		
		cbField = new JComboBox<String>();
		cbField.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				txtValue.setText("");
			}
		});
		cbField.setModel(new DefaultComboBoxModel<String>((new String[]{"藥物編碼",
				"藥物名稱"})));
		cbField.setBounds(105, 0, 150, 50);
		cbField.setFont(new Font("黑体", Font.BOLD, 20));
		jpSearch.add(cbField);
		
		cbCondition = new JComboBox<String>();
		cbCondition.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				txtValue.setText("");
				if(e.getStateChange() == ItemEvent.SELECTED){
					if(cbCondition.getSelectedIndex() == 3){
						txtValue.setEnabled(false);
					}else{
						txtValue.setEnabled(true);
					}
				}
			}
		});
		cbCondition.setModel(new DefaultComboBoxModel<String>((new String[]{"包含", "開始值", 
				"結束值"})));
		cbCondition.setBounds(255, 0, 100, 50);
		cbCondition.setFont(new Font("黑体", Font.BOLD, 20));
		jpSearch.add(cbCondition);
		
		txtValue = new JTextField();
		txtValue.setBounds(355, 0, 250, 50);
		txtValue.setFont(new Font("黑体", Font.BOLD, 20));
		jpSearch.add(txtValue);
		
		btnSearch = new JPlaintButton("搜索");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SearchMedicines();
			}
		});
		btnSearch.setBounds(605, 0, 100, 50);
		jpSearch.add(btnSearch);
		
		//藥物列表區
		srpMedicines = new JScrollPane();
		jpMedicines.add(srpMedicines);

		srpMedicines.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		srpMedicines.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", 
				TitledBorder.CENTER, TitledBorder.TOP, new Font("宋体", Font.PLAIN, 20), new Color(255, 0, 0)));

		tblMedicines = new JCommonTable();
		tblMedicines.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//.SINGLE_INTERVAL_SELECTION);
		srpMedicines.setViewportView(tblMedicines);
		tblMedicines.addMouseListener(new MouseAdapter() {

		});
		tblMedicines.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(tblMedicines.getSelectedRow() >= 0){
					btnShowDetail.setEnabled(true);
					btnBatchProvide.setEnabled(true);
				}else{
					btnShowDetail.setEnabled(false);
					btnBatchProvide.setEnabled(false);
				}
			}
			
		});
		initTables();

		//設置翻頁區
		setChangePageArea();
		
		//功能按鈕區
		jpFunctionButton = new JCommonPanel();
		jpFunctionButton.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int jpWidth = jpFunctionButton.getWidth();
				int btnWidth = 200;
				int btnHeight = 60;
				int btnGap = 5;
				btnBack.setBounds(jpWidth - 2 * (btnWidth + btnGap), btnGap, btnWidth, btnHeight);
				btnShowDetail.setBounds(jpWidth - (btnWidth + btnGap), btnGap, btnWidth, btnHeight);
				btnBatchProvide.setBounds(btnGap, btnGap, btnWidth, btnHeight);
			}
		});
		jpFunctionButton.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", 
				TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		jpFunctionButton.setPreferredSize(new Dimension(0, nFunctionButtonWidth));
		add(jpFunctionButton, BorderLayout.SOUTH);
		
		btnBack = new JPlaintButton("返回");
		btnBack.setIcon(new ImageIcon("images/back.png"));
		btnBack.setBounds(0, 0, 95, 53);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainWindow.checkOptions(Settings.OPTION_MKGENERAL_DRUG, "調劑");
			}
		});
		jpFunctionButton.setLayout(null);
		jpFunctionButton.add(btnBack);
		
		btnShowDetail = new JPlaintButton("詳情");
		btnShowDetail.setIcon(new ImageIcon("images/manager.png"));
		btnShowDetail.setBounds(401, 7, 80, 50);
		btnShowDetail.setEnabled(false);
		btnShowDetail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] rows = tblMedicines.getSelectedRows();
				for(int row : rows) {
					String selectedMedicineID = ((String)tblMedicines.getValueAt(row, 0)).trim();
					String selectedBoxId = ((String)tblMedicines.getValueAt(row, 5)).trim();
					for(VMMedicine medicine : lsVMMedicines){
						if((medicine.getMedicineId().equalsIgnoreCase(selectedMedicineID)) &&
								medicine.getBoxId().equalsIgnoreCase(selectedBoxId)){
							DlgMKDrugStatus dlgMKDrugStatus = new DlgMKDrugStatus();
							dlgMKDrugStatus.setMedicine(medicine);
							dlgMKDrugStatus.setModal(true);
							dlgMKDrugStatus.setVisible(true);
							break;
						}
					}
					btnShowDetail.setEnabled(false);
					logger.info("緊急取藥選擇的藥品編號: [{}]", selectedMedicineID);
				}
				tblMedicines.clearSelection();
				refreshMedicineList();
//				setVisible(false);
			}
		});
		jpFunctionButton.add(btnShowDetail);
		
		btnBatchProvide = new JPlaintButton("批次取藥");
		btnBatchProvide.setIcon(new ImageIcon("images/medicines.png"));
		btnBatchProvide.setBounds(401, 7, 80, 50);
		btnBatchProvide.setEnabled(false);
		btnBatchProvide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = tblMedicines.getSelectedRow();
				String selectedMedicineID = ((String)tblMedicines.getValueAt(row, 0)).trim();
				String selectedBoxId = ((String)tblMedicines.getValueAt(row, 5)).trim();
				for(VMMedicine medicine : lsVMMedicines){
					if((medicine.getMedicineId().equalsIgnoreCase(selectedMedicineID)) &&
							medicine.getBoxId().equalsIgnoreCase(selectedBoxId)){
						
						BigDecimal bd1 = new BigDecimal(medicine.getTotalQuantity());
						BigDecimal bd2 = new BigDecimal(0);

						if (bd1.compareTo(bd2) == 1) {
							dlgMKBtchProvide.setMedicine(medicine.getMedicineId().trim(), 
									medicine.getMedicineName().trim(), medicine.getTotalQuantity());
							dlgMKBtchProvide.setModal(true);
							dlgMKBtchProvide.setVisible(true);
							break;
							
						}
						
					}
				}
				btnBatchProvide.setEnabled(false);
				logger.info("緊急取藥選擇的藥品編號: [{}]", selectedMedicineID);
			tblMedicines.clearSelection();
				refreshMedicineList();
//				setVisible(false);
			}
		});
		jpFunctionButton.add(btnBatchProvide);
	}
	
	private void initTables() {
		tblMedicines.removeAll();
		DefaultTableModel mdMedicines = new DefaultTableModel();
		mdMedicines.setColumnCount(medicineHeader.length);
		mdMedicines.setColumnIdentifiers(medicineHeader);
		CommonUtils.setTable(tblMedicines, mdMedicines, medicineHeaderSize);
//		tblMedicines.setModel(mdMedicines);
//		tblMedicines.getColumnModel().getColumn(0).setPreferredWidth(718);
//		tblMedicines.getColumnModel().getColumn(1).setPreferredWidth(50);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(2).setMinWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(3).setMinWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(4).setMinWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(4).setMaxWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(5).setMinWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(6).setMinWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(6).setMaxWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(7).setMinWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(7).setMaxWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(8).setMinWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(8).setMaxWidth(0);
		srpMedicines.validate();
		tblMedicines.validate();
		jpMedicines.validate();

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
		jpMedicines.add(jpNextList, BorderLayout.SOUTH);
		jpNextList.setLayout(null);
		jpNextList.setPreferredSize(new Dimension(0, topBottomHeight));
		
		btn1stPage = new JPlaintButton("|<<");
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
		txtPageNo.setFont(new Font("黑体", Font.BOLD, 20));
		jpNextList.add(txtPageNo);
		
		lblRecordsPages = new JCommonLabel("/50頁");
		lblRecordsPages.setFont(new Font("黑体", Font.BOLD, 20));
		jpNextList.add(lblRecordsPages);
		
		btnJumpToPage = new JPlaintButton("Go");
		btnJumpToPage.setToolTipText("跳轉至");
		btnJumpToPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshMedicineList();
			}
		});
		btnJumpToPage.setBounds(487, 0, 70, 50);
		jpNextList.add(btnJumpToPage);
		
	}

	/**
	 * 設置取藥類型：
	 * 所有類型 - 0
	 * 一二類藥 - 1
	 * 三四類藥 - 2
	 * 其它類藥 - 3
	 * */
	public void getMedicineList(){
		try {
			ReqTMedicines reqTMedicines = new ReqTMedicines();
			reqTMedicines.setToken(mainWindow.getUserToken());
			reqTMedicines.getData().setTerminalId(globalData.getTerminalId());
			reqTMedicines.getData().setMedicineId("");
			reqTMedicines.getData().setPhoto(true);
			ResTMedicines resTerminalMedicines = restTerminalMedicines.doPost(reqTMedicines);
			if(ResponseCode.OK != Integer.valueOf(resTerminalMedicines.getResult())) {
				logger.error("讀取藥品列表錯誤, 原因: {}", resTerminalMedicines.getMessage());
				lsMedicines = null;
				JOptionPane.showMessageDialog(null,  "讀取藥品列表錯誤，請聯繫管理員！！","錯誤",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			
			lsMedicines = resTerminalMedicines.getData();
			totalRows = resTerminalMedicines.getReccount();
			
			lsVMMedicines.clear();
			lsSearchShow.clear();
			for(ResTMedicines.DataField medicine : lsMedicines) {
				for(ResTMedicines.BoxInfoField box : medicine.getBoxInfo()) {
					VMMedicine vmMedicine = new VMMedicine();
					vmMedicine.setMedicineId(medicine.getMedicineId().trim());
					vmMedicine.setMedicineName(medicine.getMedicineName() == null ? "" : medicine.getMedicineName().trim());
					vmMedicine.setAlarmQty(medicine.getAlarmQty());
					vmMedicine.setAlarm(medicine.isAlarm());
					vmMedicine.setTotalQuantity(medicine.getQuantity());
					vmMedicine.setTotalMaxQuantity(medicine.getMaxQuantity());
					vmMedicine.setRfid(medicine.getRfid());
					vmMedicine.setEfficiency(medicine.getEfficiency());
					vmMedicine.setType(medicine.getType());
					vmMedicine.setManufacturer(medicine.getManufacturer());
					vmMedicine.setExpiredDate(medicine.getExpiredDate());
					vmMedicine.setDoseUnit(medicine.getDoseUnit());
					vmMedicine.setDoseRoute(medicine.getDoseRoute());
					vmMedicine.setDescription(medicine.getDescription());
					vmMedicine.setPackageType(medicine.getPackageType());
					vmMedicine.setEmergencyDrug(medicine.getEmergencyDrug());
					vmMedicine.setBrandCode(medicine.getBarcode());
					vmMedicine.setNcku9(medicine.getNcku9());
					vmMedicine.setPhoto(medicine.getPhoto());
					vmMedicine.setBarcode(medicine.getBarcode());
					vmMedicine.setPno(medicine.getPno());
					vmMedicine.setControl(medicine.getControl());
					vmMedicine.setBoxId(box.getBoxId());
					vmMedicine.setBoxQuantity(box.getQuantity());
					vmMedicine.setBoxMaxQuantity(box.getMaxQuantity());
					lsVMMedicines.add(vmMedicine);
				}
			}
			lsSearchShow.addAll(lsVMMedicines);
			
			totalRows = lsSearchShow.size();
			
			rowsOfOnePage = Math.round(srpMedicines.getHeight() / 50) - 1;
			pages = (int)(Math.ceil((double)totalRows/rowsOfOnePage));
			txtPageNo.setText("1");
			lblRecordsPages.setText("/"+String.valueOf(pages)+"頁");
			
			refreshMedicineList();
		}catch(Throwable e) {
			logger.error("讀取藥品列表錯誤!", e);
			lsMedicines = null;
			return;
		}
		
		
	}
	
	/**刷新列表*/
	private void refreshMedicineList(){

		DefaultTableModel mdMedicines = new DefaultTableModel();

		mdMedicines.setColumnCount(medicineHeader.length);
		mdMedicines.setColumnIdentifiers(medicineHeader);
		
		
//"藥物編號", "藥名", "現存總藥量", "儲位現存量", "是否達警告量"
        if (rowsOfOnePage >= totalRows){		//一頁顯示
			for(VMMedicine medicine : lsSearchShow){
				mdMedicines.addRow(new Object[]{medicine.getMedicineId(), medicine.getMedicineName(), 
						medicine.getTotalQuantity(), medicine.getBoxQuantity(), medicine.isAlarm(), medicine.getBoxId()});
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
				VMMedicine medicine = lsSearchShow.get(i);
				mdMedicines.addRow(new Object[]{medicine.getMedicineId(), medicine.getMedicineName(), 
						medicine.getTotalQuantity(), medicine.getBoxQuantity(), medicine.isAlarm(), medicine.getBoxId()});
 			}
        }
        
		tblMedicines.setModel(mdMedicines);
        
		//{"藥物編號", "藥名", "現存藥量", "功效", "是否達警告量"}
		CommonUtils.setTable(tblMedicines, mdMedicines, medicineHeaderSize);
//		tblMedicines.getColumnModel().getColumn(0).setPreferredWidth(718);
//		tblMedicines.getColumnModel().getColumn(1).setPreferredWidth(50);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(2).setMinWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(3).setMinWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(4).setMinWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(4).setMaxWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(5).setMinWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(6).setMinWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(6).setMaxWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(7).setMinWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(7).setMaxWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(8).setMinWidth(0);
//		tblMedicines.getTableHeader().getColumnModel().getColumn(8).setMaxWidth(0);
        setOneRowBackgroundColor(Color.RED, Color.WHITE);
        validate();
	
	}

	private void SearchMedicines(){
		int nFieldIndex = cbField.getSelectedIndex();//"藥物編碼", "藥物常用名", "藥物類型"
		int nCondition = cbCondition.getSelectedIndex();  //"包含", "開始值", "結束值"
		String value = txtValue.getText();  
		//"一類藥", "二類藥", "其它"
		
		lsSearchShow.clear();
		
//		if ((nFieldIndex < 4) && (nCondition < 3)
//				&& ((value == null) || (value.length() <= 0) )){
//			lsSearchShow.addAll(lsMedicine);
//		}else 
		if(nFieldIndex == 0){
			if((value == null) || (value.length() <= 0)){
				lsSearchShow.addAll(lsVMMedicines);
			}else{
				if((nCondition == 0)){	//藥品ID包含
					for(VMMedicine medicine : lsVMMedicines){
						if(medicine.getMedicineId() == null){
							continue;
						}
						if(medicine.getMedicineId().indexOf(value) >= 0){
							lsSearchShow.add(medicine);
						}
					}
					
				}else if(nCondition == 1){	//藥品ID開始
					for(VMMedicine medicine : lsVMMedicines){
						if(medicine.getMedicineId() == null){
							continue;
						}
						if(medicine.getMedicineId().startsWith(value)){
							lsSearchShow.add(medicine);
						}
					}
					
				}else if(nCondition == 2){	//藥品ID結尾
					for(VMMedicine medicine : lsVMMedicines){
						if(medicine.getMedicineId() == null){
							continue;
						}
						if(medicine.getMedicineId().endsWith(value)){
							lsSearchShow.add(medicine);
						}
					}
					
				}	
			}
		}else if(nFieldIndex == 1){
			if((value == null) || (value.length() <= 0)){
				lsSearchShow.addAll(lsVMMedicines);
			}else{
				if((nCondition == 0)){	//藥物常用名包含
					for(VMMedicine medicine : lsVMMedicines){
						if(medicine.getMedicineName() == null){
							continue;
						}
						if(medicine.getMedicineName().indexOf(value) >= 0){
							lsSearchShow.add(medicine);
						}
					}
					
				}else if(nCondition == 1){	//藥物常用名開始
					for(VMMedicine medicine : lsVMMedicines){
						if(medicine.getMedicineName() == null){
							continue;
						}
						if(medicine.getMedicineName().startsWith(value)){
							lsSearchShow.add(medicine);
						}
					}
					
				}else if(nCondition == 2){	//藥物常用名結尾
					for(VMMedicine medicine : lsVMMedicines){
						if(medicine.getMedicineName() == null){
							continue;
						}
						if(medicine.getMedicineName().endsWith(value)){
							lsSearchShow.add(medicine);
						}
					}
					
				}	
			}
		}
		
		totalRows = lsSearchShow.size();

		rowsOfOnePage = Math.round(srpMedicines.getHeight() / 50) - 1;
		pages = (int)(Math.ceil((double)totalRows/rowsOfOnePage));
		txtPageNo.setText(nowPageNo);
		lblRecordsPages.setText("/"+String.valueOf(pages)+"頁");

		refreshMedicineList();
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
            int columnCount = tblMedicines.getColumnCount();  
            for (int i = 0; i < columnCount; i++) {
            	tblMedicines.getColumn(tblMedicines.getColumnName(i)).setCellRenderer(tcr);
            }  
        } catch (Exception ex) {  
            ex.printStackTrace();
            logger.error("System Error", ex);
        }  
    }
    
}
