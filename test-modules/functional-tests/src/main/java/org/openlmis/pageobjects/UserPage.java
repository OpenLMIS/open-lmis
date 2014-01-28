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


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;

public class UserPage extends Page {

  @FindBy(how = How.ID, using = "user-add-new")
  private static WebElement addNewButton = null;

  @FindBy(how = How.ID, using = "userName")
  private static WebElement userNameField = null;

  @FindBy(how = How.ID, using = "email")
  private static WebElement emailField = null;

  @FindBy(how = How.ID, using = "firstName")
  private static WebElement firstNameField = null;

  @FindBy(how = How.ID, using = "lastName")
  private static WebElement lastNameField = null;

  @FindBy(how = How.XPATH, using = "//input[1][@class='btn btn-primary save-button']")
  private static WebElement saveButton = null;

  @FindBy(how = How.XPATH, using = "//input[2][@class='btn btn-cancel cancel-button']")
  private static WebElement cancelButton = null;

  @FindBy(how = How.XPATH, using = "//input[@class='btn btn-danger delete-button']")
  private static WebElement disableButton = null;

  @FindBy(how = How.XPATH, using = "//input[@class='btn btn-primary enable-button']")
  private static WebElement enableButton = null;

  @FindBy(how = How.ID, using = "searchUser")
  private static WebElement searchUserTextField = null;

  @FindBy(how = How.XPATH, using = "//ul[@id='userList']/li/div[@class='user-actions']/a[2]")
  private static WebElement selectFirstResetPassword = null;

  @FindBy(how = How.ID, using = "password1")
  private static WebElement newPasswordField = null;

  @FindBy(how = How.ID, using = "password2")
  private static WebElement confirmPasswordField = null;

  @FindBy(how = How.XPATH, using = "//div[@id='changePassword']/div/input[1]")
  private static WebElement resetPasswordDone = null;

  @FindBy(how = How.LINK_TEXT, using = "OK")
  private static WebElement okButton = null;

  @FindBy(how = How.ID, using = "user0")
  private static WebElement firstUserLink = null;

  @FindBy(how = How.XPATH, using = "//a[@ng-click='editUser(user.id)']")
  private static WebElement selectFirstEditUser = null;

  @FindBy(how = How.XPATH, using = "//h2[contains(text(),'Edit User')]")
  private static WebElement editUserHeader = null;

  @FindBy(how = How.ID, using = "searchFacility")
  private static WebElement searchFacility = null;

  @FindBy(how = How.ID, using = "result0")
  private static WebElement selectFacility = null;

  @FindBy(how = How.XPATH, using = "//form[@id='create-user']/div/div[1]/div[7]/div/ng-switch/span")
  private static WebElement verifiedLabel = null;

  @FindBy(how = How.XPATH, using = "//div[1][@class='pull-right control-accordion']/a[1]")
  private static WebElement expandAllOption = null;

  @FindBy(how = How.XPATH, using = "//div[1][@class='pull-right control-accordion']/a[2]")
  private static WebElement collapseAllOption = null;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Home Facility Roles')]")
  private static WebElement homeFacilityRolesAccordion = null;

  @FindBy(how = How.XPATH, using = "//select[@ng-model='programSelected']")
  private static WebElement homeFacilityPrograms = null;

  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[12]")
  private static WebElement roleInputFieldHomeFacility = null;

  @FindBy(how = How.XPATH, using = "//div[@class='select2-result-label']/span")
  private static WebElement rolesSelectFieldHomeFacility = null;

  @FindBy(how = How.XPATH, using = "//input[@ng-click='addHomeFacilityRole()']")
  private static WebElement addHomeFacilityRolesButton = null;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Supervisory Roles')]")
  private static WebElement supervisoryRolesAccordion = null;

  @FindBy(how = How.XPATH, using = "//select[@ng-model='selectedProgramIdToSupervise']")
  private static WebElement programsToSupervise = null;

  @FindBy(how = How.XPATH, using = "//select[@ng-model='selectedSupervisoryNodeIdToSupervise']")
  private static WebElement supervisoryNodeToSupervise = null;

  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[14]")
  private static WebElement rolesInputFieldSupervisoryRole = null;

