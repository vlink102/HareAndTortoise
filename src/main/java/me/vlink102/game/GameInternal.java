package me.vlink102.game;

import lombok.Getter;
import lombok.Setter;
import me.vlink102.MainFrame;
import me.vlink102.objects.Participant;

import java.util.*;
import java.util.function.Consumer;

public class GameInternal {
    private final MainFrame mainFrame;
    @Getter
    private int rounds;
    @Setter
    private GameState state;

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

    public void beginRace() {
        this.beginRace(participants.values());
    }

    public void beginRace(Collection<Participant> participants) {
        for (Participant participant : participants) {

        }
    }
}
