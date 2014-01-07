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

import java.util.List;
import java.util.Map;

import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;

public class CoveragePage extends DistributionTab {

  @FindBy(how = How.XPATH, using = "//div[1]/div/div/ng-include/div/ul/li[5]/a")
  private static WebElement coverageTab = null;

  @FindBy(how = XPATH, using = "//div[@class='left-navigation ng-scope']/ul/li[5]/a/span[1][@class='status-icon']")
  public static WebElement coverageIndicator = null;

  @FindBy(how = ID, using = "femaleHealthCenter")
  public static WebElement femaleHealthCenterField = null;

  @FindBy(how = ID, using = "femaleMobileBrigade")
  public static WebElement femaleMobileBrigadeField = null;

  @FindBy(how = ID, using = "maleHealthCenter")
  public static WebElement maleHealthCenterField = null;

  @FindBy(how = ID, using = "maleMobileBrigade")
  public static WebElement maleMobileBrigadeField = null;

  public CoveragePage(TestWebDriver driver) {
    super(driver);
  }

  @Override
  public void verifyIndicator(String color) {
    verifyOverallIndicator(coverageIndicator, color);
  }

  @Override
  public void enterValues(List<Map<String, String>> dataMapList) {
    Map<String, String> data = dataMapList.get(0);
    enterFemaleHealthCenter(Integer.valueOf(data.get("femaleHealthCenter")));
    enterFemaleMobileBrigade(Integer.valueOf(data.get("femaleMobileBrigade")));
    enterMaleHealthCenter(Integer.valueOf(data.get("maleHealthCenter")));
    enterMaleMobileBrigade(Integer.valueOf(data.get("maleMobileBrigade")));
  }

  @Override
  public void verifyData(Map<String, String> map) {
    //To change body of implemented methods use File | Settings | File Templates.
  }


  @Override
  public void navigate() {
    coverageTab.click();
  }


  public void enterFemaleHealthCenter(Integer femaleHealthCenter) {
    testWebDriver.waitForElementToAppear(femaleHealthCenterField);
    sendKeys(femaleHealthCenterField, femaleHealthCenter.toString());
  }


  public void enterFemaleMobileBrigade(Integer femaleMobileBrigade) {
    testWebDriver.waitForElementToAppear(femaleMobileBrigadeField);
    sendKeys(femaleMobileBrigadeField, femaleMobileBrigade.toString());
  }

  public void enterMaleHealthCenter(Integer maleHealthCenter) {
    testWebDriver.waitForElementToAppear(maleHealthCenterField);
    sendKeys(maleHealthCenterField, maleHealthCenter.toString());
  }

  public void enterMaleMobileBrigade(Integer maleMobileBrigade) {
    testWebDriver.waitForElementToAppear(maleMobileBrigadeField);
    sendKeys(maleMobileBrigadeField, maleMobileBrigade.toString());
  }

  public void enterData(Integer femaleHealthCenter, Integer femaleMobileBrigade, Integer maleHealthCenter, Integer maleMobileBrigade) {
    navigate();
    enterFemaleHealthCenter(femaleHealthCenter);
    enterFemaleMobileBrigade(femaleMobileBrigade);
    enterMaleHealthCenter(maleHealthCenter);
    enterMaleMobileBrigade(maleMobileBrigade);
  }

}
