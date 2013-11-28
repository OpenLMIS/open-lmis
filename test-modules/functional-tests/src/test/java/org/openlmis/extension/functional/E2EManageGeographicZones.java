/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.extension.functional;

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.extension.pageobjects.ManageGeographicZonesPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.*;

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
//        HomePage homePage = loginPage.loginAs(credentials[0],credentials[1]);
//        ManageGeographicZonesPage manageGeographicZonesPage = homePage.navigateToGeographicZone();
//        manageGeographicZonesPage.EnterAndVerifyNewGeographicZone("Code123","Test123","1","2");
    }

    @Test(groups = {"functional2"},dataProvider = "Data-Provider-Function-Positive")
    public void testE2EManageGeographicZonesAddAndEditFunctionality (String [] credentials ) throws IOException{
        LoginPage loginPage = new LoginPage(testWebDriver,baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(credentials[0],credentials[1]);
//       ManageGeographicZonesPage manageGeographicZonesPage = homePage.navigateToGeographicZone();
//        manageGeographicZonesPage.EnterAndVerifyNewGeographicZone("Code123","Test123","1","2");
//        manageGeographicZonesPage.EnterAGeographicZoneAndConfirmEditWorks("Code123","Test123","Test123_Edited","1","2");
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
