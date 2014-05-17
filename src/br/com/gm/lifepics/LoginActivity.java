package br.com.gm.lifepics;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import br.com.gm.lifepics.constants.Constants;
import br.com.gm.lifepics.model.Foto;
import br.com.gm.lifepics.model.Moldura;

import com.componente.box.localizacao.util.NavegacaoUtil;
import com.componente.box.localizacao.util.SessaoUtil;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFacebookUtils.Permissions;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class LoginActivity extends Activity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		init();
	}

	private void init() {
		PackageInfo info;
		try {
		    info = getPackageManager().getPackageInfo("br.com.gm.lifepics", PackageManager.GET_SIGNATURES);
		    for (Signature signature : info.signatures) {
		        MessageDigest md;
		        md = MessageDigest.getInstance("SHA");
		        md.update(signature.toByteArray());
		        String something = new String(Base64.encode(md.digest(), 0));
		        //String something = new String(Base64.encodeBytes(md.digest()));
		        Log.e("hash key", something);
		    }
		} catch (NameNotFoundException e1) {
		    Log.e("name not found", e1.toString());
		} catch (NoSuchAlgorithmException e) {
		    Log.e("no such an algorithm", e.toString());
		} catch (Exception e) {
		    Log.e("exception", e.toString());
		}
		initParse();
		if(ParseUser.getCurrentUser() != null){
			navegar();
		}
		verificarIdioma();
	}

	private void verificarIdioma() {
		SessaoUtil.adicionarValores(this, Constants.ESTILO, Constants.POLAROID);
	}

	public void onClickLoginFacebook(View view){
		findViewById(R.id.login_progress_facebook).setVisibility(View.VISIBLE);
		ParseFacebookUtils.logIn(
				Arrays.asList(Permissions.User.ABOUT_ME, Permissions.User.BIRTHDAY, Permissions.User.RELATIONSHIPS),
				this, callBackLoginFacebook());
	}

	private LogInCallback callBackLoginFacebook() {
		return new LogInCallback() {
			
			@Override
			public void done(ParseUser user, ParseException err) {
				findViewById(R.id.login_progress_facebook).setVisibility(View.GONE);
				if (user == null) {
					Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
			    } else if (user.isNew()) {
			    	Log.d("MyApp", "User signed up and logged in through Facebook!");
			    	navegar();
			    } else {
			    	Log.d("MyApp", "User logged in through Facebook!");
			    	navegar();
			    }
			}
		};
	}
	
	private void navegar() {
		NavegacaoUtil.navegar(LoginActivity.this, HomeActivity.class);
		finish();
	}

	private void initParse() {
		ParseObject.registerSubclass(Moldura.class);
		ParseObject.registerSubclass(Foto.class);
		Parse.initialize(this, Constants.PARSE_APP_ID, Constants.PARSE_CLIENT_KEY);
		ParseFacebookUtils.initialize(Constants.FACEBOOK_APP_ID);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

}
