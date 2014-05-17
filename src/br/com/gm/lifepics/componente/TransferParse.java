package br.com.gm.lifepics.componente;

import java.util.HashMap;
import java.util.Map;

import com.parse.ParseObject;

public class TransferParse {
	
	private static TransferParse transferParse;
	private static Map<String, ParseObject> map;
	
	private TransferParse(){
	}
	
	public static TransferParse getInstance(){
		if(transferParse == null){
			transferParse = new TransferParse();
			map = new HashMap<String, ParseObject>();
		}
		return transferParse;
	}
	
	public void put(String key, ParseObject parseObject){
		map.put(key, parseObject);
	}
	public void remove(String key){
		map.remove(key);
	}
	public ParseObject get(String key){
		return map.get(key);
	}
}
