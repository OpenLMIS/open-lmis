/**
 * User: Elias
 * Date: 5/4/13
 * Time: 2:33 PM
 */

//var services = angular.module('openlmis.services', ['ngResource']);

services.factory('MailingLabels', function ($resource) {
    return $resource('/reports/reportdata/mailingLabels.json', {}, {});
});

services.factory('ConsumptionReport', function ($resource) {
    return $resource('/reports/reportdata/consumption.json', {}, {});
});

services.factory('AverageConsumptionReport', function ($resource) {
    return $resource('/reports/reportdata/averageConsumption.json', {}, {});
});

services.factory('Products', function($resource){
    return $resource('/reports/products.json', {}, {});
}) ;

services.factory('ProductCategories', function($resource){
    return $resource('/reports/productCategories.json', {}, {});
}) ;

services.factory('SummaryReport', function($resource){
    return $resource('/reports/summary.json', {}, {});
}) ;
services.factory('SupplyStatusReport', function($resource){
    return $resource('/reports/reportdata/supply_status.json', {}, {});
}) ;


services.factory('NonReportingFacilities', function($resource){
    return $resource('/reports/non_reporting.json', {}, {});
});

services.factory('RequisitionGroups', function($resource){
    return $resource('/reports/rgroups.json', {}, {});
});

services.factory('RequisitionGroupsByProgramSchedule', function($resource){
    return $resource('/reports/reporting_groups_by_program_schedule.json', {}, {});
});

services.factory('AdjustmentSummaryReport', function ($resource) {
    return $resource('/reports/reportdata/adjustmentSummary.json', {}, {});
});

services.factory('OrderReport', function ($resource) {
    return $resource('/reports/reportdata/viewOrders.json', {}, {});
});

services.factory('StockImbalanceReport', function ($resource) {
    return $resource('/reports/reportdata/stockImbalance.json', {}, {});
});

services.factory('AdjustmentTypes', function($resource){
    return $resource('/reports/adjustmentTypes.json', {}, {});
});

services.factory('StockedOutReport',function($resource){
   return $resource('/reports/reportdata/stockedOut.json',{},{});
});

services.factory('DistrictConsumptionReport', function ($resource) {
    return $resource('/reports/reportdata/districtConsumption.json', {}, {});
});

services.factory('OperationYears',function($resource){
    return $resource('/reports/operationYears.json',{},{});
});
services.factory('Months',function($resource){
    return $resource('/reports/months.json',{},{});
});

services.factory('ReportPrograms', function ($resource) {
    return $resource('/reports/programs.json', {}, {});
});

services.factory('ReportSchedules', function ($resource) {
    return $resource('/reports/schedules.json', {}, {});
});

services.factory('ReportFacilityTypes', function ($resource) {
    return $resource('/reports/facilityTypes.json', {}, {});
});

services.factory('FacilityList', function ($resource) {
    return $resource('/reports/facilitylist.json', {}, {});
});

services.factory('ReportPeriods', function ($resource) {
    return $resource('/reports/schedules/:scheduleId/periods.json', {}, {});
});

services.factory('GeographicZones', function ($resource) {
    return $resource('/reports/geographicZones.json', {}, {});
});

services.factory('GeographicZone', function ($resource) {
    return $resource('/geographicZone/:id.json', {}, {update:{method:'PUT'}});
});

services.factory('CreateGeographicZone', function ($resource) {
    return $resource('/geographicZone/insert.json', {}, {update:{method:'POST'}});
});

services.factory('GeographicLevels', function($resource) {
    return $resource('/geographicLevels.json',{},{});
});

services.factory('GetGeographicZone',function($resource){
    return $resource('/geographicZone/getDetails/:id.json',{},{});
});

services.factory('SetGeographicZone',function($resource){
    return $resource('/geographicZone/setDetails.json',{},{update:{method:'POST'}});
});

services.factory('Supplylinelist', function ($resource) {
    return $resource('/supplylineslist.json', {}, {});
});

services.factory('Supplylines', function ($resource) {
    return $resource('/supplylines.json', {});
});

