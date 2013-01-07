package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

@TransactionConfiguration(defaultRollback=true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class Upload extends TestCaseHelper {

    @BeforeClass
    public void setUp() throws Exception
    {
        DBWrapper dbWrapper = new DBWrapper();
        dbWrapper.deleteData();
        dbWrapper.deleteFacilities();
    }

    @Test(dataProvider = "Data-Provider-Function-Positive")
    public void uploadFacilities(String user,  String[] credentials) throws Exception {

        LoginPage loginPage=new LoginPage(testWebDriver);

        HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

        UploadPage uploadPage = homePage.navigateUploads();
        uploadPage.uploadFacilities();
        testWebDriver.setImplicitWait(2500);

        homePage.logout();

    }
    @AfterClass
    public void tearDown() throws Exception
    {
        DBWrapper dbWrapper = new DBWrapper();
        dbWrapper.deleteData();

    }

    @DataProvider(name = "Data-Provider-Function-Positive")
    public Object[][] parameterIntTestProviderPositive() {
        return new Object[][]{
                {"User123", new String[]{"Admin123", "Admin123"}}
        };
    }
}
