package com.cookiemouse.simple_ts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static TextView tv_content, tv_ph_en, tv_ph_am, tv_word, tv_word_show;
	private static Button translate;
	private EditText contentText;
	Bundle bundle = new Bundle();

	static public Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			System.out.print(msg.obj);
			Bundle bundle_get = new Bundle();
			bundle_get = msg.getData();
			tv_word.setText(bundle_get.getString("word_name"));
			tv_ph_en.setText("\t[英] " + bundle_get.getString("ph_en"));
			tv_ph_am.setText("\t[美]" + bundle_get.getString("ph_am"));
			tv_word_show.setText(bundle_get.getString("dst"));
			tv_content.setText(bundle_get.getString("content"));
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tv_content = (TextView)findViewById(R.id.tx_show);
		tv_ph_am = (TextView)findViewById(R.id.tv_ph_am);
		tv_ph_en = (TextView)findViewById(R.id.tv_ph_en);
		tv_word = (TextView)findViewById(R.id.tv_word);
		tv_word_show = (TextView)findViewById(R.id.tx_word_show);
		translate = (Button)findViewById(R.id.translate);
		contentText = (EditText)findViewById(R.id.content);

		translate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String str = contentText.getText().toString();
				TranslateDictionary td = new TranslateDictionary(str);
				td.start();
				TranslateWord tw = new TranslateWord(str);
				tw.start();
			}
		});

	}

	class TranslateDictionary extends Thread{

		private String str = null;

		public TranslateDictionary(String str) {
			this.str = str;
		}

		public String getString()
		{
			return str;
		}

		@Override
		public void run() {
			try {

				URL url_dict = new URL("http://openapi.baidu.com/public/2.0/translate/dict/simple?client_id=GOr7jiTs5hiQvkHqDNg4KSTV&q=" + str + "&from=en&to=zh");
				URLConnection connection = (URLConnection) url_dict.openConnection();
				InputStream is = connection.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				StringBuilder sBuilder = new StringBuilder();
				while ((line = br.readLine() )!= null) {
					sBuilder.append(line);
				}

				/**
				 * 词典解析
				 * */

				JSONTokener jtk = new JSONTokener(sBuilder.toString());
				JSONObject jObject = (JSONObject) jtk.nextValue();

				if ( jObject.getString("errno").equals("0")) 
				{
					//获取from,to,error
					bundle.putString("from", jObject.getString("from"));
					bundle.putString("to", jObject.getString("to"));
					JSONObject dataObject_1 = jObject.getJSONObject("data");
					//获取data里的word_name
					bundle.putString("word_name",
							dataObject_1.getString("word_name"));
					JSONArray jArray_1 = dataObject_1.getJSONArray("symbols");
					for (int i = 0; i < jArray_1.length(); i++) {

						JSONObject dataObject_2 = jArray_1.getJSONObject(i);

						//获取symbols里的ph_am,ph_en
						bundle.putString("ph_am",
								dataObject_2.getString("ph_am"));
						bundle.putString("ph_en",
								dataObject_2.getString("ph_en"));
						StringBuilder sb = new StringBuilder();

						JSONArray jArray_2 = dataObject_2.getJSONArray("parts");
						for (int j = 0; j < jArray_2.length(); j++) {

							JSONObject dataObject_3 = jArray_2.getJSONObject(j);

							//获取parts里的part
							bundle.putString("part_" + j,
									dataObject_3.getString("part"));

							sb.append(dataObject_3.getString("part"));

							JSONArray jArray_3 = dataObject_3
									.getJSONArray("means");
							for (int k = 0; k < jArray_3.length(); k++) {
								//获取means里的一个值
								bundle.putString("content", jArray_3.getString(k));
								sb.append(" " + jArray_3.getString(k) + ";");
							}
							sb.append("\n");
						}
						bundle.putString("content", sb.toString());
					}
				}
				else {
					bundle.putString("content", "输入为空");
				}
				br.close();
				isr.close();
				is.close();

				Message message = new Message();
				message.setData(bundle);
				handler.sendMessage(message);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
				bundle.putString("content", "您所找的单词不存在!");
			}
		}
	}

	class TranslateWord extends Thread{

		private String str = null;

		public TranslateWord(String str) {
			this.str = str;
		}

		public String getString()
		{
			return str;
		}

		@Override
		public void run() {
			try {

				URL url_word = new URL("http://openapi.baidu.com/public/2.0/bmt/translate?client_id=GOr7jiTs5hiQvkHqDNg4KSTV&q="+str+"&from=auto&to=auto");
				URLConnection connection = (URLConnection) url_word.openConnection();
				InputStream is = connection.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				StringBuilder sBuilder = new StringBuilder();
				while ((line = br.readLine() )!= null) {
					sBuilder.append(line);
				}

				/**
				 * 单词解析
				 * */

				JSONTokener jtk = new JSONTokener(sBuilder.toString());
				JSONObject jObject = (JSONObject) jtk.nextValue();

				bundle.putString("from", jObject.getString("from"));
				bundle.putString("to", jObject.getString("to"));

				JSONArray jArray = jObject.getJSONArray("trans_result");

				JSONObject sub_jObject_1 = jArray.getJSONObject(0);
				bundle.putString("src", sub_jObject_1.getString("src"));
				bundle.putString("dst", sub_jObject_1.getString("dst"));

				br.close();
				isr.close();
				is.close();

				Message message = new Message();
				message.setData(bundle);
				handler.sendMessage(message);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
