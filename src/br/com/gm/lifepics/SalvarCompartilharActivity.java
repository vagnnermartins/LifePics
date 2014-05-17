package br.com.gm.lifepics;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.gm.lifepics.componente.ManagerMessage;
import br.com.gm.lifepics.componente.MensagemDTO;
import br.com.gm.lifepics.componente.TransferParse;
import br.com.gm.lifepics.constants.Constants;
import br.com.gm.lifepics.model.Foto;
import br.com.gm.lifepics.util.FacebookUtil;

import com.componente.box.localizacao.util.ComponentBoxUtil;
import com.componente.box.toast.ToastSliding;
import com.facebook.Request.Callback;
import com.facebook.Response;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class SalvarCompartilharActivity extends Activity {
	
	public static final String FOTO_SALVAR_COMPARTILHAR = "foto_salvar_compartilhar";
	public static final String PRIMEIRA_FOTO_NA_MOLDURA = "primeira_foto_na_moldura";
	private static final int REAUTH_ACTIVITY_CODE = 0;
	
	private Foto foto;
	private boolean primeiraFotoNaMoldura;
	
	private TextView titulo;
	private ImageView polaroid;
	private ImageView imagem;
	private TextView descricao;
	
	private MensagemDTO mensagem;
	
	private boolean currentActivity;
	
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
	
	public void onSalvarCompartilhar(View view){
		try {
			ComponentBoxUtil.verificaConexao(this);
			if(!primeiraFotoNaMoldura){
				// Se atualmente ele não tem foto na current moldura eu salvo
				salvar();
			}else{
				// Está apenas compartilhando uma foto existente
				compartilhar();
			}
		} catch (Exception e) {
			new ToastSliding(this).show(ToastSliding.INFO_MESSAGE, 
					getResources().getString(R.string.msg_sem_internet), 
					ToastSliding.SLOW_MESSAGE);
		}
	}

	private void salvar() {
		exibirMensagem(R.string.msg_salvando_foto);
		foto.setUsuario(ParseUser.getCurrentUser());
		foto.saveInBackground(configurarSaveFotoCallback());
	}

	/**
	 * Coloco na ManagerSessão a mensagem a ser exibida quando finalizar a ação.
	 * @param resMensagem mensagem a ser exibida
	 */
	private void exibirMensagem(int resMensagem) {
		try {
			mensagem = new MensagemDTO(
					ComponentBoxUtil.convertByteArrayToBitmap(foto.getArquivo().getData()), 
					resMensagem, 
					Constants.STATUS_PENDENTE);
			ManagerMessage.getInstance().put(Constants.MENSAGEM_TOAST, mensagem);
			/**
			 * Verifica se está na Current Activity, caso esteja redireciona para a Home
			 */
			if(currentActivity){
				Intent intent = new Intent(SalvarCompartilharActivity.this, HomeActivity.class); 
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		currentActivity = false;
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    currentActivity = true;
	}

	private void compartilhar() {
		Boolean shareFace = Boolean.valueOf((String)findViewById(R.id.salvar_compartilhar_facebook).getTag());
		Boolean shareTwitter = Boolean.valueOf((String)findViewById(R.id.salvar_compartilhar_twitter).getTag());
		if(shareFace){
			FacebookUtil.publicarNoMural(SalvarCompartilharActivity.this, 
					foto.getMoldura().getLegenda(), foto.getArquivo(), REAUTH_ACTIVITY_CODE, configurarCallbackCompartilharFace());
		}
		if(shareTwitter){
			//TODO Implementar compartilhar no Twitter
		}
		if(!shareFace && !shareTwitter){
			//Não compartilhou e nem salvou, então finalizo a activity voltando para o DetalheMolduraActivity
			finish();
		}else{
			// Compartilhou no face ou twitter, então exibo a mensagem de compartilhando!
			exibirMensagem(R.string.msg_compartilhando);
		}
	}
	
	private Callback configurarCallbackCompartilharFace() {
		return new Callback() {
			
			@Override
			public void onCompleted(Response response) {
				if(mensagem.getCallback() != null){
					mensagem.getCallback().onReturn(null);
				}else{
					ManagerMessage.getInstance().get(Constants.MENSAGEM_TOAST).setStatus(Constants.STATUS_EXIBIDA);
				}
				finish();
			}
		};
	}

	private SaveCallback configurarSaveFotoCallback() {
		return new SaveCallback() {
			
			@Override
			public void done(ParseException exception) {
				if(exception == null){
					mensagem.getCallback().onReturn(null);
					compartilhar();
				}else{
					/**
					 * Se não foi possível salvar enviar mensagem de erro
					 */
					mensagem.setMensagem(R.string.msg_descricao_erro_salvar);
					mensagem.getCallback().onReturn(null);
				}
			}
		};
	}

	/**
	 * Carrega os valores para a tela, verifica se o usuário 
	 * está salvando ou apenas compartilhando uma foto
	 */
	private void carregarValores() {
		descricao.setText(foto.getMoldura().getLegenda());
		if(primeiraFotoNaMoldura){
			titulo.setText(R.string.salvar_compartilhamento_compartilhar);
		}else{
			titulo.setText(R.string.salvar_compartilhamento_salvar);
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
	
	/**
	 * AsyncTask para Carregar o Bitmap a ser usado para não travar a Thread Principal
	 * @author vagnnermartins
	 *
	 */
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
	
	/**
	 * Recupero os valores passado pela Intent
	 */
	private void recuperarExtra() {
		foto = (Foto) TransferParse.getInstance().get(getIntent().getExtras().getString(FOTO_SALVAR_COMPARTILHAR));
		primeiraFotoNaMoldura = getIntent().getExtras().getBoolean(PRIMEIRA_FOTO_NA_MOLDURA);
	}
	
	/**
	 * Habilita ou desabilita a opção de compartilhar clicada
	 * @param view
	 */
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			switch (requestCode) {
			/**
			 * Resulto caso seja o primeiro compartilhamento do usuário pelo facebook. 
			 */
			case REAUTH_ACTIVITY_CODE:
				ParseFacebookUtils.getSession().onActivityResult(this, requestCode, resultCode, data);
				FacebookUtil.publicarNoMural(this, foto.getMoldura().getLegenda(), foto.getArquivo(), REAUTH_ACTIVITY_CODE, configurarCallbackCompartilharFace());
				break;

			default:
				break;
			}
		}
		finish();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finalizar();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		finalizar();
	}

	private void finalizar() {
		setResult(RESULT_FIRST_USER);
		finish();
	}
}
