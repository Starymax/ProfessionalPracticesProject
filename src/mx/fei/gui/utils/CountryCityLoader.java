package mx.fei.gui.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CountryCityLoader {
    private static final Logger LOGGER = Logger.getLogger(CountryCityLoader.class.getName());
    private static Map<String, List<String>> countryCityMap;

    private CountryCityLoader() {}

    public static Map<String, List<String>> getCountryCityMap() {
        if (countryCityMap == null) {
            loadCountriesCities();
        }
        return countryCityMap;
    }

    public static List<String> getCountries() {
        return new ArrayList<>(getCountryCityMap().keySet());
    }

    public static List<String> getCitiesByCountry(String country) {
        List<String> cities = getCountryCityMap().get(country);
        return cities != null ? cities : new ArrayList<>();
    }

    private static void loadCountriesCities() {
        countryCityMap = new LinkedHashMap<>();
        InputStream input = CountryCityLoader.class.getClassLoader().getResourceAsStream("countries_cities.json");
        if (input == null) {
            LOGGER.log(Level.SEVERE, "No se encontró countries_cities.json en resources");
        } else {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input,"UTF-8"))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                String json = stringBuilder.toString();
                Pattern blockPattern = Pattern.compile("\\{\\s*\"name\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"cities\"\\s*:\\s*\\[([^\\]]*)\\]\\s*\\}");
                Pattern cityPattern = Pattern.compile("\"([^\"]+)\"");
                Matcher blockMatcher = blockPattern.matcher(json);
                while (blockMatcher.find()) {
                    String countryName = blockMatcher.group(1);
                    String citiesBlock = blockMatcher.group(2);
                    List<String> cities = new ArrayList<>();
                    Matcher cityMatcher = cityPattern.matcher(citiesBlock);
                    while (cityMatcher.find()) {
                        cities.add(cityMatcher.group(1));
                    }
                    countryCityMap.put(countryName, cities);
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error al leer countries_cities.json", e);
            }
        }
    }
}