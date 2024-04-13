package com.project1.borrowme.splash;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.project1.borrowme.MainActivity;
import com.project1.borrowme.R;
import com.project1.borrowme.logIns.LoginActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private final int DURATION_TIME = 1650;
    private final long DELAY = 700;
    private final float ALPHA_START = 0.0f;
    private final float ALPHA_STOP = 1.0f;
    private final float START_SCALEX = 0.0f;
    private final float START_SCALEY = 0.0f;
    private final float STOP_SCALEX = 1.0f;
    private final float STOP_SCALEY = 1.0f;
    private final float Y_START = 2.0f;
    private final float Y_FINISH = 0.0f;


    ShapeableImageView splash_IMG_logo;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViews();
        handler = new Handler();
        startAnimation(splash_IMG_logo);
    }

    private void startAnimation(View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        view.setY(height / Y_START);
        view.setScaleX(START_SCALEX);
        view.setScaleY(START_SCALEY);
        view.setAlpha(ALPHA_START);

        view.animate()
                .alpha(ALPHA_STOP)
                .scaleX(STOP_SCALEX)
                .scaleY(STOP_SCALEY)
                .translationY(Y_FINISH)
                .setDuration(DURATION_TIME)
                .setListener(
                        new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(@NonNull Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(@NonNull Animator animation) {
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        view.animate()
                                                .alpha(ALPHA_START)
                                                .scaleX(START_SCALEX)
                                                .scaleY(START_SCALEY)
                                                .translationY(-height / Y_START)
                                                .setDuration(DURATION_TIME)
                                                .setListener(
                                                        new Animator.AnimatorListener() {
                                                            @Override
                                                            public void onAnimationStart(@NonNull Animator animation) {

                                                            }

                                                            @Override
                                                            public void onAnimationEnd(@NonNull Animator animation) {
                                                                stopRunnable();
                                                                changeActivity();

                                                            }

                                                            @Override
                                                            public void onAnimationCancel(@NonNull Animator animation) {

                                                            }

                                                            @Override
                                                            public void onAnimationRepeat(@NonNull Animator animation) {

                                                            }
                                                        }


                                                );
                                    }
                                };
                                handler.postDelayed(runnable, DELAY);
                            }

                            @Override
                            public void onAnimationCancel(@NonNull Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(@NonNull Animator animation) {

                            }
                        }
                );
    }

    public void stopRunnable() {
        handler.removeCallbacksAndMessages(null);
    }

    private void changeActivity() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

    }

    private void findViews() {
        splash_IMG_logo = findViewById(R.id.splash_IMG_logo);
    }
}