services.factory('Supplyline', function ($resource) {
    return $resource('/supplylines/:id.json', {}, {update:{method:'PUT'}});
});

services.factory('SupplylineDelete', function ($resource) {
    return $resource('/supplylineDelete/:id.json', {}, {update:{method:'PUT'}});
});

//Parameters are passed for searching geographic zones.
services.factory('GeographicZoneList', function ($resource) {
    return $resource('/geographicZones.json', {}, {});
});

services.factory('ProductList', function ($resource) {
    return $resource('/productslist.json', {}, {});
});

services.factory('GeographicZoneCompleteList', function ($resource) {
    return $resource('/geographicZone/getList.json', {}, {});
});

services.factory('RequisitionGroupCompleteList',function($resource){
    return $resource('/requisitionGroup/getList.json',{},{});
});

services.factory('SaveRequisitionGroup',function($resource){
    return $resource('/requisitionGroup/insert.json',{},{});
});

services.factory('GetRequisitionGroup',function($resource){
    return $resource('/requisitionGroup/getDetails/:id.json',{},{});
});

services.factory('RemoveRequisitionGroup',function($resource){
    return $resource('/requisitionGroup/remove/:id.json',{},{});
});

services.factory('FacilityCompleteListInRequisitionGroup',function($resource){
    return $resource('/facilities/getListInRequisitionGroup/:id.json',{},{});
});

services.factory('GetFacilityCompleteList',function($resource){
    return $resource('/facilities/getFacilityCompleteList.json',{},{});
});

services.factory('GetFacilityByFacilityType',function($resource){
    return $resource('/facilities/facilityType/:facilityTypeId.json',{},{});
});

services.factory('SaveRequisitionGroupMember',function($resource){
    return $resource('/requisitionGroupMember/insert.json',{},{});
});

services.factory('RemoveRequisitionGroupMember',function($resource){
    return $resource('/requisitionGroupMember/remove/:rgId/:facId.json',{},{});
});

services.factory("ProductForms",function($resource)  {
    return   $resource('/reports/productForms.json', {}, {});
});

services.factory("DosageUnits",function($resource)  {
    return   $resource('/reports/dosageUnits.json', {}, {});
});

services.factory("ProductGroups",function($resource)  {
    return   $resource('/reports/productGroups.json', {}, {});
});

// mahmed 07.13.2013
services.factory('CreateProduct', function ($resource) {
    return $resource('/createProduct.json', {}, {post:{method:'POST'}});
});

// mahmed 07.13.2013
services.factory('UpdateProduct', function ($resource) {
    return $resource('/updateProduct/:id.json', {}, {update:{method:'PUT'}});
});

// mahmed 07.13.2013
services.factory('RemoveProduct', function ($resource) {
    return $resource('/removeProduct/:id.json', {}, {update:{method:'PUT'}});
});

// mahmed 07.13.2013
services.factory('RestoreProduct', function ($resource) {
    return $resource('/restoreProduct/:id.json', {}, {update:{method:'PUT'}});
});

services.factory('ProductCost', function ($resource) {
    return $resource('/programs/:productId/productcost.json', {}, {});
});

services.factory('AllProductCost', function ($resource) {
    return $resource('/allproductcost.json', {}, {});
});

services.factory("AllFacilites",function($resource)  {
    return   $resource('/reports/allFacilities.json', {}, {});
});

services.factory('SupervisoryNodeCompleteList',function($resource){
    return $resource('/supervisoryNode/getList.json',{},{});
});

services.factory('SaveSupervisoryNode',function($resource){
    return $resource('/supervisoryNode/insert.json',{},{});
});

services.factory('GetSupervisoryNode',function($resource){
    return $resource('/supervisoryNode/getDetails/:id.json',{},{});
});

services.factory('RemoveSupervisoryNode',function($resource){
    return $resource('/supervisoryNode/remove/:id.json',{},{});
});

services.factory('Settings',function($resource){
    return $resource('/settings.json',{},{});
});

services.factory('SettingUpdator', function($resource){
    return $resource('/saveSettings.json', {} , { post: {method:'POST'} } );
});