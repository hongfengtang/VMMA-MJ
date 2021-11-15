/**
 * 
 */
package com.mmh.vmma.controlcenter;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mmh.vmma.controlcenter.model.request.ReqDeviceInit;
import com.mmh.vmma.controlcenter.model.response.ResDeviceInit;
import com.mmh.vmma.ui.common.GlobalData;
import com.mmh.vmma.utils.CommonUtils;

/**
 * @author hongftan
 *
 */
@Component
public class RestfulDeviceInit implements IRestfulClient {
	private final String uri = "/system/init";
	private final String token = "system_token";
	
	@Autowired
	RestTemplate restClient;
	
	@Autowired
	GlobalData globalData;
	
	public ResDeviceInit doPost(ReqDeviceInit request) throws Throwable {
		String url = globalData.getCcBaseUrl().endsWith("/") ? 
				globalData.getCcBaseUrl() + uri.substring(1) : 
				globalData.getCcBaseUrl() + uri;
		request.setClientId(Optional.ofNullable(request.getData().getTerminalId()).orElse(globalData.getTerminalId()));
		request.setToken(token);
		request.setDatetime(Optional.ofNullable(request.getDatetime()).orElse(CommonUtils.getDateTime()));
		return (ResDeviceInit)doPost(url, request, ResDeviceInit.class);
	}
	@Override
	public <T> Object doPost(String uri, Object request, Class<T> cls) {
		return restClient.postForObject(uri, request, cls);
	}

}
