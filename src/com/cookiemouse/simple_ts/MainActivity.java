package com.cookiemouse.simple_ts;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
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
import android.widget.Toast;

public class MainActivity extends Activity implements OnInitListener{

	private static TextView tv_content, tv_ph_en, tv_ph_am, tv_word, tv_word_show, tv_sentence;
	private static Button translate, cancle, button_from, button_to, from_play;
	private EditText contentText;
	private ImageView image;
	private RelativeLayout rl;
	private LinearLayout ll;
	private TextToSpeech tts;
	
	private String from = "auto", to = "auto";
	
	Bundle bundle = new Bundle();

	static public Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle bundle_get = new Bundle();
			bundle_get = msg.getData();
			
			if (!(bundle_get.getString("ph_en") == null || bundle_get.getString("ph_en").equals("")))
			{
				tv_word.setText(bundle_get.getString("word_name"));
				tv_ph_en.setText("\t[英] " + bundle_get.getString("ph_en"));
				tv_ph_am.setText("\t[美]" + bundle_get.getString("ph_am"));
			}
			if (!(bundle_get.getString("dst") == null || bundle_get.getString("dst").equals("")))
			{
				tv_word_show.setText(bundle_get.getString("dst"));
			}
			if (!(bundle_get.getString("content") == null || bundle_get.getString("content").equals("")))
			{
				tv_content.setText(bundle_get.getString("content"));
			}
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
		
		translate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv_word_show.setText("");
				tv_word.setText("");
				tv_content.setText("");
				tv_ph_en.setText("");
				tv_ph_am.setText("");
				
				String str = contentText.getText().toString().trim();
				
				if (str.equals(""))
				{
					bundle.putString("dst", "");
					bundle.putString("word_name", "");
					bundle.putString("ph_am", "");
					bundle.putString("ph_en", "");
					bundle.putString("content", "我并不能翻译您的意念 :)");
					
					Message message = new Message();
					message.setData(bundle);
					handler.sendMessage(message);
				}
				else {
					TranslateDictionary td = new TranslateDictionary(handler, bundle, str, from, to);
					td.start();

					TranslateWord tw = new TranslateWord(handler, bundle, str, from, to);
					tw.start();
				}
				
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
		
		button_from.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("LANGUAGE", from);
				intent.putExtra("LANGUAGE_TEXT", button_from.getText().toString());
				intent.setClass(MainActivity.this, ChoiceActivity.class);
				startActivityForResult(intent, 0);
			}
		});
		
		button_to.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("LANGUAGE", to);
				intent.putExtra("LANGUAGE_TEXT", button_to.getText().toString());
				intent.setClass(MainActivity.this, ChoiceActivity.class);
				startActivityForResult(intent, 1);
			}
		});
	
		//TTS
		from_play.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String textString = contentText.getText().toString();
				tts.speak(textString, TextToSpeech.QUEUE_FLUSH, null);
			}
		});
	}

	//回调方法，接收返回的Language
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 0:
		{
			//From
			from = data.getStringExtra("LANGUAGE");
			button_from.setText(data.getStringExtra("LANGUAGE_TEXT"));
			break;
		}
		case 1:
		{
			//To
			to = data.getStringExtra("LANGUAGE");
			button_to.setText(data.getStringExtra("LANGUAGE_TEXT"));
			break;
		}
		case 2:
		{
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
			{
				tts = new TextToSpeech(this, (OnInitListener) this);
			}else{
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS)
		{
			int result = tts.setLanguage(Locale.ENGLISH);
			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
			{
				Toast.makeText(MainActivity.this, "不支持这种语言", Toast.LENGTH_SHORT).show();
			}
		}
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
		button_from = (Button)findViewById(R.id.btn_from);
		button_to = (Button)findViewById(R.id.btn_to);
		from_play = (Button)findViewById(R.id.from_play);
		image = (ImageView)findViewById(R.id.image);
		rl = (RelativeLayout)findViewById(R.id.two_button_layout);
		ll = (LinearLayout)findViewById(R.id.head);
		contentText = (EditText)findViewById(R.id.content);

		//监测TTS
		Intent intent = new Intent();
		intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(intent, 2);
		
		//one sentence
		OneSentence  oSentence = new OneSentence(handler, bundle);
		oSentence.start();
	}
	
}
