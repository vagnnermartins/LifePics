package br.com.gm.lifepics.componente;

import br.com.gm.lifepics.callback.Callback;
import android.graphics.Bitmap;

public class MensagemDTO {

	private Bitmap imagem;
	private int mensagem;
	private String status;
	private Callback callback;
	public MensagemDTO(Bitmap imagem, int mensagem, String status) {
		super();
		this.imagem = imagem;
		this.mensagem = mensagem;
		this.status = status;
	}
	public Bitmap getImagem() {
		return imagem;
	}
	public void setImagem(Bitmap imagem) {
		this.imagem = imagem;
	}
	public int getMensagem() {
		return mensagem;
	}
	public void setMensagem(int mensagem) {
		this.mensagem = mensagem;
	}
	public Callback getCallback() {
		return callback;
	}
	public void setCallback(Callback callback) {
		this.callback = callback;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
