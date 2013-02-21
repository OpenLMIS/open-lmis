function ViewRnrController($scope, facilities, RequisitionsForViewing, UserSupportedProgramInFacilityForAnOperation) {
  $scope.facilities = facilities;
  $scope.facilityLabel = (!$scope.facilities.length) ? "--none assigned--" : "--select facility--";
  $scope.programLabel = "--select program--";
  $scope.selectedItems = [];

  $scope.rnrListGrid = { data:'filteredRequisitions',
    canSelectRows:false,
    displayFooter:false,
    displaySelectionCheckbox:false,
    showColumnMenu:false,
    showFilter:false,
    rowHeight:44,
    enableSorting:true,
    sortInfo:{ field:'submittedDate', direction:'ASC'},
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
    UserSupportedProgramInFacilityForAnOperation.get({facilityId:$scope.selectedFacilityId, rights:"VIEW_REQUISITION"},
      function (data) {
        $scope.programs = data.programList;
        $scope.programLabel = (!$scope.programs.length) ? "--none assigned--" : "All";
      }, function () {
      })
  };

  function setRequisitionsFoundMessage() {
    $scope.requisitionFoundMessage = ($scope.requisitions.length) ? "" : "No R&Rs found";
  }

  $scope.filterRequisitions = function () {
    $scope.filteredRequisitions = [];
    var query = $scope.query || "";

    $scope.filteredRequisitions = $.grep($scope.requisitions, function (rnr) {
      return contains(rnr.status, query);
    });

    $scope.resultCount = $scope.filteredRequisitions.length;
  };

  function contains(string, query) {
    return string.toLowerCase().indexOf(query.toLowerCase()) != -1;
  }

  $scope.loadRequisitions = function () {
    if ($scope.viewRequisitionForm.$invalid) {
      $scope.errorShown = true;
      return;
    }
    var requisitionQueryParameters = {facilityId:$scope.selectedFacilityId,
      dateRangeStart:$scope.startDate, dateRangeEnd:$scope.endDate};

    if ($scope.selectedProgramId) requisitionQueryParameters.push('programId', $scope.selectedProgramId);

    RequisitionsForViewing.get(requisitionQueryParameters, function (data) {

      $scope.requisitions = $scope.filteredRequisitions = data.rnr_list;

      setRequisitionsFoundMessage();
    }, function () {
    })
  };
  $scope.setEndDateOffset = function () {
    $scope.endDateOffset = Math.ceil(($scope.startDate.getTime() + oneDay - Date.now()) / oneDay);
  };
}

var oneDay = 1000 * 60 * 60 * 24;

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