package com.leaden1.volumediagnostic;

import android.content.ComponentName;
import android.content.Intent;
import android.media.session.MediaSessionManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView statusText;
    private TextView session;
    private MediaSessionManager media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        tv.setText("LEADEN1 VOLUME DIAGNOSTIC V5");
        setContentView(tv);

        media = (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);
        updateSession();
    }

    void updateSession() {
        if (session == null) return;
        try {
            List<android.media.session.MediaController> l = media.getActiveSessions(null);
            if (l == null || l.isEmpty()) {
                session.setText("MEDIA SESSION: —");
                return;
            }
            android.media.session.MediaController c = l.get(0);
            android.media.session.PlaybackState p = c.getPlaybackState();
            session.setText("MEDIA SESSION: " + c.getPackageName() + " | ESTADO: " + (p == null ? "—" : state(p.getState())));
        } catch (SecurityException e) {
            session.setText("MEDIA SESSION: ACCESO NO DISPONIBLE");
        } catch (Exception e) {
            session.setText("MEDIA SESSION: ERROR " + e.getClass().getSimpleName());
        }
    }

    private String state(int state) {
        switch (state) {
            case android.media.session.PlaybackState.STATE_PLAYING: return "PLAYING";
            case android.media.session.PlaybackState.STATE_PAUSED: return "PAUSED";
            case android.media.session.PlaybackState.STATE_STOPPED: return "STOPPED";
            default: return "OTRO (" + state + ")";
        }
    }
}
