package com.travelworld.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeastCityChoice {

    City city;
    double leastDistanceFromStartCity;

}