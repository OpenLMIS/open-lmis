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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.util.HashMap;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;

public class GeneralObservationPage extends DistributionTab {

  public static final String VERIFIED_BY_NAME = "verifiedByName";
  public static final String VERIFIED_BY_TITLE = "verifiedByTitle";
  public static final String CONFIRMED_BY_NAME = "confirmedByName";
  public static final String CONFIRMED_BY_TITLE = "confirmedByTitle";
  public static final String OBSERVATIONS = "observations";
  public static final String VALUE = "value";

  @FindBy(how = How.XPATH, using = "//div[1]/div/div/ng-include/div/ul/li[6]/a")
  private static WebElement generalObservationTab;

  @FindBy(how = XPATH, using = "//div[@class='left-navigation ng-scope']/ul/li[6]/a/span[1][@class='status-icon']")
  public static WebElement generalObservationsIndicator;

  @FindBy(how = ID, using = OBSERVATIONS)
  public static WebElement observationsField;

  @FindBy(how = ID, using = VERIFIED_BY_NAME)
  public static WebElement verifiedByNameField;

  @FindBy(how = ID, using = VERIFIED_BY_TITLE)
  public static WebElement verifiedByTitleField;

  @FindBy(how = ID, using = CONFIRMED_BY_NAME)
  public static WebElement confirmedByNameField;

  @FindBy(how = ID, using = CONFIRMED_BY_TITLE)
  public static WebElement confirmedByTitleField;

  public Map<String, WebElement> fieldMap = new HashMap<String, WebElement>() {{
    put(OBSERVATIONS, observationsField);
    put(CONFIRMED_BY_NAME, confirmedByNameField);
    put(CONFIRMED_BY_TITLE, confirmedByTitleField);
    put(VERIFIED_BY_TITLE, verifiedByTitleField);
    put(VERIFIED_BY_NAME, verifiedByNameField);
  }};

  public GeneralObservationPage(TestWebDriver driver) {
    super(driver);
  }

  @Override
  public void verifyIndicator(String color) {
    verifyOverallIndicator(generalObservationsIndicator, color);
  }

  @Override
  public void enterValues(Map<String, String> data) {
    sendKeys(fieldMap.get(OBSERVATIONS), data.get(OBSERVATIONS));
    sendKeys(fieldMap.get(VERIFIED_BY_NAME), data.get(VERIFIED_BY_NAME));
    sendKeys(fieldMap.get(VERIFIED_BY_TITLE), data.get(VERIFIED_BY_TITLE));
    sendKeys(fieldMap.get(CONFIRMED_BY_NAME), data.get(CONFIRMED_BY_NAME));
    sendKeys(fieldMap.get(CONFIRMED_BY_TITLE), data.get(CONFIRMED_BY_TITLE));
  }

  @Override
  public void verifyData(Map<String, String> data) {
    assertEquals(fieldMap.get(OBSERVATIONS).getAttribute(VALUE), data.get(OBSERVATIONS));
    assertEquals(fieldMap.get(VERIFIED_BY_NAME).getAttribute(VALUE), data.get(VERIFIED_BY_NAME));
    assertEquals(fieldMap.get(VERIFIED_BY_TITLE).getAttribute(VALUE), data.get(VERIFIED_BY_TITLE));
    assertEquals(fieldMap.get(CONFIRMED_BY_NAME).getAttribute(VALUE), data.get(CONFIRMED_BY_NAME));
    assertEquals(fieldMap.get(CONFIRMED_BY_TITLE).getAttribute(VALUE), data.get(CONFIRMED_BY_TITLE));
  }

  public void setObservations(String observations) {
    testWebDriver.waitForElementToAppear(observationsField);
    sendKeys(observationsField, observations);
  }

  public void setConfirmedByName(String confirmedByName) {
    testWebDriver.waitForElementToAppear(confirmedByNameField);
    sendKeys(confirmedByNameField, confirmedByName);
  }

  public void setConfirmedByTitle(String confirmedByTitle) {
    testWebDriver.waitForElementToAppear(confirmedByTitleField);
    sendKeys(confirmedByTitleField, confirmedByTitle);
  }

  public void setVerifiedByName(String verifiedByName) {
    testWebDriver.waitForElementToAppear(verifiedByNameField);
    sendKeys(verifiedByNameField, verifiedByName);
  }

  public void setVerifiedByTitle(String verifiedByTitle) {
    testWebDriver.waitForElementToAppear(verifiedByTitleField);
    sendKeys(verifiedByTitleField, verifiedByTitle);
  }

  @Override
  public void navigate() {
    generalObservationTab.click();
  }
}
