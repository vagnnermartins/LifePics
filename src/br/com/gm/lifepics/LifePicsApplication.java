package br.com.gm.lifepics;

import android.app.Application;
import br.com.gm.lifepics.constants.Constants;
import br.com.gm.lifepics.model.Foto;
import br.com.gm.lifepics.model.Moldura;

import com.parse.Parse;
import com.parse.ParseObject;

public class LifePicsApplication extends Application{
	
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
}
