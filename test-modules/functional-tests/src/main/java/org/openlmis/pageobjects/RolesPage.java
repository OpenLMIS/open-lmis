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

  @FindBy(how = ID, using = "reportingRoleType")
  private static WebElement reportingRoleType = null;

  @FindBy(how = ID, using = "button_OK")
  private static WebElement continueButton = null;

  @FindBy(how = ID, using = "editRoleHeader")
  private static WebElement editRoleHeader = null;

  @FindBy(how = ID, using = "rolesHeader")
  private static WebElement rolesHeader = null;

  @FindBy(how = ID, using = "nameHeader")
  private static WebElement nameHeader = null;

  @FindBy(how = ID, using = "descriptionHeader")
  private static WebElement descriptionHeader = null;

  @FindBy(how = ID, using = "rightsHeader")
  private static WebElement rightsHeader = null;

  @FindBy(how = ID, using = "addNewRoleHeader")
  private static WebElement addNewRoleHeader = null;

  @FindBy(how = ID, using = "roleNameLabel")
  private static WebElement roleNameLabel = null;

  @FindBy(how = ID, using = "roleDescriptionLabel")
  private static WebElement roleDescriptionLabel = null;

  @FindBy(how = ID, using = "assignRightsLabel")
  private static WebElement assignRightsLabel = null;

  @FindBy(how = ID, using = "roleMixedWarning")
  private static WebElement roleMixedWarning = null;

  @FindBy(how = ID, using = "fulfillmentRoleType")
  private static WebElement fulfilmentRoleType = null;

  @FindBy(how = ID, using = "FACILITY_FILL_SHIPMENT")
  private static WebElement rightFillShipment = null;

  @FindBy(how = ID, using = "MANAGE_POD")
  private static WebElement rightManagePOD = null;

  @FindBy(how = ID, using = "MANAGE_REPORT")
  private static WebElement rightManageReport = null;

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
    webElementMap.put("Manage Report", rightManageReport);
    webElementMap.put("Fill shipment", rightFillShipment);
  }

  public String getRolesHeader() {
    testWebDriver.waitForElementToAppear(rolesHeader);
    return rolesHeader.getText();
  }

  public String getNameHeader() {
    testWebDriver.waitForElementToAppear(nameHeader);
    return nameHeader.getText();
  }

  public String getDescriptionHeader() {
    testWebDriver.waitForElementToAppear(descriptionHeader);
    return descriptionHeader.getText();
  }

  public String getRightsHeader() {
    testWebDriver.waitForElementToAppear(rightsHeader);
    return rightsHeader.getText();
  }

  public String getAddNewRoleHeader() {
    testWebDriver.waitForElementToAppear(addNewRoleHeader);
    return addNewRoleHeader.getText();
  }

  public String getEditRoleHeader() {
    testWebDriver.waitForElementToAppear(editRoleHeader);
    return editRoleHeader.getText();
  }

  public String getRoleNameLabel() {
    testWebDriver.waitForElementToAppear(roleNameLabel);
    return roleNameLabel.getText();
  }

  public String getRoleDescriptionLabel() {
    testWebDriver.waitForElementToAppear(roleDescriptionLabel);
    return roleDescriptionLabel.getText();
  }

  public String getAssignRightsLabel() {
    testWebDriver.waitForElementToAppear(assignRightsLabel);
    return assignRightsLabel.getText();
  }

  public String getRoleMixWarning() {
    testWebDriver.waitForElementToAppear(roleMixedWarning);
    return roleMixedWarning.getText();
  }

  public String getAdminRoleLabel() {
    testWebDriver.waitForElementToAppear(adminRoleType);
    return adminRoleType.getText();
  }

  public String getReportingRoleLabel() {
    testWebDriver.waitForElementToAppear(reportingRoleType);
    return reportingRoleType.getText();
  }

  public String getAllocationRoleLabel() {
    testWebDriver.waitForElementToAppear(allocationRoleType);
    return allocationRoleType.getText();
  }

  public String getRequisitionRoleLabel() {
    testWebDriver.waitForElementToAppear(requisitionRoleType);
    return requisitionRoleType.getText();
  }

  public String getFulfilmentRoleLabel() {
    testWebDriver.waitForElementToAppear(fulfilmentRoleType);
    return fulfilmentRoleType.getText();
  }

  public String getName(int rowNumber) {
    WebElement name = testWebDriver.getElementById("role" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(name);
    return name.getText();
  }

  public String getDescription(int rowNumber) {
    WebElement description = testWebDriver.getElementById("description" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(description);
    return description.getText();
  }

  public String getRights(int rowNumber, int rightNumber) {
    WebElement right = testWebDriver.getElementById("right" + (rowNumber - 1) + (rightNumber - 1));
    testWebDriver.waitForElementToAppear(right);
    return right.getText();
  }

  public void clickCreateNewRoleButton() {
    testWebDriver.waitForElementToAppear(createNewRoleButton);
    createNewRoleButton.click();
  }

  public String getSaveErrorMsg() {
    testWebDriver.waitForElementToAppear(saveErrorMsgDiv);
    return saveErrorMsgDiv.getText().trim();
  }

  public void selectAllocationRoleType() {
    testWebDriver.waitForElementToAppear(allocationRoleType);
    allocationRoleType.click();
  }

  public void createRole(String roleName, String roleDesc, List<String> rights, String roleType) {
    testWebDriver.waitForElementToAppear(createNewRoleButton);
    createNewRoleButton.click();
    switch (roleType) {
      case "Requisition":
        clickRequisitionTypeRole();
        clickContinueButton();
        testWebDriver.sleep(1000);
        break;
      case "Admin":
        clickAdminTypeRole();
        break;
      case "Fulfillment":
        testWebDriver.waitForElementToAppear(fulfilmentRoleType);
        fulfilmentRoleType.click();
        testWebDriver.sleep(100);
        clickContinueButton();
        testWebDriver.sleep(1000);
        break;
      case "Reporting":
        clickReportingTypeRole();
        clickContinueButton();
        testWebDriver.sleep(1000);
        break;
    }

    testWebDriver.handleScrollByPixels(0, 2000);
    for (String right : rights) {
      selectRight(right);
    }
    for (String right : rights) {
      if (!isRightSelected(right)) selectRight(right);
    }
    enterRoleName(roleName);
    roleDescription.sendKeys(roleDesc);
    clickSaveButton();
  }

  public String getSuccessMessage() {
    testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
    return saveSuccessMsgDiv.getText().trim();
  }

  public void clickRole(String roleName) {
    WebElement role = testWebDriver.getElementByXpath("//a[contains(text(),'" + roleName + "')]");
    testWebDriver.waitForElementToAppear(role);
    role.click();
    testWebDriver.waitForElementToAppear(editRoleHeader);
  }

  public void clickRequisitionTypeRole() {
    testWebDriver.waitForElementToAppear(requisitionRoleType);
    requisitionRoleType.click();
    testWebDriver.sleep(100);
  }

  public void clickAdminTypeRole() {
    testWebDriver.waitForElementToAppear(adminRoleType);
    adminRoleType.click();
    testWebDriver.sleep(100);
  }

  public boolean isAdminRoleRadioEnabled() {
    testWebDriver.waitForElementToAppear(adminRoleType);
    return adminRoleType.isEnabled();
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

  public void enterRoleName(String roleName) {
    testWebDriver.waitForElementToAppear(roleNameField);
    sendKeys(roleNameField, roleName);
  }

  public void selectRight(String rightName) {
    testWebDriver.sleep(500);
    webElementMap.get(rightName).click();
  }

  public boolean isRightSelected(String right) {
    testWebDriver.sleep(500);
    return webElementMap.get(right).isSelected();
  }

  public boolean isRightEnabled(String right) {
    testWebDriver.sleep(500);
    return webElementMap.get(right).isEnabled();
  }

  public void clickReportingTypeRole() {
    testWebDriver.waitForElementToAppear(reportingRoleType);
    reportingRoleType.click();
    testWebDriver.sleep(100);
  }
}
