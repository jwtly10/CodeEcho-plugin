package jwtly10.codeecho.toolWindow.ui;

import com.intellij.openapi.diagnostic.Logger;
import jwtly10.codeecho.callback.AsyncCallback;
import jwtly10.codeecho.model.RecordModel;
import jwtly10.codeecho.service.AudioService;
import jwtly10.codeecho.toolWindow.component.ProgressBarJPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

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
                                             ProgressBarJPanel progressBar,
                                             JButton playButton,
                                             AudioService audioService,
                                             JLabel messageLabel,
                                             JTextArea textArea
    ) {
        ImageIcon recordIcon = new ImageIcon(Objects.requireNonNull(CodeEchoUILogic.class.getResource("/images/icons/icon-record.png")));
        Image recordIconImage = recordIcon.getImage();
        Image scaledInstance = recordIconImage.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
        recordIcon = new ImageIcon(scaledInstance);
        JButton recordButton = new JButton(recordIcon);
        recordButton.setPreferredSize(new Dimension(50, 50));

        // TODO Handle record button state
        recordButton.addActionListener(e -> {
            if (isRecording[0]) {
                isRecording[0] = false;
                progressBar.stop();
//                recordButton.setText("L");
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
                public void onResult(RecordModel output) {
                    audioData[0] = output.getAudio();
                    System.out.printf("Transcript: %s\nConfidence: %f\n",
                            output.getTrans().getTranscript(),
                            output.getTrans().getConfidence());

                    textArea.setText(output.getTrans().getTranscript());
                    // TODO scroll to bottom

                    // Reset flags
//                    recordButton.setText("L");
                    isRecording[0] = false;
                    progressBar.updateDuration(audioService.estimateDuration(output.getAudio()));
                    playButton.setEnabled(true);
                }
            });
            isRecording[0] = true;
//            recordButton.setText("S");
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
                                           final ProgressBarJPanel progressBar,
                                           final JLabel messageLabel) {
        JButton playButton = new JButton("Play Audio");
        playButton.setEnabled(false);
        playButton.addActionListener(e -> {
            progressBar.start();
            playButton.setEnabled(false);
            if (audioData[0] != null) {
                AudioService.play(audioData[0], new AsyncCallback<>() {
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
