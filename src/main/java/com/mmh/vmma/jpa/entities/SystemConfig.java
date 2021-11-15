/**
 * 
 */
package com.mmh.vmma.jpa.entities;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;


/**
 * @author hongftan
 *
 */
@Data
@Entity
@Table(name="t_SystemConfig")
public class SystemConfig implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3005043331864426866L;
	
	public enum SystemConfigId {
		cabinet, control_center
		}

	@Id
	private String key;
	
	private byte[] value;

	@Override
	public String toString() {
		return "SystemConfig [id=" + key + ", value=" + Arrays.toString(value) + "]";
	}

}
