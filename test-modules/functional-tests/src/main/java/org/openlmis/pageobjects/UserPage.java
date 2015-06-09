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
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;

public class UserPage extends FilterSearchPage {

  @FindBy(how = ID, using = "userAddNew")
  private static WebElement addNewButton = null;

  @FindBy(how = ID, using = "userName")
  private static WebElement userNameField = null;

  @FindBy(how = ID, using = "email")
  private static WebElement emailField = null;

  @FindBy(how = ID, using = "firstName")
  private static WebElement firstNameField = null;

  @FindBy(how = ID, using = "lastName")
  private static WebElement lastNameField = null;

  @FindBy(how = ID, using = "userSaveButton")
  private static WebElement saveButton = null;

  @FindBy(how = ID, using = "userCancelButton")
  private static WebElement cancelButton = null;

  @FindBy(how = ID, using = "userDisableButton")
  private static WebElement disableButton = null;

  @FindBy(how = ID, using = "userEnableButton")
  private static WebElement enableButton = null;

  @FindBy(how = ID, using = "searchUser")
  private static WebElement searchUserTextField = null;

  @FindBy(how = ID, using = "resetPassword")
  private static WebElement resetPasswordButton = null;

  @FindBy(how = ID, using = "password1")
  private static WebElement newPasswordField = null;

  @FindBy(how = ID, using = "password2")
  private static WebElement confirmPasswordField = null;

  @FindBy(how = ID, using = "resetPasswordDone")
  private static WebElement resetPasswordDone = null;

  @FindBy(how = ID, using = "button_OK")
  private static WebElement okButton = null;

  @FindBy(how = ID, using = "editUserLabel")
  private static WebElement editUserHeader = null;

  @FindBy(how = How.XPATH, using = "//form[@id='create-user']/div/div[1]/div[8]/div/ng-switch/span")
  private static WebElement verifiedLabel = null;

  @FindBy(how = ID, using = "expandAll")
  private static WebElement expandAllOption = null;

  @FindBy(how = ID, using = "collapseAll")
  private static WebElement collapseAllOption = null;

  @FindBy(how = ID, using = "homeFacilityRoles")
  private static WebElement homeFacilityRolesAccordion = null;

  @FindBy(how = ID, using = "programSelected")
  private static WebElement homeFacilityPrograms = null;

  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[15]")
  private static WebElement roleInputFieldHomeFacility = null;

  @FindBy(how = How.XPATH, using = "//div[@class='select2-result-label']/span")
  private static WebElement rolesSelectField = null;

  @FindBy(how = ID, using = "addHomeRole")
  private static WebElement addHomeFacilityRolesButton = null;

  @FindBy(how = ID, using = "supervisoryRoles")
  private static WebElement supervisoryRolesAccordion = null;

  @FindBy(how = ID, using = "programToSupervise")
  private static WebElement programsToSupervise = null;

  @FindBy(how = ID, using = "supervisoryNodeToSupervise")
  private static WebElement supervisoryNodeToSupervise = null;

  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[17]")
  private static WebElement rolesInputFieldSupervisoryRole = null;

  @FindBy(how = ID, using = "addSupervisoryRole")
  private static WebElement addSupervisoryRoleButton = null;

  @FindBy(how = ID, using = "orderFulfillmentRoles")
  private static WebElement orderFulfillmentRolesAccordion = null;

  @FindBy(how = ID, using = "reportingRoles")
  private static WebElement reportingRolesAccordion = null;

  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[19]")
  private static WebElement rolesInputFieldWarehouse = null;

  @FindBy(how = XPATH, using = "//div[@id='s2id_reportingRoles']/ul/li[@class='select2-search-field']/input")
  private static WebElement rolesInputFieldReporting = null;

  @FindBy(how = ID, using = "warehouseToSelect")
  private static WebElement warehouseToSelect = null;

  @FindBy(how = ID, using = "addFulfillmentRole")
  private static WebElement addWarehouseRoleButton = null;

  @FindBy(how = ID, using = "allocationRoles")
  private static WebElement deliveryZonesAccordion = null;

  @FindBy(how = ID, using = "deliveryZoneToSelect")
  private static WebElement zoneToDelivery = null;

