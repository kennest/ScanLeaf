package wesicknessdect.example.org.wesicknessdetect.events;

public class ShowLoadingEvent {
    public final String title;
    public final String content;
    public final boolean cancelable;
    public final int type;

    public ShowLoadingEvent(String title, String content, boolean cancelable, int type) {
        this.title = title;
        this.content = content;
        this.cancelable = cancelable;
        this.type = type;
    }
}
