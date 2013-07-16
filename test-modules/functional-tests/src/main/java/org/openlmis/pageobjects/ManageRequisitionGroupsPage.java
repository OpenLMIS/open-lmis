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
 * Date: 7/16/13
 * Time: 4:08 AM
 */

public class ManageRequisitionGroupsPage extends Page {

    @FindBy(how=How.XPATH,using="//input[@value='Save']")
    private static WebElement saveButton;

    @FindBy(how=How.ID, using="code")
    private static WebElement requisitionGroupCodeField;

    @FindBy(how=How.ID, using="name")
    private static WebElement requisitionGroupNameField;

    @FindBy(how=How.XPATH, using="//select[@ng-model='requisitionGroup.supervisoryNode.id']")
    private static WebElement requisitionGroupSupervisoryNodeField;

    @FindBy(how=How.ID, using="description")
    private static WebElement requisitionGroupDescriptionField;


    @FindBy(how= How.ID, using="requisition-group-add-new")
    private static WebElement addrequisitionGroupButton;

    @FindBy(how=How.XPATH, using = "//div[@id='saveSuccessMessageDiv']/span")
    private static WebElement saveSuccessMessage;

    @FindBy(how=How.LINK_TEXT, using ="View Here")
    private static WebElement viewHereLink;


    @FindBy(how=How.ID, using = "requisition-group-facility-add-new")
    private static WebElement addAssociatedFacilityButton;

    @FindBy(how=How.ID, using = "facilityValue")
    private static WebElement associatedFacilityField;

    @FindBy(how = How.ID, using = "//input[@value='Associate']")
    private static WebElement associateFacilityButton;




    public ManageRequisitionGroupsPage(TestWebDriver driver) throws IOException {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(),10),this);
        testWebDriver.setImplicitWait(10);
    }

    public void EnterAndVerifyNewrequisitionGroup(String code, String name, String supervisoryNodeID, String description){
        testWebDriver.waitForElementToAppear(addrequisitionGroupButton);
        addrequisitionGroupButton.click();
        testWebDriver.waitForElementToAppear(requisitionGroupCodeField);

        requisitionGroupCodeField.clear();
        requisitionGroupCodeField.sendKeys(code);
        requisitionGroupNameField.clear();
        requisitionGroupNameField.sendKeys(name);
        testWebDriver.selectByValue(requisitionGroupSupervisoryNodeField, supervisoryNodeID);
        requisitionGroupDescriptionField.clear();
        requisitionGroupDescriptionField.sendKeys(description);


        testWebDriver.waitForElementToAppear(saveButton);
        saveButton.click();

        testWebDriver.sleep(1500);
        testWebDriver.waitForElementToAppear(viewHereLink);

        SeleneseTestNgHelper.assertTrue("Requisition group '" + name + "' has been successfully created message is not showing up.",saveSuccessMessage.isDisplayed());
    }

    public void EnterAndVerifyNewAssociatedFacility(String facilityId){
        testWebDriver.waitForElementToAppear(addAssociatedFacilityButton);
        addAssociatedFacilityButton.click();
        testWebDriver.waitForElementToAppear(associatedFacilityField);
        testWebDriver.selectByValue(associatedFacilityField, facilityId);

        testWebDriver.waitForElementToAppear(associateFacilityButton);
        associateFacilityButton.click();

        testWebDriver.sleep(1500);
        testWebDriver.waitForElementToAppear(viewHereLink);

        SeleneseTestNgHelper.assertTrue("Success message is not showing up.",saveSuccessMessage.isDisplayed());
    }
}