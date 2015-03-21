package com.cookiemouse.simple_ts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class OneSentence extends Thread {
	
	Handler handler = null;
	Bundle bundle = null;
	
	public OneSentence(Handler handler, Bundle bundle)
	{
		this.handler = handler;
		this.bundle = bundle;
	}

	@Override
	public void run() {
		try {
			URL url_one = new URL("http://hello.api.235dns.com/api.php?code=json");
			URLConnection connection = (URLConnection) url_one.openConnection();
			InputStream is = connection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			StringBuilder sBuilder = new StringBuilder();
			while ((line = br.readLine() )!= null) {
				sBuilder.append(line);
			}
			
			//解析json
			JSONTokener jtk = new JSONTokener(sBuilder.toString());
			JSONObject jObject = (JSONObject) jtk.nextValue();

			br.close();
			isr.close();
			is.close();

			bundle.putString("wrods", jObject.getString("words"));
			
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
