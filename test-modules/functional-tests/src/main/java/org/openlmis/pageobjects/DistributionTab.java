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
