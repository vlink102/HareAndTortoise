package me.vlink102.game;

import lombok.Getter;
import me.vlink102.MainFrame;
import me.vlink102.internal.ConstraintBuilder;
import me.vlink102.objects.PanelLink;
import me.vlink102.objects.Participant;
import me.vlink102.objects.ui.ClickableComponent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RaceFrame extends JFrame implements Serializable, Comparable<RaceFrame> {
    @Serial
    private static final long serialVersionUID = 4103204532419817535L;

    private final transient GameInternal internal;
    private final transient MainFrame mainFrame;
    private final transient List<Participant> participantList;
    private final UUID uuid;
    @Getter
    private final transient HashMap<Participant, Integer> indexMap;
    @Getter
    private JPanel tracksPanel = null;

    public RaceFrame(GameInternal internal, MainFrame mainFrame) {
        this(UUID.randomUUID(), internal, mainFrame);
    }

    public RaceFrame(UUID uuid, GameInternal internal, MainFrame mainFrame) {
        super("Race (" + uuid + ")");
        this.uuid = uuid;
        this.internal = internal;
        this.mainFrame = mainFrame;
        this.participantList = internal.getParticipants().values().stream().toList();
        this.indexMap = new HashMap<>();
        initialiseFrame();
        internal.setState(GameInternal.GameState.READY);
    }

    public void initialiseFrame() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                RaceFrame.this.setVisible(false);
                RaceFrame.this.dispose();
                mainFrame.requestFocus();
            }
        });

        mainFrame.setTitle("Game in progress");
        Container contentPane = getContentPane();
        int participantCount = internal.getParticipants().values().size();

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        final JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(1).build();
        mainPanel.add(topPanel, constraints);

        final JLabel roundsLabel = new JLabel("Rounds: " + internal.getRounds());
        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(1).build();
        topPanel.add(roundsLabel, constraints);

        final JButton startButton = ClickableComponent.of("Start", component -> {
            component.setText("Started");
            component.setEnabled(false);
            internal.beginRace(this);
        });
        constraints = ConstraintBuilder.builder().setGridX(1).setGridY(0).setWeightX(1).setAnchor(GridBagConstraints.WEST).build();
        topPanel.add(startButton, constraints);

        tracksPanel = new JPanel();
        tracksPanel.setLayout(new GridBagLayout());
        constraints = ConstraintBuilder.builder().setGridX(1).setGridY(0).setFill(GridBagConstraints.HORIZONTAL).build();
        mainPanel.add(tracksPanel, constraints);

        for (int i = 0; i < participantCount; i++) {

            Participant participant = participantList.get(i);

            indexMap.put(participant, i);

            final JLabel nameLabel = new JLabel(participant.getName());
            nameLabel.setBorder(new EmptyBorder(0, 5, 0, 10));
            constraints = ConstraintBuilder.builder().setGridX(0).setGridY(i).setWeightX(0).setWeightY(0).setAnchor(GridBagConstraints.WEST).build();
            tracksPanel.add(nameLabel, constraints);

            final JPanel startLine = new JPanel();
            startLine.setBackground(Color.yellow);
            startLine.setPreferredSize(new Dimension(100 / 8, 100));
            constraints = ConstraintBuilder.builder().setGridX(1).setGridY(i).setWeightX(0).setWeightY(0).setAnchor(GridBagConstraints.NORTHWEST).build();
            tracksPanel.add(startLine, constraints);

            final PanelLink trackPanel = new PanelLink(mainFrame, i);
            participant.linkMarker(trackPanel);

            trackPanel.setPreferredSize(new Dimension(1000, 100));

            constraints = ConstraintBuilder.builder().setGridX(2).setGridY(i).setWeightX(0).setWeightY(0).setAnchor(GridBagConstraints.NORTHWEST).build();
            tracksPanel.add(trackPanel, constraints);

            final JPanel finishLine = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    int smallerSize = 100 / 8;
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 8; j++) {
                            g.setColor((i + j) % 2 == 0 ? Color.darkGray : Color.white);
                            g.fillRect(i * smallerSize, j * smallerSize, smallerSize, smallerSize);
                        }
                    }
                }
            };
            finishLine.setPreferredSize(new Dimension((100 / 8) * 2, 100));
            constraints = ConstraintBuilder.builder().setGridX(3).setGridY(i).setWeightX(0).setWeightY(0).setAnchor(GridBagConstraints.NORTHWEST).build();
            tracksPanel.add(finishLine, constraints);


        }

        final JScrollPane pane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(1).setWeightY(1).setFill(GridBagConstraints.BOTH).build();
        mainPanel.add(pane, constraints);

        pane.getViewport().add(tracksPanel);
        pane.getVerticalScrollBar().setUnitIncrement(10);

        contentPane.add(mainPanel);
        pack();
        setVisible(true);
    }

    @Override
    public int compareTo(RaceFrame o) {
        return uuid.compareTo(o.uuid);
    }

    @Getter
    public static class AnimatedSlider extends JPanel {
        private final int minValue;
        private final int maxValue;
        private final float scalar;
        private int value;

        public AnimatedSlider(int value, int minValue, int maxValue) {
            super();
            this.value = value;
            this.minValue = minValue;
            this.maxValue = Math.min(maxValue, 1000);
            this.scalar = this.maxValue / (float) maxValue;
            setPreferredSize(new Dimension(this.maxValue, 10));
        }

        private int convertValue(int initial) {
            return (int) (initial * scalar);
        }

        public void setValue(int value) {
            this.value = value;
            //animate(new Point(convertValue(value), getY()), 50);
            setLocation(value, getY());
        }

        private void animate(Point newPoint, int interval) {
            animate(newPoint, 60, interval);
        }

        private void animate(Point newPoint, int frames, int interval) {
            Rectangle compBounds = getBounds();
            Point oldPoint = new Point(compBounds.x, compBounds.y);
            Point animFrame = new Point((newPoint.x - oldPoint.x) / frames, (newPoint.y - oldPoint.y) / frames);

            new Timer(interval, new ActionListener() {
                int currentFrame = 0;

                public void actionPerformed(ActionEvent e) {
                    setBounds(oldPoint.x + (animFrame.x * currentFrame), oldPoint.y + (animFrame.y * currentFrame), compBounds.width, compBounds.height);

                    if (currentFrame != frames) currentFrame++;
                    else ((Timer) e.getSource()).stop();
                }
            }).start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(Color.red);
            g.fillOval(0, 0, 10, 10);
        }
    }
}
