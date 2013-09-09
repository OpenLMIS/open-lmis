/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
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

public class GeneralObservation extends DistributionTab {

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
    put(CONFIRMED_BY_NAME, verifiedByNameField);
    put(CONFIRMED_BY_TITLE, verifiedByTitleField);
    put(VERIFIED_BY_TITLE, confirmedByTitleField);
    put(VERIFIED_BY_NAME, confirmedByNameField);
  }};

  public GeneralObservation(TestWebDriver driver) {
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

  @Override
  public void navigate() {
    generalObservationTab.click();
  }
}
