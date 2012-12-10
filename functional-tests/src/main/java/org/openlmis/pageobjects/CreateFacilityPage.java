package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CreateFacilityPage extends Page {


    @FindBy(how = How.XPATH, using = "//a[contains(text(),'Manage Facilities')]")
    private static WebElement manageFacilityMenuItem;

    @FindBy(how = How.LINK_TEXT, using = "Create")
    private static WebElement createFacility;

    @FindBy(how = How.ID, using = "code")
    private static WebElement facilityCode;

    @FindBy(how = How.ID, using = "name")
    private static WebElement facilityName;

    @FindBy(how = How.ID, using = "description")
    private static WebElement facilityDescription;

    @FindBy(how = How.ID, using = "gln")
    private static WebElement gln;

    @FindBy(how = How.ID, using = "main-phone")
    private static WebElement phoneNumber;

    @FindBy(how = How.ID, using = "fax-phone")
    private static WebElement faxNumber;

    @FindBy(how = How.ID, using = "address-1")
    private static WebElement address1;

    @FindBy(how = How.ID, using = "address-2")
    private static WebElement address2;

    @FindBy(how = How.ID, using = "geographic-zone")
    private static WebElement geographicZone;

    @FindBy(how = How.ID, using = "facility-type")
    private static WebElement facilityType;

    @FindBy(how = How.ID, using = "catchment-population")
    private static WebElement catchmentPopulation;

    @FindBy(how = How.ID, using = "latitude")
    private static WebElement latitude;

    @FindBy(how = How.ID, using = "longitude")
    private static WebElement longitude;

    @FindBy(how = How.ID, using = "altitude")
    private static WebElement altitude;

    @FindBy(how = How.ID, using = "operated-by")
    private static WebElement operatedBy;

    @FindBy(how = How.ID, using = "cold-storage-gross-capacity")
    private static WebElement coldStorageGrossCapacity;

    @FindBy(how = How.ID, using = "cold-storage-net-capacity")
    private static WebElement coldStorageNetCapacity;

    @FindBy(how = How.XPATH, using = "//input[@name='supplies-others' and @value='true']")
    private static WebElement facilitySuppliesOthers;

    @FindBy(how = How.XPATH, using = "//input[@name='isSdp' and @value='true']")
    private static WebElement serviceDeliveryPoint;

    @FindBy(how = How.XPATH, using = "//input[@name='has-electricity' and @value='true']")
    private static WebElement hasElectricity;

    @FindBy(how = How.XPATH, using = "//input[@name='is-online' and @value='true']")
    private static WebElement isOnline;

    @FindBy(how = How.XPATH, using = "//input[@name='has-electronic-scc' and @value='true']")
    private static WebElement hasElectronicScc;

    @FindBy(how = How.XPATH, using = "//input[@name='has-electronic-dar' and @value='true']")
    private static WebElement hasElectronicDar;

    @FindBy(how = How.XPATH, using = "//input[@name='isActive' and @value='true']")
    private static WebElement isActive;

    @FindBy(how = How.ID, using = "go-live-date")
    private static WebElement goLiveDate;

    @FindBy(how = How.ID, using = "go-down-date")
    private static WebElement goDownDate;

    @FindBy(how = How.XPATH, using = "//input[@name='data-reportable' and @value='true']")
    private static WebElement dataReportable;

    @FindBy(how = How.ID, using = "comments")
    private static WebElement comments;

    @FindBy(how = How.ID, using = "programs-supported")
    private static WebElement programsSupported;

    @FindBy(how = How.XPATH, using = "//input[@value='Save']")
    private static WebElement SaveButton;

    @FindBy(how = How.ID, using = "saveSuccessMsgDiv")
    private static WebElement saveSuccessMsgDiv;

    @FindBy(how = How.ID, using = "saveErrorMsgDiv")
    private static WebElement saveErrorMsgDiv;

    @FindBy(how = How.XPATH, using = "//a[contains(text(),'25')]")
    private static WebElement goLiveDateCalender;

    @FindBy(how = How.XPATH, using = "//a[contains(text(),'26')]")
    private static WebElement goDownDateCalender;


    //private String BASE_URL = "http://qa.221.134.198.28.xip.io/";
    private String BASE_URL = "http://localhost:9091/";



    public CreateFacilityPage(TestWebDriver driver) {
        super(driver);
        testWebDriver.setBaseURL(BASE_URL);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);
    }


    public void navigateCreateFacility() {
        //dropAllFacilities();
        testWebDriver.waitForElementToAppear(manageFacilityMenuItem);
        testWebDriver.click(manageFacilityMenuItem);
        manageFacilityMenuItem.click();
        testWebDriver.waitForElementToAppear(createFacility);
        createFacility.click();
        testWebDriver.waitForTextToAppear("Add new facility");

    }

    public void enterAndVerifyFacility() {
        Date dObj=new Date();
        SimpleDateFormat formatter_date_time = new SimpleDateFormat(
                "yyyyMMdd-hhmmss");
        String date_time = formatter_date_time.format(dObj);

        String facilityCodeText="FCcode"+date_time;
        String facilityNameText="FCname"+date_time;

        testWebDriver.waitForTextToAppear("Add new facility");
        facilityCode.clear();
        facilityCode.sendKeys(facilityCodeText);
        facilityName.sendKeys(facilityNameText);
        facilityDescription.sendKeys("Testing description");
        gln.sendKeys("Testing Gln");
        phoneNumber.sendKeys("9711231305");
        faxNumber.sendKeys("9711231305");
        address1.sendKeys("Address1");
        address2.sendKeys("Address2");

        testWebDriver.selectByIndex(geographicZone,1);
        testWebDriver.selectByIndex(facilityType,1);

        catchmentPopulation.sendKeys("500000");
        latitude.sendKeys("5555.5555");
        longitude.sendKeys("4444.4444");
        altitude.sendKeys("4545.4545");

        testWebDriver.selectByIndex(operatedBy,1);
        coldStorageGrossCapacity.sendKeys("3434.3434");
        coldStorageNetCapacity.sendKeys("3535.3535");

        facilitySuppliesOthers.click();
        serviceDeliveryPoint.click();
        hasElectricity.click();
        isOnline.click();
        hasElectronicScc.click();
        hasElectronicDar.click();
        isActive.click();

        goLiveDate.click();
        testWebDriver.sleep(500);
        goLiveDateCalender.click();
        testWebDriver.sleep(500);
        goDownDate.click();
        testWebDriver.sleep(500);
        goDownDateCalender.click();

        dataReportable.click();
        comments.sendKeys("Comments");


        testWebDriver.selectByIndex(programsSupported,0);
        testWebDriver.selectByIndex(programsSupported,1);

        SaveButton.click();

        testWebDriver.waitForTextToAppear("created successfully");
        String successMessage= testWebDriver.getText(saveSuccessMsgDiv);
        SeleneseTestNgHelper.assertEquals(successMessage,facilityNameText + " created successfully");
        testWebDriver.sleep(2000);
    }


}