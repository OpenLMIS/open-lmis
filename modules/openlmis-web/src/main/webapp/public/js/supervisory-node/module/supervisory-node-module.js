/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

var supervisoryNodeModule = angular.module('supervisoryNode', ['openlmis', 'ui.bootstrap.dropdownToggle', 'ui.bootstrap.modal', 'ui.bootstrap.pagination']);

supervisoryNodeModule.config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
      when('/search', {controller: SupervisoryNodeSearchController, templateUrl: 'partials/search.html', reloadOnSearch: false}).
      when('/create-supervisory-node', {controller: SupervisoryNodeController, templateUrl: 'partials/create.html', resolve: SupervisoryNodeController.resolve}).
      when('/edit/:id', {controller: SupervisoryNodeController, templateUrl: 'partials/create.html', resolve: SupervisoryNodeController.resolve}).
      otherwise({redirectTo: '/search'});
}]).run(function ($rootScope, AuthorizationService) {
  $rootScope.supervisoryNodeSelected = "selected";
  AuthorizationService.preAuthorize('MANAGE_SUPERVISORY_NODE');
});
