package com.j2mvc.authorization.distribute;

import java.util.ArrayList;
import java.util.List;

import com.j2mvc.authorization.distribute.entity.Menu;

public class AuthorizationUtils {
	
	public static boolean exists(String arr,List<String> array) {
		for(String a:array) {
			if(arr.equals(a)) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 生成新的菜单列表
	 * @param id
	 * @param menus
	 * @return
	 */
	public static List<Menu> getMenuTree(List<String>  id,List<Menu> menus) {
		
		List<Menu> newMenus = new ArrayList<Menu>();
		for(Menu menu:menus) {
			Menu newMenu = getNewMenu(id,menu);
			if(newMenu!=null) {
				// 存在
				newMenus.add(newMenu);
			}
		}
		return newMenus;
	}

	/**
	 * 判断是否存在生成新菜单
	 * @param id
	 * @param original
	 * @return
	 */
	public static Menu getNewMenu(List<String>  id,Menu original) {
		
		if(exists(original.getId(),id)) {
			// 存在
			Menu newMenu = original;
			List<Menu> children = original.getChildren();
			// 下级
			List<Menu> newChildren = getMenuTree(id,children);
			newMenu.setChildren(newChildren);
			return newMenu;
		}
		return null;
	}
}
