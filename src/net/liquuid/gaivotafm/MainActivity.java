package net.liquuid.gaivotafm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements OnClickListener {

	private static final String CATEGORIA = "livro";
	private PlayerMp3 player = new PlayerMp3();
	private StatusCheck stsCheck = new StatusCheck();
	private XmlHandler xmlHandler;
	private ImageButton btStart;
	private ToggleButton btStatus;
	private RadioGroup radiogroup;
	private WebView webView;
	private String mp3baixa = "http://stream.gaivota.fm.br:8000/1049.mp3";
	private String mp3alta = "http://stream.gaivota.fm.br:8000/1049-alta.mp3";
	private String mp3;
	private String RSSUrl = "http://gaivota.fm.br/blog/feed";
	private String html = "";
	boolean checkResult;
	private NotificationManager nm;
	private Notification n;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btStart = (ImageButton) findViewById(R.id.button1);
		btStatus = (ToggleButton) findViewById(R.id.toggleButton1);
		radiogroup = (RadioGroup) findViewById(R.id.radioGroup);

		radiogroup.check(R.id.altaQualidade);
		mp3 = mp3alta;

		if (savedInstanceState != null) {
			String status = savedInstanceState.getString("playerStatus");
			int rg = savedInstanceState.getInt("radioGroup");
			if (rg == 1) {
				mp3 = mp3alta;
			} else if (rg == 2) {
				mp3 = mp3baixa;
			}

			if (status == "playing") {
				btStart.setImageResource(R.drawable.pause64);
				player.start(mp3);
			} else {
				btStart.setImageResource(R.drawable.play64);
				player.pause();
			}
		}

		btStart.setOnClickListener(this);

		webView = (WebView) findViewById(R.id.webView1);
		
		statusCheck(mp3);
		updatePosts();
		notification(this, "Gaivota FM", "Gaivota FM", "Ubatuba 104.9", MainActivity.class);
	}

	@Override
	public void onClick(View view) {
		try {
			if (view == btStart) {
				if (player.isPlaying()) {
					btStart.setImageResource(R.drawable.play64);
					player.pause();
				} else {
					int rg = radiogroup.getCheckedRadioButtonId();
					if (rg == 1) {
						mp3 = mp3alta;
					} else if (rg == 2) {
						mp3 = mp3baixa;
					}
					statusCheck(mp3);
					player.start(mp3);
					btStart.setImageResource(R.drawable.pause64);
				}
			}
		} catch (Exception e) {
			Log.e(CATEGORIA, e.getMessage(), e);
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	public void onRadioButtonClicked(View view) {
		boolean checked = ((RadioButton) view).isChecked();

		switch (view.getId()) {
		case R.id.altaQualidade:
			if (checked) {
				mp3 = mp3alta;
				if (player.isPlaying())
					player.start(mp3);

			}
			break;
		case R.id.baixaQualidade:
			if (checked){
				mp3 = mp3baixa;
				if (player.isPlaying())
					player.start(mp3);
			}
			break;
		}
	}
    //@Override
	private void updatePosts(){
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
			WebView webView = (WebView) findViewById(R.id.webView1);
			@Override
			protected void onPreExecute(){
				webView.loadUrl("file:///android_asset/loading_rss.html");
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				xmlHandler = new XmlHandler(RSSUrl);
				html = xmlHandler.fetchXML();
				return null;
			}
			@Override
			protected void onPostExecute(Void result){
				
				webView.loadData(html, "text/html;charset=utf-8", "utf-8");				
			}
			
		};
		task.execute();
		
	}
	
	private void statusCheck(final String mp3){
		
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
			ToggleButton btStatus = (ToggleButton) findViewById(R.id.toggleButton1);			
			
			@Override
			protected Void doInBackground(Void... params) {
				if (stsCheck.check(mp3)){
					checkResult = true;
				} 
				return null;
			}
			@Override
			protected void onPostExecute(Void result){
				btStatus.setChecked(checkResult);
				if(!checkResult)
					Toast.makeText(MainActivity.this, "Servidor de Stream fora do ar", Toast.LENGTH_LONG).show();
			}
		};
		task.execute();
		
	}
	
	protected void notification(Context context, CharSequence msgStatusBar,
			CharSequence title, CharSequence message, Class<?> activity){
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		n = new Notification(R.drawable.play32, msgStatusBar, System.currentTimeMillis());
		n.flags = Notification.FLAG_NO_CLEAR;
	
		PendingIntent p = PendingIntent.getActivity(this, 0, new Intent(this, activity), 0);
		n.setLatestEventInfo(this, title, message, p);
		nm.notify(R.drawable.play32, n);
		
		
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("playerStatus", player.status());
		outState.putInt("radioGroup", radiogroup.getCheckedRadioButtonId());
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		nm.cancelAll();
		player.fechar();
	}

}
