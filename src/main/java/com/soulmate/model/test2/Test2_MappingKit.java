package com.soulmate.model.test2;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

/**
 * Generated by JFinal, do not modify this file.
 * <pre>
 * Example:
 * public void configPlugin(Plugins me) {
 *     ActiveRecordPlugin arp = new ActiveRecordPlugin(...);
 *     Test2_MappingKit.mapping(arp);
 *     me.add(arp);
 * }
 * </pre>
 */
public class Test2_MappingKit {
	
	public static void mapping(ActiveRecordPlugin arp) {
		arp.addMapping("admin_users", "admin_user_id", AdminUsers.class);
	}
}


