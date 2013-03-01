function ApproveRnrListController($scope, requisitionList, $location) {
  $scope.requisitions = requisitionList;
  $scope.filteredRequisitions = $scope.requisitions;
  $scope.selectedItems = [];

  $scope.gridOptions = { data:'filteredRequisitions',
    multiSelect:false,
    selectedItems: $scope.selectedItems,
    afterSelectionChange: function(rowItem, event){$scope.openRnr()},
    displayFooter:false,
    displaySelectionCheckbox:false,
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
      {field:'modifiedDate', displayName:"Date Modified", cellFilter:"date:'dd/MM/yyyy'"}
    ]
  };

  $scope.openRnr = function () {
    $scope.$parent.period = {'startDate':$scope.selectedItems[0].periodStartDate,'endDate':$scope.selectedItems[0].periodEndDate};
    $location.path("rnr-for-approval/"+$scope.selectedItems[0].id+'/'+$scope.selectedItems[0].programId);
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

  function contains(string, query) {
    return string.toLowerCase().indexOf(query.toLowerCase()) != -1;
  }

  function matchesAnyField(query, rnr) {
    var rnrString = "|" + rnr.programName + "|" + rnr.facilityName + "|" + rnr.facilityCode + "|";
    return contains(rnrString, query);
  }
}

ApproveRnrListController.resolve = {
  requisitionList:function ($q, $timeout, RequisitionForApproval) {
    var deferred = $q.defer();
    $timeout(function () {
      RequisitionForApproval.get({}, function (data) {
        deferred.resolve(data.rnr_list);
      }, {});
    }, 100);
    return deferred.promise;
  }
};