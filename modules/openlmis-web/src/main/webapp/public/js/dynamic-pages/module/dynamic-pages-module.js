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

angular.module('public', ['openlmis', 'ngTable', 'angularCombine', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/regions', {controller: RegionsController, templateUrl: 'partials/regions.html' }).
        when('/districts', {controller: DistrictsController, templateUrl: 'partials/districts.html' }).
        when('/facilities', {controller: FacilitiesController, templateUrl: 'partials/facilities.html' }).
        when('/product-arv', {controller: ARVProductsController, templateUrl: 'partials/product-arv.html' }).
        when('/product-ils', {controller: ILSProductsController, templateUrl: 'partials/product-ils.html' }).
        when('/product-tb', {controller: TBProductsController, templateUrl: 'partials/product-tb.html' }).
        when('/product-national-lab', {controller: NationalLabProductsController, templateUrl: 'partials/product-national-lab.html' }).
        when('/product-regional-lab', {controller: RegionalLabProductsController, templateUrl: 'partials/product-regional-lab.html' }).
        when('/product-zonal-lab', {controller: ZonalLabProductsController, templateUrl: 'partials/product-zonal-lab.html' }).
        when('/product-district-lab', {controller: DistrictLabProductsController, templateUrl: 'partials/product-district-lab.html' }).
        when('/reports', {controller: ReportsListController, templateUrl: 'partials/reports.html' }).

        otherwise({redirectTo: '/regions'});
}]);
