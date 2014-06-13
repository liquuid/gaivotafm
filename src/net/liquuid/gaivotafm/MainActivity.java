package net.liquuid.gaivotafm;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private static final String CATEGORIA = "livro";
	private PlayerMp3 player = new PlayerMp3();
	private Button btStart;
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btStart = (Button) findViewById(R.id.button1);
		btStart.setOnClickListener(this);
		webView = (WebView) findViewById(R.id.webView1);
		String summary = "<html><body><a href=\"http://gaivota.fm.br/blog\">Confira</a></html>";
		webView.loadData(summary, "text/html", null);
	    //webView.loadUrl("http://gaivota.fm.br/blog/feed");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		try {
			if(view == btStart) {
				if (player.isPlaying()){
					btStart.setText("Play!");
					player.pause();
				} else {
					String mp3 = "http://stream.gaivota.fm.br:8000/1049.mp3";
					player.start(mp3);
					btStart.setText("Pause");
				}
			} 
		} catch (Exception e) {
			Log.e(CATEGORIA, e.getMessage(), e );
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			
		}
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		player.fechar();
	}

}
