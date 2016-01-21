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

var staticPages=angular.module('staticPages', ['openlmis','ui.bootstrap.modal','ui.bootstrap.dialog', 'textAngular','ui.bootstrap.dropdownToggle']);
staticPages.config(['$routeProvider', function ($routeProvider) {

    $routeProvider.
//        en
        when('/home', {controller: StaticPageController, templateUrl: 'partials/home.html'}).
        when('/aboutUs', {controller: StaticPageController, templateUrl: 'partials/about-us.html'}).
        when('/about-elmis', {controller: StaticPageController, templateUrl: 'partials/about-elmis.html'}).
        when('/about-ehealth', {controller: StaticPageController, templateUrl: 'partials/about-ehealth.html'}).
        when('/about-openlmis', {controller: StaticPageController, templateUrl: 'partials/about-openlmis.html'}).
//       es
        when('/home_es', {controller: StaticPageController, templateUrl: 'partials/home.html'}).
        when('/aboutUs_es', {controller: StaticPageController, templateUrl: 'partials/about-us_es.html'}).
        when('/about-elmis_es', {controller: StaticPageController, templateUrl: 'partials/about-elmis_es.html'}).
        when('/about-ehealth_es', {controller: StaticPageController, templateUrl: 'partials/about-ehealth_es.html'}).
        when('/about-openlmis_es', {controller: StaticPageController, templateUrl: 'partials/about-openlmis_es.html'}).
//        pt
        when('/home_pt', {controller: StaticPageController, templateUrl: 'partials/home_pt.html'}).
        when('/aboutUs_pt', {controller: StaticPageController, templateUrl: 'partials/about-us_pt.html'}).
        when('/about-elmis_pt', {controller: StaticPageController, templateUrl: 'partials/about-elmis_pt.html'}).
        when('/about-ehealth_pt', {controller: StaticPageController, templateUrl: 'partials/about-ehealth_pt.html'}).
        when('/about-openlmis_pt', {controller: StaticPageController, templateUrl: 'partials/about-openlmis_pt.html'}).
        //        Order Processing
        when('/delivery', {controller: StaticPageController, templateUrl: 'partials/order-processing/delivery.html'}).
        when('/order-fullfilment', {controller: StaticPageController, templateUrl: 'partials/order-processing/order-fullfilment.html'}).
        when('/proof-of-delivery', {controller: StaticPageController, templateUrl: 'partials/order-processing/proof-of-delivery.html'}).
        when('/randr-submisssions', {controller: StaticPageController, templateUrl: 'partials/order-processing/RandR-submissions.html'}).

        //        Reports
        when('/consumption-trend', {controller: StaticPageController, templateUrl: 'partials/reports/consumption-trend.html'}).
        when('/dashboard', {controller: StaticPageController, templateUrl: 'partials/reports/dashboard.html'}).
        when('/gis-maps', {controller: StaticPageController, templateUrl: 'partials/reports/gis-maps.html'}).
        when('/stock-status-summary', {controller: StaticPageController, templateUrl: 'partials/reports/stock-status-summary.html'}).

        otherwise({redirectTo: '/home'});
}]);
