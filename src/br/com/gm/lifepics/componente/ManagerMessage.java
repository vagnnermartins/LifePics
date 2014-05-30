//package br.com.gm.lifepics.componente;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class ManagerMessage {
//
//	private static ManagerMessage gerenciadorMensagem;
//	private static Map<String, MensagemDTO> map;
//	
//	private ManagerMessage(){
//	}
//	
//	public static ManagerMessage getInstance(){
//		if(gerenciadorMensagem == null){
//			gerenciadorMensagem = new ManagerMessage();
//			map = new HashMap<String, MensagemDTO>();
//		}
//		return gerenciadorMensagem;
//	}
//	
//	public void put(String key, MensagemDTO mensagem){
//		map.put(key, mensagem);
//	}
//	public void remove(String key){
//		map.remove(key);
//	}
//	public MensagemDTO get(String key){
//		return map.get(key);
//	}
//	
//}
