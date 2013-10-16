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


import com.thoughtworks.selenium.SeleneseTestBase;
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

  @FindBy(how = XPATH, using = "//div[@class='record-facility-data ng-scope']/div[1]/div[1]/h2/span[3]")
  private static WebElement facilityPageHeaderName;

  @FindBy(how = XPATH, using = "//div[@class='record-facility-data ng-scope']/div[1]/div[1]/h2/span[1]")
  private static WebElement facilityPageHeaderZone;

  @FindBy(how = XPATH, using = "//*[@id='s2id_selectFacility']/a")
  private static WebElement facilityListSelect;

  @FindBy(how = ID, using = "selectFacility")
  private static WebElement facilityListDropDown;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/div/input")
  private static WebElement facilityListTextField;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/ul/li/ul/li/div/div")
  private static WebElement facilityListSelectField;

  @FindBy(how = ID, using = "facilityIndicator")
  private static WebElement facilityOverAllIndicator;

  @FindBy(how = XPATH, using = "//div[@class='select2-result-label']/div/span[@class='status-icon']")
  private static WebElement firstFacilityIndicator;


    @FindBy(how = XPATH, using =  "//div[@id='legend']/span[1]/span[2]")
    private static WebElement legendNotStartedText;

    @FindBy(how = XPATH, using =  "//div[@id='legend']/span[1]/span[1]")
    private static WebElement legendNotStartedIcon;


    @FindBy(how = XPATH, using =  "//div[@id='legend']/span[2]/span[2]")
    private static WebElement legendPartiallyCompletedText;

    @FindBy(how = XPATH, using =  "//div[@id='legend']/span[2]/span[1]")
    private static WebElement legendPartiallyCompletedIcon;

    @FindBy(how = XPATH, using =  "//div[@id='legend']/span[3]/span[2]")
    private static WebElement legendCompletedText;

    @FindBy(how = XPATH, using =  "//div[@id='legend']/span[3]/span[1]")
    private static WebElement legendCompletedIcon;


    @FindBy(how = XPATH, using =  "//div[@id='legend']/span[4]/span[2]")
    private static WebElement legendSynchronizedText;

    @FindBy(how = XPATH, using =  "//div[@id='legend']/span[4]/span[1]")
    private static WebElement legendSynchronizedIcon;


    @FindBy(how = XPATH, using =  "//div[@id='legend']/span[5]/span[2]")
    private static WebElement legendCannotSynchronizedText;

    @FindBy(how = XPATH, using =  "//div[@id='legend']/span[5]/span[1]")
    private static WebElement legendCannotSynchronizedIcon;

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
    testWebDriver.sleep(1500);
    assertEquals(geoZoneFirst, testWebDriver.getElementByXpath("//*[@id='select2-drop']/ul/li[1]/div").getText());
    assertEquals(geoZoneSecond, testWebDriver.getElementByXpath("//*[@id='select2-drop']/ul/li[2]/div").getText());
  }

  public void selectFacility(String facilityCode)
  {
    clickFacilityListDropDown();
    testWebDriver.waitForElementToAppear(facilityListTextField);
    facilityListTextField.clear();
    facilityListTextField.sendKeys(facilityCode);
    testWebDriver.waitForElementToAppear(facilityListSelectField);
    facilityListSelectField.click();
    testWebDriver.sleep(250);
  }

  public void clickFacilityListDropDown() {
    testWebDriver.sleep(2000);
    testWebDriver.waitForElementToAppear(facilityListSelect);
    facilityListSelect.click();
  }


  public void verifyFacilityNameInHeader(String facilityName)
  {
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(facilityPageHeaderName);
    assertEquals(facilityPageHeaderName.getText(),facilityName);
  }

  public void verifyFacilityZoneInHeader(String facilityZone)
  {
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(facilityPageHeaderZone);
    assertEquals(facilityPageHeaderZone.getText(),facilityZone);
  }

  public void verifyFacilityIndicatorColor(String whichIcon, String color) {
    testWebDriver.waitForElementToAppear(facilityOverAllIndicator);
    if(color.toLowerCase().equals("RED".toLowerCase()))
      color="rgba(203, 64, 64, 1)";
    else if(color.toLowerCase().equals("GREEN".toLowerCase()))
      color="rgba(82, 168, 30, 1)";
    else if(color.toLowerCase().equals("AMBER".toLowerCase()))
      color="rgba(240, 165, 19, 1)";
    else if (color.toLowerCase().equals("Blue".toLowerCase()))
      color="rgba(75, 169, 253, 1)";

    if(whichIcon.toLowerCase().equals("Overall".toLowerCase()))
      assertEquals(color, facilityOverAllIndicator.getCssValue("background-color"));
    else if(whichIcon.toLowerCase().equals("Individual".toLowerCase())){
      clickFacilityListDropDown();
      testWebDriver.waitForElementToAppear(facilityListTextField);
      testWebDriver.getElementByXpath("//*[@id='select2-drop']/ul/li[1]/div").click();
      assertEquals(color, firstFacilityIndicator.getCssValue("background-color"));
      clickFacilityListDropDown();
    }

  }

  public void verifyLegend() {
      assertEquals(legendNotStartedText.getText(),"Not started");
      assertEquals(legendPartiallyCompletedText.getText(),"Partially completed");
      assertEquals(legendCompletedText.getText(),"Completed");
      assertEquals(legendSynchronizedText.getText(),"Synchronized");
      assertEquals(legendCannotSynchronizedText.getText(),"Cannot synchronized");

      assertEquals(legendNotStartedIcon.getCssValue("background-color"),"rgba(203, 64, 64, 1)");
      assertEquals(legendPartiallyCompletedIcon.getCssValue("background-color"),"rgba(240, 165, 19, 1)");
      assertEquals(legendCompletedIcon.getCssValue("background-color"),"rgba(82, 168, 30, 1)");
      assertEquals(legendSynchronizedIcon.getCssValue("background-color"),"rgba(75, 169, 253, 1)");
      assertEquals(legendCannotSynchronizedIcon.getCssValue("background-color"),"rgba(124, 124, 124, 1)");

  }
}