package com.travelworld;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelworld.model.City;
import com.travelworld.model.CityList;
import com.travelworld.model.LeastCityChoice;
import com.travelworld.model.Location;

public class App {
    public static void main(String[] args) {

        try {
            /**
             * parsing cities.json
             */
            CityList cities = jsonDeserializerAndContinentFiller(
                    new File("./src/main/java/com/travelworld/cities.json"));

            Map<String, City> cityMap = new HashMap<>();

            for (City c : cities.getCities()) {
                cityMap.put(c.getId(), c);
            }

            Scanner sc = new Scanner(System.in);
            System.out.println("enter the starting city id, for e.g. BOM for BOMBAY");
            String startCity = sc.next();
            sc.close();

            Map<String, LeastCityChoice> citiesForTsp = getCitiesForTsp(cities, cityMap.get(startCity));

            int n = citiesForTsp.size();

            City[] tspCandidates = new City[n];
            int k = 0;
            for (Entry<String, LeastCityChoice> c : citiesForTsp.entrySet()) {
                tspCandidates[k] = c.getValue().getCity();
                k++;
            }

            double[][] graph = new double[n][n];

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i == j) {
                        graph[i][j] = 0;
                    } else {
                        graph[i][j] = getDistanceBtwTwoCities(tspCandidates[i].getLocation(),
                                tspCandidates[j].getLocation());
                    }
                }
            }

            boolean[] v = new boolean[n];

            v[0] = true;
            double ans = Double.MAX_VALUE;

            for (int b = 0; b < n; b++) {
                System.out.print(tspCandidates[b].getId());
                System.out.print("(");
                System.out.print(tspCandidates[b].getName());
                System.out.print(" ,");
                System.out.print(tspCandidates[b].getContId());
                System.out.print(")");
                if (b < n - 1) {
                    System.out.print(" --> ");
                }
            }

            System.out.println();

            System.out.println("Distance Travelled :" + travellingAllCities(graph, v, 0, n, 1, 0, ans) + " kms");

        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * this function find the local minima, calculates city with least distance from
     * starting city in every continent
     * 
     * for example we have just 3 cities, now all these 3 cities are going to create
     * a triangle(3 sided polygon) from their path, this will result in getting
     * minimum distances from starting city
     * 
     * 
     * @param cities
     * @param startCity
     * @return 6 cities which will be candidates for applying travelling salesman
     *         problem to find global minima
     */
    static Map<String, LeastCityChoice> getCitiesForTsp(CityList cities, City startCity) {

        Map<String, LeastCityChoice> continentCityMap = new LinkedHashMap<>();

        continentCityMap.put(startCity.getContId(), new LeastCityChoice(startCity, 0));

        for (City c : cities.getCities()) {
            double distanceBtwCurrStart = getDistanceBtwTwoCities(startCity.getLocation(), c.getLocation());

            if (!continentCityMap.containsKey(c.getContId())) {

                continentCityMap.put(c.getContId(), new LeastCityChoice(c, distanceBtwCurrStart));
            } else if (continentCityMap.containsKey(c.getContId())
                    && distanceBtwCurrStart < getDistanceBtwTwoCities(startCity.getLocation(),
                            continentCityMap.get(c.getContId()).getCity().getLocation())) {

                continentCityMap.put(c.getContId(), new LeastCityChoice(c, distanceBtwCurrStart));
            }
        }

        return continentCityMap;

    }

    /**
     * travelling salesman problem to find the global minima, minimizing the sum of
     * travelling distances, visiting each chosen cities
     * 
     * @param graph
     * @param v
     * @param currPos
     * @param n
     * @param count
     * @param cost
     * @param ans
     * @return
     */
    static double travellingAllCities(double[][] graph, boolean[] v, int currPos, int n, int count, double cost,
            double ans) {

        if (count == n && graph[currPos][0] > 0) {
            ans = Math.min(ans, cost + graph[currPos][0]);
            return ans;
        }

        for (int i = 0; i < n; i++) {
            if (v[i] == false && graph[currPos][i] > 0) {

                v[i] = true;
                ans = travellingAllCities(graph, v, i, n, count + 1, cost + graph[currPos][i], ans);

                v[i] = false;
            }
        }
        return ans;
    }

    static CityList jsonDeserializerAndContinentFiller(File file)
            throws JsonParseException, JsonMappingException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(file, CityList.class);

    }

    /**
     * finding distance between 2 cities given their location (lattitude and
     * longitude)
     * 
     * @param l1
     * @param l2
     * @return
     */
    static double getDistanceBtwTwoCities(Location l1, Location l2) {

        int R = 6371;
        double dLat = deg2rad(l2.getLat() - l1.getLat());
        double dLon = deg2rad(l2.getLon() - l1.getLon());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(l1.getLat()))
                * Math.cos(deg2rad(l2.getLat())) * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    static double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }
}
