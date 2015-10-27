/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
angular.module('vaccine_stock_movement', ['openlmis', 'ngGrid', 'ui.bootstrap.dialog', 'ui.bootstrap.accordion', 'ui.bootstrap.pagination', 'ui.bootstrap.dropdownToggle','angularCombine'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/stock-movement-view/:programId/:periodId/:facilityId/:facilityName/:id', {controller:StockMovementViewController, templateUrl:'partials/distribute.html',resolve:StockMovementViewController.resolve,  reloadOnSearch: false}).
            when('/view-pending', {controller:StockMovementViewController, templateUrl:'partials/view.html',resolve:StockMovementViewController.resolve,  reloadOnSearch: false})
            .otherwise({redirectTo: '/partials/view.html'});
    }]).directive('select2Blur', function () {
        return function (scope, elm, attrs) {
            angular.element("body").on('mousedown', function (e) {
                $('.select2-dropdown-open').each(function () {
                    if (!$(this).hasClass('select2-container-active')) {
                        $(this).data("select2").blur();
                    }
                });
            });
        };
    }).run(function ($rootScope, AuthorizationService) {

    }).config(function(angularCombineConfigProvider) {
        angularCombineConfigProvider.addConf(/filter-/, '/public/pages/reports/shared/filters.html');
    }).filter('positive', function() {
        return function(input) {
            if (!input) {
                return 0;
            }

            return Math.abs(input);
        };
    }).filter('expirationDate', [
        '$filter', function($filter) {
            return function(input, format) {
                return $filter('date')(new Date(input), format);
            };
        }
    ]);