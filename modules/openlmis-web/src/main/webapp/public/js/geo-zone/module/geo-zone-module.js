/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

var geoZoneModule = angular.module('geo-zone', ['openlmis', 'ui.bootstrap.modal','leaflet-directive', 'ui.bootstrap.dialog', 'ui.bootstrap.dropdownToggle', 'ui.bootstrap.pagination']).
    config(['$routeProvider', function ($routeProvider) {
      $routeProvider.
          when('/search', {controller: GeoZoneSearchController, templateUrl: 'partials/search.html', reloadOnSearch: false}).
          when('/create-geo-zone', {controller: GeoZoneController, templateUrl: 'partials/create.html', resolve: GeoZoneController.resolve}).
          when('/json', {controller: GeographicZonesJsonController, templateUrl: 'partials/json.html'}).
          when('/edit/:id', {controller: GeoZoneController, templateUrl: 'partials/create.html', resolve: GeoZoneController.resolve}).
          otherwise({redirectTo: '/search'});
    }]).run(function ($rootScope, AuthorizationService) {
      $rootScope.geoZoneSelected = "selected";
      AuthorizationService.preAuthorize('MANAGE_GEOGRAPHIC_ZONE');
    });
