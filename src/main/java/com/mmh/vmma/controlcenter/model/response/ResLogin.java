/**
 * 
 */
package com.mmh.vmma.controlcenter.model.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hongftan
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ResLogin extends ResEntity{
	private DataField data;

	@Data 
	public class DataField {
		private String token;
		private List<String> roles;
		private String userId;
		private String userName;
		private String department;
		private String userInfo;
		private String introduction;
		private String avatar;
		private String email;
		private List<MenuItems> menuItems;
		
		public List<String> getRoles() {
			if(roles == null) {
				roles = new ArrayList<String>();
			}
			return roles;
		}
		
		public List<MenuItems> getMenuItems() {
			if(menuItems == null) {
				menuItems = new ArrayList<MenuItems>();
			}
			return menuItems;
		}

	}

	@Data
	public class MenuItems {
		private String appid;
		private String icon;
		private String title;
		private String active;
		private List<SubItems> subItems;
		
		public List<SubItems> getSubItems() {
			if(subItems == null) {
				subItems = new ArrayList<SubItems>();
			}
			return subItems;
		}
	}

	@Data
	public class SubItems {
		private String appid;
		private String icon;
		private String title;
		private String active;
		private String url;
	}
}
