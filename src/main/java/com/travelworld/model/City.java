package com.travelworld.model;

import java.util.List;
import lombok.Data;

@Data
public class City {
    public String id;
    public String name;
    public Location location;
    public String countryName;
    public String iata;
    public int rank;
    public String countryId;
    public String dest;
    public List<String> airports;
    public List<String> images;
    public double popularity;
    public String regId;
    public String contId;
    public Object subId;
    public Object terId;
    public int con;
}