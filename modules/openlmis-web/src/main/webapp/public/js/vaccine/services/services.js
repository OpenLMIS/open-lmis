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
