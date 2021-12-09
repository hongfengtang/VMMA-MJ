/**
 * 
 */
package com.mmh.vmma.controlcenter.model.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hongftan
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ResTMedicines extends ResEntity {
	private int reccount;
	private List<DataField> data;
	
	public List<DataField> getData(){
		if(data == null) {
			data = new ArrayList<DataField>();
		}
		
		return data;
	}
	
	@Data
	public class DataField {
		private String medicineId;
		private String medicineName;
		private String scienceName;
		private double alarmQty;
		private boolean isAlarm;
		private double quantity;
		private double maxQuantity;
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
		private String lot;
		private String control;
		private List<BoxInfoField> boxInfo;
		
		public List<BoxInfoField> getBoxInfo(){
			if(boxInfo == null) {
				boxInfo = new ArrayList<BoxInfoField>();
			}
			return boxInfo;
		}
	}
	
	@Data
	public static class BoxInfoField{
		private String boxId;
		private double maxQuantity;
		private double quantity;
	}
}
