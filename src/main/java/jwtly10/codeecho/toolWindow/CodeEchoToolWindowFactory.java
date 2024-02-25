package jwtly10.codeecho.toolWindow;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.content.Content;
import com.intellij.util.ui.JBUI;
import jwtly10.codeecho.callback.AsyncCallback;
import jwtly10.codeecho.model.ChatGPTMessage;
import jwtly10.codeecho.model.ChatGPTRequest;
import jwtly10.codeecho.model.ChatGPTRole;
import jwtly10.codeecho.model.ChatGPTSession;
import jwtly10.codeecho.persistance.ChatPersistence;
import jwtly10.codeecho.service.AudioService;
import jwtly10.codeecho.service.ParserService;
import jwtly10.codeecho.service.ProxyService;
import jwtly10.codeecho.toolWindow.component.ProgressBarJPanel;
import jwtly10.codeecho.toolWindow.component.StreamMessageJPanel;
import jwtly10.codeecho.toolWindow.ui.MessageWindowJPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.FileNotFoundException;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static jwtly10.codeecho.toolWindow.ui.CodeEchoUILogic.createPlayButton;
import static jwtly10.codeecho.toolWindow.ui.CodeEchoUILogic.createRecordButton;

public class CodeEchoToolWindowFactory implements ToolWindowFactory, DumbAware {

