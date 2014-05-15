package br.com.gm.lifepics;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.gm.lifepics.model.Foto;
import br.com.gm.lifepics.model.TransferParse;

import com.componente.box.localizacao.util.ComponentBoxUtil;
import com.parse.ParseException;

public class SalvarCompartilharActivity extends Activity {
	
	public static final String FOTO_SALVAR_COMPARTILHAR = "foto_salvar_compartilhar";
	
	private Foto foto;
	
	private TextView titulo;
	private ImageView polaroid;
	private ImageView imagem;
	private TextView descricao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_salvar_compartilhar);
		init();
		carregarValores();
	}

	private void init() {
		configurarActionBar();
		recuperarExtra();
		titulo = (TextView) findViewById(R.id.salvar_compartilhar_titulo);
		polaroid = (ImageView) findViewById(R.id.salvar_compartilhar_polaroid);
		imagem = (ImageView) findViewById(R.id.salvar_compartilhar_imagem);
		descricao = (TextView) findViewById(R.id.salvar_compartilhar_descricao);
	}
	
	private void configurarActionBar() {
		getActionBar().setDisplayUseLogoEnabled(true);
		getActionBar().setTitle("");
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	private void carregarValores() {
		descricao.setText(foto.getMoldura().getLegenda());
		if(foto.getCreatedAt() == null){
			titulo.setText(R.string.salvar_compartilhamento_salvar);
		}else{
			titulo.setText(R.string.salvar_compartilhamento_compartilhar);
		}
		polaroid.post(new Runnable() {
			
			@Override
			public void run() {
				int width = polaroid.getWidth();
				int height = polaroid.getHeight();
				int marginLeft = (int) ((width * 0.555) / 10);
				int marginRigth = (int) ((width * 0.81) / 10);
				int marginTop = (int) ((height * 0.42) / 10);
				int marginBottom = (int) ((height * 22.91) / 100);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width - marginLeft - marginRigth,
						height - marginTop - marginBottom);
				imagem.setMaxHeight(height - marginTop - marginBottom);
				imagem.setMaxWidth(width - marginLeft - marginRigth);
				params.setMargins(marginLeft, marginTop, marginRigth, marginBottom);
				imagem.setAdjustViewBounds(true);
				imagem.setLayoutParams(params);
				imagem.setScaleType(ScaleType.FIT_XY);
				if(foto.getArquivo() != null){
					new CarregarImagemAsyncTask().execute();
				}
			}
		});
	}
	
	class CarregarImagemAsyncTask extends AsyncTask<Void, Void, Bitmap>{

		@Override
		protected Bitmap doInBackground(Void... params) {
			Bitmap bm = null;
			try {
				bm = ComponentBoxUtil.convertByteArrayToBitmap(foto.getArquivo().getData());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return bm;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			imagem.setImageBitmap(result);
		}
	}

	private void recuperarExtra() {
		foto = (Foto) TransferParse.getInstance().get(getIntent().getExtras().getString(FOTO_SALVAR_COMPARTILHAR));
	}
	
	public void onCompartilharClickListener(View view){
		ImageView click = (ImageView) view;
		Boolean tag = Boolean.valueOf((String) click.getTag());
		switch (click.getId()) {
		case R.id.salvar_compartilhar_facebook:
			if(tag){
				click.setImageResource(R.drawable.ic_share_facebook_off);
			}else{
				click.setImageResource(R.drawable.ic_share_facebook_on);
			}
			break;
		case R.id.salvar_compartilhar_twitter:
			if(tag){
				click.setImageResource(R.drawable.ic_share_twitter_off);
			}else{
				click.setImageResource(R.drawable.ic_share_twitter_on);
			}
			break;
		default:
			break;
		}
		click.setTag(String.valueOf(!tag));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
