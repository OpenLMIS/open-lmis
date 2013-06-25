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
import org.openqa.selenium.support.ui.Select;

import java.io.IOException;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;


public class RegimenTemplateConfigPage extends Page {


  @FindBy(how = How.LINK_TEXT, using = "Logout")
  private static WebElement logoutLink;

  @FindBy(how = How.XPATH, using = "//input[@value='Save']")
  private static WebElement SaveButton;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Cancel')]")
  private static WebElement CancelButton;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'(Change)')]")
  private static WebElement changeLink;

  @FindBy(how = How.ID, using = "//div[@id='saveSuccessMsgDiv' and @ng-show='message']")
  private static WebElement saveSuccessMsgDiv;

  @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv' and @ng-show='error']")
  private static WebElement saveErrorMsgDiv;

  @FindBy(how = How.ID, using = "new-regimen-category")
  private static WebElement newRegimenCategoryDropDown;

  @FindBy(how = How.ID, using = "new-regimen-code")
  private static WebElement newRegimenCodeTextBox;

  @FindBy(how = How.ID, using = "new-regimen-name")
  private static WebElement newRegimenNameTextBox;

  @FindBy(how = How.ID, using = "new-regimen-active")
  private static WebElement newRegimenActiveCheckBox;

  @FindBy(how = How.XPATH, using = "//input[@value='Add']")
  private static WebElement addButton;

  @FindBy(how = How.XPATH, using = "//input[@value='Edit']")
  private static WebElement editButton;

  private static String TEMPLATE_SUCCESS_MESSAGE = "Template saved successfully!";

  public RegimenTemplateConfigPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public void AddNewRegimen(String category, String code, String name, Boolean isActive) {
    testWebDriver.waitForElementsToAppear(newRegimenCategoryDropDown, newRegimenCodeTextBox);
    testWebDriver.selectByVisibleText(newRegimenCategoryDropDown, category);
    newRegimenCodeTextBox.sendKeys(code);
    newRegimenNameTextBox.sendKeys(name);
    if (isActive)
      newRegimenActiveCheckBox.click();
    addButton.click();
  }

  public WebElement getLogoutLink() {
    return logoutLink;
  }

  public WebElement getSaveButton() {
    return SaveButton;
  }

  public WebElement getCancelButton() {
    return CancelButton;
  }

  public WebElement getChangeLink() {
    return changeLink;
  }

  public WebElement getSaveSuccessMsgDiv() {
    return saveSuccessMsgDiv;
  }

  public WebElement getSaveErrorMsgDiv() {
    return saveErrorMsgDiv;
  }

  public static WebElement getNewRegimenCategoryDropDown() {
    return newRegimenCategoryDropDown;
  }

  public WebElement getNewRegimenCodeTextBox() {
    return newRegimenCodeTextBox;
  }

  public WebElement getNewRegimenNameTextBox() {
    return newRegimenNameTextBox;
  }

  public WebElement getNewRegimenActiveCheckBox() {
    return newRegimenActiveCheckBox;
  }

  public WebElement getAddButton() {
    return addButton;
  }

  public WebElement getEditButton() {
    return editButton;
  }

  public String getTEMPLATE_SUCCESS_MESSAGE() {
    return TEMPLATE_SUCCESS_MESSAGE;
  }

  public void configureProgram(String program) {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//a[@id='" + program + "']/span"));
    testWebDriver.getElementByXpath("//a[@id='" + program + "']/span").click();
    testWebDriver.waitForElementToAppear(SaveButton);
  }

  public void SaveRegime() {
    SaveButton.click();
  }

  public void CancelRegime() {
    CancelButton.click();
  }

}