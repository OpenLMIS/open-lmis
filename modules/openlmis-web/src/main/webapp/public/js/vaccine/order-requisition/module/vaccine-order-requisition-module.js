
/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

angular.module('vaccine_order_requisition', ['openlmis', 'ngGrid', 'ui.bootstrap.dialog', 'ui.bootstrap.accordion', 'ui.bootstrap.pagination', 'ui.bootstrap.dropdownToggle'])
    .config(['$routeProvider', function ($routeProvider) {

        $routeProvider.
        when('/order-requisition', {controller:VaccineOrderRequisitionController, templateUrl:'partials/order-requisition.html',resolve:VaccineOrderRequisitionController.resolve,  reloadOnSearch: false}).
        when('/initiate', {controller:newVaccineOrderRequisitionController, templateUrl:'partials/initiate.html',resolve:newVaccineOrderRequisitionController.resolve,  reloadOnSearch: false}).
        when('/create/:id/:programId',{controller:CreateVaccineOrderRequisition,templateUrl:'partials/order-requisition.html',resolve:CreateVaccineOrderRequisition.resolve,reloadOnSearch:false}).
        when('/search', {controller:ViewOrderRequisitionList, templateUrl:'partials/view/view.html',resolve:ViewOrderRequisitionList.resolve,  reloadOnSearch: false}).
        when('/view-order-requisition/:id/:facility', {controller:VaccineOrderRequisitionController, templateUrl:'partials/view-order.html',resolve:VaccineOrderRequisitionController.resolve,  reloadOnSearch: false}).
        when('/details', {controller:newVaccineOrderRequisitionController, templateUrl:'partials/details.html',resolve:newVaccineOrderRequisitionController.resolve,  reloadOnSearch: false}).
        when('/view', {controller:ViewVaccineOrderRequisitionController, templateUrl:'partials/view.html',resolve:ViewVaccineOrderRequisitionController.resolve,  reloadOnSearch: false}).
        otherwise({redirectTo: '/order-requisition-list'});
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
}).filter('orderObjectBy', function(){
        return function(input, attribute) {
            if (!angular.isObject(input)) return input;

            var array = [];
            for(var objectKey in input) {
                array.push(input[objectKey]);
            }
            array.sort(function(a, b){
                a = a[attribute];
                b = b[attribute];
                return a - b;
            });
            return array;
        };
    });