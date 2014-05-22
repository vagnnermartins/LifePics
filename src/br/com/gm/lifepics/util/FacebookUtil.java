package br.com.gm.lifepics.util;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import br.com.gm.lifepics.R;

import com.componente.box.localizacao.util.ComponentBoxUtil;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;

public class FacebookUtil {
	
	public static void publicarNoMural(Activity activity, String mensagem, ParseFile imagem, int requestCode, Request.Callback callback){
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
				new CriarMarcaDAguaAsyncTask(activity.getResources(), imagem, mensagem, callback).execute();
		    }
		}
	}
	
	static class CriarMarcaDAguaAsyncTask extends AsyncTask<Void, Void, byte[]>{
		
		private ParseFile file;
		private Resources res;
		private String mensagem;
		private com.facebook.Request.Callback callback;

		public CriarMarcaDAguaAsyncTask(Resources res, ParseFile file, String mensagem, Request.Callback callback) {
			this.res = res;
			this.file = file;
			this.mensagem = mensagem;
			this.callback = callback;
		}
		
		@Override
		protected byte[] doInBackground(Void... params) {
			byte[] retorno = null;
			try {
				Bitmap imagem = ComponentBoxUtil.convertByteArrayToBitmap(file.getData());
				Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				options.inDither = false;
				Bitmap mark = BitmapFactory.decodeResource(res, R.drawable.logo, options);
				int width = (int) ((imagem.getWidth() * 26.14)) / 100;
				int height = (int) ((imagem.getHeight() * 7.84)) / 100;
				mark = Bitmap.createScaledBitmap(mark, width, height, false);
				Bitmap imagemCompartilhar = mark(imagem, mark);
				retorno = ComponentBoxUtil.convertBitmapToBytes(imagemCompartilhar);
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
        Paint paint = new Paint();
        canvas.drawBitmap(watermark, (w - watermark.getWidth()) - 5, (h - watermark.getHeight()) - 4, paint);
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
