package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
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

    @FindBy(how = How.XPATH, using = "//a[contains(text(),'21')]")
    private static WebElement startDateCalender;

    @FindBy(how = How.XPATH, using = "//a[contains(text(),'22')]")
    private static WebElement endDateCalender;

    @FindBy(how = How.XPATH, using = "//a[contains(text(),'25')]")
    private static WebElement endDateSecondCalender;

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


    public PeriodsPage(TestWebDriver driver) throws IOException {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);

    }


    public void createAndVerifyPeriods() throws IOException {
        testWebDriver.waitForElementToAppear(namePeriod);
        enterAndVerifyPeriodDetails("Period1", "first period", "1", "1", 1);
        enterAndVerifyPeriodDetails("Period2", "second period", "2", "1", 0);
    }


    public void enterAndVerifyPeriodDetails(String name, String desc, String totalDays, String totalMonths, int indicator) {
        namePeriod.clear();
        namePeriod.sendKeys(name);
        descriptionPeriod.clear();
        descriptionPeriod.sendKeys(desc);
        if (indicator == 1) {
            startDatePeriod.click();
            testWebDriver.sleep(250);
            startDateCalender.click();
            testWebDriver.sleep(250);
            endDatePeriod.click();
            testWebDriver.sleep(250);
            endDateCalender.click();
        } else {
            testWebDriver.sleep(250);
            endDatePeriod.click();
            testWebDriver.sleep(250);
            endDateSecondCalender.click();
            testWebDriver.sleep(250);
        }

        String actualtotalDaysPeriods = totalDaysPeriods.getText().trim();
        SeleneseTestNgHelper.assertEquals(actualtotalDaysPeriods, totalDays);

        String actualNumberOfMonthsPeriods = numberOfMonthsPeriods.getText().trim();
        SeleneseTestNgHelper.seleniumEquals(actualNumberOfMonthsPeriods, totalMonths);

        addButton.click();

        testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
        SeleneseTestNgHelper.assertTrue("Message Period added successfully is not getting displayed", saveSuccessMsgDiv.isDisplayed());

        testWebDriver.waitForElementToAppear(startDateList);
        String actualStartDateListValue = startDateList.getText().trim();

        int flag = compareDateWithToday(actualStartDateListValue);

        if (flag == 1)
            SeleneseTestNgHelper.assertTrue("delete button is not getting displayed", deleteButton.isDisplayed());
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