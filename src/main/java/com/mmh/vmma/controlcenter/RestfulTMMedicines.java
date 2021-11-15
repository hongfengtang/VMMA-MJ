/**
 * 
 */
package com.mmh.vmma.controlcenter;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.mmh.vmma.controlcenter.model.request.ReqTMedicines;
import com.mmh.vmma.controlcenter.model.response.ResTMedicines;
import com.mmh.vmma.ui.common.GlobalData;
import com.mmh.vmma.utils.CommonUtils;

/**
 * @author hongftan
 *
 */
@Component
public class RestfulTMMedicines implements IRestfulClient {
	private final String uri = "/system/tmedicines";
	
	@Autowired
	RestTemplate restClient;
	
	@Autowired
	GlobalData globalData;
	
	public ResTMedicines doPost(ReqTMedicines request) throws Throwable {
		String url = globalData.getCcBaseUrl().endsWith("/") ? 
				globalData.getCcBaseUrl() + uri.substring(1) : 
				globalData.getCcBaseUrl() + uri;
		request.setClientId(Optional.ofNullable(request.getClientId()).orElse(globalData.getTerminalId()));
		request.setDatetime(Optional.ofNullable(request.getDatetime()).orElse(CommonUtils.getDateTime()));
		request.setToken(Optional.ofNullable(request.getToken()).orElse(globalData.getSystemToken()));
		String resGSR = (String)doPost(url, request, String.class);
		return JSONArray.parseObject(resGSR, ResTMedicines.class);
//		return (ResGetMedicines)doPost(url, request, ResGetMedicines.class);
	}
	@Override
	public <T> Object doPost(String uri, Object request, Class<T> cls) {
		return restClient.postForObject(uri, request, cls);
	}

}
