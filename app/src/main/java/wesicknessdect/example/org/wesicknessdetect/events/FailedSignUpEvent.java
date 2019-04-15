package wesicknessdect.example.org.wesicknessdetect.events;

public class FailedSignUpEvent {
public final String msg;
public final String title;
public final Boolean cancelable;
    public FailedSignUpEvent(String msg, String title, Boolean cancelable) {
        this.msg = msg;
        this.title = title;
        this.cancelable = cancelable;
    }
}
