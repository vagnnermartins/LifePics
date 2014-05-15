package br.com.gm.lifepics.model;

import java.util.Locale;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Moldura")
public class Moldura extends ParseObject {
	public String getLegenda() {
		return getParseObject("frase").getString(Locale.getDefault().getLanguage());
	}
	
	public void setLegenda(String legenda) {
		put("legenda", legenda);
	}
	public String getTitulo() {
		return getParseObject("tema").getString(Locale.getDefault().getLanguage());
	}

	public void setTitulo(String titulo) {
		put("titulo", titulo);
	}
}
