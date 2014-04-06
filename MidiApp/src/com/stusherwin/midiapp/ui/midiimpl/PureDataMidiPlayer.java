package com.stusherwin.midiapp.ui.midiimpl;

import android.content.*;
import android.content.res.Resources;
import android.os.IBinder;
import android.preference.PreferenceManager;
import com.stusherwin.midiapp.core.midi.MidiPlayer;
import com.stusherwin.midiapp.ui.R;
import org.puredata.android.service.PdPreferences;
import org.puredata.android.service.PdService;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PureDataMidiPlayer implements SharedPreferences.OnSharedPreferenceChangeListener, MidiPlayer {
    private PdService pdService = null;
    private Context context;

    public PureDataMidiPlayer(Context context) {
        this.context = context;
    }

    public void init() {
        PdPreferences.initPreferences(this.context);
        PreferenceManager.getDefaultSharedPreferences(this.context).registerOnSharedPreferenceChangeListener(this);

        this.context.bindService(new Intent(this.context, PdService.class), connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        startAudio();
    }

    public void destroy() {
        try {
            this.context.unbindService(connection);
        } catch (IllegalArgumentException e) {
            // already unbound
            pdService = null;
        }
    }

    private void initPd() {
        Resources res = this.context.getResources();
        File poly = null;
        try {
            PdBase.subscribe("android");
            poly = IoUtils.extractResource(res.openRawResource(R.raw.poly), "poly.pd", this.context.getCacheDir());
            PdBase.openPatch(poly);
            startAudio();
        } catch(IOException ex) {
            // do something?
        } finally {
            if (poly != null) poly.delete();
        }
    }

    private void startAudio() {
        Resources res = this.context.getResources();
        String name = res.getString(R.string.app_name);
        try {
            pdService.initAudio(-1, -1, -1, -1);   // negative values will be replaced with defaults/preferences
            pdService.startAudio(new Intent(this.context, PureDataMidiPlayer.class), R.drawable.ic_launcher, name, "Return to " + name + ".");
        } catch (IOException e) {
            // do something?
        }
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pdService = ((PdService.PdBinder)service).getService();
            initPd();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // this method will never be called
        }
    };

    @Override
    public void noteOn(int note, int velocity) {
        PdBase.sendNoteOn(1, note, velocity);
    }

    @Override
    public void noteOff(int note) {
        PdBase.sendNoteOn(1, note, 0);
    }
}
