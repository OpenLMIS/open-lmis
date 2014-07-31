/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

angular.module('report', ['openlmis', 'ui.bootstrap.modal', 'ui.bootstrap.dialog']).
  config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
      when('/create', {controller: CreateReportController, templateUrl: 'partials/create.html'}).
      when('/list', {controller: ListReportController, templateUrl: 'partials/list.html', resolve: ListReportController.resolve}).
      when('/reports/:id', {controller: ViewReportController, templateUrl: 'partials/view.html', resolve: ViewReportController.resolve}).
      otherwise({redirectTo: '/list'});
  }]).run(function ($rootScope, AuthorizationService) {
    AuthorizationService.preAuthorizeReporting();
  });
