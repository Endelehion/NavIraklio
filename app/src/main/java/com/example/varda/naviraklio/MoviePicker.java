package com.example.varda.naviraklio;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;

public class MoviePicker extends AppCompatActivity {

    private ImageButton americanMadeImage, dunkirkImage, valerianImage, smurfsImage, despicableMe3Image, detroitImage, loganLuckyImage, hitmanImage;
    private TextView americanMadeDescription, dunkirkDescription, valerianDescription, smurfsDescription, despicableMe3Description, detroitDescription, loganLuckyDescription, hitmanDescription;
    private TextView americanMadeTitle, dunkirkTitle, valerianTitle, smurfsTitle, despicableMe3Title, detroitTitle, loganLuckyTitle, hitmanTitle, moviePickedTitle, moviePickInfo, moviePickDescription;
    private ScrollView scrollView;
    private ArrayList<String> daysList, hoursList;
    private ImageView moviePickedImage;
    private ArrayAdapter daysAdapter, hoursAdapter;
    private ListView daysRadioList, hoursRadioList;
    private boolean isListChecked;
    int layoutId;
    String whichMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            layoutId = savedInstanceState.getInt("layoutIdKey", R.layout.activity_movie_picker);
            whichMovie = savedInstanceState.getString("whichMovieKey", "");
            isListChecked = savedInstanceState.getBoolean("isListCheckedKey", false);
            hoursList = savedInstanceState.getStringArrayList("hoursListKey");
        } else {
            layoutId = R.layout.activity_movie_picker;
            whichMovie = "" ;
            isListChecked = false;
            hoursList = new ArrayList<>();
        }
        setContentView(layoutId);
        if (layoutId == R.layout.activity_movie_picker) {
            createMovieList();
        } else {
            createMoviePanel(whichMovie);
        }


    }


    public void createMovieList() {
        americanMadeImage = (ImageButton) findViewById(R.id.americanMadeImage);
        dunkirkImage = (ImageButton) findViewById(R.id.dunkirkImage);
        valerianImage = (ImageButton) findViewById(R.id.valerianImage);
        smurfsImage = (ImageButton) findViewById(R.id.smurfsImage);
        despicableMe3Image = (ImageButton) findViewById(R.id.despicableMe3Image);
        detroitImage = (ImageButton) findViewById(R.id.detroitImage);
        loganLuckyImage = (ImageButton) findViewById(R.id.loganLuckyImage);
        hitmanImage = (ImageButton) findViewById(R.id.hitmanImage);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        americanMadeDescription = (TextView) findViewById(R.id.americanMadeDescription);
        dunkirkDescription = (TextView) findViewById(R.id.dunkirkDescription);
        valerianDescription = (TextView) findViewById(R.id.valerianDescription);
        smurfsDescription = (TextView) findViewById(R.id.smurfsDescription);
        despicableMe3Description = (TextView) findViewById(R.id.despicableMe3Description);
        detroitDescription = (TextView) findViewById(R.id.detroitDescription);
        loganLuckyDescription = (TextView) findViewById(R.id.loganLuckyDescription);
        hitmanDescription = (TextView) findViewById(R.id.hitmanDescription);

        americanMadeTitle = (TextView) findViewById(R.id.americanMadeTitle);
        dunkirkTitle = (TextView) findViewById(R.id.dunkirkTitle);
        valerianTitle = (TextView) findViewById(R.id.valerianTitle);
        smurfsTitle = (TextView) findViewById(R.id.smurfsTitle);
        despicableMe3Title = (TextView) findViewById(R.id.despicableMe3Title);
        detroitTitle = (TextView) findViewById(R.id.detroitTitle);
        loganLuckyTitle = (TextView) findViewById(R.id.loganLuckyTitle);
        hitmanTitle = (TextView) findViewById(R.id.hitmanTitle);
        createTableListeners();
    }

    public void createTableListeners() {
        americanMadeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichMovie = "American Made";
                createMoviePanel(whichMovie);
            }
        });
    }


    public void createMoviePanel(String movie) {
        layoutId = R.layout.movie_pick_confirm_supplemental;
        setContentView(layoutId);


        moviePickedTitle = (TextView) findViewById(R.id.moviePickedTitle);
        moviePickedImage = (ImageView) findViewById(R.id.moviePickedImage);
        moviePickDescription = (TextView) findViewById(R.id.moviePickedDescription);

        if (movie.equals("American Made")) {


            moviePickedImage.setImageResource(R.drawable.american_made_poster);
            daysRadioList = (ListView) findViewById(R.id.daysRadioList);
            hoursRadioList = (ListView) findViewById(R.id.hoursRadioList);
            daysList = new ArrayList<>();

            daysList.add("Monday");
            daysList.add("Tuesday");
            daysList.add("Wednesday");
            daysList.add("Thursday");
            daysList.add("Friday");
            daysList.add("Saturday");
            daysList.add("Sunday");
            moviePickedTitle.setText("American Made");


            daysAdapter = new ArrayAdapter<>(MoviePicker.this, android.R.layout.simple_list_item_single_choice, daysList);
            hoursAdapter = new ArrayAdapter<>(MoviePicker.this, android.R.layout.simple_list_item_single_choice, hoursList);

            daysRadioList.setAdapter(daysAdapter);
            daysRadioList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            daysRadioList.setSaveEnabled(true);


            hoursRadioList.setAdapter(hoursAdapter);
            hoursRadioList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            hoursAdapter.notifyDataSetChanged();



            daysRadioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    hoursList.clear();
                    String selectedDay = daysList.get(daysRadioList.getCheckedItemPosition());
                    if (selectedDay.equals("Thursday") || selectedDay.equals("Friday") || selectedDay.equals("Saturday") || selectedDay.equals("Sunday")) {
                        hoursList.add("Odeon Talos 17:20");
                        hoursList.add("Odeon Talos 20:15");
                        hoursList.add("Odeon Talos 21:40");
                        hoursList.add("Odeon Talos 22:30");
                        hoursList.add("Odeon Talos 23:00");
                        hoursList.add("Kornaros 20:15");
                        hoursList.add("Kornaros 22:30");
                        hoursList.add("Texnopolis 20:15");
                        hoursList.add("Texnopolis 22:30");
                    } else {
                        hoursList.add("Odeon Talos 20:15");
                        hoursList.add("Odeon Talos 21:40");
                        hoursList.add("Odeon Talos 22:30");
                        hoursList.add("Odeon Talos 23:00");
                        hoursList.add("Kornaros 20:15");
                        hoursList.add("Kornaros 22:30");
                        hoursList.add("Texnopolis 20:15");
                        hoursList.add("Texnopolis 22:30");
                    }
                    hoursAdapter.notifyDataSetChanged();
                    hoursRadioList.clearChoices();
                    hoursRadioList.setSelectionAfterHeaderView();
                }
            });


        }


    }

    /* @Override
     public void onSaveInstanceState(Bundle outState) {
         outState.putInt("layoutIdKey", layoutId);
         outState.putString("whichMovieKey", whichMovie);
         super.onSaveInstanceState(outState);
     }

     @Override
     public void onRestoreInstanceState(Bundle outState) {
         layoutId = outState.getInt("layoutIdKey");
         whichMovie = outState.getString("whichMovieKey");
         super.onRestoreInstanceState(outState);
     }*/
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("layoutIdKey", layoutId);
        savedInstanceState.putString("whichMovieKey", whichMovie);
        savedInstanceState.putStringArrayList("hoursListKey", hoursList);

    }


}

