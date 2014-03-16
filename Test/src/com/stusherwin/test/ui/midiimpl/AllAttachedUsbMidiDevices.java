package com.stusherwin.test.ui.midiimpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.stusherwin.test.core.Disposable;
import com.stusherwin.test.core.midi.MidiDevice;
import com.stusherwin.test.core.midi.MidiDeviceInputListener;
import com.stusherwin.test.core.midi.Note;
import com.stusherwin.test.core.midi.SysExMessage;
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

public class AllAttachedUsbMidiDevices extends UsbMidiInputEventListener implements Disposable, MidiDevice {
    Map<UsbDevice, UsbDeviceConnection> deviceConnections = null;
	Map<UsbDevice, Set<MidiInputDevice>> midiInputDevices = null;
	Map<UsbDevice, Set<MidiOutputDevice>> midiOutputDevices = null;
	OnMidiDeviceAttachedListener deviceAttachedListener = null;
	OnMidiDeviceDetachedListener deviceDetachedListener = null;
	Handler deviceDetachedHandler = null;
	MidiDeviceConnectionWatcher deviceConnectionWatcher = null;
	Set<MidiDeviceInputListener> inputEventListeners = null;

	public AllAttachedUsbMidiDevices(Context context) {
		inputEventListeners = new HashSet<MidiDeviceInputListener>();
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
	
	public void addInputListener(MidiDeviceInputListener listener) {
		inputEventListeners.add(listener);
	}

    public void removeInputListener(MidiDeviceInputListener listener) {
        inputEventListeners.remove(listener);
    }

    @Override
    public void dispose() {
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

    @Override
    public void onMidiSystemExclusive(MidiInputDevice sender, int cable,
                                      byte[] systemExclusive) {
        for(MidiDeviceInputListener listener: inputEventListeners) {
            listener.onSysExMessage(new SysExMessage(systemExclusive));
        }
    }

    @Override
    public void onMidiNoteOn(MidiInputDevice sender, int cable,
                             int channel, int note, int velocity) {
        for(MidiDeviceInputListener listener: inputEventListeners) {
            listener.onNoteOn(new Note( channel, note, velocity ));
        }
    }

    @Override
    public void onMidiNoteOff(MidiInputDevice sender, int cable,
                              int channel, int note, int velocity) {
        for(MidiDeviceInputListener listener: inputEventListeners) {
            listener.onNoteOff(new Note( channel, note, velocity ));
        }
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