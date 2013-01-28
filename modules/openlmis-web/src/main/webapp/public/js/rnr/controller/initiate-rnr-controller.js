function InitiateRnrController($scope, $location, $rootScope, Requisition, PeriodsForFacilityAndProgram) {

  var DEFAULT_FACILITY_MESSAGE = '--choose facility--';
  var DEFAULT_PROGRAM_MESSAGE = '--choose program--';
  var PREVIOUS_RNR_PENDING_STATUS = "Previous R&R pending";
  var RNR_NOT_YET_STARTED_STATUS = "Not yet started";


  $scope.periodGridData = [];
  $scope.selectedProgram = null;
  $scope.selectedFacilityId = null;
  $scope.selectedPeriod = null;

  var getPeriodSpecificButton = function (activeForRnr) {
    return '<input type="button" ng-click="initRnr()" value="Proceed" class="btn btn-primary btn-small grid-btn" ng-show="' + activeForRnr + '"/>';
  };

  var optionMessage = function (entity, defaultMessage) {
    return entity == null || entity.length == 0 ? "--none assigned--" : defaultMessage;
  };

  var resetValuesForFirstPeriod = function (periodGridData) {
    var firstPeriodWithRnrStatus = periodGridData[0];
    firstPeriodWithRnrStatus.activeForRnr = true;
    if (!firstPeriodWithRnrStatus.rnrId) {
      firstPeriodWithRnrStatus.rnrStatus = RNR_NOT_YET_STARTED_STATUS;
    }
  };

  var createPeriodWithRnrStatus = function (periods, rnr) {
    $scope.periodGridData = [];
    $scope.selectedPeriod = null;

    var periodWithRnrStatus;
    if (periods == null || periods.length == 0) {
      periodWithRnrStatus = new Object();
      periodWithRnrStatus.name = "No period(s) available";
      $scope.selectedPeriod = null;
      $scope.periodGridData.push(periodWithRnrStatus);
      return;
    }

    $scope.selectedPeriod = periods[0];
    periods.forEach(function (period) {
      periodWithRnrStatus = angular.copy(period);
      periodWithRnrStatus.rnrStatus = PREVIOUS_RNR_PENDING_STATUS;
      if (rnr != null && periodWithRnrStatus.id == rnr.periodId) {
        periodWithRnrStatus.rnrId = rnr.id;
        periodWithRnrStatus.rnrStatus = rnr.status;
      }
      $scope.periodGridData.push(periodWithRnrStatus);
    });

    resetValuesForFirstPeriod($scope.periodGridData);
  };

  $scope.periodGridOptions = { data:'periodGridData',
    canSelectRows:false,
    displayFooter:false,
    displaySelectionCheckbox:false,
    enableColumnResize:true,
    enableColumnReordering:true,
    showColumnMenu:false,
    showFilter:false,
    columnDefs:[
      {field:'name', displayName:'Period(s)'},
      {field:'startDate', displayName:'Start Date', cellFilter:"date:'dd/MM/yyyy'" },
      {field:'endDate', displayName:'End Date', cellFilter:"date:'dd/MM/yyyy'" },
      {field:'rnrStatus', displayName:'R&R Status' },
      {cellTemplate:getPeriodSpecificButton('row.entity.activeForRnr')}
    ]
  };

  $scope.facilityOptionMessage = function () {
    return optionMessage($scope.facilities, DEFAULT_FACILITY_MESSAGE);
  };

  $scope.programOptionMessage = function () {
    return optionMessage($scope.programs, DEFAULT_PROGRAM_MESSAGE);
  };

  $scope.loadPeriods = function () {
    if ($scope.selectedProgram && $scope.selectedFacilityId) {
      PeriodsForFacilityAndProgram.get({facilityId:$scope.selectedFacilityId, programId:$scope.selectedProgram.id},
          function (data) {
            $scope.error = "";
            createPeriodWithRnrStatus(data.periods, data.rnr);
          },
          function (data) {
            $scope.error = data.data.error;
            $scope.selectedPeriod = null;
            $scope.periodGridData = [];
          });
    } else {
      $scope.error = "";
      $scope.selectedPeriod = null;
      $scope.periodGridData = [];
    }
  };

  $scope.initRnr = function () {
    if (!($scope.selectedProgram && $scope.selectedPeriod)) {
      $scope.error = "Please select Facility, Program and Period to proceed";
      return;
    }

    $scope.error = "";
    $scope.$parent.sourceUrl = $location.$$url;

    Requisition.get({facilityId:$scope.selectedFacilityId, programId:$scope.selectedProgram.id, periodId:$scope.selectedPeriod.id}, {},
        function (data) {
          if((data.rnr == null || data.rnr == undefined) && !$rootScope.hasPermission('CREATE_REQUISITION')){
            $scope.error = "An R&R has not been initiated yet";
            return;
          }

          if (data.rnr) {
            if (data.rnr.status != 'SUBMITTED' && !$rootScope.hasPermission('CREATE_REQUISITION')) {
              $scope.error = "An R&R has not been submitted yet";
              return;
            }
            $scope.$parent.rnr = data.rnr;
            $scope.$parent.program = $scope.selectedProgram;
            $scope.$parent.period = $scope.selectedPeriod;
            $location.path('/create-rnr/' + $scope.selectedFacilityId + '/' + $scope.selectedProgram.id + '/' + $scope.selectedPeriod.id);
          }
          else {
            Requisition.save({facilityId:$scope.selectedFacilityId, programId:$scope.selectedProgram.id, periodId:$scope.selectedPeriod.id}, {}, function (data) {
              $scope.$parent.rnr = data.rnr;
              $scope.$parent.program = $scope.selectedProgram;
              $scope.$parent.period = $scope.selectedPeriod;
              $location.path('/create-rnr/' + $scope.selectedFacilityId + '/' + $scope.selectedProgram.id + '/' + $scope.selectedPeriod.id);
            }, function (data) {
              $scope.error = data.data.error ? data.data.error : "Requisition does not exist. Please initiate.";
            })
          }
        }, {});
  };
}
