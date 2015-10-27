function newVaccineOrderRequisitionController($scope,VaccineOrderRequisitionLastReport,VaccineOrderRequisitionReportInitiateEmergency, programs, facility, messageService, VaccineOrderRequisitionColumns, VaccineOrderRequisitionReportInitiate, $location, VaccineOrderRequisitionReportPeriods) {
    $scope.programs = programs;
    $scope.facility = facility;
    $scope.emergency = false;

    $scope.selectedType = 0;

    VaccineOrderRequisitionColumns.get({}, function (data) {
        $scope.columns = data.columns;
    });

    var id = parseInt($scope.programs[0].id,10);
    var facilityId = parseInt($scope.facility.id,10);

    $scope.requisitionTypes = [];
    $scope.requisitionTypes = [{id: '0', name: 'Unscheduled Reporting'}, {id: '1', name: 'Scheduled Reporting'}];


            VaccineOrderRequisitionReportPeriods.get({

                facilityId: parseInt(facilityId,10),
                programId: parseInt(id,10)
            }, function (data) {
                $scope.periodGridData = data.periods;
                if ($scope.periodGridData.length > 0) {
                    $scope.emergency = false;
                    $scope.periodGridData[0].showButton = true;
                }else{

                    VaccineOrderRequisitionLastReport.get({facilityId:parseInt(facilityId,10),programId:parseInt(id,10)}, function(data){

                        var lastReport = data.lastReport;

                            if(lastReport.status === 'ISSUED'){
                                $scope.enableIssueMenu = false;
                                VaccineOrderRequisitionReportInitiateEmergency.get({
                                periodId: lastReport.periodId,
                                programId: lastReport.programId,
                                facilityId: lastReport.facilityId
                            }, function (data) {
                                $location.path('/create/' + data.report.id);
                            });

                        }
                    });



                }


            });

    function getActionButton(showButton) {
        return '<input type="button" ng-click="initiate(row.entity)" openlmis-message="button.proceed" class="btn btn-primary btn-small grid-btn" ng-show="' + showButton + '"/>';
    }

    $scope.periodGridOptions = {
        data: 'periodGridData',
        canSelectRows: false,
        displayFooter: false,
        displaySelectionCheckbox: false,
        enableColumnResize: false,
        enableColumnReordering: true,
        enableSorting: false,
        showColumnMenu: false,
        showFilter: false,
        columnDefs: [
            {field: 'periodName', displayName: messageService.get("label.periods")},
            {field: 'status', displayName: messageService.get("label.status")},
            {field: '', displayName: '', cellTemplate: getActionButton('row.entity.showButton')}
        ]
    };


    $scope.initiate = function (period) {
        if (!angular.isUndefined(period.id) && (period.id !== null)) {
            $location.path('/create/' + period.id);

        } else {
            // initiate
            VaccineOrderRequisitionReportInitiate.get({
                periodId: period.periodId,
                programId: period.programId,
                facilityId: period.facilityId
            }, function (data) {
                $location.path('/create/' + data.report.id);
            });
        }
    };


}

newVaccineOrderRequisitionController.resolve = {

    programs: function ($q, $timeout, VaccineHomeFacilityPrograms) {
        var deferred = $q.defer();

        $timeout(function () {
            VaccineHomeFacilityPrograms.get({}, function (data) {
                deferred.resolve(data.programs);
            });
        }, 100);

        return deferred.promise;
    },
    facility: function ($q, $timeout, UserHomeFacility) {
        var deferred = $q.defer();

        $timeout(function () {
            UserHomeFacility.get({}, function (data) {
                deferred.resolve(data.homeFacility);
            });
        }, 100);

        return deferred.promise;
    }


};