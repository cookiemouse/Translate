package com.cookiemouse.simple_ts;

import java.io.IOException;
import java.net.URL;

import android.media.AudioManager;
import android.media.MediaPlayer;

public class TTS extends Thread {

	private MediaPlayer mPlayer;
	URL url_word = null;
	String contry_str  = "zh", text_str = "";
	
	public TTS(String contry_str, String text_str){
		this.contry_str = contry_str;
		this.text_str = text_str;
	}
	
	@Override
	public void run() {
		
		String str_url = "http://tts.baidu.com/text2audio?lan=" + contry_str + "&ie=UTF-8&pid=101&text=" + text_str + "&spd=1";
		try {
			url_word = new URL(str_url);
			
			mPlayer = new MediaPlayer();
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mPlayer.reset();
			mPlayer.setDataSource(url_word.toString());
			mPlayer.prepare();
			mPlayer.start();
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
