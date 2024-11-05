package me.vlink102.objects;

import lombok.Getter;
import me.vlink102.MainFrame;
import me.vlink102.internal.ConstraintBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class PanelLink extends JPanel {
    @Getter
    private int internalValue;

    private final JSlider slider;
    private volatile boolean isAnimating;
    private volatile boolean isCancelled;

    private Timer timer;
    private final MainFrame raceLength;


    public PanelLink(MainFrame frame, int i) {
        super(new GridBagLayout());
        this.raceLength = frame;
        setOpaque(true);
        setBackground(i % 2 == 0 ? new Color(0, 120, 0) : new Color(0, 150, 0));
        slider = new JSlider(JSlider.HORIZONTAL, 0, frame.getRaceLength(), 0);
        slider.setForeground(Color.red);
        timer = new Timer();
        this.add(slider, ConstraintBuilder.builder().setGridY(0).setGridX(0).setWeightX(1).setWeightY(1).setFill(GridBagConstraints.HORIZONTAL).build());
    }

    public void setValue(int value, boolean winner) {
        if (winner && (value >= raceLength.getRaceLength())) {
            slider.setForeground(Color.YELLOW);
        } else {
            slider.setForeground(winner ? Color.GREEN : Color.RED);
        }
        this.internalValue = value;

        if (isAnimating) {
            timer.cancel();
            isCancelled = true;
        }
        animate(slider, slider.getValue(), value);
    }

    // 50ms
    private void animate(JSlider slider, int from, int to) {
        int step = (to - from) / 20;
        if (isCancelled) {
            timer = new Timer();
            isCancelled = false;
        }
        slider.setValueIsAdjusting(true);
        timer.schedule(new TimerTask() {
            int current = from;
            @Override
            public void run() {
                isAnimating = true;
                slider.setValue(current + step);
                current += step;
                if (current >= to) {
                    timer.cancel();
                    isCancelled = true;
                }
            }
        }, 0, 50);
        slider.setValueIsAdjusting(false);
        isAnimating = false;
    }
}
