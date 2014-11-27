/*
Miren Tanna - mat130830
Description: Activity code to calculate the working of the game. Logic of how the change in accelerometer 
sensors controls the horse. Shared preferences determine the look of the barrel, and the accelerometer 
speed. Shared preferences are also used to save the scores. 
*/
package com.example.drawtests;

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.http.HttpVersion;

import com.example.drawtests.R.color;
import com.example.drawtests.R.dimen;
import com.example.drawtests.R.drawable;
import com.example.drawtests.R.id;
import com.example.drawtests.R.layout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.hardware.Camera.Size;
import android.net.UrlQuerySanitizer.ValueSanitizer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.format.Time;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity implements SensorEventListener {
	static Sensor accelerometerSensor;
	static SensorManager sensorManager;
	public static Vibrator touchedCircle;
	public static float xStart = 640;
	public static float yStart = 560;
	public static float circleRadius;
	public static float accelerometer_reading;
	static Calendar start = null;
	public static int startTime = 0;
	static Time end = null;
	static boolean remove= false;
	
	static float height;
	static float width;
	static float circleACenterX, circleACenterY, circleBCenterX,
			circleBCenterY, circleCCenterX, circleCCenterY;
	static boolean AnotInCircle = false, BnotInCircle = false,
			CnotInCircle = false, outsideStadium = false;
	static boolean AcenterCircled = false, BcenterCircled = false,
			CcenterCircled = false;
	static Vector<Long> circleACoordinates = new Vector<Long>();
	static Vector<Long> circleBCoordinates = new Vector<Long>();
	static Vector<Long> circleCCoordinates = new Vector<Long>();
	public static float score, high_score;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometerSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, accelerometerSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		touchedCircle = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	//	start = Calendar.getInstance();
		end = new Time();
	/*	startTime = (start.get(Calendar.MINUTE) * 60)
				+ start.get(Calendar.SECOND);
	*/	
		//obtain values for the barrel and accelerometer from the shared preferencess
		SharedPreferences sharedPref = getSharedPreferences("key", 0);
		circleRadius = sharedPref.getFloat("Radius", 0f);
		accelerometer_reading = sharedPref
				.getFloat("Accelerometer_Reading", 0f);
		if (circleRadius > 0) {
			circleRadius = circleRadius * 15;
		} else {
			circleRadius = 10;
		}
		if (accelerometer_reading > 0)
			accelerometer_reading = Math.round(accelerometer_reading * 2.5);
		else
			accelerometer_reading = 2;
		
		//set default values of variables
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			sensorManager.unregisterListener(this);
			xStart = 600;
			yStart = 560;
			AcenterCircled = false;
			BcenterCircled = false;
			CcenterCircled = false;
			circleACoordinates.clear();
			circleBCoordinates.clear();
			circleCCoordinates.clear();
		}
		
		//set the view to SampleView
		setContentView(new SampleView(this));

	}

	private static class SampleView extends View {

		Display display;
		Canvas canvas = new Canvas();
		boolean checkCoordinateVector = false;
		boolean touched = false;
		String timer;
		long count = 0L;
		long highScore = 0L;
		int punishment = 0;
		SharedPreferences settings;
		private Long mRowId;

		public SampleView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			display = wm.getDefaultDisplay();
			setFocusable(true);

			//set shared preferences to set high score value to 0 if it does not exist
			settings = context.getSharedPreferences("key", 0);
			SharedPreferences.Editor score_editor = settings.edit();

			if (settings.contains("High_Score")) {
				high_score = settings.getFloat("High_Score", 0);
			} else {
				high_score = 0;
				score_editor.putFloat("High_Score", high_score);
				score_editor.commit();
			}

			SharedPreferences.Editor editor = settings.edit();
			editor.putLong("Score", count);
			editor.putLong("HighScore", highScore);
			editor.commit();
		}

		@SuppressLint("DrawAllocation")
		@SuppressWarnings("deprecation")
		@Override
		protected void onDraw(final Canvas canvas) {
			// TODO Auto-generated method stub
			super.onDraw(canvas);
			height = display.getHeight();
			width = display.getWidth();
			circleACenterX = width / 2;
			circleACenterY = height / 3;
			circleBCenterX = width / 4;
			circleBCenterY = height * 0.66f;
			circleCCenterX = width * 0.75f;
			circleCCenterY = height * 0.66f;
			canvas.drawColor(Color.WHITE);
			Paint paintA = new Paint();
			Paint paintB = new Paint();
			Paint paintC = new Paint();
			Paint textPaint = new Paint();
			Paint textScorePaint = new Paint();
			Paint fillColor = new Paint();
			Paint stadiumPaint = new Paint();
			Paint startEndPaint = new Paint();
			Paint barrelBoundary = new Paint();

			Bitmap grass_tufts = BitmapFactory.decodeResource(getResources(),
					drawable.grass_tufts);
			Bitmap stone_brown = BitmapFactory.decodeResource(getResources(),
					drawable.stone_cartoon);
			Bitmap stone_black = BitmapFactory.decodeResource(getResources(),
					drawable.stone_cartoon_2);
			canvas.drawBitmap(grass_tufts, 100, 100, null);
			canvas.drawBitmap(grass_tufts, 300, 300, null);
			canvas.drawBitmap(grass_tufts, 500, 300, null);
			canvas.drawBitmap(grass_tufts, 500, 600, null);
			canvas.drawBitmap(grass_tufts, 1000, 630, null);
			canvas.drawBitmap(grass_tufts, 750, 270, null);
			canvas.drawBitmap(grass_tufts, 1100, 120, null);
			canvas.drawBitmap(grass_tufts, 410, 60, null);

			canvas.drawBitmap(stone_brown, 200, 200, null);
			canvas.drawBitmap(stone_brown, 960, 200, null);
			canvas.drawBitmap(stone_black, 640, 400, null);
			canvas.drawBitmap(stone_black, 900, 300, null);
			canvas.drawBitmap(stone_black, 150, 640, null);
			canvas.drawBitmap(stone_black, 120, 430, null);
			canvas.drawBitmap(stone_black, 560, 120, null);
			canvas.drawBitmap(stone_brown, 700, 160, null);
			canvas.drawBitmap(stone_brown, 740, 640, null);
			canvas.drawBitmap(stone_brown, 1100, 500, null);
			canvas.drawBitmap(stone_black, 700, 50, null);

			paintA.setAntiAlias(true);
			paintA.setColor(Color.rgb(204, 153, 51));
			paintA.setStyle(Style.FILL_AND_STROKE);
			paintA.setStrokeWidth(5.0f);

			paintB.setAntiAlias(true);
			paintB.setColor(Color.rgb(204, 153, 51));
			paintB.setStyle(Style.FILL_AND_STROKE);
			paintB.setStrokeWidth(5.0f);

			paintC.setAntiAlias(true);
			paintC.setColor(Color.rgb(204, 153, 51));
			paintC.setStyle(Style.FILL_AND_STROKE);
			paintC.setStrokeWidth(5.0f);

			barrelBoundary.setAntiAlias(true);
			barrelBoundary.setColor(Color.rgb(128, 0, 0));
			barrelBoundary.setStyle(Style.STROKE);
			barrelBoundary.setStrokeWidth(5.0f);

			textPaint.setAntiAlias(true);
			textPaint.setStyle(Style.STROKE);
			textPaint.setTextSize(30f);

			textScorePaint.setAntiAlias(true);
			textScorePaint.setColor(Color.RED);
			textScorePaint.setTextSize(45.0f);
			textScorePaint.setStrokeWidth(2.0f);
			textScorePaint.setStyle(Paint.Style.FILL_AND_STROKE);

			stadiumPaint.setAntiAlias(true);
			stadiumPaint.setColor(Color.rgb(102, 51, 0));
			stadiumPaint.setStyle(Style.STROKE);
			stadiumPaint.setTextSize(20f);
			stadiumPaint.setStrokeWidth(20f);

			startEndPaint.setAntiAlias(true);
			startEndPaint.setColor(Color.RED);
			startEndPaint.setStyle(Style.STROKE);
			startEndPaint.setStrokeWidth(3F);

			fillColor.setAntiAlias(true);
			fillColor.setColor(Color.BLACK);
			fillColor.setStyle(Style.STROKE);
			fillColor.setStrokeWidth(25.0f);

			canvas.drawRect(0 + 5, 0 + 5, width - 5, height - 5, stadiumPaint);
			canvas.drawCircle(circleACenterX, circleACenterY, circleRadius,
					paintA);
			canvas.drawCircle(circleBCenterX, circleBCenterY, circleRadius,
					paintB);
			canvas.drawCircle(circleCCenterX, circleCCenterY, circleRadius,
					paintC);

			canvas.drawCircle(circleACenterX, circleACenterY, circleRadius,
					barrelBoundary);
			canvas.drawCircle(circleBCenterX, circleBCenterY, circleRadius,
					barrelBoundary);
			canvas.drawCircle(circleCCenterX, circleCCenterY, circleRadius,
					barrelBoundary);

			
			if (touched) // game begins from this method, user needs to touch
							// the screen to let the point move
			{
				if(!remove){
				start = Calendar.getInstance();
				startTime = (start.get(Calendar.MINUTE) * 60)
						+ start.get(Calendar.SECOND);
				remove=true;
				}
				textPaint.setStyle(Style.FILL_AND_STROKE);
				textPaint.setARGB(0, 255, 255, 255);
				canvas.drawPoint(xStart, yStart, fillColor);
				canvas.drawText("Touch To Begin", 540, 50, textPaint);

				// Draw the score text
				int endTime;
				Time end = new Time();
				end.setToNow();
				endTime = (end.minute * 60) + end.second;
				score = (endTime - startTime) + punishment;
				canvas.drawText(((int) score + punishment) + "", 30, 55,
						textScorePaint);
				
			} else {
				remove=false;
				textPaint.setARGB(255, 0, 0, 0);
				xStart=640;
				yStart=560;
				canvas.drawText("Touch To Begin", 540, 50, textPaint);

			}
			
			//check if the game has begun and that horse is outside circle and has not circled circle A
			if (touched && AnotInCircle && !AcenterCircled) {
				// canvas.drawPoint(xStart, yStart, fillColor);
				textPaint.setColor(Color.BLACK);
				touchedCircle.cancel();
				long x = Math.round(Math.toDegrees(Math.atan2(
						(circleACenterY - yStart), (circleACenterX - xStart))));
				circleACoordinates.addElement(x);
				if ((circlesBarrel(circleACoordinates)) && !AcenterCircled) {
					paintA.setColor(Color.GREEN);
					canvas.drawCircle(circleACenterX, circleACenterY,
							circleRadius, paintA);
					canvas.drawCircle(circleACenterX, circleACenterY,
							circleRadius, barrelBoundary);
					AcenterCircled = true;
				}
				this.invalidate();
			}
			//check if the game has begun and that horse is outside circle and has not circled circle B
			if (touched && BnotInCircle && !BcenterCircled) {
				// canvas.drawPoint(xStart, yStart, fillColor);
				textPaint.setColor(Color.BLACK);
				touchedCircle.cancel();
				long x = Math.round(Math.toDegrees(Math.atan2(
						(circleBCenterY - yStart), (circleBCenterX - xStart))));
				circleBCoordinates.addElement(x);
				if ((circlesBarrel(circleBCoordinates)) && !BcenterCircled) {
					paintB.setColor(Color.GREEN);
					canvas.drawCircle(circleBCenterX, circleBCenterY,
							circleRadius, paintB);
					canvas.drawCircle(circleBCenterX, circleBCenterY,
							circleRadius, barrelBoundary);
					BcenterCircled = true;
				}
				this.invalidate();
			}
			//check if the game has begun and that horse is outside circle and has not circled circle C
			if (touched && CnotInCircle && !CcenterCircled) {
				// canvas.drawPoint(xStart, yStart, fillColor);
				textPaint.setColor(Color.BLACK);
				touchedCircle.cancel();
				long x = Math.round(Math.toDegrees(Math.atan2(
						(circleCCenterY - yStart), (circleCCenterX - xStart))));
				circleCCoordinates.addElement(x);
				if ((circlesBarrel(circleCCoordinates)) && !CcenterCircled) {
					paintB.setColor(Color.GREEN);
					canvas.drawCircle(circleCCenterX, circleCCenterY,
							circleRadius, paintC);
					canvas.drawCircle(circleCCenterX, circleCCenterY,
							circleRadius, barrelBoundary);
					CcenterCircled = true;
				}
				this.invalidate();
			}
			//check if horse has touched wall, if yes, penalise user
			if (outsideStadium) {
				punishment += 2;
				textPaint.setColor(Color.BLACK);
				touchedCircle.vibrate(100);
				this.invalidate();
			}
			//if horse has circled barrel A, turn the barrel green
			if (AcenterCircled) {
				paintA.setColor(Color.GREEN);
				canvas.drawCircle(circleACenterX, circleACenterY, circleRadius,
						paintA);
				canvas.drawCircle(circleACenterX, circleACenterY, circleRadius,
						barrelBoundary);
			}
			//if horse has circled barrel B, turn the barrel green
			if (BcenterCircled) {
				paintB.setColor(Color.GREEN);
				canvas.drawCircle(circleBCenterX, circleBCenterY, circleRadius,
						paintB);
				canvas.drawCircle(circleBCenterX, circleBCenterY, circleRadius,
						barrelBoundary);
			}
			//if horse has circled barrel C, turn the barrel green
			if (CcenterCircled) {
				paintC.setColor(Color.GREEN);
				canvas.drawCircle(circleCCenterX, circleCCenterY, circleRadius,
						paintC);
				canvas.drawCircle(circleCCenterX, circleCCenterY, circleRadius,
						barrelBoundary);
			}
			//calculate if the horse is on or inside the barrel, if yes, end the game
			if ((float) Math.sqrt(Math.pow(xStart - circleACenterX, 2)
					+ Math.pow(yStart - circleACenterY, 2)) <= circleRadius + 5
					|| (float) Math.sqrt(Math.pow(xStart - circleBCenterX, 2)
							+ Math.pow(yStart - circleBCenterY, 2)) <= circleRadius + 5
					|| (float) Math.sqrt(Math.pow(xStart - circleCCenterX, 2)
							+ Math.pow(yStart - circleCCenterY, 2)) <= circleRadius + 5) 
			{
				textPaint.setColor(Color.BLACK);
				touchedCircle.vibrate(100);
				Context cx = getContext();
				Intent n = new Intent(cx, MainScreenActivity.class);
				n.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				n.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				n.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Log.d("Timer value on end", Long.toString(count));
				n.putExtra("count", count);

				//on ending of game, set the shared preferences with the new score,
				//do not update high score and game ended prematurely
				SharedPreferences.Editor sendScores = settings.edit();
				sendScores.putFloat("Current_Score", score);
				sendScores.putFloat("High_Score", high_score);
				sendScores.commit();

				n.putExtra("highscore", highScore);
				n.putExtra("current_score", count);
				cx.startActivity(n);

				// this.invalidate();
			}
			//draw the box where the horse should end its race once all barrels have been circled around
			if (AcenterCircled && BcenterCircled && CcenterCircled)
				canvas.drawRect(circleACenterX - 30, circleBCenterY + 40,
						circleACenterX + 30, circleBCenterY + 100,
						startEndPaint);

			// once all barrels have been circled and the horse is inside the box, end the game successfully
			if (AcenterCircled && BcenterCircled && CcenterCircled
					&& xStart > (circleACenterX - 30)
					&& xStart < (circleACenterX + 30)
					&& yStart > (circleBCenterY + 40)
					&& yStart < (circleBCenterY + 100)) {
				Context cx = getContext();
				Intent gameOverIntent = new Intent(cx, MainScreenActivity.class);
				gameOverIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				gameOverIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				gameOverIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				gameOverIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				//set shared preference with updated score, and high score if it is better than previous one
				SharedPreferences.Editor sendScores = settings.edit();

				if (high_score > score || high_score==0F) {
					sendScores.putFloat("Current_Score", score+punishment);
					sendScores.putFloat("High_Score", score+punishment);
				} 
				else {
					sendScores.putFloat("Current_Score", score+punishment);
					sendScores.putFloat("High_Score", high_score);
				}
				sendScores.commit();

				cx.startActivity(gameOverIntent);
			}
			this.invalidate();
		}

		//check if the screen has been touched using touch event, set touched variable to true to let the game begin
		public boolean onTouchEvent(MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				touched = true;
			}
			return super.onTouchEvent(event);
		}

		//obtain coordinates of the horse, store them in Vector to calculate if it has gone around the barrel
		public boolean circlesBarrel(Vector<Long> coordinates) {
			if ((Long) coordinates.firstElement() == (Long) coordinates
					.lastElement()
					|| ((Long) coordinates.firstElement() >= ((Long) coordinates
							.lastElement() - 30) && (Long) coordinates
							.firstElement() <= ((Long) coordinates
							.lastElement() + 30)))
				checkCoordinateVector = true;
			else
				checkCoordinateVector = false;

			boolean check0 = false, check90 = false, check180 = false, checkN90 = false;

			for (int i = -20; i <= 20; i++) {
				if (coordinates.contains(0L + i)) {
					check0 = true;
					break;
				} else
					check0 = false;
			}
			for (int i = -20; i <= 20; i++) {
				if (coordinates.contains(90L + i)) {
					check90 = true;
					break;
				} else
					check90 = false;
			}
			for (int i = -20; i <= 20; i++) {
				if (coordinates.contains(180L + i)) {
					check180 = true;
					break;
				} else
					check180 = false;
			}
			for (int i = -20; i <= 20; i++) {
				if (coordinates.contains(-90L + i)) {
					checkN90 = true;
					break;
				} else
					checkN90 = false;
			}

			if (check0 && check90 && check180 && checkN90
					&& checkCoordinateVector)
				return true;
			else
				return false;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	//manipulate the position of the horse once device detects changes in the accelerometer sensors
	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub

		/* 1 */if (arg0.values[0] < 0 && arg0.values[1] >= 1 && xStart < width
				&& yStart > 0 && yStart < height) {
			xStart += accelerometer_reading;
			yStart -= accelerometer_reading;
		}
		/* 2 */if (arg0.values[0] >= 1 && arg0.values[1] >= 1 && xStart > 0
				&& yStart > 0 && yStart < height) {
			xStart += accelerometer_reading;
			yStart += accelerometer_reading;
		}
		/* 3 */if (arg0.values[0] >= 1 && arg0.values[1] < 0 && yStart < height
				&& xStart > 0 && yStart > 0) {
			xStart -= accelerometer_reading;
			yStart += accelerometer_reading;
		}
		/* 4 */if (arg0.values[0] < 0 && arg0.values[1] < 0 && yStart > 0
				&& xStart > 0 && xStart < width) {
			xStart -= accelerometer_reading;
			yStart -= accelerometer_reading;
		}
		/* 4,1 */if (arg0.values[0] < 0 && arg0.values[1] < 1
				&& arg0.values[1] >= 0 && xStart > 0 && xStart < width
				&& yStart > 0) {
			yStart -= accelerometer_reading;
			xStart = xStart;
		}
		/* 1,2 */if (arg0.values[0] < 1 && arg0.values[0] >= 0
				&& arg0.values[1] > 0 && xStart > 0 && xStart < width
				&& yStart > 0) {
			xStart += accelerometer_reading;
			yStart = yStart;
		}
		/* 2,3 */if (arg0.values[0] > 0 && arg0.values[1] < 1
				&& arg0.values[1] >= 0 && xStart < width && yStart > 0
				&& yStart < height) {
			yStart += accelerometer_reading;
			xStart = xStart;
		}
		/* 3,4 */if (arg0.values[0] < 1 && arg0.values[0] >= 0
				&& arg0.values[1] < 0 && xStart > 0 && yStart > 0
				&& yStart < height) {
			xStart -= accelerometer_reading;
			yStart = yStart;
		}
		if (yStart <= 5)
			yStart += accelerometer_reading;
		if (yStart >= height - 5)
			yStart -= accelerometer_reading;
		if (xyDistance(xStart, yStart, circleACenterX, circleACenterY) > circleRadius + 1)		{
			AnotInCircle = true;
		} else
			AnotInCircle = false;
		if (xyDistance(xStart, yStart, circleBCenterX, circleBCenterY) > circleRadius + 1){
			BnotInCircle = true;
		} else
			BnotInCircle = false;
		if (xyDistance(xStart, yStart, circleCCenterX, circleCCenterY) > circleRadius + 1){
			CnotInCircle = true;
		} else
			CnotInCircle = false;

		if (xStart <= 15 || xStart >= width - 20 || yStart <= 15
				|| yStart >= height - 20)
			outsideStadium = true;
		else
			outsideStadium = false;

	}

	//determine the distance of horse from the center of the barrel
	public float xyDistance(float xStart, float yStart, float circleCenterX,
			float circleCenterY) {
		return (float) Math.sqrt(Math.pow(xStart - circleCenterX, 2)
				+ Math.pow(yStart - circleCenterY, 2));
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sensorManager.unregisterListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sensorManager.registerListener(this, accelerometerSensor,
				SensorManager.SENSOR_DELAY_NORMAL);

	}

	//send user back to the On load screen when the device back button has been pressed
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		start = null;
		end = null;
		sensorManager.unregisterListener(this);
		Intent x = new Intent(getApplicationContext(), MainScreenActivity.class);
		x.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		x.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		x.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		x.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(x);
		this.finish();
	}
}