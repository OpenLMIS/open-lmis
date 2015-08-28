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

/* Equipment Types */

services.factory('EquipmentTypes', function ($resource) {
  return $resource('/equipment/type/list.json', {}, {});
});

services.factory('EquipmentType', function ($resource) {
  return $resource('/equipment/type/id.json', {}, {});
});

services.factory('SaveEquipmentType', function ($resource) {
  return $resource('/equipment/type/save.json', {}, {});
});


/* Equipment */
services.factory('Equipments', function ($resource) {
  return $resource('/equipment/manage/list.json', {}, {});
});

services.factory('EquipmentsByType', function ($resource) {
    return $resource('/equipment/manage/list-by-type.json', {}, {});
});

services.factory('Equipment', function ($resource) {
  return $resource('/equipment/manage/type-and-id.json', {}, {});
});

services.factory('EquipmentTypesByProgram', function ($resource) {
    return $resource('/equipment/manage/typesByProgram/:programId.json', {}, {});
});

services.factory('SaveEquipment', function ($resource) {
  return $resource('/equipment/manage/save.json', {}, {});
});

services.factory('RemoveEquipment',function($resource){
    return $resource('/equipment/manage/remove/:equipmentTypeId/:id.json',{},{});
});


/* Equipment Inventory */
services.factory('EquipmentInventories', function ($resource) {
  return $resource('/equipment/inventory/list.json', {}, {});
});

services.factory('EquipmentInventory', function ($resource) {
  return $resource('/equipment/inventory/by-id.json', {}, {});
});

services.factory('SaveEquipmentInventory', function ($resource) {
  return $resource('/equipment/inventory/save.json', {}, {});
});

services.factory('UpdateEquipmentInventoryStatus', function ($resource) {
    return $resource('/equipment/inventory/status/update.json', {}, {});
});

/* Donors */
services.factory('Donors', function ($resource) {
  return $resource('/donor/list.json', {}, {});
});


/* Service Types */
services.factory('ServiceTypes', function ($resource) {
    return $resource('/equipment/service-type/list.json', {}, {});
});

services.factory('ServiceType', function ($resource) {
    return $resource('/equipment/service-type/id.json', {}, {});
});

services.factory('SaveServiceType', function ($resource) {
    return $resource('/equipment/service-type/save.json', {}, {});
});

/* Vendor management  */
services.factory('Vendors', function ($resource) {
    return $resource('/equipment/vendor/list.json', {}, {});
});

services.factory('Vendor', function ($resource) {
    return $resource('/equipment/vendor/id.json', {}, {});
});

services.factory('SaveVendor', function ($resource) {
    return $resource('/equipment/vendor/save.json', {}, {});
});

services.factory('DeleteVendor', function ($resource) {
    return $resource('/equipment/vendor/delete/:id.json', {}, {});
});

/* Contract management  */
services.factory('Contracts', function ($resource) {
    return $resource('/equipment/service-contracts/list.json', {}, {});
});

services.factory('Contract', function ($resource) {
    return $resource('/equipment/service-contracts/id.json', {}, {});
});

services.factory('SaveContract', function ($resource) {
    return $resource('/equipment/service-contracts/save.json', {}, {});
});

/** Maintenance Requests */
services.factory('SaveMaintenanceRequest', function($resource){
    return $resource('/equipment/maintenance-request/save.json', {}, {});
});

services.factory('SaveAndLogMaintenanceRequest', function($resource){
    return $resource('/equipment/maintenance-log/saveAndLogMaintenance',{},{});
});

services.factory('EquipmentLogs', function($resource){
   return $resource('/equipment/maintenance-request/full-history.json',{},{});
});

services.factory('PendingRequests', function($resource){
    return $resource('/equipment/maintenance-request/outstanding-for-user.json',{},{});
});

services.factory('SaveDonor',function($resource){
    return $resource('/donor/insert.json',{},{});
});

services.factory('GetDonor',function($resource){
    return $resource('/donor/getDetails/:id.json',{},{});
});

services.factory('RemoveDonor',function($resource){
    return $resource('/donor/remove/:id.json',{},{});
});

services.factory('UserListForVendor',function($resource){
    return $resource('/equipment/vendor-user/getAllUsersForVendor/:id.json',{},{});
});

services.factory('SaveVendorUserAssociation', function($resource){
    return $resource('/equipment/vendor-user/saveNewUserForVendor',{},{});
});

services.factory('RemoveVendorUserAssociation', function($resource){
    return $resource('/equipment/vendor-user/remove/:vendorId/:userId.json',{},{});
});

services.factory('UserListAvailableForVendor',function($resource){
    return $resource('/equipment/vendor-user/getAllUsersAvailableForVendor.json',{},{});
});

/* Program Equipments */

services.factory('SaveProgramEquipment',function($resource){
    return $resource('/equipment/program-equipment/save.json',{},{});
});

services.factory('GetProgramEquipmentByProgramId', function($resource){
    return $resource('/equipment/program-equipment/getByProgram/:programId.json',{},{});
});

services.factory('RemoveProgramEquipment',function($resource){
    return $resource('/equipment/program-equipment/remove/:id.json',{},{});
});

services.factory("PossibleProductsForProgram", function($resource){
    return $resource('/equipment/program-equipment-product/possible-products.json', {}, {});
});

/* Program Equipment Products */

services.factory('SaveProgramEquipmentProduct',function($resource){
    return $resource('/equipment/program-equipment-product/save.json',{},{});
});

services.factory('GetProgramEquipmentProductByProgramEquipment', function($resource){
    return $resource('/equipment/program-equipment-product/getByProgramEquipment/:programEquipmentId',{},{});
});

services.factory('RemoveProgramEquipmentProduct',function($resource){
    return $resource('/equipment/program-equipment-product/remove/:id.json',{},{});
});

/** manage inventory - list facilities */
services.factory('EquipmentInventoryFacilities',function($resource){
  return $resource('/equipment/inventory/supervised/facilities.json',{},{});
});

services.factory('ManageEquipmentInventoryFacilityProgramList', function($resource){
  return $resource('/equipment/inventory/facility/programs.json',{},{});
});

/* Equipment Energy Types */

services.factory('EquipmentEnergyTypes', function ($resource) {
  return $resource('/equipment/energy-type/list.json', {}, {});
});

services.factory('EquipmentEnergyType', function ($resource) {
  return $resource('/equipment/energy-type/id.json', {}, {});
});

services.factory('SaveEquipmentEnergyType', function ($resource) {
  return $resource('/equipment/energy-type/save.json', {}, {});
});

services.factory("ColdChainDesignations", function($resource){
    return $resource('/equipment/cold-chain/designations.json',{},{});
});

services.factory("ColdChainPqsStatus", function($resource){
    return $resource('/equipment/cold-chain/pqsStatus.json',{},{});
});

services.factory("currentEquipmentTypeId", function() {
 var typeId;
 function set(id) {
   typeId = id;
 }
 function get() {
  return typeId;
 }
 return {
  set: set,
  get: get
 };
});

services.factory("currentProgramId", function() {
 var programId;
 function set(id) {
   programId = id;
 }
 function get() {
  return programId;
 }
 return {
  set: set,
  get: get
 };
});