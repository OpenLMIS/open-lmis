/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.util.List;

import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;


public class DistributionPage extends RequisitionPage {

  @FindBy(how = ID, using = "selectDeliveryZone")
  private static WebElement selectDeliveryZoneSelectBox;

  @FindBy(how = ID, using = "selectProgram")
  private static WebElement selectProgramSelectBox;

  @FindBy(how = ID, using = "selectPeriod")

  private static WebElement selectPeriodSelectBox;

  @FindBy(how = XPATH, using = "//input[@value='View load amounts']")
  private static WebElement viewLoadAmountButton;

  @FindBy(how = ID, using = "initiateDistribution")
  private static WebElement initiateDistributionButton;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Input facility data')]")
  private static WebElement inputFacilityDataLink;


  public DistributionPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);

  }

  public void selectValueFromDeliveryZone(String valueToBeSelected) {
    testWebDriver.waitForElementToAppear(selectDeliveryZoneSelectBox);
    testWebDriver.selectByVisibleText(selectDeliveryZoneSelectBox, valueToBeSelected);
  }

  public void selectValueFromProgram(String valueToBeSelected) {
    testWebDriver.waitForElementToAppear(selectProgramSelectBox);
    testWebDriver.selectByVisibleText(selectProgramSelectBox, valueToBeSelected);
  }

  public void selectValueFromPeriod(String valueToBeSelected) {
    testWebDriver.waitForElementToAppear(selectPeriodSelectBox);
    testWebDriver.selectByVisibleText(selectPeriodSelectBox, valueToBeSelected);
  }

  public void clickViewLoadAmount() {
    testWebDriver.waitForElementToAppear(viewLoadAmountButton);
    viewLoadAmountButton.click();
    testWebDriver.sleep(1000);

  }

  public void clickInitiateDistribution() {
    testWebDriver.waitForElementToAppear(initiateDistributionButton);
    initiateDistributionButton.click();

  }

  public List<WebElement> getAllSelectOptionsFromDeliveryZone() {
    testWebDriver.waitForElementToAppear(selectDeliveryZoneSelectBox);
    List<WebElement> options = testWebDriver.getOptions(selectDeliveryZoneSelectBox);
    return options;
  }

  public List<WebElement> getAllSelectOptionsFromProgram() {
    testWebDriver.waitForElementToAppear(selectProgramSelectBox);
    List<WebElement> options = testWebDriver.getOptions(selectProgramSelectBox);
    return options;
  }

  public List<WebElement> getAllSelectOptionsFromPeriod() {
    testWebDriver.waitForElementToAppear(selectPeriodSelectBox);
    List<WebElement> options = testWebDriver.getOptions(selectPeriodSelectBox);
    return options;
  }

  public WebElement getFirstSelectedOptionFromDeliveryZone() {
    testWebDriver.waitForElementToAppear(selectDeliveryZoneSelectBox);
    WebElement option = testWebDriver.getFirstSelectedOption(selectDeliveryZoneSelectBox);
    return option;
  }

  public WebElement getFirstSelectedOptionFromProgram() {
    testWebDriver.waitForElementToAppear(selectProgramSelectBox);
    WebElement option = testWebDriver.getFirstSelectedOption(selectProgramSelectBox);
    return option;
  }

  public WebElement getFirstSelectedOptionFromPeriod() {
    testWebDriver.waitForElementToAppear(selectPeriodSelectBox);
    testWebDriver.sleep(100);
    WebElement option = testWebDriver.getFirstSelectedOption(selectPeriodSelectBox);
    return option;
  }

  public boolean IsDisplayedSelectDeliveryZoneSelectBox() {
    return selectDeliveryZoneSelectBox.isDisplayed();
  }

  public boolean IsDisplayedSelectProgramSelectBox() {
    return selectProgramSelectBox.isDisplayed();
  }


  public boolean IsDisplayedSelectPeriodSelectBox() {
    return selectPeriodSelectBox.isDisplayed();
  }


  public boolean IsDisplayedViewLoadAmountButton() {
    return viewLoadAmountButton.isDisplayed();
  }


}