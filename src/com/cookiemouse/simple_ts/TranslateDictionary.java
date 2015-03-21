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

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class TranslateDictionary extends Thread{

	private Handler handler = null;
	private Bundle bundle = null;
	private String str = null;

	public TranslateDictionary(Handler handler, Bundle bundle, String str) {
		this.handler = handler;
		this.bundle = bundle;
		this.str = str;
	}

	@Override
	public void run() {
		try {
			
			URL url_dict = new URL("http://openapi.baidu.com/public/2.0/translate/dict/simple?client_id=GOr7jiTs5hiQvkHqDNg4KSTV&q=" + str + "&from=auto&to=auto");
			URLConnection connection = (URLConnection) url_dict.openConnection();
			connection.setRequestProperty("encoding", "UTF-8");
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