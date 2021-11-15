/**
 * 
 */
package com.mmh.vmma.ui.common;

import java.awt.Color;

/**
 * @author hongftan
 *
 */
public class Settings {
	
	public final static int SERIAL = 0;
	public final static int TCP_CLIENT = 1;
	public final static int TCP_SERVER = 2;
	public final static Color BACKGROUND = new Color(135, 206, 235);
	
	public static final String APP_NAME = "歡迎使用藥品櫃供藥管理系統";
	public static final String COPY_RIGHT = "";//版權所有©：RHS軟件開發服務有限公司";
	public static final int SIGNOUT_TIMEOUT = 45;
	public final static int STATUS_HEIGHT = 30;
	
	public static final String OUT = "out";
	public static final String IN = "in";
	/**
	 * 主工作區窗口
	 */
	public static final String PAGE_TMP = "tmp";
	public static final String PAGE_FUNCTIONS = "functions";				//功能選擇頁面
	public static final String PAGE_LOGIN = "login";						//登錄頁面
	public static final String PAGE_GENERAL_DRUG = "generaldrug";			//一般取藥頁面
	public static final String PAGE_EMERGENCY_DRUG = "emergencydrug";		//緊急取藥頁面
	public static final String PAGE_TEMPORARY_DRUG = "temporarydrug";		//臨時取藥頁面
	public static final String PAGE_BEDS_INFO = "bedsinfo";					//病床信息輸入頁面
	public static final String PAGE_INPUTBC = "barcode";					//馬偕掃碼輸入頁面
	public static final String PAGE_DRUGINFO = "druginfo";					//藥品信息頁面
	public static final String PAGE_BOXVIEW = "boxview";					//打開藥盒頁面
	public static final String PAGE_TAKESTORK = "takestork";				//盤點頁面
	public static final String PAGE_MANAGE = "manage";						//管理頁面
	
	public static final String PAGE_MKGENERAL_DRUG = "mackaygeneraldrug";	//馬偕調劑頁面
	public static final String PAGE_MKDRUGS_STATUS = "mackaydrugsstatus";	//馬偕藥品列表狀態頁面
	
	
	public static final int OPTIION_LOGIN = 1;
	public static final int OPTION_GENERAL_DRUG = 2;
	public static final int OPTION_EMERGENCY_DRUG = 3;
	public static final int OPTION_TEMPORARY_DRUG = 4;
	public static final int OPTION_MANAGE = 5;
	public static final int OPTION_MKGENERAL_DRUG = 6;
	public static final int OPTION_MKDRUGS_STATUS = 7;
	

}
