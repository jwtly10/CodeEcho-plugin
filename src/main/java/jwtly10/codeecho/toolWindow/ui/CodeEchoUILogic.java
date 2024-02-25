package jwtly10.codeecho.toolWindow.ui;

import com.intellij.openapi.diagnostic.Logger;
import jwtly10.codeecho.callback.AsyncCallback;
import jwtly10.codeecho.model.RecordModel;
import jwtly10.codeecho.service.AudioService;
import jwtly10.codeecho.toolWindow.component.CustomButton;
import jwtly10.codeecho.toolWindow.utils.CColor;

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
     * @param audioService an audio service that handles audio
     * @return a JButton that starts and stops recording audio
     */
    public static JButton createRecordButton(final boolean[] isRecording,
                                             final byte[][] audioData,
                                             AudioService audioService,
                                             JLabel errorMessageLabel,
                                             JTextArea textArea
    ) {
        ImageIcon stopIcon = new ImageIcon(Objects.requireNonNull(CodeEchoUILogic.class.getResource("/images/icons/icon-stop2-white.png")));
        Image stopImg = stopIcon.getImage();
        Image stopScaled = stopImg.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);


        ImageIcon recordIcon = new ImageIcon(Objects.requireNonNull(CodeEchoUILogic.class.getResource("/images/icons/icon-record-white.png")));
        Image recordIconImage = recordIcon.getImage();
        Image recScaled = recordIconImage.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
        CustomButton recordButton = new CustomButton(new ImageIcon(recScaled), CColor.RED);
        recordButton.setPreferredSize(new Dimension(50, 50));

        recordButton.addActionListener(e -> {
            errorMessageLabel.setText("");
            recordButton.updateIcon(new ImageIcon(stopScaled));
            if (isRecording[0]) {
                isRecording[0] = false;
                audioService.stopRecording();
                recordButton.updateIcon(new ImageIcon(recScaled));
                return;
            }

            audioService.record(5000, new AsyncCallback<>() {
                @Override
                public void onError(Exception e) {
                    errorMessageLabel.setText(e.getMessage());
                    log.error("Recording failed", e);
                    recordButton.updateIcon(new ImageIcon(recScaled));
                }

                @Override
                public void onResult(RecordModel output) {
                    audioData[0] = output.getAudio();

                    log.info(String.format("DEBUG: Transcript: %s\nConfidence: %f\n",
                            output.getTrans().getTranscript(),
                            output.getTrans().getConfidence()));

                    textArea.setText(output.getTrans().getTranscript());

                    recordButton.updateIcon(new ImageIcon(recScaled));

                    // Reset flags
                    isRecording[0] = false;
                }
            });
            isRecording[0] = true;
        });

        return recordButton;
    }
}
