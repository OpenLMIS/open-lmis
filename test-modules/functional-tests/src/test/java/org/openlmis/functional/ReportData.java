/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ReportData extends TestCaseHelper {

  static String separator = System.getProperty("file.separator");

  private static String downloadedFilePath = new File(System.getProperty("user.dir")).getParent() + separator + "csv";

  @BeforeMethod(groups = {"distribution"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
  }

  @Test(dataProvider = "Data-Provider-Function-Positive")
  public void testVerifyReport(String[] credentials) throws InterruptedException, IOException, SQLException {
    LoginPage loginPage = PageFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    homePage.navigateReportScreen();
    getReportData(1);
    deleteFile(downloadedFilePath);
  }

  public String[] getReportData(int reportNumber) throws InterruptedException, IOException, SQLException {
    WebElement reportLink = testWebDriver.getElementByXpath("//table[@class='table table-striped table-bordered']/tbody/tr[" + reportNumber + "]/td[2]/div/a[3]");
    reportLink.click();
    Thread.sleep(2500);
    return (readCSVFile(downloadedFilePath));
  }

  @AfterMethod(groups = {"distribution"})
  public void tearDown() throws SQLException {
    HomePage homePage = PageFactory.getHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {new String[]{"Admin123", "Admin123"}}
    };
  }
}
