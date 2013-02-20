function ViewRnrController($scope, facilities, RequisitionsForViewing, UserSupportedProgramInFacilityForAnOperation) {
  $scope.facilities = facilities;
  $scope.selectedItems = [];

  $scope.rnrListGrid = { data:'rnr',
    canSelectRows:false,
    displayFooter:false,
    displaySelectionCheckbox:false,
    showColumnMenu:false,
    showFilter:false,
    rowHeight:44,
    enableSorting:true,
    columnDefs:[
      {field:'programName', displayName:'Program' },
      {field:'facilityCode', displayName:'Facility Code'},
      {field:'facilityName', displayName:"Facility Name"},
      {field:'periodStartDate', displayName:"Period Start Date", cellFilter:"date:'dd/MM/yyyy'"},
      {field:'periodEndDate', displayName:"Period End Date", cellFilter:"date:'dd/MM/yyyy'"},
      {field:'submittedDate', displayName:"Date Submitted", cellFilter:"date:'dd/MM/yyyy'"},
      {field:'modifiedDate', displayName:"Date Modified", cellFilter:"date:'dd/MM/yyyy'"},
      {field:'status', displayName:'Status'}
    ]
  };

  $scope.loadProgramsForFacility = function () {
    UserSupportedProgramInFacilityForAnOperation.get({facilityId: $scope.selectedFacilityId, rights: "VIEW_REQUISITION"},
      function(data) {
      $scope.programs = data.programList;
    }, function(){})
  };

  $scope.loadRequisitions = function(){
    if($scope.viewRequisitionForm.$invalid){
      $scope.errorShown = true;
      return;
    }
    RequisitionsForViewing.get({facilityId:$scope.selectedFacilityId, programId:$scope.selectedProgramId,
      periodStartDate: $scope.startDate, periodEndDate: $scope.endDate}, function (data) {
      $scope.rnr = data.rnr_list;
    }, function() {})
  }
}

ViewRnrController.resolve = {
  facilities:function ($q, $timeout, UserFacilityWithViewRequisition) {
    var deferred = $q.defer();
    $timeout(function () {
      UserFacilityWithViewRequisition.get({}, function (data) {
        deferred.resolve(data.facilities);
      }, {});
    }, 100);
    return deferred.promise;
  }
};