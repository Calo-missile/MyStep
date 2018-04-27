package com.hsc.mystep.hefeng.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    public String status;
    
    public Basic basic;
    
    private int aaa= 9;
    
    public AQI aqi;
    
    public Now now;
    
    public Suggestion suggestion;
    
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