  @FindBy(how = ID, using = "programToDelivery")
  private static WebElement programToDeliver = null;

  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[21]")
  private static WebElement rolesInputFieldMDeliveryZone = null;

  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[18]")
  private static WebElement rolesInputFieldDeliveryZone = null;

  @FindBy(how = ID, using = "addAllocationRole")
  private static WebElement addDeliveryZoneRoleButton = null;

  @FindBy(how = ID, using = "adminRoles")
  private static WebElement adminAndGeneralOperationsRolesAccordion = null;

  @FindBy(how = XPATH, using = "//*[@id='s2id_adminRoles']/ul/li[@class='select2-search-field']/input")
  private static WebElement adminRolesInputField = null;

  @FindBy(how = ID, using = "viewHereLink")
  private static WebElement viewHereLink = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private static WebElement successMessage = null;

  @FindBy(how = How.XPATH, using = "//input[contains(text(),'Remove')]")
  private static WebElement removeButton = null;

  @FindBy(how = How.XPATH, using = "//div[6]/div[2]/ng-include/div/div[1]/div[2]/div[1]/div/label[@class='ng-binding']")
  private static WebElement addedDeliveryZoneLabel = null;

  @FindBy(how = ID, using = "restrictLoginYes")
  private static WebElement restrictLoginYesOption = null;

  @FindBy(how = ID, using = "restrictLoginNo")
  private static WebElement restrictLoginNoOption = null;

  @FindBy(how = ID, using = "associatedFacilityField")
  private static WebElement associatedFacilityField = null;

  @FindBy(how = ID, using = "searchIcon")
  private static WebElement searchIcon = null;

  @FindBy(how = ID, using = "clearFacility")
  private static WebElement clearFacility = null;

  @FindBy(how = ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMsg = null;

  @FindBy(how = ID, using = "oneResultMessage")
  private static WebElement oneResultMessage = null;

  @FindBy(how = ID, using = "noResultMessage")
  private static WebElement noResultMessage = null;

  @FindBy(how = ID, using = "nResultsMessage")
  private static WebElement nResultsMessage = null;

  @FindBy(how = ID, using = "closeButton")
  private static WebElement closeButton = null;

  @FindBy(how = ID, using = "nameHeader")
  private static WebElement nameHeader = null;

  @FindBy(how = ID, using = "userNameHeader")
  private static WebElement userNameHeader = null;

  @FindBy(how = ID, using = "emailHeader")
  private static WebElement emailHeader = null;

  @FindBy(how = ID, using = "verifiedHeader")
  private static WebElement verifiedHeader = null;

  @FindBy(how = ID, using = "activeHeader")
  private static WebElement activeHeader = null;

  @FindBy(how = ID, using = "dialogMessage")
  private static WebElement dialogMessage = null;

  @FindBy(how = ID, using = "searchUserLabel")
  private static WebElement searchUserLabel = null;

  public UserPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(1);
  }

  public void searchUser(String user) {
    testWebDriver.waitForElementToAppear(searchUserTextField);
    sendKeys(searchUserTextField, user);
  }

  public void resetPassword(String newPassword, String confirmPassword) {
    testWebDriver.waitForElementToAppear(resetPasswordButton);
    resetPasswordButton.click();

    testWebDriver.waitForElementToAppear(newPasswordField);
    sendKeys(newPasswordField, newPassword);

    testWebDriver.waitForElementToAppear(confirmPasswordField);
    sendKeys(confirmPasswordField, confirmPassword);

    testWebDriver.waitForElementToBeEnabled(resetPasswordDone);
    resetPasswordDone.click();
  }

  public void enterUserDetails(String userName, String email, String firstName, String lastName) {
    testWebDriver.waitForElementToAppear(addNewButton);
    addNewButton.click();
    testWebDriver.waitForElementToAppear(userNameField);
    sendKeys(userNameField, userName);
    testWebDriver.waitForElementToAppear(emailField);
    sendKeys(emailField, email);
    testWebDriver.waitForElementToAppear(firstNameField);
    sendKeys(firstNameField, firstName);
    testWebDriver.waitForElementToAppear(lastNameField);
    sendKeys(lastNameField, lastName);
    testWebDriver.handleScroll();
    clickRestrictLoginNo();
    assertFalse(resetPasswordButton.isDisplayed());
    assertFalse(disableButton.isDisplayed());

    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
    testWebDriver.waitForElementToAppear(viewHereLink);
  }

