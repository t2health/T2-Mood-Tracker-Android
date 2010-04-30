package com.t2.vas.activity;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.t2.vas.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

public class SplashScreen extends Activity implements OnClickListener {
	private static final String TAG = SplashScreen.class.getName();
	Timer startAppTimer = new Timer();
	Toast skipMessage;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		
		skipMessage = Toast.makeText(this, R.string.activity_splash_screen_message, 5000);
		skipMessage.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
		this.setContentView(R.layout.splash_screen);
	}

	private void startMain() {
		startAppTimer.cancel();
		Intent i = new Intent(this, MainActivity.class);
		this.startActivity(i);
		this.finish();
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		skipMessage.show();
		startAppTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				startMain();
				this.cancel();
			}
		}, 8000);
		
		Random random = new Random();
		Display display = this.getWindowManager().getDefaultDisplay();
		
		ScaleAnimation scaleAnimation;
		AlphaAnimation alphaAnimation;
		
		View mainView = this.findViewById(R.id.main);
		mainView.setOnClickListener(this);
		
		ImageView iBackground = (ImageView)this.findViewById(R.id.iBackground);
		ImageView iChip0Overlay = (ImageView)this.findViewById(R.id.iChip0Overlay);
		ImageView iChip0Glow = (ImageView)this.findViewById(R.id.iChip0Glow);
		ImageView iChip1Glow = (ImageView)this.findViewById(R.id.iChip1Glow);
		ImageView iChip2Glow = (ImageView)this.findViewById(R.id.iChip2Glow);
		
		ArrayList<ImageView> glow1Lines = new ArrayList<ImageView>();
		glow1Lines.add((ImageView)this.findViewById(R.id.iGlow1_1));
		glow1Lines.add((ImageView)this.findViewById(R.id.iGlow1_2));
		glow1Lines.add((ImageView)this.findViewById(R.id.iGlow1_3));
		glow1Lines.add((ImageView)this.findViewById(R.id.iGlow1_4));
		glow1Lines.add((ImageView)this.findViewById(R.id.iGlow1_5));
		glow1Lines.add((ImageView)this.findViewById(R.id.iGlow1_6));
		glow1Lines.add((ImageView)this.findViewById(R.id.iGlow1_7));
		glow1Lines.add((ImageView)this.findViewById(R.id.iGlow1_8));
		glow1Lines.add((ImageView)this.findViewById(R.id.iGlow1_9));
		glow1Lines.add((ImageView)this.findViewById(R.id.iGlow1_10));
		glow1Lines.add((ImageView)this.findViewById(R.id.iGlow1_11));
		glow1Lines.add((ImageView)this.findViewById(R.id.iGlow1_12));
		glow1Lines.add((ImageView)this.findViewById(R.id.iGlow1_13));
		glow1Lines.add((ImageView)this.findViewById(R.id.iGlow1_14));
		
		ArrayList<ImageView> glow2Lines = new ArrayList<ImageView>();
		glow2Lines.add((ImageView)this.findViewById(R.id.iGlow2_1));
		glow2Lines.add((ImageView)this.findViewById(R.id.iGlow2_2));
		glow2Lines.add((ImageView)this.findViewById(R.id.iGlow2_3));
		glow2Lines.add((ImageView)this.findViewById(R.id.iGlow2_4));
		glow2Lines.add((ImageView)this.findViewById(R.id.iGlow2_5));
		glow2Lines.add((ImageView)this.findViewById(R.id.iGlow2_6));
		glow2Lines.add((ImageView)this.findViewById(R.id.iGlow2_7));
		glow2Lines.add((ImageView)this.findViewById(R.id.iGlow2_8));
		glow2Lines.add((ImageView)this.findViewById(R.id.iGlow2_9));
		glow2Lines.add((ImageView)this.findViewById(R.id.iGlow2_10));

		float scale = this.getResources().getDisplayMetrics().density;
		
		// Set the animation for the chip.
		scaleAnimation = new ScaleAnimation(
				1.0f, 
				0.3f,
				1.0f, 
				0.3f,
				350 * scale + 0.5f,
				150 * scale + 0.5f
		);
		scaleAnimation.setDuration(2000);
		TranslateAnimation translateAnimation = new TranslateAnimation(
			0.0f,
			350 * scale + 0.5f,
			0.0f,
			150 * scale + 0.5f
		);
		translateAnimation.setDuration(2000);
		alphaAnimation = new AlphaAnimation(
				1.0f,
				0.0f
		);
		alphaAnimation.setDuration(500);
		alphaAnimation.setStartOffset(1000);
		
		AnimationSet iChip0OverlayAnimation = new AnimationSet(true);
		//iChip0OverlayAnimation.addAnimation(translateAnimation);
		iChip0OverlayAnimation.addAnimation(scaleAnimation);
		iChip0OverlayAnimation.addAnimation(alphaAnimation);
		iChip0OverlayAnimation.setFillAfter(true);
		iChip0Overlay.setVisibility(View.VISIBLE);
		iChip0Overlay.startAnimation(iChip0OverlayAnimation);
		
		
		// Init the glow1 animation
		for(int i = 0; i < glow1Lines.size(); i+=2) {
			AnimationSet glow1Animation = new AnimationSet(true);
			glow1Animation.addAnimation(circuitLineAlphaAnimation(0.0f, 1.0f, 500 + random.nextInt(250), 0 + random.nextInt(500)));
			glow1Animation.addAnimation(circuitLineAlphaAnimation(0.0f, 1.0f, 500 + random.nextInt(250), 1000 + random.nextInt(500)));
			
			glow1Animation.setFillBefore(false);
			glow1Animation.setFillAfter(true);
			glow1Animation.setStartOffset(1500 + random.nextInt(500));
			
			glow1Lines.get(i).setVisibility(View.VISIBLE);
			glow1Lines.get(i).startAnimation(glow1Animation);
		}
		
		// Init the glow2 animation
		for(int i = 0; i < glow2Lines.size(); i+=2) {
			AnimationSet glow2Animation = new AnimationSet(true);
			glow2Animation.addAnimation(circuitLineAlphaAnimation(0.0f, 1.0f, 500 + random.nextInt(250), 0 + random.nextInt(500)));
			glow2Animation.addAnimation(circuitLineAlphaAnimation(0.0f, 1.0f, 500 + random.nextInt(250), 1000 + random.nextInt(500)));
			
			glow2Animation.setFillBefore(false);
			glow2Animation.setFillAfter(true);
			glow2Animation.setStartOffset(1000 + random.nextInt(500));
			
			glow2Lines.get(i).setVisibility(View.VISIBLE);
			glow2Lines.get(i).startAnimation(glow2Animation);
			
		}
		
		
		// Init the t2 glow1 animation
		AnimationSet iChip1GlowAnimation = new AnimationSet(true);
		alphaAnimation = new AlphaAnimation(
				0.0f,
				1.0f
		);
		alphaAnimation.setDuration(3000);
		alphaAnimation.setStartOffset(2250);
		iChip1GlowAnimation.addAnimation(alphaAnimation);
		iChip1Glow.setVisibility(View.VISIBLE);
		iChip1Glow.startAnimation(alphaAnimation);
		
		
		// Init the t2 glow1 animation
		AnimationSet iChip2GlowAnimation = new AnimationSet(true);
		alphaAnimation = new AlphaAnimation(
				0.0f,
				1.0f
		);
		alphaAnimation.setDuration(3500);
		alphaAnimation.setStartOffset(2000);
		iChip2GlowAnimation.addAnimation(alphaAnimation);
		iChip2Glow.setVisibility(View.VISIBLE);
		iChip2Glow.startAnimation(alphaAnimation);
		
		
		// Init the logo glow animation
		AnimationSet iChip0GlowAnimation = new AnimationSet(true);
		alphaAnimation = new AlphaAnimation(
				0.0f,
				1.0f
		);
		alphaAnimation.setDuration(4000);
		alphaAnimation.setStartOffset(3000);
		iChip0GlowAnimation.addAnimation(alphaAnimation);
		iChip0Glow.setVisibility(View.VISIBLE);
		iChip0Glow.startAnimation(alphaAnimation);
	}
	
	private static AlphaAnimation circuitLineAlphaAnimation(float from, float to, int duration, int offset) {
		AlphaAnimation a = new AlphaAnimation(from, to);
		a.setInterpolator(new BounceInterpolator());
		a.setDuration(duration);
		a.setStartOffset(offset);
		return a;
	}

	@Override
	public void onClick(View v) {
		this.startMain();
	}
}
