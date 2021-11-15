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
import com.mmh.vmma.controlcenter.RestfulProvideResult;
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
import com.mmh.vmma.ui.templates.JRoundButton;
import com.mmh.vmma.utils.AESUtils;
import com.mmh.vmma.utils.CommonUtils;

import lombok.Data;

import javax.swing.UIManager;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

@Component
public class DlgGeneralProviding extends JDialog
implements ISignageCallBack{

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(DlgGeneralProviding.class);
	
	@Autowired
	private MainWindow mainWindow;	
	@Autowired
	private RequestQueue requestQueue;
	@Autowired
	private RestfulProvideResult restProvideResult;
	
	@Autowired
	private GlobalData globalData;

	
	//自定義變量
	private ResProvideQuery.DataField medicines = null;
	private int planQty = 0;
	private int planTakeTimes = 0;
	private int takedTimes = 0;
	
	private String medicinePic = "";
	private HashMap<String, Integer> mapMedicinesProviding = new HashMap<String, Integer>();
	private HashMap<String, MedicineResult> mapMedicinesResult = new HashMap<String, MedicineResult>();
	
//	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private int drugQtyNow  = -1;
	
	//窗口組件
	private JCommonPanel jpMedicine;
	private JCommonLabel lblOpenMsg;
	private JCommonLabel lblWarning;
	private JCommonLabel lblMedicinePhoto;
	
	//病患信息
	private JCommonPanel jpPatientInfo;
	private JCommonLabel lblChartNo;
	private JCommonLabel lblPatientName;
	private JCommonLabel lblMedNo;
	private JCommonLabel lblScrn;
	private JCommonLabel lblFq;
	
	//藥品詳細信息
	private JCommonPanel jpMedicineInfo;
	private JCommonLabel lblMedicineID;
	private JCommonLabel lblMedicineName;
	private JCommonLabel lblBarcodeNo1;
	private JCommonLabel lblBarcodeNo2;
	private JCommonLabel lblMedicineUnit;
	private JCommonLabel lblBoxId;
	private JCommonLabel lblBoxQuantityNow;
	private JCommonLabel lblPlanSupply;

	//返回消息
	private JCommonLabel lblRturnMessage;

	//確認按鈕
	private JRoundButton okButton;

	/**
	 * Create the dialog.
	 */
	public DlgGeneralProviding() {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				okButton.setEnabled(false);
				if (medicines == null) {
					lblRturnMessage.setOpaque(true);
					lblRturnMessage.setBackground(Color.YELLOW);
					lblRturnMessage.setText("缺少病患或取藥訊息！！");
					okButton.setEnabled(true);
					return;
				}
				//開始取藥
				lblRturnMessage.setOpaque(true);
				lblRturnMessage.setBackground(Color.GREEN);
				lblRturnMessage.setText("請稍後，正在取藥!……");
				okButton.setEnabled(false);
				
				providingMedicine();
			}
		});
		init();
		
		subcriberMessage();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
