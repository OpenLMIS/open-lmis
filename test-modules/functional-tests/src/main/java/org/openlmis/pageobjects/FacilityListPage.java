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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;

public class FacilityListPage extends RequisitionPage {

  @FindBy(how = XPATH, using = "//*[@id='select2-drop']/div/input")
  private static WebElement inputFacilitySearch = null;

  @FindBy(how = ID, using = "noFacilitySelectedMessage")
  private static WebElement noFacilitySelectedHeader = null;

  @FindBy(how = ID, using = "facilitySelected")
  private static WebElement facilityPageHeaderName = null;

  @FindBy(how = ID, using = "healthCenterLabel")
  private static WebElement facilityPageHeaderZone = null;

  @FindBy(how = XPATH, using = "//*[@id='s2id_selectFacility']/a")
  private static WebElement facilityListSelect = null;

  @FindBy(how = ID, using = "selectFacility")
  private static WebElement facilityListDropDown = null;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/div/input")
  private static WebElement facilityListTextField = null;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/ul/li/ul/li/div/div")
  private static WebElement facilityListSelectField = null;

  @FindBy(how = ID, using = "facilityIndicator")
  private static WebElement facilityOverAllIndicator = null;

  @FindBy(how = XPATH, using = "//div[@class='select2-result-label']/div/span[@class='status-icon']")
  private static WebElement firstFacilityIndicator = null;

  @FindBy(how = ID, using = "notStartedLegendLabel")
  private static WebElement legendNotStartedText = null;

  @FindBy(how = ID, using = "notStartedLegendIcon")
  private static WebElement legendNotStartedIcon = null;

  @FindBy(how = ID, using = "partialLegendLabel")
  private static WebElement legendPartiallyCompletedText = null;

  @FindBy(how = ID, using = "partialLegendIcon")
  private static WebElement legendPartiallyCompletedIcon = null;

  @FindBy(how = ID, using = "completedLegendLabel")
  private static WebElement legendCompletedText = null;

  @FindBy(how = ID, using = "completedLegendIcon")
  private static WebElement legendCompletedIcon = null;

  @FindBy(how = ID, using = "syncedLegendLabel")
  private static WebElement legendSynchronizedText = null;

  @FindBy(how = ID, using = "syncedLegendIcon")
  private static WebElement legendSynchronizedIcon = null;

  @FindBy(how = ID, using = "cannotSyncLegendLabel")
  private static WebElement legendCannotSynchronizedText = null;

  @FindBy(how = ID, using = "cannotSyncLegendIcon")
  private static WebElement legendCannotSynchronizedIcon = null;

  public FacilityListPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public String getFacilitiesInDropDown() {
    testWebDriver.waitForElementToAppear(facilityListDropDown);
    return facilityListDropDown.getText();
  }

  public void verifyNoFacilitySelected() {
    testWebDriver.waitForElementToAppear(noFacilitySelectedHeader);
    assertTrue("noFacilitySelectedHeader should show", noFacilitySelectedHeader.isDisplayed());
  }

  public List<WebElement> getAllFacilitiesFromDropDown() {
    return testWebDriver.getOptions(facilityListDropDown);
  }

  public void verifyHeaderElements(String deliveryZone, String program, String period) {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//div[@class='info-box']/div[@class='row-fluid']/div[2][@class='span3 offset1 info-box-labels']/span[@class='ng-binding']"));
    assertEquals(deliveryZone, testWebDriver.getElementByXpath("//div[@class='info-box']/div[@class='row-fluid']/div[2][@class='span3 offset1 info-box-labels']/span[@class='ng-binding']").getText());
    assertEquals(program, testWebDriver.getElementByXpath("//div[@class='info-box']/div[@class='row-fluid']/div[3][@class='span3 info-box-labels']/span[@class='ng-binding']").getText());
    assertEquals(period, testWebDriver.getElementByXpath("//div[@class='info-box']/div[@class='row-fluid']/div[4][@class='span2 info-box-labels']/span[@class='ng-binding']").getText());
  }

