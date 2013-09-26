/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;


public class DistributionPage extends Page {

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

  @FindBy(how = XPATH, using = "//a[contains(text(),'Record Data')]")
  private static WebElement recordDataButton;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Input facility data')]")
  private static WebElement inputFacilityDataLink;

  @FindBy(how = XPATH, using = "//div[@id='saveSuccessMsgDiv']")
  private static WebElement saveSuccessMessageDiv;

  @FindBy(how = ID, using = "indicator0")
  private static WebElement distributionIndicator;


  public DistributionPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(1);

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
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(initiateDistributionButton);
    initiateDistributionButton.click();
     testWebDriver.sleep(200);
  }

  public FacilityListPage clickRecordData() throws IOException {
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(recordDataButton);
    recordDataButton.click();
    testWebDriver.sleep(250);
    return new FacilityListPage(testWebDriver);
  }

  public void verifyDownloadSuccessFullMessage(String deliveryZone, String program, String period)
  {
    testWebDriver.sleep(200);
    SeleneseTestNgHelper.assertTrue("Data download successful message should show up",saveSuccessMessageDiv.getText().equals("Data for the selected "+deliveryZone+", "+program+", "+period+" has been downloaded"));

  }

  public void verifyDataAlreadyCachedMessage(String deliveryZone, String program, String period)
  {
    testWebDriver.sleep(200);
    SeleneseTestNgHelper.assertTrue("Data already cached  message should show up",saveSuccessMessageDiv.getText().equals("The data for the selected "+deliveryZone+", "+program+", "+period+" is already cached"));

  }

  public void verifyFacilityNotSupportedMessage(String programFirst, String deliveryZoneNameFirst)
  {
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(saveSuccessMessageDiv);
    String message="Program ''"+programFirst+"'' is not supported by any facility in delivery zone ''"+deliveryZoneNameFirst+"''";
    SeleneseTestNgHelper.assertEquals(message,saveSuccessMessageDiv.getText());
  }


  public List<WebElement> getAllSelectOptionsFromDeliveryZone() {
    testWebDriver.waitForElementToAppear(selectDeliveryZoneSelectBox);
    List<WebElement> options = testWebDriver.getOptions(selectDeliveryZoneSelectBox);
    return options;
  }

  public List<WebElement> getAllSelectOptionsFromProgram() {
    testWebDriver.sleep(500);
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
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(selectDeliveryZoneSelectBox);
    WebElement option = testWebDriver.getFirstSelectedOption(selectDeliveryZoneSelectBox);
    return option;
  }

  public WebElement getFirstSelectedOptionFromProgram() {
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(selectProgramSelectBox);
    WebElement option = testWebDriver.getFirstSelectedOption(selectProgramSelectBox);
    return option;
  }

  public WebElement getFirstSelectedOptionFromPeriod() {
    testWebDriver.sleep(500);
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

  public boolean verifyDeliveryZoneSelectBoxNotPresent() {
        boolean deliveryZoneSelectBoxPresent = false;
        try {
            deliveryZoneSelectBoxPresent=selectDeliveryZoneSelectBox.isDisplayed();
        } catch (NoSuchElementException e) {
            deliveryZoneSelectBoxPresent = false;
        } finally {
            return deliveryZoneSelectBoxPresent;
        }
    }

    public boolean verifyProgramSelectBoxNotPresent() {
        boolean programSelectBoxPresent = false;
        try {
            programSelectBoxPresent=selectProgramSelectBox.isDisplayed();
        } catch (NoSuchElementException e) {
            programSelectBoxPresent = false;
        } finally {
            return programSelectBoxPresent;
        }
    }

    public boolean verifyPeriodSelectBoxNotPresent() {
        boolean periodSelectBoxPresent = false;
        try {
            periodSelectBoxPresent=selectPeriodSelectBox.isDisplayed();
        } catch (NoSuchElementException e) {
            periodSelectBoxPresent = false;
        } finally {
            return periodSelectBoxPresent;
        }
    }

  public void verifyDistributionColor(String color) {
    testWebDriver.waitForElementToAppear(distributionIndicator);
    if(color.toLowerCase().equals("RED".toLowerCase()))
      color="rgba(203, 64, 64, 1)";
    else if(color.toLowerCase().equals("GREEN".toLowerCase()))
      color="rgba(82, 168, 30, 1)";
    else if(color.toLowerCase().equals("AMBER".toLowerCase()))
      color="rgba(240, 165, 19, 1)";

      assertEquals(color,distributionIndicator.getCssValue("background-color"));
  }

}