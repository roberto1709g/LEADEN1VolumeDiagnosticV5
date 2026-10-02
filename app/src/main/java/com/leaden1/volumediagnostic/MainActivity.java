package com.leaden1.volumediagnostic;

import android.app.Activity;
import android.content.Context;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.widget.TextView;
import java.util.List;

public class MainActivity extends Activity {

    private TextView session;
    private MediaSessionManager media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new TextView(this);
        session.setText("LEADEN1 VOLUME DIAGNOSTIC V5 - INICIANDO...");
        session.setPadding(32, 64, 32, 64);
        session.setTextSize(16f);
        setContentView(session);

        media = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        updateSession();
    }

    void updateSession() {
        if (session == null) return;
        try {
            List<MediaController> l = media.getActiveSessions(null);
            if (l == null || l.isEmpty()) {
                session.setText("LEADEN1 VOLUME DIAGNOSTIC V5\n\nMEDIA SESSION: —");
                return;
            }
            MediaController c = l.get(0);
            PlaybackState p = c.getPlaybackState();
            session.setText("LEADEN1 VOLUME DIAGNOSTIC V5\n\nMEDIA SESSION: " + c.getPackageName() + "\nESTADO: " + (p == null ? "—" : state(p.getState())));
        } catch (SecurityException e) {
            session.setText("LEADEN1 VOLUME DIAGNOSTIC V5\n\nMEDIA SESSION: ACCESO NO DISPONIBLE (Se requiere permiso de notificaciones)");
        } catch (Exception e) {
            session.setText("LEADEN1 VOLUME DIAGNOSTIC V5\n\nMEDIA SESSION: ERROR " + e.getClass().getSimpleName());
        }
    }

    private String state(int state) {
        switch (state) {
            case PlaybackState.STATE_PLAYING: return "PLAYING";
            case PlaybackState.STATE_PAUSED: return "PAUSED";
            case PlaybackState.STATE_STOPPED: return "STOPPED";
            default: return "OTRO (" + state + ")";
        }
    }
}
