package net.liquuid.gaivotafm;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import android.view.View;

public class PlayerMp3 implements OnCompletionListener{
	
	private static final String CATEGORIA = "gaivota";
	private static final int NOVO = 0;
	private static final int INICIADO = 1;
	private static final int PAUSADO = 2;
	private static final int PARADO = 3;
	
	private int status = NOVO;
	private MediaPlayer player;
	private String mp3;
	
	public boolean isPlaying(){
		if (status == INICIADO){
			return true;
		} else {
			return false;
		}
	}
	public String status(){
		return player.isPlaying() ? "playing" : "stoped";
	}
	public PlayerMp3(){
		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setOnCompletionListener(this);
	}
	
	public void start(String mp3,final ProgressDialog ringProgressDialog){
		this.mp3 = mp3;
		try{
			Log.i(CATEGORIA, Integer.toString(status));
			switch (status){
				case INICIADO:
					player.reset();
					player.setDataSource(mp3);
					player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
						  public void onPrepared(MediaPlayer mp) {
						      mp.start();
						      if(ringProgressDialog != null){
						    	  ringProgressDialog.dismiss();
						      }
						  }
						});
					player.prepareAsync();
					break;
				case PARADO:
					player.reset();
					break;
				case NOVO:
					player.setDataSource(mp3);
					player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
						  public void onPrepared(MediaPlayer mp) {
						      mp.start();
						      if(ringProgressDialog != null){
						    	  ringProgressDialog.dismiss();
						      }
						  }
						});
					player.prepareAsync();
					break;
				case PAUSADO:
					ringProgressDialog.dismiss();
					if (!player.isPlaying()){
						player.start();
					}
					else{
						player.setDataSource(mp3);
						player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
							  public void onPrepared(MediaPlayer mp) {
							      mp.start();
							      if(ringProgressDialog != null){
							    	  ringProgressDialog.dismiss();
							      }
							  }
							});
						player.prepareAsync();
					}
				break;
			}
			status = INICIADO;
		} catch (Exception e) {
			Log.e(CATEGORIA, e.getMessage(),e);
		}
	}
	public void pause(){
		player.pause();
		status = PAUSADO;
	}
	public void stop(){
		player.stop();
		status = PARADO;
	}
	public void fechar(){
		stop();
		player.release();
		player = null;
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.i(CATEGORIA, "Fim da música: " + mp3);
	}

}
