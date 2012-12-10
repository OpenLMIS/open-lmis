package org.openlmis.functional;


import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.CreateFacilityPage;
import org.openlmis.pageobjects.LoginPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
@TransactionConfiguration(defaultRollback=true)
@Transactional
public class CreateFacilityTest extends TestCaseHelper {


    @Test(dataProvider = "Data-Provider-Function-Positive")
    public void testLoginPositive(String[] credentials) {

        LoginPage loginpage=new LoginPage(testWebDriver);
        CreateFacilityPage createfacilitypage=new CreateFacilityPage(testWebDriver);
        loginpage.login(credentials[0], credentials[1]);
        createfacilitypage.navigateCreateFacility();
        createfacilitypage.enterAndVerifyFacility();
        createfacilitypage.insertUserAndAllocateFacility();

    }

//    @AfterClass
//    public void tearDown() throws Exception
//    {
//        CreateFacilityPage createfacilitypage=new CreateFacilityPage(testWebDriver);
//        createfacilitypage.deleteUser();
//    }

    @DataProvider(name = "Data-Provider-Function-Positive")
    public Object[][] parameterIntTestProviderPositive() {
        return new Object[][]{
                {new String[]{"Admin123", "Admin123"}}
        };
    }
}
