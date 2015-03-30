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


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.thoughtworks.selenium.SeleneseTestBase.assertNotEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static java.util.Arrays.asList;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EUpload extends TestCaseHelper {

  LoginPage loginPage;
  UploadPage uploadPage;
  RolesPage rolesPage;

  @BeforeMethod(groups = {"admin"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    uploadPage = PageObjectFactory.getUploadPage(testWebDriver);
    rolesPage = PageObjectFactory.getRolesPage(testWebDriver);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadCSVFiles(String[] credentials) throws SQLException {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateToRolePage();
    assertTrue(rolesPage.isCreateNewRoleButtonDisplayed());
    List<String> userRoleList = asList("Create Requisition");
    rolesPage.createRole("User", "User", userRoleList, "Requisition");
    homePage.logout();

    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", "UPLOADS");
    loginPage.loginAs(credentials[0], credentials[1]);
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.verifyUploadPage();

    verifyValidUserUpload();
    verifyInValidUserUpload();

    String userName = "User123";
    String userId = "200";
    dbWrapper.alterUserID(userName, userId);
    dbWrapper.insertRoleAssignment(userName, "User");

    verifyInValidProductGroupUpload();
    verifyValidProductGroupUpload();

    verifyValidProductCategoryUpload();
    verifyInvalidProductCategoryUpload();

    verifyInValidProductUpload();
    verifyValidProductUpload();

    verifyInvalidProgramProductMappingUpload();
    verifyValidProgramProductMappingUpload();

    verifyInvalidProductPriceUpload();
    verifyValidProductPriceUpload();

    verifyInvalidGeographicZoneUpload();
    verifyValidGeographicZoneUpload();

    verifyInvalidFacilityUpload();
    verifyValidFacilityUpload();

    verifyInValidFacilityFTPDetailsUpload();
    verifyValidFacilityFTPDetailsUpload();

    verifyInvalidFacilityTypeToProductMappingUpload();
    verifyValidFacilityTypeToProductMappingUpload();
    dbWrapper.allocateFacilityToUser(userName, "F10");

    verifyInvalidProgramSupportedByFacilitiesUpload();
    verifyValidProgramSupportedByFacilitiesUpload();

    verifyInValidSupervisoryNodesUpload();
    verifyValidSupervisoryNodesUpload();

    verifyInValidRequisitionGroupUpload();
    verifyValidRequisitionGroupUpload();

    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");

    verifyInvalidRequisitionGroupProgramScheduleUpload();
    verifyValidRequisitionGroupProgramScheduleUpload();

    verifyInvalidRequisitionGroupMembersUpload();
    verifyValidRequisitionGroupMembersUpload();

    verifyInvalidSupplyLinesUpload();
    verifyValidSupplyLinesUpload();

    verifyInValidDeliveryZonesUpload();
    verifyValidDeliveryZonesUpload();

    verifyInValidDeliveryZonesProgramScheduleUpload();
    verifyValidDeliveryZonesProgramScheduleUpload();

    verifyInValidDeliveryZonesMembersUpload();
    verifyValidDeliveryZonesMembersUpload();

    verifyInValidDeliveryZonesWarehousesUpload();
    verifyValidDeliveryZonesWarehousesUpload();

    verifyValidVirtualFacilityUpload();

    String parentFacilityCode = "F10";
    String virtualFacilityCode = "V10";

    homePage.navigateUploads();

    dbWrapper.insertVirtualFacility(virtualFacilityCode, parentFacilityCode);
    uploadPage.uploadFacilities("QA_Parent_Facility_New_Geographic_Zone.csv");
    testWebDriver.sleep(2000);
    assertEquals(dbWrapper.getAttributeFromTable("facilities", "geographicZoneId", "code", parentFacilityCode),
      dbWrapper.getAttributeFromTable("geographic_zones", "id", "code", "Ngorongoro"));
    verifyGeographicZoneAndFacilityTypeForVirtualFacility(virtualFacilityCode, parentFacilityCode);

    uploadPage.uploadFacilities("QA_Parent_Facility_New_Type.csv");
    testWebDriver.sleep(2000);
    assertEquals(dbWrapper.getAttributeFromTable("facilities", "typeId", "code", parentFacilityCode),
      dbWrapper.getAttributeFromTable("facility_types", "id", "code", "warehouse"));
    verifyGeographicZoneAndFacilityTypeForVirtualFacility(virtualFacilityCode, parentFacilityCode);

    dbWrapper.updateFieldValue("facilities", "typeId", "5", "code", virtualFacilityCode);
    uploadPage.uploadFacilities("QA_Parent_Facility_New_Name.csv");
    assertEquals("Dispensary", dbWrapper.getAttributeFromTable("facilities", "name", "code", parentFacilityCode));
    assertNotEquals(dbWrapper.getAttributeFromTable("facilities", "name", "code", virtualFacilityCode),
      dbWrapper.getAttributeFromTable("facilities", "name", "code", parentFacilityCode));

    uploadPage.uploadProgramSupportedByFacilities("QA_program_supported.csv");
    testWebDriver.sleep(2000);
    List<Integer> listOfProgramsSupportedByParentFacility;
    listOfProgramsSupportedByParentFacility = dbWrapper.getAllProgramsOfFacility(parentFacilityCode);
    assertTrue(listOfProgramsSupportedByParentFacility.contains(new Integer(String.valueOf(dbWrapper.getAttributeFromTable("programs", "id", "code", "HIV")))));

    List<Integer> listOfProgramsSupportedByVirtualFacility;
    listOfProgramsSupportedByVirtualFacility = dbWrapper.getAllProgramsOfFacility(virtualFacilityCode);
    Set<Integer> setOfProgramsSupportedByParentFacility = new HashSet<>();
    setOfProgramsSupportedByParentFacility.addAll(listOfProgramsSupportedByParentFacility);
    Set<Integer> setOfProgramsSupportedByVirtualFacility = new HashSet<>();
    setOfProgramsSupportedByVirtualFacility.addAll(listOfProgramsSupportedByVirtualFacility);
    assertTrue(setOfProgramsSupportedByParentFacility.equals(setOfProgramsSupportedByVirtualFacility));
    assertEquals(listOfProgramsSupportedByParentFacility.size(), listOfProgramsSupportedByVirtualFacility.size());

    for (Integer programId : listOfProgramsSupportedByParentFacility) {
      assertEquals(dbWrapper.getProgramFieldForProgramIdAndFacilityCode(programId, parentFacilityCode, "active"), dbWrapper.getProgramFieldForProgramIdAndFacilityCode(programId, virtualFacilityCode, "active"));
      assertEquals(dbWrapper.getProgramStartDateForProgramIdAndFacilityCode(programId, parentFacilityCode), dbWrapper.getProgramStartDateForProgramIdAndFacilityCode(programId, virtualFacilityCode));
    }

    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Parent_Requisition_Program_Schedule.csv");
    assertEquals(dbWrapper.getRequisitionGroupId(parentFacilityCode), dbWrapper.getRequisitionGroupId(virtualFacilityCode));
  }

  private void verifyValidSupplyLinesUpload() {
    uploadPage.uploadSupplyLines("QA_Supply_Lines.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadSupplyLines("QA_Supply_Lines_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidSupplyLinesUpload() {
    uploadPage.uploadSupplyLines("QA_Supply_Lines_InvalidFacilityCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Facility code");

    uploadPage.uploadSupplyLines("QA_Supply_Lines_InvalidProgramCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid program code ");

    uploadPage.uploadSupplyLines("QA_Supply_Lines_DuplicateCombination_SN_Product_Program.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate entry for Supply Line found in Record No");

    uploadPage.uploadSupplyLines("QA_Supply_Lines_ParentNodeNotNull.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.validateSuccessMessageOnUploadScreen("File uploaded successfully. \"Number of records processed: 1\".");

    uploadPage.uploadSupplyLines("QA_Supply_Lines_Redundant_Warehouse.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Redundant warehouse specified in Record No");
  }

  private void verifyValidRequisitionGroupMembersUpload() {
    uploadPage.uploadRequisitionGroupMembers("QA_Requisition_Group_Members.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadRequisitionGroupMembers("QA_Requisition_Group_Members_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidRequisitionGroupMembersUpload() {
    uploadPage.uploadRequisitionGroupMembers("QA_Requisition_Group_Members_InvalidCombination_RG_FacilityCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Requisition Group Member found in Record No");

    uploadPage.uploadRequisitionGroupMembers("QA_Requisition_Group_Members_InvalidFacilityCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Facility code in Record No");

    uploadPage.uploadRequisitionGroupMembers("QA_Requisition_Group_Members_InvalidRG.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Requisition Group does not exist in Record No");

    uploadPage.uploadRequisitionGroupMembers("QA_Requisition_Group_Members_FacilityCodeAssignedToRGWithOneProgramInCommon.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Facility F10 is already assigned to Requisition Group rg1 running same program ess_meds in Record No");

    uploadPage.uploadRequisitionGroupMembers("QA_Requisition_Group_Members_Subsequent_Duplicate.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Requisition Group Member found in Record No");
  }

  private void verifyValidRequisitionGroupProgramScheduleUpload() {
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidRequisitionGroupProgramScheduleUpload() {
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_InvalidCombination_RG_ProgramCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Requisition Group Code And Program Code Combination found in Record No");

    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_InvalidProgramCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid program code in Record No");

    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_InvalidScheduleCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Schedule Code Does Not Exist in Record No");

    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_DDFalse_DropoffFacilityNull.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("drop off facility not defined for programs with direct delivery as false in record no.");

    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_DropoffFacilityCodeNotPresent.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("drop off facility code is not present in record no.");

    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_Subsequent_Duplicate.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Requisition Group Code And Program Code Combination found in Record No");

    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_PUSH_Program.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyValidRequisitionGroupUpload() {
    uploadPage.uploadRequisitionGroup("QA_Requisition_Groups.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadRequisitionGroup("QA_Requisition_Groups_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInValidRequisitionGroupUpload() {
    uploadPage.uploadRequisitionGroup("QA_Requisition_Groups_DuplicateRequisitionGroup.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Requisition Group Code found in Record No");

    uploadPage.uploadRequisitionGroup("QA_Requisition_Groups_InvalidSupervisoryNode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Supervisory Node Code in Record No");
  }

  private void verifyValidSupervisoryNodesUpload() {
    uploadPage.uploadSupervisoryNodes("QA_Supervisory_Nodes.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadSupervisoryNodes("QA_Supervisory_Nodes_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInValidSupervisoryNodesUpload() {
    uploadPage.uploadSupervisoryNodes("QA_Supervisory_Nodes_InvalidFacilityCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Facility code in Record No");

    uploadPage.uploadSupervisoryNodes("QA_Supervisory_Nodes_InvalidParentNode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Supervisory Node Parent does not exist in Record No");

    uploadPage.uploadSupervisoryNodes("QA_Supervisory_Nodes_DuplicateSupervisoryNode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Supervisory Node found in Record No");
  }

  private void verifyValidProgramSupportedByFacilitiesUpload() {
    uploadPage.uploadProgramSupportedByFacilities("QA_program_supported.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramSupportedByFacilities("QA_program_supported_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidProgramSupportedByFacilitiesUpload() {
    uploadPage.uploadProgramSupportedByFacilities("QA_program_supported_Invalid_FacilityCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Facility code");

    uploadPage.uploadProgramSupportedByFacilities("QA_program_supported_Invalid_ProgramCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid program code ");
  }

  private void verifyValidFacilityTypeToProductMappingUpload() throws SQLException {
    uploadPage.uploadFacilityTypeToProductMapping("QA_Facility_Type_To_Product_Mapping.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadFacilityTypeToProductMapping("QA_Facility_Type_To_Product_Mapping_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidFacilityTypeToProductMappingUpload() {
    uploadPage.uploadFacilityTypeToProductMapping("QA_Facility_Type_To_Product_Mapping_Invalid_Combination.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate facility approved product. in Record No");

    uploadPage.uploadFacilityTypeToProductMapping("QA_Facility_Type_To_Product_Mapping_Invalid_FacilityType.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Facility Type in Record No");

    uploadPage.uploadFacilityTypeToProductMapping("QA_Facility_Type_To_Product_Mapping_Invalid_ProductCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid product code");

    uploadPage.uploadFacilityTypeToProductMapping("QA_Facility_Type_To_Product_Mapping_Invalid_ProgramCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid program code");

    uploadPage.uploadFacilityTypeToProductMapping("QA_Facility_Type_To_Product_Mapping_Invalid_Program_Product_Combination.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Product and Program combination");
  }

  private void verifyValidFacilityUpload() {
    uploadPage.uploadFacilities("QA_facilities.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadFacilities("QA_facilities_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidFacilityUpload() {
    uploadPage.uploadFacilities("QA_facilities_Lowest_Code.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Geographic Zone Code must be at the lowest administrative level in your hierarchy in Record No");

    uploadPage.uploadFacilities("QA_facilities_Duplicate_Code.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Facility Code in Record No");
  }

  private void verifyValidGeographicZoneUpload() {
    uploadPage.uploadGeographicZone("QA_Geographic_Data.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadGeographicZone("QA_Geographic_Data_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadGeographicZone("QA_Geographic_Data_Population_Lat_Long.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidGeographicZoneUpload() {
    uploadPage.uploadGeographicZone("QA_Geographic_Data_Invalid.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Missing Mandatory data in field : Geographic Zone Code of Record No");

    uploadPage.uploadGeographicZone("QA_Geographic_Data_Duplicate.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Geographic Zone Code in Record No");

    uploadPage.uploadGeographicZone("QA_Geographic_Data_Invalid_Code.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Geographic Zone Parent Code in Record No");

    uploadPage.uploadGeographicZone("QA_Geographic_Data_Invalid_Lat.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Incorrect data length in Record No");

    uploadPage.uploadGeographicZone("QA_Geographic_Data_Invalid_Long.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Incorrect data length in Record No");

    uploadPage.uploadGeographicZone("QA_Geographic_Data_Invalid_Parent_Same_Level.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Hierarchy in Record No");

    uploadPage.uploadGeographicZone("QA_Geographic_Data_Invalid_Parent_Below_Level.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Hierarchy in Record No");

    uploadPage.uploadGeographicZone("QA_Geographic_Data_Invalid_No_Parent.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Hierarchy in Record No");
  }

  private void verifyValidProductPriceUpload() {
    uploadPage.uploadProgramProductPrice("QA_Product_Price.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductPrice("QA_Product_Price_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidProductPriceUpload() {
    uploadPage.uploadProgramProductPrice("QA_Product_Price_DuplicateCombination_ProductCode_ProgramCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Program Product in Record No");

    uploadPage.uploadProgramProductPrice("QA_Product_Price_Invalid_Program_Product_Combination.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Product and Program combination");

    uploadPage.uploadProgramProductPrice("QA_Product_Price_InvalidPrice.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Price per pack in Record No");
  }

  private void verifyValidProgramProductMappingUpload() {
    uploadPage.uploadProgramProductMapping("QA_program_product.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidProgramProductMappingUpload() {
    uploadPage.uploadProgramProductMapping("QA_program_product_Invalid_ProductCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid product code");

    uploadPage.uploadProgramProductMapping("QA_program_product_Invalid_ProgramCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid program code");

    uploadPage.uploadProgramProductMapping("QA_program_product_missing_productCategory.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Missing Mandatory columns in upload file: [Product Category]");

    uploadPage.uploadProgramProductMapping("QA_program_product_Invalid_ProductCategory.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid reference data Product Category");
  }

  private void verifyValidUserUpload() throws SQLException {
    String tableName = "users";
    uploadPage.uploadUsers("QA_Users.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "2");
    assertEquals(dbWrapper.getAttributeFromTable("users", "restrictLogin", "userName", "User123"), "f");

    uploadPage.uploadUsers("QA_Users_Valid_withoutPassword.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "3");
    assertEquals(dbWrapper.getAttributeFromTable("users", "restrictLogin", "userName", "User123"), "f");


    uploadPage.uploadUsers("QA_Users_Others.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    assertEquals(dbWrapper.getAttributeFromTable("users", "restrictLogin", "userName", "User1234"), "f");
    assertEquals(dbWrapper.getAttributeFromTable("users", "restrictLogin", "userName", "User1235"), "t");
    assertEquals(dbWrapper.getAttributeFromTable("users", "restrictLogin", "userName", "User1236"), "f");
  }

  private void verifyInValidUserUpload() throws SQLException {
    String tableName = "users";
    uploadPage.uploadUsers("QA_Users_Duplicate_Email.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate email address in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "6");

    uploadPage.uploadUsers("QA_Users_Duplicate_EmployeeId.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate employee id in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "6");

    uploadPage.uploadUsers("QA_Users_Duplicate_UserName.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate User Name in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "6");

    uploadPage.uploadUsers("QA_Users_Invalid_Supervisor.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Supervisor User Name not present in the system in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "6");

    uploadPage.uploadUsers("QA_Users_Subsequent_Duplicate_Username.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate User Name in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "6");

    uploadPage.uploadUsers("QA_Users_Subsequent_InvalidCombination.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate User Name in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "6");
  }

  private void verifyValidProductCategoryUpload() throws SQLException {
    String tableName = "product_categories";
    uploadPage.uploadProductCategory("QA_Productcategoryupload.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "18");

    uploadPage.uploadProductCategory("QA_Productcategoryupload_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "20");
  }

  private void verifyInvalidProductCategoryUpload() throws SQLException {
    String tableName = "product_categories";
    uploadPage.uploadProductCategory("QA_ProductCategoryUpload_DuplicateCategoryCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Product Category in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "20");

    uploadPage.uploadProductCategory("QA_Productcategoryupload_Subsequent_Duplicate.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Product Category in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "20");
  }

  private void verifyInValidProductUpload() throws SQLException {
    String tableName = "products";
    uploadPage.uploadProducts("QA_products_Duplicate_Code.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Product Code in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "0");

    uploadPage.uploadProducts("QA_Products_Invalid_ProductGroupCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid reference data Product Group in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "0");

    uploadPage.uploadProducts("QA_Products_Invalid_Packsize_Less_Than_Zero.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Pack size in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "0");

    uploadPage.uploadProducts("QA_Products_Invalid_PackSize_Zero.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Pack size in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "0");

    uploadPage.uploadProducts("QA_products_Invalid_missingDosesPerDispensingUnit.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Missing Mandatory data in field : Doses Per Dispensing Unit of Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "0");

    uploadPage.uploadProducts("QA_products_Invalid_missingDispensingUnits.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Missing Mandatory data in field : Dispensing Units of Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "0");

  }

  private void verifyValidProductUpload() throws SQLException {
    String tableName = "products";
    uploadPage.uploadProducts("QA_products.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "1");

    uploadPage.uploadProducts("QA_products_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "2");
  }

  private void verifyInValidProductGroupUpload() throws SQLException {
    uploadPage.uploadProductGroupsScenarios("QA_Product_Group_Duplicate.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();

    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Product Group Code in Record No");
  }

  private void verifyValidProductGroupUpload() throws SQLException {
    uploadPage.uploadProductGroupsScenarios("QA_product_group.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProductGroupsScenarios("QA_Product_Group_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInValidDeliveryZonesUpload() throws SQLException {
    uploadPage.uploadDeliveryZones("QA_Delivery_Zones_Duplicate.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate delivery zone Code found");

    uploadPage.uploadDeliveryZones("QA_Blank.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("File is empty");
  }

  private void verifyValidDeliveryZonesUpload() throws SQLException {
    uploadPage.uploadDeliveryZones("QA_Delivery_Zones.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadDeliveryZones("QA_Delivery_Zones_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInValidDeliveryZonesProgramScheduleUpload() throws SQLException {
    uploadPage.uploadDeliveryZoneProgramSchedule("QA_Delivery_Zone_Program_Schedule_Duplicate.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Delivery zone code and Program code combination found in Record No");

    uploadPage.uploadDeliveryZoneProgramSchedule("QA_Delivery_Zone_Program_Schedule_Pull_Program.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Program does not support push mechanism");

    uploadPage.uploadDeliveryZoneProgramSchedule("QA_Delivery_Zone_Program_Schedule_Invalid_Delivery_Zone.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Delivery zone code");

    uploadPage.uploadDeliveryZoneProgramSchedule("QA_Delivery_Zone_Program_Schedule_Invalid_Schedule.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid schedule code");
  }

  private void verifyValidDeliveryZonesProgramScheduleUpload() throws SQLException {
    uploadPage.uploadDeliveryZoneProgramSchedule("QA_Delivery_Zone_Program_Schedule.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadDeliveryZoneProgramSchedule("QA_Delivery_Zone_Program_Schedule_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInValidDeliveryZonesMembersUpload() throws SQLException {
    uploadPage.uploadDeliveryZoneMembers("QA_Delivery_Zone_Members_Duplicate.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Delivery Zone code and Member code combination found");

    uploadPage.uploadDeliveryZoneMembers("QA_Delivery_Zone_Members_Duplicate_Facility.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Facility Code exists for the same program in multiple Delivery Zones");

    uploadPage.uploadDeliveryZoneMembers("QA_Delivery_Zone_Members_Invalid_Member.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Facility code");

    uploadPage.uploadDeliveryZoneMembers("QA_Delivery_Zone_Members_Delivery_Zone_Without_Program.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("No Program(s) mapped for Delivery Zones");
  }

  private void verifyValidDeliveryZonesMembersUpload() throws SQLException {
    uploadPage.uploadDeliveryZoneMembers("QA_Delivery_Zone_Members.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadDeliveryZoneMembers("QA_Delivery_Zone_Members_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInValidDeliveryZonesWarehousesUpload() throws SQLException {
    uploadPage.uploadDeliveryZoneWarehouses("QA_Delivery_Zone_Warehouses_Invalid_Delivery_Zone.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Delivery zone code");

    uploadPage.uploadDeliveryZoneWarehouses("QA_Delivery_Zone_Warehouses_Invalid_Warehouse.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Warehouse code");
  }

  private void verifyValidDeliveryZonesWarehousesUpload() throws SQLException {
    uploadPage.uploadDeliveryZoneWarehouses("QA_Delivery_zone_warehouses.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadDeliveryZoneWarehouses("QA_Delivery_zone_warehouses_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInValidFacilityFTPDetailsUpload() throws SQLException {
    uploadPage.uploadFacilityFTPDetails("QA_Blank.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("File is empty");

    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Invalid_FacilityCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Facility code");

    uploadPage.uploadFacilityFTPDetails("1QA_Facility_FTP_Details_Duplicate.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Facility Code in Record No");

    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Missing_Mandatory_Field_Facility_Code.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Missing Mandatory data in field : Facility Code of Record No");

    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Missing_Mandatory_Field_Host.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Missing Mandatory data in field : Host of Record No");

    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Missing_Mandatory_Field_Password.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Missing Mandatory data in field : Password of Record No");

    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Missing_Mandatory_Field_Port.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Missing Mandatory data in field : Port of Record No");

    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Missing_Mandatory_Field_Username.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Missing Mandatory data in field : Username of Record No");

    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Missing_Mandatory_Field_Path.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Missing Mandatory data in field : Local Folder Path of Record No");

    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Incorrect_Data_length.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Incorrect data length");

    uploadPage.uploadFacilityFTPDetails("UnassignedRequisitionGroupReport.jrxml");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Incorrect file format. Please upload Facility FTP details data as a .csv file");

    uploadPage.uploadFacilityFTPDetails("QA_Delivery_Zone_Members.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Headers in upload file");
  }

  private void verifyValidFacilityFTPDetailsUpload() throws SQLException {
    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.validateSuccessMessageOnUploadScreen("File uploaded successfully. \"Number of records processed: 1\".");
  }

  private void verifyValidVirtualFacilityUpload() throws SQLException {
    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.validateSuccessMessageOnUploadScreen("File uploaded successfully. \"Number of records processed: 1\".");
  }

  public void verifyGeographicZoneAndFacilityTypeForVirtualFacility(String virtualFacilityCode, String parentFacilityCode) throws SQLException {
    assertEquals(dbWrapper.getAttributeFromTable("facilities", "geographicZoneId", "code", virtualFacilityCode), dbWrapper.getAttributeFromTable("facilities", "geographicZoneId", "code", parentFacilityCode));
    assertEquals(dbWrapper.getAttributeFromTable("facilities", "typeId", "code", virtualFacilityCode), dbWrapper.getAttributeFromTable("facilities", "typeId", "code", parentFacilityCode));
  }

  @AfterMethod(groups = {"admin"})
  public void tearDown() throws SQLException {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.insertAllAdminRightsAsSeedData();
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {new String[]{"Admin123", "Admin123"}}
    };
  }
}
