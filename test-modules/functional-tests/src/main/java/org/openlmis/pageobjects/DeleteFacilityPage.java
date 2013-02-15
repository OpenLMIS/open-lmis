package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;


public class DeleteFacilityPage extends Page {



    @FindBy(how = How.ID, using = "searchFacility")
    private static WebElement searchFacilityTextField;

    @FindBy(how = How.XPATH, using = "//div[@class='facility-list']/ul/li/a")
    private static WebElement facilityList;

    @FindBy(how = How.LINK_TEXT, using = "Delete")
    private static WebElement deleteButton;

    @FindBy(how = How.XPATH, using = "//div[@id='deleteModal']/div[@class='modal-body']/p")
    private static WebElement deleteMessageOnAlert;

    @FindBy(how = How.XPATH, using = "//a[@ng-click='deleteFacility()']")
    private static WebElement deteteButtonOnAlert;

    @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv']/span")
    private static WebElement messageDiv;

    @FindBy(how = How.ID, using = "saveSuccessMsgDiv")
    private static WebElement successMessageDiv;

    @FindBy(how = How.ID, using = "saveErrorMsgDiv")
    private static WebElement saveErrorMsgDiv;


    @FindBy(how = How.XPATH, using = "//ng-switch/span")
    private static WebElement dataReportable;

    @FindBy(how = How.XPATH, using = "//input[@name='isActive' and @value='false']")
    private static WebElement isActiveRadioNoOption;

    @FindBy(how = How.XPATH, using = "//input[@name='isActive' and @value='true']")
    private static WebElement isActiveRadioYesOption;

    @FindBy(how = How.LINK_TEXT, using = "Restore")
    private static WebElement restoreButton;

    @FindBy(how = How.XPATH, using = "//div[@id='restoreConfirmModal']/div[@class='modal-body']/p")
    private static WebElement restoreMessageOnAlert;

    @FindBy(how = How.LINK_TEXT, using = "OK")
    private static WebElement okLink;

    @FindBy(how = How.XPATH, using = " //div[@id='activeConfirmModel']/div[@class='modal-body']/p")
    private static WebElement isActiveMessageOnAlert;

    @FindBy(how = How.LINK_TEXT, using = "Yes")
    private static WebElement yesLink;

    @FindBy(how = How.ID, using = "catchment-population")
    private static WebElement catchmentPopulation;

    @FindBy(how = How.ID, using = "latitude")
    private static WebElement latitude;

    @FindBy(how = How.ID, using = "longitude")
    private static WebElement longitude;

    @FindBy(how = How.ID, using = "altitude")
    private static WebElement altitude;


    @FindBy(how = How.ID, using = "code")
    private static WebElement facilityCode;

    @FindBy(how = How.XPATH, using = "//input[@value='Save']")
    private static WebElement SaveButton;

    @FindBy(how = How.XPATH, using = "//div[@class='ng-scope']/div[@ng-show='facility.id']/h2")
    private static WebElement facilityHeader;

    @FindBy(how = How.ID, using = "programs-supported")
    private static WebElement programsSupported;

    @FindBy(how = How.ID, using = "supported-program-active")
    private static WebElement programsSupportedActiveFlag;

    @FindBy(how = How.ID, using = "supported-program-start-date")
    private static WebElement programsSupportedStartDate;

    @FindBy(how = How.XPATH, using = "//a[contains(text(),'25')]")
    private static WebElement startDateCalender;

    @FindBy(how = How.XPATH, using = "//a[@ng-click='setNewStartDate()']")
    private static WebElement startDateAlert;

    @FindBy(how = How.ID, using = "supported-program-add")
    private static WebElement addSupportedProgram;

    @FindBy(how = How.ID, using = "remove0")
    private static WebElement removeSupportedProgram;



