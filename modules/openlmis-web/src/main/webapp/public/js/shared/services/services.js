/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

var services = angular.module('openlmis.services', ['ngResource']);
var update ={update: {method: 'PUT'}};

services.value('version', '@version@');

services.factory('Programs', function ($resource) {
  return $resource('/programs/:type.json', {type: '@type'}, {});
});

services.factory('RnRColumnList', function ($resource) {
  return $resource('/program/:programId/rnr-template.json', {}, {post: {isArray: true, method: 'POST'}});
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

services.factory('UserContext', function ($resource) {
  return $resource('/user-context.json', {}, {});
});

services.factory('Users', function ($resource) {
  return $resource('/users/:id.json', {id: '@id'}, update);
});

services.factory('UserFacilityList', function ($resource) {
  return $resource('/logistics/user/facilities.json', {}, {});
});

services.factory('UserFacilityWithViewRequisition', function ($resource) {
  return $resource('/user/facilities/view.json', {}, {});
});

services.factory('ProgramsToViewRequisitions', function ($resource) {
  return $resource('/facility/:facilityId/view/requisition/programs.json', {}, {});
});

services.factory('RequisitionHeader', function ($resource) {
  return $resource('/logistics/facility/:facilityId/requisition-header.json', {}, {});
});

services.factory('ProgramSupportedByFacility', function ($resource) {
  return $resource('/facilities/:facilityId/programs.json', {}, {});
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

services.factory('CreateRequisitionProgramList', function ($resource) {
  return $resource('/create/requisition/programs.json', {}, {})
});

services.factory('UserSupervisedFacilitiesForProgram', function ($resource) {
  return $resource('/create/requisition/supervised/:programId/facilities.json', {}, {})
});

services.factory('ReferenceData', function ($resource) {
  return $resource('/reference-data/currency.json', {}, {});
});

services.factory('Requisitions', function ($resource) {
  return $resource('/requisitions/:id/:operation.json', {}, update);
});

services.factory('Requisition', function ($resource) {
  return $resource('/requisitions.json', {}, {});
});

services.factory('RequisitionById', function ($resource) {
  return $resource('/requisitions/:id.json', {}, {});
});

services.factory('RequisitionForApproval', function ($resource) {
  return $resource('/requisitions-for-approval.json', {}, {});
});

services.factory('RequisitionsForViewing', function ($resource) {
  return $resource('/requisitions-list.json', {}, {});
});

services.factory('RequisitionForConvertToOrder', function ($resource) {
  return $resource('/requisitions-for-convert-to-order.json', {}, {});
});

services.factory('LossesAndAdjustmentsReferenceData', function ($resource) {
  return $resource('/requisitions/lossAndAdjustments/reference-data.json', {}, {})
});

services.factory('Schedule', function ($resource) {
  return $resource('/schedules/:id.json', {id: '@id'}, update);
});

services.factory('Periods', function ($resource) {
  return $resource('/schedules/:scheduleId/periods.json', {}, {});
});

services.factory('PeriodsForFacilityAndProgram', function ($resource) {
  return $resource('/logistics/facility/:facilityId/program/:programId/periods.json', {}, {});
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

services.factory('SupervisoryNodes', function ($resource) {
  return $resource('/supervisory-nodes.json', {}, {});
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


services.factory('ReportTemplates', function ($resource) {
  return $resource('/report-templates.json', {}, {});
});

//Allocation

services.factory('ProgramProducts', function ($resource) {
  return $resource('/programProducts/programId/:programId.json', {}, {});
});

services.factory('FacilityProgramProducts', function ($resource) {
  return $resource('/facility/:facilityId/program/:programId/isa.json', {}, {update: {method: 'PUT', isArray: true}});
});

services.factory('ProgramProductsISA', function ($resource) {
  return $resource('/programProducts/:programProductId/isa/:isaId.json', {isaId: '@isaId'}, update);
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
  return $resource('/geographicZones/:id.json', {}, {});
});


