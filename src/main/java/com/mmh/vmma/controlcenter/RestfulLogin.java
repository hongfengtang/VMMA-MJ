/**
 * 
 */
package com.mmh.vmma.controlcenter;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mmh.vmma.controlcenter.model.request.ReqLogin;
import com.mmh.vmma.controlcenter.model.response.ResLogin;
import com.mmh.vmma.ui.common.GlobalData;
import com.mmh.vmma.utils.CommonUtils;

/**
 * @author hongftan
 *
 */
@Component
public class RestfulLogin implements IRestfulClient {
	private final String uri = "/user/login";
	
	@Autowired
	RestTemplate restClient;
	
	@Autowired
	GlobalData globalData;

	public ResLogin doPost(ReqLogin request) throws Throwable {
		String url = globalData.getCcBaseUrl().endsWith("/") ? 
				globalData.getCcBaseUrl() + uri.substring(1) : 
				globalData.getCcBaseUrl() + uri;
		request.setClientId(globalData.getTerminalId());
		request.setDatetime(Optional.ofNullable(request.getDatetime()).orElse(CommonUtils.getDateTime()));
		return (ResLogin)doPost(url, request, ResLogin.class);
	}

	@Override
	public <T> Object doPost(String uri, Object request, Class<T> cls) {
		return restClient.postForObject(uri, request, cls);
	}

}
