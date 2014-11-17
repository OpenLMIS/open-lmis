/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

var vaccine = angular.module('vaccine', ['openlmis', 'ui.bootstrap']).config(['$routeProvider', function ($routeProvider) {

    $routeProvider.
        when('/distribution-batch', {controller: VaccineDistributionSearchController, templateUrl: 'partials/vaccine-distribution-search.html'}).
        when('/create-distribution-batch', {controller: VaccineDistributionController, templateUrl: 'partials/vaccine-distribution-edit.html', resolve:VaccineDistributionController.resolve}).
        when('/edit-distribution-batch/:distributionBatchId', {controller: VaccineDistributionController, templateUrl: 'partials/vaccine-distribution-edit.html', resolve:VaccineDistributionController.resolve}).
        when('/targets', {controller: VaccineTargetController, templateUrl: 'partials/vaccine-targets.html'}).
        when('/targetEdit/:id', {controller: VaccineTargetController, templateUrl: 'partials/vaccine-targets-edit.html'}).
        when('/quantification', {controller: VaccineQuantificationController, templateUrl: 'partials/vaccine-quantification.html'}).
        when('/vaccine-storage', {controller: VaccineStorageController, templateUrl: 'partials/vaccine-storage.html'}).
        when('/targets', {controller: VaccineTargetController, templateUrl: 'partials/vaccine-targets.html'}).
        when('/targetEdit/:id', {controller: VaccineTargetController, templateUrl: 'partials/vaccine-targets-edit.html'}).
        when('/quantification', {controller: VaccineQuantificationController, templateUrl: 'partials/vaccine-quantification.html'}).
        otherwise({redirectTo: '/distribution-batch'});
}]).directive('onKeyup', function () {
        return function (scope, elm, attrs) {
            elm.bind("keyup", function () {
                scope.$apply(attrs.onKeyup);
            });
        };
});
