package org.openlmis.functional;

import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.openlmis.pageobjects.edi.ConfigureOrderNumberPage;
import org.openlmis.pageobjects.edi.ConfigureSystemSettingsPage;
import org.testng.annotations.AfterMethod;
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
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", "SYSTEM_SETTINGS");
    dbWrapper.setupOrderNumberConfiguration("O", true, true, true, true);
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Test(groups = {"admin"})
  public void testByDefaultValues() {
    HomePage homePage = loginPage.loginAs(user, password);
    configureSystemSettingsPage = homePage.navigateSystemSettingsScreen();
    configureOrderNumberPage = configureSystemSettingsPage.navigateConfigureOrderNumberPage();

    assertEquals(configureOrderNumberPage.getOrderNumberPrefix(), "O");
    assertTrue(configureOrderNumberPage.isProgramCodeChecked());
    assertTrue(configureOrderNumberPage.isIncludeSequenceCodeChecked());
    assertTrue(configureOrderNumberPage.isIncludeSequenceCodeDisabled());
    assertTrue(configureOrderNumberPage.isIncludeRnrTypeSuffixChecked());
  }

  @Test(groups = {"admin"})
  public void testOrderNumber() {
    HomePage homePage = loginPage.loginAs(user, password);
    configureSystemSettingsPage = homePage.navigateSystemSettingsScreen();
    configureOrderNumberPage = configureSystemSettingsPage.navigateConfigureOrderNumberPage();
    configureOrderNumberPage.deletePreExistingData();
    configureOrderNumberPage.setOrderNumberPrefix("P");
    configureOrderNumberPage.clickSaveButton();
    configureOrderNumberPage.verifyMessage("Order number configuration saved successfully");

    testWebDriver.refresh();
    configureSystemSettingsPage = homePage.navigateSystemSettingsScreen();
    configureOrderNumberPage = configureSystemSettingsPage.navigateConfigureOrderNumberPage();

    assertEquals(configureOrderNumberPage.getOrderNumberPrefix(), "P");
    assertTrue(configureOrderNumberPage.isProgramCodeChecked());
    assertTrue(configureOrderNumberPage.isIncludeSequenceCodeChecked());
    assertTrue(configureOrderNumberPage.isIncludeSequenceCodeDisabled());
    assertTrue(configureOrderNumberPage.isIncludeRnrTypeSuffixChecked());
  }

  @Test(groups = {"admin"})
  public void testMixedValueInOrderNumberField() {
    HomePage homePage = loginPage.loginAs(user, password);
    configureSystemSettingsPage = homePage.navigateSystemSettingsScreen();
    configureOrderNumberPage = configureSystemSettingsPage.navigateConfigureOrderNumberPage();
    configureOrderNumberPage.deletePreExistingData();
    configureOrderNumberPage.setOrderNumberPrefix("P#67hj89");
    configureOrderNumberPage.clickSaveButton();
    configureOrderNumberPage.verifyMessage("Order number configuration saved successfully");

    testWebDriver.refresh();
    configureSystemSettingsPage = homePage.navigateSystemSettingsScreen();
    configureOrderNumberPage = configureSystemSettingsPage.navigateConfigureOrderNumberPage();

    assertEquals(configureOrderNumberPage.getOrderNumberPrefix(), "P#67hj89");
    assertTrue(configureOrderNumberPage.isProgramCodeChecked());
    assertTrue(configureOrderNumberPage.isIncludeSequenceCodeChecked());
    assertTrue(configureOrderNumberPage.isIncludeSequenceCodeDisabled());
    assertTrue(configureOrderNumberPage.isIncludeRnrTypeSuffixChecked());
  }

  @Test(groups = {"admin"})
  public void verifyAfterUnCheckingAllFields() {
    HomePage homePage = loginPage.loginAs(user, password);
    configureSystemSettingsPage = homePage.navigateSystemSettingsScreen();
    configureOrderNumberPage = configureSystemSettingsPage.navigateConfigureOrderNumberPage();

    configureOrderNumberPage.unCheckOrderNumberPrefixCheckbox();
    configureOrderNumberPage.unCheckProgramCodeCheckbox();
    configureOrderNumberPage.unCheckIncludeRnrTypeSuffixCheckbox();
    configureOrderNumberPage.clickSaveButton();
    configureOrderNumberPage.verifyMessage("Order number configuration saved successfully");

    testWebDriver.refresh();
    configureSystemSettingsPage = homePage.navigateSystemSettingsScreen();
    configureOrderNumberPage = configureSystemSettingsPage.navigateConfigureOrderNumberPage();

    assertFalse(configureOrderNumberPage.isOrderNumberPrefixSelected());
    assertFalse(configureOrderNumberPage.isProgramCodeChecked());
    assertTrue(configureOrderNumberPage.isIncludeSequenceCodeChecked());
    assertTrue(configureOrderNumberPage.isIncludeSequenceCodeDisabled());
    assertFalse(configureOrderNumberPage.isIncludeRnrTypeSuffixChecked());
  }

  @AfterMethod(groups = "admin")
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.insertAllAdminRightsAsSeedData();
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
  }
}
