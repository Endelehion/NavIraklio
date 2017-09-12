package com.example.varda.naviraklio;

import android.app.Activity;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import static android.R.attr.subtitleTextAppearance;

public class MoviePicker extends AppCompatActivity {

    private ImageButton americanMadeImage, dunkirkImage, valerianImage, smurfsImage, despicableMe3Image, detroitImage, loganLuckyImage, hitmanImage;
    private TextView moviePickedTitle, moviePickedInfo, moviePickedDescription;
    private ArrayList<String> daysList, hoursList;
    private ImageView moviePickedImage;
    private ArrayAdapter daysAdapter, hoursAdapter;
    private ListView daysRadioList, hoursRadioList;
    private Button moviePickedCancelBtn,moviePickedConfirmBtn;
    int layoutId;
    String whichMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            layoutId = savedInstanceState.getInt("layoutIdKey", R.layout.activity_movie_picker);
            whichMovie = savedInstanceState.getString("whichMovieKey", "");
            hoursList = savedInstanceState.getStringArrayList("hoursListKey");
        } else {
            layoutId = R.layout.activity_movie_picker;
            whichMovie = "";
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
        dunkirkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichMovie = "Dunkirk";
                createMoviePanel(whichMovie);
            }
        });
        valerianImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichMovie = "Valerian";
                createMoviePanel(whichMovie);
            }
        });
        despicableMe3Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichMovie = "Despicable Me 3";
                createMoviePanel(whichMovie);
            }
        });
        loganLuckyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichMovie = "Logan Lucky";
                createMoviePanel(whichMovie);
            }
        });
        detroitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichMovie = "Detroit";
                createMoviePanel(whichMovie);
            }
        });
        hitmanImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichMovie = "Hitman";
                createMoviePanel(whichMovie);
            }
        });
        smurfsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichMovie = "Smurfs";
                createMoviePanel(whichMovie);
            }
        });
    }


    public void createMoviePanel(String movie) {
        layoutId = R.layout.movie_pick_confirm_supplemental;
        setContentView(layoutId);

        moviePickedConfirmBtn=(Button) findViewById(R.id.moviePickedConfirmBtn);
        moviePickedCancelBtn=(Button) findViewById(R.id.moviePickedCancelBtn);
        moviePickedTitle = (TextView) findViewById(R.id.moviePickedTitle);
        moviePickedImage = (ImageView) findViewById(R.id.moviePickedImage);
        moviePickedDescription = (TextView) findViewById(R.id.moviePickedDescription);
        moviePickedInfo = (TextView) findViewById(R.id.moviePickedInfo);
        moviePickedDescription.setMovementMethod(new ScrollingMovementMethod());
        daysRadioList = (ListView) findViewById(R.id.daysRadioList);
        hoursRadioList = (ListView) findViewById(R.id.hoursRadioList);
        daysList = new ArrayList<>();
        daysAdapter = new ArrayAdapter<>(MoviePicker.this, android.R.layout.simple_list_item_single_choice, daysList);
        hoursAdapter = new ArrayAdapter<>(MoviePicker.this, android.R.layout.simple_list_item_single_choice, hoursList);

        daysRadioList.setAdapter(daysAdapter);
        daysRadioList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        daysRadioList.setSaveEnabled(true);
        hoursRadioList.setAdapter(hoursAdapter);
        hoursRadioList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        hoursAdapter.notifyDataSetChanged();

        moviePickedConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dayString,hourString,cinemaString,movieDuration;

                if(daysRadioList.getCheckedItemPosition()!=-1){
                    if(hoursRadioList.getCheckedItemPosition()!=-1){
                        dayString=daysList.get(daysRadioList.getCheckedItemPosition());
                        hourString=hoursList.get(hoursRadioList.getCheckedItemPosition());
                        hourString=hoursList.get(hoursRadioList.getCheckedItemPosition()).substring(hourString.indexOf(":")-2);
                        cinemaString=hoursList.get(hoursRadioList.getCheckedItemPosition());
                        cinemaString=hoursList.get(hoursRadioList.getCheckedItemPosition()).substring(0,cinemaString.indexOf(":")-2);
                        int mins;

                        mins=Integer.parseInt(moviePickedInfo.getText().toString().
                                substring(moviePickedInfo.getText().toString().indexOf("Duration: ")+10,moviePickedInfo.getText().toString().indexOf("min")));
                        movieDuration=(mins/60)+":"+(mins%60);

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("moviePickedDayKey",dayString);
                        resultIntent.putExtra("moviePickedHourKey",hourString);
                        resultIntent.putExtra("moviePickedCinemaKey",cinemaString);
                        resultIntent.putExtra("moviePickedTitleKey",moviePickedTitle.getText());
                        resultIntent.putExtra("moviePickedDurationKey",movieDuration);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                }
            }
        });

        moviePickedCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutId = R.layout.activity_movie_picker;
                whichMovie = "";
                hoursList = new ArrayList<>();
                setContentView(layoutId);
                createMovieList();
            }
        });

        if (movie.equals("American Made")) {
            moviePickedImage.setImageResource(R.drawable.american_made_poster);

            daysList.add("Monday");
            daysList.add("Tuesday");
            daysList.add("Wednesday");
            daysList.add("Thursday");
            daysList.add("Friday");
            daysList.add("Saturday");
            daysList.add("Sunday");
            moviePickedTitle.setText("American Made");
            moviePickedInfo.setText("Rating: R \n Duration: 115min \n Genre: Action | Biography | Crime \n Directed by: Doug Liman");
            moviePickedDescription.setText("Barry Seal, a TWA pilot, is recruited by the CIA to provide reconnaissance on the burgeoning communist threat" +
                    " in Central America and soon finds himself in charge of one of the biggest covert CIA operations in the history of the United States." +
                    " The operation spawns the birth of the Medellin cartel and almost brings down the Reagan White House.");
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
        } else if (movie.equals("Dunkirk")) {
            moviePickedImage.setImageResource(R.drawable.dunkirk_poster);

            daysList.add("Monday");
            daysList.add("Tuesday");
            daysList.add("Wednesday");
            daysList.add("Thursday");
            daysList.add("Friday");
            daysList.add("Saturday");
            daysList.add("Sunday");
            moviePickedTitle.setText("Dunkirk");
            moviePickedInfo.setText("Rating: PG-13 \n Duration: 107min \n Genre: Drama | History \n Directed by: Christopher Nolan ");
            moviePickedDescription.setText("In May 1940, Germany advanced into France, trapping Allied troops on the beaches of Dunkirk. " +
                    "Under air and ground cover from British and French forces, troops were slowly and methodically evacuated from the beach using every serviceable naval and civilian vessel" +
                    " that could be found. At the end of this heroic mission, 330,000 French, British, Belgian and Dutch soldiers were safely evacuated.");
            daysRadioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    hoursList.clear();

                    hoursList.add("Odeon Talos 20:30");
                    hoursList.add("Odeon Talos 22:50");
                    hoursList.add("Kornaros 18:30");
                    hoursList.add("Kornaros 20:30");
                    hoursList.add("Kornaros 22:30");
                    hoursList.add("Texnopolis 20:30");
                    hoursList.add("Texnopolis 21:15");
                    hoursList.add("Texnopolis 22:30");

                    hoursAdapter.notifyDataSetChanged();
                    hoursRadioList.clearChoices();
                    hoursRadioList.setSelectionAfterHeaderView();
                }
            });

        } else if (movie.equals("Valerian")) {

            moviePickedImage.setImageResource(R.drawable.valerian_poster);

            daysList.add("Monday");
            daysList.add("Tuesday");
            daysList.add("Wednesday");
            daysList.add("Thursday");
            daysList.add("Friday");
            daysList.add("Saturday");
            daysList.add("Sunday");
            moviePickedTitle.setText("Valerian and the City of a Thousand Planets");
            moviePickedInfo.setText("Rating: PG-13 \n Duration: 137min \n Genre: Action | Sci-Fi | Fantasy \n Directed by: Luc Besson");
            moviePickedDescription.setText("In the 28th century, special operatives Valerian (Dane DeHaan) and Laureline work together to maintain order throughout the human territories." +
                    " Under assignment from the minister of defense, the duo embarks on a mission to Alpha, an ever-expanding metropolis where diverse species gather to share knowledge and culture. " +
                    "When a dark force threatens the peaceful city, Valerian and Laureline must race against time to identify the menace that also jeopardizes the future of the universe.");
            daysRadioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    hoursList.clear();
                    hoursList.add("Odeon Talos 19:10");
                    hoursList.add("Odeon Talos 20:00");
                    hoursList.add("Odeon Talos 22:00");
                    hoursList.add("Texnopolis 22:30");

                    hoursAdapter.notifyDataSetChanged();
                    hoursRadioList.clearChoices();
                    hoursRadioList.setSelectionAfterHeaderView();
                }
            });
        } else if (movie.equals("Despicable Me 3")) {

            moviePickedImage.setImageResource(R.drawable.despicable_me3_poster);

            daysList.add("Monday");
            daysList.add("Tuesday");
            daysList.add("Wednesday");
            daysList.add("Thursday");
            daysList.add("Friday");
            daysList.add("Saturday");
            daysList.add("Sunday");
            moviePickedTitle.setText("Despicable Me 3");
            moviePickedInfo.setText("Rating: PG \n Duration: 115min \n Genre: Animation | Action | Family \n Directed by: Balda, Coffin, Guillon");
            moviePickedDescription.setText("The mischievous Minions hope that Gru will return to a life of crime after the new boss of the Anti-Villain League fires him." +
                    " Instead, Gru decides to remain retired and travel to Freedonia to meet his long-lost twin brother for the first time. " +
                    "The reunited siblings soon find themselves in an uneasy alliance to take down the elusive Balthazar Bratt, " +
                    "a former 1980s child star who seeks revenge against the world.");
            daysRadioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    hoursList.clear();
                    String selectedDay = daysList.get(daysRadioList.getCheckedItemPosition());
                    if (selectedDay.equals("Thursday") || selectedDay.equals("Friday") || selectedDay.equals("Saturday") || selectedDay.equals("Sunday")) {
                        hoursList.add("Odeon Talos 16:30");
                        hoursList.add("Odeon Talos 17:00");
                        hoursList.add("Odeon Talos 17:30");
                        hoursList.add("3D Odeon Talos 18:00");
                        if (selectedDay.equals("Saturday") || selectedDay.equals("Sunday")) {
                            hoursList.add("Odeon Talos 18:30");
                        }
                        hoursList.add("Odeon Talos 19:00");
                        hoursList.add("Odeon Talos 19:30");
                        hoursList.add("3D Texnopolis 16:00");
                        hoursList.add("Texnopolis 16:30");
                        hoursList.add("Texnopolis 17:30");
                        hoursList.add("Texnopolis 19:30");
                        if (selectedDay.equals("Saturday") || selectedDay.equals("Sunday")) {
                            hoursList.add("Texnopolis 18:30");
                        }
                        hoursList.add("Kornaros 18:30");

                    } else {
                        hoursList.add("Odeon Talos 16:30");
                        hoursList.add("Odeon Talos 17:00");
                        hoursList.add("Odeon Talos 17:30");
                        hoursList.add("3D Odeon Talos 18:00");
                        hoursList.add("3D Texnopolis 16:00");
                        hoursList.add("Texnopolis 16:30");
                        hoursList.add("Texnopolis 17:30");
                        hoursList.add("Texnopolis 19:30");
                        hoursList.add("Kornaros 18:30");
                    }
                    hoursAdapter.notifyDataSetChanged();
                    hoursRadioList.clearChoices();
                    hoursRadioList.setSelectionAfterHeaderView();
                }
            });
        }
