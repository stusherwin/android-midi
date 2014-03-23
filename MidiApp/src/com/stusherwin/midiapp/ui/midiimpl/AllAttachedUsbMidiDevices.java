package com.stusherwin.midiapp.ui.midiimpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.stusherwin.midiapp.core.Destroyable;
import com.stusherwin.midiapp.core.midi.MidiDevice;
import com.stusherwin.midiapp.core.midi.MidiEvent;
import com.stusherwin.midiapp.core.midi.NoteOff;
import com.stusherwin.midiapp.core.midi.NoteOn;
import jp.kshoji.driver.midi.device.MidiInputDevice;
import jp.kshoji.driver.midi.device.MidiOutputDevice;
import jp.kshoji.driver.midi.listener.OnMidiDeviceAttachedListener;
import jp.kshoji.driver.midi.listener.OnMidiDeviceDetachedListener;
import jp.kshoji.driver.midi.thread.MidiDeviceConnectionWatcher;
import jp.kshoji.driver.midi.util.Constants;
import jp.kshoji.driver.midi.util.UsbMidiDeviceUtils;
import jp.kshoji.driver.usb.util.DeviceFilter;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.Log;
import rx.Observable;
import rx.subjects.PublishSubject;

public class AllAttachedUsbMidiDevices extends UsbMidiInputEventListener implements Destroyable, MidiDevice {
    Map<UsbDevice, UsbDeviceConnection> deviceConnections = null;
	Map<UsbDevice, Set<MidiInputDevice>> midiInputDevices = null;
	Map<UsbDevice, Set<MidiOutputDevice>> midiOutputDevices = null;
	OnMidiDeviceAttachedListener deviceAttachedListener = null;
	OnMidiDeviceDetachedListener deviceDetachedListener = null;
	Handler deviceDetachedHandler = null;
	MidiDeviceConnectionWatcher deviceConnectionWatcher = null;

