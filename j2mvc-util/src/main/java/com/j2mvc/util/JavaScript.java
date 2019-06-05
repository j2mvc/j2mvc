package com.j2mvc.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
/**
 * javascript引擎
 * @author root
 *
 */
public class JavaScript {
	static ScriptEngineManager factory = new ScriptEngineManager();  
    static ScriptEngine engine = factory.getEngineByName("JavaScript");  
      
    /**
     * 将字符串公式转换为Double
     * @param option
     */
    public static Double parse(String option){  
    	Double result;  
        try {  
            Object o = engine.eval(option);  
            result = Double.parseDouble(o.toString());  
        } catch (ScriptException e) { 
            return null;  
        }  
        return result;  
    }  
}
