/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.pageobjects;

import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.edi.*;

public class PageObjectFactory {
  private static FacilityPage instanceOfFacilityPage;
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
  private static ForgotPasswordPage instanceOfForgotPasswordPage;
  private static UploadPage instanceOfUploadPage;
  private static ApprovePage instanceOfApprovePage;
  private static ConvertOrderPage instanceOfConvertOrderPage;
  private static ViewOrdersPage instanceOfViewOrdersPage;
  private static ConfigureShipmentPage instanceOfConfigureShipmentPage;
  private static ConfigureBudgetPage instanceOfConfigureBudgetPage;
  private static ConfigureSystemSettingsPage instanceOfConfigureSystemSettingsPage;
  private static ManagePodPage instanceOfManagePodPage;
  private static AdultCoveragePage instanceOfAdultCoveragePage;
  private static ReportPage instanceOfReportPage;
  private static ManageSchedulePage instanceOfManageSchedulePage;
  private static TemplateConfigPage instanceOfTemplateConfigPage;
  private static GeographicZonePage instanceOfGeographicZonePage;
  private static ConfigureOrderNumberPage instanceOfConfigureOrderNumberPage;
  private static PeriodsPage instanceOfPeriodsPage;
  private static SupervisoryNodesPage instanceOfSupervisoryNodesPage;
  private static RequisitionGroupPage instanceOfRequisitionGroupPage;
  private static RequisitionPage instanceOfRequisitionPage;
  private static SupplyLinePage instanceOfSupplyLinePage;
  private static FacilityApprovedProductPage instanceOfFacilityApprovedProductPage;
  private static ProductPage instanceOfProductPage;

  public static FacilityPage getFacilityPage(TestWebDriver testWebDriver) {
    if (instanceOfFacilityPage == null) {
      instanceOfFacilityPage = new FacilityPage(testWebDriver);
    }
    return instanceOfFacilityPage;
  }

  public static GeographicZonePage getGeographicZonePage(TestWebDriver testWebDriver) {
    if (instanceOfGeographicZonePage == null) {
      instanceOfGeographicZonePage = new GeographicZonePage(testWebDriver);
    }
    return instanceOfGeographicZonePage;
  }

  public static DistributionPage getDistributionPage(TestWebDriver testWebDriver) {
    if (instanceOfDistributionPage == null) {
      instanceOfDistributionPage = new DistributionPage(testWebDriver);
    }
    return instanceOfDistributionPage;
  }

  public static FacilityListPage getFacilityListPage(TestWebDriver testWebDriver) {
    if (instanceOfFacilityListPage == null) {
      instanceOfFacilityListPage = new FacilityListPage(testWebDriver);
    }
    return instanceOfFacilityListPage;
  }

  public static RefrigeratorPage getRefrigeratorPage(TestWebDriver testWebDriver) {
    if (instanceOfRefrigeratorPage == null) {
      instanceOfRefrigeratorPage = new RefrigeratorPage(testWebDriver);
    }
    return instanceOfRefrigeratorPage;
  }

  public static VisitInformationPage getVisitInformationPage(TestWebDriver testWebDriver) {
    if (instanceOfVisitInformationPage == null) {
      instanceOfVisitInformationPage = new VisitInformationPage(testWebDriver);
    }
    return instanceOfVisitInformationPage;
  }

  public static FullCoveragePage getFullCoveragePage(TestWebDriver testWebDriver) {
    if (instanceOfFullCoveragePage == null) {
      instanceOfFullCoveragePage = new FullCoveragePage(testWebDriver);
    }
    return instanceOfFullCoveragePage;
  }

  public static EPIUsePage getEpiUsePage(TestWebDriver testWebDriver) {
    if (instanceOfEpiUsePage == null) {
      instanceOfEpiUsePage = new EPIUsePage(testWebDriver);
    }
    return instanceOfEpiUsePage;
  }

  public static EpiInventoryPage getEpiInventoryPage(TestWebDriver testWebDriver) {
    if (instanceOfEpiInventoryPage == null) {
      instanceOfEpiInventoryPage = new EpiInventoryPage(testWebDriver);
    }
    return instanceOfEpiInventoryPage;
  }

  public static ChildCoveragePage getChildCoveragePage(TestWebDriver testWebDriver) {
    if (instanceOfChildCoveragePage == null) {
      instanceOfChildCoveragePage = new ChildCoveragePage(testWebDriver);
    }
    return instanceOfChildCoveragePage;
  }

