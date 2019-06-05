package wesicknessdect.example.org.wesicknessdetect.mvp.presenters;

public interface ILoginPresenter {
    void clear();
    void doLogin(String name, String passwd);
    void setProgressBarVisiblity(int visiblity);
}
