/*
Pragathi Shekhar - psb130230
Description: Control logic for the main screen layout, which the user views when the app opens up for the 
first time. The activity code contains code to display the latest games score, and the highest games score.
Three buttons respond to button click events, and accordingly exit from the app, or show help dialog, or
begin the game.
*/
package com.example.drawtests;

import com.example.drawtests.MainActivity;
import com.example.drawtests.R.id;
import com.example.drawtests.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainScreenActivity extends Activity {
	ImageButton customizeButton;
	private TextView score;
	private TextView highScore;
	private TextView yourScore;
	private Button startButton;
	private Button helpButton;
	private Button exitButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//set window flags to display app in full screen mode and to render the device screen on at all times
		//when the app is active
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(layout.activity_mainscreen);

		// Call the button event.
		addListenerOnCustButton();
		addListenerOnStartButton();
		addListenerOnHelpButton();
		addListenerOnExitButton();
	
		float current_score = 0;
		float high_score = 0;

		//check in Shared Preferences if the scores exists, if not, display as zero
		SharedPreferences sharedPref = getSharedPreferences("key", 0);
		
		if (sharedPref.contains("Current_Score")
				&& sharedPref.contains("High_Score")) {
			current_score = sharedPref.getFloat("Current_Score", 0);
			high_score = sharedPref.getFloat("High_Score", 0);
		} else {
			current_score = 0;
			high_score = 0;

			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putFloat("Current_Score", 0);
			editor.putFloat("High_Score", 0);
			editor.commit();
		}

		yourScore = (TextView) findViewById(R.id.textViewYourScore);
		highScore = (TextView) findViewById(id.textViewYourHighScore);
		yourScore.setText((int) current_score + "");
		highScore.setText((int) high_score + "");

	}

	// Button Listener for the customize view.
	public void addListenerOnCustButton() {

		customizeButton = (ImageButton) findViewById(R.id.imageButton1);

		customizeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				/******************************************************
				 * Starting the customize activity
				 ******************************************************/
				Log.d("Main Screen Activity", "Starting Main Activity");
				Intent customizeIntent = new Intent(MainScreenActivity.this,
						CustomizeActivity.class);
				startActivity(customizeIntent);

			}

		});

	}
//listener on Exit button
	public void addListenerOnExitButton() {

		exitButton = (Button) findViewById(R.id.bexit);

		exitButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				AlertDialog.Builder exitAlert = new AlertDialog.Builder(
						MainScreenActivity.this);
				exitAlert.setMessage("Are yeh sure yeh leavin' my Stadium?");
				exitAlert.setCancelable(true);
				exitAlert.setPositiveButton("Yah",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Log.d("Menu Option Selected",
										"User will now Exit from the app");
								MainScreenActivity.this.finish();
							}
						});
				exitAlert.setNegativeButton("Nah",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
				AlertDialog exitAlertDialog = exitAlert.create();
				exitAlertDialog.show();
			}

		});

	}
//button listener on Help button
	public void addListenerOnHelpButton() {

		helpButton = (Button) findViewById(R.id.bhelp);

		helpButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Log.d("Main Screen Activity", "Starting Main Activity");
				HelpDialogActivity hda = new HelpDialogActivity(
						MainScreenActivity.this);
				hda.show();

			}
		});

	}

	// Button Listener for the button to begin the game.
	public void addListenerOnStartButton() {

		startButton = (Button) findViewById(R.id.bstart);

		startButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				/******************************************************
				 * Starting the Game
				 ******************************************************/
				Log.d("Main Screen Activity", "Starting Game");
				Intent gameIntent = new Intent(MainScreenActivity.this,
						MainActivity.class);
				gameIntent.putExtra("GameOver", true);
				startActivity(gameIntent);

			}

		});

	}

	//verify what to do when the user presses the back button of the device
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();

		AlertDialog.Builder exitAlert = new AlertDialog.Builder(
				MainScreenActivity.this);
		exitAlert.setMessage("Are yeh sure yeh leavin' my Stadium?");
		exitAlert.setCancelable(true);
		exitAlert.setPositiveButton("Yah",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d("Menu Option Selected",
								"User will now Exit from the app");
						MainScreenActivity.this.finish();
					}
				});
		exitAlert.setNegativeButton("Nah",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog exitAlertDialog = exitAlert.create();
		exitAlert.show();

	}
}
