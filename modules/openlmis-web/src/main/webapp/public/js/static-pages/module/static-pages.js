/*
 *This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.

 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.

 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

var staticPages=angular.module('staticPages', ['openlmis','ui.bootstrap.modal','ui.bootstrap.dialog', 'ui.bootstrap.dropdownToggle']);
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
        otherwise({redirectTo: '/home'});
}]);
