package br.com.gm.lifepics.componente;

import java.io.Serializable;

import android.graphics.Bitmap;

@SuppressWarnings("serial")
public class MensagemDTO implements Serializable{
	
	private int tipoMensagem;
	private int mensagem;
	private Bitmap leftImage;
	private Integer time;
	public MensagemDTO(int tipoMensagem, int mensagem, Bitmap leftImage,
			Integer time) {
		super();
		this.tipoMensagem = tipoMensagem;
		this.mensagem = mensagem;
		this.leftImage = leftImage;
		this.time = time;
	}
	public int getTipoMensagem() {
		return tipoMensagem;
	}
	public void setTipoMensagem(int tipoMensagem) {
		this.tipoMensagem = tipoMensagem;
	}
	public int getMensagem() {
		return mensagem;
	}
	public void setMensagem(int mensagem) {
		this.mensagem = mensagem;
	}
	public Bitmap getLeftImage() {
		return leftImage;
	}
	public void setLeftImage(Bitmap leftImage) {
		this.leftImage = leftImage;
	}
	public Integer getTime() {
		return time;
	}
	public void setTime(Integer time) {
		this.time = time;
	}
}