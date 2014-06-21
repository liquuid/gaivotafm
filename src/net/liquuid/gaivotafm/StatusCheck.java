package net.liquuid.gaivotafm;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.widget.ToggleButton;

public class StatusCheck {
	public volatile boolean checkingComplete = false;

	public void check(final String urlString,final ToggleButton btStatus) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					URL url = new URL(urlString);
					HttpURLConnection urlConnection = (HttpURLConnection) url
							.openConnection();
					urlConnection.setReadTimeout(10000 /* milliseconds */);
					urlConnection.setConnectTimeout(15000 /* milliseconds */);
					Integer responseCode = urlConnection.getResponseCode();
					urlConnection.disconnect();
					checkingComplete = true;
					if(responseCode == 200){
						btStatus.setChecked(true);
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		});
		thread.start();
	}
}