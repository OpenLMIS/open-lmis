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
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;

abstract public class DistributionTab extends Page {

  public DistributionTab(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  abstract public void verifyIndicator(String color);
  abstract public void enterValues(Map<String, String> map);
  abstract public void verifyData(Map<String, String> map);
  abstract public void navigate();

  public void verifyOverallIndicator(WebElement element, String color) {
    testWebDriver.sleep(500);
    if (color.toLowerCase().equals("RED".toLowerCase()))
      color = "rgba(203, 64, 64, 1)";
    else if (color.toLowerCase().equals("GREEN".toLowerCase()))
      color = "rgba(82, 168, 30, 1)";
    else if (color.toLowerCase().equals("AMBER".toLowerCase()))
      color = "rgba(240, 165, 19, 1)";

    assertEquals(color, element.getCssValue("background-color"));
  }

}
