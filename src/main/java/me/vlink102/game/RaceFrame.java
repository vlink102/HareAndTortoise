package me.vlink102.game;

import me.vlink102.MainFrame;
import me.vlink102.internal.ConstraintBuilder;
import me.vlink102.objects.Participant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RaceFrame extends JFrame implements Serializable, Comparable<RaceFrame> {
    @Serial
    private static final long serialVersionUID = 4103204532419817535L;

    private final transient GameInternal internal;
    private final transient MainFrame mainFrame;
    private final transient List<Participant> participantList;
    private final UUID uuid;
    private final long startTime;

    public RaceFrame(GameInternal internal, MainFrame mainFrame) {
        this(UUID.randomUUID(), internal, mainFrame);
    }

    public RaceFrame(UUID uuid, GameInternal internal, MainFrame mainFrame) {
        super("Race (" + uuid + ")");
        this.uuid = uuid;
        this.startTime = System.currentTimeMillis();
        this.internal = internal;
        this.mainFrame = mainFrame;
        this.participantList = internal.getParticipants().values().stream().toList();
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
        final JLabel roundsLabel = new JLabel("Rounds: " + internal.getRounds());
        GridBagConstraints constraints = ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(1).build();
        mainPanel.add(roundsLabel, constraints);

        final JPanel tracksPanel = new JPanel();
        tracksPanel.setLayout(new GridBagLayout());
        constraints = ConstraintBuilder.builder().setGridX(1).setGridY(0).setFill(GridBagConstraints.HORIZONTAL).build();
        mainPanel.add(tracksPanel, constraints);

        for (int i = 0; i < participantCount; i++) {
            Participant participant = participantList.get(i);
//            constraints = ConstraintBuilder.builder().setGridX(0).setGridY(i).build();
            //tracksPanel.add(new TrackPanel(participant, i, 100), constraints);
            final JLabel nameLabel = new JLabel(participant.getName()) {
            };
            nameLabel.setFont(nameLabel.getFont().deriveFont(20f));
            constraints = ConstraintBuilder.builder().setGridX(0).setGridY(i).setWeightX(0.1f).setWeightY(1).setFill(GridBagConstraints.BOTH).build();
            tracksPanel.add(nameLabel, constraints);

            final JPanel startLine = new JPanel();
            startLine.setPreferredSize(new Dimension(-1, 100));
            startLine.setBackground(Color.yellow);
            constraints = ConstraintBuilder.builder().setGridX(1).setGridY(i).setWeightX(0.05f).setWeightY(1).setFill(GridBagConstraints.BOTH).build();
            tracksPanel.add(startLine, constraints);
            final JPanel raceTrack = new JPanel();
            raceTrack.setPreferredSize(new Dimension(1000, 100));
            raceTrack.setBackground(i % 2 == 0 ? new Color(0, 150, 0, 120) : new Color(0, 180, 0, 150));
            constraints = ConstraintBuilder.builder().setGridX(2).setGridY(i).setWeightX(0.8f).setWeightY(1).setFill(GridBagConstraints.BOTH).build();

            tracksPanel.add(raceTrack, constraints);

            final JPanel finishLine = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    int smaller = 100 / 4;
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 8; j++) {
                            g.setColor((i + j) % 2 == 0 ? Color.white : Color.darkGray);
                            g.fillRect(i * smaller, j * smaller, smaller, smaller);
                        }
                    }
                }
            };
            constraints = ConstraintBuilder.builder().setGridX(3).setGridY(i).setWeightX(0.05f).setWeightY(1).setFill(GridBagConstraints.BOTH).build();
            tracksPanel.add(finishLine, constraints);
        }

        final JScrollPane pane = new JScrollPane();
        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(1).setWeightY(1).setFill(GridBagConstraints.BOTH).build();
        mainPanel.add(pane, constraints);

        pane.setViewportView(tracksPanel);

        contentPane.add(mainPanel);
        pack();
        setVisible(true);
    }

    public class TrackPanel extends JPanel {
        public TrackPanel(Participant participant, int counter, int size) {
            super(new GridBagLayout());









        }
    }

    public void initializeFrame() {
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

        final JPanel raceGraphics = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                int heightOfBar = contentPane.getHeight() / participantCount;
                if (heightOfBar > 100) heightOfBar = 100;
                for (int i = 0; i < participantCount; i++) {
                    int x = 0;
                    int y = i * heightOfBar;

                    g.setColor(i % 2 == 0 ? new Color(0, 180, 0, 120) : new Color(0, 130, 0, 120));
                    g.fillRect(x, y, contentPane.getWidth(), heightOfBar);
                }
            }
        };
        //raceGraphics.setPreferredSize(new Dimension(-1, Math.min(100, contentPane.getHeight() / participantCount)));
        GridBagConstraints constraints = ConstraintBuilder.builder().setGridX(2).setGridY(0).setWeightY(1).setWeightX(0.8f).setFill(GridBagConstraints.BOTH).build();
        mainPanel.add(raceGraphics, constraints);

        final JPanel startLine = new JPanel() {
            Font font = getFont().deriveFont(14f);
            @Override
            protected void paintComponent(Graphics g) {
                int heightOfBar = contentPane.getHeight() / participantCount;
                if (heightOfBar > 100) heightOfBar = 100;
                for (int i = 0; i < participantCount; i++) {
                    Participant participant = participantList.get(i);
                    g.setFont(font);
                    g.drawString(participant.getName(), 0, (i * heightOfBar) + (heightOfBar / 2));
                }
            }
        };
        final JPanel startLineGraphics = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                int heightOfBar = contentPane.getHeight() / participantCount;
                if (heightOfBar > 100) heightOfBar = 100;
                g.setColor(Color.YELLOW);
                g.fillRect(0, 0, 10, heightOfBar * participantCount);
            }
        };
