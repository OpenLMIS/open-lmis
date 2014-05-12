/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.pageobjects.edi;

import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.Page;
import org.openlmis.pageobjects.PageObjectFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import static org.openqa.selenium.support.How.ID;


public class ConfigureSystemSettingsPage extends Page {

  @FindBy(how = ID, using = "configureOrder")
  private static WebElement configureOrderButton = null;

  @FindBy(how = ID, using = "configureShipment")
  private static WebElement configureShipmentButton = null;

  @FindBy(how = ID, using = "configureBudget")
  private static WebElement configureBudgetButton = null;

  @FindBy(how = ID, using = "configureOrderNumber")
  private static WebElement configureOrderNumberButton = null;

  public ConfigureSystemSettingsPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
    testWebDriver.waitForElementToAppear(configureOrderButton);
  }

  public ConfigureOrderPage navigateConfigureOrderPage() {
    testWebDriver.waitForElementToAppear(configureOrderButton);
    configureOrderButton.click();
    return PageObjectFactory.getConfigureOrderPage(testWebDriver);
  }

  public ConfigureShipmentPage navigateConfigureShipmentPage() {
    testWebDriver.waitForElementToAppear(configureShipmentButton);
    configureShipmentButton.click();
    return PageObjectFactory.getConfigureShipmentPage(testWebDriver);
  }

  public ConfigureBudgetPage navigateConfigureBudgetPage() {
    testWebDriver.waitForElementToAppear(configureBudgetButton);
    configureBudgetButton.click();
    return PageObjectFactory.getConfigureBudgetPage(testWebDriver);
  }

  public ConfigureOrderNumberPage navigateConfigureOrderNumberPage() {
    testWebDriver.waitForElementToAppear(configureOrderNumberButton);
    configureOrderNumberButton.click();
    return PageObjectFactory.getConfigureOrderNumberPage(testWebDriver);
  }
}
