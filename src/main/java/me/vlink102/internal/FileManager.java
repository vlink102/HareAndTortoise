package me.vlink102.internal;

import me.vlink102.Main;
import me.vlink102.MainFrame;
import me.vlink102.game.RaceFrame;
import me.vlink102.objects.Participant;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private final MainFrame main;

    public FileManager(MainFrame main) {
        this.main = main;
    }

    public void serializeParticipants(ArrayList<Participant> participants) {
        JFileChooser chooser = new JFileChooser();
        chooser.showSaveDialog(null);
        try (
                FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(participants);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException ioe) {
            System.out.println("Error while writing data : " + ioe);
            ioe.printStackTrace();
        }
    }

    public ArrayList<Participant> deserializeParticipants() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(null);
        ArrayList<Participant> participants;
        if (result == JFileChooser.APPROVE_OPTION) {
            try (
                    FileInputStream fis = new FileInputStream(chooser.getSelectedFile());
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ) {
                participants = (ArrayList) ois.readObject();
                for (Participant participant : participants) {
                    System.out.println(participant);
                }
                return participants;
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return new ArrayList<>();
    }
}
