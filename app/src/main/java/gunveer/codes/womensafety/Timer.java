package gunveer.codes.womensafety;

import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.List;
import java.util.Map;

public class Timer {
    int hour, minutes, missedTimer;
    boolean toggleOn, excludeLocation;
    Map<String, String> lastClickedPhoto;
    String message, label;
    List<Contact> contactsToAlert;
    Location lastLocation;


    public Timer(int hour, String label, int minutes, int missedTimer, boolean toggleOn, Map<String, String> lastClickedPhoto, String message, List<Contact> contactsToAlert,
                 Location lastLocation, boolean excludeLocation) {
        this.hour = hour;
        this.label = label;
        this.minutes = minutes;
        this.missedTimer = missedTimer;
        this.toggleOn = toggleOn;
        this.lastClickedPhoto = lastClickedPhoto;
        this.message = message;
        this.contactsToAlert = contactsToAlert;
        this.lastLocation = lastLocation;
        this.excludeLocation = excludeLocation;
    }


    public Timer() {

    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isExcludeLocation() {
        return excludeLocation;
    }

    public void setExcludeLocation(boolean excludeLocation) {
        this.excludeLocation = excludeLocation;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }


    public int getMissedTimer() {
        return missedTimer;
    }

    public void setMissedTimer(int missedTimer) {
        this.missedTimer = missedTimer;
    }

    public boolean isToggleOn() {
        return toggleOn;
    }

    public void setToggleOn(boolean toggleOn) {
        this.toggleOn = toggleOn;
    }

    public Map<String, String> getLastClickedPhoto() {
        return lastClickedPhoto;
    }

    public void setLastClickedPhoto(Map<String, String> lastClickedPhoto) {
        this.lastClickedPhoto = lastClickedPhoto;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Contact> getContactsToAlert() {
        return contactsToAlert;
    }

    public void setContactsToAlert(List<Contact> contactsToAlert) {
        this.contactsToAlert = contactsToAlert;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }
}
