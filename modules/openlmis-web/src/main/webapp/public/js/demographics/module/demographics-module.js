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
angular.module('demographics', ['openlmis','ngTable','ui.sortable' , 'ui.bootstrap.modal', 'ui.bootstrap.dialog']).
  config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/list', {controller:DemographicEstimateCategoryController, templateUrl:'partials/category.html', resolve : DemographicEstimateCategoryController.resolve }).
        when('/category/add', {controller:DemographicEstimateCategoryFormController, templateUrl:'partials/category_form.html', resolve : DemographicEstimateCategoryFormController.resolve }).
        when('/category/edit/:id', {controller:DemographicEstimateCategoryFormController, templateUrl:'partials/category_form.html', resolve : DemographicEstimateCategoryFormController.resolve }).
        when('/facility', {controller:FacilityDemographicEstimateController, templateUrl:'partials/facility.html', resolve : BaseDemographicEstimateController.resolve }).
        when('/district', {controller:DistrictDemographicEstimateController, templateUrl:'partials/district.html', resolve : BaseDemographicEstimateController.resolve }).
      otherwise({redirectTo:'/list'});
  }]);
