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
angular.module('mapModule', ['openlmis', 'angularCombine', 'ngTable', 'ui.bootstrap.modal', 'ui.bootstrap.dialog', 'leaflet-directive', 'ui.bootstrap.dropdownToggle'])
    .config(['$routeProvider',
        function($routeProvider) {
            $routeProvider.
            when('/reporting-rate', {  controller: ReportingRateController,  templateUrl: 'partials/reporting-rate.html', reloadOnSearch: false }).
            when('/lab-equipments-by-location', {controller:LabEquipmentStatusByLocationController, templateUrl:'partials/lab-equipments-by-location.html',reloadOnSearch:false}).
            when('/mnch-status', {controller:MNCHStatusController, templateUrl:'partials/mnch-status.html',reloadOnSearch:false}).
            when('/stock-status', {controller:StockStatusController, templateUrl:'partials/stock-status.html',reloadOnSearch:false}).
            otherwise({
                redirectTo: '/list'
            });
        }
    ]).config(function(angularCombineConfigProvider) {
            angularCombineConfigProvider.addConf(/filter-/, '/public/pages/reports/shared/filters.html');
        });
