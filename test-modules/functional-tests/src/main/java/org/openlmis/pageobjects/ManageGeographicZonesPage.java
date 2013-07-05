package org.openlmis.pageobjects;

import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Henok
 * Date: 7/3/13
 * Time: 6:34 AM
 */

public class ManageGeographicZonesPage extends Page {

    @FindBy(how=How.XPATH,using="//input[@value='Save']")
    private static WebElement saveButton;

    @FindBy(how=How.ID, using="code")
    private static WebElement geographicZoneCodeField;

    @FindBy(how=How.ID, using="name")
    private static WebElement geographicZoneNameField;

    @FindBy(how=How.XPATH, using="//select[@ng-model='geographizZone.level.id']")
    private static WebElement geographicZoneLevelField;

    @FindBy(how=How.XPATH, using="//select[@ng-model='geographicZone.parent.id']")
    private static WebElement geographicZoneParentField;


    @FindBy(how= How.ID, using="geographic-zone-add-new")
    private static WebElement addGeographicZoneButton;

    @FindBy(how=How.XPATH, using = "//div[@id='saveSuccessMessageDiv']/span")
    private static WebElement saveSuccessMessage;

    @FindBy(how=How.LINK_TEXT, using ="View Here")
    private static WebElement viewHereLink;


    public ManageGeographicZonesPage(TestWebDriver driver) throws IOException {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(),10),this);
        testWebDriver.setImplicitWait(10);
    }

    public void EnterAndVerifyNewGeographicZone(String code, String name, String levelID, String parentID){
        testWebDriver.waitForElementToAppear(addGeographicZoneButton);
        addGeographicZoneButton.click();
        testWebDriver.waitForElementToAppear(geographicZoneCodeField);

        geographicZoneCodeField.clear();
        geographicZoneCodeField.sendKeys(code);
        geographicZoneNameField.clear();
        geographicZoneNameField.sendKeys(name);
        testWebDriver.selectByValue(geographicZoneLevelField, levelID);
        testWebDriver.selectByValue(geographicZoneParentField, parentID);

        testWebDriver.waitForElementToAppear(saveButton);
        saveButton.click();

        testWebDriver.sleep(1500);
        testWebDriver.waitForElementToAppear(viewHereLink);

        SeleneseTestNgHelper.assertTrue("Geographic zone '" + name + "' has been successfully created message is not showing up.",saveSuccessMessage.isDisplayed());
        viewHereLink.click();
    }



}
