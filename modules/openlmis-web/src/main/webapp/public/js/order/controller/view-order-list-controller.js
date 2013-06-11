/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ViewOrderListController($scope, orders) {
  $scope.orders = orders;

  $scope.gridOptions = { data: 'orders',
    showFooter: false,
    showColumnMenu: false,
    showFilter: false,
    enableColumnResize:true,
    enableSorting: false,
    columnDefs: [
      {field: 'rnr.id', displayName: 'Order No.'},
      {field: 'facilityCode', displayName: 'Facility Code-Name', cellTemplate:"<div class='ngCellText'><span ng-cell-text>{{row.entity.rnr.facilityCode}} - {{row.entity.rnr.facilityName}}</span></div>"},
      {field: 'rnr.programName', displayName: "Program Name"},
      {field: 'periodName', displayName: "Period", cellTemplate:"<div class='ngCellText'><span ng-cell-text>{{row.entity.rnr.periodName}} ({{row.entity.rnr.periodStartDate | date: 'dd/MM/yyyy'}} - {{row.entity.rnr.periodEndDate | date: 'dd/MM/yyyy'}})</span></div>"},
      {field: 'rnr.supplyingDepot', displayName: "Supplying Depot"},
      {field: 'createdDate', displayName: "Order Date/Time", cellFilter: "date:'dd/MM/yyyy hh:mm:ss'"},
      {field: 'status', displayName: "Order Status",
        cellTemplate:"<div class='ngCellText'><span ng-cell-text><div id=\"orderStatus\">{{row.entity.status}} <span ng-show='row.entity.shipmentError' >(Shipment File Error)</span></div> "},
      {cellTemplate: "<div class='ngCellText'><a ng-show=\"row.entity.productsOrdered\" ng-href='/orders/{{row.entity.id}}/download.csv'>Download CSV</a>" +
        "<span ng-show=\"!row.entity.productsOrdered\"  ng-cell-text>No products in this order</span></div>"}
    ]
  };
}

ViewOrderListController.resolve = {
  orders: function ($q, $timeout, Orders) {
    var deferred = $q.defer();
    $timeout(function () {
      Orders.get({}, function (data) {
        deferred.resolve(data.orders);
      }, {});
    }, 100);
    return deferred.promise;
  }
}