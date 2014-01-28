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

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.ManagePodPage;
import org.openlmis.pageobjects.UpdatePodPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PODPagination extends TestCaseHelper{


  public static final String USER = "user";
  public static final String PASSWORD = "password";
  public static final String PROGRAM = "program";

  public Map<String, String> podPaginationData = new HashMap<String, String>() {{
    put(USER, "storeInCharge");
    put(PASSWORD, "Admin123");
    put(PROGRAM, "HIV");
  }};

  @BeforeMethod(groups = {"requisition"})
  public void setUp() throws Exception {
    super.setup();
    dbWrapper.setupMultipleProducts(podPaginationData.get(PROGRAM), "Lvl3 Hospital", 25, false);
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(podPaginationData.get(PROGRAM));
    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_POD");

    setupTestUserRoleRightsData("200", podPaginationData.get(USER), rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", podPaginationData.get(PROGRAM), "F10", true);
    dbWrapper.insertFulfilmentRoleAssignment("storeInCharge", "store in-charge", "F10");
    dbWrapper.insertRequisitionWithMultipleLineItems(25, podPaginationData.get(PROGRAM), true, "F10", false);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK");
  }

  @Test(groups = {"requisition"})
  public void testRnRPaginationAndSpecificDisplayOrder() throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(podPaginationData.get(USER), podPaginationData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    UpdatePodPage updatePodPage = managePodPage.clickUpdatePODLink();
  }

}
