/**
 * Created by issa on 4/24/14.
 */
function SendNotificationController($scope, programsList,dashboardFiltersHistoryService, formInputValue,RequisitionGroupsBySupervisoryNodeProgramSchedule,userPreferredFilterValues,ReportProgramsBySupervisoryNode, UserSupervisoryNodes,ReportSchedules, ReportPeriods, RequisitionGroupsByProgram,RequisitionGroupsByProgramSchedule,FacilitiesByProgramAndRequisitionGroupParams, OperationYears, ReportPeriodsByScheduleAndYear, ngTableParams) {
    $scope.filterObject = {};

    $scope.formFilter = {};

    initialize();

    function initialize() {
        $scope.$parent.currentTab = 'NOTIFICATION';

        $scope.showProductsFilter = false;
        $scope.showStockStatusFilter = false;
        $scope.showFacilitiesFilter = true;
    }

    UserSupervisoryNodes.get(function (data){
        $scope.supervisoryNodes = data.supervisoryNodes;
        if(!isUndefined( $scope.supervisoryNodes)){
            $scope.supervisoryNodes.unshift({'name': formInputValue.supervisoryNodeOptionAll});
        }

    });

    OperationYears.get(function (data) {
        $scope.startYears = data.years;
        $scope.startYears.unshift(formInputValue.yearOptionAll);
    });

    ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.unshift({'name': formInputValue.scheduleOptionSelect}) ;

    });

    $scope.$watch('formFilter.facilityId', function (selection) {
        $scope.filterObject.facilityId = $scope.formFilter.facilityId;

    });

    $scope.processSupervisoryNodeChange = function(){

        $scope.filterObject.supervisoryNodeId = $scope.formFilter.supervisoryNodeId;

        if(isUndefined($scope.formFilter.supervisoryNodeId)){
            $scope.programs = _.filter(programsList, function(program){ return program.name !== formInputValue.programOptionSelect;});

            $scope.programs.unshift({'name': formInputValue.programOptionSelect});
        }else if(!isUndefined($scope.formFilter.supervisoryNodeId)){
            ReportProgramsBySupervisoryNode.get({supervisoryNodeId : $scope.filterObject.supervisoryNodeId},function(data){
                $scope.programs = data.programs;
                $scope.programs.unshift({'name': formInputValue.programOptionSelect});
            });
        }

        $scope.filterProductsByProgram();
    };

    $scope.filterProductsByProgram = function (){
        if(isUndefined($scope.formFilter.programId)){
            $scope.resetShipmentLeadTimeData();
            return;
        }
        $scope.filterObject.programId = $scope.formFilter.programId;

        if(!isUndefined($scope.formFilter.supervisoryNodeId)){
            RequisitionGroupsBySupervisoryNodeProgramSchedule.get(
                {programId : $scope.filterObject.programId,
                    scheduleId : isUndefined($scope.filterObject.scheduleId) ? 0 : $scope.filterObject.scheduleId ,
                    supervisoryNodeId : $scope.filterObject.supervisoryNodeId
                },function(data){
                    $scope.requisitionGroups = data.requisitionGroupList;
                    if(!isUndefined($scope.requisitionGroups)){
                        $scope.requisitionGroups.unshift({'name':formInputValue.requisitionOptionAll});
                    }
                });
        }else{
            RequisitionGroupsByProgram.get({program: $scope.filterObject.programId }, function(data){
                $scope.requisitionGroups = data.requisitionGroupList;
                if(!isUndefined($scope.requisitionGroups)){
                    $scope.requisitionGroups.unshift({'name':formInputValue.requisitionOptionAll});
                }
            });
        }
    };


    $scope.processPeriodFilter = function (){
        if ( $scope.formFilter.periodId == "All") {
            $scope.filterObject.periodId = -1;
        } else if ($scope.formFilter.periodId !== undefined || $scope.formFilter.periodId === "") {
            $scope.filterObject.periodId = $scope.formFilter.periodId;
            $.each($scope.periods, function (item, idx) {
                if (idx.id == $scope.formFilter.periodId) {
                    $scope.filterObject.period = idx.name;
                }
            });

        } else {
            $scope.filterObject.periodId = 0;
        }
        $scope.getShipmentLeadTimeData();
    };

    $scope.processRequisitionFilter = function(){

        if($scope.formFilter.rgroupId && $scope.formFilter.rgroupId.length > 1) {
            $scope.formFilter.rgroupId = _.reject($scope.formFilter.rgroupId, function(rgroup){return rgroup === ""; });
        }

        $scope.filterObject.rgroupId = $scope.formFilter.rgroupId;

        $scope.loadFacilities();
    };

    $scope.changeSchedule = function(){

        if (!isUndefined($scope.formFilter.scheduleId)) {
            $scope.filterObject.scheduleId = $scope.formFilter.scheduleId;
        }

        if(!isUndefined($scope.filterObject.scheduleId) ){
            if(!isUndefined($scope.filterObject.year) ){
                ReportPeriodsByScheduleAndYear.get({scheduleId: $scope.filterObject.scheduleId, year: $scope.filterObject.year}, function(data){
                    $scope.periods = data.periods;
                    $scope.periods.unshift({'name':formInputValue.periodOptionSelect});
                });
            }else{
                ReportPeriods.get({ scheduleId : $scope.filterObject.scheduleId },function(data) {
                    $scope.periods = data.periods;
                    $scope.periods.unshift({'name': formInputValue.periodOptionSelect});

                });
            }
            if(!isUndefined($scope.filterObject.programId)){
                if(!isUndefined($scope.filterObject.supervisoryNodeId)){
                    RequisitionGroupsBySupervisoryNodeProgramSchedule.get(
                        {programId: $scope.filterObject.programId,
                            scheduleId: $scope.filterObject.scheduleId,
                            supervisoryNodeId: $scope.filterObject.supervisoryNodeId}, function(data){
                            $scope.requisitionGroups = data.requisitionGroupList;
                            if(!isUndefined($scope.requisitionGroups)){
                                $scope.requisitionGroups.unshift({'name':formInputValue.requisitionOptionAll});
                            }

                        });
                }else{
                    RequisitionGroupsByProgramSchedule.get({program: $scope.filterObject.programId, schedule:$scope.filterObject.scheduleId}, function(data){
                        $scope.requisitionGroups = data.requisitionGroupList;
                        if(!isUndefined($scope.requisitionGroups)){
                            $scope.requisitionGroups.unshift({'name':formInputValue.requisitionOptionAll});
                        }
                    });
                }

            }
        }

        $scope.loadFacilities();

    };

    $scope.changeScheduleByYear = function (){

        if (!isUndefined($scope.formFilter.year)) {
            $scope.filterObject.year = $scope.formFilter.year;

        }
        $scope.changeSchedule();

    };
    $scope.loadFacilities = function(){
        FacilitiesByProgramAndRequisitionGroupParams.get({
            supervisoryNodeId: isUndefined($scope.filterObject.supervisoryNodeId) ? 0 : $scope.filterObject.supervisoryNodeId,
            programId: isUndefined($scope.filterObject.programId)? 0 : $scope.filterObject.programId ,
            scheduleId: isUndefined($scope.filterObject.scheduleId) ? 0 : $scope.filterObject.scheduleId,
            rgroupId: $scope.filterObject.rgroupId
        }, function(data){
            $scope.allFacilities = data.facilities;
            if(!isUndefined($scope.allFacilities)){
                $scope.allFacilities.unshift({code:formInputValue.facilityOptionSelect});
            }

        });

    };


    $scope.$on('$viewContentLoaded', function () {
        var filterHistory = dashboardFiltersHistoryService.get($scope.$parent.currentTab);

        if(isUndefined(filterHistory)){
            if(!_.isEmpty(userPreferredFilterValues)){
                var date = new Date();
                $scope.filterObject.supervisoryNodeId = $scope.formFilter.supervisoryNodeId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_SUPERVISORY_NODE];
                $scope.processSupervisoryNodeChange();

                $scope.filterObject.programId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_PROGRAM];
                $scope.filterObject.periodId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_PERIOD];
                $scope.filterObject.scheduleId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_SCHEDULE];
                $scope.filterObject.year = date.getFullYear() - 1;
                $scope.filterObject.rgroupId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_REQUISITION_GROUP];

                $scope.registerWatches();

                $scope.formFilter = $scope.filterObject;

            }
        }else{

            $scope.formFilter.supervisoryNodeId = filterHistory.supervisoryNodeId;
            $scope.processSupervisoryNodeChange();
            $scope.registerWatches();
            $scope.formFilter = $scope.filterObject = filterHistory;

        }

    });
    $scope.registerWatches = function(){

        $scope.$watch('formFilter.scheduleId', function(){
            $scope.changeSchedule();

        });

    };


}
