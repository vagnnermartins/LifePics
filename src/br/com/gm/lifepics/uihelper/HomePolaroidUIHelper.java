package br.com.gm.lifepics.uihelper;

import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.gm.lifepics.R;
import br.com.gm.lifepics.constants.Constants;
import br.com.gm.lifepics.model.Moldura;

public class HomePolaroidUIHelper {
	
	private Activity activity;
	private LinearLayout view;
	private OnClickListener onClickListener;
	
	public HomePolaroidUIHelper(Activity activity, OnClickListener onClickListener) {
		this.activity = activity;
		this.view = new LinearLayout(activity);
		view.setOrientation(LinearLayout.VERTICAL);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		view.setLayoutParams(params);
		this.onClickListener = onClickListener;
	}
	
	public boolean contemConteudo(){
		if(view.getChildCount() > 0){
			return true;
		}else{
			return false;
		}
	}
	
	public void configurarMolduras(Map<String, Moldura> result) {
		view.removeAllViews();
		View item = null;
		for (Entry<String, Moldura> moldura : result.entrySet()) {
			item = activity.getLayoutInflater().inflate(R.layout.item_colecao_card_polaroid, null);
			item.setOnClickListener(onClickListener);
			item.setTag(moldura.getValue().getObjectId());
			view.addView(item);
			montarItemCard(moldura.getValue(), item,
					R.id.item_colecao_card_polaroid_polaroid1, 
					R.id.item_colecao_card_polaroid_imagem1,
					R.id.item_colecao_card_polaroid_titulo1);
		}
	}
	
	private void montarItemCard(Moldura current, View item, int polaroidId, int imagemId, int tituloId) {
		final ImageView polaroid;
		final ImageView imagem;
		item.findViewById(R.id.item_colecao_card_polaroid_ellipze).setTag(current.getObjectId() + "ellipze");
		TextView titulo;
		polaroid = (ImageView) item.findViewById(polaroidId);
		imagem = (ImageView) item.findViewById(imagemId);
		imagem.setTag(current.getObjectId() + Constants.POLAROID);
		titulo = (TextView) item.findViewById(tituloId);
		titulo.setText("\"" + current.getLegenda() + "\"" );
		imagem.post(new Runnable() {
			
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
			}
		});
	}

	public View getView() {
		return view;
	}
}
