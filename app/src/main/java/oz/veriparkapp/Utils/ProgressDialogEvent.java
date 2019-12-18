package oz.veriparkapp.Utils;

public class ProgressDialogEvent {

    public enum EventType { START, STOP }

    private EventType eventType;
    private String eventMessage;

    public ProgressDialogEvent(EventType eventType) {
        this.eventType = eventType;
    }

    public ProgressDialogEvent(EventType eventType, String eventMessage) {
        this.eventType = eventType;
        this.eventMessage = eventMessage;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType value) {
        this.eventType = value;
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }
}
