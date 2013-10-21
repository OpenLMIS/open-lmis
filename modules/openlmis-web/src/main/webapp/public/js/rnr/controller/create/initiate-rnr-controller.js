/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function InitiateRnrController($scope, $location, Requisitions,
                               PeriodsForFacilityAndProgram, UserFacilityList,
                               CreateRequisitionProgramList,
                               UserSupervisedFacilitiesForProgram,
                               FacilityProgramRights, navigateBackService,
                               messageService) {
  $scope.fullScreen = false;
  $scope.$watch('fullScreen', function () {
    angular.element(window).scrollTop(0);
    if (!$.browser.msie) {
      $scope.fullScreen ? angular.element('.toggleFullScreen').slideUp('slow', function () {
      }) : angular.element('.toggleFullScreen').slideDown('slow', function () {
      });
    }
    else {
      $scope.fullScreen ? angular.element('.toggleFullScreen').hide() : angular.element('.toggleFullScreen').show();
    }
    $scope.fullScreen ? angular.element('.print-button').css('opacity', '1.0') : angular.element('.print-button').css('opacity', '0');
  });

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
    $scope.myFacility = null;
    $scope.programs = null;
    $scope.facilities = null;
    $scope.error = null;
  };

  $scope.$on('$viewContentLoaded', function () {
    $scope.selectedType = navigateBackService.selectedType || "0";
    $scope.selectedProgram = navigateBackService.selectedProgram;
    $scope.selectedFacilityId = navigateBackService.selectedFacilityId;
    isNavigatedBack = navigateBackService.isNavigatedBack;
    $scope.$watch('programs', function () {
      isNavigatedBack = navigateBackService.isNavigatedBack;
      if (!isNavigatedBack) $scope.selectedProgram = undefined;
      if ($scope.programs && !isUndefined($scope.selectedProgram)) {
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
    isNavigatedBack = isNavigatedBack ? selectedType !== "0" : resetRnrData();

    if (selectedType === "0") { //My facility
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
    } else if (selectedType === "1") { // Supervised facility
      resetRnrData();
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
    return '<input type="button" ng-click="initRnr(row.entity)" openlmis-message="button.proceed" class="btn btn-primary btn-small grid-btn" ng-show="' + activeForRnr + '"/>';
  };

  var optionMessage = function (entity, defaultMessage) {
    return entity === null || entity.length === 0 ? messageService.get("label.none.assigned") : defaultMessage;
  };

  var resetValuesForFirstPeriod = function (periodGridData) {
    var firstPeriodWithRnrStatus = periodGridData[0];
    firstPeriodWithRnrStatus.activeForRnr = true;
    if (!firstPeriodWithRnrStatus.rnrId) {
      firstPeriodWithRnrStatus.rnrStatus = messageService.get("msg.rnr.not.started");
    }
  };

  var createPeriodWithRnrStatus = function (periods, rnrs) {

    if (periods === null || periods.length === 0) {
      $scope.error = messageService.get("msg.no.period.available");
      if ($scope.isEmergency) {
        addPreviousRequisitionToPeriodList(rnrs);
      }
      return;
    }

    $scope.periodGridData = [];

    var periodWithRnrStatus;

    periods.forEach(function (period) {
      periodWithRnrStatus = angular.copy(period);
      if ($scope.isEmergency) {
        periodWithRnrStatus.rnrStatus = messageService.get("msg.rnr.not.started");
      }
      else {
        periodWithRnrStatus.rnrStatus = messageService.get("msg.rnr.previous.pending");
        if (rnrs !== null && rnrs.length > 0 && periodWithRnrStatus.id === rnrs[0].period.id) {
          periodWithRnrStatus.rnrId = rnrs[0].id;
          periodWithRnrStatus.rnrStatus = rnrs[0].status;
        }
      }
      $scope.periodGridData.push(periodWithRnrStatus);
    });

    resetValuesForFirstPeriod($scope.periodGridData);

    if ($scope.isEmergency) {
      addPreviousRequisitionToPeriodList(rnrs);
    }

  };

  var addPreviousRequisitionToPeriodList = function (rnrs) {
    var periodWithRnrStatus;
    if (rnrs === null || rnrs.length === 0) return;
    rnrs.forEach(function (rnr) {
      if (rnr.status === 'INITIATED' || rnr.status === 'SUBMITTED') {
        periodWithRnrStatus = angular.copy(rnr.period);
        periodWithRnrStatus.rnrStatus = rnr.status;
        periodWithRnrStatus.rnrId = rnr.id;
        periodWithRnrStatus.activeForRnr = true;
        $scope.periodGridData.push(periodWithRnrStatus);
      }
    });
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
    $scope.periodGridData = [];
    if (!($scope.selectedProgram && $scope.selectedProgram.id && $scope.selectedFacilityId)) {
      $scope.error = "";
      return;
    }
    PeriodsForFacilityAndProgram.get({facilityId: $scope.selectedFacilityId, programId: $scope.selectedProgram.id, emergency: $scope.selectedRnrType.emergency},
      function (data) {
        $scope.error = "";
        $scope.isEmergency = data.is_emergency;
        createPeriodWithRnrStatus(data.periods, data.rnr_list);
      },
      function (data) {
        if (data.data.error === 'error.current.rnr.already.post.submit') {
          $scope.error = $scope.selectedType !== "0" ? messageService.get("msg.no.rnr.awaiting.authorization") :
            messageService.get("msg.rnr.current.period.already.submitted");
          return;
        }
        $scope.error = data.data.error;
      });
  };

  $scope.initRnr = function (selectedPeriod) {
    var data = {selectedType: $scope.selectedType, selectedProgram: $scope.selectedProgram, selectedFacilityId: $scope.selectedFacilityId, isNavigatedBack: true};
    navigateBackService.setData(data);

    $scope.error = "";
    $scope.sourceUrl = $location.$$url;
    var createRnrPath;

    FacilityProgramRights.get({facilityId: $scope.selectedFacilityId, programId: $scope.selectedProgram.id}, function (data) {

      var rights = data.rights;
      var hasPermission = function (permission) {
        return _.find(rights, function (right) {
          return right.right === permission;
        });
      };

      if (selectedPeriod.rnrId) {
        Requisitions.get({id: selectedPeriod.rnrId}, function (data) {
          if (data.rnr.status !== 'SUBMITTED' && !hasPermission('CREATE_REQUISITION')) {
            $scope.error = messageService.get("error.requisition.not.submitted");
            return;
          }
          $scope.$parent.rnr = data.rnr;
          createRnrPath = '/create-rnr/' + $scope.$parent.rnr.id + '/' + $scope.selectedFacilityId + '/' + $scope.selectedProgram.id + "?supplyType=full-supply&page=1";
          $location.url(createRnrPath);
        });
      } else if (hasPermission('CREATE_REQUISITION')) {

        Requisitions.save({facilityId: $scope.selectedFacilityId, programId: $scope.selectedProgram.id,
          periodId: selectedPeriod.id, emergency: $scope.selectedRnrType.emergency}, {}, function (data) {
          $scope.$parent.rnr = data.rnr;
          createRnrPath = '/create-rnr/' + $scope.$parent.rnr.id + '/' + $scope.selectedFacilityId + '/' + $scope.selectedProgram.id + "?supplyType=full-supply&page=1";
          $location.url(createRnrPath);
        }, function (data) {
          $scope.error = data.data.error ? data.data.error : messageService.get("error.requisition.not.exist");
        });
      } else {
        $scope.error = messageService.get("error.requisition.not.initiated");
      }
    }, {});
  };
}

InitiateRnrController.resolve = {
  preAuthorize: function (AuthorizationService) {
    AuthorizationService.preAuthorize('CREATE_REQUISITION', 'AUTHORIZE_REQUISITION');
  }
};
