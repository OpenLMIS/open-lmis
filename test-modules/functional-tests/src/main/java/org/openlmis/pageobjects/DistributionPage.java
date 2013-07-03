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

  @FindBy(how = ID, using = "viewLoadAmounts")
  private static WebElement viewLoadAmountButton;


  @FindBy(how = XPATH, using = "//a[contains(text(),'Input facility data')]")
  private static WebElement inputFacilityDataLink;


  public DistributionPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);

  }

  public void selectValueFromDeliveryZone(String valueToBeSelected) {
    testWebDriver.waitForElementToAppear(getSelectDeliveryZoneSelectBox());
    testWebDriver.selectByVisibleText(getSelectDeliveryZoneSelectBox(), valueToBeSelected);
  }

  public void selectValueFromProgram(String valueToBeSelected) {
    testWebDriver.waitForElementToAppear(getSelectProgramSelectBox());
    testWebDriver.selectByVisibleText(getSelectProgramSelectBox(), valueToBeSelected);
  }

  public void selectValueFromPeriod(String valueToBeSelected) {
    testWebDriver.waitForElementToAppear(getSelectPeriodSelectBox());
    testWebDriver.selectByVisibleText(getSelectPeriodSelectBox(), valueToBeSelected);
  }

  public void clickViewLoadAmount() {
    testWebDriver.waitForElementToAppear(getViewLoadAmountButton());
    getViewLoadAmountButton().click();

  }

  public List<WebElement> getAllSelectOptionsFromDeliveryZone() {
    testWebDriver.waitForElementToAppear(getSelectDeliveryZoneSelectBox());
    List<WebElement> options = testWebDriver.getOptions(getSelectDeliveryZoneSelectBox());
    return options;
  }

  public List<WebElement> getAllSelectOptionsFromProgram() {
    testWebDriver.waitForElementToAppear(getSelectProgramSelectBox());
    List<WebElement> options = testWebDriver.getOptions(getSelectProgramSelectBox());
    return options;
  }

  public List<WebElement> getAllSelectOptionsFromPeriod() {
    testWebDriver.waitForElementToAppear(getSelectPeriodSelectBox());
    List<WebElement> options = testWebDriver.getOptions(getSelectPeriodSelectBox());
    return options;
  }

  public WebElement getFirstSelectedOptionFromDeliveryZone() {
    testWebDriver.waitForElementToAppear(getSelectDeliveryZoneSelectBox());
    WebElement option = testWebDriver.getFirstSelectedOption(getSelectDeliveryZoneSelectBox());
    return option;
  }

  public WebElement getFirstSelectedOptionFromProgram() {
    testWebDriver.waitForElementToAppear(getSelectProgramSelectBox());
    WebElement option = testWebDriver.getFirstSelectedOption(getSelectProgramSelectBox());
    return option;
  }

  public WebElement getFirstSelectedOptionFromPeriod() {
    testWebDriver.waitForElementToAppear(getSelectPeriodSelectBox());
    testWebDriver.sleep(100);
    WebElement option = testWebDriver.getFirstSelectedOption(getSelectPeriodSelectBox());
    return option;
  }

  public WebElement getSelectDeliveryZoneSelectBox() {
    return selectDeliveryZoneSelectBox;
  }

  public WebElement getSelectProgramSelectBox() {
    return selectProgramSelectBox;
  }


  public WebElement getSelectPeriodSelectBox() {
    return selectPeriodSelectBox;
  }


  public WebElement getViewLoadAmountButton() {
    return viewLoadAmountButton;
  }


  public static WebElement getInputFacilityDataLink() {
    return inputFacilityDataLink;
  }


}