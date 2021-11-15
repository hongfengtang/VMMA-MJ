/**
 * 
 */
package com.mmh.vmma.controlcenter.model.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author hongftan
 *
 */
@Data
public class ReqProvideResult {
	private String clientId;
	private String datetime;
	private String token;
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
		private String ChartNo;
		private String PhrOrderNo;
		private String barcodeNo1;
		private String barcodeNo2;
		private List<MedicineData> medicinedata;
		
		public List<MedicineData> getMedicinedata(){
			if(medicinedata == null) {
				medicinedata = new ArrayList<MedicineData>();
			}
			return medicinedata;
		}
	}
	
	@Data
	public static class MedicineData {
		private String boxId;
		private String medicineId;
		private double takeQty;
		private int status;
	}

}
