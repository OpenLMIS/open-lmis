package org.openlmis.functional;

import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.CreateFacilityPage;
import org.openlmis.pageobjects.LoginPage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreateFacilityTest extends TestCaseHelper {


    @Test(dataProvider = "Data-Provider-Function-Positive")
    public void testLoginPositive(String facilityCode, String facilityName, String[] credentials) {
        LoginPage loginpage=new LoginPage(testWebDriver);
        CreateFacilityPage createfacilitypage=new CreateFacilityPage(testWebDriver);
        loginpage.login(credentials[0], credentials[1]);
        createfacilitypage.navigateCreateFacility();
        SeleneseTestNgHelper.assertEquals(createfacilitypage.enterAndVerifyFacility(facilityCode, facilityName),
                facilityName + " created successfully");

    }

    @DataProvider(name = "Data-Provider-Function-Positive")
    public Object[][] parameterIntTestProviderPositive() {
        return new Object[][]{
                {"FCcode4","FCname4", new String[]{"Admin123", "Admin123"}}
        };
    }


}
