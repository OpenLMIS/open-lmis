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

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;


public class FacilityListPage extends RequisitionPage {

  @FindBy(how = XPATH, using = "//h2[contains(text(),'No facility selected')]")
  private static WebElement noFacilitySelectedHeader;

  @FindBy(how = XPATH, using = "//div[@class='record-facility-data ng-scope']/h2[1]")
  private static WebElement facilityPageHeader;

  @FindBy(how = XPATH, using = "//a[@class='select2-choice']/span")
  private static WebElement facilityListSelect;

  @FindBy(how = ID, using = "selectFacility")
  private static WebElement facilityListDropDown;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/div/input")
  private static WebElement facilityListTextField;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/ul/li/ul/li/div/div")
  private static WebElement facilityListSelectField;



  public FacilityListPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public void verifyNoFacilitySelected() {
    testWebDriver.waitForElementToAppear(noFacilitySelectedHeader);
    assertTrue("noFacilitySelectedHeader should show", noFacilitySelectedHeader.isDisplayed());
  }

  public List<WebElement> getAllFacilitiesFromDropDown() {
    List<WebElement> options = testWebDriver.getOptions(facilityListDropDown);
    return options;
  }

  public void verifyHeaderElements(String deliveryZone, String program, String period)
  {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//div[@class='info-box']/div[@class='row-fluid']/div[2][@class='span3 offset1 info-box-labels']/span[@class='ng-binding']"));
    assertEquals(deliveryZone, testWebDriver.getElementByXpath("//div[@class='info-box']/div[@class='row-fluid']/div[2][@class='span3 offset1 info-box-labels']/span[@class='ng-binding']").getText());
    assertEquals(program, testWebDriver.getElementByXpath("//div[@class='info-box']/div[@class='row-fluid']/div[3][@class='span3 info-box-labels']/span[@class='ng-binding']").getText());
    assertEquals(period, testWebDriver.getElementByXpath("//div[@class='info-box']/div[@class='row-fluid']/div[4][@class='span2 info-box-labels']/span[@class='ng-binding']").getText());
  }

  public void verifyGeographicZoneOrder(String geoZoneFirst, String geoZoneSecond)
  {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//ul[@class='select2-results']/li[2][@class='select2-results-dept-0 select2-result select2-result-unselectable select2-result-with-children']/div[@class='select2-result-label']"));
    assertEquals(geoZoneFirst, testWebDriver.getElementByXpath("//ul[@class='select2-results']/li[2][@class='select2-results-dept-0 select2-result select2-result-unselectable select2-result-with-children']/div[@class='select2-result-label']").getText());
    assertEquals(geoZoneSecond, testWebDriver.getElementByXpath("//ul[@class='select2-results']/li[3][@class='select2-results-dept-0 select2-result select2-result-unselectable select2-result-with-children']/div[@class='select2-result-label']").getText());
  }

  public void selectFacility(String facilityCode)
  {
    clickFacilityListDropDown();
    testWebDriver.waitForElementToAppear(facilityListTextField);
    facilityListTextField.clear();
    facilityListTextField.sendKeys(facilityCode);
    testWebDriver.waitForElementToAppear(facilityListSelectField);
    facilityListSelectField.click();
  }

  public void clickFacilityListDropDown() {
    testWebDriver.waitForElementToAppear(facilityListSelect);
    facilityListSelect.click();
  }


  public void verifyFacilityNameInHeader(String facilityName)
  {
    testWebDriver.waitForElementToAppear(facilityPageHeader);
    assertEquals(facilityPageHeader.getText(), facilityName);
  }




}