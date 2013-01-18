function ApproveRnrController($scope, requisitionList) {
  $scope.requisitions = requisitionList;

  $scope.gridOptions = { data:'requisitions',
    canSelectRows:false,
    displayFooter:false,
    displaySelectionCheckbox:false,
    showColumnMenu:false,
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
}

ApproveRnrController.resolve = {
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