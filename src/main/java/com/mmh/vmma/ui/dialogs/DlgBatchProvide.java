package com.mmh.vmma.ui.dialogs;

import javax.swing.ImageIcon;
/**
 * 計劃取藥確認窗口
 * @author Tang Hongfeng
 * */
import javax.swing.JDialog;
import javax.swing.border.EmptyBorder;

import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mmh.vmma.controlcenter.CONSTANTS;
import com.mmh.vmma.controlcenter.ResponseCode;
import com.mmh.vmma.controlcenter.RestfulProvideQuery;
import com.mmh.vmma.controlcenter.RestfulProvideResult;
import com.mmh.vmma.controlcenter.model.request.ReqProvideQuery;
import com.mmh.vmma.controlcenter.model.request.ReqProvideResult;
import com.mmh.vmma.controlcenter.model.response.ResProvideQuery;
import com.mmh.vmma.controlcenter.model.response.ResProvideResult;
import com.mmh.vmma.mina.socket.signage.ISignageCallBack;
import com.mmh.vmma.mina.socket.signage.flow.Vendout;
import com.mmh.vmma.mina.socket.signage.queue.RequestQueue;
import com.mmh.vmma.ui.common.CODES;
import com.mmh.vmma.ui.common.GlobalData;
import com.mmh.vmma.ui.common.ResponseMessage;
import com.mmh.vmma.ui.frames.MainWindow;
import com.mmh.vmma.ui.templates.JCommonLabel;
import com.mmh.vmma.ui.templates.JCommonPanel;
import com.mmh.vmma.ui.templates.JCommonTextField;
import com.mmh.vmma.ui.templates.JRoundButton;
import com.mmh.vmma.utils.AESUtils;
import com.mmh.vmma.utils.CommonUtils;

import lombok.Data;

import javax.swing.UIManager;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@Component
public class DlgBatchProvide extends JDialog
implements ISignageCallBack{

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(DlgBatchProvide.class);
	
	private static final String BARCODENO1 = "MMH001";
//	private static final String BARCODENO2 = "MMH001";
	
	@Autowired
	private GlobalData globalData;

	@Autowired
	private MainWindow mainWindow;
	
	@Autowired
	private RequestQueue requestQueue;
	
	@Autowired
	private RestfulProvideQuery restProvideQuery;
	
	@Autowired
	private RestfulProvideResult restProvideResult;
	
	//自定義變量
	private String medicineId = "";
	private double stockQty = 0.0d;
	private ResProvideQuery.DataField medicines = null;
	private int planQty = 0;
	private int planTakeTimes = 0;
	private int takedTimes = 0;
	
	private String medicinePic = "";
	private HashMap<String, Integer> mapMedicinesProviding = new HashMap<String, Integer>();
	private HashMap<String, MedicineResult> mapMedicinesResult = new HashMap<String, MedicineResult>();
	
	private int drugQtyNow  = -1;
	
	//窗口組件
	private JCommonPanel jpMedicine;
	private JCommonLabel lblOpenMsg;
	private JCommonLabel lblWarning;
	private JCommonLabel lblMedicinePhoto;
	
	//藥品詳細信息
	private JCommonPanel jpMedicineInfo;
	private JCommonLabel lblMedicineID;
	private JCommonLabel lblMedicineName;
	private JCommonLabel lblBoxId;
	private JCommonLabel lblBoxQuantityNow;
	private JCommonLabel lblPlanSupply;
	private JCommonTextField textPlanSupply;
	private JRoundButton btnMinus;
	private JRoundButton btnPlus;
	

	//返回消息
	private JCommonLabel lblRturnMessage;

	//確認按鈕
	private JRoundButton btnCancel;
	private JRoundButton btnOK;

	/**
	 * Create the dialog.
	 */
	public DlgBatchProvide() {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				medicines = null;

			}
		});
		init();
		
