package jwtly10.codeecho.toolWindow.ui;

import com.intellij.openapi.diagnostic.Logger;
import jwtly10.codeecho.callback.AsyncCallback;
import jwtly10.codeecho.service.AudioService;
import jwtly10.codeecho.toolWindow.component.CustomProgressBar;

import javax.swing.*;

public class CodeEchoUILogic {

    private static final Logger log = Logger.getInstance(CodeEchoUILogic.class);

    /**
     * Creates a button that starts and stops recording audio
     *
     * @param isRecording  a boolean array that holds the recording state
     * @param audioData    a byte array that holds the recorded audio data
     * @param progressBar  a progress bar that shows the recording progress
     * @param playButton   a button that plays the recorded audio
     * @param audioService an audio service that handles audio
     * @return a JButton that starts and stops recording audio
     */
    public static JButton createRecordButton(final boolean[] isRecording,
                                             final byte[][] audioData,
                                             CustomProgressBar progressBar,
                                             JButton playButton,
                                             AudioService audioService,
                                             JLabel messageLabel) {
        JButton recordButton = new JButton("Start Listening");

        recordButton.addActionListener(e -> {
            if (isRecording[0]) {
                isRecording[0] = false;
                progressBar.stop();
                recordButton.setText("Start Listening");
                audioService.stopRecording();
                return;
            }

            progressBar.updateDuration(5);
            progressBar.start();
            playButton.setEnabled(false);
            audioService.record(5000, new AsyncCallback<>() {
                @Override
                public void onError(Exception e) {
                    messageLabel.setText("Recording failed");
                    log.error("Recording failed", e);
                }

                @Override
                public void onResult(byte[] output) {
                    audioData[0] = output;
                    recordButton.setText("Start Listening");
                    isRecording[0] = false;
                    progressBar.updateDuration(audioService.estimateDuration(output));
                    playButton.setEnabled(true);
                }
            });
            isRecording[0] = true;
            recordButton.setText("Stop Listening");
        });

        return recordButton;
    }

    /**
     * Creates a button that plays the recorded audio
     *
     * @param audioData a byte array that holds the recorded audio data
     * @return a JButton that plays the recorded audio
     */
    public static JButton createPlayButton(final byte[][] audioData,
                                           final CustomProgressBar progressBar,
                                           final JLabel messageLabel) {
        JButton playButton = new JButton("Play Audio");
        playButton.setEnabled(false);
        playButton.addActionListener(e -> {
            progressBar.start();
            playButton.setEnabled(false);
            if (audioData[0] != null) {
                AudioService.play(audioData[0], new AsyncCallback<Void>() {
                    @Override
                    public void onError(Exception e) {
                        messageLabel.setText("Playback failed");
                        log.error("Playback failed", e);
                    }

                    @Override
                    public void onComplete() {
                        playButton.setEnabled(true);
                    }
                });
            }
        });

        return playButton;
    }
}