  public void verifyGeographicZoneOrder(String geoZoneFirst, String geoZoneSecond) {
    testWebDriver.sleep(1500);
    assertEquals(geoZoneFirst, testWebDriver.getElementByXpath("//*[@id='select2-drop']/ul/li[1]/div").getText());
    assertEquals(geoZoneSecond, testWebDriver.getElementByXpath("//*[@id='select2-drop']/ul/li[2]/div").getText());
  }

  public VisitInformationPage selectFacility(String facilityCode) {
    clickFacilityListDropDown();
    testWebDriver.waitForElementToAppear(facilityListTextField);
    facilityListTextField.clear();
    facilityListTextField.sendKeys(facilityCode);
    testWebDriver.waitForElementToAppear(facilityListSelectField);
    facilityListSelectField.click();
    testWebDriver.sleep(250);
    return PageObjectFactory.getVisitInformationPage(testWebDriver);
  }

  public void clickFacilityListDropDown() {
    testWebDriver.sleep(2000);
    testWebDriver.waitForElementToAppear(facilityListSelect);
    facilityListSelect.click();
    testWebDriver.sleep(2000);
  }

  public void verifyFacilityNameInHeader(String facilityName) {
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(facilityPageHeaderName);
    assertEquals(facilityPageHeaderName.getText(), facilityName);
  }

  public void verifyFacilityZoneInHeader(String facilityZone) {
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(facilityPageHeaderZone);
    assertEquals(facilityPageHeaderZone.getText(), facilityZone);
  }

  public void verifyOverallFacilityIndicatorColor(String color) {
    testWebDriver.waitForElementToAppear(facilityOverAllIndicator);
    if (color.toLowerCase().equals("RED".toLowerCase()))
      color = "rgba(203, 64, 64, 1)";
    else if (color.toLowerCase().equals("GREEN".toLowerCase()))
      color = "rgba(69, 182, 0, 1)";
    else if (color.toLowerCase().equals("AMBER".toLowerCase()))
      color = "rgba(240, 165, 19, 1)";
    else if (color.toLowerCase().equals("Blue".toLowerCase()))
      color = "rgba(22, 131, 230, 1)";

    assertEquals(facilityOverAllIndicator.getCssValue("background-color"), color);
  }

  public void verifyIndividualFacilityIndicatorColor(String facilityCode, String color) {
    testWebDriver.waitForElementToAppear(facilityOverAllIndicator);
    if (color.toLowerCase().equals("RED".toLowerCase()))
      color = "rgba(203, 64, 64, 1)";
    else if (color.toLowerCase().equals("GREEN".toLowerCase()))
      color = "rgba(69, 182, 0, 1)";
    else if (color.toLowerCase().equals("AMBER".toLowerCase()))
      color = "rgba(240, 165, 19, 1)";
    else if (color.toLowerCase().equals("Blue".toLowerCase()))
      color = "rgba(22, 131, 230, 1)";

    clickFacilityListDropDown();
    testWebDriver.waitForElementToAppear(facilityListTextField);
    inputFacilitySearch.sendKeys(facilityCode);
    assertEquals(firstFacilityIndicator.getCssValue("background-color"), color);
    inputFacilitySearch.clear();
    inputFacilitySearch.sendKeys(Keys.ESCAPE);
  }

  public void verifyLegend() {
    assertEquals(legendNotStartedText.getText(), "Not started");
    assertEquals(legendPartiallyCompletedText.getText(), "Partially completed");
    assertEquals(legendCompletedText.getText(), "Completed");
    assertEquals(legendSynchronizedText.getText(), "Synced");
    assertEquals(legendCannotSynchronizedText.getText(), "Cannot sync");

    assertEquals(legendNotStartedIcon.getCssValue("background-color"), "rgba(203, 64, 64, 1)");
    assertEquals(legendPartiallyCompletedIcon.getCssValue("background-color"), "rgba(240, 165, 19, 1)");
    assertEquals(legendCompletedIcon.getCssValue("background-color"), "rgba(69, 182, 0, 1)");
    assertEquals(legendSynchronizedIcon.getCssValue("background-color"), "rgba(22, 131, 230, 1)");
    assertEquals(legendCannotSynchronizedIcon.getCssValue("background-color"), "rgba(124, 124, 124, 1)");
  }
}