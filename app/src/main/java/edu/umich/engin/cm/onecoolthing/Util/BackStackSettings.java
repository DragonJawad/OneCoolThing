package edu.umich.engin.cm.onecoolthing.Util;

import edu.umich.engin.cm.onecoolthing.Core.ActivityMain;

/**
 * Created by jawad on 05/12/14.
 *
 * Represents the settings to restore when pressing the back button
 */
public class BackStackSettings {
    // Describes the previous systemSettings that were necessary
    ActivityMain.SettingsType previousSettings;
    // Describes the previous fragment's id
    int previousFragPosition;

    // Default constructor
    public BackStackSettings() {}

    // Custom constructor, must give the settings type and previous position
    public BackStackSettings(ActivityMain.SettingsType settingsType, int previousPosition) {
        this.previousSettings = settingsType;
        this.previousFragPosition = previousPosition;
    }

    public ActivityMain.SettingsType getPreviousSettings() {
        return previousSettings;
    }

    public void setPreviousSettings(ActivityMain.SettingsType previousSettings) {
        this.previousSettings = previousSettings;
    }

    public int getPreviousFragPosition() {
        return previousFragPosition;
    }

    public void setPreviousFragPosition(int previousFragPosition) {
        this.previousFragPosition = previousFragPosition;
    }
}
