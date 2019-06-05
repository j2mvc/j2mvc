package com.j2mvc.util;

public class ServerPort {
	
	final static String configSource = "config/client.properties";
	

	public static int get(){
		PropertiesConfiguration config = new PropertiesConfiguration(configSource);
		return Integer.valueOf(config.get("port"));
	}
}
