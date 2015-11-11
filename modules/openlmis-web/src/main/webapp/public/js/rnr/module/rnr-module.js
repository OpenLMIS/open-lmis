/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

var rnrModule = angular.module('rnr', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle', 'ui.bootstrap.dialog']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/init-rnr', {controller: InitiateRnrController, templateUrl: 'partials/create/init.html', resolve: InitiateRnrController.resolve}).
    when('/create-rnr/:rnr/:facility/:program', {controller: CreateRequisitionController, templateUrl: 'partials/create/index.html', resolve: CreateRequisitionController.resolve, reloadOnSearch: false}).
    when('/rnr-for-approval', {controller: ApproveRnrListController, templateUrl: 'partials/approve/list-for-approval.html', resolve: ApproveRnrListController.resolve}).
    when('/requisitions-for-convert-to-order', {controller: ConvertToOrderListController, templateUrl: 'partials/convert-to-order-list.html', reloadOnSearch: false}).
    when('/view-requisitions', {controller: ViewRnrListController, templateUrl: 'partials/view/index.html', resolve: ViewRnrListController.resolve}).
    when('/rnr-for-approval/:rnr/:program', {controller: ApproveRnrController, templateUrl: 'partials/approve/approve.html', resolve: ApproveRnrController.resolve, reloadOnSearch: false}).
    when('/requisition/:rnr/:program', {controller: ViewRnrController, templateUrl: 'partials/view/view.html', resolve: ViewRnrController.resolve, reloadOnSearch: false}).
    when('/view-requisition-via/:rnr/:program', {controller: ViewRnrViaDetailController, templateUrl: 'partials/view/rnr-via-view.html', reloadOnSearch: false}).
    when('/view-requisition-mmia/:rnr/:program', {controller: ViewRnrMmiaController, templateUrl: 'partials/view/rnr-mmia-view.html', reloadOnSearch: false}).
    otherwise({redirectTo: '/init-rnr'});
}]);