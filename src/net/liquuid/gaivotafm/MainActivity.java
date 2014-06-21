package net.liquuid.gaivotafm;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements OnClickListener {

	private static final String CATEGORIA = "livro";
	private PlayerMp3 player = new PlayerMp3();
	private ImageButton btStart;
	private ToggleButton btStatus;
	private WebView webView;
	private String mp3 = "http://stream.gaivota.fm.br:8000/1049.mp3";
	private String RSSUrl = "http://gaivota.fm.br/blog/feed";
	private XmlHandler obj;
	private StatusCheck stsCheck = new StatusCheck();
	private String html = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btStart = (ImageButton) findViewById(R.id.button1);
		btStatus = (ToggleButton) findViewById(R.id.toggleButton1);
		stsCheck.check(mp3, btStatus);

		if (savedInstanceState != null) {
			String status = savedInstanceState.getString("playerStatus");
			if (status == "playing") {
				btStart.setImageResource(R.drawable.pause64); //setText("Pause");
				player.start(mp3);
			} else {
				btStart.setImageResource(R.drawable.play64);
				player.pause();
			}
		}
		obj = new XmlHandler(RSSUrl);
		obj.fetchXML();
		obj.getPosts();

		while (obj.parsingComplete);
		System.out.println(obj.getPosts());
		//html = obj.getPosts();
		html = "<html><head><style type=\"text/css\">hr { clear: both; float: none;" +
				" width: 100%; height: 1px; margin: 1.0em 0; border: none; " +
				"background: #ddd; background-image: -webkit-gradient( linear, " +
				"left bottom, right bottom,color-stop(0, rgb(255,255,255)),color-stop" +
				"(0.1, rgb(221,221,221)),color-stop(0.9, rgb(221,221,221)),color-stop" +
				"(1, rgb(255,255,255)));}</style></head><body>" +
				"<a href=\"http://gaivota.fm.br/blog/novidades-no-ar\">Novidades no ar</a>" +
				"<br><hr> <a href=\"http://gaivota.fm.br/blog/recado-de-um-ouvinte\">" +
				"Recado de um ouvinte</a><br><hr> <a href=\"http://gaivota.fm.br/blog\">" +
				"Blogs Gaivota FM</a><br><hr> <a href=\"http://gaivota.fm.br/blog/amplific" +
				"ador-marshall-valvstate-vs65r\">Amplificador Marshall Valvstate VS65R</a>" +
				"<br><hr> <a href=\"http://gaivota.fm.br/blog/amplificador-fender-frontman-25r-1\">" +
				"Amplificador Fender Frontman 25R</a><br><hr> <a href=\"http://gaivota.fm.br/b" +
				"log/pedal-boss-ps-3\">Pedal Boss PS-3</a><br><hr> <a href=\"http://gaivota.fm" +
				".br/blog/pronatec-curso-operador-de-udio\">Pronatec - Curso Operador de &Aacute;udio" +
				"</a><br><hr> <a href=\"http://gaivota.fm.br/blog/reformula-o-do-portal-gaivota-fm\">" +
				"Reformula&ccedil;&atilde;o do portal Gaivota FM</a><br><hr> </body></html>";

		
		btStart.setOnClickListener(this);

		webView = (WebView) findViewById(R.id.webView1);
		
		webView.loadData(html, "text/html", "utf-8");
		// webView.loadUrl("http://gaivota.fm.br/blog/feed");

	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.main, menu); return true; }
	 */
 
	@Override
	public void onClick(View view) {
		try {
			if (view == btStart) {
				if (player.isPlaying()) {
					btStart.setImageResource(R.drawable.play64);
					player.pause();
				} else {
					player.start(mp3);
					btStart.setImageResource(R.drawable.pause64);
				}
			}
		} catch (Exception e) {
			Log.e(CATEGORIA, e.getMessage(), e);
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("playerStatus", player.status());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		player.fechar();
	}

	

}
