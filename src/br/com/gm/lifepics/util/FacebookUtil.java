package br.com.gm.lifepics.util;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

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
				publicarNoFacebook(imagem, mensagem, callback);
		    }
		}
	}
	
	private static void publicarNoFacebook(ParseFile file, String mensagem, Request.Callback callback) {
		try {
			Bundle params = new Bundle();
				params.putByteArray("source", file.getData());
			params.putString("message", "\"" + mensagem + "\" " + "http://goo.gl/GgII7K");
			new Request(
			    ParseFacebookUtils.getSession(),
			    "/me/photos",
			    params,
			    HttpMethod.POST,
			    callback).executeAsync();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private static StatusCallback configurarPermissionCallback() {
		return new StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
			}
		};
	}
	
}
