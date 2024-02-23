package jwtly10.codeecho.toolWindow;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.content.Content;
import com.intellij.util.ui.JBUI;
import jwtly10.codeecho.model.ChatGPTMessage;
import jwtly10.codeecho.model.ChatGPTRole;
import jwtly10.codeecho.model.ChatGPTSession;
import jwtly10.codeecho.persistance.ChatPersistence;
import jwtly10.codeecho.service.AudioService;
import jwtly10.codeecho.toolWindow.component.CustomProgressBar;
import jwtly10.codeecho.toolWindow.ui.MessageWindowUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        private static ChatGPTSession openSession;
        private final MessageWindowUI messageWindowUI;

        public CodeEchoToolWindowContent() {
            // Generate a test session
//            ChatGPTSession testSession = new ChatGPTSession();
//            testSession.addMessage(new ChatGPTMessage(ChatGPTRole.user, "Hello"));
//            testSession.addMessage(new ChatGPTMessage(ChatGPTRole.system, "Hi there!"));
//            testSession.addMessage(new ChatGPTMessage(ChatGPTRole.user, "How are you?"));
//            testSession.addMessage(new ChatGPTMessage(ChatGPTRole.system, "I'm good, thanks!"));
//            try {
//                ChatPersistence.saveSessions(List.of(testSession));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            List<ChatGPTSession> sessions = new ArrayList<>();
            try {
                sessions = ChatPersistence.loadSessions();
            } catch (Exception e) {
                // TODO: Handle what happens when the sessions cannot be loaded
                System.out.println("Error loading sessions");
                e.printStackTrace();
            }

            this.messageWindowUI = new MessageWindowUI();
            if (sessions == null) {
                // TODO: Handle what happens when the sessions are  empty
                System.out.println("No session");
                return;
            }
            openSession = sessions.get(0);

            contentPanel.setLayout(new BorderLayout());

            contentPanel.add(messageWindowUI, BorderLayout.CENTER);
            contentPanel.setBorder(JBUI.Borders.empty(30));
            this.messageWindowUI.set(openSession.getMessages());
            this.messageWindowUI.setBorder(JBUI.Borders.emptyBottom(30));

            contentPanel.add(createInputPanel(), BorderLayout.SOUTH);
        }

        public JPanel createInputPanel() {
            JPanel inputPanel = new JPanel();

            inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

            JPanel tmpPanel = new JPanel();
            tmpPanel.setLayout(new BorderLayout());

            JPanel innerPanel = new JPanel();
            innerPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            ImageIcon sendIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/icons/icon-send.png")));
            Image sendIconImage = sendIcon.getImage();
            Image scaledInstance = sendIconImage.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
            sendIcon = new ImageIcon(scaledInstance);

            JButton sendButton = new JButton(sendIcon);
            sendButton.setPreferredSize(new Dimension(50, 50));

            JTextArea textField = new JTextArea();
            textField.setMargin(JBUI.insets(10));
            textField.setText("Message CodeEcho...");
            textField.setLineWrap(true);
            textField.setWrapStyleWord(true);
            textField.setBorder(BorderFactory.createLineBorder(JBColor.BLACK));
            textField.setForeground(JBColor.GRAY);
            Border margin = JBUI.Borders.empty(10);
            textField.setBorder(new CompoundBorder(textField.getBorder(), margin));
            textField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (textField.getText().equals("Message CodeEcho...")) {
                        textField.setText("");
                        textField.setForeground(JBColor.WHITE);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (textField.getText().isEmpty()) {
                        textField.setForeground(JBColor.WHITE);
                        textField.setText("Message CodeEcho...");
                    }
                }
            });

            sendButton.setEnabled(false);

            textField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    if ((evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) && evt.isControlDown()) {
                        String text = textField.getText();
                        if (!sendMessage(new ChatGPTMessage(ChatGPTRole.user, text), textField)) {
                            textField.setText("Message CodeEcho...");
                            sendButton.setEnabled(false);
                        }
                    }

                    // Enable send button if the text field is not empty
                    sendButton.setEnabled(!textField.getText().isEmpty() || !textField.getText().equals("Message CodeEcho..."));
                }
            });

            sendButton.addActionListener(e -> {
                String text = textField.getText();
                if (!sendMessage(new ChatGPTMessage(ChatGPTRole.user, text), textField)) {
                    textField.setText("Message CodeEcho...");
                    sendButton.setEnabled(false);
                }
            });


            JLabel messageLabel = new JLabel("Error messages go here");


            AudioService audioService = new AudioService();
            CustomProgressBar progressBar = new CustomProgressBar(5000);

            final boolean[] isRecording = {false};
            final byte[][] audioData = new byte[1][];

            JButton playButton = createPlayButton(audioData, progressBar, messageLabel);
            JButton recordButton = createRecordButton(isRecording, audioData, progressBar, playButton, audioService, messageLabel, textField);
            progressBar.setVisible(false);

            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.gridx = 0;
            gbc.gridy = 0;
            innerPanel.add(textField, gbc);

            JPanel pinPanel = new JPanel();
            pinPanel.setLayout(new BorderLayout());
            gbc.fill = GridBagConstraints.VERTICAL;
            gbc.weighty = 1.0;
            gbc.gridx = 1;
            gbc.gridy = 0;
            innerPanel.add(pinPanel, gbc);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BorderLayout());

            JPanel buttonPanelInner = new JPanel();
            buttonPanelInner.setLayout(new GridBagLayout());

            gbc.weightx = 0;
            gbc.gridx = 1;
            gbc.gridy = 0;
            buttonPanelInner.add(recordButton, gbc);
            gbc.gridx = 2;
            gbc.gridy = 0;
            buttonPanelInner.add(sendButton, gbc);
            buttonPanel.add(buttonPanelInner, BorderLayout.SOUTH);


            pinPanel.add(buttonPanel, BorderLayout.SOUTH);


            tmpPanel.add(innerPanel, BorderLayout.NORTH);
            inputPanel.add(tmpPanel);
            inputPanel.add(progressBar);

            return inputPanel;
        }

        private boolean sendMessage(ChatGPTMessage message, JTextArea textField) {
            if (message.getContent() == null || message.getContent().isEmpty() || message.getContent().equals("Message CodeEcho...")) {
                return false;
            }
            openSession.addMessage(message);
            SwingUtilities.invokeLater(() -> {
                textField.setText("");
                textField.requestFocusInWindow();
                messageWindowUI.set(openSession.getMessages());
            });
            try {
                ChatPersistence.saveSessions(List.of(openSession));
            } catch (IOException e) {
                System.out.println("Error saving session");
            }

            return true;
        }

        public JPanel getContentPanel() {
            return contentPanel;
        }
    }
}
