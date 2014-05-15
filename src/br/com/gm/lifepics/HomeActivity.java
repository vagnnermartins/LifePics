package br.com.gm.lifepics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
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
import br.com.gm.lifepics.constants.Constants;
import br.com.gm.lifepics.model.Foto;
import br.com.gm.lifepics.model.Moldura;
import br.com.gm.lifepics.model.TransferParse;
import br.com.gm.lifepics.uihelper.HomeGridUIHelper;
import br.com.gm.lifepics.uihelper.HomePolaroidUIHelper;

import com.componente.box.localizacao.util.ComponentBoxUtil;
import com.componente.box.localizacao.util.NavegacaoUtil;
import com.componente.box.localizacao.util.SessaoUtil;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;

public class HomeActivity extends Activity {

	private LinearLayout container;
	
	private HomePolaroidUIHelper polaroidUIHelper;
	private HomeGridUIHelper gridUIHelper;
	
//	private List<Foto> minhasFotos;
	private Map<String, Foto> minhasFotos;
	private Map<String, Moldura> molduras;

	private boolean isAtualizando;

	private Menu menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_home);
		init();
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
					TransferParse.getInstance().put(foto.getObjectId(), foto); 
					intent.putExtra(DetalheMolduraActivity.CACHE_DETALHE_FOTO, foto.getObjectId());
				}else{
					TransferParse.getInstance().put(molduraObjectId, molduras.get(molduraObjectId)); 
				}
				startActivity(intent);
			}
		};
	}

	private void buscarMinhasFotos() {
		configurarMenu(true);
		ParseQuery<Foto> qFoto = ParseQuery.getQuery(Foto.class);
		qFoto.whereEqualTo("usuario", ParseUser.getCurrentUser());
		qFoto.include("moldura");
		qFoto.setCachePolicy(CachePolicy.NETWORK_ELSE_CACHE);
		qFoto.findInBackground(configurarBuscarMinhasFotosCallback());
	}

	private FindCallback<Foto> configurarBuscarMinhasFotosCallback() {
		return new FindCallback<Foto>() {

			@Override
			public void done(List<Foto> fotos, ParseException arg1) {
				for (Foto foto : fotos) {
					minhasFotos.put(foto.getMoldura().getObjectId(), foto);
				}
				atualizarMoldurasComFoto(SessaoUtil.recuperarValores(HomeActivity.this, 
						Constants.ESTILO));
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
				container.findViewWithTag(foto.getMoldura().getObjectId() + "ellipze").setVisibility(View.GONE);
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
			verificaStatusAtualizandoMenu(menu);
			verificaEstiloAtual();
		}
		return true;
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_home_refresh:
			buscarMolduras();
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
		case R.id.menu_home_desconectar:
			ParseUser.logOut();
			NavegacaoUtil.navegar(this, LoginActivity.class);
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
