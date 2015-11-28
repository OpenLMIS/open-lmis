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
angular.module('vaccine-report-create', ['openlmis', 'ngGrid', 'angularCombine', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle', 'ui.bootstrap.dialog']).
    config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/create/:id', {
                controller: CreateVaccineReportController,
                templateUrl: 'partials/create.html',
                resolve: CreateVaccineReportController.resolve
            }).
            when('/list', {
                controller: VaccineReportController,
                templateUrl: 'partials/list.html',
                resolve: VaccineReportController.resolve
            }).
            when('/view/:id', {
                controller: ViewVaccineReportDetailController,
                templateUrl: 'partials/view.html',
                resolve: ViewVaccineReportDetailController.resolve
            }).
            when('/view', {
                controller: ViewVaccineReportController,
                templateUrl: 'partials/view-list.html',
                resolve: ViewVaccineReportController.resolve
            }).
            when('/bundled-distribution-vaccination-supplies', {
                controller: ViewbundledDistributionVacinationSuppliesController,
                templateUrl: 'partials/view/bundled-distribution-vacination-supplies.html'
            }).
            when('/performance-by-dropout-rate-by-district', {
                controller: ViewPerformanceByDropoutRateByDistrictController,
                templateUrl: 'partials/view/performance-by-dropout-rate-by-district.html',reloadOnSearch:false
            }).

            otherwise({redirectTo: '/list'});
    }]).config(function (angularCombineConfigProvider) {
        angularCombineConfigProvider.addConf(/filter-/, '/public/pages/reports/shared/filters.html');
        angularCombineConfigProvider. addConf(/filter2-/, '/public/pages/vaccine/shared/vaccine-filter.html');

    }).config(function (angularCombineConfigProvider) {

    });
