/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

var services = angular.module('openlmis.services', ['ngResource']);
var update = {update: {method: 'PUT'}};

services.value('version', '@version@');

services.factory('Programs', function ($resource) {
  return $resource('/programs/:type.json', {type: '@type'}, {});
});

services.factory('UpdateProgram', function ($resource) {
  return $resource('/programs/save.json', {}, update);
});

services.factory('RnRColumnList', function ($resource) {
  return $resource('/program/:programId/rnr-template.json', {}, {});
});

services.factory('ProgramRnRColumnList', function ($resource) {
  return $resource('/rnr/:programId/columns.json', {}, {});
});

services.factory('Facility', function ($resource) {
  var resource = $resource('/facilities/:id.json', {id: '@id'}, update);

  resource.restore = function (pathParams, success, error) {
    $resource('/facilities/:id/restore.json', {}, update).update(pathParams, {}, success, error);
  };

  return resource;
});

services.factory("Facilities", function ($resource) {
  return $resource('/filter-facilities.json', {}, {});
});

services.factory("FacilityTypes", function ($resource) {
  return $resource('/facility-types.json', {}, {});
});

services.factory('UserContext', function ($resource) {
  return $resource('/user-context.json', {}, {});
});

services.factory('Users', function ($resource) {
  var resource = $resource('/users/:id.json', {id: '@id'}, update);

  resource.disable = function (pathParams, success, error) {
    $resource('/users/:id.json', {}, {update: {method: 'DELETE'}}).update(pathParams, {}, success, error);
  };

  return resource;
});

services.factory('UserFacilityList', function ($resource) {
  return $resource('/user/facilities.json', {}, {});
});

services.factory('UserFacilityWithViewRequisition', function ($resource) {
  return $resource('/user/facilities/view.json', {}, {});
});

services.factory('ProgramsToViewRequisitions', function ($resource) {
  return $resource('/facility/:facilityId/view/requisition/programs.json', {}, {});
});

services.factory('ProgramSupportedByFacility', function ($resource) {
  return $resource('/facilities/:facilityId/programs.json', {}, {});
});

services.factory('ManageEquipmentInventoryProgramList', function ($resource) {
  return $resource('/equipment/inventory/programs.json', {}, {});
});


services.factory('FacilityReferenceData', function ($resource) {
  return $resource('/facilities/reference-data.json', {}, {});
});

services.factory('Rights', function ($resource) {
  return $resource('/rights.json', {}, {});
});

services.factory('Roles', function ($resource) {
  return $resource('/roles/:id.json', {id: '@id'}, update);
});

services.factory('RolesFlat', function ($resource) {
  return $resource('/roles-flat', {id: '@id'}, update);
});

services.factory('CreateRequisitionProgramList', function ($resource) {
  return $resource('/create/requisition/programs.json', {}, {});
});

services.factory('UserSupervisedFacilitiesForProgram', function ($resource) {
  return $resource('/create/requisition/supervised/:programId/facilities.json', {}, {});
});

services.factory('ReferenceData', function ($resource) {
  return $resource('/reference-data/currency.json', {}, {});
});

services.factory('LineItemsPerPage', function ($resource) {
  return $resource('/reference-data/pageSize.json', {}, {});
});

services.factory('Requisitions', function ($resource) {
  return $resource('/requisitions/:id/:operation.json', {id: '@id', operation: '@operation'}, update);
});

services.factory('RequisitionForApproval', function ($resource) {
  return $resource('/requisitions-for-approval.json', {}, {});
});

services.factory('RequisitionsForViewing', function ($resource) {
  return $resource('/requisitions.json', {}, {});
});

services.factory('RequisitionForConvertToOrder', function ($resource) {
  return $resource('/requisitions-for-convert-to-order.json', {}, {});
});

services.factory('LossesAndAdjustmentsReferenceData', function ($resource) {
  return $resource('/requisitions/lossAndAdjustments/reference-data.json', {}, {});
});

services.factory('DeleteRequisition', function ($resource) {
  return $resource('/requisitions/delete/:id.json', {id: '@id'}, {post: {method: 'POST', isArray: false}});
});

services.factory('SkipRequisition', function ($resource) {
  return $resource('/requisitions/skip/:id.json', {id: '@id'}, {post: {method: 'POST', isArray: false}});
});

services.factory('ReOpenRequisition', function ($resource) {
  return $resource('/requisitions/reopen/:id.json', {id: '@id'}, {post: {method: 'POST', isArray: false}});
});

services.factory('RejectRequisition', function ($resource) {
  return $resource('/requisitions/reject/:id.json', {id: '@id'}, {post: {method: 'POST', isArray: false}});
});

services.factory('Schedule', function ($resource) {
  return $resource('/schedules/:id.json', {id: '@id'}, update);
});

services.factory('Periods', function ($resource) {
  return $resource('/schedules/:scheduleId/periods.json', {}, {});
});

services.factory('PeriodsForFacilityAndProgram', function ($resource) {
  return $resource('/logistics/periods.json', {}, {});
});

