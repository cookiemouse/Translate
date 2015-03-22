package com.cookiemouse.simple_ts;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static TextView tv_content, tv_ph_en, tv_ph_am, tv_word, tv_word_show, tv_sentence;
	private static Button translate, cancle;
	private EditText contentText;
	private ImageView image;
	private RelativeLayout rl;
	private LinearLayout ll;
	
	Bundle bundle = new Bundle();

	static public Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle bundle_get = new Bundle();
			bundle_get = msg.getData();
			tv_word.setText(bundle_get.getString("word_name"));
			if (bundle_get.getString("ph_en") != null)
			{
				tv_ph_en.setText("\t[英] " + bundle_get.getString("ph_en"));
				tv_ph_am.setText("\t[美]" + bundle_get.getString("ph_am"));
			}
			tv_word_show.setText(bundle_get.getString("dst"));
			tv_content.setText(bundle_get.getString("content"));
			tv_sentence.setText(bundle_get.getString("wrods"));
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		Initial();
		
		//animation
		final TranslateAnimation ta = new TranslateAnimation(-20, 20, 0, 0);
		ta.setDuration(3000);
		ta.setRepeatCount(1000);
		ta.setRepeatMode(2);
		tv_sentence.setAnimation(ta);
		ta.startNow();
		
		//one sentence
		OneSentence  oSentence = new OneSentence(handler, bundle);
		oSentence.start();
		
		translate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String str = contentText.getText().toString();
				
				TranslateDictionary td = new TranslateDictionary(handler, bundle, str);
				td.start();
				
				TranslateWord tw = new TranslateWord(handler, bundle, str);
				tw.start();
				
				HideKeyboard();
			}
		});
		
		cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				image.setVisibility(View.VISIBLE);
				HideKeyboard();
				TranslateAnimation tt = new TranslateAnimation(0, 0, -150, 0);
				tt.setDuration(200);
				tt.setRepeatMode(1);
				ll.startAnimation(tt);
				rl.setVisibility(View.GONE);
			}
		});
		
		
		//好像没触发
		contentText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (contentText.hasFocus() == false)
				{
					HideKeyboard();
				}else{
					//
				}
			}
		});
		
		contentText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				rl.setVisibility(View.VISIBLE);
				image.setVisibility(View.GONE);
				TranslateAnimation tt_2 = new TranslateAnimation(0, 0, 120, 0);
				tt_2.setDuration(200);
				tt_2.setRepeatMode(1);
				ll.startAnimation(tt_2);
			}
		});
		
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	//隐藏Keyborad方法
	public void HideKeyboard()
	{
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(contentText.getWindowToken(), 0);
	}
	
	public void Initial() {
		tv_content = (TextView)findViewById(R.id.tx_show);
		tv_ph_am = (TextView)findViewById(R.id.tv_ph_am);
		tv_ph_en = (TextView)findViewById(R.id.tv_ph_en);
		tv_word = (TextView)findViewById(R.id.tv_word);
		tv_word_show = (TextView)findViewById(R.id.tx_word_show);
		tv_sentence = (TextView)findViewById(R.id.sentence_show);
		translate = (Button)findViewById(R.id.translate);
		cancle = (Button)findViewById(R.id.cancel);
		image = (ImageView)findViewById(R.id.image);
		rl = (RelativeLayout)findViewById(R.id.two_button_layout);
		ll = (LinearLayout)findViewById(R.id.head);
		contentText = (EditText)findViewById(R.id.content);
	}
	
}
