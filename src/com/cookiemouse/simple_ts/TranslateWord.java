package com.cookiemouse.simple_ts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class TranslateWord extends Thread{

	private Handler handler = null;
	private Bundle bundle = null;
	private String str = null, fromString = "auto", toString="auto";

	public TranslateWord(Handler handler, Bundle bundle, String str, String fromString, String toString) {
		this.handler = handler;
		this.bundle = bundle;
		this.str = str;
		this.fromString = fromString;
		this.toString = toString;
	}

	@Override
	public void run() {
		try {
			String str_utf = URLEncoder.encode(str, "UTF-8");
			String str_url = "http://openapi.baidu.com/public/2.0/bmt/translate?client_id=GOr7jiTs5hiQvkHqDNg4KSTV&q="+ str_utf +"&from=" + fromString + "&to=" + toString;
			URL url_word = new URL(str_url);
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
			
			JSONArray jArray = jObject.getJSONArray("trans_result");
			Log.i("TAG", url_word.toString());
			Log.i("TAG", jObject.toString());
			
			JSONObject sub_jObject_1 = jArray.getJSONObject(0);
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
			bundle.putString("dst", "");
		}
	}
}