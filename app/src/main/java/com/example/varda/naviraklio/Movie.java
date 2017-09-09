package com.example.varda.naviraklio;

import java.util.Date;

/**
 * Created by Endelehion on 9/9/2017.
 */

public class Movie {

    Place cinemaPlace;
    Date startTime;
    float duration;
    String movieTitle;

    public Movie(String movieTitle, Place cinemaPlace, Date startTime, float duration) {
        this.cinemaPlace = cinemaPlace;
        this.startTime = startTime;
        this.duration = duration;
        this.movieTitle = movieTitle;
    }

    public Place getCinemaPlace() {
        return cinemaPlace;
    }

    public void setCinemaPlace(Place cinemaPlace) {
        this.cinemaPlace = cinemaPlace;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }
}