//		subcriberMessage();
	}

	//初始化窗口
	private void init(){
		
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
//		setUndecorated(true);
		setType(Type.UTILITY);
		
		setBounds(100, 100, 850, 650);
		setLocationRelativeTo(null);
		
		getContentPane().setLayout(null);
		getContentPane().setBackground(new Color(181, 223, 226));
		jpMedicine = new JCommonPanel();
		jpMedicine.setBounds(0, 0, 748, 377);
		jpMedicine.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(jpMedicine);
		jpMedicine.setLayout(null);
		
		lblOpenMsg = new JCommonLabel("批次取藥");
		lblOpenMsg.setHorizontalAlignment(SwingConstants.CENTER);
		lblOpenMsg.setForeground(new Color(0, 0, 128));
		lblOpenMsg.setFont(new Font("楷体", Font.BOLD, 25));
		lblOpenMsg.setBounds(10, 10, 724, 37);
		jpMedicine.add(lblOpenMsg);

		lblWarning = new JCommonLabel("提示");
		lblWarning.setForeground(new Color(220, 20, 60));
		lblWarning.setFont(new Font("隶书", Font.BOLD, 30));
		lblWarning.setHorizontalAlignment(SwingConstants.CENTER);
		lblWarning.setBounds(20, 36, 84, 69);
		jpMedicine.add(lblWarning);

		lblMedicinePhoto = new JCommonLabel();
		lblMedicinePhoto.setBackground(Color.PINK);
		lblMedicinePhoto.setBounds(22, 115, 180, 245);
		jpMedicine.add(lblMedicinePhoto);
		
		jpMedicineInfo = new JCommonPanel();
		jpMedicineInfo.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "藥品訊息", 
				TitledBorder.LEADING, TitledBorder.TOP, new Font("楷体", Font.PLAIN, 22), new Color(220, 20, 60)));
		jpMedicineInfo.setBounds(232, 55, 492, 305);
		jpMedicine.add(jpMedicineInfo);
		jpMedicineInfo.setLayout(null);

		lblMedicineID = new JCommonLabel("藥物編號:");
		lblMedicineID.setBounds(10, 40, 472, 25);
		jpMedicineInfo.add(lblMedicineID);
		lblMedicineID.setForeground(new Color(220, 20, 60));
		lblMedicineID.setFont(new Font("楷体", Font.PLAIN, 20));

		lblMedicineName = new JCommonLabel("藥物名稱:");
		lblMedicineName.setForeground(new Color(220, 20, 60));
		lblMedicineName.setFont(new Font("楷体", Font.PLAIN, 20));
		lblMedicineName.setBounds(10, 95, 472, 25);
		jpMedicineInfo.add(lblMedicineName);
		
		lblBoxQuantityNow = new JCommonLabel("現存藥量:" );
		lblBoxQuantityNow.setBounds(10, 150, 230, 25);
		lblBoxQuantityNow.setFont(new Font("楷体", Font.PLAIN, 20));
		lblBoxQuantityNow.setForeground(new Color(220, 20, 60));
		jpMedicineInfo.add(lblBoxQuantityNow);
		
		lblBoxId = new JCommonLabel("儲位編號:");
		lblBoxId.setForeground(new Color(220, 20, 60));
		lblBoxId.setFont(new Font("楷体", Font.PLAIN, 20));
		lblBoxId.setBounds(10, 205, 230, 25);
		jpMedicineInfo.add(lblBoxId);
		
		lblPlanSupply = new JCommonLabel("領 藥 量:");
		lblPlanSupply.setForeground(new Color(220, 20, 60));
		lblPlanSupply.setFont(new Font("楷体", Font.PLAIN, 20));
		lblPlanSupply.setBounds(10, 260, 103, 25);
		jpMedicineInfo.add(lblPlanSupply);
		
		textPlanSupply = new JCommonTextField("0");
		textPlanSupply.setBounds(120, 255, 250, 35);
		textPlanSupply.setColumns(10);
		textPlanSupply.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
		  		char ekey = e.getKeyChar();
		  		if((ekey < '0' || ekey > '9') && (ekey != '.')){
		  			e.setKeyChar('\0');
		  		}
			}
		});
		jpMedicineInfo.add(textPlanSupply);
		
		btnMinus = new JRoundButton("-");
		btnMinus.setBounds(373, 255, 50, 35);
		btnMinus.setFont(new Font("黑體", Font.BOLD, 25));
		btnMinus.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String now = textPlanSupply.getText() == null ? "0" : textPlanSupply.getText();
				if(Integer.valueOf(now) > 0) {
					textPlanSupply.setText(String.valueOf(Integer.valueOf(now) - 1));
					return;
				}
				
				textPlanSupply.setText("0");
			}
			
		});
		jpMedicineInfo.add(btnMinus);

		btnPlus = new JRoundButton("+");
		btnPlus.setBounds(425, 255, 50, 35);
		btnPlus.setFont(new Font("黑體", Font.BOLD, 25));
		btnPlus.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String now = textPlanSupply.getText() == null ? "0" : textPlanSupply.getText();
				if(Integer.valueOf(now) >= stockQty) {
					textPlanSupply.setText(String.valueOf((int) Math.floor(stockQty)));
					return;
				}
				
				textPlanSupply.setText(String.valueOf(Integer.valueOf(now) + 1));
			}
			
		});
		jpMedicineInfo.add(btnPlus);

		btnCancel = new JRoundButton("返回");
		btnCancel.setIcon(new ImageIcon("images/back.png"));
		btnCancel.setForeground(new Color(245, 255, 250));
		btnCancel.setBounds(227, 491, 172, 70);
		getContentPane().add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeWindow();
			}
		});

		btnOK = new JRoundButton("確定");
		btnOK.setIcon(new ImageIcon("images/check.png"));
		btnOK.setForeground(new Color(245, 255, 250));
		btnOK.setBounds(546, 491, 172, 70);
		getContentPane().add(btnOK);
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int takeQty = Integer.valueOf(textPlanSupply.getText().trim());
				
				if (takeQty <= 0) {
					lblRturnMessage.setOpaque(true);
					lblRturnMessage.setBackground(Color.YELLOW);
					lblRturnMessage.setText("取藥數量不能低于0。");
					return;
				}
				btnOK.setEnabled(false);
				btnCancel.setEnabled(false);
				if(getDrugInfo(medicineId, takeQty)) {
					providingMedicine();
					return;
				}
				btnOK.setEnabled(true);
				btnCancel.setEnabled(true);
			}
		});
		
		lblRturnMessage = new JCommonLabel("");
		lblRturnMessage.setBounds(20, 404, 699, 56);
		lblRturnMessage.setOpaque(false);
		lblRturnMessage.setForeground(new Color(255, 0, 0));
		lblRturnMessage.setBackground(Color.YELLOW);
		lblRturnMessage.setFont(new Font("楷体", Font.PLAIN, 23));
		getContentPane().add(lblRturnMessage);
	}
	
	public void setMedicine(String medicineId, String medicineName, double stockQty) {
		this.medicineId = medicineId;
		this.stockQty = stockQty;
		lblMedicineID.setText("藥物編號: " + medicineId.trim());
		lblMedicineName.setText("藥物名稱: " + medicineName.trim());
		lblBoxId.setText("儲位編號: N/A");
		lblBoxQuantityNow.setText("現存藥量: " + String.valueOf(stockQty));
	}
	
	//根據輸入參數, 設置窗口顯示內容
	private void setPageItems(int medicineIndex, int boxIndex) {
		String boxId = medicines.getMedicinedata().get(medicineIndex).getBoxInfo().get(boxIndex).getBoxId().trim();
		double stockQty = medicines.getMedicinedata().get(medicineIndex).getBoxInfo().get(boxIndex).getStockQty();
		double planQty = medicines.getMedicinedata().get(medicineIndex).getBoxInfo().get(boxIndex).getTakeQty();
		lblMedicineID.setText("藥物編號: " + 
				medicines.getMedicinedata().get(medicineIndex).getMedicineId().trim());
		lblMedicineName.setText("藥物名稱：" + 
				(medicines.getMedicinedata().get(medicineIndex).getMedicineName() == null ? "" : 
					medicines.getMedicinedata().get(medicineIndex).getMedicineName().trim()));
		lblBoxId.setText("儲位編號: " + boxId);
		lblBoxQuantityNow.setText("現存藥量: " + String.valueOf(stockQty));
		lblPlanSupply.setText("領 藥 量: " + String.valueOf(planQty));
		
		BigDecimal bd1 = new BigDecimal(planQty);
		BigDecimal bd2 = new BigDecimal(stockQty);
		
		if(bd1.compareTo(bd2) == 1){
			lblRturnMessage.setOpaque(true);
			lblRturnMessage.setBackground(Color.YELLOW);
			lblRturnMessage.setText("計劃領藥數據量大於藥盒現有數量，無法繼續領藥!");
			logger.error("計劃領藥失敗 - 計劃領藥數據量大於藥盒現有數量，無法繼續領藥!");
			btnOK.setEnabled(true);
			return;
		}
		lblRturnMessage.setOpaque(false);
		lblRturnMessage.setBackground(new Color(181, 223, 226));
		lblRturnMessage.setText("");
		
		//base64圖片
		medicinePic = medicines.getMedicinedata().get(0).getPhoto();
		if((medicinePic != null) && (medicinePic.length() > 0)){
			lblMedicinePhoto.setIcon(CommonUtils.createAutoAdjustIcon((new ImageIcon(AESUtils.GenerateImage(medicinePic))).getImage(), true));
		}else {
			lblMedicinePhoto.setIcon(CommonUtils.createAutoAdjustIcon((new ImageIcon("images/nopic.png")).getImage(), true));
		}


	}
	
	private boolean getDrugInfo(String medicineId, int takeQty){

		try {
			String barcodeNo2 = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
			ReqProvideQuery reqProvideQuery = new ReqProvideQuery();
			reqProvideQuery.setToken(mainWindow.getUserToken());
			reqProvideQuery.getData().setTerminalId(globalData.getTerminalId());
			reqProvideQuery.getData().setChartNo("");
			reqProvideQuery.getData().setPhrOrderNo("");
			reqProvideQuery.getData().setBarcodeNo1(BARCODENO1);
			reqProvideQuery.getData().setBarcodeNo2(barcodeNo2);
			
			ReqProvideQuery.MedicineData medicineData = new ReqProvideQuery.MedicineData();
			medicineData.setBoxId("");
			medicineData.setMedicineId(medicineId);
			medicineData.setTakeQty(takeQty);
			reqProvideQuery.getData().getMedicinedata().add(medicineData);
			
			ResProvideQuery resProvideQuery = restProvideQuery.doPost(reqProvideQuery);
			
			
			
			if(ResponseCode.OK != Integer.valueOf(resProvideQuery.getResult())) {
				logger.error("讀取藥品列表錯誤, 原因: {}", resProvideQuery.getMessage());
				lblRturnMessage.setOpaque(true);
				lblRturnMessage.setBackground(Color.YELLOW);
				lblRturnMessage.setText("讀取藥品列表錯誤, 原因: " + resProvideQuery.getMessage());
				return false;
			}
			
			medicines = resProvideQuery.getData();
			return true;
			
		}catch(Throwable e) {
			logger.error("讀取藥品列表錯誤!", e);
			lblRturnMessage.setOpaque(true);
			lblRturnMessage.setBackground(Color.YELLOW);
			lblRturnMessage.setText("讀取藥品列表錯誤!");
			return false;
		}
	}
	
	private void providingMedicine() {
		mapMedicinesProviding.clear();
		mapMedicinesResult.clear();
		planTakeTimes = 0;
		takedTimes = 0;
		int medicineIndex = 0;
		for(ResProvideQuery.MedicineData medicine : medicines.getMedicinedata()) {
			int boxIndex = 0;
			String medicineId = medicine.getMedicineId().trim();
			mapMedicinesProviding.put(medicineId, 0);
			planQty = (int)Math.ceil(medicine.getTakeQty());
			for(ResProvideQuery.BoxInfo box : medicine.getBoxInfo()) {
				
				String key = medicine.getMedicineId().trim() + "-" + box.getBoxId().trim();
				MedicineResult mr = new MedicineResult();
				mr.setMedicineIndex(medicineIndex);
				mr.setBoxIndex(boxIndex);
				mr.setMedicineId(medicineId);
				mr.setBoxId(box.getBoxId().trim());
				mr.setProvided(false);
				mr.setStatus(-1);
				mr.setTakedQty(0.0d);
				mapMedicinesResult.put(key, mr);
				
				Vendout vendout = new Vendout(key, Integer.valueOf(box.getBoxId()), (int)Math.ceil(box.getTakeQty()), this);
				
				planTakeTimes++;
				requestQueue.push(vendout);
				boxIndex++;
			}
			medicineIndex++;
		}
	}
	
	private void closeWindow(){
		medicines = null;
		dispose();
	}
	
	private void updateProvidedResult() {
		//更新中央控管
		try {
			ReqProvideResult reqProvideResult = new ReqProvideResult();
			reqProvideResult.setToken(mainWindow.getUserToken());
			reqProvideResult.getData().setTerminalId(globalData.getTerminalId());
			reqProvideResult.getData().setChartNo(medicines.getChartNo().trim());
			reqProvideResult.getData().setPhrOrderNo(medicines.getPhrOrderNo().trim());
			reqProvideResult.getData().setBarcodeNo1(medicines.getBarcodeNo1().trim());
			reqProvideResult.getData().setBarcodeNo2(medicines.getBarcodeNo2().trim());
			
			List<ReqProvideResult.MedicineData> lsMedicinesData = new ArrayList<ReqProvideResult.MedicineData>();
			
			for (MedicineResult mr : mapMedicinesResult.values()) {
				ReqProvideResult.MedicineData medicineData = new ReqProvideResult.MedicineData();
				medicineData.setBoxId(mr.getBoxId());
				medicineData.setMedicineId(mr.getMedicineId());
				medicineData.setTakeQty(mr.getTakedQty());
				medicineData.setStatus(mr.getStatus());
				lsMedicinesData.add(medicineData);
			}
			reqProvideResult.getData().getMedicinedata().addAll(lsMedicinesData);
	
			logger.debug("privide result request: [{}]", reqProvideResult.toString());
			ResProvideResult resProvideResult = restProvideResult.doPost(reqProvideResult);
			logger.debug("provide result response: [{}]", resProvideResult.toString());
			if (ResponseCode.OK != Integer.valueOf(resProvideResult.getResult())) {
				logger.error("調劑-回寫取藥結果錯誤, 原因: {}", resProvideResult.getMessage());
				lblRturnMessage.setOpaque(true);
				lblRturnMessage.setBackground(Color.YELLOW);
				lblRturnMessage.setText("回寫取藥結果錯誤, 原因:" +  resProvideResult.getMessage());
			}
			
		}catch(Throwable e) {
			logger.error("調劑-回寫取藥結果時系統錯誤！", e);
			lblRturnMessage.setOpaque(true);
			lblRturnMessage.setBackground(Color.YELLOW);
			lblRturnMessage.setText("回寫取藥結果錯誤時系統錯誤, 請聯繫系統管理員！" );
		}

	}

	@Override
	public void update(ResponseMessage message) {
		logger.debug("get message.");
		
		if(message.getCode() == CODES.PROV_VM_START) {
			if (message.getMessage() != null) {
				String key = message.getMessage().trim();
				MedicineResult mrStart = mapMedicinesResult.get(key);
				setPageItems(mrStart.getMedicineIndex(), mrStart.getBoxIndex());
			}
			return;
		}
		int index = lblMedicineID.getText().indexOf(":");
		String medicineId = lblMedicineID.getText().substring(index + 1).trim();
		int takedQty = mapMedicinesProviding.get(medicineId) == null ? 0 : mapMedicinesProviding.get(medicineId);
		
		index = lblBoxId.getText().indexOf(":");
		String boxId = lblBoxId.getText().substring(index + 1).trim();
		
		index = lblPlanSupply.getText().indexOf(":");
		double planQty = Double.valueOf(lblPlanSupply.getText().substring(index + 1).trim());
		
		int status = -1;
		if(message.getCode() == CODES.PROV_VM_PROCESS) {
			takedQty++;
			mapMedicinesProviding.put(medicineId, takedQty);
			logger.info("調劑 - 藥品ID:[{}] 正在第[{}]次取藥。", 
					medicineId, takedQty);
			lblRturnMessage.setOpaque(true);
			lblRturnMessage.setBackground(Color.GREEN);
			lblRturnMessage.setText("正在取第[" + takedQty + "]包藥，請稍候……");
			return;
		}

		int columnTakeTimes = 0;
		takedTimes++;

		if(message.getCode() != CODES.SUCCESS) {
			columnTakeTimes = message.getLoop() - 1;
			takedQty--;
			mapMedicinesProviding.put(medicineId, takedQty);
			status = CONSTANTS.ERR_PROVIDE;
			logger.error("調劑失敗 - 藥品ID:[{}]; 取藥數量:[{}]; 已取數量:[{}]; 錯誤原因：[{}]", 
					medicineId, planQty, takedQty, message.getMessage());
			lblRturnMessage.setOpaque(true);
			lblRturnMessage.setBackground(Color.YELLOW);
			lblRturnMessage.setText("調劑 - " + message.getMessage());
//			return;
		}else {
		
			//更新接口，取藥成功
			status = CONSTANTS.SUCCESS;
			columnTakeTimes = message.getLoop();
			lblRturnMessage.setOpaque(true);
			lblRturnMessage.setBackground(Color.GREEN);
			lblRturnMessage.setForeground(Color.RED);;
			lblRturnMessage.setText("調劑成功 - 取藥數量:[" + takedQty + "]");
			logger.info("調劑成功 - 藥品ID:[{}]; 取藥數量:[{}]; 已取數量:[{}]", 
					medicineId, planQty, takedQty);
		}
		
		MedicineResult mr = mapMedicinesResult.get(medicineId + "-" + boxId);
		mr.setTakedQty(columnTakeTimes);
		mr.setProvided(true);
		mr.setStatus(status);
		mapMedicinesResult.put(medicineId + "-" + boxId, mr);
		
		if(takedTimes >= planTakeTimes) {
			btnOK.setEnabled(true);
			btnCancel.setEnabled(true);

			updateProvidedResult();
		}
	}
	
	public int getDrugQtyNow(){
		return drugQtyNow;
	}
	
	public double getProvidedQty(){
		return planQty;
	}
	
	@Data
	class MedicineResult {
		private int medicineIndex;
		private int boxIndex;
		private String medicineId;
		private String boxId;
		private double takedQty = 0.0d;
		private int status = -1;
		private boolean isProvided = false;
	}
}
