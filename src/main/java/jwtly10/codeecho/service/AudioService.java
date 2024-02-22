package jwtly10.codeecho.service;

import com.intellij.openapi.diagnostic.Logger;
import jwtly10.codeecho.callback.AsyncCallback;
import jwtly10.codeecho.exception.AudioException;
import jwtly10.codeecho.model.RecordModel;
import jwtly10.codeecho.model.TranscriptResponse;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.http.HttpClient;

public class AudioService {
    private static volatile boolean STOP_RECORDING = false;

    private static final float sampleRate = 16000;
    private static final int sampleSizeInBits = 16;
    private static final int channels = 2;

    private static final Logger log = Logger.getInstance(AudioService.class);

    /**
     * Starts recording for the given duration and calls the callback when the recording is stopped
     *
     * @param duration duration in milliseconds
     * @param callback callback to be called when the recording is stopped, returns the recorded audio data
     */
    public void record(int duration, AsyncCallback<RecordModel> callback) {
        Thread recordingThread = new Thread(() -> {
            try {
                log.info("Recording started");
                AudioFormat format = getAudioFormat();
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                final TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);

                targetLine.open(format);
                targetLine.start();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] data = new byte[4096];
                long startTime = System.currentTimeMillis();

                while (System.currentTimeMillis() - startTime < duration) {
                    if (STOP_RECORDING) {
                        log.info("Recording forcefully stopped");
                        // Reset the flag
                        STOP_RECORDING = false;
                        break;
                    }
                    int bytesRead = targetLine.read(data, 0, data.length);
                    out.write(data, 0, bytesRead);
                }

                log.info("Recording ended");

                targetLine.stop();
                targetLine.close();

                ProxyService proxyService = new ProxyService(HttpClient.newHttpClient());

                try {
                    TranscriptResponse result = proxyService.transcribeAudio(out.toByteArray());
                    RecordModel res = new RecordModel(result, out.toByteArray());

                    if (callback != null) {
                        SwingUtilities.invokeLater(() -> callback.onResult(res));
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Transcription failed", e);
                }
            } catch (Exception ex) {
                log.error("Recording failed", ex);
                // TODO Handle different error cases and feedback to the user
                SwingUtilities.invokeLater(() -> callback.onError(new AudioException("Recording failed")));
            }
        });

        recordingThread.start();
    }

    /**
     * Plays back the given audio data
     *
     * @param audioData audio data to be played back
     * @param callback  callback to be called when the playback is stopped, no data is returned
     */
    public static void play(byte[] audioData, AsyncCallback<Void> callback) {
        Thread playbackThread = new Thread(() -> {
            try {
                log.info("Playing back audio");
                playBack(audioData);
                log.info("Playback ended");
                if (callback != null) {
                    SwingUtilities.invokeLater(callback::onComplete);
                }
            } catch (LineUnavailableException e) {
                log.error("Playback failed", e);
                // TODO Handle different error cases and feedback to the user
                SwingUtilities.invokeLater(() -> callback.onError(new AudioException("Playback failed")));
            }
        });

        playbackThread.start();
    }

    /**
     * Stops the recording forcefully
     */
    public void stopRecording() {
        STOP_RECORDING = true;
    }

    /**
     * Stops the playback forcefully
     */
    public void stopPlayback() {
        // TODO Implement this method
    }

    private static AudioFormat getAudioFormat() {
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public double estimateDuration(byte[] audioData) {
        double bytesPerSecond = sampleRate * channels * (sampleSizeInBits / 8.0);
        return audioData.length / bytesPerSecond;
    }

    private static void playBack(byte[] audioData) throws LineUnavailableException {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            final SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info);

            sourceLine.open(format);
            sourceLine.start();

            ByteArrayInputStream in = new ByteArrayInputStream(audioData);
            int bytesRead;
            byte[] data = new byte[4096];
            while ((bytesRead = in.read(data, 0, data.length)) != -1) {
                sourceLine.write(data, 0, bytesRead);
            }

            sourceLine.drain();
            sourceLine.close();
        } catch (Exception e) {
            log.error("Playback failed", e);
            throw new LineUnavailableException("Playback failed.");
        }
    }
}
