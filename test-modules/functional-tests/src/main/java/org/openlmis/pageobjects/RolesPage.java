/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.*;

public class RolesPage extends Page {


  private Map<String, WebElement> webElementMap = new HashMap();

  @FindBy(how = How.ID, using = "role-add-new")
  private static WebElement createNewRoleButton;

  @FindBy(how = How.ID, using = "name")
  private static WebElement roleNameField;

  @FindBy(how = How.ID, using = "description")
  private static WebElement roleDescription;

  @FindBy(how = How.XPATH, using = "//div[@id='rights-CONFIGURE_RNR")
  private static WebElement rightConfigureTemplate;

  @FindBy(how = How.XPATH, using = "//div[@id='rights-MANAGE_FACILITY']/input")
  private static WebElement rightManageFacilities;

  @FindBy(how = How.XPATH, using = "//div[@id='rights-MANAGE_ROLE']/input")
  private static WebElement rightManageRoles;

  @FindBy(how = How.XPATH, using = "//div[@id='allocationRights-MANAGE_DISTRIBUTION']/input")
  private static WebElement rightManageDistribution;

  @FindBy(how = How.XPATH, using = "//div[@id='rights-MANAGE_SCHEDULE']/input")
  private static WebElement rightManageSchedules;

  @FindBy(how = How.XPATH, using = "//div[@id='rights-UPLOADS']/input")
  private static WebElement rightUploads;

  @FindBy(how = How.XPATH, using = "//div[@id='requisitionRights-CREATE_REQUISITION']/input")
  private static WebElement rightCreateRequisition;

  @FindBy(how = How.XPATH, using = "//div[@id='requisitionRights-AUTHORIZE_REQUISITION']/input")
  private static WebElement rightAuthorizeRequisition;

  @FindBy(how = How.XPATH, using = "//div[@id='requisitionRights-APPROVE_REQUISITION']/input")
  private static WebElement rightApproveRequisition;

  @FindBy(how = How.XPATH, using = "//div[@id='rights-CONVERT_TO_ORDER']/input")
  private static WebElement rightConvertToOrderRequisition;

  @FindBy(how = How.XPATH, using = "//div[@id='rights-VIEW_ORDER']/input")
  private static WebElement rightViewOrders;

  @FindBy(how = How.XPATH, using = "//input[@value='Save']")
  private static WebElement saveButton;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Cancel')]")
  private static WebElement cancelButton;

  @FindBy(how = How.ID, using = "button_Cancel")
  private static WebElement cancelButtonOnModal;

  @FindBy(how = How.ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMsgDiv;

  @FindBy(how = How.ID, using = "saveFailMessage")
  private static WebElement saveErrorMsgDiv;

  @FindBy(how = How.ID, using = "requisitionRoleType")
  private static WebElement requisitionRoleType;

  @FindBy(how = How.ID, using = "adminRoleType")

  private static WebElement adminRoleType;

  @FindBy(how = How.ID, using = "allocationRoleType")
  private static WebElement allocationRoleType;

  @FindBy(how = How.ID, using = "button_OK")
  private static WebElement continueButton;

  @FindBy(how = How.XPATH, using = "//h2[contains(text(),'Edit role')]")
  private static WebElement editRoleHeader;

  @FindBy(how = How.XPATH, using = "//h2/span[contains(text(),'Roles')]")
  private static WebElement rolesHeader;

  @FindBy(how = How.XPATH, using = "//h2[contains(text(),'Add new role')]")
  private static WebElement addNewRoleHeader;


  public RolesPage(TestWebDriver driver) throws IOException {
    super(driver);

    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
    testWebDriver.waitForElementToAppear(createNewRoleButton);
    assertTrue(createNewRoleButton.isDisplayed());
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
  }

  public Map<String, WebElement> getWebElementMap() {
    return webElementMap;
  }

  public  WebElement getCreateNewRoleButton() {
    return createNewRoleButton;
  }

  public  WebElement getRoleNameField() {
    return roleNameField;
  }

  public  WebElement getRoleDescription() {
    return roleDescription;
  }

  public  WebElement getRightConfigureTemplate() {
    return rightConfigureTemplate;
  }

  public  WebElement getRightManageFacilities() {
    return rightManageFacilities;
  }

  public  WebElement getRightManageRoles() {
    return rightManageRoles;
  }

  public  WebElement getRightManageSchedules() {
    return rightManageSchedules;
  }

  public  WebElement getRightUploads() {
    return rightUploads;
  }

  public  WebElement getRightCreateRequisition() {
    return rightCreateRequisition;
  }

  public  WebElement getRightAuthorizeRequisition() {
    return rightAuthorizeRequisition;
  }

  public  WebElement getRightApproveRequisition() {
    return rightApproveRequisition;
  }

  public  WebElement getRightConvertToOrderRequisition() {
    return rightConvertToOrderRequisition;
  }

  public  WebElement getRightViewOrders() {
    return rightViewOrders;
  }

  public  WebElement getSaveButton() {
    return saveButton;
  }

  public  WebElement getCancelButton() {
    return cancelButton;
  }

  public  WebElement getSaveSuccessMsgDiv() {
    return saveSuccessMsgDiv;
  }

  public  WebElement getSaveErrorMsgDiv() {
    return saveErrorMsgDiv;
  }

  public  WebElement getContinueButton() {
    return continueButton;
  }

  public  WebElement getEditRoleHeader() {
    return editRoleHeader;
  }

  public  WebElement getRolesHeader() {
    return rolesHeader;
  }

  public  WebElement getRequisitionRoleType() {
    return requisitionRoleType;
  }

  public  WebElement getAdminRoleType() {
    return adminRoleType;
  }

  public  WebElement getAllocationRoleType() {
    return allocationRoleType;
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
      if (!webElementMap.get(right).isSelected())
        testWebDriver.click(webElementMap.get(right));
    }
    roleNameField.sendKeys(roleName);
    roleDescription.sendKeys(roleDesc);
    saveButton.click();
    testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
    assertEquals(saveSuccessMsgDiv.getText().trim(), "'" + roleName + "' created successfully");

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

  public void verifyProgramRoleRadioNonEditable() {
    testWebDriver.waitForElementToAppear(requisitionRoleType);
    assertTrue(testWebDriver.getAttribute(requisitionRoleType, "disabled"), true);
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

  public void clickContinueButton()
  {
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
}
