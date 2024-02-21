package jwtly10.codeecho.service;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class AudioRecorder {
    public interface AudioCallback {
        void onRecordingStopped();
    }

    public static void startRecording(AudioCallback callback) {
        Thread recordingThread = new Thread(() -> {
            try {
                System.out.println("Recording started...");
                AudioFormat format = getAudioFormat();
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                final TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);

                targetLine.open(format);
                targetLine.start();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] data = new byte[4096];
                long startTime = System.currentTimeMillis();

                // Recording for 5 seconds
                while (System.currentTimeMillis() - startTime < 5000) {
                    int bytesRead = targetLine.read(data, 0, data.length);
                    out.write(data, 0, bytesRead);
                }

                System.out.println("Recording stopped...");

                targetLine.stop();
                targetLine.close();

                if (callback != null) {
                    callback.onRecordingStopped();
                }

                // Playback the recorded audio
                System.out.println("Playing back the recorded audio...");
                playBack(out.toByteArray());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        recordingThread.start();
    }

    private static AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
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
            e.printStackTrace();
            throw new LineUnavailableException("Playback failed.");
        }
    }
}
