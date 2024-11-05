package me.vlink102.objects;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class Participant implements Serializable, Comparable<Participant> {
    @Serial
    private static final long serialVersionUID = -2675347376441266935L;

    private final transient UUID uniqueID;
    private final String name;

    private final Speed baseSpeed;
    private final float endurance;
/*
    public static class TrackedPanel extends JPanel {
        @Setter
        @Getter
        private int value;
        private final int i;
        public TrackedPanel(GridBagLayout layout, int i) {
            super();
            this.i = i;

            this.setPreferredSize(new Dimension(1000, 100));
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(i % 2 == 0 ? new Color(0, 120, 0, 120) : new Color(0, 150, 0, 150));
            g.fillRect(getX(), getY(), getWidth(), getHeight());
            g.setColor(Color.RED);
            g.fillOval(getX(), getY(), 10, 10);
        }
    }*/

    private transient PanelLink markerLink = null;

    public void linkMarker(PanelLink racePanel) {
        this.markerLink = racePanel;
    }

    public Participant(String name, Speed baseSpeed, float endurance) {
        this.uniqueID = UUID.randomUUID();
        this.name = name;
        this.baseSpeed = baseSpeed;
        this.endurance = endurance;
    }

    public Participant(String name, int minSpeed, int maxSpeed, float endurance) {
        this.uniqueID = UUID.randomUUID();
        this.name = name;
        this.baseSpeed = new Speed(minSpeed, maxSpeed);
        this.endurance = endurance;
    }

    public Participant(String name, Speed baseSpeed, float endurance, UUID uuid) {
        this.uniqueID = uuid;
        this.name = name;
        this.baseSpeed = baseSpeed;
        this.endurance = endurance;
    }

    public Participant(String name, int minSpeed, int maxSpeed, float endurance, UUID uuid) {
        this.uniqueID = uuid;
        this.name = name;
        this.baseSpeed = new Speed(minSpeed, maxSpeed);
        this.endurance = endurance;
    }

    @Override
    public int compareTo(Participant o) {
        return uniqueID.compareTo(o.getUniqueID());
    }

    @Override
    public String toString() {
        return "Participant{" +
                "uniqueID=" + uniqueID +
                ", name='" + name + '\'' +
                ", baseSpeed=" + baseSpeed +
                ", endurance=" + endurance +
                '}';
    }

    public static final String[] randomNames = {
            "Dog",
            "Cow",
            "Cat",
            "Horse",
            "Donkey",
            "Tiger",
            "Lion",
            "Panther",
            "Leopard",
            "Cheetah",
            "Bear",
            "Elephant",
            "Polar bear",
            "Turtle",
            "Tortoise",
            "Crocodile",
            "Rabbit",
            "Porcupine",
            "Hare",
            "Hen",
            "Pigeon",
            "Albatross",
            "Crow",
            "Fish",
            "Dolphin",
            "Frog",
            "Whale",
            "Alligator",
            "Eagle",
            "Flying squirrel",
            "Ostrich",
            "Fox",
            "Goat",
            "Jackal",
            "Emu",
            "Armadillo",
            "Eel",
            "Goose",
            "Arctic fox",
            "Wolf",
            "Beagle",
            "Gorilla",
            "Chimpanzee",
            "Monkey",
            "Beaver",
            "Orangutan",
            "Antelope",
            "Bat",
            "Badger",
            "Giraffe",
            "Hermit Crab",
            "Giant Panda",
            "Hamster",
            "Cobra",
            "Hammerhead shark",
            "Camel",
            "Hawk",
            "Deer",
            "Chameleon",
            "Hippopotamus",
            "Jaguar",
            "Chihuahua",
            "King Cobra",
            "Ibex",
            "Lizard",
            "Koala",
            "Kangaroo",
            "Iguana",
            "Llama",
            "Chinchillas",
            "Dodo",
            "Jellyfish",
            "Rhinoceros",
            "Hedgehog",
            "Zebra",
            "Possum",
            "Wombat",
            "Bison",
            "Bull",
            "Buffalo",
            "Sheep",
            "Meerkat",
            "Mouse",
            "Otter",
            "Sloth",
            "Owl",
            "Vulture",
            "Flamingo",
            "Racoon",
            "Mole",
            "Duck",
            "Swan",
            "Lynx",
            "Monitor lizard",
            "Elk",
            "Boar",
            "Lemur",
            "Mule",
            "Baboon",
            "Mammoth",
            "Blue whale",
            "Rat",
            "Snake",
            "Peacock"
    };

    public static Random random = ThreadLocalRandom.current();

    public static Participant generateRandom() {
        String randomName = randomNames[random.nextInt(randomNames.length)];
        int speedOne = random.nextInt(0, 100);
        int speedTwo = random.nextInt(0, 100);
        Speed randomSpeed = new Speed(Math.min(speedOne, speedTwo), Math.max(speedOne, speedTwo));
        float endurance = random.nextFloat(100);
        return new Participant(randomName, randomSpeed, endurance);
    }

    public static Object[] generateRandomRow() {
        return generateRandom().tableData();
    }

    public Object[] tableData() {
        return new Object[] {
                name,
                baseSpeed.min(),
                baseSpeed.max(),
                endurance,
                uniqueID
        };
    }
}
