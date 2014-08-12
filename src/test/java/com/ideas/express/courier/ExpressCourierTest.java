package com.ideas.express.courier;


import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class ExpressCourierTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldInitializeExpressCourierWithCititesAlongWithTheirRatesPerCourier(){
        //Given
        Map<String, BigDecimal> cityWithRates = getCityRatesMap();

        //When
        ExpressCourier expressCourier = new ExpressCourier(cityWithRates);

        //Then
        assertNotNull(expressCourier);
    }

    @Test
    public void shouldCalculateCourierPriceAccordingToCityRatePer100Grams() throws Exception {
        //Given
        Map<String, BigDecimal> cityWithRates = getCityRatesMap();
        ExpressCourier expressCourier = new ExpressCourier(cityWithRates);

        //When
        BigDecimal charges = expressCourier.calculatePrice(100,"Banglore", false);

        //Then
        Assert.assertEquals(new BigDecimal(30.00).setScale(2), charges.setScale(2));
    }

    @Test
    public void shouldCalculateCourierPriceAsPer100GramsWhenTheWeightIsLessThan100gms() throws Exception {
        //Given
        Map<String, BigDecimal> cityWithRates = getCityRatesMap();
        ExpressCourier expressCourier = new ExpressCourier(cityWithRates);

        //When
        BigDecimal charges = expressCourier.calculatePrice(60,"Banglore", false);

        //Then
        Assert.assertEquals(new BigDecimal(30.00).setScale(2), charges.setScale(2));
    }

    @Test
    public void shouldCalculateCourierPriceInMultiplesOf100gramsWhenTheWeightIs140() throws Exception {
        //Given
        Map<String, BigDecimal> cityWithRates = getCityRatesMap();
        ExpressCourier expressCourier = new ExpressCourier(cityWithRates);

        //When
        BigDecimal charges = expressCourier.calculatePrice(140,"Banglore", false);

        //Then
        Assert.assertEquals(new BigDecimal(60.00).setScale(2), charges.setScale(2));
    }

    @Test
    public void shouldShowErrorMessageWhenTheWeightExceeds1Kg() throws Exception {
        //Expected
        expectedException.expect(Exception.class);
        expectedException.expectMessage("Maximum parcel limit is 1000 grams.");

        //When
        new ExpressCourier(getCityRatesMap()).calculatePrice(1001, "Banglore", false);
    }

    @Test
    public void shouldDeductCourierWeightForCityToDispatch() throws Exception {
        //Given
        Map<String, BigDecimal> cityWithRates = getCityRatesMap();
        ExpressCourier expressCourier = new ExpressCourier(cityWithRates);
        expressCourier.calculatePrice(60,"Banglore", false);

        //When
        int remainingWeight = expressCourier.getRemainingCityWeightFor("Banglore");

        //Then
        Assert.assertEquals(4940, remainingWeight);
    }

    @Test
    public void shouldShowErrorMsgWhenCityReachesToItsLimitPerDay() throws Exception {

        //Expected
        expectedException.expect(Exception.class);
        expectedException.expectMessage("Maximum parcel weight exceeded 5000 grams!");

        //When
        ExpressCourier expressCourier = new ExpressCourier(getCityRatesMap());
        expressCourier.calculatePrice(1000, "Banglore", false);
        expressCourier.calculatePrice(1000, "Banglore", false);
        expressCourier.calculatePrice(1000, "Banglore", false);
        expressCourier.calculatePrice(1000, "Banglore", false);
        expressCourier.calculatePrice(1000, "Banglore", false);
        expressCourier.calculatePrice(1, "Banglore", false);
    }

    @Test
    public void shouldDeductCourierWeightByApplying20PercentAfter3PMAndBefore9PM() throws Exception {
        //Given
        Map<String, BigDecimal> cityWithRates = getCityRatesMap();
        ExpressCourier expressCourier = new ExpressCourier(cityWithRates);

        //When
        BigDecimal charges = expressCourier.calculatePrice(225, "Delhi", true);

        //Then
        Assert.assertEquals(126, charges.intValue());
        Assert.assertEquals(4775, expressCourier.getRemainingCityWeightFor("Delhi"));
    }

    @Test
    public void shouldGetTheCountOfParcels() throws Exception {
        //Given
        Map<String, BigDecimal> cityWithRates = getCityRatesMap();
        ExpressCourier expressCourier = new ExpressCourier(cityWithRates);
        expressCourier.calculatePrice(100, "Banglore", true);
        expressCourier.calculatePrice(100, "Banglore", true);

        //When
        int parcelCount = expressCourier.getCountOfParcelFor("Banglore");


        //Then
        Assert.assertEquals(2, parcelCount);
    }

    //Mock can be used!!!
    @Test
    public void shouldShowErrorMsgWhenParcelCountExceeds50() throws Exception {
        //Expected
        expectedException.expect(Exception.class);
        expectedException.expectMessage("Max parcel count reached");

        //When
        ExpressCourier expressCourier = new ExpressCourier(getCityRatesMap());
        for (int i=0; i<51; i++){
            expressCourier.calculatePrice(1, "Banglore", false);
        }
    }

    private Map<String, BigDecimal> getCityRatesMap() {
        Map<String, BigDecimal> cityWithRates = new HashMap<String, BigDecimal>();
        cityWithRates.put("Banglore", new BigDecimal(30.00).setScale(2));
        cityWithRates.put("Delhi", new BigDecimal(35.00).setScale(2));
        cityWithRates.put("Goa", new BigDecimal(40.00).setScale(2));
        return cityWithRates;
    }

}
