/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 3/16/14
 * Time: 9:08 PM
 * To change this template use File | Settings | File Templates.
 */

function StockedOutController($scope, $location,$routeParams, userGeographicZoneList,ReportPrograms, ReportSchedules, ReportPeriods, RequisitionGroupsByProgram,RequisitionGroupsByProgramSchedule, ReportProductsByProgram, OperationYears, ReportPeriodsByScheduleAndYear, ngTableParams) {
    $scope.filterObject = {};

    $scope.formFilter = {};

    $scope.startYears = [];

    initialize();

    function initialize() {
        if(isUndefined($scope.filterObject.geographicZoneId)){
            $scope.filterObject.geographicZoneId = 0;
            if(!isUndefined(userGeographicZoneList) ){
                $scope.filterObject.geographicZoneId = userGeographicZoneList[0] !== null ? userGeographicZoneList[0].id : undefined;
            }
        }

    }
    $scope.productSelectOption = {maximumSelectionSize : 1};

    OperationYears.get(function (data) {
        $scope.startYears = data.years;
        $scope.startYears.unshift('-- All Years --');
    });

    ReportPrograms.get(function (data) {
        $scope.programs = data.programs;
        $scope.programs.unshift({'name': '-- Select Programs --'});
    });

    ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.unshift({'name':'-- Select a Schedule --', 'id':'0'}) ;

    });

    $scope.filterProductsByProgram = function (){
        if(isUndefined($scope.formFilter.programId)){
            return;
        }
        $scope.filterObject.programId = $scope.formFilter.programId;

        ReportProductsByProgram.get({programId:  $scope.filterObject.programId}, function(data){
            $scope.products = data.productList;
        });

        RequisitionGroupsByProgram.get({program: $scope.filterObject.programId }, function(data){
            $scope.requisitionGroups = data.requisitionGroupList;
            $scope.requisitionGroups.unshift({'name':'-- All Requisition Groups --'});
        });
    };

    $scope.processProductsFilter = function (){
        $scope.filterObject.productIdList = $scope.formFilter.productIdList;

        $scope.loadStockingData();

    };

    $scope.changeSchedule = function(){

        if ($scope.formFilter.scheduleId == "All") {
            $scope.filterObject.scheduleId = -1;
        } else if ($scope.formFilter.scheduleId !== undefined || $scope.formFilter.scheduleId === "") {
            $scope.filterObject.scheduleId = $scope.formFilter.scheduleId;

        } else {
            $scope.filterObject.scheduleId = 0;
        }

        if(!isUndefined($scope.filterObject.scheduleId)){
            ReportPeriods.get({ scheduleId : $scope.filterObject.scheduleId },function(data) {
                $scope.periods = data.periods;
                $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});

            });

            if(!isUndefined($scope.filterObject.programId)){
                RequisitionGroupsByProgramSchedule.get({program: $scope.filterObject.programId, schedule:$scope.filterObject.scheduleId}, function(data){
                    $scope.requisitionGroups = data.requisitionGroupList;
                    $scope.requisitionGroups.unshift({'name':'-- All Requisition Groups --','id':'0'});
                });
            }

            $scope.loadStockingData();
        }


    };

    $scope.processPeriodFilter = function (){
        if ( $scope.formFilter.periodId == "All") {
            $scope.filterObject.periodId = -1;
        } else if ($scope.formFilter.periodId !== undefined || $scope.formFilter.periodId === "") {
            $scope.filterObject.periodId = $scope.formFilter.periodId;

        } else {
            $scope.filterObject.periodId = 0;
        }

        $scope.loadStockingData();
    };

    $scope.changeScheduleByYear = function (){

        if ($scope.formFilter.year == "-- All Years --") {
            $scope.filterObject.year = -1;
        } else if ($scope.formFilter.year !== undefined || $scope.formFilter.year === "") {
            $scope.filterObject.year = $scope.formFilter.year;

        } else {
            $scope.filterObject.year = 0;
        }

        if($scope.filterObject.year === -1 || $scope.filterObject.year === 0){
            $scope.changeSchedule();

        }else{
            if(!isUndefined($scope.filterObject.scheduleId) && !isUndefined($scope.filterObject.year)){
                ReportPeriodsByScheduleAndYear.get({scheduleId: $scope.filterObject.scheduleId, year: $scope.filterObject.year}, function(data){
                    $scope.periods = data.periods;
                    $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});
                });
            }
            if(!isUndefined($scope.filterObject.scheduleId) && !isUndefined($scope.filterObject.programId)){
                RequisitionGroupsByProgramSchedule.get({program: $scope.filterObject.programId, schedule:$scope.filterObject.scheduleId}, function(data){
                    $scope.requisitionGroups = data.requisitionGroupList;
                    $scope.requisitionGroups.unshift({'name':'-- All Requisition Groups --','id':'0'});
                });
            }
            $scope.loadStockingData();
        }

    };

}