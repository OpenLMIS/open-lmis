package org.openlmis.functional;

import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.openlmis.pageobjects.edi.ConfigureOrderNumberPage;
import org.openlmis.pageobjects.edi.ConfigureShipmentPage;
import org.openlmis.pageobjects.edi.ConfigureSystemSettingsPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;


@Listeners(CaptureScreenshotOnFailureListener.class)
public class ConfigureOrderNumberTemplate extends TestCaseHelper {

  private static final String user = "Admin123";
  private static final String password = "Admin123";
  LoginPage loginPage;
  ConfigureOrderNumberPage configureOrderNumberPage;
  ConfigureSystemSettingsPage configureSystemSettingsPage;

  @BeforeMethod(groups = "admin")
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.setupShipmentFileConfiguration("false");
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Test(groups = {"admin"})
  public void testOrderNumber() {
    HomePage homePage = loginPage.loginAs(user, password);
    configureSystemSettingsPage = homePage.navigateSystemSettingsScreen();
    //configureOrderNumberPage = configureSystemSettingsPage.navigateConfigureOrderNumberPage();
    //configureOrderNumberPage.checkIncludeHeader();
    //configureOrderNumberPage.setOrderNumberPrefix("P");
    //configureOrderNumberPage.clickSaveButton();
  }
}
