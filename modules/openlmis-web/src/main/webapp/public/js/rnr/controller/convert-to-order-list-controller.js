function ConvertToOrderListController($scope, requisitionList, Order, RequisitionForConvertToOrder) {
  $scope.requisitions = requisitionList;
  $scope.filteredRequisitions = $scope.requisitions;
  $scope.selectedItems = [];

  $scope.gridOptions = { data:'filteredRequisitions',
    multiSelect:true,
    selectedItems:$scope.selectedItems,
    displayFooter:false,
    displaySelectionCheckbox:true,
    showColumnMenu:false,
    sortInfo:{ field:'submittedDate', direction:'ASC'},
    showFilter:false,
    columnDefs:[
      {field:'programName', displayName:'Program' },
      {field:'facilityCode', displayName:'Facility Code'},
      {field:'facilityName', displayName:"Facility Name"},
      {field:'periodStartDate', displayName:"Period Start Date", cellFilter:"date:'dd/MM/yyyy'"},
      {field:'periodEndDate', displayName:"Period End Date", cellFilter:"date:'dd/MM/yyyy'"},
      {field:'submittedDate', displayName:"Date Submitted", cellFilter:"date:'dd/MM/yyyy'"},
      {field:'modifiedDate', displayName:"Date Modified", cellFilter:"date:'dd/MM/yyyy'"},
      {field:'supplyingDepot', displayName:"Supplying Depot"}
    ]
  };

  $scope.filterRequisitions = function () {
    $scope.filteredRequisitions = [];
    var query = $scope.query || "";
    var searchField = $scope.searchField;

    $scope.filteredRequisitions = $.grep($scope.requisitions, function (rnr) {
      return (searchField) ? contains(rnr[searchField], query) : matchesAnyField(query, rnr);
    });

    $scope.resultCount = $scope.filteredRequisitions.length;
  };

  $scope.convertToOrder = function () {
    var successHandler = function () {
      RequisitionForConvertToOrder.get({}, function(data) {
        $scope.requisitions = data.rnr_list;
        $scope.filteredRequisitions();
      });

      $scope.message = "Created successfully";
      $scope.error = "";
    };

    var errorHandler = function () {
      $scope.error = "Error Occurred";
    };

    $scope.order={"rnrList":$scope.gridOptions.selectedItems};
    Order.save({}, $scope.order, successHandler, errorHandler);
  };

  function contains(string, query) {
    return string.toLowerCase().indexOf(query.toLowerCase()) != -1;
  }

  function matchesAnyField(query, rnr) {
    var rnrString = "|" + rnr.programName + "|" + rnr.facilityCode + "|" + "|" + rnr.facilityName + "|" + "|" + rnr.supplyingDepot + "|";
    return contains(rnrString, query);
  }
}

ConvertToOrderListController.resolve = {
  requisitionList:function ($q, $timeout, RequisitionForConvertToOrder) {
    var deferred = $q.defer();
    $timeout(function () {
      RequisitionForConvertToOrder.get({}, function (data) {
        deferred.resolve(data.rnr_list);
      }, {});
    }, 100);
    return deferred.promise;
  }
};