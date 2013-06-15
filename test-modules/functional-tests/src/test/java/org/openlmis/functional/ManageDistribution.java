/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.WebElement;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;
import org.testng.annotations.Listeners;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestBase.fail;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageDistribution extends TestCaseHelper {

  public static final String NONE_ASSIGNED = "--None Assigned--";
  public static final String SELECT_DELIVERY_ZONE = "--Select Delivery Zone--";
  public static final String periodDisplayedByDefault = "Period14";
  public static final String periodNotToBeDisplayedInDropDown = "Period1";

  @BeforeMethod(groups = {"functional2"})
  public void setUp() throws Exception {
    super.setup();
  }


  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
  public void testManageDistributionWithSingleDeliveryZone(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                                           String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                                           String facilityCodeFirst, String facilityCodeSecond,
                                                           String programFirst, String programSecond, String schedule, String period, Integer totalNumberOfPeriods) throws Exception {

    List<String> rightsList = new ArrayList<String>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRForDistribution(true, programFirst, userSIC, "200", "openLmis", rightsList, programSecond);
    setupDataForDeliveryZone(deliveryZoneCodeFirst, deliveryZoneCodeSecond,
      deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);


    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();
    verifyElementsPresent(distributionPage);

    String defaultDistributionZoneValuesToBeVerified = NONE_ASSIGNED;
    verifySelectedOptionFromDeliveryZoneSelectField(distributionPage, defaultDistributionZoneValuesToBeVerified);

    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);

    homePage.navigateHomePage();
    homePage.navigatePlanDistribution();

    List<String> distributionZoneValuesToBeVerified = new ArrayList<String>();
    distributionZoneValuesToBeVerified.add(deliveryZoneNameFirst);
    distributionZoneValuesToBeVerified.add(deliveryZoneNameSecond);
    verifyDeliveryZoneSelectFieldValues(distributionPage, distributionZoneValuesToBeVerified);

    String defaultProgramValuesToBeVerified = NONE_ASSIGNED;
    verifySelectedOptionFromProgramSelectField(distributionPage, defaultProgramValuesToBeVerified);

    String defaultPeriodValuesToBeVerified = NONE_ASSIGNED;
    verifySelectedOptionFromPeriodSelectField(distributionPage, defaultPeriodValuesToBeVerified);


    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    List<String> firstProgramValuesToBeVerified = new ArrayList<String>();
    firstProgramValuesToBeVerified.add(programFirst);
    verifyProgramSelectFieldValues(distributionPage, firstProgramValuesToBeVerified);
    verifySelectedOptionFromPeriodSelectField(distributionPage, defaultPeriodValuesToBeVerified);


    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameSecond);
    List<String> secondProgramValuesToBeVerified = new ArrayList<String>();
    secondProgramValuesToBeVerified.add(programSecond);
    verifyProgramSelectFieldValues(distributionPage, secondProgramValuesToBeVerified);
    verifySelectedOptionFromPeriodSelectField(distributionPage, defaultPeriodValuesToBeVerified);


    distributionPage.selectValueFromProgram(programSecond);
    List<String> periodValuesToBeVerified = new ArrayList<String>();
    verifySelectedOptionFromPeriodSelectField(distributionPage, periodDisplayedByDefault);
    for (int counter = 2; counter <= totalNumberOfPeriods; counter++) {
      String periodWithCounter = period + counter;
      periodValuesToBeVerified.add(periodWithCounter);
    }
    verifyPeriodSelectFieldValuesPresent(distributionPage, periodValuesToBeVerified);
    verifyPeriodSelectFieldValuesNotPresent(distributionPage, periodNotToBeDisplayedInDropDown);

    distributionPage.selectValueFromPeriod(periodDisplayedByDefault);

    verifySelectedOptionFromDeliveryZoneSelectField(distributionPage, deliveryZoneNameSecond);
    verifySelectedOptionFromProgramSelectField(distributionPage, programSecond);
    verifySelectedOptionFromPeriodSelectField(distributionPage, periodDisplayedByDefault);

    distributionPage.selectValueFromDeliveryZone(SELECT_DELIVERY_ZONE);
    verifySelectedOptionFromProgramSelectField(distributionPage, defaultProgramValuesToBeVerified);
    verifySelectedOptionFromPeriodSelectField(distributionPage, defaultPeriodValuesToBeVerified);

    distributionPage.clickProceed();
    verifySubOptionsOfProceedButton(distributionPage);

  }


  private void verifyElementsPresent(DistributionPage distributionPage) {
    assertTrue("selectDeliveryZoneSelectBox should be present", distributionPage.getSelectDeliveryZoneSelectBox().isDisplayed());
    assertTrue("selectProgramSelectBox should be present", distributionPage.getSelectProgramSelectBox().isDisplayed());
    assertTrue("selectPeriodSelectBox should be present", distributionPage.getSelectPeriodSelectBox().isDisplayed());
    assertTrue("proceedButton should be present", distributionPage.getProceedButton().isDisplayed());
  }


  private void verifyDeliveryZoneSelectFieldValues(DistributionPage distributionPage, List<String> valuesToBeVerified) {
    testWebDriver.waitForElementToAppear(distributionPage.getSelectDeliveryZoneSelectBox());
    List<WebElement> selectFieldValues = distributionPage.getAllSelectOptionsFromDeliveryZone();
    int valuesInSelectFieldCounter = 0;
    int valuesToBeVerifiedCounter = 0;
    int finalCounter = 0;
    int counterOfFirstSelectValueToBeSkipped = 0;
    for (String values : valuesToBeVerified)
      valuesToBeVerifiedCounter++;
    for (WebElement webElement : selectFieldValues)
      valuesInSelectFieldCounter++;

    if (valuesToBeVerifiedCounter == valuesInSelectFieldCounter - 1) {
      for (WebElement webElement : selectFieldValues) {
        counterOfFirstSelectValueToBeSkipped++;
        if (counterOfFirstSelectValueToBeSkipped != 1) {
          for (String values : valuesToBeVerified) {
            if (values.equalsIgnoreCase(webElement.getText().trim())) {
              finalCounter++;
            }
          }

        }
      }
    } else {
      fail("Values in select field are not same in number as values to be verified");
    }
    assertEquals(valuesToBeVerifiedCounter, finalCounter);
    assertEquals(valuesInSelectFieldCounter - 1, finalCounter);
  }

  private void verifyProgramSelectFieldValues(DistributionPage distributionPage, List<String> valuesToBeVerified) {
    testWebDriver.waitForElementToAppear(distributionPage.getSelectProgramSelectBox());
    List<WebElement> selectFieldValues = distributionPage.getAllSelectOptionsFromProgram();
    int valuesInSelectFieldCounter = 0;
    int valuesToBeVerifiedCounter = 0;
    int finalCounter = 0;
    int counterOfFirstSelectValueToBeSkipped = 0;
    for (String values : valuesToBeVerified)
      valuesToBeVerifiedCounter++;
    for (WebElement webElement : selectFieldValues)
      valuesInSelectFieldCounter++;

    if (valuesToBeVerifiedCounter == valuesInSelectFieldCounter - 1) {
      for (WebElement webElement : selectFieldValues) {
        counterOfFirstSelectValueToBeSkipped++;
        if (counterOfFirstSelectValueToBeSkipped != 1) {
          for (String values : valuesToBeVerified) {
            if (values.equalsIgnoreCase(webElement.getText().trim())) {
              finalCounter++;
            }
          }

        }
      }
    } else {
      fail("Values in select field are not same in number as values to be verified");
    }
    assertEquals(valuesToBeVerifiedCounter, finalCounter);
    assertEquals(valuesInSelectFieldCounter - 1, finalCounter);
  }

  private void verifyPeriodSelectFieldValuesPresent(DistributionPage distributionPage, List<String> valuesToBeVerified) {
    testWebDriver.waitForElementToAppear(distributionPage.getSelectPeriodSelectBox());
    List<WebElement> selectFieldValues = distributionPage.getAllSelectOptionsFromPeriod();
    int valuesInSelectFieldCounter = 0;
    int valuesToBeVerifiedCounter = 0;
    int finalCounter = 0;
    int counterOfFirstSelectValueToBeSkipped = 0;
    for (String values : valuesToBeVerified)
      valuesToBeVerifiedCounter++;
    for (WebElement webElement : selectFieldValues)
      valuesInSelectFieldCounter++;

    if (valuesToBeVerifiedCounter == valuesInSelectFieldCounter - 1) {
      for (WebElement webElement : selectFieldValues) {
        counterOfFirstSelectValueToBeSkipped++;
        if (counterOfFirstSelectValueToBeSkipped != 1) {
          for (String values : valuesToBeVerified) {
            if (values.equalsIgnoreCase(webElement.getText().trim())) {
              finalCounter++;
            }
          }

        }
      }
    } else {
      fail("Values in select field are not same in number as values to be verified");
    }
    assertEquals(valuesToBeVerifiedCounter, finalCounter);
    assertEquals(valuesInSelectFieldCounter - 1, finalCounter);
  }

  private void verifyPeriodSelectFieldValuesNotPresent(DistributionPage distributionPage, String valueToBeVerified) {
    List<WebElement> selectFieldValues = distributionPage.getAllSelectOptionsFromPeriod();
    boolean flag = false;


    for (WebElement webElement : selectFieldValues) {
      if (valueToBeVerified.equalsIgnoreCase(webElement.getText().trim())) {
        flag = true;
        break;
      }

    }
    assertTrue(valueToBeVerified + " should not exist in period drop down", flag == false);
  }


  private void verifySelectedOptionFromDeliveryZoneSelectField(DistributionPage distributionPage, String valuesToBeVerified) {
    WebElement selectFieldValue = distributionPage.getFirstSelectedOptionFromDeliveryZone();
    assertEquals(valuesToBeVerified, selectFieldValue.getText());
  }

  private void verifySelectedOptionFromProgramSelectField(DistributionPage distributionPage, String valuesToBeVerified) {
    WebElement selectFieldValue = distributionPage.getFirstSelectedOptionFromProgram();
    assertEquals(valuesToBeVerified, selectFieldValue.getText());
  }

  private void verifySelectedOptionFromPeriodSelectField(DistributionPage distributionPage, String valuesToBeVerified) {
    WebElement selectFieldValue = distributionPage.getFirstSelectedOptionFromPeriod();
    assertEquals(valuesToBeVerified, selectFieldValue.getText());
  }


  private void verifySubOptionsOfProceedButton(DistributionPage distributionPage) {
    assertTrue("getViewWarehouseLoadAmount Link should be present", distributionPage.getViewWarehouseLoadAmountLink().isDisplayed());
    assertTrue("getInputFacilityData Link should be present", distributionPage.getInputFacilityDataLink().isDisplayed());
  }


  @AfterMethod(groups = {"functional2"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"storeincharge", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
        "F10", "F11", "VACCINES", "TB", "M", "Period", 14}
    };

  }
}

