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


public class DeleteFacilityPage extends Page {



    @FindBy(how = How.ID, using = "searchFacility")
    private static WebElement searchFacilityTextField;

    @FindBy(how = How.XPATH, using = "//div[@class='facility-list']/ul/li[1]/a")
    private static WebElement facilityList;

    @FindBy(how = How.LINK_TEXT, using = "Delete")
    private static WebElement deleteButton;

    @FindBy(how = How.XPATH, using = "//div[@id='deleteModal']/div[@class='modal-body']/p")
    private static WebElement deleteMessageOnAlert;

    @FindBy(how = How.XPATH, using = "//a[@ng-click='deleteFacility()']")
    private static WebElement deteteButtonOnAlert;

    @FindBy(how = How.ID, using = "saveSuccessMsgDiv")
    private static WebElement messageDiv;

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


    public DeleteFacilityPage(TestWebDriver driver) throws  IOException {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);

    }



    public void searchFacility(String facilityCodeValue) {

        testWebDriver.waitForElementToAppear(searchFacilityTextField);
        searchFacilityTextField.sendKeys(facilityCodeValue);
         testWebDriver.waitForElementToAppear(facilityList);
    }

    public void deleteAndVerifyFacility(String facilityCodeValue, String facilityNameValue) {

        String expectedMessageOnAlert="\""+facilityNameValue+" / \""+facilityCodeValue+"\" will be soft-deleted from the system";
        String expectedMessageOnFacilityScreenAfterDelete="\""+facilityNameValue+"\" / \""+facilityCodeValue+"\" deleted successfully";
        testWebDriver.waitForElementToAppear(facilityList);
        facilityList.click();
        testWebDriver.waitForElementToAppear(deleteButton);
        deleteButton.click();
        testWebDriver.waitForElementToAppear(deleteMessageOnAlert);

        String deleteMessageOnAlertValue= deleteMessageOnAlert.getText();
        SeleneseTestNgHelper.assertEquals(deleteMessageOnAlertValue,expectedMessageOnAlert);

        testWebDriver.sleep(1000);
        deteteButtonOnAlert.click();
        testWebDriver.waitForElementToAppear(messageDiv);

        String deleteMessageOnFacilityScreenValue=messageDiv.getText();
        SeleneseTestNgHelper.assertEquals(deleteMessageOnFacilityScreenValue,expectedMessageOnFacilityScreenAfterDelete);

        String dataReportableValue=dataReportable.getText();
        SeleneseTestNgHelper.assertEquals(dataReportableValue.trim(), "No");

        SeleneseTestNgHelper.assertTrue(isActiveRadioNoOption.isSelected());

    }

    public void restoreAndVerifyFacility(String facilityCodeValue, String facilityNameValue) {
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

    }


}
