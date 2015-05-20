
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
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