package com.j2mvc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @version 1.0
 * 
 *          按字节长度截取字符串(支持截取带HTML代码样式的字符串)
 * @param param
 *            将要截取的字符串参数
 * @param length
 *            截取的字节长度
 * @param end
 *            字符串末尾补上的字符串
 */
public class SubStringHTML {

    public static void main(String[] args) {  
        String htmlCode = "<h1><span style=\"\" mce_style=\"\font-size: xx-large; color: #000000;\">新华网北京7月13,.</span></h1><h1><span>北京7——月13</span></h1>";  
        System.out.println(subStringHTML(htmlCode, 2));  
        // 测试结果：<h1><span style="font-size: xx-large; color: #000000;" mce_style="font-size: xx-large; color: #000000;">新华网</span></h1>   
    }  

    /** 
     * 按子节长度截取字符串(支持截取带HTML代码样式的字符串)<br> 
     * 如：<span>中国人发在线</span> 当截取2个字节得到的结果是：<span>中国 
     *  
     * @param param 
     *            将要截取的含html代码的字符串参数 
     * @param length 
     *            截取的字节长度 
     * @return 返回截取后的字符串 
     * date 2010-7-15 
     */  
    public static String subStringHTML(String param, int length) {  
        StringBuffer result = new StringBuffer();  
        int n = 0;  
        char temp;  
        boolean isCode = false; // 是不是HTML代码   
        boolean isHTML = false; // 是不是HTML特殊字符,如    
        for(int i = 0; i < param.length(); i++) {  
            temp = param.charAt(i);  
            if(temp == '<') {  
                isCode = true;  
            }else if(temp == '&') {  
                isHTML = true;  
            }else if(temp == '>' && isCode) {  
                n = n - 1;  
                isCode = false;  
            }else if(temp == ';' && isHTML) {  
                isHTML = false;  
            }  
            if(!isCode && !isHTML) {  
                n = n + 1;  
                // UNICODE码字符占两个字节   
                if((temp + "").getBytes().length > 1) {  
                    n = n + 1;  
                }  
            }  
            result.append(temp);  
            if(n >= length) {  
                break;  
            }  
        }  
        return fix(result.toString());  
    }  
    
    /** 
     * 补全HTML代码<br> 
     * 如：<span>中国 ---> <span>中国</span> 
     *  
     */  
    private static String fix(String str) {  
        StringBuffer fixed = new StringBuffer(); // 存放修复后的字符串   
        TagsList[] unclosedTags = getUnclosedTags(str);  
        // 生成新字符串   
        for(int i = unclosedTags[0].size() - 1; i > -1; i--) {  
            fixed.append("<" + unclosedTags[0].get(i) + ">");  
        }  
        fixed.append(str);  
        for(int i = unclosedTags[1].size() - 1; i > -1; i--) {  
            String s = null;  
            if((s = unclosedTags[1].get(i)) != null) {  
                fixed.append("</" + s + ">");  
            }  
        }  
        return fixed.toString();  
    }  
    private static TagsList[] getUnclosedTags(String str) {  
        StringBuffer temp = new StringBuffer(); // 存放标签   
        TagsList[] unclosedTags = new TagsList[2];  
        unclosedTags[0] = new TagsList(); // 前不闭合，如有</div>而前面没有<div>   
        unclosedTags[1] = new TagsList(); // 后不闭合，如有<div>而后面没有</div>   
        boolean flag = false; // 记录双引号"或单引号'   
        char currentJump = ' '; // 记录需要跳过''还是""   
        char current = ' ', last = ' '; // 当前 & 上一个   
        // 开始判断   
        for(int i = 0; i < str.length();) {  
            current = str.charAt(i++); // 读取一个字符   
            if(current == '"' || current == '\'') {  
                flag = flag ? false : true; // 若为引号，flag翻转   
                currentJump = current;  
            }  
            if(!flag) {  
                if(current == '<') { // 开始提取标签   
                    current = str.charAt(i++);  
                    if(current == '/') { // 标签的闭合部分，如</div>   
                        current = str.charAt(i++);  
                        // 读取标签   
                        while(i < str.length() && current != '>') {  
                            temp.append(current);  
                            current = str.charAt(i++);  
                        }  
                        // 从tags_bottom移除一个闭合的标签   
                        if(!unclosedTags[1].remove(temp.toString())) { // 若移除失败，说明前面没有需要闭合的标签   
                            unclosedTags[0].add(temp.toString()); // 此标签需要前闭合   
                        }  
                        temp.delete(0, temp.length()); // 清空temp   
                    }else { // 标签的前部分，如<div>   
                        last = current;  
                        while(i < str.length() && current != ' ' && current != ' ' && current != '>') {  
                            temp.append(current);  
                            last = current;  
                            current = str.charAt(i++);  
                        }  
                        // 已经读取到标签，跳过其他内容，如<div id=test>跳过id=test   
                        while(i < str.length() && current != '>') {  
                            last = current;  
                            current = str.charAt(i++);  
                            if(current == '"' || current == '\'') { // 判断引号   
                                flag = flag ? false : true;  
                                currentJump = current;  
                                if(flag) { // 若引号不闭合，跳过到下一个引号之间的内容   
                                    while(i < str.length() && str.charAt(i++) != currentJump)  
                                        ;  
                                    current = str.charAt(i++);  
                                    flag = false;  
                                }  
                            }  
                        }  
                        if(last != '/' && current == '>') // 判断这种类型：<TagName />   
                            unclosedTags[1].add(temp.toString());  
                        temp.delete(0, temp.length());  
                    }  
                }  
            }else {  
                while(i < str.length() && str.charAt(i++) != currentJump)  
                    ; // 跳过引号之间的部分   
                flag = false;  
            }  
        }  
        return unclosedTags;  
    }  

