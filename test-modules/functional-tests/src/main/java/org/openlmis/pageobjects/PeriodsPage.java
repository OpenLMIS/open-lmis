/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.openqa.selenium.support.How.ID;


public class PeriodsPage extends Page {

  @FindBy(how = ID, using = "name")
  private static WebElement namePeriod = null;

  @FindBy(how = ID, using = "description")
  private static WebElement descriptionPeriod = null;

  @FindBy(how = ID, using = "startDate")
  private static WebElement startDatePeriod = null;

  @FindBy(how = ID, using = "endDate")
  private static WebElement endDatePeriod = null;

  @FindBy(how = How.XPATH, using = "//span[contains(text(),'Prev')]")
  private static WebElement prevCalender = null;

  @FindBy(how = How.XPATH, using = "//span[contains(text(),'Next')]")
  private static WebElement nextCalender = null;

  @FindBy(how = How.XPATH, using = "//a[@class='ui-state-default' and contains(text(),'1')]")
  private static WebElement startDateCalender = null;

  @FindBy(how = How.XPATH, using = "//a[@class='ui-state-default' and contains(text(),'2')]")
  private static WebElement endDateCalender = null;

  @FindBy(how = How.XPATH, using = "//a[@class='ui-state-default' and contains(text(),'26')]")
  private static WebElement endDateSecondCalender = null;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'28')]")
  private static WebElement endDateThirdCalender = null;

  @FindBy(how = ID, using = "numberOfMonths")
  private static WebElement numberOfMonthsPeriods = null;

  @FindBy(how = ID, using = "periodAdd")
  private static WebElement addButton = null;

  @FindBy(how = How.XPATH, using = "//input[@value='Delete']")
  private static WebElement deleteButton = null;

  @FindBy(how = How.XPATH, using = ".//table[@id='periodTable']/tbody/tr/td[3]")
  private static WebElement startDateList = null;

  @FindBy(how = How.XPATH, using = ".//table[@id='periodTable']/tbody/tr/td[4]")
  private static WebElement endDateList = null;


  public PeriodsPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public void createAndVerifyPeriods() {
    testWebDriver.waitForElementToAppear(namePeriod);
    testWebDriver.sleep(1000);
    enterAndVerifyPeriodDetails("Period1", "first period", "2", 1);
    testWebDriver.sleep(1000);
    enterAndVerifyPeriodDetails("Period2", "second period", "2", 2);
    testWebDriver.sleep(1000);
    enterAndVerifyPeriodDetails("Period3", "third period", "1", 3);
  }

  public void deleteAndVerifyPeriods() {
    testWebDriver.waitForElementToAppear(startDateList);
    String actualStartDateListValue = startDateList.getText().trim();

    int flag = compareDateWithToday(actualStartDateListValue);

    if (flag == 1) {
      testWebDriver.sleep(1000);
      assertTrue("delete button is not getting displayed", deleteButton.isDisplayed());
      deleteButton.click();
      testWebDriver.sleep(1500);
      testWebDriver.waitForElementToAppear(endDateList);
    }
    String actualEndDateList = endDateList.getText().trim();
    String actualStartDateCalender = testWebDriver.getAttribute(startDatePeriod, "value");
    int diffInDays = compareTwoDates(actualStartDateCalender, actualEndDateList);
    assertEquals(String.valueOf(diffInDays), "1");
  }

  public void enterAndVerifyPeriodDetails(String name, String desc, String totalMonths, int indicator) {
    namePeriod.clear();
    namePeriod.sendKeys(name);
    descriptionPeriod.clear();
    descriptionPeriod.sendKeys(desc);
    if (indicator == 1) {
      startDatePeriod.click();
      testWebDriver.sleep(100);
      prevCalender.click();
      testWebDriver.sleep(100);
      startDateCalender.click();
      testWebDriver.sleep(100);
      endDatePeriod.click();
      testWebDriver.sleep(100);
      nextCalender.click();
      testWebDriver.sleep(100);
      endDateCalender.click();
    } else if (indicator == 2) {
      testWebDriver.sleep(100);
      endDatePeriod.click();
      testWebDriver.sleep(100);
      nextCalender.click();
      testWebDriver.sleep(100);
      endDateSecondCalender.click();
      testWebDriver.sleep(100);
    } else {
      testWebDriver.sleep(100);
      endDatePeriod.click();
      testWebDriver.sleep(100);
      nextCalender.click();
      testWebDriver.sleep(100);
      endDateThirdCalender.click();
      testWebDriver.sleep(100);
    }

    String actualNumberOfMonthsPeriods = numberOfMonthsPeriods.getText().trim();
    assertEquals(actualNumberOfMonthsPeriods, totalMonths);

    addButton.click();
    testWebDriver.sleep(500);

    testWebDriver.waitForElementToAppear(startDateList);
    String actualStartDateListValue = startDateList.getText().trim();

    int flag = compareDateWithToday(actualStartDateListValue);

    if (flag == 1)
      assertTrue("delete button is not getting displayed", deleteButton.isDisplayed());
  }

  public int compareTwoDates(String newerDateString, String olderDateString) {
    int diffInDays = 0;
    try {

      java.text.SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
      java.util.Date newerDate = sdf.parse(newerDateString);

      java.util.Date olderDate = sdf.parse(olderDateString);

      diffInDays = (int) ((newerDate.getTime() - olderDate.getTime())
        / (1000 * 60 * 60 * 24));

    } catch (java.text.ParseException e) {
      e.printStackTrace();
    }
    return diffInDays;
  }

  public int compareDateWithToday(String dateToCompare) {

    Date todayDate;
    java.text.SimpleDateFormat sdf;
    Date datePeriods;
    int flag = 0;
    try {

      todayDate = new Date();
      sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
      String todayDateString = sdf.format(todayDate);
      todayDate = sdf.parse(todayDateString);

      datePeriods = sdf.parse(dateToCompare);

      if (datePeriods.compareTo(todayDate) > 0) {
        flag = 1;
      } else if (datePeriods.compareTo(todayDate) < 0) {
        flag = 2;

      } else if (datePeriods.compareTo(todayDate) == 0) {
        flag = 3;
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return flag;
  }
}