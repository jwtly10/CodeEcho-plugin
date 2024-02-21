package jwtly10.codeecho.toolWindow;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import jwtly10.codeecho.service.AudioService;
import jwtly10.codeecho.toolWindow.component.CustomProgressBar;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static jwtly10.codeecho.toolWindow.ui.CodeEchoUILogic.createPlayButton;
import static jwtly10.codeecho.toolWindow.ui.CodeEchoUILogic.createRecordButton;

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

            // For error messages etc
            JLabel messageLabel = new JLabel("Code Echo");
            contentPanel.add(createRecorderPanel(messageLabel), BorderLayout.CENTER);

            contentPanel.add(messageLabel, BorderLayout.SOUTH);
        }

        public JPanel createRecorderPanel(JLabel messageLabel) {
            JPanel recorderPanel = new JPanel();
            CustomProgressBar progressBar = new CustomProgressBar(5000);

            AudioService audioService = new AudioService();

            final boolean[] isRecording = {false};
            final byte[][] audioData = new byte[1][];

            JButton playButton = createPlayButton(audioData, progressBar, messageLabel);
            JButton recordButton = createRecordButton(isRecording, audioData, progressBar, playButton, audioService, messageLabel);

            recorderPanel.add(recordButton);
            recorderPanel.add(playButton);
            recorderPanel.add(progressBar);
            return recorderPanel;
        }


        public JPanel createIntroPanel() {
            JPanel introPanel = new JPanel();
            JLabel introLabel = new JLabel("Click the button below to start listening");
            introPanel.add(introLabel);
            return introPanel;
        }

        public JPanel getContentPanel() {
            return contentPanel;
        }
    }
}
