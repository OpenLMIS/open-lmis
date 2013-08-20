/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.DBWrapper;
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

  @FindBy(how = How.ID, using = "userName")
  private static WebElement userNameField;

  @FindBy(how = How.ID, using = "email")
  private static WebElement emailField;

  @FindBy(how = How.ID, using = "firstName")
  private static WebElement firstNameField;

  @FindBy(how = How.ID, using = "lastName")
  private static WebElement lastNameField;

  @FindBy(how = How.ID, using = "password1")
  private static WebElement newPasswordField;

  @FindBy(how = How.ID, using = "password2")
  private static WebElement confirmPasswordField;

  @FindBy(how = How.XPATH, using = "//input[@value='Save']")
  private static WebElement saveButton;

  @FindBy(how = How.LINK_TEXT, using = "View Here")
  private static WebElement viewHereLink;

  @FindBy(how = How.LINK_TEXT, using = "Cancel")
  private static WebElement cancelButton;


  @FindBy(how = How.ID, using = "user-add-new")
  private static WebElement addNewButton;

  @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv']/span")
  private static WebElement successMessage;

  @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv']")
  private static WebElement userUpdateSuccessMessage;

  @FindBy(how = How.ID, using = "searchFacility")
  private static WebElement searchFacility;

  @FindBy(how = How.ID, using = "searchUser")
  private static WebElement searchUserTextField;

  @FindBy(how = How.ID, using = "user0")
  private static WebElement firstUserLink;

  @FindBy(how = How.XPATH, using = "//div[@id='changePassword']/div/input[1]")
  private static WebElement resetPasswordDone;

  @FindBy(how = How.XPATH, using = "//div[@id='changePassword']/div/input[2]")
  private static WebElement resetPasswordCancel;

  @FindBy(how = How.XPATH, using = "//a[@ng-click='setSelectedFacility(facility)']")
  private static WebElement selectFacility;

  @FindBy(how = How.XPATH, using = "//a[@ng-click='editUser(user.id)']")
  private static WebElement selectFirstEditUser;

  @FindBy(how = How.XPATH, using = "//ul[@class='user-list']/li/div[@class='user-actions']/a[2]")
  private static WebElement selectFirstResetPassword;

  @FindBy(how = How.XPATH, using = "//select[@ng-model='selectedProgramIdToSupervise']")
  private static WebElement programsToSupervise;

  @FindBy(how = How.XPATH, using = "//select[@ng-model='selectedSupervisoryNodeIdToSupervise']")
  private static WebElement supervisoryNodeToSupervise;

  @FindBy(how = How.XPATH, using = "//select[@name='selectDeliveryZone']")
  private static WebElement deliveryZone;

  @FindBy(how = How.XPATH, using = "//select[@name='selectDeliveryZoneProgram']")
  private static WebElement deliveryZoneProgram;


  @FindBy(how = How.XPATH, using = "//select[@ng-model='programSelected']")
  private static WebElement programsMyFacility;

  @FindBy(how = How.XPATH, using = "//div[@class='select2-result-label']/span")
  private static WebElement rolesSelectFieldMyFacility;

  @FindBy(how = How.XPATH, using = "//div[@id='allocationRole' and @class='fluid-grid-cell']/select[@class='ng-valid select2-offscreen ng-dirty']")
  private static WebElement rolesSelectFieldDeliveryZone;

  @FindBy(how = How.XPATH, using = "//div[@class='select2-result-label']/span")
  private static WebElement rolesSelectField;

  @FindBy(how = How.XPATH, using = "//div[@id='supervisoryRole']/div/ul/li[1]/div")
  private static WebElement rolesListFieldMySupervisedFacility;

  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[12]")
  private static WebElement rolesInputFieldMyFacility;

  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[16]")
  private static WebElement rolesInputFieldMDeliveryZone;

  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[14]")
  private static WebElement rolesInputFieldDeliveryZone;

  @FindBy(how = How.LINK_TEXT, using = "OK")
  private static WebElement okButton;

  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[14]")
  private static WebElement rolesInputField;

  @FindBy(how = How.XPATH, using = "//div[@id='s2id_adminRoles']/ul/li/input")
  private static WebElement adminRolesInputField;


  @FindBy(how = How.XPATH, using = "//div[contains(text(),'Store In-Charge')]")
  private static WebElement storeInChargeOption;

  @FindBy(how = How.XPATH, using = "//div[contains(text(),'Medical-Officer')]")
  private static WebElement medicalOfficerOption;

  @FindBy(how = How.XPATH, using = "//input[@ng-click='addSupervisoryRole()']")
  private static WebElement addButton;

  @FindBy(how = How.XPATH, using = "//input[@ng-click='addAllocationRole()']")
  private static WebElement addAllocationRoleButton;

  @FindBy(how = How.XPATH, using = "//input[@ng-click='addHomeFacilityRole()']")
  private static WebElement addButtonMyFacility;


  @FindBy(how = How.XPATH, using = "//h2[contains(text(),'Edit User')]")
  private static WebElement editUserHeader;


  @FindBy(how = How.XPATH, using = "//input[contains(text(),'Remove')]")
  private static WebElement removeButton;

  @FindBy(how = How.XPATH, using = "//label[@ng-bind='getDeliveryZoneName(roleAssignment.deliveryZone.id)']")
  private static WebElement addedDeliveryZoneLabel;

  @FindBy(how = How.XPATH, using = "//div/div[4]/div/ng-include/div/div[1]/div[2]/div[2]/div/label")
  private static WebElement addedDeliveryZoneProgramLabel;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'No matches found for')]")
  private static WebElement noMatchFoundLink;

  @FindBy(how = How.XPATH, using = "//input[@class='btn btn-danger delete-button']")
  private static WebElement disableButton;

  @FindBy(how = How.XPATH, using = "//input[@class='btn btn-primary enable-button']")
  private static WebElement enableButton;

  public UserPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(1);

  }

  public void searchUser(String user) {
    testWebDriver.waitForElementToAppear(searchUserTextField);
    sendKeys(searchUserTextField, user);
    testWebDriver.sleep(2000);
  }

  public void clickUserList(String userString) {
    testWebDriver.waitForElementToAppear(firstUserLink);
    firstUserLink.click();
    testWebDriver.waitForElementToAppear(userNameField);
  }

  public void focusOnFirstUserLink() {
    testWebDriver.waitForElementToAppear(firstUserLink);
    searchUserTextField.click();
    searchUserTextField.sendKeys(Keys.TAB);

    testWebDriver.waitForElementToAppear(selectFirstEditUser);
  }

  public void clickEditUser() {
    testWebDriver.waitForElementToAppear(selectFirstEditUser);
    selectFirstEditUser.click();
  }

  public void verifyUserOnList(String userString) {
    testWebDriver.waitForElementToAppear(firstUserLink);
    SeleneseTestNgHelper.assertTrue("User not available in list.", firstUserLink.getText().contains(userString));
  }

  public void verifyResetPassword() {
    testWebDriver.waitForElementToAppear(firstUserLink);
    SeleneseTestNgHelper.assertTrue("Reset password link not available.", selectFirstResetPassword.isDisplayed());
  }

  public void verifyDisabledResetPassword() {
    testWebDriver.waitForElementToAppear(firstUserLink);
    assertTrue("Reset password link not disabled.", selectFirstResetPassword.getAttribute("class").contains("disabled"));
  }

  public void resetPassword(String newPassword, String confirmPassword) {
    firstUserLink.sendKeys(Keys.TAB);
    selectFirstEditUser.sendKeys(Keys.TAB);

    selectFirstResetPassword.click();
    testWebDriver.waitForElementToAppear(newPasswordField);
    newPasswordField.sendKeys(newPassword);
    confirmPasswordField.sendKeys(confirmPassword);
    resetPasswordDone.click();
  }

  public String enterAndVerifyUserDetails(String userName, String email, String firstName, String lastName, String baseurl, String dburl) throws IOException, SQLException {
    testWebDriver.waitForElementToAppear(addNewButton);
    addNewButton.click();
    testWebDriver.waitForElementToAppear(userNameField);
    userNameField.clear();
    userNameField.sendKeys(userName);
    emailField.clear();
    emailField.sendKeys(email);
    firstNameField.clear();
    firstNameField.sendKeys(firstName);
    lastNameField.clear();
    lastNameField.sendKeys(lastName);
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
    testWebDriver.sleep(1500);
    testWebDriver.waitForElementToAppear(viewHereLink);

    SeleneseTestNgHelper.assertTrue("User '" + firstName + " " + lastName + "' has been successfully created, password link sent on registered Email address message is not getting displayed", successMessage.isDisplayed());
    viewHereLink.click();

    DBWrapper dbWrapper = new DBWrapper(baseurl, dburl);
    String userID = dbWrapper.getUserID(userName);

    return userID;

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
    testWebDriver.sleep(1000);
  }

  public void verifyNoMatchedFoundMessage() {
    SeleneseTestNgHelper.assertTrue("No match found link should show up", noMatchFoundLink.isDisplayed());
  }

  public void enterMyFacilityAndMySupervisedFacilityData(String firstName, String lastName, String facilityCode, String program1, String node, String role, String roleType) {
    testWebDriver.waitForElementToAppear(searchFacility);
    if (!roleType.equals("ADMIN")) {
      enterUserHomeFacility(facilityCode);
      selectFacility.click();

      testWebDriver.selectByVisibleText(programsMyFacility, program1);
      rolesInputFieldMyFacility.click();
      rolesInputFieldMyFacility.clear();
      rolesInputFieldMyFacility.sendKeys(role);
      testWebDriver.waitForElementToAppear(rolesSelectFieldMyFacility);
      rolesSelectFieldMyFacility.click();
      addButtonMyFacility.click();
      testWebDriver.sleep(1000);
      testWebDriver.selectByVisibleText(programsToSupervise, program1);
      testWebDriver.sleep(1000);
      testWebDriver.selectByVisibleText(supervisoryNodeToSupervise, node);
      testWebDriver.sleep(1000);

      testWebDriver.handleScroll();
      testWebDriver.sleep(500);
      rolesInputField.click();
      rolesInputField.clear();
      rolesInputField.sendKeys(role);
      testWebDriver.waitForElementToAppear(rolesSelectField);
      rolesSelectField.click();

      assertEquals(testWebDriver.getFirstSelectedOption(supervisoryNodeToSupervise).getText(), node);
      assertEquals(testWebDriver.getFirstSelectedOption(programsToSupervise).getText(), program1);
      assertEquals(rolesListFieldMySupervisedFacility.getText().trim().toLowerCase(), role.toLowerCase());

      addButton.click();
      testWebDriver.sleep(1000);
    } else {
      testWebDriver.handleScroll();
      testWebDriver.sleep(500);
      adminRolesInputField.click();
      adminRolesInputField.clear();
      adminRolesInputField.sendKeys(role);
      testWebDriver.waitForElementToAppear(rolesSelectField);
      rolesSelectField.click();
    }

    saveButton.click();
    testWebDriver.sleep(1000);
    SeleneseTestNgHelper.assertTrue("User '" + firstName + " " + lastName + "' has been successfully updated message is not getting displayed", successMessage.isDisplayed());

  }

  public void verifyMessage(String message) {
    testWebDriver.sleep(500);
    assertEquals(userUpdateSuccessMessage.getText(), message);
  }

  public void enterDeliveryZoneData(String deliveryZoneCode, String program, String role) {
    testWebDriver.handleScroll();
    testWebDriver.waitForElementToAppear(deliveryZone);
    testWebDriver.selectByVisibleText(deliveryZone, deliveryZoneCode);
    testWebDriver.sleep(1000);
    testWebDriver.selectByVisibleText(deliveryZoneProgram, program);
    testWebDriver.sleep(1000);
    rolesInputFieldMDeliveryZone.click();
    rolesInputFieldMDeliveryZone.clear();
    rolesInputFieldMDeliveryZone.sendKeys(role);
    rolesInputFieldMDeliveryZone.sendKeys(Keys.RETURN);
    addAllocationRoleButton.click();
    testWebDriver.sleep(1000);
  }

  public void enterDeliveryZoneDataWithoutHomeAndSupervisoryRolesAssigned(String deliveryZoneCode, String program, String role) {
    testWebDriver.handleScroll();
    testWebDriver.waitForElementToAppear(deliveryZone);
    testWebDriver.selectByVisibleText(deliveryZone, deliveryZoneCode);
    testWebDriver.sleep(1000);
    testWebDriver.selectByVisibleText(deliveryZoneProgram, program);
    testWebDriver.sleep(1000);
    rolesInputFieldDeliveryZone.click();
    rolesInputFieldDeliveryZone.clear();
    rolesInputFieldDeliveryZone.sendKeys(role);
    rolesInputFieldDeliveryZone.sendKeys(Keys.RETURN);
    addAllocationRoleButton.click();
    testWebDriver.sleep(1000);
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


  public void verifyRoleNotPresent(String roleName) {
    boolean rolePresent;
    try {
      testWebDriver.sleep(1000);
      WebElement element = testWebDriver.getElementByXpath("//div[contains(text(),'" + roleName + "')]");
      element.click();
      rolePresent = true;
    } catch (ElementNotVisibleException e) {
      rolePresent = false;
    } catch (NoSuchElementException e) {
      rolePresent = false;
    }
    assertFalse(rolePresent);
  }

  public void clickCancelButton() {
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
    testWebDriver.sleep(100);
  }

  public void clickSaveButton() {
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
    testWebDriver.sleep(100);
  }

  public void clickDisableButton() {
    testWebDriver.waitForElementToAppear(disableButton);
    disableButton.click();
    clickOk();
  }

  public void clickEnableButton() {
    testWebDriver.waitForElementToAppear(enableButton);
    enableButton.click();
    clickOk();
  }

  public void verifyRolePresent(String roleName) {
    testWebDriver.sleep(500);
    WebElement roleElement = testWebDriver.getElementByXpath("//div[contains(text(),'" + roleName + "')]");
    SeleneseTestNgHelper.assertTrue(roleName + " should be displayed", roleElement.isDisplayed());
  }

  public String getAllProgramsToSupervise() {
    return programsToSupervise.getText();
  }

  public String getAllProgramsHomeFacility() {
    return programsMyFacility.getText();
  }

  public void clickViewHere() {
    testWebDriver.waitForElementToAppear(viewHereLink);
    viewHereLink.click();
    testWebDriver.waitForElementToAppear(editUserHeader);
  }

  public void clickOk() {
    testWebDriver.waitForElementToAppear(okButton);
    okButton.click();
    testWebDriver.sleep(100);
  }

  public void clickRemoveButton() {
    testWebDriver.waitForElementToAppear(removeButton);
    removeButton.click();
    testWebDriver.sleep(100);
  }

  public void verifyRemoveNotPresent() {
    boolean removePresent;
    try {
      testWebDriver.sleep(1000);
      removeButton.click();
      removePresent = true;
    } catch (ElementNotVisibleException e) {
      removePresent = false;
    } catch (NoSuchElementException e) {
      removePresent = false;
    }
    assertFalse(removePresent);
  }

  public void clickAllRemoveButton() {
    List<WebElement> removeButtons = testWebDriver.getElementsByXpath("//input[@class='btn delete-role']");
    for (WebElement removeButton : removeButtons) {
      removeButton.click();
      clickOk();
      testWebDriver.sleep(100);
    }
  }

  public String getAddedDeliveryZoneLabel() {
    return addedDeliveryZoneLabel.getText();
  }

  public String getAddedDeliveryZoneProgramLabel() {
    return addedDeliveryZoneProgramLabel.getText();
  }
}