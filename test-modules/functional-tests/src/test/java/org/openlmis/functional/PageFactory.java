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
import org.openlmis.pageobjects.edi.*;

public class PageFactory {

  private static ManageFacilityPage instanceOfManageFacilityPage;
  private static DistributionPage instanceOfDistributionPage;
  private static FacilityListPage instanceOfFacilityListPage;
  private static RefrigeratorPage instanceOfRefrigeratorPage;
  private static VisitInformationPage instanceOfVisitInformationPage;
  private static FullCoveragePage instanceOfFullCoveragePage;
  private static EPIUsePage instanceOfEpiUsePage;
  private static ChildCoveragePage instanceOfChildCoveragePage;
  private static EpiInventoryPage instanceOfEpiInventoryPage;
  private static WarehouseLoadAmountPage instanceOfWarehouseLoadAmountPage;
  private static ProgramProductISAPage instanceOfProgramProductISAPage;
  private static ConfigureOrderPage instanceOfConfigureOrderPage;
  private static RegimenTemplateConfigPage instanceOfRegimenTemplateConfigPage;
  private static LoginPage instanceOfLoginPage;
  private static HomePage instanceOfHomePage;
  private static InitiateRnRPage instanceOfInitiateRnRPage;
  private static ViewRequisitionPage instanceOfViewRequisitionPage;
  private static UpdatePodPage instanceOfUpdatePodPage;
  private static UserPage instanceOfUserPage;
  private static RolesPage instanceOfRolesPage;
  private static InitiateRnR instanceOfInitiateRnR;
  private static ForgotPasswordPage instanceOfForgotPasswordPage;
  private static UploadPage instanceOfUploadPage;
  private static ApprovePage instanceOfApprovePage;
  private static ConvertOrderPage instanceOfConvertOrderPage;
  private static ViewOrdersPage instanceOfViewOrdersPage;
  private static ConfigureShipmentPage instanceOfConfigureShipmentPage;
  private static ConfigureBudgetPage instanceOfConfigureBudgetPage;
  private static ConfigureEDIPage instanceOfConfigureEdiPage;
  private static ManagePodPage instanceOfManagePodPage;

  public static ManageFacilityPage getInstanceOfManageFacilityPage(TestWebDriver testWebDriver) {
    if (instanceOfManageFacilityPage == null) {
      instanceOfManageFacilityPage = new ManageFacilityPage(testWebDriver);
    }
    return instanceOfManageFacilityPage;
  }

  public static DistributionPage getInstanceOfDistributionPage(TestWebDriver testWebDriver) {
    if (instanceOfDistributionPage == null) {
      instanceOfDistributionPage = new DistributionPage(testWebDriver);
    }
    return instanceOfDistributionPage;
  }