    private static final Logger log = Logger.getInstance(CodeEchoToolWindowFactory.class);

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CodeEchoToolWindowContent toolWindowContent = new CodeEchoToolWindowContent();
        Content content = toolWindow.getContentManager().getFactory().createContent(toolWindowContent.getMainContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private static class CodeEchoToolWindowContent {
        private final JPanel mainContentPanel = new JPanel();
        private static ChatGPTSession openSession;
        private final MessageWindowJPanel messageWindowJPanel;
        private final JLabel noChatsLabel = new JLabel("Get started by asking CodeEcho a question!");
        private volatile boolean isCancelled = false;
        private volatile boolean isRequestingChatGPT = false;
        private final AtomicInteger currentReqId = new AtomicInteger(0);

        public CodeEchoToolWindowContent() {
            List<ChatGPTSession> sessions = new ArrayList<>();
            try {
                sessions = ChatPersistence.loadSessions();
            } catch (FileNotFoundException e) {
                log.info("No sessions found, creating new session");
            } catch (Exception e) {
                log.error("Error loading sessions", e);
            }
            this.messageWindowJPanel = new MessageWindowJPanel();
            if (sessions.isEmpty()) {
                openSession = new ChatGPTSession();
                sessions.add(openSession);

                mainContentPanel.setLayout(new BorderLayout());
                mainContentPanel.add(noChatsLabel, BorderLayout.NORTH);
                mainContentPanel.add(messageWindowJPanel, BorderLayout.CENTER);
                this.messageWindowJPanel.initialLoad(openSession.getMessages());
                mainContentPanel.setBorder(JBUI.Borders.empty(30));
                this.messageWindowJPanel.setBorder(JBUI.Borders.emptyBottom(30));
                mainContentPanel.add(createInputPanel(), BorderLayout.SOUTH);

                return;
            }
            // TODO: Support multiple session
            // For now, we only support one session
            openSession = sessions.get(0);

            mainContentPanel.setLayout(new BorderLayout());

            mainContentPanel.add(messageWindowJPanel, BorderLayout.CENTER);
            mainContentPanel.setBorder(JBUI.Borders.empty(30));
            this.messageWindowJPanel.initialLoad(openSession.getMessages());
            this.messageWindowJPanel.setBorder(JBUI.Borders.emptyBottom(30));

            mainContentPanel.add(createInputPanel(), BorderLayout.SOUTH);
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

            JTextArea mainInputField = new JTextArea();
            mainInputField.setMargin(JBUI.insets(10));
            mainInputField.setText("Message CodeEcho...");
            mainInputField.setLineWrap(true);
            mainInputField.setWrapStyleWord(true);
            mainInputField.setBorder(BorderFactory.createLineBorder(JBColor.BLACK));
            mainInputField.setForeground(JBColor.BLACK);
            Border margin = JBUI.Borders.empty(10);
            mainInputField.setBorder(new CompoundBorder(mainInputField.getBorder(), margin));
            mainInputField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (mainInputField.getText().equals("Message CodeEcho...")) {
                        mainInputField.setText("");
                        mainInputField.setForeground(JBColor.BLACK);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (mainInputField.getText().isEmpty()) {
                        mainInputField.setForeground(JBColor.BLACK);
                        mainInputField.setText("Message CodeEcho...");
                    }
                }
            });

            mainInputField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    if ((evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) && evt.isControlDown()) {
                        handleNewReq(mainInputField);
                    }
                }
            });

            sendButton.addActionListener(e -> {
                handleNewReq(mainInputField);
            });

            /* TODO: Remove this message label code spaghetti */
            JLabel messageLabel = new JLabel("Error messages go here");

            AudioService audioService = new AudioService();
            ProgressBarJPanel progressBar = new ProgressBarJPanel(5000);

            final boolean[] isRecording = {false};
            final byte[][] audioData = new byte[1][];

            JButton playButton = createPlayButton(audioData, progressBar, messageLabel);
            JButton recordButton = createRecordButton(isRecording, audioData, progressBar, playButton, audioService, messageLabel, mainInputField);
            progressBar.setVisible(false);

            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.gridx = 0;
            gbc.gridy = 0;
            innerPanel.add(mainInputField, gbc);

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

        private void handleNewReq(JTextArea mainInputField) {
            if (isRequestingChatGPT) {
                log.info("Request already in progress, cancelling request");
                cancelOperation();
                isRequestingChatGPT = false;
                return;
            }

            if (canWeSendMessage(new ChatGPTMessage(ChatGPTRole.user, mainInputField.getText()), mainInputField)) {
                isRequestingChatGPT = true;
                sendNewChatMessage(mainInputField.getText());
                mainInputField.setText("");
                mainInputField.requestFocus();
            }
        }

        private void sendNewChatMessage(String message) {
            isCancelled = false;

            noChatsLabel.setVisible(false);

            String trimmedText = message.replaceAll("\\n+$", "");
            if (openSession.getMessages().size() >= 10) {
                log.debug("Deleting oldest message");
                openSession.getMessages().remove(0);
                messageWindowJPanel.removeOldestMessage();
            }

            openSession.addMessage(new ChatGPTMessage(ChatGPTRole.user, trimmedText));
            this.messageWindowJPanel.addNewMessage(new ChatGPTMessage(ChatGPTRole.user, trimmedText));

            StreamMessageJPanel streamMessageComponent = new StreamMessageJPanel();
            this.messageWindowJPanel.addNewStreamComponent(streamMessageComponent);
            streamMessageComponent.setHidden(true);


            Thread proxyThread = new Thread(() -> {
                int reqId = currentReqId.incrementAndGet();
                // Atomic integer to make sure we only run onResult > isCancelled once
                // Thread specific
                AtomicInteger count = new AtomicInteger();

                ProxyService proxyService = new ProxyService(HttpClient.newHttpClient());
                final String[] updatedContent = {""};

                ChatGPTRequest req = new ChatGPTRequest(openSession.getMessages(), message);
                proxyService.getChatGPTResponse(req, new AsyncCallback<>() {
                    @Override
                    public void onResult(String result) {
                        SwingUtilities.invokeLater(() -> {
                            if (reqId != currentReqId.get()) {
                                log.info("Request id mismatch, ignoring result");
                                return;
                            }
                            if (isCancelled && count.get() == 0) {
                                count.getAndIncrement();
                                log.info("Request cancelled, saving as is");
                                saveGPTMessage(updatedContent, streamMessageComponent);
                                // This prevents constantly updating the UI with the same message
                                // And allows us to ignore any subsequent messages from the stream
                                isRequestingChatGPT = false;
                                return;
                            }
                            if (isRequestingChatGPT) {
                                streamMessageComponent.setHidden(false);
                                updatedContent[0] = updatedContent[0].concat(result + "\n");
                                String htmlContent = ParserService.markdownToHtml(updatedContent[0]);
                                log.info("DEBUG: Html content stream: " + htmlContent);
                                streamMessageComponent.setHidden(false);
                                streamMessageComponent.setText(htmlContent);
                                messageWindowJPanel.scrollToBottom();
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        SwingUtilities.invokeLater(() -> {
                            if (reqId != currentReqId.get()) {
                                log.info("Request id mismatch, ignoring result");
                                return;
                            }
                            isRequestingChatGPT = false;
                            if (isCancelled) {
                                log.error("Error during request cancellation: ", e);
                                return;
                            }
                            // Simulating an empty message which will be handled by the UI as an error message, so we can keep track of errors
                            openSession.addMessage(new ChatGPTMessage(ChatGPTRole.system, ""));
                            messageWindowJPanel.addNewErrorMessage(e.getMessage());
                            try {
                                ChatPersistence.saveSessions(List.of(openSession));
                            } catch (Exception ex) {
                                log.error("Error saving session", ex);
                            }
                        });
                    }

                    @Override
                    public void onComplete() {
                        if (reqId != currentReqId.get()) {
                            log.info("Request id mismatch, ignoring result");
                            return;
                        }
                        SwingUtilities.invokeLater(() -> {
                            if (isCancelled) {
                                log.info("Request was cancelled, data should be saved already.");
                                isRequestingChatGPT = false;
                                return;
                            }
                            saveGPTMessage(updatedContent, streamMessageComponent);
                            isRequestingChatGPT = false;
                        });
                    }
                });
            });
            proxyThread.start();
        }

        private void saveGPTMessage(String[] updatedContent, StreamMessageJPanel streamMessageComponent) {
            String trimmedText = updatedContent[0].replaceAll("\\n+$", "");
            String finalHtmlContent = ParserService.markdownToHtml(trimmedText);
            streamMessageComponent.setText(finalHtmlContent);

            if (openSession.getMessages().size() >= 10) {
                log.info("Deleting oldest message");
                openSession.getMessages().remove(0);
                messageWindowJPanel.removeOldestMessage();
            }

            openSession.addMessage(new ChatGPTMessage(ChatGPTRole.system, trimmedText));
            try {
                ChatPersistence.saveSessions(List.of(openSession));
            } catch (Exception e) {
                log.error("Error saving session", e);
            }
        }

        private boolean canWeSendMessage(ChatGPTMessage message, JTextArea textField) {
            return !(message.getContent() == null || message.getContent().isEmpty() || message.getContent().equals("Message CodeEcho..."));
        }

        public void cancelOperation() {
            isCancelled = true;
        }

        public JPanel getMainContentPanel() {
            return mainContentPanel;
        }
    }
}
