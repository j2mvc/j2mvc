package com.j2mvc.framework;

import com.j2mvc.framework.config.Config;

public class Start {

	public static void main(String...args){
		while(true){
			try {
				Config.init();
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
