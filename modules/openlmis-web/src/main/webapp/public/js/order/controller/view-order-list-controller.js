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

    columnDefs: [
      {field: 'id', displayName: 'Order No.', cellTemplate:"<div class='ngCellText'><span ng-cell-text>ORD{{row.entity.id}}</span></div>"},
      {field: 'facilityCode', displayName: 'Facility Code-Name', cellTemplate:"<div class='ngCellText'><span ng-cell-text>{{row.entity.facilityCode}} - {{row.entity.facilityName}}</span></div>"},
      {field: 'programName', displayName: "Program Name"},
      {field: 'periodName', displayName: "Period", cellTemplate:"<div class='ngCellText'><span ng-cell-text>{{row.entity.periodName}} ({{row.entity.periodStartDate | date: 'dd/MM/yyyy'}} - {{row.entity.periodEndDate | date: 'dd/MM/yyyy'}})</span></div>"},
      {field: 'supplyingDepot', displayName: "Supplying Depot"},
      {field: 'modifiedDate', displayName: "Order Date/Time", cellFilter: "date:'dd/MM/yyyy hh:mm:ss'"},
      {field: 'status', displayName: "Order Status"}
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