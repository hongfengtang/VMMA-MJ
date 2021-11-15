/**
 * 
 */
package com.mmh.vmma.controlcenter;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mmh.vmma.controlcenter.model.request.ReqLogout;
import com.mmh.vmma.controlcenter.model.response.ResLogout;
import com.mmh.vmma.ui.common.GlobalData;
import com.mmh.vmma.utils.CommonUtils;

/**
 * @author hongftan
 *
 */
@Component
public class RestfulLogout implements IRestfulClient {
	private final String uri = "/user/logout";
	
	@Autowired
	RestTemplate restClient;
	
	@Autowired
	GlobalData globalData;
	
	public ResLogout doPost(ReqLogout request) throws Throwable {
		String url = globalData.getCcBaseUrl().endsWith("/") ? 
				globalData.getCcBaseUrl() + uri.substring(1) : 
				globalData.getCcBaseUrl() + uri;
		request.setTerminalid(Optional.ofNullable(request.getTerminalid()).orElse(globalData.getTerminalId()));
		request.setDatetime(Optional.ofNullable(request.getDatetime()).orElse(CommonUtils.getDateTime()));
		return (ResLogout)doPost(url, request, ResLogout.class);
	}
	@Override
	public <T> Object doPost(String uri, Object request, Class<T> cls) {
		return restClient.postForObject(uri, request, cls);
	}

}
