package br.com.gm.lifepics;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.gm.lifepics.callback.Callback;
import br.com.gm.lifepics.componente.TransferParse;
import br.com.gm.lifepics.model.Foto;
import br.com.gm.lifepics.model.Moldura;
import br.com.gm.lifepics.util.DialogUtil;
import br.com.gm.lifepics.util.ToastSliding;

import com.componente.box.localizacao.util.ComponentBoxUtil;
import com.componente.box.localizacao.util.CropImage;
import com.componente.box.localizacao.util.DataUtil;
import com.componente.box.localizacao.util.NavegacaoUtil;
import com.componente.box.localizacao.util.SessaoUtil;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

public class DetalheMolduraActivity extends Activity {
	
	public static final String CACHE_DETALHE_MOLDURA = "cache_detalhe_moldura";
	private static final int RESULT_TAKE_IMAGE = 1;
	private static final int RESULT_LOAD_IMAGE = 2;
	private static final int REQUEST_SALVAR_COMPARTILHAR = 3;
	
	private static final String PATH_TAKE_PICTURE = "path_take_picture";
	
	private TextView descricao;
	private TextView titulo;
	private ImageView imagem;
	private ImageView polaroid;
	private TextView ellipze;
	
	private Foto foto;
	private Bitmap bitmapImagem;
	private boolean primeiraFotoNaMoldura;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalhe_moldura);
		init();
	}

	private void init() {
		configurarActionBar();
		descricao = (TextView) findViewById(R.id.detalhe_moldura_descricao);
		titulo = (TextView) findViewById(R.id.detalhe_moldura_titulo);
		polaroid = (ImageView) findViewById(R.id.detalhe_moldura_polaroid);
		imagem = (ImageView) findViewById(R.id.detalhe_moldura_imagem);
		imagem.setOnClickListener(configurarOnImagemClickListener());
		ellipze = (TextView) findViewById(R.id.detalhe_moldura_ellipze);
		recuperarExtras();
		carregarValores();
	}

	private OnClickListener configurarOnImagemClickListener() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				exibirDialogAdicionarFoto();
			}
		};
	}

	private void configurarActionBar() {
		getActionBar().setDisplayUseLogoEnabled(true);
		getActionBar().setTitle("");
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void recuperarExtras() {
		String objectIdMoldura = getIntent().getExtras().getString(CACHE_DETALHE_MOLDURA);
		String keyFoto = getIntent().getExtras().getString(objectIdMoldura);
		if(keyFoto != null){
			foto = (Foto) TransferParse.getInstance().get(keyFoto);
		}
		if(foto == null){
			foto = new Foto();
			foto.setMoldura((Moldura) TransferParse.getInstance().get(objectIdMoldura));
		}else{
			primeiraFotoNaMoldura = true;
		}
	}
	
	private void carregarValores() {
		descricao.setText(foto.getMoldura().getLegenda());
		titulo.setText(foto.getMoldura().getTitulo());
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
				if(foto.getCreatedAt() != null){
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
			ellipze.setVisibility(View.GONE);
			bitmapImagem = result;
		}
	}
	
	class CriarNovaImagemParseFileAsyncTask extends AsyncTask<Void, Void, Void>{

		private Bitmap bitmap;
		private Callback callback;

		public CriarNovaImagemParseFileAsyncTask(Bitmap bitmap, Callback callback) {
			this.bitmap = bitmap;
			this.callback = callback;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			String imageFileName = "JPEG_" + DataUtil.transformDateToSting(new Date(), "dd_MM_yyyy_HH_mm_ss") + "_.jpg";
			foto.setArquivo(new ParseFile(imageFileName, ComponentBoxUtil.convertBitmapToBytes(bitmap)));
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(callback != null){
				callback.onReturn(null);
			}
		}
	}
	
	private void exibirDialogAdicionarFoto() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(R.array.adicionar_foto, onItemAdicionarFotoClickListener());
		builder.setTitle(R.string.menu_detalhe_moldura_excluir);
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}
	
	private android.content.DialogInterface.OnClickListener onItemAdicionarFotoClickListener() {
		return new android.content.DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					onClickTirarFoto();
					break;
				case 1:
					onClickBuscarGaleria();
					break;
				default:
					break;
				}
			}

			private void onClickBuscarGaleria() {
				Intent i = new Intent(Intent.ACTION_PICK, 
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}

			private void onClickTirarFoto() {
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			        File photoFile = null;
			        try {
			            photoFile = createImageFile();
			        } catch (IOException ex) {
			        	System.out.println(ex);
			        }
			        if (photoFile != null) {
			            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
			                    Uri.fromFile(photoFile));
			            startActivityForResult(takePictureIntent, RESULT_TAKE_IMAGE);
			        }
			    }
			}
			
			private File createImageFile() throws IOException {
			    String imageFileName = "LP_" + DataUtil.transformDateToSting(new Date(), "dd_MM_yyyy_HH_mm_ss") + "_";
			    File storageDir = Environment.getExternalStoragePublicDirectory(
			            Environment.DIRECTORY_PICTURES);
			    File image = File.createTempFile(
			        imageFileName,  /* prefix */
			        ".jpg",         /* suffix */
			        storageDir      /* directory */
			    );
			    SessaoUtil.adicionarValores(getApplicationContext(), PATH_TAKE_PICTURE, image.getAbsolutePath());
			    return image;
			}
		};
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
				switch (requestCode) {
				case RESULT_LOAD_IMAGE:
					try {
						if(data != null){
							buscarImagemNaGaleria(data);
							String pathImage = SessaoUtil.recuperarValores(getApplicationContext(), PATH_TAKE_PICTURE);
							CropImage.doCrop(DetalheMolduraActivity.this, Uri.fromFile(new File(pathImage)), 300, 300);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case RESULT_TAKE_IMAGE:
					String pathImage = SessaoUtil.recuperarValores(getApplicationContext(), PATH_TAKE_PICTURE);
					CropImage.doCrop(DetalheMolduraActivity.this, Uri.fromFile(new File(pathImage)), 300, 300);
					break;
				case CropImage.CROP_IMAGE:
					try {
						cropImage(data);
						if(foto.getCreatedAt() != null){
							primeiraFotoNaMoldura = false;
						}
					} catch (ParseException e) {
					}
					break;
				}
		} else if(resultCode == RESULT_FIRST_USER){
			if(primeiraFotoNaMoldura || foto.getCreatedAt() != null){
				imagem.setImageBitmap(bitmapImagem);
				new CriarNovaImagemParseFileAsyncTask(bitmapImagem, null).execute();
			}else{
				imagem.setImageResource(android.R.color.transparent);
				ellipze.setVisibility(View.VISIBLE);
			}
		}else{
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void iniciarCompartilharESalvar() {
		TransferParse.getInstance().put(foto.getObjectId(), foto);
		Map<String, Serializable> extras = new HashMap<String, Serializable>();
		extras.put(SalvarCompartilharActivity.FOTO_SALVAR_COMPARTILHAR, foto.getObjectId());
		extras.put(SalvarCompartilharActivity.PRIMEIRA_FOTO_NA_MOLDURA, primeiraFotoNaMoldura);
		NavegacaoUtil.navegarComResultComExtra(this, 
				SalvarCompartilharActivity.class, 
				REQUEST_SALVAR_COMPARTILHAR, extras);
	}
	
	private void cropImage(Intent data) throws ParseException {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			imagem.setImageBitmap(photo);
			new CriarNovaImagemParseFileAsyncTask(photo, configurarOnCriarNovaImagemCallback()).execute();
			ellipze.setVisibility(View.GONE);
		}
	}
	
	private Callback configurarOnCriarNovaImagemCallback() {
		return new Callback() {

			@Override
			public void onReturn(Exception excpetion, Object... objects) {
				iniciarCompartilharESalvar();
			}
		};
	}

	private void buscarImagemNaGaleria(Intent data) throws Exception {
		Uri selectedImage = data.getData();
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		SessaoUtil.adicionarValores(getApplicationContext(), PATH_TAKE_PICTURE, cursor.getString(columnIndex));
		cursor.close();
	}
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(menu != null){
			menu.clear();
		}
		getMenuInflater().inflate(R.menu.menu_detalhe_moldura, menu);
		if(foto.getCreatedAt() == null){
			menu.findItem(R.id.menu_detalhe_moldura_compartilhar).setVisible(false);
			menu.findItem(R.id.menu_detalhe_moldura_excluir).setVisible(false);
		}else{
			menu.findItem(R.id.menu_detalhe_moldura_compartilhar).setVisible(true);
			menu.findItem(R.id.menu_detalhe_moldura_excluir).setVisible(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			TransferParse.getInstance().remove(foto.getObjectId());
			finish();
			break;
		case R.id.menu_detalhe_moldura_tirar_foto:
			exibirDialogAdicionarFoto();
			break;
		case R.id.menu_detalhe_moldura_compartilhar:
			iniciarCompartilharESalvar();
			break;
		case R.id.menu_detalhe_moldura_excluir:
			excluirFoto();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void excluirFoto() {
		DialogUtil.show(this, 
				R.string.menu_detalhe_moldura_excluir, 
				R.string.msg_descricao_detalhe_moldura_excluir, 
				configurarPositiveButton(), android.R.string.yes, 
				configurarNegativeButton(), android.R.string.no);
	}

	private android.content.DialogInterface.OnClickListener configurarNegativeButton() {
		return new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};
	}

	private android.content.DialogInterface.OnClickListener configurarPositiveButton() {
		return new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					ComponentBoxUtil.verificaConexao(getApplicationContext());
					Foto f = (Foto) TransferParse.getInstance().remove(foto.getObjectId());
					System.out.println(f);
					ToastSliding toast = new ToastSliding(DetalheMolduraActivity.this);
					toast.show(ToastSliding.FOTO_MESSAGE, 
							bitmapImagem, 
							R.string.msg_descricao_detalhe_moldura_excluindo);
					imagem.setImageResource(android.R.color.transparent);
					ellipze.setVisibility(View.VISIBLE);
					foto.deleteEventually(configurarDeleteCallback(toast));
				} catch (Exception e) {
					new com.componente.box.toast.ToastSliding(DetalheMolduraActivity.this).show(com.componente.box.toast.ToastSliding.INFO_MESSAGE, 
							getResources().getString(R.string.msg_sem_internet), 
							com.componente.box.toast.ToastSliding.SLOW_MESSAGE);
				}
			}

			private DeleteCallback configurarDeleteCallback(final ToastSliding toast) {
				return new DeleteCallback() {
					
					@Override
					public void done(ParseException exception) {
						if(exception == null){
							toast.alterarMensagem(R.string.msg_descricao_detalhe_moldura_excluir_sucesso);
						}else{
							toast.alterarMensagem(R.string.msg_descricao_detalhe_moldura_erro_ecluir, R.drawable.ic_delete);
						}
						toast.removerToast(ToastSliding.SLOW_MESSAGE);
					}
				};
			}
		};
	}
}
