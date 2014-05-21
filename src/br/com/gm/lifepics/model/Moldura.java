package br.com.gm.lifepics.model;

import java.util.Locale;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Moldura")
public class Moldura extends ParseObject {
	public String getLegenda() {
		String lang = Locale.getDefault().getLanguage();
		if(!lang.equals("pt") && !lang.equals("en") && !lang.equals("es")){
			lang = "en";
		}
		return getParseObject("frase").getString(lang);
	}
	
	public void setLegenda(String legenda) {
		put("legenda", legenda);
	}
	public String getTitulo() {
		String lang = Locale.getDefault().getLanguage();
		if(!lang.equals("pt") && !lang.equals("en") && !lang.equals("es")){
			lang = "en";
		}
		return getParseObject("tema").getString(lang);
	}

	public void setTitulo(String titulo) {
		put("titulo", titulo);
	}
}
