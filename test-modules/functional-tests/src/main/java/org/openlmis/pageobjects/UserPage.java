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


public class UserPage extends Page {

  @FindBy(how = How.ID, using = "userName")
  private static WebElement userNameField;

  @FindBy(how = How.ID, using = "email")
  private static WebElement emailField;

  @FindBy(how = How.ID, using = "firstName")
  private static WebElement firstNameField;

  @FindBy(how = How.ID, using = "lastName")
  private static WebElement lastNameField;

  @FindBy(how = How.XPATH, using = "//input[@value='Save']")
  private static WebElement saveButton;

  @FindBy(how = How.LINK_TEXT, using = "View Here")
  private static WebElement viewHereLink;

  @FindBy(how = How.LINK_TEXT, using = "Cancel")
  private static WebElement cancelButton;


  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Add new')]")
  private static WebElement addNewButton;

  @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv']/span")
  private static WebElement successMessage;

  @FindBy(how = How.ID, using = "searchFacility")
  private static WebElement searchFacility;

  @FindBy(how = How.XPATH, using = "//a[@ng-click='setSelectedFacility(facility)']")
  private static WebElement selectFacility;

  @FindBy(how = How.XPATH, using = "//select[@ng-model='selectedProgramIdToSupervise']")
  private static WebElement programsToSupervise;

  @FindBy(how = How.XPATH, using = "//select[@ng-model='selectedSupervisoryNodeIdToSupervise']")
  private static WebElement supervisoryNodeToSupervise;

  @FindBy(how = How.XPATH, using = "//select[@ng-model='programSelected']")
  private static WebElement programsMyFacility;


  @FindBy(how = How.XPATH, using = "//div[@class='select2-result-label']/span")
  private static WebElement rolesSelectFieldMyFacility;

  @FindBy(how = How.XPATH, using = "//div[@class='select2-result-label']/span")
  private static WebElement rolesSelectField;

  @FindBy(how = How.XPATH, using = "//div[@id='supervisoryRole']/div/ul/li[1]/div")
  private static WebElement rolesListFieldMySupervisedFacility;

  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[12]")
  private static WebElement rolesInputFieldMyFacility;

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

  @FindBy(how = How.XPATH, using = "//a[@ng-click='addSupervisoryRole()']")
  private static WebElement addButton;

  @FindBy(how = How.XPATH, using = "//a[@ng-click='addHomeFacilityRole()']")
  private static WebElement addButtonMyFacility;


  @FindBy(how = How.XPATH, using = "//h2[contains(text(),'Edit User')]")
  private static WebElement editUserHeader;


  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Remove')]")
  private static WebElement removeButton;


  public UserPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(1);

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
    System.out.println("brute force" + "14.2.1");
    testWebDriver.waitForElementToAppear(saveButton);
    testWebDriver.sleep(1500);
    saveButton.click();
    testWebDriver.sleep(1500);
    System.out.println("brute force" + "14.2.2");
    testWebDriver.waitForElementToAppear(viewHereLink);

    SeleneseTestNgHelper.assertTrue("User '" + firstName + " " + lastName + "' has been successfully created, password link sent on registered Email address message is not getting displayed", successMessage.isDisplayed());
    viewHereLink.click();

    DBWrapper dbWrapper = new DBWrapper(baseurl, dburl);
    String userID = dbWrapper.getUserID(userName);

    return userID;

  }

  public void enterMyFacilityAndMySupervisedFacilityData(String firstName, String lastName, String facilityCode, String program1, String node, String role, boolean adminRole) {
    testWebDriver.waitForElementToAppear(searchFacility);
    if (!adminRole) {
      searchFacility.clear();
      testWebDriver.handleScrollByPixels(0, 4000);
      searchFacility.sendKeys(facilityCode);
      for (int i = 0; i < facilityCode.length(); i++) {
        searchFacility.sendKeys(Keys.ARROW_LEFT);
        searchFacility.sendKeys(Keys.DELETE);
      }
      searchFacility.sendKeys(facilityCode);
      testWebDriver.sleep(1000);
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

      SeleneseTestNgHelper.assertEquals(testWebDriver.getFirstSelectedOption(supervisoryNodeToSupervise).getText(), node);
      SeleneseTestNgHelper.assertEquals(testWebDriver.getFirstSelectedOption(programsToSupervise).getText(), program1);
      SeleneseTestNgHelper.assertEquals(rolesListFieldMySupervisedFacility.getText().trim().toLowerCase(), role.toLowerCase());

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

  public void verifyRolePresent(String roleName) {
    testWebDriver.sleep(500);
    WebElement roleElement = testWebDriver.getElementByXpath("//div[contains(text(),'" + roleName + "')]");
    SeleneseTestNgHelper.assertTrue(roleName + " should be displayed", roleElement.isDisplayed());
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
    List<WebElement> removeButtons = testWebDriver.getElementsByXpath("//a[contains(text(),'Remove')]");
    for (WebElement removeButton : removeButtons) {
      removeButton.click();
      clickOk();
      testWebDriver.sleep(100);
    }
  }


}