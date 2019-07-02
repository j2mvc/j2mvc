package com.j2mvc.framework.upload;

import java.util.UUID;

public class Util {

    /**
     * 創建UUID
     * @param str
     * @return
     */
	public static UUID getRandomUUID(String str) {
		// 产生UUID
		if (str == null) {
			return UUID.randomUUID();
		} else {
			return UUID.nameUUIDFromBytes(str.getBytes());
		}
	}
}
