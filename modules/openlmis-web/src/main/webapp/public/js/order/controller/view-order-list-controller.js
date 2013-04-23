/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ViewOrderListController($scope, orders) {
  $scope.orders = orders;

  $scope.downloadOrderCSV = function(orderId){

  }

  $scope.gridOptions = { data: 'orders',
    showFooter: false,
    showColumnMenu: false,
    showFilter: false,
    sortInfo: { field: 'createdDate', direction: 'DESC'},

    columnDefs: [
      {field: 'rnr.id', displayName: 'Order No.'},
      {field: 'facilityCode', displayName: 'Facility Code-Name', cellTemplate:"<div class='ngCellText'><span ng-cell-text>{{row.entity.rnr.facilityCode}} - {{row.entity.rnr.facilityName}}</span></div>"},
      {field: 'rnr.programName', displayName: "Program Name"},
      {field: 'periodName', displayName: "Period", cellTemplate:"<div class='ngCellText'><span ng-cell-text>{{row.entity.rnr.periodName}} ({{row.entity.rnr.periodStartDate | date: 'dd/MM/yyyy'}} - {{row.entity.rnr.periodEndDate | date: 'dd/MM/yyyy'}})</span></div>"},
      {field: 'rnr.supplyingDepot', displayName: "Supplying Depot"},
      {field: 'createdDate', displayName: "Order Date/Time", cellFilter: "date:'dd/MM/yyyy hh:mm:ss'"},
      {field: 'status', displayName: "Order Status",
        cellTemplate:"<div class='ngCellText'><span ng-cell-text><div id=\"shippedOrderStaus\" ng-show=\"row.entity.fulfilled\">SHIPPED</div><div id=\"releasedOrderStaus\" ng-show=\"!row.entity.fulfilled\">RELEASED</div></span></div>"},
      {cellTemplate: "<a ng-href='/orders/{{row.entity.id}}/download.csv'>Download CSV</a>"}
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