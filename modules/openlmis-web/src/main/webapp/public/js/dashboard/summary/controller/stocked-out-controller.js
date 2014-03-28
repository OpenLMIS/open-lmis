/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 3/16/14
 * Time: 9:08 PM
 * To change this template use File | Settings | File Templates.
 */

function StockedOutController($scope, $location, programsList,formInputValue,ReportProgramsBySupervisoryNode,navigateBackService, userDefaultSupervisoryNode,RequisitionGroupsBySupervisoryNodeProgramSchedule, UserSupervisoryNodes,ReportSchedules, ReportPeriods, RequisitionGroupsByProgram,RequisitionGroupsByProgramSchedule, ReportProductsByProgram, OperationYears, ReportPeriodsByScheduleAndYear,StockedOutFacilities, ngTableParams) {
    $scope.filterObject = {};

    $scope.formFilter = {};

    initialize();

    function initialize() {

        $scope.showProductsFilter = true;
        $scope.$parent.currentTab = 'STOCK-OUT';

        $scope.productSelectOption = {maximumSelectionSize : 1};
        $scope.defaultSupervisoryNodeId = $scope.filterObject.supervisoryNodeId = $scope.formFilter.supervisoryNodeId = !isUndefined(userDefaultSupervisoryNode) ? userDefaultSupervisoryNode.id : undefined ;

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

        $scope.filterObject.programId = $scope.formFilter.programId;
        if(!isUndefined($scope.formFilter.programId)){
            ReportProductsByProgram.get({programId:  $scope.filterObject.programId}, function(data){
                $scope.products = data.productList;
            });
        }else{
            $scope.products = undefined;
            $scope.formFilter.productIdList = undefined;
            $scope.processProductsFilter();
        }


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
    };

    $scope.processRequisitionFilter = function(){
        if($scope.formFilter.rgroupId && $scope.formFilter.rgroupId.length > 1) {
            $scope.formFilter.rgroupId = _.reject($scope.formFilter.rgroupId, function(rgroup){return rgroup === ""; });
        }
        $scope.filterObject.rgroupId = $scope.formFilter.rgroupId;

        $scope.loadStockedOutData();
    };

    $scope.processProductsFilter = function (){

        $scope.filterObject.productIdList = $scope.formFilter.productIdList;
        $scope.loadStockedOutData();

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

        $scope.loadStockedOutData();
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
        $scope.loadStockedOutData();
    };

    $scope.changeScheduleByYear = function (){

        if (!isUndefined($scope.formFilter.year)) {
            $scope.filterObject.year = $scope.formFilter.year;

        }
        $scope.changeSchedule();

    };

    $scope.processPeriodFilter = function (){
        if (!isUndefined($scope.formFilter.periodId)) {
            $scope.filterObject.periodId = $scope.formFilter.periodId;
        }

        $scope.loadStockedOutData();
    };

    $scope.stockedOutPieChartOption = {
        series: {
            pie: {
                show: true,
                radius: 1,
                label: {
                    show: true,
                    radius: 2 / 3,
                    formatter: function (label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:black;">' + Math.round(series.percent) + '%</div>';
                    },
                    threshold: 0.1
                }
            }
        },
        legend: {
            container:$("#stockedOutReportLegend"),
            noColumns: 0,
            labelBoxBorderColor: "none"
        },
        grid:{
            hoverable: true,
            clickable: true,
            borderWidth: 1,
            borderColor: "#d6d6d6",
            backgroundColor: {
                colors: ["#FFF", "#CCC"]
            }
        },
        tooltip: true,
        tooltipOpts: {
            content: "%p.0%, %s",
            shifts: {
                x: 20,
                y: 0
            },
            defaultTheme: false
        }
    };

    $scope.loadStockedOutData = function(){
        if(!isUndefined($scope.filterObject.programId) &&
            !isUndefined($scope.filterObject.periodId) &&
            !isUndefined($scope.filterObject.productIdList) && $scope.filterObject.productIdList.length > 0){
            StockedOutFacilities.get({
                periodId: $scope.filterObject.periodId,
                programId: $scope.filterObject.programId,
                productId: $scope.filterObject.productIdList[0],
                rgroupId: $scope.filterObject.rgroupId
            },function(stockData){
                $scope.totalStockOuts = 0;
                if(!isUndefined(stockData.stockOut)){
                    $scope.datarows = stockData.stockOut;

                    $scope.districts = _.pairs(_.object(_.range(stockData.stockOut.length), _.pluck(stockData.stockOut,'requisitionGroupId')));
                    $scope.stockedOutPieChartData = [];
                    for (var i = 0; i < stockData.stockOut.length; i++) {
                        $scope.totalStockOuts += stockData.stockOut[i].totalStockOut;
                        $scope.stockedOutPieChartData[i] = {
                            label: stockData.stockOut[i].location,
                            data: stockData.stockOut[i].totalStockOut
                        };
                    }

                    bindChartEvent("#stocked-out-reporting","plotclick",$scope.stockedOutChartClickHandler);
                    bindChartEvent("#stocked-out-reporting","plothover",flotChartHoverCursorHandler);

                }else{
                    $scope.resetStockedOutData();
                }

            });
        } else{
            $scope.resetStockedOutData();

        }
    };

    $scope.getFacilityStockOutPercent = function(value){
        return Math.round((value/$scope.totalStockOuts)*100) +'%';
    };

    $scope.resetStockedOutData = function(){
        $scope.stockedOutPieChartData = null;
        $scope.datarows = null;
    };

    $scope.stockedOutChartClickHandler = function (event, pos, item){
        if(item){
           var rgroupId = $scope.districts[item.seriesIndex][1];
           var districtStockOutPath = '/requisition-group-stock-out/'+$scope.filterObject.programId+'/'+$scope.filterObject.periodId+'/'+rgroupId+'/'+$scope.filterObject.productIdList[0];
            $location.path(districtStockOutPath);

            navigateBackService.setData($scope.filterObject);
            $scope.$apply();
        }

    };

    function flotChartHoverCursorHandler(event,pos,item){

        if (item && !isUndefined(item.dataIndex)) {
            $(event.target).css('cursor','pointer');
        } else {
            $(event.target).css('cursor','auto');
        }
    }

    function bindChartEvent(elementSelector, eventType, callback){
        $(elementSelector).bind(eventType, callback);
    }

    $scope.$on('$viewContentLoaded', function () {
        if(navigateBackService.isNavigatedBack){
            $scope.formFilter = navigateBackService;
        }

    });

    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        total: 0,           // length of data
        count: 25           // count per page
    });


}