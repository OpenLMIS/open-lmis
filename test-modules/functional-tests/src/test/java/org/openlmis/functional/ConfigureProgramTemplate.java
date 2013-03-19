package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ConfigureProgramTemplate extends TestCaseHelper {

  DBWrapper dbWrapper;

  @BeforeMethod(groups = {"functional"})
  @Parameters({"browser"})
  public void setUp(String browser) throws Exception {
    super.setupSuite(browser);
    dbWrapper = new DBWrapper();
    dbWrapper.deleteData();
  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Column-Label-Source")
  public void testVerifyColumnLabelsAndSource(String program, String[] credentials) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    TemplateConfigPage templateConfigPage = homePage.selectProgramToConfigTemplate(program);
    templateConfigPage.verifyColumnLabels();
    templateConfigPage.verifyColumnSource();

  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Column-Label-Source")
  public void testVerifyArithmeticValidation(String program, String[] credentials) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    TemplateConfigPage templateConfigPage = homePage.selectProgramToConfigTemplate(program);
    templateConfigPage.verifyArithmeticValidations();


  }

  @AfterMethod(groups = {"functional"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout();
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Column-Label-Source")
  public Object[][] parameterColumnLabelSource() {
    return new Object[][]{
      {"HIV", new String[]{"Admin123", "Admin123"}}
    };

  }
}