  public String getSuccessMessage() {
    testWebDriver.waitForElementToAppear(successMessage);
    return successMessage.getText();
  }

  public void enterUserHomeFacility(String facilityCode) {
    if (clearFacility.isDisplayed()) {
      clearFacility.click();
    }
    testWebDriver.sleep(500);
    testWebDriver.handleScrollByPixels(0, 1600);
    testWebDriver.sleep(500);
    associatedFacilityField.click();
    searchFacility(facilityCode);
  }

  public void ExpandAll() {
    testWebDriver.waitForElementToAppear(expandAllOption);
    expandAllOption.click();
  }

  public void collapseAll() {
    testWebDriver.waitForElementToAppear(collapseAllOption);
    collapseAllOption.click();
  }

  public void clickRestrictLoginYes() {
    testWebDriver.waitForElementToAppear(restrictLoginYesOption);
    restrictLoginYesOption.click();
  }

  public void clickRestrictLoginNo() {
    testWebDriver.waitForElementToAppear(restrictLoginNoOption);
    restrictLoginNoOption.click();
  }

  public boolean isProgramsToSuperviseDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(programsToSupervise);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return programsToSupervise.isDisplayed();
  }

  public boolean isProgramToDeliverDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(programToDeliver);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return programToDeliver.isDisplayed();
  }

  public boolean isWarehouseToSelectDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(warehouseToSelect);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return warehouseToSelect.isDisplayed();
  }

  public void enterMyFacilityAndMySupervisedFacilityData(String facilityCode, String program1, String node, String role, String roleType) {
    assertFalse(homeFacilityRolesAccordion.isDisplayed());
    if (!roleType.equals("ADMIN")) {
      enterUserHomeFacility(facilityCode);
      testWebDriver.waitForAjax();
      selectFacility(1);
      testWebDriver.waitForAjax();
      testWebDriver.scrollAndClick(homeFacilityRolesAccordion);
      testWebDriver.sleep(500);
      testWebDriver.scrollToElement(homeFacilityPrograms);
      testWebDriver.selectByVisibleText(homeFacilityPrograms, program1);
      testWebDriver.waitForElementToAppear(roleInputFieldHomeFacility);
      testWebDriver.scrollAndClick(roleInputFieldHomeFacility);
      sendKeys(roleInputFieldHomeFacility, role);
      testWebDriver.waitForElementToAppear(rolesSelectField);
      testWebDriver.scrollAndClick(rolesSelectField);
      testWebDriver.scrollAndClick(addHomeFacilityRolesButton);
      testWebDriver.waitForElementToAppear(supervisoryRolesAccordion);
      testWebDriver.scrollAndClick(supervisoryRolesAccordion);
      testWebDriver.sleep(500);
      testWebDriver.waitForElementToAppear(programsToSupervise);
      testWebDriver.selectByVisibleText(programsToSupervise, program1);
      testWebDriver.waitForElementToAppear(supervisoryNodeToSupervise);
      testWebDriver.selectByVisibleText(supervisoryNodeToSupervise, node);
      testWebDriver.sleep(1000);
      testWebDriver.handleScroll();
      testWebDriver.waitForElementToAppear(rolesInputFieldSupervisoryRole);
      testWebDriver.scrollAndClick(rolesInputFieldSupervisoryRole);
      sendKeys(rolesInputFieldSupervisoryRole, role);
      testWebDriver.waitForElementToAppear(rolesSelectField);
      testWebDriver.scrollAndClick(rolesSelectField);

      assertEquals(testWebDriver.getFirstSelectedOption(supervisoryNodeToSupervise).getText(), node);
      assertEquals(testWebDriver.getFirstSelectedOption(programsToSupervise).getText(), program1);
      testWebDriver.waitForElementToAppear(addSupervisoryRoleButton);
      testWebDriver.scrollAndClick(addSupervisoryRoleButton);

    } else {
      testWebDriver.handleScroll();
      testWebDriver.waitForElementToAppear(adminAndGeneralOperationsRolesAccordion);
      testWebDriver.scrollAndClick(adminAndGeneralOperationsRolesAccordion);
      testWebDriver.sleep(500);
      testWebDriver.waitForElementToAppear(adminRolesInputField);
      testWebDriver.scrollAndClick(adminRolesInputField);
      sendKeys(adminRolesInputField, role);
      testWebDriver.waitForElementToAppear(rolesSelectField);
      testWebDriver.scrollAndClick(rolesSelectField);
    }
  }

  public void verifyUserUpdated(String firstName, String lastName) {
    testWebDriver.sleep(1000);
    assertTrue("User '" + firstName + " " + lastName + "' has been successfully updated message is not getting displayed",
      successMessage.isDisplayed());
  }

  public void assignWarehouse(String warehouse, String role) {
    testWebDriver.waitForElementToAppear(orderFulfillmentRolesAccordion);
    orderFulfillmentRolesAccordion.click();
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(warehouseToSelect);
    testWebDriver.selectByVisibleText(warehouseToSelect, warehouse);
    testWebDriver.waitForElementToAppear(rolesInputFieldWarehouse);
    rolesInputFieldWarehouse.click();
    rolesInputFieldWarehouse.clear();
    rolesInputFieldWarehouse.sendKeys(role);
    testWebDriver.waitForElementToAppear(rolesSelectField);
    rolesSelectField.click();
    assertEquals(testWebDriver.getFirstSelectedOption(warehouseToSelect).getText(), warehouse);
    testWebDriver.waitForElementToAppear(addWarehouseRoleButton);
    addWarehouseRoleButton.click();
    testWebDriver.sleep(1000);
    verifyWarehouseSelectedNotAvailable(warehouse);
    testWebDriver.waitForElementToAppear(orderFulfillmentRolesAccordion);
    orderFulfillmentRolesAccordion.click();
    testWebDriver.sleep(500);
  }

  public void assignReportingRole(String role) {
    testWebDriver.waitForElementToAppear(reportingRolesAccordion);
    reportingRolesAccordion.click();
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(rolesInputFieldReporting);
    rolesInputFieldReporting.click();
    sendKeys(rolesInputFieldReporting, role);
    testWebDriver.waitForElementToAppear(rolesSelectField);
    rolesSelectField.click();
    testWebDriver.sleep(1000);
  }

  public void assignAdminRole(String role) {
    testWebDriver.waitForElementToAppear(adminAndGeneralOperationsRolesAccordion);
    testWebDriver.scrollAndClick(adminAndGeneralOperationsRolesAccordion);
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(adminRolesInputField);
    testWebDriver.scrollAndClick(adminRolesInputField);
    sendKeys(adminRolesInputField, role);
    testWebDriver.waitForElementToAppear(rolesSelectField);
    rolesSelectField.click();
    testWebDriver.sleep(1000);
  }

  public void verifyMessage(String message) {
    testWebDriver.waitForElementToAppear(successMessage);
    assertEquals(successMessage.getText(), message);
  }

  public void enterDeliveryZoneData(String deliveryZoneCode, String program, String role) {
    testWebDriver.handleScroll();
    testWebDriver.getElementByXpath("//a[contains(text(),'Delivery zones')]").click();
    testWebDriver.waitForElementToAppear(zoneToDelivery);
    testWebDriver.selectByVisibleText(zoneToDelivery, deliveryZoneCode);
    testWebDriver.waitForElementToAppear(programToDeliver);
    testWebDriver.selectByVisibleText(programToDeliver, program);
    testWebDriver.waitForElementToAppear(rolesInputFieldMDeliveryZone);
    rolesInputFieldMDeliveryZone.click();
    rolesInputFieldMDeliveryZone.clear();
    rolesInputFieldMDeliveryZone.sendKeys(role);
    rolesInputFieldMDeliveryZone.sendKeys(Keys.RETURN);
    addDeliveryZoneRoleButton.click();
  }

  public void enterDeliveryZoneDataWithoutHomeAndSupervisoryRolesAssigned(String deliveryZoneCode, String program, String role) {
    testWebDriver.handleScroll();
    deliveryZonesAccordion.click();
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(zoneToDelivery);
    testWebDriver.selectByVisibleText(zoneToDelivery, deliveryZoneCode);
    testWebDriver.waitForElementToAppear(programToDeliver);
    testWebDriver.selectByVisibleText(programToDeliver, program);
    testWebDriver.waitForElementToAppear(rolesInputFieldDeliveryZone);
    rolesInputFieldDeliveryZone.click();
    rolesInputFieldDeliveryZone.clear();
    rolesInputFieldDeliveryZone.sendKeys(role);
    rolesInputFieldDeliveryZone.sendKeys(Keys.RETURN);
    testWebDriver.waitForElementToAppear(addDeliveryZoneRoleButton);
    addDeliveryZoneRoleButton.click();
  }

  public void removeRole(int indexOfCancelIcon, boolean adminRole) {
    int counter = 1;
    List<WebElement> closeButtons = testWebDriver.getElementsByXpath("//a[@class='select2-search-choice-close']");
    for (WebElement closeButton : closeButtons) {
      if (counter == indexOfCancelIcon) {
        closeButton.click();
        if (adminRole)
          clickOk();
        testWebDriver.sleep(100);
        counter++;
      }
    }
  }

  public void clickCancelButton() {
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
  }

  public void clickSaveButton() {
    testWebDriver.waitForElementToBeEnabled(saveButton);
    saveButton.click();
  }

  public void disableUser(String userName) {
    testWebDriver.waitForElementToBeEnabled(disableButton);
    disableButton.click();
    assertEquals(testWebDriver.getElementByXpath("//*[@id='disableUserDialog']/div[1]/h3").getText(), "Disable user");
    assertEquals(dialogMessage.getText(), "User \"" + userName + "\" will be disabled");
    clickOk();
  }

  public void clickEnableButton() {
    testWebDriver.waitForElementToBeEnabled(enableButton);
    enableButton.click();
    clickOk();
  }

  public void verifyRolePresent(String roleName) {
    testWebDriver.sleep(500);
    WebElement roleElement = testWebDriver.getElementByXpath("//div[contains(text(),'" + roleName + "')]");
    assertTrue(roleName + " should be displayed", roleElement.isDisplayed());
  }

  public void verifyRoleNotPresent(String roleName) {
    boolean rolePresent;
    try {
      testWebDriver.sleep(500);
      WebElement element = testWebDriver.getElementByXpath("//div[contains(text(),'" + roleName + "')]");
      rolePresent = element.isDisplayed();
    } catch (ElementNotVisibleException e) {
      rolePresent = false;
    } catch (NoSuchElementException e) {
      rolePresent = false;
    }
    assertFalse(rolePresent);
  }

  public String getAllProgramsToSupervise() {
    testWebDriver.waitForElementToAppear(programsToSupervise);
    return programsToSupervise.getText();
  }

  public String getAllProgramsHomeFacility() {
    testWebDriver.waitForElementToAppear(homeFacilityPrograms);
    return homeFacilityPrograms.getText();
  }

  public String getAllWarehouseToSelect() {
    testWebDriver.waitForElementToAppear(warehouseToSelect);
    return warehouseToSelect.getText();
  }

  public void clickViewHere() {
    testWebDriver.waitForElementToAppear(viewHereLink);
    viewHereLink.click();
    testWebDriver.waitForElementToAppear(editUserHeader);
  }

  public void clickOk() {
    testWebDriver.waitForElementToAppear(okButton);
    okButton.click();
  }

  public void verifyRemoveNotPresent() {
    boolean removePresent;
    try {
      testWebDriver.sleep(500);
      removeButton.isDisplayed();
      removePresent = true;
    } catch (ElementNotVisibleException e) {
      removePresent = false;
    } catch (NoSuchElementException e) {
      removePresent = false;
    }
    assertFalse(removePresent);
  }

  public void clickRemoveButtonWithOk(int removeButtonNumber) {
    testWebDriver.sleep(500);
    testWebDriver.getElementByXpath("(//input[@value='Remove'])[" + removeButtonNumber + "]").click();
    clickOk();
  }

  public String getAddedDeliveryZoneLabel() {
    testWebDriver.waitForElementToAppear(addedDeliveryZoneLabel);
    return addedDeliveryZoneLabel.getText();
  }

  public String getVerifiedLabel() {
    testWebDriver.waitForElementToAppear(verifiedLabel);
    return verifiedLabel.getText();
  }

  public void clickWarehouseRolesAccordion() {
    testWebDriver.waitForElementToAppear(orderFulfillmentRolesAccordion);
    orderFulfillmentRolesAccordion.click();
    testWebDriver.sleep(500);
  }

  public void clickHomeFacilityRolesAccordion() {
    testWebDriver.waitForElementToAppear(homeFacilityRolesAccordion);
    homeFacilityRolesAccordion.click();
    testWebDriver.sleep(500);
//      testWebDriver.refresh();
  }

  public void clickSupervisoryRolesAccordion() {
    testWebDriver.waitForElementToAppear(supervisoryRolesAccordion);
    supervisoryRolesAccordion.click();
    testWebDriver.sleep(500);
  }

  public void clickDeliveryZonesAccordion() {
    testWebDriver.waitForElementToAppear(deliveryZonesAccordion);
    deliveryZonesAccordion.click();
    testWebDriver.sleep(500);
  }

  private void verifyWarehouseSelectedNotAvailable(String warehouse1) {
    assertFalse(getAllWarehouseToSelect().contains(warehouse1));
  }

  public void clickSearchIcon() {
    testWebDriver.waitForElementToAppear(searchIcon);
    searchIcon.click();
  }

  public void clickUserName(int rowNumber) {
    WebElement element = testWebDriver.getElementById("name" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(element);
    element.click();
  }

  public void clickResetPasswordButton() {
    testWebDriver.waitForElementToAppear(resetPasswordButton);
    resetPasswordButton.click();
  }

  public String getSaveErrorMessage() {
    testWebDriver.waitForElementToAppear(saveErrorMsg);
    return saveErrorMsg.getText();
  }

  public String getOneResultMessage() {
    testWebDriver.waitForElementToAppear(oneResultMessage);
    return oneResultMessage.getText();
  }

  public String getNResultsMessage() {
    testWebDriver.waitForElementToAppear(nResultsMessage);
    return nResultsMessage.getText();
  }

  public String getNoResultMessage() {
    testWebDriver.waitForElementToAppear(noResultMessage);
    return noResultMessage.getText();
  }

  public String getNameHeader() {
    testWebDriver.waitForElementToAppear(nameHeader);
    return nameHeader.getText();
  }

  public String getUserNameHeader() {
    testWebDriver.waitForElementToAppear(userNameHeader);
    return userNameHeader.getText();
  }

  public String getEmailHeader() {
    testWebDriver.waitForElementToAppear(emailHeader);
    return emailHeader.getText();
  }

  public String getVerifiedHeader() {
    testWebDriver.waitForElementToAppear(verifiedHeader);
    return verifiedHeader.getText();
  }

  public String getActiveHeader() {
    testWebDriver.waitForElementToAppear(activeHeader);
    return activeHeader.getText();
  }

  public String getName(int rowNumber) {
    WebElement element = testWebDriver.getElementById("name" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(element);
    return element.getText();
  }

  public String getUserName(int rowNumber) {
    WebElement element = testWebDriver.getElementById("userName" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(element);
    return element.getText();
  }

  public String getEmail(int rowNumber) {
    WebElement element = testWebDriver.getElementById("email" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(element);
    return element.getText();
  }

  public boolean getIsVerified(int rowNumber) {
    WebElement element = testWebDriver.getElementById("verifiedIconOk" + (rowNumber - 1));
    return element.isDisplayed();
  }

  public boolean getIsActive(int rowNumber) {
    WebElement element = testWebDriver.getElementById("activeIconOk" + (rowNumber - 1));
    return element.isDisplayed();
  }

  public void clickCrossIcon() {
    testWebDriver.waitForElementToAppear(closeButton);
    closeButton.click();
      testWebDriver.refresh();
  }

  public void clickHomeFacilityField() {
    testWebDriver.waitForElementToAppear(associatedFacilityField);
    associatedFacilityField.click();
  }

  public void clearFacility() {
    testWebDriver.waitForElementToAppear(clearFacility);
    clearFacility.click();
    okButton.click();
  }

  public boolean isHomeFacilityAccordionDisplayed() {
    return homeFacilityRolesAccordion.isDisplayed();
  }

  public boolean isNameHeaderPresent() {
    return nameHeader.isDisplayed();
  }

  public String getSearchUserLabel() {
    testWebDriver.waitForElementToAppear(searchUserLabel);
    return searchUserLabel.getText();
  }

  public String getSearchPlaceHolder() {
    testWebDriver.waitForElementToAppear(searchUserTextField);
    return searchUserTextField.getAttribute("placeholder");
  }
}