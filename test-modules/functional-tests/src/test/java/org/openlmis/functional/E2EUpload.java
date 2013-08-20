/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http:mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.RolesPage;
import org.openlmis.pageobjects.UploadPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EUpload extends TestCaseHelper {

  @BeforeMethod(groups = {"functional"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadCSVFiles(String[] credentials) throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateRoleAssignments();
    List<String> userRoleList = new ArrayList<String>();
    userRoleList.add("Create Requisition");

    rolesPage.createRoleWithSuccessMessageExpected("User", "User", userRoleList, true);

    UploadPage uploadPage = homePage.navigateUploads();
    verifyValidUserUpload(uploadPage);
    verifyInValidUserUpload(uploadPage);


    String userName = "User123";
    String userId = "200";
    dbWrapper.alterUserID(userName, userId);
    dbWrapper.insertRoleAssignment(userId, "User");

    verifyInValidProductGroupUpload(uploadPage);
    verifyValidProductGroupUpload(uploadPage);

    verifyValidProductCategoryUpload(uploadPage);
    verifyInvalidProductCategoryUpload(uploadPage);

    verifyInValidProductUpload(uploadPage);
    verifyValidProductUpload(uploadPage);

    verifyInvalidProgramProductMappingUpload(uploadPage);
    verifyValidProgramProductMappingUpload(uploadPage);

    verifyInvalidProductPriceUpload(uploadPage);
    verifyValidProductPriceUpload(uploadPage);

    verifyInvalidGeographicZoneUpload(uploadPage);
    verifyValidGeographicZoneUpload(uploadPage);

    verifyInvalidFacilityUpload(uploadPage);
    verifyValidFacilityUpload(uploadPage);

    verifyInValidFacilityFTPDetailsUpload(uploadPage);
    verifyValidFacilityFTPDetailsUpload(uploadPage);

    verifyInvalidFacilityTypeToProductMappingUpload(uploadPage);
    verifyValidFacilityTypeToProductMappingUpload(uploadPage);
    dbWrapper.allocateFacilityToUser(userId, "F10");

    verifyInvalidProgramSupportedByFacilitiesUpload(uploadPage);
    verifyValidProgramSupportedByFacilitiesUpload(uploadPage);

    verifyInValidSupervisoryNodesUpload(uploadPage);
    verifyValidSupervisoryNodesUpload(uploadPage);

    verifyInValidRequisitionGroupUpload(uploadPage);
    verifyValidRequisitionGroupUpload(uploadPage);

    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");

    verifyInvalidRequisitionGroupProgramScheduleUpload(uploadPage);
    verifyValidRequisitionGroupProgramScheduleUpload(uploadPage);

    verifyInvalidRequisitionGroupMembersUpload(uploadPage);
    verifyValidRequisitionGroupMembersUpload(uploadPage);

    verifyInvalidSupplyLinesUpload(uploadPage);
    verifyValidSupplyLinesUpload(uploadPage);

    verifyInValidDeliveryZonesUpload(uploadPage);
    verifyValidDeliveryZonesUpload(uploadPage);

    verifyInValidDeliveryZonesProgramScheduleUpload(uploadPage);
    verifyValidDeliveryZonesProgramScheduleUpload(uploadPage);

    verifyInValidDeliveryZonesMembersUpload(uploadPage);
    verifyValidDeliveryZonesMembersUpload(uploadPage);

    verifyInValidDeliveryZonesWarehousesUpload(uploadPage);
    verifyValidDeliveryZonesWarehousesUpload(uploadPage);

  }

  private void verifyValidSupplyLinesUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadSupplyLines("QA_Supply_Lines.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadSupplyLines("QA_Supply_Lines_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidSupplyLinesUpload(UploadPage uploadPage) throws FileNotFoundException {
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
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Supervising Node is not the Top node in Record No");
  }

  private void verifyValidRequisitionGroupMembersUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadRequisitionGroupMembers("QA_Requisition_Group_Members.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadRequisitionGroupMembers("QA_Requisition_Group_Members_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidRequisitionGroupMembersUpload(UploadPage uploadPage) throws FileNotFoundException {
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
    uploadPage.validateErrorMessageOnUploadScreen("Facility F10 is already assigned to Requisition Group rg1 running same program ESS_MEDS in Record No");
    uploadPage.uploadRequisitionGroupMembers("QA_Requisition_Group_Members_Subsequent_Duplicate.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Requisition Group Member found in Record No");

  }

  private void verifyValidRequisitionGroupProgramScheduleUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidRequisitionGroupProgramScheduleUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_InvalidCombination_RG_ProgramCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Requisition Group Code And Program Code Combination found in Record No");
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_InvalidProgramCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid program code in Record No");
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_InvalidScheduleCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Schedule Code Does Not Exist in Record No");
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_DDTrue_DropoffFacilityNotNull.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Incorrect combination of Direct Delivery and Drop off Facility in Record No");
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_DDFalse_DropoffFacilityNull.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Drop off facility code not defined in Record No");
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_DropoffFacilityCodeNotPresent.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Incorrect combination of Direct Delivery and Drop off Facility in Record No");
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_Subsequent_Duplicate.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Requisition Group Code And Program Code Combination found in Record No");
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_PUSH_Program.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Program type not supported for requisitions in Record No");

  }

  private void verifyValidRequisitionGroupUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadRequisitionGroup("QA_Requisition_Groups.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadRequisitionGroup("QA_Requisition_Groups_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInValidRequisitionGroupUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadRequisitionGroup("QA_Requisition_Groups_DuplicateRequisitionGroup.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Requisition Group Code found in Record No");
    uploadPage.uploadRequisitionGroup("QA_Requisition_Groups_InvalidSupervisoryNode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Supervisory Node Code in Record No");
  }

  private void verifyValidSupervisoryNodesUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadSupervisoryNodes("QA_Supervisory_Nodes.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadSupervisoryNodes("QA_Supervisory_Nodes_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInValidSupervisoryNodesUpload(UploadPage uploadPage) throws FileNotFoundException {
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

  private void verifyValidProgramSupportedByFacilitiesUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadProgramSupportedByFacilities("QA_program_supported.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadProgramSupportedByFacilities("QA_program_supported_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidProgramSupportedByFacilitiesUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadProgramSupportedByFacilitiesInvalidScenarios("QA_program_supported_Invalid_FacilityCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Facility code");
    uploadPage.uploadProgramSupportedByFacilitiesInvalidScenarios("QA_program_supported_Invalid_ProgramCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid program code ");
  }

  private void verifyValidFacilityTypeToProductMappingUpload(UploadPage uploadPage) throws IOException, SQLException {
    uploadPage.uploadFacilityTypeToProductMapping("QA_Facility_Type_To_Product_Mapping.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadFacilityTypeToProductMapping("QA_Facility_Type_To_Product_Mapping_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidFacilityTypeToProductMappingUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadFacilityTypeToProductMappingInvalidScenarios("QA_Facility_Type_To_Product_Mapping_Invalid_Combination.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate facility approved product. in Record No");
    uploadPage.uploadFacilityTypeToProductMappingInvalidScenarios("QA_Facility_Type_To_Product_Mapping_Invalid_FacilityType.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Facility Type in Record No");
    uploadPage.uploadFacilityTypeToProductMappingInvalidScenarios("QA_Facility_Type_To_Product_Mapping_Invalid_ProductCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid product code");
    uploadPage.uploadFacilityTypeToProductMappingInvalidScenarios("QA_Facility_Type_To_Product_Mapping_Invalid_ProgramCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid program code");
    uploadPage.uploadFacilityTypeToProductMappingInvalidScenarios("QA_Facility_Type_To_Product_Mapping_Invalid_Program_Product_Combination.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Product and Program combination");
  }

  private void verifyValidFacilityUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadFacilities("QA_facilities.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadFacilities("QA_facilities_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidFacilityUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadFacilitiesInvalidScenarios("QA_facilities_Lowest_Code.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Geographic Zone Code must be at the lowest administrative level in your hierarchy in Record No");
    uploadPage.uploadFacilitiesInvalidScenarios("QA_facilities_Duplicate_Code.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Facility Code in Record No");
  }

  private void verifyValidGeographicZoneUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadAndVerifyGeographicZone("QA_Geographic_Data.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadAndVerifyGeographicZone("QA_Geographic_Data_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadAndVerifyGeographicZone("QA_Geographic_Data_Population_Lat_Long.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidGeographicZoneUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadGeographicZoneInvalidScenarios("QA_Geographic_Data_Invalid.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Missing Mandatory data in field : Geographic Zone Code of Record No");
    uploadPage.uploadGeographicZoneInvalidScenarios("QA_Geographic_Data_Duplicate.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Geographic Zone Code in Record No");
    uploadPage.uploadGeographicZoneInvalidScenarios("QA_Geographic_Data_Invalid_Code.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Geographic Zone Parent Code in Record No");
    uploadPage.uploadGeographicZoneInvalidScenarios("QA_Geographic_Data_Invalid_Lat.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Incorrect data length in Record No");
    uploadPage.uploadGeographicZoneInvalidScenarios("QA_Geographic_Data_Invalid_Long.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Incorrect data length in Record No");

  }

  private void verifyValidProductPriceUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadProgramProductPrice("QA_Product_Price.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadProgramProductPrice("QA_Product_Price_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidProductPriceUpload(UploadPage uploadPage) throws FileNotFoundException {
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


  private void verifyValidProgramProductMappingUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadProgramProductMapping("QA_program_product.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadProgramProductMapping("QA_program_product_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidProgramProductMappingUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadProgramProductMappingInvalidScenarios("QA_program_product_Invalid_ProductCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid product code");
    uploadPage.uploadProgramProductMappingInvalidScenarios("QA_program_product_Invalid_ProgramCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid program code");
  }


  private void verifyValidUserUpload(UploadPage uploadPage) throws IOException, SQLException {
    String tableName = "users";
    uploadPage.uploadUsers("QA_Users.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "2");
  }

  private void verifyInValidUserUpload(UploadPage uploadPage) throws IOException, SQLException {
    String tableName = "users";
    uploadPage.uploadInvalidUserScenarios("QA_Users_Duplicate_Email.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate email address in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "2");
    uploadPage.uploadInvalidUserScenarios("QA_Users_Duplicate_EmployeeId.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate employee id in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "2");
    uploadPage.uploadInvalidUserScenarios("QA_Users_Duplicate_UserName.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate User Name in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "2");
    uploadPage.uploadInvalidUserScenarios("QA_Users_Invalid_Supervisor.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Supervisor User Name not present in the system in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "2");
    uploadPage.uploadInvalidUserScenarios("QA_Users_Subsequent_Duplicate_Username.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate User Name in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "2");
    uploadPage.uploadInvalidUserScenarios("QA_Users_Subsequent_InvalidCombination.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate User Name in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "2");
  }

  private void verifyValidProductCategoryUpload(UploadPage uploadPage) throws IOException, SQLException {
    String tableName = "product_categories";
    uploadPage.uploadProductCategory("QA_Productcategoryupload.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "18");
    uploadPage.uploadProductCategory("QA_Productcategoryupload_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "20");
  }

  private void verifyInvalidProductCategoryUpload(UploadPage uploadPage) throws IOException, SQLException {
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

  private void verifyInValidProductUpload(UploadPage uploadPage) throws IOException, SQLException {
    String tableName = "products";
    uploadPage.uploadProductsInvalidScenarios("QA_products_Duplicate_Code.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Product Code in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "0");
    uploadPage.uploadProductsInvalidScenarios("QA_Products_Invalid_ProductGroupCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid reference data Product Group in Record No");
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "0");
  }

  private void verifyValidProductUpload(UploadPage uploadPage) throws IOException, SQLException {
    String tableName = "products";
    uploadPage.uploadProducts("QA_products.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "1");
    uploadPage.uploadProducts("QA_products_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    assertEquals(dbWrapper.getRowsCountFromDB(tableName), "2");
  }

  private void verifyInValidProductGroupUpload(UploadPage uploadPage) throws IOException, SQLException {
    uploadPage.uploadProductGroupsScenarios("QA_Product_Group_Duplicate.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Product Group Code in Record No");
  }

  private void verifyValidProductGroupUpload(UploadPage uploadPage) throws IOException, SQLException {
    uploadPage.uploadProductGroupsScenarios("QA_product_group.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadProductGroupsScenarios("QA_Product_Group_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInValidDeliveryZonesUpload(UploadPage uploadPage) throws IOException, SQLException {
    uploadPage.uploadDeliveryZonesInvalidScenarios("QA_Delivery_Zones_Duplicate.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate delivery zone Code found");
    uploadPage.uploadDeliveryZonesInvalidScenarios("QA_Blank.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("File is empty");

  }

  private void verifyValidDeliveryZonesUpload(UploadPage uploadPage) throws IOException, SQLException {
    uploadPage.uploadDeliveryZones("QA_Delivery_Zones.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadDeliveryZones("QA_Delivery_Zones_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInValidDeliveryZonesProgramScheduleUpload(UploadPage uploadPage) throws IOException, SQLException {
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

  private void verifyValidDeliveryZonesProgramScheduleUpload(UploadPage uploadPage) throws IOException, SQLException {
    uploadPage.uploadDeliveryZoneProgramScheduleValidScenarios("QA_Delivery_Zone_Program_Schedule.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadDeliveryZoneProgramScheduleValidScenarios("QA_Delivery_Zone_Program_Schedule_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInValidDeliveryZonesMembersUpload(UploadPage uploadPage) throws IOException, SQLException {
    uploadPage.uploadDeliveryZoneMembers("QA_Delivery_Zone_Members_Duplicate.csv") ;
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Duplicate Delivery Zone code and Member code combination found");

    uploadPage.uploadDeliveryZoneMembers("QA_Delivery_Zone_Members_Duplicate_Facility.csv") ;
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Facility Code exists for the same program in multiple Delivery Zones");

    uploadPage.uploadDeliveryZoneMembers("QA_Delivery_Zone_Members_Invalid_Member.csv") ;
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Facility code");

    uploadPage.uploadDeliveryZoneMembers("QA_Delivery_Zone_Members_Delivery_Zone_Without_Program.csv") ;
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("No Program(s) mapped for Delivery Zones");
  }

  private void verifyValidDeliveryZonesMembersUpload(UploadPage uploadPage) throws IOException, SQLException {
    uploadPage.uploadDeliveryZoneMembersValidScenarios("QA_Delivery_Zone_Members.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadDeliveryZoneMembersValidScenarios("QA_Delivery_Zone_Members_Subsequent.csv") ;
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInValidDeliveryZonesWarehousesUpload(UploadPage uploadPage) throws IOException, SQLException {
    uploadPage.uploadDeliveryZoneWarehouses("QA_Delivery_Zone_Warehouses_Invalid_Delivery_Zone.csv") ;
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Delivery zone code");

    uploadPage.uploadDeliveryZoneWarehouses("QA_Delivery_Zone_Warehouses_Invalid_Warehouse.csv") ;
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Warehouse code");
  }

  private void verifyValidDeliveryZonesWarehousesUpload(UploadPage uploadPage) throws IOException, SQLException {
    uploadPage.uploadDeliveryZoneWarehousesValidScenarios("QA_Delivery_zone_warehouses.csv") ;
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadDeliveryZoneWarehousesValidScenarios("QA_Delivery_zone_warehouses_Subsequent.csv") ;
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInValidFacilityFTPDetailsUpload(UploadPage uploadPage) throws IOException, SQLException {
    uploadPage.uploadFacilityFTPDetails("QA_Blank.csv") ;
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("File is empty");

    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Invalid_FacilityCode.csv") ;
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Facility code");

    //uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Duplicate.csv") ;
    //uploadPage.verifyErrorMessageOnUploadScreen();
    //uploadPage.validateErrorMessageOnUploadScreen("Duplicate Facility Code in Record No");

    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Missing_Mandatory_Field_Facility_Code.csv") ;
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Missing Mandatory data in field : Facility Code of Record No");

    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Missing_Mandatory_Field_Host.csv") ;
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Missing Mandatory data in field : Host of Record No");

    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Missing_Mandatory_Field_Password.csv") ;
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Missing Mandatory data in field : Password of Record No");

    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Missing_Mandatory_Field_Port.csv") ;
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Missing Mandatory data in field : Port of Record No");

    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Missing_Mandatory_Field_Username.csv") ;
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Missing Mandatory data in field : Username of Record No");

    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Missing_Mandatory_Field_Path.csv") ;
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Missing Mandatory data in field : Local Folder Path of Record No");

    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Incorrect_Data_length.csv") ;
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Incorrect data length");

    uploadPage.uploadFacilityFTPDetails("UnassignedRequisitionGroupReport.jrxml") ;
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Incorrect file format. Please upload Facility FTP details data as a .csv file");

    uploadPage.uploadFacilityFTPDetails("QA_Delivery_Zone_Members.csv") ;
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.validateErrorMessageOnUploadScreen("Invalid Headers in upload file");
  }

  private void verifyValidFacilityFTPDetailsUpload(UploadPage uploadPage) throws IOException, SQLException {
    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details.csv") ;
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadFacilityFTPDetails("QA_Facility_FTP_Details_Subsequent.csv") ;
    uploadPage.verifySuccessMessageOnUploadScreen();
//    uploadPage.validateSuccessMessageOnUploadScreen("File uploaded successfully. 'Number of records created:1', 'Number of records updated: 1'");

  }

  @AfterMethod(groups = {"functional"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
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
