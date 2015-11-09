/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

services.factory('VaccineDiseases', function ($resource) {
  return $resource('/vaccine/disease/all.json', {}, {});
});

services.factory('VaccineDisease', function ($resource) {
  return $resource('/vaccine/disease/get/:id.json', {id : '@id'}, {});
});

services.factory('SaveVaccineDisease', function ($resource) {
  return $resource('/vaccine/disease/save.json', {}, update);
});


services.factory('SaveVaccineProductDose', function ($resource) {
  return $resource('/vaccine/product-dose/save.json', {}, update);
});

services.factory('VaccineProductDose', function ($resource) {
  return $resource('/vaccine/product-dose/get/:programId.json', {productId : '@programId'}, {});
});

services.factory('VaccineIvdTabConfigs', function ($resource) {
  return $resource('/vaccine/config/tab-visibility/:programId.json', {productId : '@programId'}, {});
});

services.factory('SaveVaccineIvdTabConfigs', function ($resource) {
  return $resource('/vaccine/config/save-tab-visibility.json', {}, update);
});


services.factory('VaccineReportConfigurablePrograms', function ($resource) {
  return $resource('/vaccine/report/programs.json', {}, {});
});

services.factory('VaccineSupervisedIvdPrograms', function ($resource) {
  return $resource('/vaccine/report/ivd-form/supervised-programs.json', {}, {});
});

services.factory('VaccineHomeFacilityIvdPrograms', function ($resource) {
  return $resource('/vaccine/report/ivd-form/programs.json', {}, {});
});

services.factory('VaccineReportPrograms', function ($resource) {
  return $resource('/vaccine/report/programs.json', {}, {});
});

services.factory('VaccineReportFacilities', function ($resource) {
  return $resource('/vaccine/report/ivd-form/facilities/:programId.json', { programId: '@programId'}, {});
});

services.factory('VaccineReportPeriods', function ($resource) {
  return $resource('/vaccine/report/periods/:facilityId/:programId.json', {facilityId: '@facilityId', programId: '@programId'}, {});
});

services.factory('ViewVaccineReportPeriods', function ($resource) {
  return $resource('/vaccine/report/view-periods/:facilityId/:programId.json', {facilityId: '@facilityId', programId: '@programId'}, {});
});


services.factory('VaccineReportInitiate', function ($resource) {
  return $resource('/vaccine/report/initialize/:facilityId/:programId/:periodId.json', {facilityId: '@facilityId', programId: '@programId', periodId: '@periodId'}, {});
});

services.factory('VaccineReport', function ($resource) {
  return $resource('/vaccine/report/get/:id.json', {id: '@id'}, {});
});

services.factory('VaccineReportSave', function ($resource) {
  return $resource('/vaccine/report/save.json', {}, update);
});

services.factory('VaccineReportSubmit', function ($resource) {
  return $resource('/vaccine/report/submit.json', {}, update);
});


services.factory('VaccineColumnTemplate', function ($resource) {
  return $resource('/vaccine/columns/get/:id.json', {id : '@id'}, {});
});

services.factory('VaccineColumnTemplateSave', function ($resource) {
  return $resource('/vaccine/columns/save.json', {}, update);
});

services.factory('VaccineDiscardingReasons', function($resource){
  return $resource('/vaccine/discarding/reasons/all.json',{},{});
});

services.factory('StockCards', function($resource){
   return $resource('/api/v2/facilities/:facilityId/stockCards.json', {facilityId: '@facilityId'}, {});
});

services.factory('Forecast', function($resource){
//TODO call Forecast API
});

services.factory('ProgramProducts',function($resource){
    return $resource('/programProducts/programId/:programId.json',{programId:'@programId'},{});
});

services.factory('VaccineInventoryPrograms',function($resource){
    return $resource('/vaccine/inventory/programs.json',{},{});
});

services.factory('SaveVaccineInventoryAdjustment',function($resource){
    return $resource('/vaccine/inventory/stock/adjustment.json',{},{update:{method:'PUT'}});
});

services.factory('VaccineAdjustmentReasons',function($resource){
    return $resource('/api/v2/stockManagement/adjustmentReasons.json',{},{});
});

services.factory('VaccineProgramProducts', function ($resource) {
  return $resource('/vaccine/inventory/programProducts/programId/:programId.json', {}, {});
});

services.factory('ProductLots', function ($resource) {
  return $resource('/vaccine/inventory/lots/byProduct/:productId.json', {productId:'@productId'}, {});
});

services.factory('SaveVaccineInventoryReceived',function($resource){
    return $resource('/vaccine/inventory/stock/credit.json',{},{update:{method:'PUT'}});
});

services.factory('SaveVaccineInventoryConfigurations',function($resource){
    return $resource('/vaccine/inventory/configuration/save.json',{},{update:{method:'PUT'}});
});

services.factory('VaccineInventoryConfigurations',function($resource){
    return $resource('/vaccine/inventory/configuration/getAll.json',{},{});
});

services.factory('ManufacturerList', function ($resource) {
  return $resource('/vaccine/manufacturers.json', {}, {});
});