  public static WarehouseLoadAmountPage getWarehouseLoadAmountPage(TestWebDriver testWebDriver) {
    if (instanceOfWarehouseLoadAmountPage == null) {
      instanceOfWarehouseLoadAmountPage = new WarehouseLoadAmountPage(testWebDriver);
    }
    return instanceOfWarehouseLoadAmountPage;
  }

  public static ProgramProductISAPage getProgramProductIsaPage(TestWebDriver testWebDriver) {
    if (instanceOfProgramProductISAPage == null) {
      instanceOfProgramProductISAPage = new ProgramProductISAPage(testWebDriver);
    }
    return instanceOfProgramProductISAPage;
  }

  public static ConfigureOrderPage getConfigureOrderPage(TestWebDriver testWebDriver) {
    if (instanceOfConfigureOrderPage == null) {
      instanceOfConfigureOrderPage = new ConfigureOrderPage(testWebDriver);
    }
    return instanceOfConfigureOrderPage;
  }

  public static RegimenTemplateConfigPage getRegimenTemplateConfigPage(TestWebDriver testWebDriver) {
    if (instanceOfRegimenTemplateConfigPage == null) {
      instanceOfRegimenTemplateConfigPage = new RegimenTemplateConfigPage(testWebDriver);
    }
    return instanceOfRegimenTemplateConfigPage;
  }

  public static LoginPage getLoginPage(TestWebDriver testWebDriver, String baseUrlGlobal) {
    if (instanceOfLoginPage == null) {
      instanceOfLoginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    } else {
      testWebDriver.setBaseURL(baseUrlGlobal);
    }
    return instanceOfLoginPage;
  }

  public static HomePage getHomePage(TestWebDriver testWebDriver) {
    if (instanceOfHomePage == null) {
      instanceOfHomePage = new HomePage(testWebDriver);
    }
    return instanceOfHomePage;
  }

  public static InitiateRnRPage getInitiateRnRPage(TestWebDriver testWebDriver) {
    if (instanceOfInitiateRnRPage == null) {
      instanceOfInitiateRnRPage = new InitiateRnRPage(testWebDriver);
    }
    return instanceOfInitiateRnRPage;
  }

  public static ViewRequisitionPage getViewRequisitionPage(TestWebDriver testWebDriver) {
    if (instanceOfViewRequisitionPage == null) {
      instanceOfViewRequisitionPage = new ViewRequisitionPage(testWebDriver);
    }
    return instanceOfViewRequisitionPage;
  }

  public static UpdatePodPage getUpdatePodPage(TestWebDriver testWebDriver) {
    if (instanceOfUpdatePodPage == null) {
      instanceOfUpdatePodPage = new UpdatePodPage(testWebDriver);
    }
    return instanceOfUpdatePodPage;
  }

  public static UserPage getUserPage(TestWebDriver testWebDriver) {
    if (instanceOfUserPage == null) {
      instanceOfUserPage = new UserPage(testWebDriver);
    }
    return instanceOfUserPage;
  }

  public static RolesPage getRolesPage(TestWebDriver testWebDriver) {
    if (instanceOfRolesPage == null) {
      instanceOfRolesPage = new RolesPage(testWebDriver);
    }
    return instanceOfRolesPage;
  }

  public static ForgotPasswordPage getForgotPasswordPage(TestWebDriver testWebDriver) {
    if (instanceOfForgotPasswordPage == null) {
      instanceOfForgotPasswordPage = new ForgotPasswordPage(testWebDriver);
    }
    return instanceOfForgotPasswordPage;
  }

  public static UploadPage getUploadPage(TestWebDriver testWebDriver) {
    if (instanceOfUploadPage == null) {
      instanceOfUploadPage = new UploadPage(testWebDriver);
    }
    return instanceOfUploadPage;
  }

  public static ApprovePage getApprovePage(TestWebDriver testWebDriver) {
    if (instanceOfApprovePage == null) {
      instanceOfApprovePage = new ApprovePage(testWebDriver);
    }
    return instanceOfApprovePage;
  }

  public static ConvertOrderPage getConvertOrderPage(TestWebDriver testWebDriver) {
    if (instanceOfConvertOrderPage == null) {
      instanceOfConvertOrderPage = new ConvertOrderPage(testWebDriver);
    }
    return instanceOfConvertOrderPage;
  }

  public static ViewOrdersPage getViewOrdersPage(TestWebDriver testWebDriver) {
    if (instanceOfViewOrdersPage == null) {
      instanceOfViewOrdersPage = new ViewOrdersPage(testWebDriver);
    }
    return instanceOfViewOrdersPage;
  }

  public static ConfigureShipmentPage getConfigureShipmentPage(TestWebDriver testWebDriver) {
    if (instanceOfConfigureShipmentPage == null) {
      instanceOfConfigureShipmentPage = new ConfigureShipmentPage(testWebDriver);
    }
    return instanceOfConfigureShipmentPage;
  }

