/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 2/28/14
 * Time: 4:40 PM
 * To change this template use File | Settings | File Templates.
 */

function ShipmentLeadTimeController($scope,$filter,programsList, formInputValue,RequisitionGroupsBySupervisoryNodeProgramSchedule,userDefaultSupervisoryNode,ReportProgramsBySupervisoryNode, UserSupervisoryNodes,ReportSchedules, ReportPeriods, RequisitionGroupsByProgram,RequisitionGroupsByProgramSchedule, ReportProductsByProgram, OperationYears, ReportPeriodsByScheduleAndYear,ShipmentLeadTime, ngTableParams) {

    $scope.filterObject = {};

    $scope.formFilter = {};

    $scope.startYears = [];

    initialize();

    function initialize() {
        $scope.defaultSupervisoryNodeId = $scope.filterObject.supervisoryNodeId = $scope.formFilter.supervisoryNodeId = !isUndefined(userDefaultSupervisoryNode) ? userDefaultSupervisoryNode.id : undefined ;
        $scope.showProductsFilter = false;
        $scope.$parent.currentTab = 'ORDER';
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

    $scope.filterProductsByProgram = function (){
        if(isUndefined($scope.formFilter.programId)){
            $scope.resetShipmentLeadTimeData();
            return;
        }
        $scope.filterObject.programId = $scope.formFilter.programId;

        ReportProductsByProgram.get({programId:  $scope.filterObject.programId}, function(data){
            $scope.products = data.productList;
        });

        if(!isUndefined($scope.formFilter.supervisoryNodeId)){
            RequisitionGroupsBySupervisoryNodeProgramSchedule.get(
                {programId : $scope.filterObject.programId,
                    scheduleId : isUndefined($scope.filterObject.scheduleId) ? 0 : $scope.filterObject.scheduleId ,
                    supervisoryNodeId : $scope.filterObject.supervisoryNodeId
                },function(data){
                    $scope.requisitionGroups = data.requisitionGroupList;
                    $scope.requisitionGroups.unshift({'name':formInputValue.requisitionOptionAll});
                })
        }else{
            RequisitionGroupsByProgram.get({program: $scope.filterObject.programId }, function(data){
                $scope.requisitionGroups = data.requisitionGroupList;
                $scope.requisitionGroups.unshift({'name':formInputValue.requisitionOptionAll});
            });
        }
        $scope.getShipmentLeadTimeData();

    };


    $scope.loadFacilitiesByRequisition = function(){
        if ($scope.formFilter.rgroupId == "All") {
            $scope.filterObject.rgroupId = -1;
        } else if ($scope.formFilter.rgroupId !== undefined || $scope.formFilter.rgroupId === "") {
            $scope.filterObject.rgroupId = $scope.formFilter.rgroupId;
            $.each($scope.requisitionGroups, function (item, idx) {
                if (idx.id == $scope.formFilter.rgroupId) {
                    $scope.filterObject.rgroup = idx.name;
                }
            });
        } else {
            $scope.filterObject.rgroupId = 0;
        }

    };
    $scope.processSupervisoryNodeChange = function(){

        $scope.filterObject.supervisoryNodeId = $scope.formFilter.supervisoryNodeId;

        if(isUndefined($scope.formFilter.supervisoryNodeId)){
            $scope.programs = _.filter(programsList, function(program){ return program.name !== formInputValue.programOptionSelect;});

            $scope.programs.unshift({'name': formInputValue.programOptionSelect});
        }else if(!isUndefined($scope.formFilter.supervisoryNodeId)){
            ReportProgramsBySupervisoryNode.get({supervisoryNodeId : $scope.filterObject.supervisoryNodeId}
                ,function(data){
                    $scope.programs = data.programs;
                    $scope.programs.unshift({'name': formInputValue.programOptionSelect});
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

        $scope.getShipmentLeadTimeData();
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
                            supervisoryNodeId: $scope.filterObject.supervisoryNodeId}
                        , function(data){
                            $scope.requisitionGroups = data.requisitionGroupList;
                            $scope.requisitionGroups.unshift({'name':formInputValue.requisitionOptionAll});

                        })
                }else{
                    RequisitionGroupsByProgramSchedule.get({program: $scope.filterObject.programId, schedule:$scope.filterObject.scheduleId}, function(data){
                        $scope.requisitionGroups = data.requisitionGroupList;
                        $scope.requisitionGroups.unshift({'name':formInputValue.requisitionOptionAll});
                    });
                }

            }
        }

        $scope.getShipmentLeadTimeData();
    };

    $scope.changeScheduleByYear = function (){

        if (!isUndefined($scope.formFilter.year) && $scope.formFilter.year !== formInputValue.yearOptionAll) {
            $scope.filterObject.year = $scope.formFilter.year;

        }
        $scope.changeSchedule();

    };

    // the grid options
    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        total: 0,           // length of data
        count: 25           // count per page
    });

    $scope.getShipmentLeadTimeData = function () {
        if(isUndefined($scope.filterObject.periodId) || isUndefined($scope.filterObject.programId)){
            return;
        }

        ShipmentLeadTime.get($scope.filterObject, function (data) {
            $scope.data = data.leadTime;
            $scope.paramsChanged($scope.tableParams);
        });

    };

    $scope.resetShipmentLeadTimeData = function(){
         $scope.data = undefined;
    };

    $scope.paramsChanged = function(params) {

        // slice array data on pages
        if($scope.data === undefined ){
            $scope.datarows = [];
            params.total = 0;
        }else{
            var data = $scope.data;
            var orderedData = params.filter ? $filter('filter')(data, params.filter) : data;
            orderedData = params.sorting ?  $filter('orderBy')(orderedData, params.orderBy()) : data;

            params.total = orderedData.length;
            $scope.datarows = orderedData.slice( (params.page - 1) * params.count,  params.page * params.count );
            var i = 0;
            var baseIndex = params.count * (params.page - 1) + 1;
            while(i < $scope.datarows.length){
                $scope.datarows[i].no = baseIndex + i;
                i++;
            }
        }
    };

    // watch for changes of parameters
    $scope.$watch('tableParams', $scope.paramsChanged , true);

}
