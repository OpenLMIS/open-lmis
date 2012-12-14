package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;


public class InitiateRnRPage extends Page {

    @FindBy(how = How.XPATH, using = "//div[@id='requisition-header']/h2")
    private static WebElement requisitionHeader;

    @FindBy(how = How.XPATH, using = "//div[@id='requisition-header']/div/table/tbody/tr/td")
    private static WebElement facilityLabel;

    @FindBy(how = How.XPATH, using = "//input[@value='Save']")
    private static WebElement saveButton;

    @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv' and @ng-model='message']")
    private static WebElement successMessage;



    public InitiateRnRPage(TestWebDriver driver) throws  IOException {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);
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

        testWebDriver.sleep(1500);
        String successMessageText=testWebDriver.getText(successMessage);
        testWebDriver.sleep(1500);
        SeleneseTestNgHelper.assertEquals(successMessageText.trim(),successText);

    }
}