package com.leaden1.volumediagnostic;

import android.app.Activity;
import android.content.*;
import android.media.AudioManager;
import android.media.session.*;
import android.os.*;
import android.text.method.ScrollingMovementMethod;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    TextView status, session, volume, key, last, history;
    AudioManager audio; MediaSessionManager media; int lastVol=-1; boolean receiver=false;
    Handler handler=new Handler(Looper.getMainLooper());
    Runnable poller=new Runnable(){public void run(){checkVolume("POLL"); updateSession(); handler.postDelayed(this,250);}};
    BroadcastReceiver volumeReceiver=new BroadcastReceiver(){public void onReceive(Context c,Intent i){add("BROADCAST: "+i.getAction()); checkVolume("BROADCAST");}};
    MediaSessionManager.OnActiveSessionsChangedListener sessionListener=list->updateSession();

    public void onCreate(Bundle b){super.onCreate(b); audio=(AudioManager)getSystemService(AUDIO_SERVICE); media=(MediaSessionManager)getSystemService(MEDIA_SESSION_SERVICE); buildUi(); lastVol=audio.getStreamVolume(AudioManager.STREAM_MUSIC); add("APP_INICIADA"); add("STREAM_MUSIC="+lastVol+"/"+audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)); add("MONITOR_VOLUMEN=ACTIVO"); updateVolume(); updateSession();}
    protected void onStart(){super.onStart(); register(); try{media.addOnActiveSessionsChangedListener(sessionListener,null);}catch(Exception e){add("MEDIA_SESSION_LISTENER: "+e.getClass().getSimpleName());} handler.post(poller); add("POLLING_VOLUMEN=250ms");}
    protected void onStop(){handler.removeCallbacks(poller); if(receiver){try{unregisterReceiver(volumeReceiver);}catch(Exception ignored){} receiver=false;} try{media.removeOnActiveSessionsChangedListener(sessionListener);}catch(Exception ignored){} super.onStop();}
    void register(){try{IntentFilter f=new IntentFilter();f.addAction("android.media.VOLUME_CHANGED_ACTION");f.addAction("android.media.STREAM_VOLUME_CHANGED_ACTION");registerReceiver(volumeReceiver,f);receiver=true;add("RECEPTOR_VOLUMEN=REGISTRADO");}catch(Exception e){add("RECEPTOR_VOLUMEN_ERROR="+e.getClass().getSimpleName());}}

    void buildUi(){LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.VERTICAL);r.setPadding(28,28,28,28); TextView t=make("LEADEN1 - VOLUME DIAGNOSTIC V5",22);t.setGravity(Gravity.CENTER);r.addView(t); status=make("● MONITOR ACTIVO",19);session=make("MEDIA SESSION: —",14);volume=make("STREAM_MUSIC: —",18);key=make("ÚLTIMO KEY EVENT: —",16);last=make("ÚLTIMO CAMBIO: —",16);r.addView(status);r.addView(session);r.addView(volume);r.addView(key);r.addView(last);Button refresh=new Button(this);refresh.setText("ACTUALIZAR DIAGNÓSTICO");refresh.setOnClickListener(v->{checkVolume("MANUAL");updateSession();add("ACTUALIZACION_MANUAL");});r.addView(refresh);Button clear=new Button(this);clear.setText("LIMPIAR HISTORIAL");clear.setOnClickListener(v->history.setText(""));r.addView(clear);r.addView(make("HISTORIAL DEL DIAGNÓSTICO",16));ScrollView s=new ScrollView(this);history=make("",13);history.setMovementMethod(new ScrollingMovementMethod());s.addView(history);r.addView(s,new LinearLayout.LayoutParams(-1,0,1));setContentView(r);}
    TextView make(String x,float z){TextView v=new TextView(this);v.setText(x);v.setTextSize(z);v.setPadding(0,5,0,5);return v;}

    void checkVolume(String source){int cur=audio.getStreamVolume(AudioManager.STREAM_MUSIC);if(lastVol<0){lastVol=cur;updateVolume();return;}if(cur!=lastVol){int old=lastVol;lastVol=cur;String d=cur>old?"VOLUME_UP":"VOLUME_DOWN";add("CAMBIO_VOLUMEN: "+old+" → "+cur+" | "+d+" | fuente="+source);last.setText("ÚLTIMO CAMBIO: "+d+" ("+old+" → "+cur+")");updateVolume();}}
    void updateVolume(){if(volume!=null)volume.setText("STREAM_MUSIC: "+audio.getStreamVolume(AudioManager.STREAM_MUSIC)+" / "+audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));}
    void updateSession(){if(session==null)return;try{List<MediaController> l=media.getActiveSessions(null);if(l==null||l.isEmpty()){session.setText("MEDIA SESSION: —");return;}MediaController c=l.get(0);PlaybackState p=c.getPlaybackState();session.setText("MEDIA SESSION: "+c.getPackageName()+" | ESTADO: "+(p==null?"—":state(p.getState())));}catch(SecurityException e){session.setText("MEDIA SESSION: ACCESO NO DISPONIBLE");}catch(Exception e){session.setText("MEDIA SESSION: ERROR "+e.getClass().getSimpleName());}}
    String state(int s){switch(s){case PlaybackState.STATE_PLAYING:return"PLAYING";case PlaybackState.STATE_PAUSED:return"PAUSED";case PlaybackState.STATE_STOPPED:return"STOPPED";case PlaybackState.STATE_BUFFERING:return"BUFFERING";default:return"STATE_"+s;}}

    public boolean dispatchKeyEvent(KeyEvent e){int c=e.getKeyCode();if(c==KeyEvent.KEYCODE_VOLUME_UP||c==KeyEvent.KEYCODE_VOLUME_DOWN||c==KeyEvent.KEYCODE_VOLUME_MUTE||c==KeyEvent.KEYCODE_MEDIA_PLAY||c==KeyEvent.KEYCODE_MEDIA_PAUSE||c==KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE){String n=keyName(c),a=e.getAction()==KeyEvent.ACTION_DOWN?"DOWN":"UP";key.setText("ÚLTIMO KEY EVENT: "+n+" / "+a);add("KEY_EVENT: "+n+" / "+a+" / repeat="+e.getRepeatCount());}return super.dispatchKeyEvent(e);}
    String keyName(int c){switch(c){case KeyEvent.KEYCODE_VOLUME_UP:return"VOLUME_UP";case KeyEvent.KEYCODE_VOLUME_DOWN:return"VOLUME_DOWN";case KeyEvent.KEYCODE_VOLUME_MUTE:return"VOLUME_MUTE";case KeyEvent.KEYCODE_MEDIA_PLAY:return"MEDIA_PLAY";case KeyEvent.KEYCODE_MEDIA_PAUSE:return"MEDIA_PAUSE";case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:return"MEDIA_PLAY_PAUSE";default:return"KEYCODE_"+c;}}
    void add(String m){if(history==null)return;String line="["+new SimpleDateFormat("HH:mm:ss.SSS",Locale.getDefault()).format(new Date())+"] "+m;String old=history.getText().toString();history.setText(old.isEmpty()?line:old+"\n"+line);history.post(()->{if(history.getLayout()!=null)history.scrollTo(0,Math.max(0,history.getLayout().getLineTop(history.getLineCount())));});}
}