  @FindBy(how = How.XPATH, using = "//div[@class='select2-result-label']/span")
  private static WebElement rolesSelectFieldSupervisoryRole = null;

  @FindBy(how = How.XPATH, using = "//input[@ng-click='addSupervisoryRole()']")
  private static WebElement addSupervisoryRoleButton = null;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Warehouse Roles')]")
  private static WebElement warehouseRolesAccordion = null;

  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[16]")
  private static WebElement rolesInputFieldWarehouse = null;

  @FindBy(how = How.XPATH, using = "//div[@class='select2-result-label']/span")
  private static WebElement rolesSelectFieldWarehouse = null;

  @FindBy(how = How.XPATH, using = "//select[@ng-model='warehouseRole.facilityId']")
  private static WebElement warehouseToSelect = null;

  @FindBy(how = How.XPATH, using = "//input[@ng-click='addFulfillmentRole()']")
  private static WebElement addWarehouseRoleButton = null;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Delivery zones')]")
  private static WebElement deliveryZonesAccordion = null;

  @FindBy(how = How.XPATH, using = "//select[@name='selectDeliveryZone']")
  private static WebElement zoneToDelivery = null;

  @FindBy(how = How.XPATH, using = "//select[@name='selectDeliveryZoneProgram']")
  private static WebElement programToDeliver = null;

  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[18]")
  private static WebElement rolesInputFieldMDeliveryZone = null;

  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[15]")
  private static WebElement rolesInputFieldDeliveryZone = null;

  @FindBy(how = How.XPATH, using = "//input[@ng-click='addAllocationRole()']")
  private static WebElement addDeliveryZoneRoleButton = null;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Admin and General Operations Roles')]")
  private static WebElement adminAndGeneralOperationsRolesAccordion = null;

  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[17]")
  private static WebElement adminRolesInputField = null;


  @FindBy(how = How.LINK_TEXT, using = "View Here")
  private static WebElement viewHereLink = null;

  @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv']")
  private static WebElement userUpdateSuccessMessage = null;

  @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv']")
  private static WebElement successMessage = null;

  @FindBy(how = How.XPATH, using = "//input[contains(text(),'Remove')]")
  private static WebElement removeButton = null;