	public String subStringHTML(String param, int length, String end) {
		StringBuffer result = new StringBuffer();
		int n = 0;
		char temp;
		boolean isCode = false; // 是不是HTML代码
		boolean isHTML = false; // 是不是HTML特殊字符,如&nbsp;
		for (int i = 0; i < param.length(); i++) {
			temp = param.charAt(i);
			if (temp == '<') {
				isCode = true;
			} else if (temp == '&') {
				isHTML = true;
			} else if (temp == '>' && isCode) {
				n = n - 1;
				isCode = false;
			} else if (temp == ';' && isHTML) {
				isHTML = false;
			}

			if (!isCode && !isHTML) {
				n = n + 1;
				// UNICODE码字符占两个字节
				if ((temp + "").getBytes().length > 1) {
					n = n + 1;
				}
			}

			result.append(temp);
			if (n >= length) {
				break;
			}
		}
		result.append(end);
		// 取出截取字符串中的HTML标记
		String temp_result = result.toString().replaceAll("(>)[^<>]*(<?)",
				"$1$2");
		// 去掉不需要结素标记的HTML标记
		temp_result = temp_result
				.replaceAll(
						"</?(AREA|BASE|BASEFONT|BODY|BR|COL|COLGROUP|DD|DT|FRAME|HEAD|HR|HTML|IMG|INPUT|ISINDEX|LI|LINK|META|OPTION|P|PARAM|TBODY|TD|TFOOT|TH|THEAD|TR|area|base|basefont|body|br|col|colgroup|dd|dt|frame|head|hr|html|img|input|isindex|li|link|meta|option|p|param|tbody|td|tfoot|th|thead|tr)[^<>]*/?>",
						"");
		// 去掉成对的HTML标记
		temp_result = temp_result.replaceAll("<([a-zA-Z]+)[^<>]*>(.*?)</\\1>",
				"$2");
		// 用正则表达式取出标记
		Pattern p = Pattern.compile("<([a-zA-Z]+)[^<>]*>");
		Matcher m = p.matcher(temp_result);

		List<String> endHTML = new ArrayList<String>();

		while (m.find()) {
			endHTML.add(m.group(1));
		}
		// 补全不成对的HTML标记
		for (int i = endHTML.size() - 1; i >= 0; i--) {
			result.append("</");
			result.append(endHTML.get(i));
			result.append(">");
		}

		return result.toString();
	}

}
