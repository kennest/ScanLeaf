package wesicknessdect.example.org.wesicknessdetect.events;

public class ShowLoadingEvent {
    public final String title;
    public final String content;
    public final boolean cancelable;

    public ShowLoadingEvent(String title, String content, boolean cancelable) {
        this.title = title;
        this.content = content;
        this.cancelable = cancelable;
    }
}
