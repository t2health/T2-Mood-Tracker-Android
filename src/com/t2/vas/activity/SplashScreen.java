package com.t2.vas.activity;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.t2.vas.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

public class SplashScreen extends Activity implements OnClickListener {
	private static final String TAG = SplashScreen.class.getName();
	private View contentView;
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
		int width = display.getWidth();
		int height = display.getHeight();
		
		ScaleAnimation scaleAnimation;
		AlphaAnimation alphaAnimation;
		TranslateAnimation translateAnimation;
		
		View mainView = this.findViewById(R.id.main);
		mainView.setOnClickListener(this);
		
		ImageView iBackground = (ImageView)this.findViewById(R.id.iBackground);
		ImageView iChip = (ImageView)this.findViewById(R.id.iChip);
		ImageView iT2Glow1 = (ImageView)this.findViewById(R.id.iT2Glow1);
		ImageView iT2Glow2 = (ImageView)this.findViewById(R.id.iT2Glow2);
		
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
		for(int i = 0; i < glow1Lines.size(); i++) {
			glow1Lines.get(i).setVisibility(View.INVISIBLE);
		}
		
		ArrayList<ImageView> glow2Lines = new ArrayList<ImageView>();
		glow2Lines.add((ImageView)this.findViewById(R.id.iGlow3_1));
		glow2Lines.add((ImageView)this.findViewById(R.id.iGlow3_2));
		glow2Lines.add((ImageView)this.findViewById(R.id.iGlow3_3));
		glow2Lines.add((ImageView)this.findViewById(R.id.iGlow3_4));
		glow2Lines.add((ImageView)this.findViewById(R.id.iGlow3_5));
		glow2Lines.add((ImageView)this.findViewById(R.id.iGlow3_6));
		glow2Lines.add((ImageView)this.findViewById(R.id.iGlow3_7));
		glow2Lines.add((ImageView)this.findViewById(R.id.iGlow3_8));
		glow2Lines.add((ImageView)this.findViewById(R.id.iGlow3_9));
		for(int i = 0; i < glow2Lines.size(); i++) {
			glow2Lines.get(i).setVisibility(View.INVISIBLE);
		}
		
		
		// Set the animation for the chip.
		scaleAnimation = new ScaleAnimation(
				1.0f, 
				0.52f, 
				1.0f, 
				0.52f,
				(width / 2) + 185,
				(height / 2) + 5
		);
		scaleAnimation.setDuration(2000);
		alphaAnimation = new AlphaAnimation(
				1.0f,
				0.0f
		);
		alphaAnimation.setDuration(1500);
		alphaAnimation.setStartOffset(1500);
		
		AnimationSet iChipAnimation = new AnimationSet(true);
		iChipAnimation.addAnimation(scaleAnimation);
		iChipAnimation.addAnimation(alphaAnimation);
		iChipAnimation.setFillAfter(true);
		
		
		// Init the glow1 animation
		for(int i = 0; i < glow1Lines.size(); i+=4) {
			AnimationSet glow1Animation = new AnimationSet(true);
			translateAnimation = new TranslateAnimation(
					0.0f,
					(width / 2) - 122 + (i * 3.1f),
					0.0f,
					(height / 2) - 8 + (i * 2.3f)
			);
			translateAnimation.setFillAfter(true);
			
			glow1Animation.addAnimation(translateAnimation);
			glow1Animation.addAnimation(newAlphaAnimation(0.0f, 1.0f, 500, 0 + random.nextInt(500)));
			glow1Animation.addAnimation(newAlphaAnimation(0.0f, 1.0f, 500, 1000 + random.nextInt(500)));
			
			glow1Animation.setFillBefore(false);
			glow1Animation.setFillAfter(true);
			glow1Animation.setStartOffset(2500 + random.nextInt(500));
			glow1Animation.setInterpolator(new BounceInterpolator());
			
			glow1Lines.get(i).setVisibility(View.VISIBLE);
			glow1Lines.get(i).startAnimation(glow1Animation);
		}
		
		
		// Init the glow2 animation
		for(int i = 0; i < glow2Lines.size(); i+=2) {
			AnimationSet glow2Animation = new AnimationSet(true);
			translateAnimation = new TranslateAnimation(
					0.0f,
					(width / 2) + 125 - (i * 2.8f),
					0.0f,
					(height / 2) - 4 + (i * 3.5f)
			);
			
			glow2Animation.addAnimation(translateAnimation);
			glow2Animation.addAnimation(newAlphaAnimation(0.0f, 1.0f, 500, 0 + random.nextInt(500)));
			glow2Animation.addAnimation(newAlphaAnimation(0.0f, 1.0f, 500, 1000 + random.nextInt(500)));
			
			glow2Animation.setFillBefore(false);
			glow2Animation.setFillAfter(true);
			glow2Animation.setStartOffset(2000 + random.nextInt(500));
			
			glow2Lines.get(i).setVisibility(View.VISIBLE);
			glow2Lines.get(i).startAnimation(glow2Animation);
		}
		
		
		
		// Init the t2 glow1 animation
		AnimationSet iT2Glow1Animation = new AnimationSet(true);
		alphaAnimation = new AlphaAnimation(
				0.0f,
				1.0f
		);
		alphaAnimation.setDuration(2000);
		alphaAnimation.setStartOffset(3000);
		iT2Glow1Animation.addAnimation(alphaAnimation);
		iT2Glow1.startAnimation(alphaAnimation);
		
		
		// Init the t2 glow1 animation
		AnimationSet iT2Glow2Animation = new AnimationSet(true);
		alphaAnimation = new AlphaAnimation(
				0.0f,
				1.0f
		);
		alphaAnimation.setDuration(2000);
		alphaAnimation.setStartOffset(3000);
		iT2Glow2Animation.addAnimation(alphaAnimation);
		iT2Glow2.startAnimation(alphaAnimation);
		
		
		iChip.startAnimation(iChipAnimation);
	}
	
	private static AlphaAnimation newAlphaAnimation(float from, float to, int duration, int offset) {
		AlphaAnimation a = new AlphaAnimation(from, to);
		a.setDuration(duration);
		a.setStartOffset(offset);
		return a;
	}

	@Override
	public void onClick(View v) {
		this.startMain();
	}
}
