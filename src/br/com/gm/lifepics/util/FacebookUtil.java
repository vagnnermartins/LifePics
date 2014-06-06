package br.com.gm.lifepics.util;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import br.com.gm.lifepics.R;

import com.componente.box.localizacao.util.ComponentBoxUtil;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;

public class FacebookUtil {
	
	public static void publicarNoMural(Activity activity, String mensagem, ParseFile imagem, boolean shareComPolaroid,int requestCode, Request.Callback callback){
		if (ParseFacebookUtils.getSession() != null && ParseFacebookUtils.getSession().isOpened()) {
		    List<String> permissions = ParseFacebookUtils.getSession().getPermissions();
		    if (!permissions.contains("publish_actions")) {
		        Session.NewPermissionsRequest newPermissionsRequest =
		                new Session.NewPermissionsRequest(activity, Arrays.asList("publish_actions"))
		                        .setDefaultAudience(SessionDefaultAudience.FRIENDS)
		                        .setCallback(configurarPermissionCallback())
		                        .setRequestCode(requestCode);
		        ParseFacebookUtils.getSession().requestNewPublishPermissions(newPermissionsRequest);
		    } else {
				new CriarMarcaDAguaAsyncTask(activity, imagem, mensagem, shareComPolaroid, callback).execute();
		    }
		}
	}
	
	static class CriarMarcaDAguaAsyncTask extends AsyncTask<Void, Void, byte[]>{
		
		private ParseFile file;
		private Activity activity;
		private String mensagem;
		private com.facebook.Request.Callback callback;
		private boolean shareComPolaroid;

		public CriarMarcaDAguaAsyncTask(Activity activity, ParseFile file, String mensagem, boolean shareComPolaroid, Request.Callback callback) {
			this.activity = activity;
			this.file = file;
			this.mensagem = mensagem;
			this.callback = callback;
			this.shareComPolaroid = shareComPolaroid;
		}
		
		@Override
		protected byte[] doInBackground(Void... params) {
			byte[] retorno = null;
//			try {
				retorno = compartilharComPolaroid(activity, file, callback);
//				Bitmap imagem = ComponentBoxUtil.convertByteArrayToBitmap(file.getData());
//				Options options = new BitmapFactory.Options();
//				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//				options.inDither = false;
//				Bitmap mark = BitmapFactory.decodeResource(res, R.drawable.logo, options);
//				int width = (int) ((imagem.getWidth() * 26.14)) / 100;
//				int height = (int) ((imagem.getHeight() * 7.84)) / 100;
//				mark = Bitmap.createScaledBitmap(mark, width, height, false);
//				Bitmap imagemCompartilhar = mark(imagem, mark);
//				retorno = ComponentBoxUtil.convertBitmapToBytes(imagemCompartilhar);
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
			return retorno;
		}
		
		private byte[] compartilharComPolaroid(Activity activity, ParseFile file, Callback callback){
			byte[] retorno = null;
			try {
				Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				options.inDither = false;
				Bitmap polaroid = BitmapFactory.decodeResource(activity.getResources(), R.drawable.polaroid_sem_borda, options);
				Bitmap imagem = ComponentBoxUtil.convertByteArrayToBitmap(file.getData());
				int width = polaroid.getWidth();
				int height = polaroid.getHeight();
				int marginLeft = (int) ((width * 0.580) / 10);
				int marginRigth = (int) ((width * 0.516) / 10);
				int marginTop = (int) ((height * 0.455) / 10);
				int marginBottom = (int) ((height * 21.17) / 100);
				Bitmap src = Bitmap.createScaledBitmap(polaroid, imagem.getWidth() + marginRigth + marginLeft, 
						imagem.getHeight() + marginTop + marginBottom, false);
				width = src.getWidth();
				height = src.getHeight();
				marginLeft = (int) ((width * 0.580) / 10);
				marginRigth = (int) ((width * 0.516) / 10);
				marginTop = (int) ((height * 0.455) / 10);
				marginBottom = (int) ((height * 21.17) / 100);
				imagem = Bitmap.createScaledBitmap(imagem, src.getWidth() - marginRigth - marginLeft, 
						src.getHeight() - marginTop - marginBottom, false);
				Bitmap mark = BitmapFactory.decodeResource(activity.getResources(), R.drawable.logo, options);
				int widthMark = (int) ((imagem.getWidth() * 26.14)) / 100;
				int heightMark = (int) ((imagem.getHeight() * 7.84)) / 100;
				mark = Bitmap.createScaledBitmap(mark, widthMark, heightMark, false);
				Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
				Canvas canvas = new Canvas(result);
				canvas.drawBitmap(imagem, marginLeft, marginTop, null);
				canvas.drawBitmap(src, 0, 0, new Paint());
				canvas.save();
				
				TextPaint textPaint = new TextPaint();
				textPaint.setTextSize(27);
				textPaint.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Noteworthy.ttf"));
				StaticLayout mTextLayout = new StaticLayout("Aquela com o meu amor Aquela com o meu amor", 
						textPaint, imagem.getWidth(), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
				int heightLegenda = (int) ((polaroid.getHeight() * 21.17) / 100);
//				heightLegenda /= 2;
//				heightLegenda = heightLegenda - (mTextLayout.getHeight() / 2);
//				heightLegenda = heightLegenda + (imagem.getHeight());
				heightLegenda -= mTextLayout.getHeight();
				heightLegenda = (heightLegenda / 2) + imagem.getHeight() + (marginTop / 2);
				canvas.translate(marginLeft, heightLegenda);
				mTextLayout.draw(canvas);
				canvas.restore();
				
				
				float marginLeftMark = (float) ((width * 7.09) / 100);
				float marginTopMark = (float) (height * 22.02) / 100;
				canvas.drawBitmap(mark, width - marginLeftMark - mark.getWidth(), height - marginTopMark - mark.getHeight(), null);
				retorno = ComponentBoxUtil.convertBitmapToBytes(result);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return retorno;
		}
		
		@Override
		protected void onPostExecute(byte[] result) {
			super.onPostExecute(result);
			publicarNoFacebook(result, mensagem, callback);
		}
	}
	
	private static Bitmap mark(Bitmap src, Bitmap watermark) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        canvas.drawBitmap(watermark, (w - watermark.getWidth()) - 5, (h - watermark.getHeight()) - 4, new Paint());
        return result;
    }
	
	private static void publicarNoFacebook(byte[] file, String mensagem, Request.Callback callback) {
		Bundle params = new Bundle();
			params.putByteArray("source", file);
		params.putString("message", mensagem + " http://goo.gl/GgII7K");
		new Request(
		    ParseFacebookUtils.getSession(),
		    "/me/photos",
		    params,
		    HttpMethod.POST,
		    callback).executeAsync();
	}
	
	private static StatusCallback configurarPermissionCallback() {
		return new StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
			}
		};
	}
	
}
