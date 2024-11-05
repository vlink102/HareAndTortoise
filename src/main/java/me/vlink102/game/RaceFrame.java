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
    private final long startTime;
    @Getter
    private final transient HashMap<Participant, Integer> indexMap;

    public RaceFrame(GameInternal internal, MainFrame mainFrame) {
        this(UUID.randomUUID(), internal, mainFrame);
    }

    @Getter
    public class AnimatedSlider extends JPanel {
        private int value;
        private final int minValue;
        private final int maxValue;

        private final float scalar;

        private int convertValue(int initial) {
            return (int) (initial * scalar);
        }

        public AnimatedSlider(int value, int minValue, int maxValue) {
            super();
            this.value = value;
            this.minValue = minValue;
            this.maxValue = Math.min(maxValue, 1000);
            this.scalar = this.maxValue / (float) maxValue;
            setPreferredSize(new Dimension(this.maxValue, 10));
        }

        public void setValue(int value) {
            this.value = value;
            //animate(new Point(convertValue(value), getY()), 50);
            setLocation(value, getY());
        }

        private void animate(Point newPoint, int interval) {
            animate(newPoint, 60,  interval);
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
            g.fillOval(0,0, 10, 10);
        }
    }

    public RaceFrame(UUID uuid, GameInternal internal, MainFrame mainFrame) {
        super("Race (" + uuid + ")");
        this.uuid = uuid;
        this.startTime = System.currentTimeMillis();
        this.internal = internal;
        this.mainFrame = mainFrame;
        this.participantList = internal.getParticipants().values().stream().toList();
        this.indexMap = new HashMap<>();
        initialiseFrame();
        internal.setState(GameInternal.GameState.READY);
    }

    @Getter
    private JPanel tracksPanel = null;

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
            /*Participant participant = participantList.get(i);
//            constraints = ConstraintBuilder.builder().setGridX(0).setGridY(i).build();
            //tracksPanel.add(new TrackPanel(participant, i, 100), constraints);
            final JLabel nameLabel = new JLabel(participant.getName()) {
            };
            nameLabel.setFont(nameLabel.getFont().deriveFont(20f));
            constraints = ConstraintBuilder.builder().setGridX(0).setGridY(i).setWeightX(0.1f).setWeightY(1).setFill(GridBagConstraints.HORIZONTAL).build();
            tracksPanel.add(nameLabel, constraints);

            final JPanel startLine = new JPanel();

            startLine.setBackground(Color.yellow);
            constraints = ConstraintBuilder.builder().setGridX(1).setGridY(i).setWeightX(0.05f).setWeightY(1).setFill(GridBagConstraints.BOTH).build();
            tracksPanel.add(startLine, constraints);
            final JPanel raceTrack = new JPanel() {
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(1000, 100);
                }
            };



            raceTrack.setBackground(i % 2 == 0 ? new Color(0, 150, 0, 120) : new Color(0, 180, 0, 150));
            constraints = ConstraintBuilder.builder().setGridX(2).setGridY(i).setWeightX(0.8f).setWeightY(1).setFill(GridBagConstraints.HORIZONTAL).build();

            tracksPanel.add(raceTrack, constraints);

            final JPanel finishLine = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    int smaller = 100 / 8;
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 8; j++) {
                            g.setColor((i + j) % 2 == 0 ? Color.white : Color.darkGray);
                            g.fillRect(i * smaller, j * smaller, smaller, smaller);
                        }
                    }
                }
            };
            constraints = ConstraintBuilder.builder().setGridX(3).setGridY(i).setWeightX(0.05f).setWeightY(1).setFill(GridBagConstraints.BOTH).build();
            tracksPanel.add(finishLine, constraints);*/
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


            /*
            final AnimatedSlider slider = new AnimatedSlider(0, 0, internal.getRaceLength());
            participant.linkMarker(slider);
            constraints = ConstraintBuilder.builder().setAnchor(GridBagConstraints.WEST).setWeightY(0).setWeightX(1).setFill(GridBagConstraints.HORIZONTAL).setGridY(0).setGridX(0).build();
            trackPanel.add(slider, constraints);
*/

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
        //tracksPanel.setPreferredSize(new Dimension(1000 + ((100 / 8) * 3) + ));

        final JScrollPane pane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(1).setWeightY(1).setFill(GridBagConstraints.BOTH).build();
        mainPanel.add(pane, constraints);

        pane.getViewport().add(tracksPanel);
        pane.getVerticalScrollBar().setUnitIncrement(10);

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
        return uuid.compareTo(o.uuid);
    }
}
