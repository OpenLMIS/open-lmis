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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.awt.*;
import java.awt.event.KeyEvent;

import static org.openqa.selenium.support.How.ID;

public abstract class Page {

  public TestWebDriver testWebDriver;

  @FindBy(how = ID, using = "logout")
  private static WebElement logoutLink = null;

  protected Page(TestWebDriver driver) {
    this.testWebDriver = driver;
  }

  public LoginPage logout() {
    testWebDriver.waitForElementToBeEnabled(logoutLink);
    logoutLink.click();
    testWebDriver.sleep(500);
    return new LoginPage(testWebDriver);
  }

  public void sendKeys(WebElement locator, String value) {
    String locatorValueAttribute = testWebDriver.getAttribute(locator, "value");
    int length = locatorValueAttribute.length();
    for (int i = 0; i < length; i++) {
      locator.sendKeys("\u0008");
    }
    locator.sendKeys(value);
  }

  public void downloadFileWhileSaveDialogOPen(WebElement element) throws InterruptedException {
    try {
      Robot robot = new Robot();
      //get the focus on the element..don't use click since it stalls the driver
      element.sendKeys("");
      element.sendKeys(Keys.RETURN);
      //wait for the modal dialog to open
      Thread.sleep(3000);
      //press s key to save
      robot.keyPress(KeyEvent.VK_ALT);
      robot.keyPress(KeyEvent.VK_S);
      robot.keyRelease(KeyEvent.VK_S);
      robot.keyRelease(KeyEvent.VK_ALT);
      Thread.sleep(3000);

    } catch (AWTException e) {
      e.printStackTrace();
    }
  }
}
