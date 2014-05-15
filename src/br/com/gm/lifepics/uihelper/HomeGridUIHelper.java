package br.com.gm.lifepics.uihelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

public class HomeGridUIHelper {
	private Activity activity;
	private LinearLayout view;
	private OnClickListener onClickListener;
	
	public HomeGridUIHelper(Activity activity, OnClickListener onClickListener) {
		this.activity = activity;
		this.view = new LinearLayout(activity);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(params);
		view.setOrientation(LinearLayout.VERTICAL);
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
		View linha = null;
		List<Moldura> list = new ArrayList<Moldura>(result.values());
		for (int i = 0; i < list.size(); i++) {
			Moldura current = list.get(i);
			if(i % 3 == 0){
				linha = activity.getLayoutInflater().inflate(R.layout.item_colecao_card_grid, null);
				linha.findViewById(R.id.item_colecao_card_conteudo1).setVisibility(View.VISIBLE);
				linha.findViewById(R.id.item_colecao_card_conteudo1).setOnClickListener(onClickListener);
				linha.findViewById(R.id.item_colecao_card_conteudo1).setTag(current.getObjectId());
				view.addView(linha);
				montarItemCard(current, linha,
						R.id.item_colecao_card_grid_polaroid1, 
						R.id.item_colecao_card_grid_imagem1,
						R.id.item_colecao_card_grid_titulo1);
			}else if(i % 3 == 1){
				linha.findViewById(R.id.item_colecao_card_conteudo2).setVisibility(View.VISIBLE);
				linha.findViewById(R.id.item_colecao_card_conteudo2).setOnClickListener(onClickListener);
				linha.findViewById(R.id.item_colecao_card_conteudo2).setTag(current.getObjectId());
				montarItemCard(current, linha,
						R.id.item_colecao_card_grid_polaroid2, 
						R.id.item_colecao_card_grid_imagem2,
						R.id.item_colecao_card_grid_titulo2);
			}else if (i % 3 == 2){
				linha.findViewById(R.id.item_colecao_card_conteudo3).setVisibility(View.VISIBLE);
				linha.findViewById(R.id.item_colecao_card_conteudo3).setOnClickListener(onClickListener);
				linha.findViewById(R.id.item_colecao_card_conteudo3).setTag(current.getObjectId());
				montarItemCard(current, linha,
						R.id.item_colecao_card_grid_polaroid3, 
						R.id.item_colecao_card_grid_imagem3,
						R.id.item_colecao_card_grid_titulo3);
			}
		}
	}
	
	private void montarItemCard(Moldura current, View linha, int polaroidId, int imagemId, int tituloId) {
		final ImageView polaroid;
		final ImageView imagem;
		TextView titulo;
		polaroid = (ImageView) linha.findViewById(polaroidId);
		imagem = (ImageView) linha.findViewById(imagemId);
		imagem.setTag(current.getObjectId() + Constants.GRID);
		titulo = (TextView) linha.findViewById(tituloId);
		titulo.setText(current.getTitulo());
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
			}
		});
	}

	public View getView() {
		return view;
	}
}
