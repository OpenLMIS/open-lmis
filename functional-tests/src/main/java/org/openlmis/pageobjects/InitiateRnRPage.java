package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class InitiateRnRPage extends Page {

    @FindBy(how = How.XPATH, using = "//a[contains(text(),'Create')]")
    private static WebElement createRnRLink;

    @FindBy(how = How.XPATH, using = "//select[@ng-change='loadPrograms()']")
    private static WebElement facilityDropDown;

    @FindBy(how = How.XPATH, using = "//option[@value='0']")
    private static WebElement programDropDown;

    @FindBy(how = How.XPATH, using = "//select[2]")
    private static WebElement programDropDownSelect;


    @FindBy(how = How.XPATH, using = "//input[@value='Next']")
    private static WebElement nextButton;

    @FindBy(how = How.XPATH, using = "//div[@id='requisition-header']/h2")
    private static WebElement requisitionHeader;

    @FindBy(how = How.XPATH, using = "//div[@id='requisition-header']/div/table/tbody/tr/td")
    private static WebElement facilityLabel;

    @FindBy(how = How.XPATH, using = "//input[@value='Save']")
    private static WebElement saveButton;

    @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv' and @ng-model='message']")
    private static WebElement successMessage;


    private String BASE_URL, baseUrl;


    public InitiateRnRPage(TestWebDriver driver) throws FileNotFoundException, IOException {
        super(driver);
        Properties props = new Properties();
        props.load(new FileInputStream(System.getProperty("user.dir")+"/src/main/resources/config.properties"));
        baseUrl = props.getProperty("baseUrl");
        BASE_URL=baseUrl;
        testWebDriver.setBaseURL(BASE_URL);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);
    }


    public void navigateAndInitiateRnr(String FCstring, String program) {
        testWebDriver.waitForElementToAppear(createRnRLink);
        createRnRLink.click();
        testWebDriver.waitForElementToAppear(facilityDropDown);
        testWebDriver.selectByVisibleText(facilityDropDown, "FCcode"+FCstring+"-FCname"+FCstring);
        testWebDriver.waitForElementToAppear(programDropDown);
        programDropDown.click();
        testWebDriver.selectByVisibleText(programDropDownSelect,program);
        nextButton.click();
    }

    public void verifyRnRHeader(String FCstring, String program)
    {
        String successText="R&R saved successfully!";
       testWebDriver.waitForElementToAppear(requisitionHeader);
        String headerText=testWebDriver.getText(requisitionHeader);
        SeleneseTestNgHelper.assertTrue(headerText.contains("Report and Requisition for "+program));
        String facilityText=testWebDriver.getText(facilityLabel);
        SeleneseTestNgHelper.assertTrue(facilityText.contains("FCcode" + FCstring + " - FCname" + FCstring));
        saveButton.click();
        String successMessageText=testWebDriver.getText(successMessage);
        testWebDriver.sleep(1500);
        SeleneseTestNgHelper.assertEquals(successMessageText.trim(),successText);
    }




}