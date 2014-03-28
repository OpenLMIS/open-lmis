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
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static org.openqa.selenium.support.How.ID;

abstract public class DistributionTab extends Page {

  private static final String COLOR_RED = "rgba(203, 64, 64, 1)";
  private static final String COLOR_GREEN = "rgba(69, 182, 0, 1)";
  private static final String COLOR_AMBER = "rgba(240, 165, 19, 1)";
  private static final HashMap colorMap = new HashMap() {{
    put("red", COLOR_RED);
    put("green", COLOR_GREEN);
    put("amber", COLOR_AMBER);
  }};

  @FindBy(how = ID, using = "epiInventoryTab")
  private static WebElement epiInventoryTab = null;

  @FindBy(how = ID, using = "refrigeratorTab")
  private static WebElement refrigeratorTab = null;

  @FindBy(how = ID, using = "epiUseTab")
  private static WebElement epiUseTab = null;

  @FindBy(how = ID, using = "facilityVisitTab")
  private static WebElement visitInformationTab = null;

  @FindBy(how = ID, using = "coverageTab")
  private static WebElement coverageTab = null;

  @FindBy(how = ID, using = "childCoverageTabLabel")
  private static WebElement childCoverageTab = null;

  @FindBy(how = ID, using = "adultCoverageTabLabel")
  private static WebElement adultCoverageTab = null;


  public DistributionTab(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  abstract public void verifyIndicator(String color);

  abstract public void enterValues(List<Map<String, String>> dataMapList);

  abstract public void verifyData(List<Map<String, String>> map);

  abstract public void navigate();

  abstract public void verifyAllFieldsDisabled();

  abstract public void removeFocusFromElement();

  public void verifyOverallIndicator(WebElement element, String color) {
    testWebDriver.sleep(500);
    assertEquals(colorMap.get(color.toLowerCase()), element.getCssValue("background-color"));
  }

  public RefrigeratorPage navigateToRefrigerators() {
    refrigeratorTab.click();
    return PageObjectFactory.getRefrigeratorPage(testWebDriver);
  }

  public EPIUsePage navigateToEpiUse() {
    epiUseTab.click();
    return PageObjectFactory.getEpiUsePage(testWebDriver);
  }

  public EpiInventoryPage navigateToEpiInventory() {
    epiInventoryTab.click();
    return PageObjectFactory.getEpiInventoryPage(testWebDriver);
  }

  public FullCoveragePage navigateToFullCoverage() {
    coverageTab.click();
    return PageObjectFactory.getFullCoveragePage(testWebDriver);
  }

  public VisitInformationPage navigateToVisitInformation() {
    visitInformationTab.click();
    return PageObjectFactory.getVisitInformationPage(testWebDriver);
  }

  public ChildCoveragePage navigateToChildCoverage() {
    childCoverageTab.click();
    return PageObjectFactory.getChildCoveragePage(testWebDriver);
  }

  public AdultCoveragePage navigateToAdultCoverage() {
    adultCoverageTab.click();
    return PageObjectFactory.getAdultCoveragePage(testWebDriver);
  }
}
