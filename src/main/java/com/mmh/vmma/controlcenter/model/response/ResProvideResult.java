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
public class ResProvideResult extends ResEntity {
	private DataField data;
	
	public DataField getData() {
		if(data == null) {
			data = new DataField();
		}
		return data;
	}

	@Data 
	public class DataField {
		private String terminalId;
		private String chartNo;
		private String PhrOrderNo;
		private String barcodeNo1;
		private String barcodeNo2;
		private List<MedicineData> medicinedata;

		public List<MedicineData> getMedicinedata() {
			if(medicinedata == null) {
				medicinedata = new ArrayList<MedicineData>();
			}
			return medicinedata;
		}
	}

	@Data
	public static class MedicineData {
		private String medicineId;
		private double stockQty;
		private boolean isAlarm;
		private List<BoxInfo> boxInfo;
		
		public List<BoxInfo> getBoxInfo(){
			if(boxInfo == null) {
				boxInfo = new ArrayList<BoxInfo>();
			}
			return boxInfo;
		}
	}
	
	@Data
	public static class BoxInfo {
		private String BoxId;
		private double StockQty;
		private boolean takeQty;
	}

}
