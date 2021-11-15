/**
 * 
 */
package com.mmh.vmma.controlcenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author hongftan
 *
 */
@Component
public class RestfulClient {

	@Autowired
	RestTemplate restClient;
	
	public <T> Object doPost(String uri, Object request, Class<T> cls) {
		return restClient.postForEntity(uri, request, cls);
	}
}
