package br.com.gm.lifepics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import br.com.gm.lifepics.callback.Callback;
import br.com.gm.lifepics.componente.ManagerMessage;
import br.com.gm.lifepics.componente.MensagemDTO;
import br.com.gm.lifepics.componente.TransferParse;
import br.com.gm.lifepics.constants.Constants;
import br.com.gm.lifepics.model.Foto;
import br.com.gm.lifepics.model.Moldura;
import br.com.gm.lifepics.uihelper.HomeGridUIHelper;
import br.com.gm.lifepics.uihelper.HomePolaroidUIHelper;
import br.com.gm.lifepics.util.DialogUtil;
import br.com.gm.lifepics.util.ToastSliding;

import com.componente.box.localizacao.util.ComponentBoxUtil;
import com.componente.box.localizacao.util.NavegacaoUtil;
import com.componente.box.localizacao.util.SessaoUtil;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFacebookUtils.Permissions;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;

public class HomeActivity extends Activity {

	private LinearLayout container;
	
	private HomePolaroidUIHelper polaroidUIHelper;
	private HomeGridUIHelper gridUIHelper;
	
	private Map<String, Foto> minhasFotos;
	private Map<String, Moldura> molduras;

	private boolean isAtualizando;

	private Menu menu;

	private ToastSliding toast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_home);
		init();
	}

	private void atualizar() {
		buscarMolduras();
		buscarMinhasFotos();
	}
	
	private void init() {
		getActionBar().setDisplayUseLogoEnabled(true);
		getActionBar().setTitle("");
		container = (LinearLayout) findViewById(R.id.home_container);
		polaroidUIHelper = new HomePolaroidUIHelper(HomeActivity.this, configurarOnItemClickListener());
		gridUIHelper = new HomeGridUIHelper(HomeActivity.this, configurarOnItemClickListener());
		minhasFotos = new HashMap<String, Foto>();
		molduras = new HashMap<String, Moldura>();
		atualizar();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		verificarMensagemNaSessao();
	}
	
	private void verificarMensagemNaSessao(){
		MensagemDTO mensagem = ManagerMessage.getInstance().get(Constants.MENSAGEM_TOAST);
		if(mensagem != null){
			mensagem.setCallback(configurarCallbackMensagem());
			toast = new ToastSliding(this);
			toast.show(ToastSliding.FOTO_MESSAGE, 
					mensagem.getImagem(), 
					mensagem.getMensagem());
			if(mensagem.getStatus().equals(Constants.STATUS_EXIBIDA)){
				mensagem.getCallback().onReturn(null);
			}
			ManagerMessage.getInstance().remove(Constants.MENSAGEM_TOAST);
		}
	}

	private Callback configurarCallbackMensagem() {
		return new Callback() {
			
			@Override
			public void onReturn(Exception excpetion, Object... objects) {
				toast.alterarMensagem(R.string.msg_finalizado);
				toast.removerToast(ToastSliding.SLOW_MESSAGE);
				atualizar();
			}
		};
	}

	private OnClickListener configurarOnItemClickListener() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String molduraObjectId = (String) v.getTag();
				final Foto foto = minhasFotos.get(molduraObjectId);
				Intent intent = new Intent(HomeActivity.this, DetalheMolduraActivity.class);
				intent.putExtra(DetalheMolduraActivity.CACHE_DETALHE_MOLDURA, molduraObjectId);
				if(foto != null){
					foto.setMoldura(molduras.get(molduraObjectId));
					intent.putExtra(molduraObjectId, foto.getObjectId());
					TransferParse.getInstance().put(foto.getObjectId(), foto); 
				}else{
					TransferParse.getInstance().put(molduraObjectId, molduras.get(molduraObjectId)); 
				}
				startActivity(intent);
			}
		};
	}

	private void buscarMinhasFotos() {
		if(ParseUser.getCurrentUser() != null){
			configurarMenu(true);
			ParseQuery<Foto> qFoto = ParseQuery.getQuery(Foto.class);
			qFoto.whereEqualTo("usuario", ParseUser.getCurrentUser());
			qFoto.include("moldura");
			qFoto.setCachePolicy(CachePolicy.NETWORK_ELSE_CACHE);
			qFoto.findInBackground(configurarBuscarMinhasFotosCallback());
		}
	}

	private FindCallback<Foto> configurarBuscarMinhasFotosCallback() {
		return new FindCallback<Foto>() {

			@Override
			public void done(List<Foto> fotos, ParseException exception) {
				if(exception == null){
					minhasFotos = new HashMap<String, Foto>();
					for (Foto foto : fotos) {
						minhasFotos.put(foto.getMoldura().getObjectId(), foto);
					}
					atualizarMoldurasComFoto(SessaoUtil.recuperarValores(HomeActivity.this, 
							Constants.ESTILO));
				}else{
					new com.componente.box.toast.ToastSliding(HomeActivity.this).show(com.componente.box.toast.ToastSliding.ERROR_MESSAGE, 
							getString(R.string.msg_erro_buscar_fotos), 
							com.componente.box.toast.ToastSliding.SLOW_MESSAGE);
				}
			}
		};
	}

	private void buscarMolduras() {
		configurarMenu(true);
		ParseQuery<Moldura> pQuery = ParseQuery.getQuery(Moldura.class);
		pQuery.include("tema");
		pQuery.include("frase");
		pQuery.setCachePolicy(CachePolicy.NETWORK_ELSE_CACHE);
		pQuery.findInBackground(configurarCallbackBuscarMolduras());
	}

	private void configurarMenu(boolean isAtualizando) {
		this.isAtualizando = isAtualizando;
		onPrepareOptionsMenu(menu);
	}

	private FindCallback<Moldura> configurarCallbackBuscarMolduras() {
		return new FindCallback<Moldura>() {

			@Override
			public void done(List<Moldura> result, ParseException exception) {
				if(exception == null){
					for (Moldura moldura : result) {
						molduras.put(moldura.getObjectId(), moldura);
					}
					container.removeAllViews();
					container.addView(polaroidUIHelper.getView());
					container.addView(gridUIHelper.getView());
					atualizarEstiloAtual();
					configurarMenu(false);
				}else{
					new com.componente.box.toast.ToastSliding(HomeActivity.this).show(com.componente.box.toast.ToastSliding.ERROR_MESSAGE, 
							getString(R.string.msg_erro_buscar_moldura), 
							com.componente.box.toast.ToastSliding.SLOW_MESSAGE);
				}
			}
		};
	}
	
	private void atualizarEstiloAtual() {
		new Handler().post(new Runnable() {
			
			@Override
			public void run() {
				if(SessaoUtil.recuperarValores(HomeActivity.this, Constants.ESTILO).equals(Constants.POLAROID)){
					polaroidUIHelper.configurarMolduras(molduras);
					atualizarMoldurasComFoto(Constants.POLAROID);
				}else if(SessaoUtil.recuperarValores(HomeActivity.this, Constants.ESTILO).equals(Constants.GRID)){
					gridUIHelper.configurarMolduras(molduras);
					atualizarMoldurasComFoto(Constants.GRID);
				}
			}
		});
	}
	
	private void verificaEstiloAtual() {
		if(SessaoUtil.recuperarValores(this, Constants.ESTILO).equals(Constants.POLAROID)){
			menu.findItem(R.id.menu_home_polaroid).setVisible(false);
			menu.findItem(R.id.menu_home_grid).setVisible(true);
			polaroidUIHelper.getView().setVisibility(View.VISIBLE);
			gridUIHelper.getView().setVisibility(View.GONE);
		}else{
			menu.findItem(R.id.menu_home_polaroid).setVisible(true);
			menu.findItem(R.id.menu_home_grid).setVisible(false);
			gridUIHelper.getView().setVisibility(View.VISIBLE);
			polaroidUIHelper.getView().setVisibility(View.GONE);
		}
	}
	
	private void atualizarMoldurasComFoto(String gridOuPolaroid) {
		if(!minhasFotos.isEmpty()){
			for (Entry<String, Foto> item : minhasFotos.entrySet()) {
				new CarregarImagemAsyncTask(item.getValue(), gridOuPolaroid).execute();
			}
			configurarMenu(false);
		}
	}
	
	class CarregarImagemAsyncTask extends AsyncTask<Void, Void, Bitmap>{
		private Foto foto;
		private ImageView imagem;
		private String gridOuPolaroid;

		public CarregarImagemAsyncTask(Foto foto, String gridOuPolaroid) {
			this.foto = foto;
			this.gridOuPolaroid = gridOuPolaroid;
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			Bitmap bm = null;
			try {
				imagem = (ImageView) container.findViewWithTag(foto.getMoldura().getObjectId() + gridOuPolaroid);
				if(imagem != null){
					bm = ComponentBoxUtil.convertByteArrayToBitmap(foto.getArquivo().getData());
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return bm;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if(imagem != null && result != null){
				imagem.setImageBitmap(result);
				imagem.setVisibility(View.VISIBLE);
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if(menu != null){
			this.menu = menu;
			menu.clear();
			getMenuInflater().inflate(R.menu.menu_home, menu);
			verificarMenuLoginOuLogout();
			verificaStatusAtualizandoMenu(menu);
			verificaEstiloAtual();
		}
		return true;
	}

	private void verificarMenuLoginOuLogout() {
		ParseUser user = ParseUser.getCurrentUser();
		if(user == null){
			menu.findItem(R.id.menu_home_login).setVisible(true);
			menu.findItem(R.id.menu_home_logout).setVisible(false);
		}else{
			menu.findItem(R.id.menu_home_login).setVisible(false);
			menu.findItem(R.id.menu_home_logout).setVisible(true);
		}
	}

	private void verificaStatusAtualizandoMenu(Menu menu) {
		if(isAtualizando){
			menu.findItem(R.id.menu_home_refresh).setVisible(false);
			setProgressBarIndeterminateVisibility(isAtualizando);
		}else{
			menu.findItem(R.id.menu_home_refresh).setVisible(true);
			setProgressBarIndeterminateVisibility(isAtualizando);
		}
	}
	
	private LogInCallback callBackLoginFacebook() {
		return new LogInCallback() {
			
			@Override
			public void done(ParseUser user, ParseException err) {
				setProgressBarIndeterminateVisibility(false);
				if (user == null) {
			    } else if (user.isNew()) {
			    	onPrepareOptionsMenu(menu);
			    } else {
			    	onPrepareOptionsMenu(menu);
			    }
				DialogUtil.show(HomeActivity.this, R.string.bem_vindo,
						R.string.msg_login_realizado, 
						configurarOnPositiveButtonLogin(), 
						android.R.string.ok, 
						null, 0);	
			}

			private android.content.DialogInterface.OnClickListener configurarOnPositiveButtonLogin() {
				return new android.content.DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				};
			}
		};
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_home_refresh:
			atualizar();
			break;
		case R.id.menu_home_grid:
			SessaoUtil.adicionarValores(this, Constants.ESTILO, Constants.GRID);
			verificaEstiloAtual();
			if(!gridUIHelper.contemConteudo()){
				atualizarEstiloAtual();
			}
			break;
		case R.id.menu_home_polaroid:
			SessaoUtil.adicionarValores(this, Constants.ESTILO, Constants.POLAROID);
			verificaEstiloAtual();
			if(!polaroidUIHelper.contemConteudo()){
				atualizarEstiloAtual();
			}
			break;
		case R.id.menu_home_logout:
			ParseUser.logOut();
			NavegacaoUtil.navegar(this, LoginActivity.class);
			finish();
			break;
		case R.id.menu_home_login:
			login();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void login() {
		ParseFacebookUtils.logIn(
			Arrays.asList(Permissions.User.ABOUT_ME, Permissions.User.BIRTHDAY, Permissions.User.RELATIONSHIPS),
			this, callBackLoginFacebook());
		setProgressBarIndeterminateVisibility(true);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}
}
