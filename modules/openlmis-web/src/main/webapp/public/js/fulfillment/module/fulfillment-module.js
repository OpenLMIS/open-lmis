/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

angular.module('fulfillment', ['openlmis', 'ngGrid', 'ui.bootstrap.dialog', 'ui.bootstrap.accordion', 'ui.bootstrap.pagination', 'ui.bootstrap.dropdownToggle']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/view-orders', {controller: ViewOrderListController, templateUrl: 'order/partials/view-order.html', resolve: ViewOrderListController.resolve,  reloadOnSearch: false}).
    when('/manage-pod-orders', {controller: ManagePODController, templateUrl: 'pod/partials/manage-pod.html'}).
    when('/pods/:id', {controller: PODController, templateUrl: 'pod/partials/pod.html', resolve: PODController.resolve, reloadOnSearch: false}).
    otherwise({redirectTo: '/view-orders'});
}]).directive('select2Blur', function () {
  return function (scope, elm, attrs) {
    angular.element("body").on('mousedown', function (e) {
      $('.select2-dropdown-open').each(function () {
        if (!$(this).hasClass('select2-container-active')) {
          $(this).data("select2").blur();
        }
      });
    });
  };
});