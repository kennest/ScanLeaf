package wesicknessdect.example.org.wesicknessdetect.activities;

import wesicknessdect.example.org.wesicknessdetect.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class CommunityActivity extends BaseActivity {

    LinearLayout v1,v2,v3,v4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        v1 =findViewById(R.id.com1);
        v2 =findViewById(R.id.com2);
        v3 =findViewById(R.id.com3);
        v4 =findViewById(R.id.com4);
         v1.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent a= new Intent(CommunityActivity.this, FinalResultActivity.class);
                    startActivity(a);
                                 }
         });
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent b= new Intent(CommunityActivity.this, FinalResultActivity.class);
                startActivity(b);
            }
        });
        v3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent c= new Intent(CommunityActivity.this, FinalResultActivity.class);
                startActivity(c);            }
        });
        v4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent d= new Intent(CommunityActivity.this, FinalResultActivity.class);
                startActivity(d);
            }
        });

    }
}
