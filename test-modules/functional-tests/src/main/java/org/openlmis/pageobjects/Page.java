/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;

import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class Page {

  public TestWebDriver testWebDriver;

  protected Page(TestWebDriver driver) {
    this.testWebDriver = driver;
  }
    public void sendKeys(WebElement locator, String value) {
        int length = testWebDriver.getAttribute(locator, "value").length();
        for (int i = 0; i < length; i++)
            locator.sendKeys("\u0008");
        locator.sendKeys(value);
    }

}
