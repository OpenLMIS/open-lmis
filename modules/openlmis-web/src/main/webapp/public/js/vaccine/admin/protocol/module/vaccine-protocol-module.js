/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

angular.module('vaccine-protocol', ['openlmis','ngTable','ui.sortable' , 'ui.bootstrap.modal', 'ui.bootstrap.dialog']).
  config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/disease', {controller:VaccineDiseaseController, templateUrl:'partials/disease.html', resolve : VaccineDiseaseController.resolve }).
        when('/disease/add', {controller:VaccineDiseaseFormController, templateUrl:'partials/disease_form.html', resolve: VaccineDiseaseFormController.resolve }).
        when('/disease/edit/:id', {controller:VaccineDiseaseFormController, templateUrl:'partials/disease_form.html', resolve: VaccineDiseaseFormController.resolve }).
        when('/template', {controller:LogisticsColumnTemplate, templateUrl:'partials/column-template.html', resolve: LogisticsColumnTemplate.resolve }).
        when('/storage-type', {controller:StorageTypeController, templateUrl:'partials/storage-type.html', resolve: StorageTypeController.resolve}).
        when('/storage-type-create', {controller:StorageTypeController, templateUrl:'partials/storage-type-create.html', resolve: StorageTypeController.resolve}).
        when('/storage-type-manage/:id', {controller:StorageTypeEditController, templateUrl:'partials/storage-type-manage.html'}).
        when('/temperature', {controller:TempratureLookupController, templateUrl:'partials/temperature.html', resolve: TempratureLookupController.resolve}).
        when('/temperature-create', {controller:TempratureLookupController, templateUrl:'partials/temprature-create.html', resolve: TempratureLookupController.resolve}).
        when('/temperature-update/:id', {controller:TempratureUpdateController, templateUrl:'partials/temprature-update.html'}).
        when('/countries', {controller:CountriesLookupController, templateUrl:'partials/countries.html', resolve: CountriesLookupController.resolve}).
        when('/countries-create', {controller:CountriesLookupController, templateUrl:'partials/countries-create.html', resolve: CountriesLookupController.resolve}).
        when('/countries-update/:id', {controller:CountriesUpdateController, templateUrl:'partials/countries-update.html',  resolve: CountriesUpdateController.resolve}).
        otherwise({redirectTo:'/protocol'});
  }]).run(function ($rootScope, AuthorizationService) {
    //AuthorizationService.preAuthorize('VIEW_REPORT');
  });
