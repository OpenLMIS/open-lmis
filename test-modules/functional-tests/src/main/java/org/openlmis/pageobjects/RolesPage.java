/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static org.openqa.selenium.support.How.ID;

public class RolesPage extends Page {

  @FindBy(how = ID, using = "role-add-new")
  private static WebElement createNewRoleButton = null;

  @FindBy(how = ID, using = "name")
  private static WebElement roleNameField = null;

  @FindBy(how = ID, using = "description")
  private static WebElement roleDescription = null;

  @FindBy(how = ID, using = "CONFIGURE_RNR")
  private static WebElement rightConfigureTemplate = null;

  @FindBy(how = ID, using = "MANAGE_FACILITY")
  private static WebElement rightManageFacilities = null;

  @FindBy(how = ID, using = "MANAGE_ROLE")
  private static WebElement rightManageRoles = null;

  @FindBy(how = ID, using = "MANAGE_DISTRIBUTION")
  private static WebElement rightManageDistribution = null;

  @FindBy(how = ID, using = "MANAGE_SCHEDULE")
  private static WebElement rightManageSchedules = null;

  @FindBy(how = ID, using = "UPLOADS")
  private static WebElement rightUploads = null;

  @FindBy(how = ID, using = "CREATE_REQUISITION")
  private static WebElement rightCreateRequisition = null;

  @FindBy(how = ID, using = "AUTHORIZE_REQUISITION")
  private static WebElement rightAuthorizeRequisition = null;

  @FindBy(how = ID, using = "APPROVE_REQUISITION")
  private static WebElement rightApproveRequisition = null;

  @FindBy(how = ID, using = "CONVERT_TO_ORDER")
  private static WebElement rightConvertToOrderRequisition = null;

  @FindBy(how = ID, using = "VIEW_ORDER")
  private static WebElement rightViewOrders = null;

  @FindBy(how = ID, using = "saveButton")
  private static WebElement saveButton = null;

  @FindBy(how = ID, using = "cancelButton")
  private static WebElement cancelButton = null;

  @FindBy(how = ID, using = "button_Cancel")
  private static WebElement cancelButtonOnModal = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMsgDiv = null;

  @FindBy(how = ID, using = "saveFailMessage")
  private static WebElement saveErrorMsgDiv = null;

  @FindBy(how = ID, using = "requisitionRoleType")
  private static WebElement requisitionRoleType = null;

  @FindBy(how = ID, using = "adminRoleType")
  private static WebElement adminRoleType = null;

  @FindBy(how = ID, using = "allocationRoleType")
  private static WebElement allocationRoleType = null;

  @FindBy(how = ID, using = "button_OK")
  private static WebElement continueButton = null;

  @FindBy(how = ID, using = "editRoleHeader")
  private static WebElement editRoleHeader = null;

  @FindBy(how = ID, using = "rolesHeader")
  private static WebElement rolesHeader = null;

  @FindBy(how = ID, using = "addNewRoleHeader")
  private static WebElement addNewRoleHeader = null;

  @FindBy(how = ID, using = "fulfillmentRoleType")
  private static WebElement facilityBasedRoleType = null;

  @FindBy(how = ID, using = "FACILITY_FILL_SHIPMENT")
  private static WebElement rightFillShipment = null;

  @FindBy(how = ID, using = "MANAGE_POD")
  private static WebElement rightManagePOD = null;

  private Map<String, WebElement> webElementMap = new HashMap<>();

  public RolesPage(TestWebDriver driver) {
    super(driver);

    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
    webElementMap.put("Configure Template", rightConfigureTemplate);
    webElementMap.put("Manage Facilities", rightManageFacilities);
    webElementMap.put("Manage Roles", rightManageRoles);
    webElementMap.put("Manage Schedules", rightManageSchedules);
    webElementMap.put("Uploads", rightUploads);
    webElementMap.put("Create Requisition", rightCreateRequisition);
    webElementMap.put("Authorize Requisition", rightAuthorizeRequisition);
    webElementMap.put("Approve Requisition", rightApproveRequisition);
    webElementMap.put("Convert To Order Requisition", rightConvertToOrderRequisition);
    webElementMap.put("View Orders Requisition", rightViewOrders);
    webElementMap.put("Manage Distribution", rightManageDistribution);
    webElementMap.put("Manage POD", rightManagePOD);
  }

  public Map<String, WebElement> getWebElementMap() {
    return webElementMap;
  }

  public WebElement getCreateNewRoleButton() {
    return createNewRoleButton;
  }

