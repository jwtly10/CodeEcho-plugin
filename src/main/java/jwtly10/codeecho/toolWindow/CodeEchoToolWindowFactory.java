package jwtly10.codeecho.toolWindow;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import jwtly10.codeecho.service.AudioRecorder;
import jwtly10.codeecho.toolWindow.component.RecordProgressBar;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class CodeEchoToolWindowFactory implements ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CodeEchoToolWindowContent toolWindowContent = new CodeEchoToolWindowContent();
        Content content = toolWindow.getContentManager().getFactory().createContent(toolWindowContent.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private static class CodeEchoToolWindowContent {

        private final JPanel contentPanel = new JPanel();

        public CodeEchoToolWindowContent() {
            contentPanel.setLayout(new BorderLayout(0, 20));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));

            contentPanel.add(createIntroPanel(), BorderLayout.PAGE_START);
            contentPanel.add(createRecorderPanel(), BorderLayout.CENTER);
        }

        public JPanel createRecorderPanel() {
            JPanel recorderPanel = new JPanel();

            JButton startButton = new JButton("Start Listening");
            startButton.setFont(new Font("Arial", Font.PLAIN, 16));

            RecordProgressBar progressBar = new RecordProgressBar(5000);

            final boolean[] isRecording = {false};

            startButton.addActionListener(e -> {
                if (isRecording[0]) {
                    progressBar.stopRecording();
                    isRecording[0] = false;
                    startButton.setText("Start Listening");
                    return;
                }

                progressBar.startRecording();
                AudioRecorder.startRecording(() -> {
                    SwingUtilities.invokeLater(() -> {
                        progressBar.stopRecording();
                        startButton.setText("Start Listening");
                        isRecording[0] = false;
                    });
                });
                isRecording[0] = true;
                startButton.setText("Stop Listening");
            });

            recorderPanel.add(startButton);
            recorderPanel.add(progressBar);
            return recorderPanel;
        }

        public JPanel createIntroPanel() {
            JPanel introPanel = new JPanel();

            JLabel introLabel = new JLabel("Click the button below to start listening");
            introLabel.setFont(new Font("Arial", Font.PLAIN, 16));

            introPanel.add(introLabel);
            return introPanel;
        }

        public JPanel getContentPanel() {
            return contentPanel;
        }
    }
}
