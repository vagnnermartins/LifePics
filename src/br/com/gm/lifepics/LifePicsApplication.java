package br.com.gm.lifepics;

import android.app.Activity;
import android.app.Application;
import br.com.gm.lifepics.callback.Callback;
import br.com.gm.lifepics.componente.MensagemDTO;
import br.com.gm.lifepics.constants.Constants;
import br.com.gm.lifepics.model.Foto;
import br.com.gm.lifepics.model.Moldura;
import br.com.gm.lifepics.util.CustomToastSliding;

import com.parse.Parse;
import com.parse.ParseObject;

public class LifePicsApplication extends Application{
	
	private Activity currentActivity;
	private CustomToastSliding toast;
	private MensagemDTO mensagem;
	private Callback callback;
	
	@Override
	public void onCreate() {
		super.onCreate();
		initParse();
	}
	
	private void initParse() {
		try {
			ParseObject.registerSubclass(Moldura.class);
			ParseObject.registerSubclass(Foto.class);
			Parse.initialize(this, Constants.PARSE_APP_ID, Constants.PARSE_CLIENT_KEY);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void adicionarMensagem(MensagemDTO mensagem){
		this.mensagem = mensagem;
	}
	
	/**
	 * Exibi o toast quando for carregada uma nova activity
	 */
	private void showMessage(){
		if(currentActivity != null && toast != null){
			toast.show();
		}
	}
	
	/**
	 * Força a exibição do toast na atual activity
	 * @param mensagem
	 */
	public void showMessage(MensagemDTO mensagem){
		toast.carregarMensagem(mensagem);
		toast.show();
	}
	
	public void dismissMessage(int time){
		if(toast != null){
			toast.removerToast(time);
		}
	}
	
	public void dismissMessage(int resMensagem, int time){
		if(toast != null){
			toast.alterarMensagem(resMensagem);
			toast.removerToast(time);
			if(callback != null){
				callback.onReturn(null);
			}
		}
	}
	
	public void setCurrentActivity(Activity currentActivity){
		this.currentActivity = currentActivity;
		if(currentActivity != null && mensagem != null){
			toast = new CustomToastSliding(currentActivity, mensagem);
			showMessage();
			mensagem = null;
		}
	}

	public Callback getCallback() {
		return callback;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

}
