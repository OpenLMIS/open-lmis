/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function InitiateRnrController($scope, $location, $rootScope, Requisitions, PeriodsForFacilityAndProgram, UserFacilityList, CreateRequisitionProgramList, UserSupervisedFacilitiesForProgram, FacilityProgramRights, navigateBackService, messageService) {

  $rootScope.fullScreen = false;
  var isNavigatedBack;

  $scope.selectedRnrType = {"name": "Regular", "emergency": false};
  $scope.rnrTypes = {"types": [
    {"name": messageService.get("requisition.type.regular"), "emergency": false},
    {"name": messageService.get("requisition.type.emergency"), "emergency": true}
  ]};

  var resetRnrData = function () {
    $scope.periodGridData = [];
    $scope.selectedProgram = null;
    $scope.selectedFacilityId = null;
    $scope.selectedPeriod = null;
    $scope.myFacility = null;
    $scope.programs = null;
    $scope.facilities = null;
    $scope.error = null;
  };

  $scope.$on('$viewContentLoaded', function () {
    $scope.selectedType = navigateBackService.selectedType || 0;
    $scope.selectedProgram = navigateBackService.selectedProgram;
    $scope.selectedFacilityId = navigateBackService.selectedFacilityId;
    isNavigatedBack = navigateBackService.isNavigatedBack;
    $scope.$watch('programs', function () {
      if ($scope.programs && $scope.selectedProgram) {
        $scope.selectedProgram = _.where($scope.programs, {id: $scope.selectedProgram.id})[0];
        $scope.loadPeriods();
      }
    });
    $scope.loadFacilityData($scope.selectedType);
    if (isNavigatedBack) {
      $scope.loadFacilitiesForProgram();
    }
    $scope.$watch('facilities', function () {
      if ($scope.facilities && isNavigatedBack) {
        $scope.selectedFacilityId = navigateBackService.selectedFacilityId;
        isNavigatedBack = false;
      }
    });
  });

  $scope.loadFacilityData = function (selectedType) {
    isNavigatedBack = isNavigatedBack ? (selectedType == "0" ? false : true) : resetRnrData();

    if (selectedType == 0) { //My facility
      UserFacilityList.get({}, function (data) {
        $scope.facilities = data.facilityList;
        $scope.myFacility = data.facilityList[0];
        if ($scope.myFacility) {
          $scope.facilityDisplayName = $scope.myFacility.code + '-' + $scope.myFacility.name;
          $scope.selectedFacilityId = $scope.myFacility.id;

          CreateRequisitionProgramList.get({facilityId: $scope.selectedFacilityId}, function (data) {
            $scope.programs = data.programList;
          }, {});
        } else {
          $scope.facilityDisplayName = messageService.get("label.none.assigned");
          $scope.programs = null;
          $scope.selectedProgram = null;
        }
      }, {});
    } else if (selectedType == 1) { // Supervised facility
      CreateRequisitionProgramList.get({}, function (data) {
        $scope.programs = data.programList;
      }, {});
    }
  };

  $scope.loadFacilitiesForProgram = function () {
    if ($scope.selectedProgram.id) {
      UserSupervisedFacilitiesForProgram.get({programId: $scope.selectedProgram.id}, function (data) {
        $scope.facilities = data.facilities;
        $scope.selectedFacilityId = null;
        $scope.error = null;
      }, {});
    } else {
      $scope.facilities = null;
      $scope.selectedFacilityId = null;
    }
  };

  var getPeriodSpecificButton = function (activeForRnr) {
    return '<input type="button" ng-click="initRnr()" openlmis-message="button.proceed" class="btn btn-primary btn-small grid-btn" ng-show="' + activeForRnr + '"/>';
  };

  var optionMessage = function (entity, defaultMessage) {
    return entity == null || entity.length == 0 ? messageService.get("label.none.assigned") : defaultMessage;
  };

  var resetValuesForFirstPeriod = function (periodGridData) {
    var firstPeriodWithRnrStatus = periodGridData[0];
    firstPeriodWithRnrStatus.activeForRnr = true;
    if (!firstPeriodWithRnrStatus.rnrId) {
      firstPeriodWithRnrStatus.rnrStatus = messageService.get("msg.rnr.not.started");
    }
  };

  var createPeriodWithRnrStatus = function (periods, rnr) {
    $scope.periodGridData = [];
    $scope.selectedPeriod = null;

    var periodWithRnrStatus;
    if (periods == null || periods.length == 0) {
      periodWithRnrStatus = {name: messageService.get("msg.no.period.available")};
      $scope.selectedPeriod = null;
      $scope.periodGridData.push(periodWithRnrStatus);
      return;
    }

    $scope.selectedPeriod = periods[0];
    periods.forEach(function (period) {
      periodWithRnrStatus = angular.copy(period);
      periodWithRnrStatus.rnrStatus = messageService.get("msg.rnr.previous.pending");
      if (rnr != null && periodWithRnrStatus.id == rnr.period.id) {
        periodWithRnrStatus.rnrId = rnr.id;
        periodWithRnrStatus.rnrStatus = rnr.status;
      }
      $scope.periodGridData.push(periodWithRnrStatus);
    });

    resetValuesForFirstPeriod($scope.periodGridData);
  };

  $scope.periodGridOptions = { data: 'periodGridData',
    canSelectRows: false,
    displayFooter: false,
    displaySelectionCheckbox: false,
    enableColumnResize: true,
    enableColumnReordering: true,
    enableSorting: false,
    showColumnMenu: false,
    showFilter: false,
    columnDefs: [
      {field: 'name', displayName: messageService.get("label.periods")},
      {field: 'startDate', displayName: messageService.get("period.header.startDate"), cellFilter: "date:'dd/MM/yyyy'" },
      {field: 'endDate', displayName: messageService.get("period.header.endDate"), cellFilter: "date:'dd/MM/yyyy'" },
      {field: 'rnrStatus', displayName: messageService.get("label.rnr.status") },
      {field: '', displayName: '', cellTemplate: getPeriodSpecificButton('row.entity.activeForRnr')}
    ]
  };


  $scope.$watch("error", function (errorMsg) {
    setTimeout(function () {
      if (errorMsg) {
        document.getElementById('saveSuccessMsgDiv').scrollIntoView();
      }
    });
  });

  $scope.facilityOptionMessage = function () {
    return optionMessage($scope.facilities, messageService.get("label.select.facility"));
  };

  $scope.programOptionMessage = function () {
    return optionMessage($scope.programs, messageService.get("label.select.program"));
  };

  $scope.loadPeriods = function () {
    $scope.selectedPeriod = null;
    $scope.periodGridData = [];
    if (!($scope.selectedProgram && $scope.selectedProgram.id && $scope.selectedFacilityId)) {
      $scope.error = "";
      return;
    }
    PeriodsForFacilityAndProgram.get({facilityId: $scope.selectedFacilityId, programId: $scope.selectedProgram.id, emergency: $scope.selectedRnrType.emergency},
      function (data) {
        $scope.error = "";
        createPeriodWithRnrStatus(data.periods, data.rnr);
      },
      function (data) {
        $scope.error = data.data.error;
      });
  };

  $scope.initRnr = function () {
    var data = {selectedType: $scope.selectedType, selectedProgram: $scope.selectedProgram, selectedFacilityId: $scope.selectedFacilityId, isNavigatedBack: true};
    navigateBackService.setData(data);

    $scope.error = "";
    $scope.sourceUrl = $location.$$url;
    var createRnrPath;

    FacilityProgramRights.get({facilityId: $scope.selectedFacilityId, programId: $scope.selectedProgram.id}, function (data) {

      var rights = data.rights;
      var hasPermission = function (permission) {
        return _.find(rights, function (right) {
          return right.right == permission
        });
      };

      Requisitions.get({facilityId: $scope.selectedFacilityId, programId: $scope.selectedProgram.id, periodId: $scope.selectedPeriod.id}, {},
        function (data) {
          if ((data.rnr == null || data.rnr == undefined) && !hasPermission('CREATE_REQUISITION')) {
            $scope.error = messageService.get("error.requisition.not.initiated");
            return;
          }
          if (data.rnr) {
            if (data.rnr.status != 'SUBMITTED' && !hasPermission('CREATE_REQUISITION')) {
              $scope.error = messageService.get("error.requisition.not.submitted");
              return;
            }
            $scope.$parent.rnr = data.rnr;
            createRnrPath = '/create-rnr/' + $scope.$parent.rnr.id + '/' + $scope.selectedFacilityId + '/' + $scope.selectedProgram.id + "?supplyType=full-supply&page=1";
            $location.url(createRnrPath);
          }
          else {
            Requisitions.save({facilityId: $scope.selectedFacilityId, programId: $scope.selectedProgram.id, periodId: $scope.selectedPeriod.id}, {}, function (data) {
              $scope.$parent.rnr = data.rnr;
              createRnrPath = '/create-rnr/' + $scope.$parent.rnr.id + '/' + $scope.selectedFacilityId + '/' + $scope.selectedProgram.id + "?supplyType=full-supply&page=1";
              $location.url(createRnrPath);
            }, function (data) {
              $scope.error = data.data.error ? data.data.error : messageService.get("error.requisition.not.exist");
            })
          }
        }, {});
    }, {});
  };
}

InitiateRnrController.resolve = {
  preAuthorize: function (AuthorizationService) {
    AuthorizationService.preAuthorize('CREATE_REQUISITION', 'AUTHORIZE_REQUISITION');
  }
};
