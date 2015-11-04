/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

angular.module('vaccine-inventory', ['openlmis', 'ngTable','ui.bootstrap','angularCombine','ui.bootstrap.modal','ui.bootstrap.pagination'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/stock-adjustment', {controller:StockAdjustmentController, templateUrl:'partials/stock-adjustment.html',resolve:StockAdjustmentController.resolve}).
            when('/receive', {controller:ReceiveStockController, templateUrl:'partials/receive-stock.html',reloadOnSearch:false,resolve:ReceiveStockController.resolve}).
            when('/mass-distribution', {controller:MassDistributionController, templateUrl:'partials/mass-distribution.html',reloadOnSearch:false,resolve:MassDistributionController.resolve}).
            when('/configuration', {controller:VaccineInventoryConfigurationController, templateUrl:'partials/configuration.html',reloadOnSearch:false,resolve:VaccineInventoryConfigurationController.resolve}).
            when('/vaccine-forecasting', {controller:VaccineForecastingController, templateUrl:'partials/vaccine-forecast.html',reloadOnSearch:false,resolve:VaccineForecastingController.resolve}).
            otherwise({redirectTo:'/stock-on-hand'});
    }]).run(function ($rootScope, AuthorizationService) {

     }).config(function(angularCombineConfigProvider) {
        angularCombineConfigProvider.addConf(/filter-/, '/public/pages/reports/shared/filters.html');
    }).filter('positive', function() {
               return function(input) {
                   if (!input) {
                       return 0;
                   }

                   return Math.abs(input);
               };
    });