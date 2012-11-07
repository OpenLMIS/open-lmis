var services = angular.module('openlmis.services', ['ngResource']);

services.factory('Program', function ($resource) {
    return $resource('/openlmis/admin/programs/programs.json', {}, {});
});

services.factory('RnRColumnList', function ($resource) {
    return $resource('/openlmis/admin/rnr/:programId/columns.json', {}, {});
});

services.factory('Facility', function ($resource) {
    return $resource('/openlmis/facilities/all.json', {}, {});
});

