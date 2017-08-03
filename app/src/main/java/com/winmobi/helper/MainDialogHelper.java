package com.winmobi.helper;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import com.winmobi.R;


public class MainDialogHelper {

	/**
	 * 显示正在同步对话框
	 */
	public static Dialog showSyncDialog(Context context, Dialog dialog, View view, String msg, DialogInterface.OnKeyListener listener) {
		if (dialog == null) {

			dialog = new Dialog(context, R.style.new_circle_progress);
			dialog.setContentView(view);
			dialog.setOnKeyListener(listener);
			dialog.setCanceledOnTouchOutside(false);
//			dialog.show();
		} else {
//			dialog.show();
		}
		return dialog;
	}

}
