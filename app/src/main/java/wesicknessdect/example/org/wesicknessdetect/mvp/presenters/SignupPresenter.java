package wesicknessdect.example.org.wesicknessdetect.mvp.presenters;

import android.content.Context;
import android.view.View;
import java.util.concurrent.ExecutionException;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.models.User;
import wesicknessdect.example.org.wesicknessdetect.tasks.RemoteTasks;
import wesicknessdect.example.org.wesicknessdetect.mvp.view.ISignupView;

public class SignupPresenter implements ISignupPresenter {
    private ISignupView signupView;
    private View v;
    private Context ctx;

    public SignupPresenter(ISignupView signupView, View view, Context context) {
        this.signupView = signupView;
        this.v = view;
        this.ctx = context;
        ButterKnife.bind(this,v);
    }


    @Override
    public void doSignup(User u) {
        RemoteTasks.getInstance(ctx).doSignUp(u);
        signupView.onSignupResuslt("Finished");
    }
}
