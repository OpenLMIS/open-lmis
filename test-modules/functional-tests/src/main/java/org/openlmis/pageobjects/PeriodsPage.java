package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PeriodsPage extends Page {

  @FindBy(how = How.ID, using = "name")
  private static WebElement namePeriod;

  @FindBy(how = How.ID, using = "description")
  private static WebElement descriptionPeriod;

  @FindBy(how = How.ID, using = "startDate")
  private static WebElement startDatePeriod;

  @FindBy(how = How.ID, using = "endDate")
  private static WebElement endDatePeriod;

  @FindBy(how = How.XPATH, using = "//span[contains(text(),'Prev')]")
  private static WebElement prevCalender;

  @FindBy(how = How.XPATH, using = "//span[contains(text(),'Next')]")
  private static WebElement nextCalender;

  @FindBy(how = How.XPATH, using = "//a[@class='ui-state-default' and contains(text(),'1')]")
  private static WebElement startDateCalender;

  @FindBy(how = How.XPATH, using = "//a[@class='ui-state-default' and contains(text(),'2')]")
  private static WebElement endDateCalender;

  @FindBy(how = How.XPATH, using = "//a[@class='ui-state-default' and contains(text(),'26')]")
  private static WebElement endDateSecondCalender;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'28')]")
  private static WebElement endDateThirdCalender;

  @FindBy(how = How.XPATH, using = "//span[@ng-show='calculateDays(newPeriod.startDate, newPeriod.endDate)+1']")
  private static WebElement totalDaysPeriods;

  @FindBy(how = How.XPATH, using = "//span[@ng-model='newPeriod.numberOfMonths']")
  private static WebElement numberOfMonthsPeriods;

  @FindBy(how = How.XPATH, using = "//input[@value='Add']")
  private static WebElement addButton;


  @FindBy(how = How.ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMsgDiv;

  @FindBy(how = How.XPATH, using = "//input[@value='Delete']")
  private static WebElement deleteButton;

  @FindBy(how = How.XPATH, using = "(//div[@class='row-fluid schedule-row ng-scope']/div[@class='span2 ng-binding'])[2]")
  private static WebElement startDateList;

  @FindBy(how = How.XPATH, using = "(//div[@class='row-fluid schedule-row ng-scope']/div[@class='span2 ng-binding'])[3]")
  private static WebElement endDateList;


  public PeriodsPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(25);

  }


  public void createAndVerifyPeriods() throws IOException {
    testWebDriver.waitForElementToAppear(namePeriod);
    testWebDriver.sleep(1000);
    enterAndVerifyPeriodDetails("Period1", "first period", "1", 1);
    testWebDriver.sleep(1000);
    enterAndVerifyPeriodDetails("Period2", "second period", "1", 2);
    testWebDriver.sleep(1000);
    enterAndVerifyPeriodDetails("Period3", "third period", "1", 3);
  }

  public void deleteAndVerifyPeriods() throws IOException {
    testWebDriver.waitForElementToAppear(startDateList);
    String actualStartDateListValue = startDateList.getText().trim();

    int flag = compareDateWithToday(actualStartDateListValue);

    if (flag == 1) {
      testWebDriver.sleep(1000);
      SeleneseTestNgHelper.assertTrue("delete button is not getting displayed", deleteButton.isDisplayed());
      deleteButton.click();
      testWebDriver.sleep(1500);
      testWebDriver.waitForElementToAppear(endDateList);
    }
    String actualEndDateList = endDateList.getText().trim();
    String actualStartDateCalender = testWebDriver.getAttribute(startDatePeriod, "value");
    int diffInDays = compareTwoDates(actualStartDateCalender, actualEndDateList);
    SeleneseTestNgHelper.assertEquals(String.valueOf(diffInDays), "1");

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
    SeleneseTestNgHelper.seleniumEquals(actualNumberOfMonthsPeriods, totalMonths);

    addButton.click();
    testWebDriver.sleep(250);
    testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
    SeleneseTestNgHelper.assertTrue("Message Period added successfully is not getting displayed", saveSuccessMsgDiv.isDisplayed());

    testWebDriver.waitForElementToAppear(startDateList);
    String actualStartDateListValue = startDateList.getText().trim();

    int flag = compareDateWithToday(actualStartDateListValue);

    if (flag == 1)
      SeleneseTestNgHelper.assertTrue("delete button is not getting displayed", deleteButton.isDisplayed());
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
    } finally {
      return diffInDays;
    }
  }

  public int compareDateWithToday(String dateToCompare) {

    Date todayDate = null;
    java.text.SimpleDateFormat sdf;
    Date datePeriods = null;
    int flag = 0;
    try {

      todayDate = new Date();
      sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
      String todayDateString = sdf.format(todayDate);
      todayDate = sdf.parse(todayDateString);

      datePeriods = sdf.parse(dateToCompare);

      if (datePeriods.compareTo(todayDate) > 0) {
        System.out.println("DatePeriods is after TodayDate");
        flag = 1;
      } else if (datePeriods.compareTo(todayDate) < 0) {
        System.out.println("DatePeriods is before TodayDate");
        flag = 2;

      } else if (datePeriods.compareTo(todayDate) == 0) {
        System.out.println("DatePeriods is equal to TodayDate");

      }

    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      return flag;
    }
  }
}