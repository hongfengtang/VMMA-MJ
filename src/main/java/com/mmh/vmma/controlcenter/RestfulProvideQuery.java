/**
 * 
 */
package com.mmh.vmma.controlcenter;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.mmh.vmma.controlcenter.model.request.ReqProvideQuery;
import com.mmh.vmma.controlcenter.model.response.ResProvideQuery;
import com.mmh.vmma.ui.common.GlobalData;
import com.mmh.vmma.utils.CommonUtils;

/**
 * @author hongftan
 *
 */
@Component
public class RestfulProvideQuery implements IRestfulClient {
	private final String uri = "/user/provide/query";
	
	@Autowired
	RestTemplate restClient;
	
	@Autowired
	GlobalData globalData;
	
	public ResProvideQuery doPost(ReqProvideQuery request) throws Throwable {
		String url = globalData.getCcBaseUrl().endsWith("/") ? 
				globalData.getCcBaseUrl() + uri.substring(1) : 
				globalData.getCcBaseUrl() + uri;
		request.setClientId(Optional.ofNullable(request.getClientId()).orElse(globalData.getTerminalId()));
		request.setDatetime(Optional.ofNullable(request.getDatetime()).orElse(CommonUtils.getDateTime()));
		String resGSR = (String)doPost(url, request, String.class);
		return JSONArray.parseObject(resGSR, ResProvideQuery.class);
//		return (ResProvide)doPost(url, request, ResProvide.class);
	}
	@Override
	public <T> Object doPost(String uri, Object request, Class<T> cls) {
		return restClient.postForObject(uri, request, cls);
	}

}
