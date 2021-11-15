/**
 * 
 */
package com.mmh.vmma.controlcenter;

/**
 * @author hongftan
 *
 */
public interface IRestfulClient {
	<T> Object doPost(String uri, Object request, Class<T> cls);
}
