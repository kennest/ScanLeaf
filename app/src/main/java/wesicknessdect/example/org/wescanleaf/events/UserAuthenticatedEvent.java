package wesicknessdect.example.org.wescanleaf.events;

public class UserAuthenticatedEvent {
    public final String token;

    public UserAuthenticatedEvent(String token) {
        this.token = token;
    }
}