else if(movie.equals("Detroit")){
            moviePickedImage.setImageResource(R.drawable.detroit_poster);

            daysList.add("Monday");
            daysList.add("Tuesday");
            daysList.add("Wednesday");
            daysList.add("Thursday");
            daysList.add("Friday");
            daysList.add("Saturday");
            daysList.add("Sunday");
            moviePickedTitle.setText("Detroit");
            moviePickedInfo.setText("Rating: PG-13 \n Duration: 137min \n Genre: Action | Sci-Fi | Fantasy \n Directed by: Luc Besson");
            moviePickedDescription.setText("In the summer of 1967, rioting and civil unrest starts to tear apart the city of Detroit." +
                    " Two days later, a report of gunshots prompts the Detroit Police Department, the Michigan State Police and " +
                    "the Michigan Army National Guard to search and seize an annex of the nearby Algiers Motel. Several policemen" +
                    " start to flout procedure by forcefully and viciously interrogating guests to get a confession. By the end of the night," +
                    " three unarmed men are gunned down while several others are brutally beaten.");
            daysRadioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    hoursList.clear();
                    hoursList.add("Odeon Talos 19:40");
                    hoursList.add("Odeon Talos 22:30");
                    hoursList.add("Texnopolis 20:00");
                    hoursList.add("Texnopolis 23:00");


                    hoursAdapter.notifyDataSetChanged();
                    hoursRadioList.clearChoices();
                    hoursRadioList.setSelectionAfterHeaderView();
                }
            });
        }
        else if(movie.equals("Smurfs")){
            moviePickedImage.setImageResource(R.drawable.smurfs_poster);

            daysList.add("Monday");
            daysList.add("Tuesday");
            daysList.add("Wednesday");
            daysList.add("Thursday");
            daysList.add("Friday");
            daysList.add("Saturday");
            daysList.add("Sunday");
            moviePickedTitle.setText("Smurfs: The Lost Village");
            moviePickedInfo.setText("Rating: PG \n Duration: 115min \n Genre: Animation | Adventure | Family \n Directed by: Kelly Asbury");
                    moviePickedDescription.setText("Best friends Smurfette (Demi Lovato), Brainy (Danny Pudi), Clumsy (Jack McBrayer) " +
                            "and Hefty (Joe Manganiello) use a special map that guides them through the Forbidden Forest, an enchanted " +
                            "wonderland that's filled with magical creatures. Their adventure leads them on a course " +
                            "to discover the biggest secret in Smurf history as they race against time and the evil wizard Gargamel (Rainn Wilson) to find a mysterious village.");
            daysRadioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    hoursList.clear();
                    String selectedDay = daysList.get(daysRadioList.getCheckedItemPosition());
                        if (selectedDay.equals("Saturday") || selectedDay.equals("Sunday")) {
                            hoursList.add("Texnopolis 16:30");
                            hoursList.add("Texnopolis 18:30");
                        }
                        else{
                            hoursList.add("Texnopolis 16:30");
                        }
                        hoursList.add("Kornaros 18:30");
                    hoursAdapter.notifyDataSetChanged();
                    hoursRadioList.clearChoices();
                    hoursRadioList.setSelectionAfterHeaderView();
                }
            });
        }
        else if(movie.equals("Logan Lucky")){
            moviePickedImage.setImageResource(R.drawable.logan_lucky_poster);

            daysList.add("Monday");
            daysList.add("Tuesday");
            daysList.add("Wednesday");
            daysList.add("Thursday");
            daysList.add("Friday");
            daysList.add("Saturday");
            daysList.add("Sunday");
            moviePickedTitle.setText("Logan Lucky");
            moviePickedInfo.setText("Rating: PG-13 \n Duration: 115min \n Genre: Comedy | Crime | Drama \n Directed by: Steven Soderbergh");
                    moviePickedDescription.setText("West Virginia family man Jimmy Logan teams up with his one-armed brother Clyde " +
                                    "and sister Mellie to steal money from the Charlotte Motor Speedway in North Carolina. Jimmy also " +
                                    "recruits demolition expert Joe Bang to help them break into the track's underground system. " +
                                    "Complications arise when a mix-up forces the crew to pull off the heist during a popular NASCAR race while also trying to dodge a relentless FBI agent.");
            daysRadioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    hoursList.clear();
                    hoursList.add("Odeon Talos 22:50");
                    hoursList.add("Texnopolis 22:30");

                    hoursAdapter.notifyDataSetChanged();
                    hoursRadioList.clearChoices();
                    hoursRadioList.setSelectionAfterHeaderView();
                }
            });
        }
        else if(movie.equals("Hitman")){
            moviePickedImage.setImageResource(R.drawable.hitman_poster);

            daysList.add("Monday");
            daysList.add("Tuesday");
            daysList.add("Wednesday");
            daysList.add("Thursday");
            daysList.add("Friday");
            daysList.add("Saturday");
            daysList.add("Sunday");
            moviePickedTitle.setText("The Hitman's Bodyguard");
            moviePickedInfo.setText("Rating: R \n Duration: 111min \n Genre: Action | Comedy \n Directed by: Patrick Hughes");
                    moviePickedDescription.setText("The world's top protection agent is called upon to guard the life of his mortal enemy," +
                    " one of the world's most notorious hit men. The relentless bodyguard and manipulative assassin have been on the " +
                    "opposite end of the bullet for years and are thrown together for a wildly outrageous 24 hours. " +
                    "During their journey from England to the Hague, they encounter high-speed car chases, outlandish boat " +
                    "escapades and a merciless Eastern European dictator who is out for blood.");
            daysRadioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    hoursList.clear();
                    hoursList.add("Odeon Talos 21:00");

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
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("layoutIdKey", layoutId);
        savedInstanceState.putString("whichMovieKey", whichMovie);
        savedInstanceState.putStringArrayList("hoursListKey", hoursList);

    }


}