/*
        startLine.setLayout(new GridBagLayout());
        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(1).setWeightY(1).setFill(GridBagConstraints.HORIZONTAL).build();
        for (int i = 0; i < participantCount; i++) {
            Participant participant = internal.getParticipants().values().stream().toList().get(i);
            String name = participant.getName();
            JLabel contestantLabel = new JLabel(name);
            constraints.gridy = i;
            startLine.add(contestantLabel, constraints);
        }*/
        constraints = ConstraintBuilder.builder().setGridX(1).setGridY(0).setWeightY(1).setWeightX(0).setFill(GridBagConstraints.VERTICAL).build();
        mainPanel.add(startLineGraphics, constraints);

        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightY(1).setWeightX(0.1f).setFill(GridBagConstraints.BOTH).build();
        mainPanel.add(startLine, constraints);

        final JPanel finishLine = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                int heightOfBar = contentPane.getHeight() / participantCount;
                if (heightOfBar > 100) heightOfBar = 100;
                for (int i = 0; i < participantCount; i++) {
                    int x = 0;
                    int y = i * heightOfBar;
                    int sizeOfSmallRect = heightOfBar / 8;
                    for (int j = 0; j < 2; j++) {
                        for (int k = 0; k < 8; k++) {
                            g.setColor((j + k) % 2 == 0 ? Color.white : Color.darkGray);

                            g.fillRect(j * sizeOfSmallRect, y + (k * sizeOfSmallRect), sizeOfSmallRect, sizeOfSmallRect);
                        }
                    }
                }
            }
        };

        constraints = ConstraintBuilder.builder().setGridX(3).setGridY(0).setWeightY(1).setWeightX(0.1f).setFill(GridBagConstraints.BOTH).build();
        mainPanel.add(finishLine, constraints);

        final JLabel roundsLabel = new JLabel("Rounds: " + internal.getRounds());
        roundsLabel.setFont(roundsLabel.getFont().deriveFont(20f));
        contentPane.setLayout(new GridBagLayout());
        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(1).setWeightY(0).setAnchor(GridBagConstraints.CENTER).build();

        contentPane.add(roundsLabel, constraints);
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;

        contentPane.add(mainPanel, constraints);
        //raceGraphics.setPreferredSize(new Dimension(500, Math.min(participantCount * 100, 720)));
        setPreferredSize(new Dimension(1080, 720));
        pack();
        setVisible(true);
        //raceGraphics.repaint();
    }

    @Override
    public int compareTo(RaceFrame o) {
        return 0;
    }
}
