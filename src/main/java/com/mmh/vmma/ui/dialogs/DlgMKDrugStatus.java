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

import com.mmh.vmma.controlcenter.model.VMMedicine;
import com.mmh.vmma.ui.templates.JCommonLabel;
import com.mmh.vmma.ui.templates.JCommonPanel;
import com.mmh.vmma.ui.templates.JRoundButton;
import com.mmh.vmma.utils.AESUtils;
import com.mmh.vmma.utils.CommonUtils;

import javax.swing.UIManager;

public class DlgMKDrugStatus extends JDialog{

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(DlgMKDrugStatus.class);
	
	//自定義變量

	private String medicinePic = "";
	
	//窗口組件
	private final JCommonPanel jpMedicine = new JCommonPanel();
	private JCommonLabel lblOpenMsg;
	private JCommonLabel lblWarning;
	private JCommonLabel lblMedicinePhoto;
	
	//藥品ID
	private JCommonPanel jpMedicineID;
	private JCommonLabel lblMedicineID;
	
	//藥品詳細信息
	private JCommonPanel jpMedicineInfo;
	private JCommonLabel lblMedicineName;
	private JCommonLabel lblMedicineScience;
	private JCommonLabel lblPackgeType;
	private JCommonLabel lblDoseRoute;
	private JCommonLabel lblMedicineUnit;
	private JCommonLabel lblMedicineType;
	private JCommonLabel lblBoxQuantityNow;
	private JCommonLabel lblQtyMax;
	private JCommonLabel lblBoxId;
	
	//返回消息
	private JCommonLabel lblRturnMessage;
	private JRoundButton btnBack;

	/**
	 * Create the dialog.
	 */
	public DlgMKDrugStatus() {
		init();
	}

