package br.com.gm.lifepics.util;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.gm.lifepics.R;

public class ToastSliding extends RelativeLayout {
	
	private ImageView imagem;
	private TextView texto;
	private ImageView btnFechar;
	private ProgressBar progress;
	private View view;
	private Context context;
	private FrameLayout viewRoot;
	public static final int LONG_MESSAGE = 10000;
	public static final int SLOW_MESSAGE = 4000;
	public static final int FAST_MESSAGE = 2000;

	public static final int INFO_MESSAGE = 1;
	public static final int WARNNING_MESSAGE = 2;
	public static final int ERROR_MESSAGE = 3;
	public static final int SUCCESS_MESSAGE = 4;
	public static final int FOTO_MESSAGE = 5;
	

	public ToastSliding(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	@SuppressLint("NewApi")
	private void init() {
		Activity activity = ((Activity)context);
		viewRoot = (FrameLayout)activity.getWindow().getDecorView().findViewById(android.R.id.content);
		
		LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); // You might want to tweak these to WRAP_CONTENT
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.view = inflater.inflate(R.layout.toast_lp, null);
		view.setLayoutParams(params);
		
		LayoutTransition transition = new LayoutTransition();
		setLayoutTransition(transition);
		
		carregarComponentes();
	}

	private void carregarComponentes() {
		texto = (TextView)view.findViewById(R.id.toast_lp_texto_msgs);
		btnFechar = (ImageView)view.findViewById(R.id.toast_lp_btn_status);
		imagem = (ImageView)view.findViewById(R.id.toast_lp_img_msgs);
		progress = (ProgressBar)view.findViewById(R.id.toast_lp_progress);
		
		btnFechar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewRoot.removeView(view);
			}
			
		});
	}
	
	public void show(int tipo, Bitmap bitmap, int mensagem){
		this.removeAllViews();
		imagem.setImageBitmap(bitmap);
		texto.setText(mensagem);
		configurarTipoMensagem(tipo);
		viewRoot.addView(view);
	}
	
	public void alterarMensagem(int mensagem){
		texto.setText(mensagem);
	}
	
	public void alterarMensagem(int mensagem, int imageResourceBtnFechar){
		texto.setText(mensagem);
		btnFechar.setImageResource(imageResourceBtnFechar);
	}
	
	public void removerToast(int time){
		progress.setVisibility(ProgressBar.GONE);
		btnFechar.setVisibility(ImageView.VISIBLE);
		new RemoverMensagemAsyncTask(time).execute();
	}
	
	private void configurarTipoMensagem(int tipo) {
		int idColor = 0;
		
		switch (tipo) {
		case INFO_MESSAGE:
			idColor = R.color.info;
			break;

		case WARNNING_MESSAGE:
			idColor = R.color.warnning;
			break;

		case ERROR_MESSAGE:
			idColor = R.color.error;
			break;
			
		case SUCCESS_MESSAGE:
			idColor = R.color.success;
			break;
		case FOTO_MESSAGE:
			idColor = R.color.cinza;
			break;
		default:
			break;
		}
		
		view.setBackgroundResource(idColor);
		
	}

	public class RemoverMensagemAsyncTask extends AsyncTask<Void, Void, String>{
		
		int time;
		public RemoverMensagemAsyncTask(int time) {
			this.time = time;
		}
		
		@SuppressWarnings("static-access")
		@Override
		protected String doInBackground(Void... params) {
			try {
				new Thread().sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String retorno) {
			viewRoot.removeView(view);
		}
	}

}