  public WebElement getSaveErrorMsgDiv() {
    return saveErrorMsgDiv;
  }

  public WebElement getAllocationRoleType() {
    return allocationRoleType;
  }

  public void createRole(String roleName, String roleDesc, List<String> rights, String roleType) {
    testWebDriver.waitForElementToAppear(createNewRoleButton);
    createNewRoleButton.click();
    switch (roleType) {
      case "Requisition":
        clickProgramRole();
        break;
      case "Admin":
        clickAdminRole();
        break;
      case "Fulfillment":
        testWebDriver.waitForElementToAppear(facilityBasedRoleType);
        facilityBasedRoleType.click();
        testWebDriver.sleep(100);
        break;
    }

    clickContinueButton();
    testWebDriver.sleep(1000);
    testWebDriver.handleScrollByPixels(0, 2000);
    for (String right : rights) {
      testWebDriver.sleep(500);
      webElementMap.get(right).click();
    }
    for (String right : rights) {
      if (!webElementMap.get(right).isSelected()) testWebDriver.click(webElementMap.get(right));
    }
    roleNameField.sendKeys(roleName);
    roleDescription.sendKeys(roleDesc);
    saveButton.click();
    testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
  }

  public void verifyCreatedRoleMessage(String roleName) {
    testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
    assertEquals(saveSuccessMsgDiv.getText().trim(), "\"" + roleName + "\" created successfully");
  }

  public void createFacilityBasedRole(String roleName, String roleDesc) {
    testWebDriver.waitForElementToAppear(createNewRoleButton);
    createNewRoleButton.click();
    facilityBasedRoleType.click();
    clickContinueButton();
    testWebDriver.sleep(1000);
    roleNameField.sendKeys(roleName);
    roleDescription.sendKeys(roleDesc);
    rightFillShipment.click();
    saveButton.click();
    testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
  }

  public void createRole(String roleName, String roleDesc, List<String> rights, boolean programDependant) {
    testWebDriver.waitForElementToAppear(createNewRoleButton);
    createNewRoleButton.click();
    if (programDependant) {
      clickProgramRole();
      clickContinueButton();
    }
    testWebDriver.sleep(1000);
    testWebDriver.handleScrollByPixels(0, 2000);
    for (String right : rights) {
      testWebDriver.sleep(500);
      webElementMap.get(right).click();
    }
    for (String right : rights) {
      if (!webElementMap.get(right).isSelected()) testWebDriver.click(webElementMap.get(right));
    }
    roleNameField.sendKeys(roleName);
    roleDescription.sendKeys(roleDesc);
    saveButton.click();
    testWebDriver.sleep(1000);
  }

  public void clickARole(String roleName) {
    WebElement role = testWebDriver.getElementByXpath("//a[contains(text(),'" + roleName + "')]");
    testWebDriver.waitForElementToAppear(role);
    role.click();
    testWebDriver.waitForElementToAppear(editRoleHeader);
  }

  public void clickProgramRole() {
    testWebDriver.waitForElementToAppear(requisitionRoleType);
    requisitionRoleType.click();
    testWebDriver.sleep(100);
  }

  public void clickAdminRole() {
    testWebDriver.waitForElementToAppear(adminRoleType);
    adminRoleType.click();
    testWebDriver.sleep(100);
  }

  public void verifyAdminRoleRadioNonEditable() {
    testWebDriver.waitForElementToAppear(adminRoleType);
    assertTrue(testWebDriver.getAttribute(adminRoleType, "disabled"), true);
  }

  public void verifyRoleSelected(List<String> roleList) {
    for (String right : roleList) {
      testWebDriver.sleep(500);
      assertTrue(webElementMap.get(right).isSelected());
    }
  }

  public void clickContinueButton() {
    testWebDriver.waitForElementToAppear(continueButton);
    testWebDriver.click(continueButton);
    testWebDriver.sleep(250);
  }

  public void clickSaveButton() {
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
    testWebDriver.sleep(100);
  }

  public void clickCancelButton() {
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
    testWebDriver.waitForElementToAppear(rolesHeader);
  }

  public void clickCancelButtonOnModal() {
    testWebDriver.waitForElementToAppear(cancelButtonOnModal);
    cancelButtonOnModal.click();
    testWebDriver.waitForElementToAppear(addNewRoleHeader);
  }

  public boolean isCreateNewRoleButtonDisplayed() {
    testWebDriver.waitForElementToAppear(createNewRoleButton);
    return createNewRoleButton.isDisplayed();
  }
}
