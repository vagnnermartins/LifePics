package br.com.gm.lifepics.model;

import br.com.gm.lifepics.util.IdiomaUtil;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Moldura")
public class Moldura extends ParseObject {
	public String getLegenda() {
		return getParseObject("frase").getString(IdiomaUtil.getDefaultLanguage());
	}
	
	public void setLegenda(String legenda) {
		put("legenda", legenda);
	}
	public String getTitulo() {
		return getParseObject("tema").getString(IdiomaUtil.getDefaultLanguage());
	}

	public void setTitulo(String titulo) {
		put("titulo", titulo);
	}
}
