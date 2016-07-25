package com.gime.locationsb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SelectTextActivity extends Activity {

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_sel_text);

	}
	
	public void selectText1(View v)
	{
        Intent intent = new Intent();
        intent.putExtra("text", getResources().getString(R.string.select_text_1));
        setResult(RESULT_OK, intent);
		finish();
	}
	
	
	public void selectText2(View v)
	{
        Intent intent = new Intent();
        intent.putExtra("text", getResources().getString(R.string.select_text_2));
        setResult(RESULT_OK, intent);
		finish();
	}
	
	
	public void selectText3(View v)
	{
        Intent intent = new Intent();
        intent.putExtra("text", getResources().getString(R.string.select_text_3));
        setResult(RESULT_OK, intent);
		finish();
	}
	
	
	public void selectText4(View v)
	{
        Intent intent = new Intent();
        intent.putExtra("text", "");
        setResult(RESULT_OK, intent);
		finish();
	}
	
	
}
