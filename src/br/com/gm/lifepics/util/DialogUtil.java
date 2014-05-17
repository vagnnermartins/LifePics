package br.com.gm.lifepics.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtil {

	/**
	 * 
	 * @param context
	 * @param titulo Título do dialog
	 * @param mensagem Mensagem do dialog
	 * @param positiveButtonListener
	 * @param titlePositiveButton Título do positivo button
	 * @param negativeButtonListener Se negativoButton != null botão Negativo é configurado
	 * @param titleNegativeButton Título do negative button
	 */
	public static void show(Context context,  int titulo, int mensagem,
			DialogInterface.OnClickListener positiveButtonListener, int titlePositiveButton,
			DialogInterface.OnClickListener negativeButtonListener, int titleNegativeButton) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(titulo);
		builder.setMessage(mensagem);
		builder.setPositiveButton(titlePositiveButton, positiveButtonListener);
		if(negativeButtonListener != null)
			builder.setNegativeButton(titleNegativeButton, negativeButtonListener);
		builder.show();
	}

	public static void show(Context context,  int titulo, String mensagem,
			DialogInterface.OnClickListener positiveButtonListener, int titlePositiveButton,
			DialogInterface.OnClickListener negativeButtonListener, int titleNegativeButton) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(titulo);
		builder.setMessage(mensagem);
		builder.setPositiveButton(titlePositiveButton, positiveButtonListener);
		if(negativeButtonListener != null)
			builder.setNegativeButton(titleNegativeButton, negativeButtonListener);
		builder.show();
	}

}