  public static FacilityListPage getInstanceOfFacilityListPage(TestWebDriver testWebDriver) {
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

  public static VisitInformationPage getInstanceOfVisitInformation(TestWebDriver testWebDriver) {
    if (instanceOfVisitInformationPage == null) {
      instanceOfVisitInformationPage = new VisitInformationPage(testWebDriver);
    }
    return instanceOfVisitInformationPage;
  }

  public static FullCoveragePage getInstanceOfFullCoveragePage(TestWebDriver testWebDriver) {
    if (instanceOfFullCoveragePage == null) {
      instanceOfFullCoveragePage = new FullCoveragePage(testWebDriver);
    }
    return instanceOfFullCoveragePage;
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

  public static ChildCoveragePage getInstanceOfChildCoveragePage(TestWebDriver testWebDriver) {
    if (instanceOfChildCoveragePage == null) {
      instanceOfChildCoveragePage = new ChildCoveragePage(testWebDriver);
    }
    return instanceOfChildCoveragePage;
  }

  public static WarehouseLoadAmountPage getInstanceOfWarehouseLoadAmountPage(TestWebDriver testWebDriver) {
    if (instanceOfWarehouseLoadAmountPage == null) {
      instanceOfWarehouseLoadAmountPage = new WarehouseLoadAmountPage(testWebDriver);
    }
    return instanceOfWarehouseLoadAmountPage;
  }

  public static ProgramProductISAPage getInstanceOfProgramProductIsaPage(TestWebDriver testWebDriver) {
    if (instanceOfProgramProductISAPage == null) {
      instanceOfProgramProductISAPage = new ProgramProductISAPage(testWebDriver);
    }
    return instanceOfProgramProductISAPage;
  }

  public static ConfigureOrderPage getInstanceOfConfigureOrderPage(TestWebDriver testWebDriver) {
    if (instanceOfConfigureOrderPage == null) {
      instanceOfConfigureOrderPage = new ConfigureOrderPage(testWebDriver);
    }
    return instanceOfConfigureOrderPage;
  }

  public static RegimenTemplateConfigPage getInstanceOfRegimenTemplateConfigPage(TestWebDriver testWebDriver) {
    if (instanceOfRegimenTemplateConfigPage == null) {
      instanceOfRegimenTemplateConfigPage = new RegimenTemplateConfigPage(testWebDriver);
    }
    return instanceOfRegimenTemplateConfigPage;
  }

  public static LoginPage getInstanceOfLoginPage(TestWebDriver testWebDriver, String baseUrlGlobal) {
    if (instanceOfLoginPage == null) {
      instanceOfLoginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    } else {
      testWebDriver.setBaseURL(baseUrlGlobal);
    }
    return instanceOfLoginPage;
  }

  public static HomePage getInstanceOfHomePage(TestWebDriver testWebDriver) {
    if (instanceOfHomePage == null) {
      instanceOfHomePage = new HomePage(testWebDriver);
    }
    return instanceOfHomePage;
  }

  public static InitiateRnRPage getInstanceOfInitiateRnRPage(TestWebDriver testWebDriver) {
    if (instanceOfInitiateRnRPage == null) {
      instanceOfInitiateRnRPage = new InitiateRnRPage(testWebDriver);
    }
    return instanceOfInitiateRnRPage;
  }

  public static ViewRequisitionPage getInstanceOfViewRequisitionPage(TestWebDriver testWebDriver) {
    if (instanceOfViewRequisitionPage == null) {
      instanceOfViewRequisitionPage = new ViewRequisitionPage(testWebDriver);
    }
    return instanceOfViewRequisitionPage;
  }

  public static UpdatePodPage getInstanceOfUpdatePodPage(TestWebDriver testWebDriver) {
    if (instanceOfUpdatePodPage == null) {
      instanceOfUpdatePodPage = new UpdatePodPage(testWebDriver);
    }
    return instanceOfUpdatePodPage;
  }

  public static UserPage getInstanceOfUserPage(TestWebDriver testWebDriver) {
    if (instanceOfUserPage == null) {
      instanceOfUserPage = new UserPage(testWebDriver);
    }
    return instanceOfUserPage;
  }

  public static RolesPage getInstanceOfRolesPage(TestWebDriver testWebDriver) {
    if (instanceOfRolesPage == null) {
      instanceOfRolesPage = new RolesPage(testWebDriver);
    }
    return instanceOfRolesPage;
  }

  public static InitiateRnR getInstanceOfInitiateRnR() {
    if (instanceOfInitiateRnR == null) {
      instanceOfInitiateRnR = new InitiateRnR();
    }
    return instanceOfInitiateRnR;
  }

  public static ForgotPasswordPage getInstanceOfForgotPasswordPage(TestWebDriver testWebDriver) {
    if (instanceOfForgotPasswordPage == null) {
      instanceOfForgotPasswordPage = new ForgotPasswordPage(testWebDriver);
    }
    return instanceOfForgotPasswordPage;
  }

  public static UploadPage getInstanceOfUploadPage(TestWebDriver testWebDriver) {
    if (instanceOfUploadPage == null) {
      instanceOfUploadPage = new UploadPage(testWebDriver);
    }
    return instanceOfUploadPage;
  }

  public static ApprovePage getInstanceOfApprovePage(TestWebDriver testWebDriver) {
    if (instanceOfApprovePage == null) {
      instanceOfApprovePage = new ApprovePage(testWebDriver);
    }
    return instanceOfApprovePage;
  }

  public static ConvertOrderPage getInstanceOfConvertOrderPage(TestWebDriver testWebDriver) {
    if (instanceOfConvertOrderPage == null) {
      instanceOfConvertOrderPage = new ConvertOrderPage(testWebDriver);
    }
    return instanceOfConvertOrderPage;
  }

  public static ViewOrdersPage getInstanceOfViewOrdersPage(TestWebDriver testWebDriver) {
    if (instanceOfViewOrdersPage == null) {
      instanceOfViewOrdersPage = new ViewOrdersPage(testWebDriver);
    }
    return instanceOfViewOrdersPage;
  }

  public static ConfigureShipmentPage getInstanceOfConfigureShipmentPage(TestWebDriver testWebDriver) {
    if (instanceOfConfigureShipmentPage == null) {
      instanceOfConfigureShipmentPage = new ConfigureShipmentPage(testWebDriver);
    }
    return instanceOfConfigureShipmentPage;
  }

  public static ConfigureBudgetPage getInstanceOfConfigureBudgetPage(TestWebDriver testWebDriver) {
    if (instanceOfConfigureBudgetPage == null) {
      instanceOfConfigureBudgetPage = new ConfigureBudgetPage(testWebDriver);
    }
    return instanceOfConfigureBudgetPage;
  }

  public static ConfigureEDIPage getInstanceOfConfigureEdiPage(TestWebDriver testWebDriver) {
    if (instanceOfConfigureEdiPage == null) {
      instanceOfConfigureEdiPage = new ConfigureEDIPage(testWebDriver);
    }
    return instanceOfConfigureEdiPage;
  }

  public static ManagePodPage getInstanceOfManagePodPage(TestWebDriver testWebDriver) {
    if (instanceOfManagePodPage == null) {
      instanceOfManagePodPage = new ManagePodPage(testWebDriver);
    }
    return instanceOfManagePodPage;
  }
}