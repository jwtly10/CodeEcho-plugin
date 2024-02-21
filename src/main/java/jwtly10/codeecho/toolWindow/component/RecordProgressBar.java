package jwtly10.codeecho.toolWindow.component;

import javax.swing.*;
import java.awt.*;

public class RecordProgressBar extends JPanel {
    private JProgressBar progressBar;
    private Timer timer;

    private JLabel timeLabel;

    private int duration;
    private int interval = 100;
    private int elapsed = 0;

    public RecordProgressBar(int dur) {
        this.duration = dur;

        setLayout(new BorderLayout());
        progressBar = new JProgressBar(0, dur);
        progressBar.setValue(0);
        progressBar.setStringPainted(false);

        add(progressBar, BorderLayout.CENTER);

        timeLabel = new JLabel(String.format("00:%02d/0:%02d", 0, dur / 1000));
        add(timeLabel, BorderLayout.EAST);

        timer = new Timer(interval, e -> updateProgressBar());
    }

    public void startRecording() {
        elapsed = 0;
        progressBar.setValue(0);
        timer.start();
        System.out.println("Recording started...");
    }

    private void updateProgressBar() {
        elapsed += interval;
        progressBar.setValue(elapsed);

        int secondsElapsed = elapsed / 1000;
        timeLabel.setText(String.format("00:%02d/0:%02d", secondsElapsed, duration / 1000));

        if (elapsed >= duration) {
            timer.stop();

            // Hack to make the progress bar reach the end
            timeLabel.setText(String.format("0:%02d/0:%02d", duration / 1000, duration / 1000));

            System.out.println("Recording stopped.");
        }
    }

    public void stopRecording() {
        timer.stop();
        progressBar.setValue(duration);
        System.out.println("Recording manually stopped.");
    }
}
