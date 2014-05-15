package br.com.gm.lifepics.model;

import java.io.Serializable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@SuppressWarnings("serial")
@ParseClassName("Foto")
public class Foto extends ParseObject implements Serializable{
	
	public ParseFile getArquivo() {
		return getParseFile("arquivo");
	}
	
	public void setArquivo(ParseFile arquivo) {
		put("arquivo", arquivo);
	}
	public ParseUser getUsuario() {
		return getParseUser("usuario");
	}
	
	public void setUsuario(ParseUser usuario) {
		put("usuario", usuario);
	}
	public Moldura getMoldura(){
		return (Moldura) getParseObject("moldura");
	}
	public void setMoldura(Moldura moldura){
		put("moldura", moldura);
	}
}
