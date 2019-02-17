package net.minecord.beautyindicator.model;

public class Combat {

    private String nameToRestore;
    private int insertedSeconds;
    private int seconds;

    public Combat(String nameToRestore, int seconds) {
        this.nameToRestore = nameToRestore;
        this.insertedSeconds = seconds;
        this.seconds = seconds;
    }

    public int getSeconds() {
        return seconds;
    }

    public void resetSeconds() {
        seconds = insertedSeconds;
    }

    public String getNameToRestore() {
        return nameToRestore;
    }

    public void doUpdate() {
        seconds--;
    }
}