services.factory('StockEvent', function($resource){
    return $resource('/api/v2/facilities/:facilityId/stockCards',{facilityId:'@facilityId'}, {update:{method:"POST"}});
});


services.factory('VaccineReportPrograms', function ($resource) {
    return $resource('/vaccine/orderRequisition/programs.json', {}, {});
});


services.factory('VaccineOrderRequisitionReportPeriods', function ($resource) {
    return $resource('/vaccine/orderRequisition/periods/:facilityId/:programId.json', {facilityId: '@facilityId', programId: '@programId'}, {});
});

services.factory('ViewOrderRequisitionVaccineReportPeriods', function ($resource) {
    return $resource('/vaccine/orderRequisition/view-periods/:facilityId/:programId.json', {facilityId: '@facilityId', programId: '@programId'}, {});
});


services.factory('VaccineOrderRequisitionReportInitiate', function ($resource) {
    return $resource('/vaccine/orderRequisition/initialize/:periodId/:programId/:facilityId.json', {facilityId: '@facilityId', programId: '@programId', periodId: '@periodId'}, {});
});

services.factory('VaccineOrderRequisitionReportInitiateEmergency', function ($resource) {
    return $resource('/vaccine/orderRequisition/initializeEmergency/:periodId/:programId/:facilityId.json', {facilityId: '@facilityId', programId: '@programId', periodId: '@periodId'}, {});
});


services.factory('VaccineOrderRequisitionReport', function ($resource) {
    return $resource('/vaccine/orderRequisition/get/:id.json', {id: '@id'}, {});
});

services.factory('UserHomeFacility', function ($resource) {
    return $resource('/vaccine/orderRequisition/userHomeFacility.json', {}, {});
});


services.factory('UserPrograms', function ($resource) {
    return $resource('/reports/user-programs.json', {}, {});
});

services.factory('VaccineOrderRequisitionSubmit', function ($resource) {
    return $resource('/vaccine/orderRequisition/submit.json', {}, update);
});


services.factory('VaccineOrderRequisitionColumns', function ($resource) {
    return $resource('/vaccine/columns/get/columns.json', {}, {});
});

services.factory('VaccinePendingRequisitions', function ($resource) {
    return $resource('/vaccine/orderRequisition/getPendingRequest/:facilityId/:programId.json', {}, {});
});


services.factory('LoggedInUserDetails',function($resource){
    return $resource('/vaccine/orderRequisition/loggedInUserDetails.json',{},{});
});

services.factory('ProgramForUserHomeFacility', function($resource){
    return $resource('/vaccine/orderRequisition/order-requisition/programs.json',{},{});
});

services.factory('VaccineOrderRequisitionInsert', function($resource){
    return $resource('/vaccine/orderRequisition/initialize/:programId/:facilityId.json',{programId:'@programId',facilityId:'@facilityId'},{});
});

services.factory('VaccineOrderRequisitionLastReport', function($resource){
    return $resource('/vaccine/orderRequisition/lastReport/:facilityId/:programId.json',{facilityId:'@facilityId',programId:'@programId'},{});
});

services.factory('VaccineOrderRequisitionSave', function ($resource) {
    return $resource('/vaccine/orderRequisition/save.json', {}, update);
});

services.factory('VaccineOrderRequisitionSubmit', function ($resource) {
    return $resource('/vaccine/orderRequisition/submit.json', {}, update);
});

services.factory('VaccineHomeFacilityPrograms', function ($resource) {
    return $resource('/vaccine/orderRequisition/programs.json', {}, {});
});

services.factory('UpdateOrderRequisitionStatus',function($resource){
    return $resource('/vaccine/orderRequisition/updateOrderRequest/:orderId.json',{orderId:'@orderId'},{update:{method:'PUT'}});
});

services.factory('VaccineLastStockMovement', function ($resource) {
    return $resource('/vaccine/inventory/stock/lastReport.json', {}, {});
});

services.factory('SaveForecastConfiguration',function($resource){
    return $resource('/vaccine/inventory/configuration/saveForecastConfiguration.json',{},{update:{method:'PUT'}});
});

services.factory('VaccineForecastConfigurations',function($resource){
    return $resource('/vaccine/inventory/configuration/getAllForecastConfigurations.json',{},{});
});

services.factory('FacilityDistributed', function ($resource) {
    return $resource('/vaccine/inventory/distribution/get-distributed.json', {}, {});
});

services.factory('SaveDistribution', function ($resource) {
    return $resource('/vaccine/inventory/distribution/save.json', {}, {save:{method:'POST'}});
});

services.factory('DistributedFacilities', function ($resource) {
    return $resource('/vaccine/inventory/distribution/get-distributed.json', {}, {});
});

services.factory('EquipmentNonFunctional',function($resource){
    return $resource('/vaccine/inventory/dashboard/get-equipment-alerts',{},{});
});

services.factory('OneLevelSupervisedFacilities',function($resource){
    return $resource('/vaccine/inventory/distribution/supervised-facilities',{},{});
});
services.factory('ViewBundledDistributionVaccinationSupplies', function ($resource) {
    alert("here");
    return $resource('/vaccine/report/view-bundled-distribution-vaccination-supplies/:year/:productId.json', {year: '@year', productId: '@productId'}, {});
});