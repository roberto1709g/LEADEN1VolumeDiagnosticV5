package com.leaden1.volumediagnostic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

public class MainActivity extends Activity {

    private TextView session;
    private MediaSessionManager media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 64, 32, 64);

        session = new TextView(this);
        session.setText("LEADEN1 VOLUME DIAGNOSTIC V5\n\nINICIANDO...");
        session.setTextSize(16f);
        layout.addView(session);

        Button btnPermiso = new Button(this);
        btnPermiso.setText("CONCEDER PERMISO DE NOTIFICACIONES");
        btnPermiso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                } catch (Exception e) {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            }
        });
        layout.addView(btnPermiso);

        setContentView(layout);
        media = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSession();
    }

    void updateSession() {
        if (session == null) return;
        try {
            List<MediaController> l = media.getActiveSessions(null);
            if (l == null || l.isEmpty()) {
                session.setText("LEADEN1 VOLUME DIAGNOSTIC V5\n\nMEDIA SESSION: — (Sin reproducción activa)");
                return;
            }
            MediaController c = l.get(0);
            PlaybackState p = c.getPlaybackState();
            session.setText("LEADEN1 VOLUME DIAGNOSTIC V5\n\nMEDIA SESSION: " + c.getPackageName() + "\nESTADO: " + (p == null ? "—" : state(p.getState())));
        } catch (SecurityException e) {
            session.setText("LEADEN1 VOLUME DIAGNOSTIC V5\n\nMEDIA SESSION: ACCESO NO DISPONIBLE\nPresiona el botón de abajo para activar.");
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