	public AllAttachedUsbMidiDevices(Context context) {
		deviceConnections = new HashMap<UsbDevice, UsbDeviceConnection>();
		midiInputDevices = new HashMap<UsbDevice, Set<MidiInputDevice>>();
		midiOutputDevices = new HashMap<UsbDevice, Set<MidiOutputDevice>>();
		
		UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
		deviceAttachedListener = new OnMidiDeviceAttachedListenerImpl(context, usbManager);
		deviceDetachedListener = new OnMidiDeviceDetachedListenerImpl();
		
		deviceDetachedHandler = new Handler(new Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				Log.i(Constants.TAG, "(handleMessage) detached device:" + msg.obj);
				UsbDevice usbDevice = (UsbDevice) msg.obj;
				return true;
			}
		});

		deviceConnectionWatcher = new MidiDeviceConnectionWatcher(context, usbManager, deviceAttachedListener, deviceDetachedListener);
	}

    @Override
    public void destroy() {
        deviceConnectionWatcher.stop();
        deviceConnectionWatcher = null;

        if (midiInputDevices != null) {
            for (Set<MidiInputDevice> inputDevices : midiInputDevices.values()) {
                if (inputDevices != null) {
                    for (MidiInputDevice inputDevice : inputDevices) {
                        if (inputDevice != null) {
                            inputDevice.stop();
                        }
                    }
                }
            }

            midiInputDevices.clear();
        }
        midiInputDevices = null;

        if (midiOutputDevices != null) {
            midiOutputDevices.clear();
        }
        midiOutputDevices = null;

        deviceConnections = null;
    }

    private PublishSubject<MidiEvent> _eventSubject = PublishSubject.create();

    @Override
    public void onMidiSystemExclusive(MidiInputDevice sender, int cable,
                                      byte[] systemExclusive) {
    }

    @Override
    public void onMidiNoteOn(MidiInputDevice sender, int cable,
                             int channel, int note, int velocity) {
        _eventSubject.onNext(new NoteOn( channel, note, velocity ));
    }

    @Override
    public void onMidiNoteOff(MidiInputDevice sender, int cable,
                              int channel, int note, int velocity) {
        _eventSubject.onNext(new NoteOff( channel, note, velocity ));
    }

    @Override
    public Observable<MidiEvent> getNoteStream() {
        return _eventSubject.asObservable();
    }

    final class OnMidiDeviceAttachedListenerImpl implements OnMidiDeviceAttachedListener {
        private final UsbManager usbManager;
        private final Context context;

        public OnMidiDeviceAttachedListenerImpl(Context context, UsbManager usbManager) {
            this.context = context;
            this.usbManager = usbManager;
        }

        @Override
        public synchronized void onDeviceAttached(UsbDevice attachedDevice) {
            // these fields are null; when this event fired while Activity destroying.
            if (midiInputDevices == null || midiOutputDevices == null || deviceConnections == null) {
                // nothing to do
                return;
            }

            deviceConnectionWatcher.notifyDeviceGranted();

            UsbDeviceConnection deviceConnection = usbManager.openDevice(attachedDevice);
            if (deviceConnection == null) {
                return;
            }

            deviceConnections.put(attachedDevice, deviceConnection);

            List<DeviceFilter> deviceFilters = DeviceFilter.getDeviceFilters(this.context);

            Set<MidiInputDevice> foundInputDevices = UsbMidiDeviceUtils.findMidiInputDevices(
                    attachedDevice,
                    deviceConnection,
                    deviceFilters,
                    AllAttachedUsbMidiDevices.this);

            for (MidiInputDevice midiInputDevice : foundInputDevices) {
                try {
                    Set<MidiInputDevice> inputDevices = midiInputDevices.get(attachedDevice);
                    if (inputDevices == null) {
                        inputDevices = new HashSet<MidiInputDevice>();
                    }
                    inputDevices.add(midiInputDevice);
                    midiInputDevices.put(attachedDevice, inputDevices);
                } catch (IllegalArgumentException iae) {
                    Log.i(Constants.TAG, "This device didn't have any input endpoints.", iae);
                }
            }

            Set<MidiOutputDevice> foundOutputDevices = UsbMidiDeviceUtils.findMidiOutputDevices(attachedDevice, deviceConnection, deviceFilters);
            for (MidiOutputDevice midiOutputDevice : foundOutputDevices) {
                try {
                    Set<MidiOutputDevice> outputDevices = midiOutputDevices.get(attachedDevice);
                    if (outputDevices == null) {
                        outputDevices = new HashSet<MidiOutputDevice>();
                    }
                    outputDevices.add(midiOutputDevice);
                    midiOutputDevices.put(attachedDevice, outputDevices);
                } catch (IllegalArgumentException iae) {
                    Log.i(Constants.TAG, "This device didn't have any output endpoints.", iae);
                }
            }

            Log.d(Constants.TAG, "Device " + attachedDevice.getDeviceName() + " has been attached.");
        }
    }

    final class OnMidiDeviceDetachedListenerImpl implements OnMidiDeviceDetachedListener {
        @Override
        public synchronized void onDeviceDetached(UsbDevice detachedDevice) {
            // these fields are null; when this event fired while Activity destroying.
            if (midiInputDevices == null || midiOutputDevices == null || deviceConnections == null) {
                // nothing to do
                return;
            }

            // Stop input device's thread.
            Set<MidiInputDevice> inputDevices = midiInputDevices.get(detachedDevice);
            if (inputDevices != null && inputDevices.size() > 0) {
                for (MidiInputDevice inputDevice : inputDevices) {
                    if (inputDevice != null) {
                        inputDevice.stop();
                    }
                }
                midiInputDevices.remove(detachedDevice);
            }

            Set<MidiOutputDevice> outputDevices = midiOutputDevices.get(detachedDevice);
            if (outputDevices != null) {
                for (MidiOutputDevice outputDevice : outputDevices) {
                    if (outputDevice != null) {
                        outputDevice.stop();
                    }
                }
                midiOutputDevices.remove(detachedDevice);
            }

            UsbDeviceConnection deviceConnection = deviceConnections.get(detachedDevice);
            if (deviceConnection != null) {
                deviceConnection.close();

                deviceConnections.remove(detachedDevice);
            }

            Log.d(Constants.TAG, "Device " + detachedDevice.getDeviceName() + " has been detached.");

            Message message = new Message();
            message.obj = detachedDevice;
            deviceDetachedHandler.sendMessage(message);
        }
    }
}