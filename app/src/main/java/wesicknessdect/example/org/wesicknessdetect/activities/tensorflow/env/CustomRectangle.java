package wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.env;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

@SuppressLint({"ViewConstructor", "AppCompatCustomView"})
public class CustomRectangle extends View {
    Paint paint;
    float left_side, top_side;
    String color;
    RectF rectf;

    //!< Constructor for the log in rectangle shaped panel
    public CustomRectangle(Context context, RectF rectf, float left_side, float top_side, String color) {
        super(context);

        this.rectf = rectf;
        this.left_side= left_side;
        this.top_side = top_side;
        this.color = color;
        setFocusable(true);
    }

    //!< Implement to draw the rectangle
    @SuppressLint("DrawAllocation")
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint = new Paint();
        paint.setColor(Color.parseColor(color));
        paint.setStrokeWidth(3);
        //paint.setAlpha(61);

        canvas.drawRect(rectf, paint);

    }
}
