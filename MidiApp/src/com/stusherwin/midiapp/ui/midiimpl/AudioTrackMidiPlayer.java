package com.stusherwin.midiapp.ui.midiimpl;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import com.stusherwin.midiapp.core.midi.MidiPlayer;

public class AudioTrackMidiPlayer implements MidiPlayer {
    private static final double frequencyA = 440;
    private static final double midiNoteA = 57;
    private static final double twelfthRootOf2 = 1.0594630943592952645618252949463;
    private static final double twoPi = 2 * Math.PI;
    private static final short noOfTones = 88;
    private final Tone[] tones = new Tone[noOfTones];
    private final int sampleRate;
    private AudioTrack track;

    public AudioTrackMidiPlayer(int sampleRate) {
        this.sampleRate = sampleRate;

        int minSize = AudioTrack.getMinBufferSize(this.sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
        this.track = new AudioTrack( AudioManager.STREAM_MUSIC, this.sampleRate,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
                minSize, AudioTrack.MODE_STREAM);

        for(short i = 0; i < this.noOfTones; i++) {
            this.tones[i] = new Tone(i);
        }
    }

    public void init() {
        new Thread( new Runnable( ) {
            public void run( ) {
                track.play();

                short samples[] = new short[256];

                while( true )
                {
                    synchronized (AudioTrackMidiPlayer.this.tones) {
                        for( int i = 0; i < samples.length; i++ )
                        {
                            double sample = 0.0;
                            short tonesPlaying = 0;
                            for( int t = 0; t < noOfTones; t++ ) {
                                if( !tones[t].isPlaying() ) {
                                    continue;
                                }
                                sample += tones[t].getNextSample();
                                tonesPlaying++;
                            }
                            samples[i] = (short)((sample / tonesPlaying) * Short.MAX_VALUE);
                        }
                    }

                    track.write(samples, 0, samples.length);
                }
            }
        } ).start();
    }

    public void noteOn(int note) {
        synchronized (tones) {
            tones[note].start();
        }
    }

    public void noteOff(int note) {
        synchronized (tones) {
            tones[note].stop();
        }
    }

    public void destroy() {
        if (track != null) {
            try {
                track.stop();
                track.flush();
                track.release();
            } catch (Throwable t) {
                // do nothing
            } finally {
                track = null;
            }
        }
    }

    private class Tone {
        private final double frequency;
        private final double increment;
        private double angle = 0.0;
        private boolean playing = false;
        private boolean stopping = false;

        Tone(double frequency) {
            this.frequency = frequency;
            this.increment = (twoPi * frequency) / sampleRate;
        }

        Tone(int midiNote) {
            this(frequencyA * Math.pow(twelfthRootOf2, midiNote - midiNoteA));
        }

        public boolean isPlaying() {
            return playing;
        }

        public double getNextSample() {
            if(!this.playing) {
                return 0.0;
            }
            double sample = Math.sin( this.angle );

            this.angle += increment;
            if( this.angle >= twoPi ) {
                if(this.stopping) {
                    this.playing = false;
                    this.stopping = false;
                    this.angle = 0.0;
                }
                else {
                    this.angle = this.angle - twoPi;
                }
            }

            return sample;
        }

        public void start() {
            this.playing = true;
            this.stopping = false;
        }

        public void stop() {
            this.stopping = true;
        }
    }
}