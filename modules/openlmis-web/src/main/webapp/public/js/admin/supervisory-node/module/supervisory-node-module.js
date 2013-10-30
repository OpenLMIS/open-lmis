  /*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

angular.module('supervisory-node', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dialog']).
    config(['$routeProvider', function ($routeProvider) {
      $routeProvider.
        when('/list', {controller: SupervisoryNodeListController, templateUrl: 'partials/list.html'}).
        when('/create-supervisory-node', {controller: SupervisoryNodeController, templateUrl: 'partials/create.html',resolve:SupervisoryNodeController.resolve}).
        when('/edit/:supervisoryNodeId', {controller: SupervisoryNodeController, templateUrl: 'partials/create.html',resolve:SupervisoryNodeController.resolve}).
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
      $rootScope.supervisoryNodeSelected = "selected";
      AuthorizationService.preAuthorize('MANAGE_SUPERVISORY_NODE');
    });

