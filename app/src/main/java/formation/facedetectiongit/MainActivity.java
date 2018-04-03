package formation.facedetectiongit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;


public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button btnProgress;

    Bitmap tempBitmap;
    Canvas canvas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        btnProgress = (Button) findViewById(R.id.btnProgress);

        final Bitmap myBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.tom);
        imageView.setImageBitmap(myBitmap);


        tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        canvas = new Canvas(tempBitmap);
        canvas.drawBitmap(myBitmap, 0, 0, null);

        btnProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                        .setTrackingEnabled(false)
                        .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                        .setMode(FaceDetector.FAST_MODE)
                        .build();

                if (!faceDetector.isOperational()) {
                    Toast.makeText(MainActivity.this, "Face Detector could not be set up on your device", Toast.LENGTH_SHORT).show();
                    return;
                }

                Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                SparseArray<Face> sparseArray = faceDetector.detect(frame);
                faceDetector.release();
                double scale = drawBitmap(canvas);

                for (int i = 0; i < sparseArray.size(); i++) {
                    Face face = sparseArray.valueAt(i);
                    detectLandmarks(face, scale);
                }

                imageView.setImageDrawable(new BitmapDrawable(getResources(), myBitmap));

            }
        });

    }

    private double drawBitmap(Canvas canvas) {
        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = tempBitmap.getWidth();
        double imageHeight = tempBitmap.getHeight();
        double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);

        Rect destBounds = new Rect(0, 0, (int) (imageWidth * scale), (int) (imageHeight * scale));
        canvas.drawBitmap(tempBitmap, null, destBounds, null);
        return scale;
    }

    private void detectLandmarks(Face face, double scale) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);


        for (Landmark landmark : face.getLandmarks()) {

            int cx = (int) (landmark.getPosition().x * scale);
            int cy = (int) (landmark.getPosition().y * scale);

            canvas.drawCircle(cx, cy, 10, paint);
            //drawEyePatchBitmap(landmark.getType(), cx, cy);
        }
    }
}