  @FindBy(how = How.XPATH, using = "//div[6]/div[2]/ng-include/div/div[1]/div[2]/div[1]/div/label[@class='ng-binding']")
  private static WebElement addedDeliveryZoneLabel = null;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'No matches found for')]")
  private static WebElement noMatchFoundLink = null;

  @FindBy(how = How.XPATH, using = "//div[@class='form-field radio-group']/input[1]")
  private static WebElement restrictLoginYesOption = null;

  @FindBy(how = How.XPATH, using = "//div[@class='form-field radio-group']/input[2]")
  private static WebElement restrictLoginNoOption = null;

  @FindBy(how = How.ID, using = "resetPasswordOk")
  private static WebElement resetPasswordOkButton = null;

  public UserPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(1);

  }

  public void searchUser(String user) {
    testWebDriver.waitForElementToAppear(searchUserTextField);
    sendKeys(searchUserTextField, user);
  }

  public void clickUserList() {
    testWebDriver.waitForElementToAppear(firstUserLink);
    firstUserLink.click();
    testWebDriver.waitForElementToAppear(userNameField);
  }

  public void focusOnFirstUserLink() {
    testWebDriver.waitForElementToAppear(firstUserLink);
    testWebDriver.moveToElement(firstUserLink);
    testWebDriver.waitForElementToAppear(selectFirstEditUser);
  }

  public void clickEditUser() {
    testWebDriver.waitForElementToAppear(selectFirstEditUser);
    selectFirstEditUser.click();
  }

  public void verifyUserOnList(String userString) {
    testWebDriver.waitForElementToAppear(firstUserLink);
    assertTrue("User not available in list.", firstUserLink.getText().contains(userString));
  }

  public void verifyDisabledResetPassword() {
    testWebDriver.waitForElementToAppear(firstUserLink);
    assertTrue("Reset password link not disabled.", selectFirstResetPassword.getAttribute("class").contains("disabled"));
  }

  public void resetPassword(String newPassword, String confirmPassword) {
    firstUserLink.sendKeys(Keys.TAB);
    selectFirstEditUser.sendKeys(Keys.TAB);
    testWebDriver.waitForElementToBeEnabled(selectFirstResetPassword);
    selectFirstResetPassword.click();
    testWebDriver.waitForElementToAppear(newPasswordField);
    newPasswordField.sendKeys(newPassword);
    confirmPasswordField.sendKeys(confirmPassword);
    testWebDriver.waitForElementToBeEnabled(resetPasswordDone);
    resetPasswordDone.click();
    testWebDriver.waitForElementToBeEnabled(resetPasswordOkButton);
    resetPasswordOkButton.click();
  }

  public void enterUserDetails(String userName, String email, String firstName, String lastName)
    throws IOException, SQLException {
    testWebDriver.waitForElementToAppear(addNewButton);
    addNewButton.click();
    testWebDriver.waitForElementToAppear(userNameField);
    userNameField.clear();
    userNameField.sendKeys(userName);
    testWebDriver.waitForElementToAppear(emailField);
    emailField.clear();
    emailField.sendKeys(email);
    testWebDriver.waitForElementToAppear(firstNameField);
    firstNameField.clear();
    firstNameField.sendKeys(firstName);
    testWebDriver.waitForElementToAppear(lastNameField);
    lastNameField.clear();
    lastNameField.sendKeys(lastName);
    testWebDriver.handleScroll();
    clickRestrictLoginNo();
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
    testWebDriver.waitForElementToAppear(viewHereLink);
  }

  public void verifyUserCreated(String firstName, String lastName)
    throws IOException, SQLException {
    testWebDriver.waitForElementToAppear(successMessage);

    String expectedMessage = String.format("User \"%s %s\" has been successfully created," +
      " password link has been sent on registered Email address. View Here", firstName, lastName);
    assertEquals(expectedMessage, successMessage.getText());
  }

  public void enterUserHomeFacility(String facilityCode) {
    searchFacility.clear();
    testWebDriver.handleScrollByPixels(0, 5000);
    searchFacility.sendKeys(facilityCode);
    for (int i = 0; i < facilityCode.length(); i++) {
      searchFacility.sendKeys(Keys.ARROW_LEFT);
      searchFacility.sendKeys(Keys.DELETE);
    }
    searchFacility.sendKeys(facilityCode);
  }

  public void verifyNoMatchedFoundMessage() {
    SeleneseTestNgHelper.assertTrue("No match found link should show up", noMatchFoundLink.isDisplayed());
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

  public void verifyExpandAll() {
    assertTrue(programsToSupervise.isDisplayed());
    assertTrue(programToDeliver.isDisplayed());
  }

  public void verifyCollapseAll() {
    assertFalse(programsToSupervise.isDisplayed());
    assertFalse(programToDeliver.isDisplayed());
    assertFalse(warehouseToSelect.isDisplayed());
  }

  public void enterMyFacilityAndMySupervisedFacilityData(String facilityCode, String program1, String node, String role, String roleType) {
    testWebDriver.waitForElementToAppear(searchFacility);
    if (!roleType.equals("ADMIN")) {
      enterUserHomeFacility(facilityCode);
      testWebDriver.waitForElementToAppear(selectFacility);
      selectFacility.click();
      testWebDriver.waitForAjax();
      homeFacilityRolesAccordion.click();
      testWebDriver.selectByVisibleText(homeFacilityPrograms, program1);
      testWebDriver.waitForElementToAppear(roleInputFieldHomeFacility);
      roleInputFieldHomeFacility.click();
      roleInputFieldHomeFacility.clear();
      roleInputFieldHomeFacility.sendKeys(role);
      testWebDriver.waitForElementToAppear(rolesSelectFieldHomeFacility);
      rolesSelectFieldHomeFacility.click();
      addHomeFacilityRolesButton.click();
      testWebDriver.waitForElementToAppear(supervisoryRolesAccordion);
      supervisoryRolesAccordion.click();
      testWebDriver.waitForElementToAppear(programsToSupervise);
      testWebDriver.selectByVisibleText(programsToSupervise, program1);
      testWebDriver.waitForElementToAppear(supervisoryNodeToSupervise);
      testWebDriver.selectByVisibleText(supervisoryNodeToSupervise, node);
      testWebDriver.sleep(1000);
      testWebDriver.handleScroll();
      testWebDriver.waitForElementToAppear(rolesInputFieldSupervisoryRole);
      rolesInputFieldSupervisoryRole.click();
      rolesInputFieldSupervisoryRole.clear();
      rolesInputFieldSupervisoryRole.sendKeys(role);
      testWebDriver.waitForElementToAppear(rolesSelectFieldSupervisoryRole);
      rolesSelectFieldSupervisoryRole.click();

      assertEquals(testWebDriver.getFirstSelectedOption(supervisoryNodeToSupervise).getText(), node);
      assertEquals(testWebDriver.getFirstSelectedOption(programsToSupervise).getText(), program1);
      testWebDriver.waitForElementToAppear(addSupervisoryRoleButton);
      addSupervisoryRoleButton.click();

    } else {
      testWebDriver.handleScroll();
      testWebDriver.waitForElementToAppear(adminAndGeneralOperationsRolesAccordion);
      adminAndGeneralOperationsRolesAccordion.click();
      testWebDriver.waitForElementToAppear(adminRolesInputField);
      adminRolesInputField.click();
      adminRolesInputField.clear();
      adminRolesInputField.sendKeys(role);
      testWebDriver.waitForElementToAppear(rolesSelectFieldSupervisoryRole);
      rolesSelectFieldSupervisoryRole.click();
    }
  }

  public void verifyUserUpdated(String firstName, String lastName) {
    testWebDriver.sleep(1000);
    assertTrue("User '" + firstName + " " + lastName + "' has been successfully updated message is not getting displayed",
      successMessage.isDisplayed());
  }

  public void assignWarehouse(String warehouse, String role) {
    testWebDriver.waitForElementToAppear(warehouseRolesAccordion);
    warehouseRolesAccordion.click();
    testWebDriver.waitForElementToAppear(warehouseToSelect);
    testWebDriver.selectByVisibleText(warehouseToSelect, warehouse);
    testWebDriver.waitForElementToAppear(rolesInputFieldWarehouse);
    rolesInputFieldWarehouse.click();
    rolesInputFieldWarehouse.clear();
    rolesInputFieldWarehouse.sendKeys(role);
    testWebDriver.waitForElementToAppear(rolesSelectFieldWarehouse);
    rolesSelectFieldSupervisoryRole.click();
    assertEquals(testWebDriver.getFirstSelectedOption(warehouseToSelect).getText(), warehouse);
    testWebDriver.waitForElementToAppear(addWarehouseRoleButton);
    addWarehouseRoleButton.click();
    testWebDriver.sleep(1000);
    verifyWarehouseSelectedNotAvailable(warehouse);
    testWebDriver.waitForElementToAppear(warehouseRolesAccordion);
    warehouseRolesAccordion.click();
  }

  public void verifyMessage(String message) {
    testWebDriver.waitForElementToAppear(userUpdateSuccessMessage);
    assertEquals(userUpdateSuccessMessage.getText(), message);
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

  public void clickDisableButton() {
    testWebDriver.waitForElementToBeEnabled(disableButton);
    disableButton.click();
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
    testWebDriver.waitForElementToAppear(warehouseRolesAccordion);
    warehouseRolesAccordion.click();
  }

  public void clickHomeFacilityRolesAccordion() {
    testWebDriver.waitForElementToAppear(homeFacilityRolesAccordion);
    homeFacilityRolesAccordion.click();
  }

  public void clickSupervisoryRolesAccordion() {
    testWebDriver.waitForElementToAppear(supervisoryRolesAccordion);
    supervisoryRolesAccordion.click();
  }

  public void clickDeliveryZonesAccordion() {
    testWebDriver.waitForElementToAppear(deliveryZonesAccordion);
    deliveryZonesAccordion.click();
  }

  private void verifyWarehouseSelectedNotAvailable(String warehouse1) {
    assertFalse(getAllWarehouseToSelect().contains(warehouse1));
  }

  public void clickEditUserButton() {
    focusOnFirstUserLink();
    testWebDriver.waitForElementToAppear(selectFirstEditUser);
    selectFirstEditUser.click();
  }
}