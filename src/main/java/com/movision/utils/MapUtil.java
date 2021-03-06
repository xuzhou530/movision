package com.movision.utils;

import org.apache.commons.collections.map.HashedMap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhuangyuhao
 * @since 15/12/12.
 */
public class MapUtil {
	/**
	 * 转换map key值 eg:{下划线转后首字母大写}
	 * 
	 * @param map
	 *            map
	 * @return newMap
	 */
	private static Map<String, String> mapKeyToUpper(Map<String, String> map) {
		Map<String, String> newMap = new HashMap<>();
		for (Object o : map.entrySet()) {

			Map.Entry entry = (Map.Entry) o;
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			if (key.contains("_")) {
				String newKey = replaceUnderlineAndfirstToUpper(key, "_", "");
				newMap.put(newKey, value);
			} else {
				newMap.put(key, value);
			}
		}
		return newMap;
	}

	/**
	 * 首字母大写
	 *
	 * @param srcStr
	 *            要转换的字符
	 * @return newStr
	 */
	public static String firstCharacterToUpper(String srcStr) {
		return srcStr.substring(0, 1).toUpperCase() + srcStr.substring(1);
	}

	/**
	 * 替换字符串并让它的下一个字母为大写
	 *
	 * @param srcStr
	 *            要转换的字符
	 * @param org
	 *            要替换的字符
	 * @param ob
	 *            附加字段
	 * @return newString eg: replaceUnderlineAndfirstToUpper("house_id","_","")
	 *         {house_id -> houseId}
	 *         replaceUnderlineAndfirstToUpper("house_id","_","A") {house_id ->
	 *         houseAId}
	 */
	public static String replaceUnderlineAndfirstToUpper(String srcStr, String org, String ob) {
		String newString = "";
		int first;
		while (srcStr.contains(org)) {
			first = srcStr.indexOf(org);
			if (first != srcStr.length()) {
				newString = newString + srcStr.substring(0, first) + ob;
				srcStr = srcStr.substring(first + org.length(), srcStr.length());
				srcStr = firstCharacterToUpper(srcStr);
			}
		}
		newString = newString + srcStr;
		return newString;
	}

	/**
	 * 根据参数创建HashMap
	 * 
	 * @param args
	 *            形式为key,value,key,value..
	 * @return
	 */
	public static Map convert2HashMap(Object... args) {
		Map<Object, Object> result = new HashMap<>();
		
		int argsNum = args.length;
		if (argsNum % 2 == 0) {
			//key的数量
			int count = argsNum / 2;
			int idx = 0;
			while (count > 0) {
				Object key = args[idx];
				Object value = args[idx + 1];
				result.put(key, value);

				idx += 2;
				count--;
			}
		}
		return result;
	}
	
	public static Map<Object, Object> convertStringMapToObjectMap (Map<String, String> map){
		Map<Object, Object> result = new HashMap<Object, Object>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String value = entry.getValue();
			 if (value != null) {
				 result.put(entry.getKey(), value);
             }else{
            	 result.put(entry.getKey(), null);
             }
		}
		return result;
	}

	public static Boolean isEmpty(Map map) {
		return null == map || map.size() < 1;
	}

	public static Boolean isNotEmpty(Map map) {
		return null != map && map.size() >= 1;
	}

	public static void main(String[] args) {
		Map map = new HashedMap();
		map.put("1", 11111);
		map.put("2", 222222);
		System.out.println(map.toString());
	}
	
}
