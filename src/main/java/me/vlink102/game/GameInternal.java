package me.vlink102.game;

import lombok.Getter;
import me.vlink102.MainFrame;
import me.vlink102.objects.Participant;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.Consumer;

public class GameInternal {
    private final MainFrame mainFrame;

    public GameInternal(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.participants = new HashMap<>();
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
}