services.factory('Period', function ($resource) {
  return $resource('/periods/:id.json', {}, {});
});

services.factory('Program', function ($resource) {
  return $resource('/programs/:id.json', {id: '@id'}, {});
});

services.factory('SupportedUploads', function ($resource) {
  return $resource('/supported-uploads.json', {}, {});
});

services.factory('ForgotPassword', function ($resource) {
  return $resource('/forgot-password.json', {}, {});
});

services.factory('FacilityApprovedProducts', function ($resource) {
  return $resource('/facilityApprovedProducts/facility/:facilityId/program/:programId/nonFullSupply.json', {}, {});
});

services.factory('RequisitionLineItem', function ($resource) {
  return $resource('/logistics/requisition/lineItem.json', {}, {});
});

services.factory('UpdateUserPassword', function ($resource) {
  return $resource('/user/resetPassword/:token.json', {}, update);
});

services.factory('ValidatePasswordToken', function ($resource) {
  return $resource('/user/validatePasswordResetToken/:token.json', {}, {});
});

services.factory('Messages', function ($resource) {
  return $resource('/messages.json', {}, {});
});

services.factory('SupervisoryNodesPagedSearch', function ($resource) {
  return $resource('/paged-search-supervisory-nodes.json', {}, {});
});

services.factory('FacilityProgramRights', function ($resource) {
  return $resource('/facility/:facilityId/program/:programId/rights.json');
});

services.factory('RequisitionComment', function ($resource) {
  return $resource('/requisitions/:id/comments.json', {}, {});
});

services.factory('Orders', function ($resource) {
  return $resource('/orders.json', {}, {post: {isArray: true, method: 'POST'}});
});

services.factory('OrdersForManagePOD', function ($resource) {
  return $resource('/manage-pod-orders', {}, {});
});

services.factory('ReportTemplates', function ($resource) {
  return $resource('/report-templates.json', {}, {});
});

//Allocation

services.factory('ProgramProducts', function ($resource) {
  return $resource('/programProducts/programId/:programId.json', {}, {});
});

services.factory('FacilityProgramProducts', function ($resource) {
  return $resource('/facility/:facilityId/program/:programId.json', {}, {update: {method: 'PUT'}});
});

services.factory('ProgramProductsISA', function ($resource) {
  return $resource('/programProducts/:programProductId/isa/:isaId.json', {isaId: '@isaId'}, update);
});

services.factory('FacilityProgramProductsISA', function ($resource)
{
  //return $resource('/facility/:facilityId/programProducts/:programProductId/isa/:isaId.json', {isaId: '@isaId', facilityId: '@facilityId'}, update);
  return $resource('/facility/:facilityId/programProducts/:programProductId/isa.json', {}, update);
});

services.factory('AllocationProgramProducts', function ($resource) {
  return $resource('/facility/:facilityId/programProduct/:programProductId.json', {}, update);
});

services.factory('UserDeliveryZones', function ($resource) {
  return $resource('/user/deliveryZones.json', {}, {});
});

services.factory('DeliveryZone', function ($resource) {
  return $resource('/deliveryZones/:id.json', {id: '@id'}, {});
});

services.factory('DeliveryZoneActivePrograms', function ($resource) {
  return $resource('/deliveryZones/:zoneId/activePrograms.json', {}, {});
});

services.factory('DeliveryZonePrograms', function ($resource) {
  return $resource('/deliveryZones/:zoneId/programs.json', {}, {});
});

services.factory('DeliveryZoneProgramPeriods', function ($resource) {
  return $resource('/deliveryZones/:zoneId/programs/:programId/periods.json', {}, {});
});

services.factory('DeliveryZoneFacilities', function ($resource) {
  return $resource('/deliveryZones/:deliveryZoneId/programs/:programId/facilities.json', {}, {});
});

services.factory('ProgramRegimens', function ($resource) {
  return $resource('/programId/:programId/regimens.json', {}, {});
});

services.factory('RegimenCategories', function ($resource) {
  return $resource('/regimenCategories.json', {}, {});
});

services.factory('Regimens', function ($resource) {
  return $resource('/programId/:programId/regimens.json', {}, {post: {method: 'POST', isArray: true}});
});

services.factory('RegimenColumns', function ($resource) {
  return $resource('/programId/:programId/regimenColumns.json', {}, {});
});

services.factory('RegimenTemplate', function ($resource) {
  return $resource('/programId/:programId/configureRegimenTemplate.json', {}, {});
});

services.factory('ProgramRegimenTemplate', function ($resource) {
  return $resource('/programId/:programId/programRegimenTemplate.json', {}, {});
});

services.factory('GeographicZones', function ($resource) {
  return $resource('/geographicZones/:id.json', {id: '@id'}, update);
});

services.factory("GeographicZoneSearch", function ($resource) {
  return $resource('/filtered-geographicZones.json', {}, {});
});

services.factory('Distributions', function ($resource) {
  return $resource('/distributions.json', {}, {});
});

