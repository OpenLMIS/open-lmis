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

services.factory('Equipment', function ($resource) {
  return $resource('/equipment/manage/id.json', {}, {});
});

services.factory('SaveEquipment', function ($resource) {
  return $resource('/equipment/manage/save.json', {}, {});
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

