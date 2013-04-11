function ViewOrdersController($scope, orders, Orders) {
  $scope.orders = orders;

  $scope.gridOptions = { data: 'orders',
    selectedItems: $scope.selectedItems,
    showFooter: false,
    showColumnMenu: false,
    sortInfo: { field: 'submittedDate', direction: 'ASC'},
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

ViewOrdersController.resolve = {
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