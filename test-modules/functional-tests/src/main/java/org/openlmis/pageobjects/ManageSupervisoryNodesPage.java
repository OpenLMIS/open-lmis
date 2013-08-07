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
 */

public class ManageSupervisoryNodesPage extends Page {

    @FindBy(how=How.XPATH,using="//input[@value='Save']")
    private static WebElement saveButton;

    @FindBy(how=How.ID, using="code")
    private static WebElement supervisoryNodeCodeField;

    @FindBy(how=How.ID, using="name")
    private static WebElement supervisoryNodeNameField;

    @FindBy(how=How.XPATH, using="//select[@ng-model='supervisoryNode.parent.id']")
    private static WebElement supervisoryNodeParentField;

    @FindBy(how=How.ID, using="description")
    private static WebElement supervisoryNodeDescriptionField;


    @FindBy(how= How.ID, using="supervisory-node-add-new")
    private static WebElement addSupervisoryNodeButton;

    @FindBy(how=How.XPATH, using = "//div[@id='saveSuccessMessageDiv']/span")
    private static WebElement saveSuccessMessage;

    @FindBy(how=How.XPATH, using = "//div[@id='saveErrorMsgDiv']")
    private static WebElement saveErrorMessage;


    @FindBy(how=How.ID, using = "supervisory-node-facility-add-new")
    private static WebElement chooseFacilityButton;

    @FindBy(how=How.ID, using = "facilityValue")
    private static WebElement associatedFacilityField;

    @FindBy(how = How.ID, using = "//input[@value='Close']")
    private static WebElement closeFacilityDialogButton;


    public ManageSupervisoryNodesPage(TestWebDriver driver) throws IOException {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(),10),this);
        testWebDriver.setImplicitWait(10);
    }

    public void EnterAndVerifyNewSupervisoryNode(String code, String name, String supervisoryNodeParentID, String description,String facilityId){
        testWebDriver.waitForElementToAppear(addSupervisoryNodeButton);
        addSupervisoryNodeButton.click();
        testWebDriver.waitForElementToAppear(supervisoryNodeCodeField);

        supervisoryNodeCodeField.clear();
        supervisoryNodeCodeField.sendKeys(code);
        supervisoryNodeNameField.clear();
        supervisoryNodeNameField.sendKeys(name);
        testWebDriver.selectByValue(supervisoryNodeParentField, supervisoryNodeParentID);
        supervisoryNodeDescriptionField.clear();
        supervisoryNodeDescriptionField.sendKeys(description);

        testWebDriver.waitForElementToAppear(chooseFacilityButton);
        chooseFacilityButton.click();
        testWebDriver.waitForElementToAppear(associatedFacilityField);
        testWebDriver.selectByValue(associatedFacilityField, facilityId);

        testWebDriver.waitForElementToAppear(closeFacilityDialogButton);
        closeFacilityDialogButton.click();

        testWebDriver.waitForElementToAppear(saveButton);
        saveButton.click();

        testWebDriver.sleep(1500);

        SeleneseTestNgHelper.assertTrue("Supervisory node '" + name + "' has been successfully created message is not showing up.",saveSuccessMessage.isDisplayed());
    }

    public void EnterAndVerifyNewSupervisoryNodeWOFacility(String code, String name, String supervisoryNodeParentID, String description){
        testWebDriver.waitForElementToAppear(addSupervisoryNodeButton);
        addSupervisoryNodeButton.click();
        testWebDriver.waitForElementToAppear(supervisoryNodeCodeField);

        supervisoryNodeCodeField.clear();
        supervisoryNodeCodeField.sendKeys(code);
        supervisoryNodeNameField.clear();
        supervisoryNodeNameField.sendKeys(name);
        testWebDriver.selectByValue(supervisoryNodeParentField, supervisoryNodeParentID);
        supervisoryNodeDescriptionField.clear();
        supervisoryNodeDescriptionField.sendKeys(description);

        testWebDriver.waitForElementToAppear(saveButton);
        saveButton.click();

        testWebDriver.sleep(1500);

        SeleneseTestNgHelper.assertTrue("Error message is not showing up for empty facility.",saveErrorMessage.isDisplayed());
    }
}