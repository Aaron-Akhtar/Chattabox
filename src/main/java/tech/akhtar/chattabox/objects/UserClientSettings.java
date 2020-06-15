package tech.akhtar.chattabox.objects;

import tech.akhtar.chattabox.utils.Colour;

public class UserClientSettings {

    private Colour usernameColour;
    private Colour messageColour;

    public UserClientSettings(Colour usernameColour, Colour messageColour) {
        this.usernameColour = usernameColour;
        this.messageColour = messageColour;
    }

    public UserClientSettings() {
        usernameColour = Colour.CYAN;
        messageColour = Colour.WHITE;
    }

    public Colour getUsernameColour() {
        return usernameColour;
    }

    public void setUsernameColour(Colour usernameColour) {
        this.usernameColour = usernameColour;
    }

    public Colour getMessageColour() {
        return messageColour;
    }

    public void setMessageColour(Colour messageColour) {
        this.messageColour = messageColour;
    }
}
