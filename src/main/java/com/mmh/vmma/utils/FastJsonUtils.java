/**
 * 
 */
package com.mmh.vmma.utils;

import java.util.List;

import org.apache.commons.lang3.Validate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/**
 * @author hongftan
 *
 */
public class FastJsonUtils {
	public static byte[] serialize(final Object obj) {
		Validate.notNull(obj);
		return JSONObject.toJSONString(obj).getBytes();
	}
	
	public static <T> T deserializeObject(final byte[] contents, final Class<T> cls) {
		Validate.notNull(contents);
		Validate.notNull(cls);
		return JSONObject.parseObject(new String(contents), cls);
	}
	
	public static <T> List<T> deserializeArray(final byte[] contents, final Class<T> cls) {
		Validate.notNull(contents);
		Validate.notNull(cls);
		return JSONArray.parseArray(new String(contents), cls);
	}

}
