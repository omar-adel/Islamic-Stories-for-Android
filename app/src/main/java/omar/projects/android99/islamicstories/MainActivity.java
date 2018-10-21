package omar.projects.android99.islamicstories;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public Button btnAllStories;
	public Button btnFavStories;
	public ImageButton btnShareStory;
	public ImageButton btnFavStory;
	public ImageButton btnNext;
	public ImageButton btnPrevious;
	public ImageButton btnList;
	public SeekBar storyProgressBar;
	public TextView storyTitleLabel;
	public TextView storyDetails;

	public ListView listview;
	public ArrayAdapter adapter_of_listview;

	public int now_view = R.layout.activity_main;
	public int currentStoryIndex = 0;
	public int text_size = 18;
	public boolean fav = false;

	int is_oncreate = 0;

	public ArrayList<String> items_labels = new ArrayList<String>();
	public ArrayList<String> items_details = new ArrayList<String>();
	public ArrayList<Integer> ids_fav = new ArrayList<Integer>();

	public ArrayList<String> items_fav_labels = new ArrayList<String>();
	public ArrayList<String> items_fav_details = new ArrayList<String>();

	SharedPreferences stored_variables;
	SharedPreferences.Editor editor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		is_oncreate = 1;

		load_prefs();
		switch_screen();

	}

	@Override
	public void onResume() {

		super.onResume();

		if (is_oncreate == 0) {

			load_prefs();
			switch_screen();

		}

	}

	@Override
	public void onPause() {
		super.onPause();
		save_prefs();

	}

	public void switch_screen() {

		switch (now_view) {

			case R.layout.activity_main:
				initialize_main_screen();
				break;

			case R.layout.items_list:
				initialize_list();
				break;

			case R.layout.item_details:
				initialize_datails_layout();
				break;
		}

	}

	public void initialize_main_screen()

	{
		now_view = R.layout.activity_main;
		setContentView(now_view);

		btnAllStories = (Button) findViewById(R.id.all_stories);
		btnFavStories = (Button) findViewById(R.id.fav_stories);

		btnAllStories.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				fav = false;
				initialize_list();

			}
		});

		btnFavStories.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0)

			{

				fav = true;
				initialize_list();

			}

		});

	}

	public void initialize_list() {

		now_view = R.layout.items_list;

		setContentView(now_view);

		listview = (ListView) findViewById(R.id.list);

		load_labels_and_details();

		if (!fav)

		{

			adapter_of_listview = new ArrayAdapter<String>(this,
					R.layout.item_of_list, R.id.storyTitle, items_labels);

		} else {

			load_fav_labels_and_details();
			adapter_of_listview = new ArrayAdapter<String>(this,
					R.layout.item_of_list, R.id.storyTitle, items_fav_labels);
		}
		initialize_list_view();

	}

	public void initialize_list_view() {

		String headerText = "الرئيسية";
		Button home_btn = new Button(this);
		home_btn.setText(headerText);
		home_btn.setTextSize(20.0f);
		home_btn.setTextColor(Color.parseColor("#000000"));
		home_btn.setBackgroundResource(R.drawable.header_btn_rounded_corner);
		home_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				initialize_main_screen();
			}
		});

		listview.addHeaderView(home_btn, null, false);

		listview.setAdapter(adapter_of_listview);

		// selecting single ListView item
		// listening to single listitem click
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// getting listitem index
				currentStoryIndex = position-1;

				initialize_datails_layout();

			}
		});

	}

	public void initialize_datails_layout()

	{
		load_labels_and_details();
		load_fav_labels_and_details();

		now_view = R.layout.item_details;
		setContentView(now_view);

		item_click_function();
	}

	public void item_click_function()

	{

		btnList = (ImageButton) findViewById(R.id.btn_list);
		storyTitleLabel = (TextView) findViewById(R.id.storyTitle);

		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		btnShareStory = (ImageButton) findViewById(R.id.btnShare);
		btnFavStory = (ImageButton) findViewById(R.id.btnFav);
		storyDetails = (TextView) findViewById(R.id.storyText);
		storyProgressBar = (SeekBar) findViewById(R.id.storyProgressBar);
		storyProgressBar.setProgress(text_size);

		storyDetails.setTextSize(text_size);

		storyDetails.setMovementMethod(new ScrollingMovementMethod());

		storyProgressBar.setOnSeekBarChangeListener(customSeekBarListener);

		if (!fav) {
			storyTitleLabel.setText(items_labels.get(currentStoryIndex));
			storyDetails.setText(items_details.get(currentStoryIndex));

		} else {
			storyTitleLabel.setText(items_fav_labels.get(currentStoryIndex));
			storyDetails.setText(items_fav_details.get(currentStoryIndex));
		}

		set_btn_fav_story_img();

		btnShareStory.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent("android.intent.action.SEND");
				intent.setType("text/plain");
				intent.putExtra("android.intent.extra.TEXT", storyDetails
						.getText().toString());

				try {
					startActivity(Intent.createChooser(intent,
							"Share this story"));
				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(getApplicationContext(),
							"No apps available to share", Toast.LENGTH_LONG)
							.show();
				}

			}
		});

		btnFavStory.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (!fav) {

					if (ids_fav.indexOf(currentStoryIndex) == -1)

					{
						ids_fav.add(currentStoryIndex);
						saveArray("ids_fav", ids_fav);

						items_fav_labels.add(items_labels
								.get(currentStoryIndex));
						items_fav_details.add(items_details
								.get(currentStoryIndex));

						btnFavStory
								.setImageResource(R.drawable.btn_favourite_gold);
						Toast.makeText(getApplicationContext(),
								"تمت الإضافة إلي قائمة المفضلات",
								Toast.LENGTH_SHORT).show();
					}

					else

					{

						ids_fav.remove(ids_fav.indexOf(currentStoryIndex));
						saveArray("ids_fav", ids_fav);
						items_fav_labels.remove(items_labels
								.get(currentStoryIndex));
						items_fav_details.remove(items_details
								.get(currentStoryIndex));
						btnFavStory.setImageResource(R.drawable.btn_favourite);
						Toast.makeText(getApplicationContext(),
								"تم الحذف من  قائمة المفضلات",
								Toast.LENGTH_SHORT).show();
					}

				}

				else {

					items_fav_labels.remove(items_labels.get(ids_fav
							.get(currentStoryIndex)));
					items_fav_details.remove(items_details.get(ids_fav
							.get(currentStoryIndex)));
					ids_fav.remove(currentStoryIndex);
					saveArray("ids_fav", ids_fav);

					btnFavStory.setImageResource(R.drawable.btn_favourite);
					Toast.makeText(getApplicationContext(),
							"تم الحذف من  قائمة المفضلات", Toast.LENGTH_SHORT)
							.show();

					if (currentStoryIndex != 0) {
						currentStoryIndex = currentStoryIndex - 1;
					}

					if (items_fav_labels.size() == 0) {

						initialize_list();

						Toast.makeText(getApplicationContext(),
								"لا توجد مفضلات", Toast.LENGTH_SHORT).show();

					} else {
						if (((currentStoryIndex >= 0) && (currentStoryIndex < items_fav_labels
								.size())) || (currentStoryIndex == 0)) {
							storyTitleLabel.setText(items_fav_labels
									.get(currentStoryIndex));
							storyDetails.setText(items_fav_details
									.get(currentStoryIndex));
							set_btn_fav_story_img();

						}
					}
				}
			}

		});

		btnNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!fav) {
					next_function(items_labels, items_details);
				} else {
					next_function(items_fav_labels, items_fav_details);
				}

			}
		});

		btnPrevious.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (!fav) {
					previous_function(items_labels, items_details);
				} else {
					previous_function(items_fav_labels, items_fav_details);
				}

			}
		});

		// العودة الي القائمة
		btnList.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				now_view = R.layout.items_list;
				setContentView(now_view);
				initialize_list();

			}
		});

	}

	public void next_function(ArrayList<String> items_lab,
							  ArrayList<String> items_det)

	{

		if (currentStoryIndex < (items_lab.size() - 1))

		{
			currentStoryIndex = currentStoryIndex + 1;
		} else {
			currentStoryIndex = 0;
		}

		storyTitleLabel.setText(items_lab.get(currentStoryIndex));
		storyDetails.setText(items_det.get(currentStoryIndex));

		set_btn_fav_story_img();

	}

	public void previous_function(ArrayList<String> items_lab,
								  ArrayList<String> items_det)

	{

		if (currentStoryIndex > 0) {

			currentStoryIndex = currentStoryIndex - 1;

		} else {

			currentStoryIndex = items_lab.size() - 1;

		}

		storyTitleLabel.setText(items_lab.get(currentStoryIndex));
		storyDetails.setText(items_det.get(currentStoryIndex));

		set_btn_fav_story_img();

	}

	public void set_btn_fav_story_img() {

		if (!fav) {

			if (ids_fav.indexOf(currentStoryIndex) == -1) {
				btnFavStory.setImageResource(R.drawable.btn_favourite);
			} else {
				btnFavStory.setImageResource(R.drawable.btn_favourite_gold);
			}
		}

		else {
			btnFavStory.setImageResource(R.drawable.btn_favourite_gold);
		}

	}

	public void load_prefs() {

		stored_variables = getPreferences(MODE_PRIVATE);
		now_view = stored_variables.getInt("now_view", R.layout.activity_main);
		currentStoryIndex = stored_variables.getInt("currentStoryIndex", 0);
		text_size = stored_variables.getInt("text_size", 18);
		fav = stored_variables.getBoolean("fav", false);

	}

	public void save_prefs() {

		stored_variables = getPreferences(MODE_PRIVATE);
		editor = stored_variables.edit();
		editor.putInt("now_view", now_view);
		editor.putInt("currentStoryIndex", currentStoryIndex);
		editor.putInt("text_size", text_size);
		editor.putBoolean("fav", fav);

		// Commit the edits!
		editor.commit();

	}

	/**
	 * Loads a JSONArray from shared preferences and converts it to an
	 * ArrayList<Integer>
	 *
	 * @param String
	 *            key Preference key for SharedPreferences
	 * @return ArrayList<Integer> containing the saved values from the JSONArray
	 */

	public ArrayList<Integer> getArray(String key_arrayName) {
		stored_variables = getPreferences(MODE_PRIVATE);
		ArrayList<Integer> array = new ArrayList<Integer>();
		String jArrayString = stored_variables.getString(key_arrayName,
				"NOPREFSAVED");
		if (jArrayString.matches("NOPREFSAVED"))
			return array;
		else {
			try {
				JSONArray jArray = new JSONArray(jArrayString);
				for (int i = 0; i < jArray.length(); i++) {
					array.add(jArray.getInt(i));
				}
				return array;
			} catch (JSONException e) {
				return array;
			}
		}
	}

	/**
	 * Converts the provided ArrayList<Integer> into a JSONArray and saves it as
	 * a single string in the apps shared preferences
	 *
	 * @param String
	 *            key Preference key for SharedPreferences
	 * @param array
	 *            ArrayList<Integer> containing the list items
	 */
	public void saveArray(String key, ArrayList<Integer> array) {
		JSONArray jArray = new JSONArray(array);
		stored_variables = getPreferences(MODE_PRIVATE);
		editor = stored_variables.edit();
		editor.remove(key);
		editor.putString(key, jArray.toString());
		editor.commit();
	}

	public void load_labels_and_details() {
		String[] items_labels_res = getResources().getStringArray(
				R.array.titles);

		items_labels = new ArrayList<String>();

		for (String s : items_labels_res) {
			items_labels.add(s);
		}

		String[] items_details_res = getResources().getStringArray(
				R.array.details);
		items_details = new ArrayList<String>();
		for (String s : items_details_res) {
			items_details.add(s);
		}

	}

	public void load_fav_labels_and_details()

	{
		ids_fav = new ArrayList<Integer>();
		items_fav_labels = new ArrayList<String>();
		items_fav_details = new ArrayList<String>();
		ids_fav = getArray("ids_fav");

		for (int i = 0; i < ids_fav.size(); i++) {

			items_fav_labels.add(items_labels.get(ids_fav.get(i)));
			items_fav_details.add(items_details.get(ids_fav.get(i)));
		}
	}

	public SeekBar.OnSeekBarChangeListener customSeekBarListener = new SeekBar.OnSeekBarChangeListener() {

		int p = 1;

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
									  boolean fromUser) {

			text_size = progress;
			storyDetails.setTextSize(text_size);

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

			if (text_size < p) {
				text_size = p;
				storyProgressBar.setProgress(text_size);
			}

		}

	};

}