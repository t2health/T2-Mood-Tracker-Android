/*
 * 
 * T2 Mood Tracker
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: T2MoodTracker001
 * Government Agency Original Software Title: T2 Mood Tracker
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package com.t2.vas.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;

public class ColorPickerView extends View {
        Paint paint;
        Shader luar;
        final float[] color = { 1.f, 1.f, 1.f };

        public ColorPickerView(Context context, AttributeSet attrs) {
                super(context, attrs);
        }

        public ColorPickerView(Context context, AttributeSet attrs, int defStyle) {
                super(context, attrs, defStyle);
        }

        @SuppressLint("DrawAllocation") @Override protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (paint == null) {
                        paint = new Paint();
                        luar = new LinearGradient(0.f, 0.f, 0.f, this.getMeasuredHeight(), 0xffffffff, 0xff000000, TileMode.CLAMP);
                }
                int rgb = Color.HSVToColor(color);
                Shader dalam = new LinearGradient(0.f, 0.f, this.getMeasuredWidth(), 0.f, 0xffffffff, rgb, TileMode.CLAMP);
                ComposeShader shader = new ComposeShader(luar, dalam, PorterDuff.Mode.MULTIPLY);
                paint.setShader(shader);
                canvas.drawRect(0.f, 0.f, this.getMeasuredWidth(), this.getMeasuredHeight(), paint);
        }

        void setHue(float hue) {
                color[0] = hue;
                invalidate();
        }
}