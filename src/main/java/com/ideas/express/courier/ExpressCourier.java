package com.ideas.express.courier;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExpressCourier {
    private Map<String, BigDecimal> cityWithRates;
    private Map<String, Integer> cityWithWeightLimitPerDay = new HashMap<String, Integer>();
    private Map<String, Integer> cityWithParcelcount = new HashMap<String, Integer>();


    public ExpressCourier(Map<String, BigDecimal> cityWithRates) {
        this.cityWithRates = cityWithRates;
        initializeCityPerDayWeight();
    }

    private void initializeCityPerDayWeight() {
        Set<String> cities = cityWithRates.keySet();
        for (String city : cities) {
            cityWithWeightLimitPerDay.put(city, new Integer(5000));
            cityWithParcelcount.put(city, new Integer(0));
        }
    }

    public BigDecimal calculatePrice(int parcelWeightInGrams, String cityName, Boolean isAfter3Pm) throws Exception {
        validateMaxParcelWeight(parcelWeightInGrams);
        int deductedWeight = cityWithWeightLimitPerDay.get(cityName) - parcelWeightInGrams;
        validateMaxWeightLimitForCity(deductedWeight);
        int multiplier = new BigDecimal(parcelWeightInGrams).divide(new BigDecimal(100), BigDecimal.ROUND_UP).intValue();
        cityWithWeightLimitPerDay.put(cityName, deductedWeight);
        Double cityRate = getCityRate(cityName, isAfter3Pm);
        Integer parcelCount = cityWithParcelcount.get(cityName);
        validateParcelCountPerCity(parcelCount);
        cityWithParcelcount.put(cityName, (parcelCount+1));
        return new BigDecimal(cityRate * (multiplier <= 0 ? 1 : multiplier)).setScale(2);
    }

    private void validateParcelCountPerCity(Integer parcelCount) throws Exception {
        if(parcelCount == 50){
            throw new Exception("Max parcel count reached");
        }
    }

    private Double getCityRate(String cityName, Boolean isAfter3Pm) {
        Double cityRate = cityWithRates.get(cityName).doubleValue();
        if(isAfter3Pm) {
            cityRate = cityRate + (cityRate * 0.2);
        }
        return cityRate;
    }

    private void validateMaxWeightLimitForCity(int deductedWeight) throws Exception {
        if(deductedWeight < 0) {
            throw new Exception("Maximum parcel weight exceeded 5000 grams!");
        }
    }

    private void validateMaxParcelWeight(int parcelWeightInGrams) throws Exception {
        if(parcelWeightInGrams > 1000){
            throw new Exception("Maximum parcel limit is 1000 grams.");
        }
    }

    public int getRemainingCityWeightFor(String city) {
        return cityWithWeightLimitPerDay.get(city);
    }

    public int getCountOfParcelFor(String city) {
        return cityWithParcelcount.get(city);
    }
}