services.factory('SyncFacilityDistributionData', function ($resource) {
  return $resource('/distributions/:id/facilities/:facilityId.json', {}, update);
});

services.factory('Locales', function ($resource) {
  return $resource('/locales.json', {}, {});
});

services.factory('ChangeLocale', function ($resource) {
  return $resource('/changeLocale.json', {}, update);
});

services.factory('UpdatePassword', function ($resource) {
  return $resource('/admin/resetPassword/:userId.json', {}, update);
});

services.factory('OrderFileTemplate', function ($resource) {
  return $resource('/order-file-template.json', {}, {post: {method: 'POST', isArray: true}});
});

services.factory('DateFormats', function ($resource) {
  return $resource('/date-formats.json', {}, {});
});

services.factory('ShipmentFileTemplate', function ($resource) {
  return $resource('/shipment-file-template.json', {}, {post: {method: 'POST', isArray: true}});
});


services.factory('BudgetFileTemplate', function ($resource) {
  return $resource('/budget-file-template.json', {}, {post: {method: 'POST', isArray: true}});
});

services.factory('EnabledWarehouse', function ($resource) {
  return $resource('/enabledWarehouses.json', {}, {});
});

services.factory('OrderPOD', function ($resource) {
  return $resource('/pods/:action/:id.json', {id: '@id', action: '@action'}, update);
});

services.factory('OrderNumberConfiguration', function ($resource) {
  return $resource('/order-number-configuration.json', {}, {post: {method: 'POST'}});
});

services.factory('GeoLevels', function ($resource) {
  return $resource('/geographicLevels.json', {}, {});
});

services.factory('GeographicZonesAboveLevel', function ($resource) {
  return $resource('/parentGeographicZones/:geoLevelCode.json', {}, {});
});

services.factory('SupervisoryNodes', function ($resource) {
  return $resource('/supervisory-nodes/:id.json', {}, update);
});

services.factory('SupplyLines', function ($resource) {
  return $resource('/supplyLines/:id.json', {}, update);
});

services.factory('ParentSupervisoryNodes', function ($resource) {
  return $resource('/search-supervisory-nodes.json', {}, {});
});

services.factory('RequisitionGroups', function ($resource) {
  return $resource('/requisitionGroups/:id.json', {id: '@id'}, update);
});

services.factory('SupervisoryNodesSearch', function ($resource) {
  return $resource('/search-supervisory-nodes.json', {}, {});
});

services.factory('TopLevelSupervisoryNodes', function ($resource) {
  return $resource('/topLevelSupervisoryNodes.json', {}, {});
});

services.factory('SupplyLinesSearch', function ($resource) {
  return $resource('/supplyLines/search.json', {}, {});
});

services.factory('ProgramProductsFilter', function ($resource) {
  return $resource('/programProducts/filter/programId/:programId/facilityTypeId/:facilityTypeId.json',
      {programId: '@programId', facilityTypeId: '@facilityTypeId'}, {}, {});
});

services.factory('FacilityTypeApprovedProducts', function ($resource) {
  return $resource('/facilityApprovedProducts/:id.json', {id: '@id'}, update);
});

services.factory('ProgramProductsSearch', function ($resource) {
  return $resource('/programProducts/search.json', {}, {});
});

services.factory('Reports', function ($resource) {
  return $resource('/reports/:id/:format.json', {}, {});
});

services.factory('ProductGroups', function ($resource) {
  return $resource('/products/groups.json', {}, {});
});

services.factory('ProductForms', function ($resource) {
  return $resource('/products/forms.json', {}, {});
});

services.factory('DosageUnits', function ($resource) {
  return $resource('/products/dosageUnits.json', {}, {});
});

services.factory('Products', function ($resource) {
  return $resource('/products/:id.json', {id: '@id'}, update);
});

services.factory('ProductCategories', function ($resource) {
  return $resource('/products/categories.json', {}, {});
});


services.factory('EquipmentOperationalStatus', function ($resource) {
  return $resource('/equipment/type/operational-status.json',{},  {});
});

services.factory('FacilityImages', function($resource){
  return $resource('/facility-images.json', {},{});
});

services.factory('ConfigSettingsByKey',function($resource){
  return $resource('/settings/:key.json',{},{});
});

services.factory('Supplylines', function ($resource) {
  return $resource('/supplylines.json', {});
});

services.factory('SupplyingDepots', function ($resource) {
  return $resource('/supplyLines/supplying-depots.json', {});
});

services.factory('UserFacilityWithViewVaccineOrderRequisition', function ($resource) {
  return $resource('/user/facilities/view-order-requisition.json', {}, {});
});

services.factory('ProgramsToViewVaccineOrderRequisitions', function ($resource) {
  return $resource('/facility/:facilityId/view/vaccine-order-requisition/programs.json', {}, {});
});

services.factory('VaccineOrderRequisitionsForViewing', function ($resource) {
  return $resource('/vaccine/orderRequisition/search.json', {}, {});
});
