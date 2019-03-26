package wesicknessdect.example.org.wesicknessdetect.events;

public class UserAuthenticatedEvent {
    public final String token;

    public UserAuthenticatedEvent(String token) {
        this.token = token;
    }
}
