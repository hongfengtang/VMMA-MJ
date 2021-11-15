/**
 * 
 */
package com.mmh.vmma.controlcenter.model;

import lombok.Data;

/**
 * @author hongftan
 *
 */
@Data
public class VMMedicine {
	private String medicineId;
	private String medicineName;
	private String scienceName;
	private double alarmQty;
	private boolean isAlarm;
	private double totalQuantity;
	private double totalMaxQuantity;
	private String rfid;
	private String efficiency;
	private int type;
	private String manufacturer;
	private String expiredDate;
	private String doseUnit;
	private String doseRoute;
	private String description;
	private String packageType;
	private String emergencyDrug;
	private String brandCode;
	private String ncku9;
	private String Photo;
	private String barcode;
	private String pno;
	private String control;
	private String boxId;
	private double boxMaxQuantity;
	private double boxQuantity;

}
