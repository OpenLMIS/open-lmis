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

angular.module('program-equipment-product', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dialog']).
  config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
      when('/list', {controller: ProgramEquipmentProductController, templateUrl: 'partials/list.html'}).
      otherwise({redirectTo: '/list'});
  }]).directive('onKeyup', function () {
    return function (scope, elm, attrs) {
      elm.bind("keyup", function () {
        scope.$apply(attrs.onKeyup);
      });
    };
  })
  .directive('select2Blur', function () {
    return function (scope, elm, attrs) {
      angular.element("body").on('mousedown', function (e) {
        $('.select2-dropdown-open').each(function () {
          if (!$(this).hasClass('select2-container-active')) {
            $(this).data("select2").blur();
          }
        });
      });
    };
  })
  .run(function ($rootScope, AuthorizationService) {
    $rootScope.manageProgramEquipmentProductSelected = "selected";
    AuthorizationService.preAuthorize('MANAGE_EQUIPMENT_SETTINGS');
  });

