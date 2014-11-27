/*
 * Miren Tanna - mat130830
 * Description: Activity to display the help text as a dialog using its corresponding layout.
*/
package com.example.drawtests;

import android.app.Activity;
import android.app.Dialog;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class HelpDialogActivity extends Dialog {

	public Activity c;

	public HelpDialogActivity(Activity a) {
		super(a);
		this.c = a;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.help);
	}

}
