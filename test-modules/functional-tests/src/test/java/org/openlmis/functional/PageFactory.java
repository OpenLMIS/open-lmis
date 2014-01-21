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

import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.*;
import org.openlmis.pageobjects.edi.ConfigureOrderPage;

import java.io.IOException;

public class PageFactory{

  private static ManageFacilityPage instanceOfManageFacilityPage;
  private static DistributionPage instanceOfDistributionPage;
  private static FacilityListPage instanceOfFacilityListPage;
  private static RefrigeratorPage instanceOfRefrigeratorPage;
  private static GeneralObservationPage instanceOfObservation;
  private static CoveragePage instanceOfCoveragePage;
  private static EPIUsePage instanceOfEpiUsePage;
  private static EpiInventoryPage instanceOfEpiInventoryPage;
  private static WarehouseLoadAmountPage instanceOfWarehouseLoadAmountPage;
  private static ProgramProductISAPage instanceOfProgramProductISAPage;
  private static ConfigureOrderPage instanceOfConfigureOrderPage;

  public static ManageFacilityPage getInstanceOfManageFacilityPage(TestWebDriver testWebDriver) {
    if (instanceOfManageFacilityPage == null) {
      instanceOfManageFacilityPage = new ManageFacilityPage(testWebDriver);
    }
    return instanceOfManageFacilityPage;
  }

  public static DistributionPage getInstanceOfDistributionPage(TestWebDriver testWebDriver) throws IOException {
    if (instanceOfDistributionPage == null) {
      instanceOfDistributionPage = new DistributionPage(testWebDriver);
    }
    return instanceOfDistributionPage;
  }

  public static FacilityListPage getInstanceOfFacilityListPage(TestWebDriver testWebDriver) throws IOException {
    if (instanceOfFacilityListPage == null) {
      instanceOfFacilityListPage = new FacilityListPage(testWebDriver);
    }
    return instanceOfFacilityListPage;
  }

  public static RefrigeratorPage getInstanceOfRefrigeratorPage(TestWebDriver testWebDriver) {
    if (instanceOfRefrigeratorPage == null) {
      instanceOfRefrigeratorPage = new RefrigeratorPage(testWebDriver);
    }
    return instanceOfRefrigeratorPage;
  }

  public static GeneralObservationPage getInstanceOfObservation(TestWebDriver testWebDriver) {
    if (instanceOfObservation == null) {
      instanceOfObservation = new GeneralObservationPage(testWebDriver);
    }
    return instanceOfObservation;
  }

  public static CoveragePage getInstanceOfCoveragePage(TestWebDriver testWebDriver) {
    if (instanceOfCoveragePage == null) {
      instanceOfCoveragePage = new CoveragePage(testWebDriver);
    }
    return instanceOfCoveragePage;
  }

  public static EPIUsePage getInstanceOfEpiUsePage(TestWebDriver testWebDriver) {
    if (instanceOfEpiUsePage == null) {
      instanceOfEpiUsePage = new EPIUsePage(testWebDriver);
    }
    return instanceOfEpiUsePage;
  }

  public static EpiInventoryPage getInstanceOfEpiInventoryPage(TestWebDriver testWebDriver) {
    if (instanceOfEpiInventoryPage == null) {
      instanceOfEpiInventoryPage = new EpiInventoryPage(testWebDriver);
    }
    return instanceOfEpiInventoryPage;
  }

  public static WarehouseLoadAmountPage getInstanceOfWarehouseLoadAmountPage(TestWebDriver testWebDriver) throws IOException {
    if (instanceOfWarehouseLoadAmountPage == null) {
      instanceOfWarehouseLoadAmountPage = new WarehouseLoadAmountPage(testWebDriver);
    }
    return instanceOfWarehouseLoadAmountPage;
  }

  public static ProgramProductISAPage getInstanceOfProgramProductIsaPage(TestWebDriver testWebDriver) throws IOException {
    if (instanceOfProgramProductISAPage == null) {
      instanceOfProgramProductISAPage = new ProgramProductISAPage(testWebDriver);
    }
    return instanceOfProgramProductISAPage;
  }

  public static ConfigureOrderPage getInstanceOfConfigureOrderPage(TestWebDriver testWebDriver) throws IOException {
    if (instanceOfConfigureOrderPage == null) {
      instanceOfConfigureOrderPage = new ConfigureOrderPage(testWebDriver);
    }
    return instanceOfConfigureOrderPage;
  }
}