  public static ConfigureBudgetPage getConfigureBudgetPage(TestWebDriver testWebDriver) {
    if (instanceOfConfigureBudgetPage == null) {
      instanceOfConfigureBudgetPage = new ConfigureBudgetPage(testWebDriver);
    }
    return instanceOfConfigureBudgetPage;
  }

  public static ConfigureOrderNumberPage getConfigureOrderNumberPage(TestWebDriver testWebDriver) {
    if (instanceOfConfigureOrderNumberPage == null) {
      instanceOfConfigureOrderNumberPage = new ConfigureOrderNumberPage(testWebDriver);
    }
    return instanceOfConfigureOrderNumberPage;
  }

  public static ConfigureSystemSettingsPage getConfigureSystemSettingsPage(TestWebDriver testWebDriver) {
    if (instanceOfConfigureSystemSettingsPage == null) {
      instanceOfConfigureSystemSettingsPage = new ConfigureSystemSettingsPage(testWebDriver);
    }
    return instanceOfConfigureSystemSettingsPage;
  }

  public static ManagePodPage getManagePodPage(TestWebDriver testWebDriver) {
    if (instanceOfManagePodPage == null) {
      instanceOfManagePodPage = new ManagePodPage(testWebDriver);
    }
    return instanceOfManagePodPage;
  }

  public static AdultCoveragePage getAdultCoveragePage(TestWebDriver testWebDriver) {
    if (instanceOfAdultCoveragePage == null) {
      instanceOfAdultCoveragePage = new AdultCoveragePage(testWebDriver);
    }
    return instanceOfAdultCoveragePage;
  }

  public static ReportPage getReportPage(TestWebDriver testWebDriver) {
    if (instanceOfReportPage == null) {
      instanceOfReportPage = new ReportPage(testWebDriver);
    }
    return instanceOfReportPage;
  }

  public static ManageSchedulePage getManageSchedulePage(TestWebDriver testWebDriver) {
    if (instanceOfManageSchedulePage == null) {
      instanceOfManageSchedulePage = new ManageSchedulePage(testWebDriver);
    }
    return instanceOfManageSchedulePage;
  }

  public static TemplateConfigPage getTemplateConfigPage(TestWebDriver testWebDriver) {
    if (instanceOfTemplateConfigPage == null) {
      instanceOfTemplateConfigPage = new TemplateConfigPage(testWebDriver);
    }
    return instanceOfTemplateConfigPage;
  }

  public static PeriodsPage getPeriodsPage(TestWebDriver testWebDriver) {
    if (instanceOfPeriodsPage == null) {
      instanceOfPeriodsPage = new PeriodsPage(testWebDriver);
    }
    return instanceOfPeriodsPage;
  }

  public static SupervisoryNodesPage getSupervisoryNodesPage(TestWebDriver testWebDriver) {
    if (instanceOfSupervisoryNodesPage == null) {
      instanceOfSupervisoryNodesPage = new SupervisoryNodesPage(testWebDriver);
    }
    return instanceOfSupervisoryNodesPage;
  }

  public static RequisitionGroupPage getRequisitionGroupPage(TestWebDriver testWebDriver) {
    if (instanceOfRequisitionGroupPage == null) {
      instanceOfRequisitionGroupPage = new RequisitionGroupPage(testWebDriver);
    }
    return instanceOfRequisitionGroupPage;
  }

  public static RequisitionPage getRequisitionPage(TestWebDriver testWebDriver) {
    if (instanceOfRequisitionPage == null) {
      instanceOfRequisitionPage = new RequisitionPage(testWebDriver);
    }
    return instanceOfRequisitionPage;
  }

  public static SupplyLinePage getSupplyLinePage(TestWebDriver testWebDriver) {
    if (instanceOfSupplyLinePage == null) {
      instanceOfSupplyLinePage = new SupplyLinePage(testWebDriver);
    }
    return instanceOfSupplyLinePage;
  }

  public static FacilityApprovedProductPage getFacilityApprovedProductPage(TestWebDriver testWebDriver) {
    if (instanceOfFacilityApprovedProductPage == null) {
      instanceOfFacilityApprovedProductPage = new FacilityApprovedProductPage(testWebDriver);
    }
    return instanceOfFacilityApprovedProductPage;
  }

  public static ProductPage getProductPage(TestWebDriver testWebDriver) {
    if (instanceOfProductPage == null) {
      instanceOfProductPage = new ProductPage(testWebDriver);
    }
    return instanceOfProductPage;
  }
}