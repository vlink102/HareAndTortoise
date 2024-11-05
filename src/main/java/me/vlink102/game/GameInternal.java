package me.vlink102.game;

import lombok.Getter;
import lombok.Setter;
import me.vlink102.MainFrame;
import me.vlink102.internal.ConstraintBuilder;
import me.vlink102.internal.ResultsDialog;
import me.vlink102.objects.Participant;
import me.vlink102.objects.Speed;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class GameInternal {
    private final MainFrame mainFrame;
    @Getter
    private int rounds;
    @Setter
    private GameState state;

    @Getter
    private int raceLength;

    public enum GameState {
        PRE_INITIALIZATION,
        READY,
        STARTED,
        COMPLETED
    }

    public GameInternal(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.participants = new HashMap<>();
        this.rounds = 0;
        this.state = GameState.PRE_INITIALIZATION;
    }

    @Getter
    private final HashMap<UUID, Participant> participants;

    public void addParticipant(Participant participant) {
        this.participants.put(participant.getUniqueID(), participant);
    }

    public void removeParticipant(UUID uniqueID) {
        this.participants.remove(uniqueID);
    }

    public void clearParticipants() {
        this.participants.clear();
    }

    private RaceFrame frame = null;

    public void beginRace(RaceFrame frame) {
        this.frame = frame;
        this.beginRace(participants.values());
    }

    private static final Random random = ThreadLocalRandom.current();

    private static HashMap<Participant, Integer> raceScores;
    private static HashMap<Participant, Integer> bestRound;
    //private static HashMap<Integer, List<Participant>> winnerMap;
    private static List<UUID> currentWinners;

    private HashMap<Integer, List<Participant>> reduceKeys(HashMap<Integer, List<Participant>> map) {
        Map<Integer, List<Participant>> sortedMap = map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, _) -> e1,
                        TreeMap::new
                ));
        HashMap<Integer, List<Participant>> newMap = new HashMap<>();
        int newKey = 1;

        for (List<Participant> value : sortedMap.values()) {
            newMap.put(newKey++, value);
        }
        return newMap;
    }

    public void beginRace(Collection<Participant> participants) {
        this.raceLength = mainFrame.getRaceLength();

        raceScores = new HashMap<>();
        bestRound = new HashMap<>();
        participants.forEach(p -> bestRound.put(p, 0));
        //winnerMap = new HashMap<>();
        currentWinners = new ArrayList<>();

        final int[] participantsLeft = {participants.size()};

        state = GameState.STARTED;
        final List<List<Participant>> roundCompleted = new ArrayList<>();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (state == GameState.COMPLETED) {
                    timer.cancel();
                    ResultsDialog dialog = new ResultsDialog(frame, roundCompleted, bestRound);
                    return;
                }
                roundCompleted.add(new ArrayList<>());
                for (Participant participant : participants) {
                    if (currentWinners.contains(participant.getUniqueID())) continue;
                    Speed speed = participant.getBaseSpeed();
                    int distance = random.nextInt(speed.min(), speed.max() + 1);
                    int newDistance = raceScores.getOrDefault(participant, 0) + distance;
                    if (distance >= bestRound.get(participant)) {
                        bestRound.put(participant, distance);
                    }

                    if (newDistance >= raceLength) {
                        newDistance = raceLength;
                        // won
                        participantsLeft[0]--;
                        currentWinners.add(participant.getUniqueID());
                        roundCompleted.getFirst().add(participant);
                    } else {
                        raceScores.put(participant, newDistance);
                    }
                    participant.getMarkerLink().setValue(newDistance, raceScores.values().stream().max(Integer::compareTo).get() == raceScores.get(participant));

                    System.out.println("Participant: " + participant.getName() + " moved " + distance + " meters (" + participant.getMarkerLink().getInternalValue() + ")");
                }

                if (participantsLeft[0] <= 0) {
                    state = GameState.COMPLETED;
                } else {
                    rounds ++;
                }

                /*if (state == GameState.COMPLETED) {
                    timer.cancel();
                    //HashMap<Integer, List<Participant>> reducedWinnerMap = reduceKeys(winnerMap);

                    *//*reducedWinnerMap.forEach((integer, winners) -> {
                        if (winners.size() > 1) {
                            // tie
                            for (Participant winner : winners) {
                                int gridIndex = frame.getIndexMap().get(winner);
                                frame.getTracksPanel().add(new JLabel("#" + integer + " (tied)"), ConstraintBuilder.builder().setGridX(4).setGridY(gridIndex).setWeightY(1).setWeightX(1).setFill(GridBagConstraints.BOTH).build());
                            }
                        } else {
                            int gridIndex = frame.getIndexMap().get(winners.getFirst());
                            frame.getTracksPanel().add(new JLabel("#" + integer), ConstraintBuilder.builder().setGridX(4).setGridY(gridIndex).setWeightX(1).setWeightY(1).setFill(GridBagConstraints.BOTH).build());
                        }
                    });*//*

                    ResultsDialog resultsDialog = new ResultsDialog(frame, roundCompleted, true);


                    mainFrame.repaint();
                    return;
                }
                roundCompleted.add(new ArrayList<>());
                for (Participant participant : participants) {
                    if (currentWinners.contains(participant.getUniqueID())) continue;
                    Speed speed = participant.getBaseSpeed();
                    int distance = random.nextInt(speed.min(), speed.max() + 1);
                    int newDistance = raceScores.getOrDefault(participant, 0) + distance;
                    if (newDistance >= raceLength) {
                        // won
                        raceScores.put(participant, raceLength);
                        participantsLeft[0]--;
                        currentWinners.add(participant.getUniqueID());
                        roundCompleted.getLast().add(participant);
                    } else {
                        raceScores.put(participant, newDistance);
                    }
                    participant.getMarkerLink().setValue(newDistance, raceScores.values().stream().max(Integer::compareTo).get() == raceScores.get(participant));

                    System.out.println("Participant: " + participant.getName() + " moved " + distance + " meters (" + participant.getMarkerLink().getInternalValue() + ")");
                }

                *//*int max = raceScores.values().stream().max(Integer::compareTo).get();

                List<Participant> roundWinners = new ArrayList<>();

                raceScores.forEach((participant, integer) -> {
                    if (integer == max) {
                        roundWinners.add(participant);
                    }
                });

                winnerMap.put(rounds, roundWinners);*//*
                if (participantsLeft[0] <= 0) {
                    state = GameState.COMPLETED;
                } else {
                    rounds ++;
                }
*/
            }
        }, 0, mainFrame.getRoundIntervalMs());
    }
}
