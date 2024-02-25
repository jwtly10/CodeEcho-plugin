package jwtly10.codeecho.toolWindow.component;

import javax.swing.*;
import java.awt.*;

public class ProgressBarJPanel extends JPanel {
    private final JProgressBar progressBar;
    private final Timer timer;
    private final JLabel timeLabel;
    private int duration;
    private final int interval = 100;
    private int elapsed = 0;

    /**
     * Creates a progress bar with the given duration
     *
     * @param dur duration in milliseconds
     */
    public ProgressBarJPanel(int dur) {
        this.duration = dur;

        setLayout(new BorderLayout());
        progressBar = new JProgressBar(0, dur);
        progressBar.setValue(0);
        progressBar.setStringPainted(false);

        add(progressBar, BorderLayout.CENTER);

        timeLabel = new JLabel(String.format("00:%02d/0:%02d", 0, dur / 1000));
        add(timeLabel, BorderLayout.EAST);

        timer = new Timer(interval, e -> update());
    }

    /**
     * Updates the duration of the progress bar, in seconds
     *
     * @param seconds duration in seconds
     */
    public void updateDuration(double seconds) {
        duration = (int) (Math.ceil(seconds) * 1000);
        progressBar.setMaximum(duration);

        timeLabel.setText(String.format("00:%02d/0:%02d", 0, (int) Math.ceil(seconds)));
    }

    /**
     * Starts the progress bar
     */
    public void start() {
        elapsed = 0;
        progressBar.setValue(0);
        timer.start();
    }

    private void update() {
        elapsed += interval;
        progressBar.setValue(elapsed);

        int secondsElapsed = elapsed / 1000;
        timeLabel.setText(String.format("00:%02d/0:%02d", secondsElapsed, duration / 1000));

        if (elapsed >= duration) {
            timer.stop();
            // Hack to make the progress bar reach the end
            timeLabel.setText(String.format("0:%02d/0:%02d", duration / 1000, duration / 1000));
        }
    }

    /**
     * Stops the progress bar
     */
    public void stop() {
        timer.stop();
        progressBar.setValue(duration);
    }

    /**
     * Resets the progress bar
     */
    public void reset() {
        timer.stop();
        progressBar.setValue(0);
        timeLabel.setText(String.format("00:%02d/0:%02d", 0, duration / 1000));
    }
}
