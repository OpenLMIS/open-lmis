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

import java.io.IOException;

import static org.openqa.selenium.support.How.ID;


public class ConfigureEDIPage extends Page {

  @FindBy(how = ID, using = "configureOrder")
  private static WebElement configureOrderButton;

  @FindBy(how = ID, using = "configureShipment")
  private static WebElement configureShipmentButton;

  @FindBy(how = ID, using = "configureBudget")
  private static WebElement configureBudgetButton;

  public ConfigureEDIPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
    testWebDriver.waitForElementToAppear(configureOrderButton);
  }

  public ConfigureOrderPage navigateConfigureOrderPage() throws IOException {
    testWebDriver.waitForElementToAppear(configureOrderButton);
    configureOrderButton.click();

    return new ConfigureOrderPage(testWebDriver);
  }

  public ConfigureShipmentPage navigateConfigureShipmentPage() throws IOException {
    testWebDriver.waitForElementToAppear(configureShipmentButton);
      configureShipmentButton.click();

    return new ConfigureShipmentPage(testWebDriver);
  }

  public ConfigureBudgetPage navigateConfigureBudgetPage() throws IOException {
     testWebDriver.waitForElementToAppear(configureBudgetButton);
     configureBudgetButton.click();

     return new ConfigureBudgetPage(testWebDriver);
  }
}
