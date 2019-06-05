package wesicknessdect.example.org.wescanleaf.events;

public class DoSigninEvent {
    public final String email;
    public final String password;

    public DoSigninEvent(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
