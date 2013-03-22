/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.sql.SQLException;


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


  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[14]")
  private static WebElement rolesInputField;


  @FindBy(how = How.XPATH, using = "//div[contains(text(),'Store In-Charge')]")
  private static WebElement storeInChargeOption;

  @FindBy(how = How.XPATH, using = "//div[contains(text(),'Medical-Officer')]")
  private static WebElement medicalOfficerOption;

  @FindBy(how = How.XPATH, using = "//a[@ng-click='addSupervisoryRole()']")
  private static WebElement addButton;

  @FindBy(how = How.XPATH, using = "//a[@ng-click='addHomeFacilityRole()']")
  private static WebElement addButtonMyFacility;


  public UserPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10
    );

  }

  public String enterAndverifyUserDetails(String userName, String email, String firstName, String lastName, String baseurl, String dburl) throws IOException, SQLException {
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

    saveButton.click();
    testWebDriver.waitForElementToAppear(viewHereLink);

    SeleneseTestNgHelper.assertTrue("User '" + firstName + " " + lastName + "' has been successfully created, password link sent on registered Email address message is not getting displayed", successMessage.isDisplayed());
    viewHereLink.click();

    DBWrapper dbWrapper = new DBWrapper(baseurl, dburl);
    String userID = dbWrapper.getUserID(userName);

    return userID;

  }

  public void enterMyFacilityAndMySupervisedFacilityData(String firstName, String lastName, String facilityCode, String program1, String node, String role) {
    testWebDriver.waitForElementToAppear(searchFacility);
    searchFacility.clear();
    searchFacility.sendKeys(facilityCode);
      for (int i=0;i<facilityCode.length();i++){
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

     SeleneseTestNgHelper.assertEquals(testWebDriver.getFirstSelectedOption(supervisoryNodeToSupervise).getText(),node);
     SeleneseTestNgHelper.assertEquals(testWebDriver.getFirstSelectedOption(programsToSupervise).getText(),program1);
     SeleneseTestNgHelper.assertEquals(rolesListFieldMySupervisedFacility.getText().trim().toLowerCase(),role.toLowerCase());

      addButton.click();
      testWebDriver.sleep(1000);

      saveButton.click();
      testWebDriver.sleep(1000);
      SeleneseTestNgHelper.assertTrue("User '" + firstName + " " + lastName + "' has been successfully updated message is not getting displayed", successMessage.isDisplayed());

  }


}