    public DeleteFacilityPage(TestWebDriver driver) throws  IOException {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);

    }



    public void searchFacility(String facilityCodeValue) {

        testWebDriver.waitForElementToAppear(searchFacilityTextField);
        searchFacilityTextField.sendKeys(facilityCodeValue);
           testWebDriver.sleep(2000);
    }

    public void deleteAndVerifyFacility(String facilityCodeValue, String facilityNameValue) {

        String expectedMessageOnAlert="\""+facilityNameValue+" / \""+facilityCodeValue+"\" will be soft-deleted from the system";
        String expectedMessageOnFacilityScreenAfterDelete="\""+facilityNameValue+"\" / \""+facilityCodeValue+"\" deleted successfully";
        testWebDriver.waitForElementToAppear(facilityList);
        facilityList.click();

        testWebDriver.waitForElementToAppear(facilityHeader);
        SeleneseTestNgHelper.assertEquals(facilityHeader.getText().trim(), "Edit facility");

        testWebDriver.waitForElementToAppear(deleteButton);
        deleteButton.click();
        testWebDriver.waitForElementToAppear(deleteMessageOnAlert);

        String deleteMessageOnAlertValue= deleteMessageOnAlert.getText();
        SeleneseTestNgHelper.assertEquals(deleteMessageOnAlertValue,expectedMessageOnAlert);

        testWebDriver.sleep(1000);
        deteteButtonOnAlert.click();
        testWebDriver.waitForElementToAppear(successMessageDiv);

        testWebDriver.sleep(1000);
        String deleteMessageOnFacilityScreenValue=successMessageDiv.getText();
        SeleneseTestNgHelper.assertEquals(deleteMessageOnFacilityScreenValue,expectedMessageOnFacilityScreenAfterDelete);

        String dataReportableValue=dataReportable.getText();
        SeleneseTestNgHelper.assertEquals(dataReportableValue.trim(), "No");

        SeleneseTestNgHelper.assertTrue(isActiveRadioNoOption.isSelected());

    }

    public HomePage restoreAndVerifyFacility(String facilityCodeValue, String facilityNameValue) throws IOException {
        String expectedIsActiveMessageOnAlert= "Do you want to set facility as active?";

        testWebDriver.waitForElementToAppear(restoreButton);
        testWebDriver.sleep(1000);
        restoreButton.click();
        testWebDriver.waitForElementToAppear(restoreMessageOnAlert);


        testWebDriver.sleep(1000);
        okLink.click();
        testWebDriver.sleep(1000);
        testWebDriver.waitForElementToAppear(isActiveMessageOnAlert);
        String isActiveMessageOnAlertValue=isActiveMessageOnAlert.getText();
        SeleneseTestNgHelper.assertEquals(isActiveMessageOnAlertValue,expectedIsActiveMessageOnAlert);
        testWebDriver.waitForElementToAppear(yesLink);
        testWebDriver.sleep(1000);
        yesLink.click();

        String dataReportableValue=dataReportable.getText();
        SeleneseTestNgHelper.assertEquals(dataReportableValue.trim(), "Yes");
        SeleneseTestNgHelper.assertTrue(isActiveRadioYesOption.isSelected());

        testWebDriver.waitForElementToAppear(facilityHeader);
        SeleneseTestNgHelper.assertEquals(facilityHeader.getText().trim(), "Edit facility");

        return new HomePage(testWebDriver);
    }

    public HomePage editAndVerifyFacility(String facilityNameValue) throws IOException {
        String catchmentPopulationValue="600000";
        String  latitudeValue="6555.5555";
        String longitudeValue="6444.4444";
        String altitudeValue="6545.4545";

        testWebDriver.waitForElementToAppear(facilityList);
        facilityList.click();

        testWebDriver.waitForElementToAppear(facilityHeader);
        SeleneseTestNgHelper.assertEquals(facilityHeader.getText().trim(), "Edit facility");

        testWebDriver.waitForElementToAppear(deleteButton);
        testWebDriver.sleep(1500);
        testWebDriver.waitForElementToAppear(facilityCode);
        catchmentPopulation.clear();
        catchmentPopulation.sendKeys(catchmentPopulationValue);
        latitude.clear();
        latitude.sendKeys(latitudeValue);
        longitude.clear();
        longitude.sendKeys(longitudeValue);
        altitude.clear();
        altitude.sendKeys(altitudeValue);

        testWebDriver.selectByVisibleText(programsSupported, "SMALL POX");
        programsSupportedActiveFlag.click();
        testWebDriver.sleep(500);
        programsSupportedStartDate.click();
        startDateCalender.click();
        testWebDriver.sleep(500);
        startDateAlert.click();
        testWebDriver.sleep(500);
        addSupportedProgram.click();



        SeleneseTestNgHelper.assertEquals(testWebDriver.getAttribute(catchmentPopulation,"value"), catchmentPopulationValue);
        SeleneseTestNgHelper.assertEquals(testWebDriver.getAttribute(latitude,"value"), latitudeValue);
        SeleneseTestNgHelper.assertEquals(testWebDriver.getAttribute(longitude,"value"), longitudeValue);
        SeleneseTestNgHelper.assertEquals(testWebDriver.getAttribute(altitude,"value"), altitudeValue);

        SeleneseTestNgHelper.assertTrue(removeSupportedProgram.isDisplayed());

        SaveButton.click();
        testWebDriver.sleep(1000);
        testWebDriver.waitForElementsToAppear(messageDiv, saveErrorMsgDiv);
        String updateMessage=getMessage();
        SeleneseTestNgHelper.assertEquals(updateMessage, "Facility '"+facilityNameValue+"' updated successfully");

        return new HomePage(testWebDriver);
    }

    public HomePage verifyProgramSupported() throws IOException {


        testWebDriver.waitForElementToAppear(facilityList);
        facilityList.click();

        testWebDriver.waitForElementToAppear(facilityHeader);
        SeleneseTestNgHelper.assertEquals(facilityHeader.getText().trim(), "Edit facility");

        testWebDriver.waitForElementToAppear(deleteButton);
        testWebDriver.sleep(1500);

        SeleneseTestNgHelper.assertTrue(removeSupportedProgram.isDisplayed());

        return new HomePage(testWebDriver);
    }

    public String getMessage()
    {
        String updateMessage;
        if(messageDiv.isDisplayed())
        {
            updateMessage=messageDiv.getText();
        }
        else
        {
            updateMessage=saveErrorMsgDiv.getText();
        }
        return updateMessage;
    }


}