	//初始化窗口
	private void init(){
		
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
//		setUndecorated(true);
		setType(Type.UTILITY);
		
		setBounds(0, 0, 800, 650);
		setLocationRelativeTo(null);
		
		getContentPane().setLayout(null);
		getContentPane().setBackground(new Color(181, 223, 226));
		jpMedicine.setBounds(0, 0, 734, 420);
		jpMedicine.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(jpMedicine);
		jpMedicine.setLayout(null);
		
		lblOpenMsg = new JCommonLabel("藥物詳細訊息");
		lblOpenMsg.setHorizontalAlignment(SwingConstants.CENTER);
		lblOpenMsg.setForeground(new Color(0, 0, 128));
		lblOpenMsg.setFont(new Font("楷体", Font.BOLD, 25));
		lblOpenMsg.setBounds(10, 10, 724, 37);
		jpMedicine.add(lblOpenMsg);

		lblWarning = new JCommonLabel("提示");
		lblWarning.setForeground(new Color(220, 20, 60));
		lblWarning.setFont(new Font("隶书", Font.BOLD, 30));
		lblWarning.setHorizontalAlignment(SwingConstants.CENTER);
		lblWarning.setBounds(10, 20, 84, 69);
		jpMedicine.add(lblWarning);

		lblMedicinePhoto = new JCommonLabel();
		lblMedicinePhoto.setBackground(Color.PINK);
		lblMedicinePhoto.setBounds(20, 89, 180, 245);
		jpMedicine.add(lblMedicinePhoto);
		
		jpMedicineID = new JCommonPanel();
		jpMedicineID.setLayout(null);
		jpMedicineID.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", 
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		jpMedicineID.setBounds(232, 57, 492, 50);
		jpMedicine.add(jpMedicineID);
		
		lblMedicineID = new JCommonLabel("藥物編號:");
		lblMedicineID.setForeground(new Color(205, 92, 92));
		lblMedicineID.setFont(new Font("楷体", Font.PLAIN, 22));
		lblMedicineID.setBounds(10, 10, 472, 30);
		jpMedicineID.add(lblMedicineID);
		
		jpMedicineInfo = new JCommonPanel();
		jpMedicineInfo.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "藥品訊息", 
				TitledBorder.LEADING, TitledBorder.TOP, new Font("楷体", Font.PLAIN, 22), new Color(220, 20, 60)));
		jpMedicineInfo.setBounds(232, 124, 492, 280);
		jpMedicine.add(jpMedicineInfo);
		jpMedicineInfo.setLayout(null);
		
		lblMedicineName = new JCommonLabel("藥品名稱:");
		lblMedicineName.setForeground(new Color(220, 20, 60));
		lblMedicineName.setFont(new Font("楷体", Font.PLAIN, 20));
		lblMedicineName.setBounds(10, 35, 473, 25);
		jpMedicineInfo.add(lblMedicineName);
		
		lblMedicineScience = new JCommonLabel("藥品學名:");
		lblMedicineScience.setForeground(new Color(220, 20, 60));
		lblMedicineScience.setFont(new Font("楷体", Font.PLAIN, 20));
		lblMedicineScience.setBounds(10, 72, 473, 25);
		jpMedicineInfo.add(lblMedicineScience);
		
		lblPackgeType = new JCommonLabel("包裝類型:");
		lblPackgeType.setForeground(new Color(220, 20, 60));
		lblPackgeType.setFont(new Font("楷体", Font.PLAIN, 20));
		lblPackgeType.setBounds(10, 146, 233, 25);
		jpMedicineInfo.add(lblPackgeType);
		
		lblDoseRoute = new JCommonLabel("藥品劑型:");
		lblDoseRoute.setForeground(new Color(220, 20, 60));
		lblDoseRoute.setFont(new Font("楷体", Font.PLAIN, 20));
		lblDoseRoute.setBounds(10, 109, 233, 25);
		jpMedicineInfo.add(lblDoseRoute);
		
		lblMedicineUnit = new JCommonLabel("藥物單位:");
		lblMedicineUnit.setForeground(new Color(220, 20, 60));
		lblMedicineUnit.setFont(new Font("楷体", Font.PLAIN, 20));
		lblMedicineUnit.setBounds(249, 109, 233, 25);
		jpMedicineInfo.add(lblMedicineUnit);
		
		lblMedicineType = new JCommonLabel("管制類藥:");
		lblMedicineType.setForeground(new Color(220, 20, 60));
		lblMedicineType.setFont(new Font("楷体", Font.PLAIN, 20));
		lblMedicineType.setBounds(249, 146, 234, 25);
		jpMedicineInfo.add(lblMedicineType);
		
		lblBoxQuantityNow = new JCommonLabel("現存藥量:" );
		lblBoxQuantityNow.setBounds(10, 183, 233, 25);
		lblBoxQuantityNow.setFont(new Font("楷体", Font.PLAIN, 20));
		lblBoxQuantityNow.setForeground(new Color(220, 20, 60));
		jpMedicineInfo.add(lblBoxQuantityNow);
		
		lblQtyMax = new JCommonLabel("最大存量：");
		lblQtyMax.setForeground(new Color(220, 20, 60));
		lblQtyMax.setFont(new Font("楷体", Font.PLAIN, 20));
		lblQtyMax.setBounds(249, 220, 233, 25);
		jpMedicineInfo.add(lblQtyMax);
		
		lblBoxId = new JCommonLabel("儲位編號:");
		lblBoxId.setBounds(10, 220, 233, 25);
		lblBoxId.setFont(new Font("楷体", Font.PLAIN, 20));
		lblBoxId.setForeground(new Color(220, 20, 60));
		jpMedicineInfo.add(lblBoxId);

		btnBack = new JRoundButton("返回");
		btnBack.setEnabled(true);
		btnBack.setVisible(true);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnBack.setIcon(new ImageIcon("images/close.png"));
		btnBack.setForeground(new Color(245, 255, 250));
		btnBack.setBounds(547, 478, 172, 70);
		getContentPane().add(btnBack);

		lblRturnMessage = new JCommonLabel("");
		lblRturnMessage.setBounds(20, 420, 699, 56);
		lblRturnMessage.setOpaque(false);
		lblRturnMessage.setForeground(new Color(255, 0, 0));
		lblRturnMessage.setBackground(Color.YELLOW);
		lblRturnMessage.setFont(new Font("楷体", Font.PLAIN, 23));
		getContentPane().add(lblRturnMessage);

	}
	
	public void setMedicine(VMMedicine medicine) {
		logger.info("顯示藥品詳細訊息。");
		if(medicine == null){
			lblMedicineID.setText("藥品編號:N/A");
			
			lblMedicineName.setText("藥品名稱:N/A");
			lblMedicineScience.setText("藥品學名:N/A");
			lblPackgeType.setText("包裝類型:N/A");
			lblDoseRoute.setText("藥品劑型:N/A");
			lblMedicineUnit.setText("藥物單位:N/A");
			lblMedicineType.setText("管制類藥:N/A");
			lblBoxQuantityNow.setText("現存藥量：N/A" );
			lblQtyMax.setText("最大存量: N/A");
			lblBoxId.setText("儲位編號: N/A");

			lblRturnMessage.setOpaque(true);
			lblRturnMessage.setBackground(Color.YELLOW);
			lblRturnMessage.setText("無藥品訊息與本藥盒配置。");
			logger.warn("無藥品訊息與本藥盒配置。");
			
		}else{
			logger.info("藥品編號為: [{}]", medicine.getMedicineId().trim());
			lblMedicineID.setText("藥品編號: " + medicine.getMedicineId().trim());
			lblMedicineName.setText("藥品名稱：" + (medicine.getMedicineName() == null ? "" : medicine.getMedicineName()));
			lblMedicineScience.setText("藥品學名: " + (medicine.getScienceName() == null ? "" : medicine.getScienceName()));
			lblPackgeType.setText("包裝類型: " + (medicine.getPackageType() == null ? "" : medicine.getPackageType()));
			lblDoseRoute.setText("藥品劑型: " + (medicine.getDoseRoute() == null ? "" : medicine.getDoseRoute()));
			lblMedicineUnit.setText("藥物單位: " + (medicine.getDoseUnit() == null ? "0" : medicine.getDoseUnit()));
			lblMedicineType.setText("管制類藥: " + medicine.getType());
			lblBoxQuantityNow.setText("現存藥量: " + String.valueOf(medicine.getBoxQuantity()));
			lblQtyMax.setText("最大存量: " + String.valueOf(medicine.getBoxMaxQuantity()));
			lblBoxId.setText("儲位編號: " + (medicine.getBoxId() == null ? "" : medicine.getBoxId().trim()));
			
			if(medicine.isAlarm()) {
				lblRturnMessage.setOpaque(true);
				lblRturnMessage.setBackground(Color.RED);
				lblRturnMessage.setForeground(Color.GREEN);
				lblRturnMessage.setText("藥物存儲量已達警告藥量，請及時補藥！");
			}else {
				lblRturnMessage.setOpaque(false);
				lblRturnMessage.setBackground(new Color(181, 223, 226));
				lblRturnMessage.setText("");

			}
			
			//base64圖片
			medicinePic = medicine.getPhoto();
			if((medicinePic != null) && (medicinePic.length() > 0)){
				lblMedicinePhoto.setIcon(CommonUtils.createAutoAdjustIcon((new ImageIcon(AESUtils.GenerateImage(medicinePic))).getImage(), true));
			}else {
				lblMedicinePhoto.setIcon(CommonUtils.createAutoAdjustIcon((new ImageIcon("images/nopic.png")).getImage(), true));
			}

		}
	}
}
