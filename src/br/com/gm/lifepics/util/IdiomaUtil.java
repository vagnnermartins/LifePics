package br.com.gm.lifepics.util;

import java.util.Locale;

public class IdiomaUtil {
	public static String getDefaultLanguage(){
		String lang = Locale.getDefault().getLanguage();
		if(!lang.equals("pt") && !lang.equals("en") && !lang.equals("es")){
			lang = "en";
		}
		return lang;
	}
}
