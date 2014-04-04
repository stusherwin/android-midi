package com.stusherwin.midiapp.ui;

import android.app.Activity;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.noisepages.nettoyeur.midi.MidiReceiver;
import com.noisepages.nettoyeur.usb.ConnectionFailedException;
import com.noisepages.nettoyeur.usb.DeviceNotConnectedException;
import com.noisepages.nettoyeur.usb.InterfaceNotAvailableException;
import com.noisepages.nettoyeur.usb.UsbBroadcastHandler;
import com.noisepages.nettoyeur.usb.midi.UsbMidiDevice;
import com.noisepages.nettoyeur.usb.midi.util.UsbMidiInputSelector;
import com.noisepages.nettoyeur.usb.util.AsyncDeviceInfoLookup;
import com.noisepages.nettoyeur.usb.util.UsbDeviceSelector;
import com.stusherwin.midiapp.core.midi.MidiPlayer;
import com.stusherwin.midiapp.ui.midiimpl.PureDataMidiPlayer;

import java.util.List;


public class MyActivity extends Activity {
    private UsbMidiDevice midiDevice = null;
    private MidiPlayer midiPlayer;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseMidiDevice();
            }
        });

        UsbMidiDevice.installBroadcastHandler(this, broadcastHandler);

        midiPlayer = new PureDataMidiPlayer(getApplicationContext());
        midiPlayer.init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (midiDevice != null) {
            midiDevice.close();
        }
        UsbMidiDevice.uninstallBroadcastHandler(this);
        midiPlayer.destroy();
    }

    private final MidiReceiver receiver = new DefaultMidiReceiver() {
        @Override
        public void onNoteOn(int channel, int note, final int velocity) {
            if( velocity > 0 )
                midiPlayer.noteOn(note, velocity);
            else
                midiPlayer.noteOff(note);
        }

        @Override
        public void onNoteOff(int channel, int note, int velocity) {
            midiPlayer.noteOff(note);
        }
    };

    private final UsbBroadcastHandler broadcastHandler = new UsbBroadcastHandler() {
        @Override
        public void onPermissionGranted(UsbDevice device) {
            if (midiDevice == null || !midiDevice.matches(device)) return;
            try {
                midiDevice.open(MyActivity.this);
            } catch (ConnectionFailedException e) {
                toast("USB connection failed");
                midiDevice = null;
                return;
            }

            new UsbMidiInputSelector(midiDevice) {
                @Override
                protected void onInputSelected(UsbMidiDevice.UsbMidiInput input, UsbMidiDevice device, int iface,
                                               int index) {
                    toast("Input selection: Interface " + iface + ", Input " + index);
                    input.setReceiver(receiver);
                    try {
                        input.start();
                    } catch (DeviceNotConnectedException e) {
                        toast("MIDI device has been disconnected");
                        return;
                    } catch (InterfaceNotAvailableException e) {
                        toast("MIDI interface is unavailable");
                        return;
                    }
                }

                @Override
                protected void onNoSelection(UsbMidiDevice device) {
                    toast("No input selected");
                }
            }.show(getFragmentManager(), null);
        }

        @Override
        public void onPermissionDenied(UsbDevice device) {
            if (midiDevice == null || !midiDevice.matches(device)) return;
            toast("Permission denied for device " + midiDevice.getCurrentDeviceInfo());
            midiDevice = null;
        }

        @Override
        public void onDeviceDetached(UsbDevice device) {
            if (midiDevice == null || !midiDevice.matches(device)) return;
            midiDevice.close();
            midiDevice = null;
            toast("MIDI device disconnected");
        }
    };

    private void chooseMidiDevice() {
        final List<UsbMidiDevice> devices = UsbMidiDevice.getMidiDevices(this);
        new AsyncDeviceInfoLookup() {

            @Override
            protected void onLookupComplete() {
                new UsbDeviceSelector<UsbMidiDevice>(devices) {
                    @Override
                    protected void onDeviceSelected(UsbMidiDevice device) {
                        midiDevice = device;
                        midiDevice.requestPermission(MyActivity.this);
                    }

                    @Override
                    protected void onNoSelection() {
                        toast( "No device selected" );
                    }
                }.show(getFragmentManager(), null);
            }
        }.execute(devices.toArray(new UsbMidiDevice[devices.size()]));
    }

    private Toast toast = null;

    private void toast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            if (toast == null) {
                toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
            }
            toast.setText("MidiApp: " + msg);
            toast.show();
            }
        });
    }
}