package org.openlmis.functional;

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.ManageGeographicZonesPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Henok
 * Date: 7/5/13
 * Time: 10:08 AM
 */

@TransactionConfiguration(defaultRollback = true)
@Transactional
@Listeners
public class E2EManageGeographicZones extends TestCaseHelper {

    @BeforeMethod(groups = {"functional2"})
    public void setup() throws Exception {
        super.setup();
    }

    @Test(groups = {"functional2"},dataProvider = "Data-Provider-Function-Positive")
    public void testE2EManageGeographicZonesAddFunctionality (String [] credentials ) throws IOException{
        LoginPage loginPage = new LoginPage(testWebDriver,baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(credentials[0],credentials[1]);
        ManageGeographicZonesPage manageGeographicZonesPage = homePage.navigateToGeographicZone();
        manageGeographicZonesPage.EnterAndVerifyNewGeographicZone("Code123","Test123","1","2");
    }

    @Test(groups = {"functional2"},dataProvider = "Data-Provider-Function-Positive")
    public void testE2EManageGeographicZonesAddAndEditFunctionality (String [] credentials ) throws IOException{
        LoginPage loginPage = new LoginPage(testWebDriver,baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(credentials[0],credentials[1]);
        ManageGeographicZonesPage manageGeographicZonesPage = homePage.navigateToGeographicZone();
        manageGeographicZonesPage.EnterAndVerifyNewGeographicZone("Code123","Test123","1","2");
        manageGeographicZonesPage.EnterAGeographicZoneAndConfirmEditWorks("Code123","Test123","Test123_Edited","1","2");
    }

    @AfterMethod
    public void tearDown() throws IOException{
        HomePage homePage= new HomePage(testWebDriver);
        homePage.logout(baseUrlGlobal);
    }

    @DataProvider(name="Data-Provider-Function-Positive")
    public Object[] parameterForTheTest(){
        return new Object[]{
                new String[]{"Admin123","Admin123"}
        };
    }



}
