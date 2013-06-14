/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.math.BigDecimal;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;


public class DistributionPage extends RequisitionPage {

  @FindBy(how = ID, using = "selectDeliveryZone")
  private static WebElement selectDeliveryZoneSelectBox;

  @FindBy(how = ID, using = "selectProgram")
  private static WebElement selectProgramSelectBox;

  @FindBy(how = ID, using = "selectPeriod")

  private static WebElement selectPeriodSelectBox;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Proceed')]")
  private static WebElement proceedButton;

  @FindBy(how = XPATH, using = "//a[contains(text(),'View warehouse load amount')]")
  private static WebElement viewWarehouseLoadAmountLink;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Input facility data')]")
  private static WebElement inputFacilityDataLink;




  public DistributionPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);

  }

  public  WebElement getSelectDeliveryZoneSelectBox() {
    return selectDeliveryZoneSelectBox;
  }

  public  WebElement getSelectProgramSelectBox() {
    return selectProgramSelectBox;
  }

  public  void setSelectProgramSelectBox(WebElement selectProgramSelectBox) {
    DistributionPage.selectProgramSelectBox = selectProgramSelectBox;
  }

  public  WebElement getSelectPeriodSelectBox() {
    return selectPeriodSelectBox;
  }

  public  void setSelectPeriodSelectBox(WebElement selectPeriodSelectBox) {
    DistributionPage.selectPeriodSelectBox = selectPeriodSelectBox;
  }

  public  WebElement getProceedButton() {
    return proceedButton;
  }

  public  void setProceedButton(WebElement proceedButton) {
    DistributionPage.proceedButton = proceedButton;
  }

  public  void setSelectDeliveryZoneSelectBox(WebElement selectDeliveryZoneSelectBox) {
    DistributionPage.selectDeliveryZoneSelectBox = selectDeliveryZoneSelectBox;
  }

  public static WebElement getInputFacilityDataLink() {
    return inputFacilityDataLink;
  }

  public static void setInputFacilityDataLink(WebElement inputFacilityDataLink) {
    DistributionPage.inputFacilityDataLink = inputFacilityDataLink;
  }

  public static WebElement getViewWarehouseLoadAmountLink() {
    return viewWarehouseLoadAmountLink;
  }

  public static void setViewWarehouseLoadAmountLink(WebElement viewWarehouseLoadAmountLink) {
    DistributionPage.viewWarehouseLoadAmountLink = viewWarehouseLoadAmountLink;
  }

}