//				okButton.setEnabled(false);
//				if ((patientSelected == null) || (medicine == null)) {
//					lblRturnMessage.setOpaque(true);
//					lblRturnMessage.setBackground(Color.YELLOW);
//					lblRturnMessage.setText("缺少病患或取藥信息！！");
//					okButton.setEnabled(true);
//					return;
//				}
//				//開始取藥
//				lblRturnMessage.setOpaque(true);
//				lblRturnMessage.setBackground(Color.GREEN);
//				lblRturnMessage.setText("請稍後，正在取藥!……");
//				okButton.setEnabled(false);
//				
//				providingMedicine(medicine.getBoxId().trim(), planQty);
			}
			
		});
		
	}

	//初始化窗口
	private void init(){
		
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
//		setUndecorated(true);
		setType(Type.UTILITY);
		
		setBounds(100, 100, 850, 800);
		setLocationRelativeTo(null);
		
		getContentPane().setLayout(null);
		getContentPane().setBackground(new Color(181, 223, 226));
		jpMedicine = new JCommonPanel();
		jpMedicine.setBounds(0, 0, 748, 521);
		jpMedicine.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(jpMedicine);
		jpMedicine.setLayout(null);
		
		lblOpenMsg = new JCommonLabel("調劑操作確認");
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
		jpMedicineInfo.setBounds(232, 55, 492, 245);
		jpMedicine.add(jpMedicineInfo);
		jpMedicineInfo.setLayout(null);

		lblMedicineID = new JCommonLabel("藥物編號:");
		lblMedicineID.setBounds(10, 30, 472, 25);
		jpMedicineInfo.add(lblMedicineID);
		lblMedicineID.setForeground(new Color(220, 20, 60));
		lblMedicineID.setFont(new Font("楷体", Font.PLAIN, 20));

		lblMedicineName = new JCommonLabel("藥物名稱:");
		lblMedicineName.setForeground(new Color(220, 20, 60));
		lblMedicineName.setFont(new Font("楷体", Font.PLAIN, 20));
		lblMedicineName.setBounds(10, 65, 472, 25);
		jpMedicineInfo.add(lblMedicineName);
		
		lblBarcodeNo1 = new JCommonLabel("條 碼 1 :");
		lblBarcodeNo1.setForeground(new Color(220, 20, 60));
		lblBarcodeNo1.setFont(new Font("楷体", Font.PLAIN, 20));
		lblBarcodeNo1.setBounds(10,100, 472, 25);
		jpMedicineInfo.add(lblBarcodeNo1);
		
		lblBarcodeNo2 = new JCommonLabel("條 碼 2 :");
		lblBarcodeNo2.setForeground(new Color(220, 20, 60));
		lblBarcodeNo2.setFont(new Font("楷体", Font.PLAIN, 20));
		lblBarcodeNo2.setBounds(10, 135, 472, 25);
		jpMedicineInfo.add(lblBarcodeNo2);
		
		lblMedicineUnit = new JCommonLabel("藥物單位:");
		lblMedicineUnit.setForeground(new Color(220, 20, 60));
		lblMedicineUnit.setFont(new Font("楷体", Font.PLAIN, 20));
		lblMedicineUnit.setBounds(10, 170, 230, 25);
		jpMedicineInfo.add(lblMedicineUnit);
		
		lblBoxId = new JCommonLabel("儲位編號:");
		lblBoxId.setForeground(new Color(220, 20, 60));
		lblBoxId.setFont(new Font("楷体", Font.PLAIN, 20));
		lblBoxId.setBounds(250, 170, 230, 25);
		jpMedicineInfo.add(lblBoxId);
		
		lblBoxQuantityNow = new JCommonLabel("現存藥量:" );
		lblBoxQuantityNow.setBounds(10, 200, 230, 25);
		lblBoxQuantityNow.setFont(new Font("楷体", Font.PLAIN, 20));
		lblBoxQuantityNow.setForeground(new Color(220, 20, 60));
		jpMedicineInfo.add(lblBoxQuantityNow);
		
		lblPlanSupply = new JCommonLabel("領 藥 量:");
		lblPlanSupply.setForeground(new Color(220, 20, 60));
		lblPlanSupply.setFont(new Font("楷体", Font.PLAIN, 20));
		lblPlanSupply.setBounds(250, 200, 230, 25);
		jpMedicineInfo.add(lblPlanSupply);
		
		jpPatientInfo = new JCommonPanel();
		jpPatientInfo.setLayout(null);
		jpPatientInfo.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "病患訊息", 
				TitledBorder.LEADING, TitledBorder.TOP, new Font("楷体", Font.PLAIN, 22), new Color(0, 0, 140)));
		jpPatientInfo.setBounds(232, 310, 492, 206);
		jpMedicine.add(jpPatientInfo);
		
		lblChartNo = new JCommonLabel("病 歷 號:");
		lblChartNo.setBounds(10, 30, 472, 25);
		jpPatientInfo.add(lblChartNo);
		lblChartNo.setForeground(new Color(0, 0, 140));
		lblChartNo.setFont(new Font("楷体", Font.PLAIN, 20));
		
		lblPatientName = new JCommonLabel("病患姓名:");
		lblPatientName.setBounds(10, 65, 472, 25);
		jpPatientInfo.add(lblPatientName);
		lblPatientName.setForeground(new Color(0, 0, 140));
		lblPatientName.setFont(new Font("楷体", Font.PLAIN, 20));

		lblMedNo = new JCommonLabel("領 藥 號:");
		lblMedNo.setBounds(10, 100, 472, 25);
		jpPatientInfo.add(lblMedNo);
		lblMedNo.setForeground(new Color(0, 0, 140));
		lblMedNo.setFont(new Font("楷体", Font.PLAIN, 20));

		lblScrn = new JCommonLabel("處方日期:");
		lblScrn.setBounds(10, 135, 472, 25);
		jpPatientInfo.add(lblScrn);
		lblScrn.setForeground(new Color(0, 0, 140));
		lblScrn.setFont(new Font("楷体", Font.PLAIN, 20));

		lblFq = new JCommonLabel("頻    次:");
		lblFq.setBounds(10, 170, 472, 25);
		jpPatientInfo.add(lblFq);
		lblFq.setForeground(new Color(0, 0, 140));
		lblFq.setFont(new Font("楷体", Font.PLAIN, 20));
		
		okButton = new JRoundButton("確定");
		okButton.setIcon(new ImageIcon("images/check.png"));
		okButton.setForeground(new Color(245, 255, 250));
		okButton.setBounds(547, 595, 172, 70);
		getContentPane().add(okButton);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeWindow();
			}
		});
		
		lblRturnMessage = new JCommonLabel("");
		lblRturnMessage.setBounds(21, 529, 699, 56);
		lblRturnMessage.setOpaque(false);
		lblRturnMessage.setForeground(new Color(255, 0, 0));
		lblRturnMessage.setBackground(Color.YELLOW);
		lblRturnMessage.setFont(new Font("楷体", Font.PLAIN, 23));
		getContentPane().add(lblRturnMessage);
	}
	
	public void setMedicine(ResProvideQuery.DataField medicines) {
		this.medicines = medicines;
		if((medicines == null) || medicines.getMedicinedata().size() <= 0){
			lblMedicineID.setText("藥物編號: N/A");
			lblMedicineName.setText("藥物名稱: N/A");
			lblBarcodeNo1.setText("條 碼 1 : N/A");
			lblBarcodeNo2.setText("條 碼 2 :N/A");
			lblMedicineUnit.setText("藥物單位:N/A");
			lblBoxId.setText("儲位編號: N/A");
			lblBoxQuantityNow.setText("現存藥量: N/A" );
			lblPlanSupply.setText("領 藥 量：N/A");
			
			lblChartNo.setText("病 歷 號: N/A");
			lblPatientName.setText("病患姓名: N/A");
			lblMedNo.setText("領 藥 號: N/A");
			lblScrn.setText("處方日期: N/A");
			lblFq.setText("頻    次: N/A");

			lblRturnMessage.setOpaque(true);
			lblRturnMessage.setBackground(Color.YELLOW);
			lblRturnMessage.setText("無取藥訊息。");
			
		}else{

			setPageItems(0, 0);
		}
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
		lblBarcodeNo1.setText("條 碼 1 : " + 
				(medicines.getBarcodeNo1() == null ? "" : medicines.getBarcodeNo1().trim()));
		lblBarcodeNo2.setText("條 碼 2 : " + 
				(medicines.getBarcodeNo2() == null ? "" : medicines.getBarcodeNo2().trim()));
		lblMedicineUnit.setText("藥物單位: " + 
				(medicines.getMedicinedata().get(medicineIndex).getDoseUnit() == null ? "" : 
					medicines.getMedicinedata().get(medicineIndex).getDoseUnit().trim()));;
		lblBoxId.setText("儲位編號: " + boxId);
		lblBoxQuantityNow.setText("現存藥量: " + String.valueOf(planQty));
		lblPlanSupply.setText("領 藥 量: " + String.valueOf(stockQty));
		
		lblChartNo.setText("病 歷 號: " + (medicines.getChartNo() == null ? "" : medicines.getChartNo().trim()));
		lblPatientName.setText("病患姓名: " + 
				(medicines.getMedicinedata().get(medicineIndex).getPatientName() == null ? "" : medicines.getMedicinedata().get(medicineIndex).getPatientName().trim()));
		lblMedNo.setText("領 藥 號: " + 
				(medicines.getMedicinedata().get(medicineIndex).getMedNo() == null ? "" : medicines.getMedicinedata().get(medicineIndex).getMedNo().trim()));
		lblScrn.setText("處方日期: " + 
				(medicines.getMedicinedata().get(medicineIndex).getScrn() == null ? "" : medicines.getMedicinedata().get(medicineIndex).getScrn().trim()));
		lblFq.setText("頻    次: " + 
				(medicines.getMedicinedata().get(medicineIndex).getFq() == null ? "" : medicines.getMedicinedata().get(medicineIndex).getFq().trim()));

		BigDecimal bd1 = new BigDecimal(planQty);
		BigDecimal bd2 = new BigDecimal(stockQty);
		
		if(bd1.compareTo(bd2) == 1){
			lblRturnMessage.setOpaque(true);
			lblRturnMessage.setBackground(Color.YELLOW);
			lblRturnMessage.setText("計劃領藥數據量大於藥盒現有數量，無法繼續領藥!");
			logger.error("計劃領藥失敗 - 計劃領藥數據量大於藥盒現有數量，無法繼續領藥!");
			okButton.setEnabled(true);
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
				mr.setMedicineId(medicineId);
				mr.setBoxId(box.getBoxId().trim());
				mr.setProvided(false);
				mr.setStatus(-1);
				mr.setTakedQty(0.0d);
				mapMedicinesResult.put(key, mr);
				
				setPageItems(medicineIndex, boxIndex);
				Vendout vendout = new Vendout(Integer.valueOf(box.getBoxId()), (int)Math.ceil(box.getTakeQty()), this);
				
				planTakeTimes++;
				requestQueue.push(vendout);
				boxIndex++;
			}
			medicineIndex++;
		}
	}
	
	private void subcriberMessage(){
//		FirmwareAccess.getInstance().subcriberMessage(this);
	}
	
	private void unsubcriberMessage(){
//		FirmwareAccess.getInstance().unsubcriberMessage(this);
	}
	
	private void closeWindow(){
		unsubcriberMessage();
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
	
			ResProvideResult resProvideResult = restProvideResult.doPost(reqProvideResult);
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
			lblRturnMessage.setText("正在取第[" + takedQty + "]包藥，請稍後……");
			return;
		}

		takedTimes++;

		if(message.getCode() != CODES.SUCCESS) {
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
			logger.info("調劑成功 - 藥品ID:[{}]; 取藥數量:[{}]; 已取數量:[{}]", 
					medicineId, planQty, takedQty);
		}
		
		MedicineResult mr = mapMedicinesResult.get(medicineId + "-" + boxId);
		mr.setTakedQty(takedQty);
		mr.setProvided(true);
		mr.setStatus(status);
		mapMedicinesResult.put(medicineId + "-" + boxId, mr);
		
		if(takedTimes >= planTakeTimes) {
			okButton.setEnabled(true);
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
		private String medicineId;
		private String boxId;
		private double takedQty = 0.0d;
		private int status = -1;
		private boolean isProvided = false;
	}
}
