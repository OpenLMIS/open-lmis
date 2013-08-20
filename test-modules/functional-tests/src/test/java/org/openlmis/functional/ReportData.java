/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http:mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.RolesPage;
import org.openlmis.pageobjects.UploadPage;
import org.openqa.selenium.WebElement;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ReportData extends TestCaseHelper {

    private static String downloadedFilePath = "/Users/Raman/Downloads/csv";

  @BeforeMethod(groups = {"functional"})
  public void setUp() throws Exception {
    super.setup();
  }
    @Test(dataProvider = "Data-Provider-Function-Positive")
    public void testVerifyReport(String[] credentials) throws Exception {
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
        homePage.navigateReportScreen();
        getReportData(1);
        deleteFile(downloadedFilePath);
    }

    public String[] getReportData(int reportNumber) throws Exception {
        WebElement reportLink=testWebDriver.getElementByXpath("//table[@class='table table-striped table-bordered']/tbody/tr[" + reportNumber + "]/td[2]/div/a[3]");
        reportLink.click();
        Thread.sleep(2000);
        return(readCSVFile(downloadedFilePath));
    }

    @AfterMethod(groups = {"functional"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
        {new String[]{"Admin123", "Admin123"}}
    };
  }
}
