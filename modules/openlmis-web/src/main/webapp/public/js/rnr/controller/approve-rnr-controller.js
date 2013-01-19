function ApproveRnrController($scope, requisitionList) {
  $scope.requisitions = requisitionList;

  function dateTemplate(model) {
    var divElement = '<div>{{' + model + "| date:'dd/MM/yyyy'" + '}}</div>';
    return divElement;
  }

  $scope.gridOptions = { data:'requisitions',
    canSelectRows:false,
    displayFooter:false,
    displaySelectionCheckbox:false,
    showColumnMenu:false,
    sortInfo:{ field:'submittedDate', direction:'ASC'},
    columnDefs:[
      {field:'programName', displayName:'Program' },
      {field:'facilityCode', displayName:'Facility Code'},
      {field:'facilityName', displayName:"Facility Name"},
      {field:'periodStartDate', displayName:"Period Start Date", cellFilter:"date", cellTemplate:dateTemplate('COL_FIELD')},
      {field:'periodEndDate', displayName:"Period End Date", cellFilter:"date", cellTemplate:dateTemplate('COL_FIELD')},
      {field:'submittedDate', displayName:"Date Submitted", cellFilter:"date", cellTemplate:dateTemplate('COL_FIELD')},
      {field:'modifiedDate', displayName:"Date Modified", cellFilter:"date", cellTemplate:dateTemplate('COL_FIELD')}
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