package wesicknessdect.example.org.wesicknessdetect.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import wesicknessdect.example.org.wesicknessdetect.R;

public class QuizActivity extends BaseActivity {


    private TextView mScoreView;
    private TextView mQuestionView;
    private Button mButtonChoice1;
    private Button mButtonChoice2;
    private Button mButtonChoice3;

    private String mAnswer;
    private int mScore = 0;
    private int mQuestionNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        mScoreView = (TextView)findViewById(R.id.score);
        mQuestionView = (TextView)findViewById(R.id.question);
        mButtonChoice1 = (Button)findViewById(R.id.choice1);
        mButtonChoice2 = (Button)findViewById(R.id.choice2);
        mButtonChoice3 = (Button)findViewById(R.id.choice3);


        //Start of Button Listener for Button1
        mButtonChoice1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //My logic for Button goes in here

                if (mButtonChoice1.getText() == mAnswer){
                    mScore = mScore + 1;
                    updateScore(mScore);

                //This line of code is optiona
//                    Toast.makeText(QuizActivity.this, "correct", Toast.LENGTH_SHORT).show();

                }else {
//                    Toast.makeText(QuizActivity.this, "wrong", Toast.LENGTH_SHORT).show();

                }
            }
        });

        //End of Button Listener for Button1

        //Start of Button Listener for Button2
        mButtonChoice2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //My logic for Button goes in here

                if (mButtonChoice2.getText() == mAnswer){
                    mScore = mScore + 1;
                    updateScore(mScore);

                    //This line of code is optiona
//                    Toast.makeText(QuizActivity.this, "correct", Toast.LENGTH_SHORT).show();

                }else {
//                    Toast.makeText(QuizActivity.this, "wrong", Toast.LENGTH_SHORT).show();

                }
            }
        });

        //End of Button Listener for Button2


        //Start of Button Listener for Button3
        mButtonChoice3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //My logic for Button goes in here

                if (mButtonChoice3.getText() == mAnswer){
                    mScore = mScore + 1;
                    updateScore(mScore);
                    //This line of code is optiona
//                    Toast.makeText(QuizActivity.this, "correct", Toast.LENGTH_SHORT).show();

                }else {
//                    Toast.makeText(QuizActivity.this, "wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //End of Button Listener for Button3





    }



    private void updateScore(int point) {
        mScoreView.setText("" + mScore);
    }
}
