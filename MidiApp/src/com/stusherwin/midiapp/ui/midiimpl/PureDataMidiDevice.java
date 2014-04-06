package com.stusherwin.midiapp.ui.midiimpl;

import android.app.FragmentManager;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import com.noisepages.nettoyeur.midi.MidiReceiver;
import com.noisepages.nettoyeur.usb.ConnectionFailedException;
import com.noisepages.nettoyeur.usb.DeviceNotConnectedException;
import com.noisepages.nettoyeur.usb.InterfaceNotAvailableException;
import com.noisepages.nettoyeur.usb.UsbBroadcastHandler;
import com.noisepages.nettoyeur.usb.midi.UsbMidiDevice;
import com.noisepages.nettoyeur.usb.midi.util.UsbMidiInputSelector;
import com.noisepages.nettoyeur.usb.util.AsyncDeviceInfoLookup;
import com.noisepages.nettoyeur.usb.util.UsbDeviceSelector;
import com.stusherwin.midiapp.core.InitializationException;
import com.stusherwin.midiapp.core.Notifier;
import com.stusherwin.midiapp.core.midi.MidiDevice;
import com.stusherwin.midiapp.core.midi.MidiEvent;
import com.stusherwin.midiapp.core.midi.NoteOff;
import com.stusherwin.midiapp.core.midi.NoteOn;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.util.List;

public class PureDataMidiDevice implements MidiDevice {
    private UsbMidiDevice midiDevice = null;
    private Context context;
    private FragmentManager fragmentManager;
    private Notifier notifier;
    private PublishSubject<MidiEvent> subject;

    public PureDataMidiDevice(Context context, FragmentManager fragmentManager, Notifier notifier) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.notifier = notifier;
        this.subject = PublishSubject.create();
    }

    @Override
    public void init() throws InitializationException {
        UsbBroadcastHandler broadcastHandler = new UsbBroadcastHandler() {
            @Override
            public void onPermissionGranted(UsbDevice device) {
                if (midiDevice == null || !midiDevice.matches(device)) return;
                try {
                    midiDevice.open(PureDataMidiDevice.this.context);
                } catch (ConnectionFailedException e) {
                    notifier.Notify("USB connection failed");
                    midiDevice = null;
                    return;
                }
                new UsbMidiInputSelector(midiDevice) {
                    @Override
                    protected void onInputSelected(UsbMidiDevice.UsbMidiInput input, UsbMidiDevice device, int iface,
                                                   int index) {
                        notifier.Notify("Input selection: Interface " + iface + ", Input " + index);
                        input.setReceiver(receiver);
                        try {
                            input.start();
                        } catch (DeviceNotConnectedException e) {
                            notifier.Notify("MIDI device has been disconnected");
                            return;
                        } catch (InterfaceNotAvailableException e) {
                            notifier.Notify("MIDI interface is unavailable");
                            return;
                        }
                    }

                    @Override
                    protected void onNoSelection(UsbMidiDevice device) {
                        notifier.Notify("No input selected");
                    }
                }.show(PureDataMidiDevice.this.fragmentManager, null);
            }

            @Override
            public void onPermissionDenied(UsbDevice device) {
                if (midiDevice == null || !midiDevice.matches(device)) return;
                notifier.Notify("Permission denied for device " + midiDevice.getCurrentDeviceInfo());
                midiDevice = null;
            }

            @Override
            public void onDeviceDetached(UsbDevice device) {
                if (midiDevice == null || !midiDevice.matches(device)) return;
                midiDevice.close();
                midiDevice = null;
                notifier.Notify("MIDI device disconnected");
            }
        };

        UsbMidiDevice.installBroadcastHandler(context, broadcastHandler);

        chooseMidiDevice();
    }

    @Override
    public void destroy() {
        if (midiDevice != null) {
            midiDevice.close();
        }
        UsbMidiDevice.uninstallBroadcastHandler(context);
    }

    @Override
    public Observable<MidiEvent> getNoteStream() {
        return subject;
    }

    private final MidiReceiver receiver = new DefaultMidiReceiver() {
        @Override
        public void onNoteOn(int channel, int note, final int velocity) {
            if( velocity > 0 )
                subject.onNext(new NoteOn(channel, note, velocity));
            else
                subject.onNext(new NoteOff(channel, note, velocity));
        }

        @Override
        public void onNoteOff(int channel, int note, int velocity) {
            subject.onNext(new NoteOff(channel, note, velocity));
        }
    };

    private void chooseMidiDevice() {
        final List<UsbMidiDevice> devices = UsbMidiDevice.getMidiDevices(this.context);
        new AsyncDeviceInfoLookup() {

            @Override
            protected void onLookupComplete() {
                new UsbDeviceSelector<UsbMidiDevice>(devices) {
                    @Override
                    protected void onDeviceSelected(UsbMidiDevice device) {
                        midiDevice = device;
                        midiDevice.requestPermission(PureDataMidiDevice.this.context);
                    }

                    @Override
                    protected void onNoSelection() {
                        notifier.Notify("No device selected");
                    }
                }.show(PureDataMidiDevice.this.fragmentManager, null);
            }
        }.execute(devices.toArray(new UsbMidiDevice[devices.size()]));
    }
}
