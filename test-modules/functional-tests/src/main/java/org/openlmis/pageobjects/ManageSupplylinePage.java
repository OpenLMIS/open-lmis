/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;


public class ManageSupplylinePage extends Page {

  @FindBy(how = How.ID, using = "supplyline-add-new")
  private static WebElement addSupplylineButton;

    @FindBy(how = How.ID, using = "programid")
    private static WebElement programSelectField;

    @FindBy(how = How.ID, using = "lstprogram_0")
    private static WebElement programEditSelectField;

    @FindBy(how = How.ID, using = "supplyingfacilityid")
    private static WebElement facilitySelectField;

    @FindBy(how = How.ID, using = "lstfacility_0")
    private static WebElement facilityEditSelectField;

    @FindBy(how = How.ID, using = "supervisorynode")
    private static WebElement nodeSelectField;

    @FindBy(how = How.ID, using = "lstsupervisorynode_0")
    private static WebElement nodeEditSelectField;

    @FindBy(how = How.ID, using = "description")
    private static WebElement descriptionTextField;

    @FindBy(how = How.ID, using = "description_0")
    private static WebElement descriptionEditTextField;

    @FindBy(how = How.XPATH, using = "//input[@value='Create']")
    private static WebElement createButton;

    @FindBy(how = How.ID, using = "saveSuccessMsgDiv")
    private static WebElement saveSuccessMsgDiv;

    @FindBy(how = How.ID, using = "lstEditBtn_0")
    private static WebElement editFirstButton;

    @FindBy(how = How.ID, using = "lstEditBtn_1")
    private static WebElement editSecondButton;

    @FindBy(how = How.ID, using = "lstdescription_0")
    private static WebElement descriptionFirstNonEditableField;

    @FindBy(how = How.ID, using = "lstdescription_1")
    private static WebElement descriptionSecondNonEditableField;

    @FindBy(how = How.XPATH, using = " //input[@type='submit' and @value='Save']")
    private static WebElement saveButton;

   public ManageSupplylinePage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

   // send, create and verify new supply line data, repeat for multiple records
    public void createAndVerifySupplyline(Integer r, String program, String facility, String node, String desc) {
        testWebDriver.waitForElementToAppear(addSupplylineButton);
        enterCreateSupplylineDetails(program, facility, node,desc);
        SeleneseTestNgHelper.assertTrue("'" + desc +"' created successfully message not showing up", saveSuccessMsgDiv.isDisplayed());
        //first record
        SeleneseTestNgHelper.assertTrue("First edit button is not showing up", editFirstButton.isDisplayed());
        // second record
        if (r > 1) {
        SeleneseTestNgHelper.assertTrue("Second edit button is not showing up", editSecondButton.isDisplayed());
        }
    }

    // enter new data into form
    public void enterCreateSupplylineDetails(String program, String facility, String node, String desc) {
    addSupplylineButton.click();
    testWebDriver.waitForElementToAppear(programSelectField);
    programSelectField.clear();
    programSelectField.sendKeys(program);
    facilitySelectField.clear();
    facilitySelectField.sendKeys(facility);
    nodeSelectField.clear();
    nodeSelectField.sendKeys(node);
    descriptionTextField.clear();
    descriptionTextField.sendKeys(desc);
    createButton.click();
    testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
  }

   // send, save and verify edit data, repeat for multiple records
    public void editAndVerifySupplyline(Integer r, String program, String facility, String node, String desc) {
        testWebDriver.waitForElementToAppear(editFirstButton);
        enterEditSupplylineDetails(r, program, facility, node,desc);
        SeleneseTestNgHelper.assertTrue("'" + desc + "' updated successfully message not showing up", saveSuccessMsgDiv.isDisplayed());
        testWebDriver.sleep(500);
        if (r == 1) {
        SeleneseTestNgHelper.assertEquals(descriptionFirstNonEditableField.getText().trim(), desc);
        }else {
            SeleneseTestNgHelper.assertEquals(descriptionSecondNonEditableField.getText().trim(), desc);
        }
    }

   public void enterEditSupplylineDetails(Integer r, String program, String facility, String node, String desc) {
    editFirstButton.click();
    testWebDriver.waitForElementToAppear(descriptionEditTextField);
    programEditSelectField.clear();
    programEditSelectField.sendKeys(program);
    facilityEditSelectField.clear();
    facilityEditSelectField.sendKeys(facility);
    nodeEditSelectField.clear();
    nodeEditSelectField.sendKeys(node);
    descriptionEditTextField.clear();
    descriptionEditTextField.sendKeys(desc);
    saveButton.click();
    testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
    }

  // success message
  public void verifyMessage(String message) {
    String successMessage = saveSuccessMsgDiv.getText();
    SeleneseTestNgHelper.assertEquals(successMessage, message);

  }


}