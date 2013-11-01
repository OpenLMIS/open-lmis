/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.extension.pageobjects;

import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.Page;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;

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