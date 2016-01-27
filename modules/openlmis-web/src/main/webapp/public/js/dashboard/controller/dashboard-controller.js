/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

function AdminDashboardController($scope,$timeout,$filter,$location,userPreferredFilters,messageService,dashboardMenuService,FlatGeographicZoneList,dashboardFiltersHistoryService,UserGeographicZoneTree,programsList,ReportingPerformance,formInputValue,ReportSchedules, ReportPeriods, ReportProductsByProgram, OperationYears, ReportPeriodsByScheduleAndYear, FacilitiesByGeographicZoneTree, OrderFillRate, ItemFillRate, StockEfficiency, SyncDashboard,AuthorizationService) {

    initialize();

    function initialize() {
        $scope.productSelectOption = {maximumSelectionSize : 4};
        $scope.$parent.currentTab = 'SUMMARY';
        $scope.showProductsFilter = true;
    }

    var filterHistory = dashboardFiltersHistoryService.get($scope.$parent.currentTab);

    if(isUndefined(filterHistory)){
        $scope.formFilter = $scope.filterObject  = userPreferredFilters || {};
    }else{
        $scope.formFilter = $scope.filterObject  = filterHistory || {};
    }

    $scope.formPanel = {openPanel:true};

    $scope.alertsPanel = {openAlertPanel:true, openStockPanel:true};

    FlatGeographicZoneList.get(function (data) {
        $scope.geographicZones = data.zones;
    });

    var itemFillRateColors = [{'minRange': -100, 'maxRange': 0, 'color' : '#E23E3E', 'description' : 'Red color for product with a fill rate <= 0 '},
        {'minRange': 1, 'maxRange': 50, 'color' : '#FEBA50', 'description' : 'Yellow color for product with a fill rate > 0 and <= 50 '},
        {'minRange': 51, 'maxRange': 100, 'color' : '#38AB49', 'description' : 'Green color for product with a fill rate > 50 '}];
    var $scaleColor = '#D7D5D5';
    var defaultBarColor = '#FEBA50';
    var $lineWidth = 5;
    var barColor = defaultBarColor;

   $scope.programs = programsList;
   $scope.programs.unshift({'name': formInputValue.programOptionSelect});


   $scope.loadGeoZones = function(){
       if(!isUndefined($scope.formFilter.programId)){
           UserGeographicZoneTree.get({programId:$scope.formFilter.programId}, function(data){
               $scope.zones = data.zone;
               if(!isUndefined($scope.zones)){
                   $scope.rootZone = $scope.zones.id;
               }

           });
       }
   };


   OperationYears.get(function (data) {
        $scope.startYears = data.years;
        $scope.startYears.unshift(formInputValue.yearOptionAll);
    });

     ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.unshift({'name': formInputValue.scheduleOptionSelect}) ;

    });

    $scope.processFacilityFilter = function(){
        $scope.filterObject.facilityId = $scope.formFilter.facilityId;

    };

    $scope.filterProductsByProgram = function (){
        $scope.loadGeoZones();
        loadProducts();
    };

    var loadProducts = function(){
        $scope.filterObject.programId = $scope.formFilter.programId;
        ReportProductsByProgram.get({programId:  $scope.filterObject.programId}, function(data){
            $scope.products = data.productList;
        });
    };

    $scope.processProductsFilter = function (){
        $scope.filterObject.productIdList = $scope.formFilter.productIdList;
        $scope.formFilter.productNamesList = getSelectedItemNames($scope.filterObject.productIdList, $scope.products);
    };

    $scope.fillRates = {openPanel:true};
    $scope.resetFillRates = function(){
        $scope.productItemFillRates = null;
        $scope.orderFillChart = null;

    };
    $scope.loadFillRates = function(){
        //For managing visibility of chart rendering container. All most all javascript chart rendering tools needs the container to be visible before the render process starts.
        $scope.showItemFill = false;
        $scope.showOrderFill = false;

       if(!isUndefined($scope.filterObject.facilityId)){
            if(!isUndefined($scope.filterObject.productIdList)){
               //Item Fill Rate
               ItemFillRate.get({
                   periodId: $scope.filterObject.periodId,
                   facilityId: $scope.filterObject.facilityId,
                   programId: $scope.filterObject.programId,
                   productListId: $scope.filterObject.productIdList
               },function (data){
                   if(!isUndefined(data)){//set visibility of item fill chart container to true if there is data to be rendered

                       $scope.showItemFill = true;
                       $scope.fillRates.openPanel = true;
                   }
                   $scope.itemFills = data.itemFillRate;
                   $scope.productItemFillRates = null;
                   if(!isUndefined($scope.itemFills)){
                       $scope.productItemFillRates = [];
                       $.each($scope.itemFills, function (item, idx) {
                           $.each(itemFillRateColors, function(index, item){
                               if(idx.fillRate <= item.maxRange && idx.fillRate >= item.minRange){
                                   barColor = item.color;
                               }
                           });
                           $scope.productItemFillRates.push({'option': {animate:3000, barColor: barColor, scaleColor: $scaleColor, lineWidth: $lineWidth}, 'percent':  idx.fillRate, 'name': idx.product});
                       });
                   }
               });

           }
           //Order Fill Rate
           OrderFillRate.get({
               periodId: $scope.filterObject.periodId,
               facilityId:$scope.filterObject.facilityId,
               programId: $scope.filterObject.programId},function(data){
               $scope.orderFillChart = null; //reset
               $scope.orderFill = data.orderFillRate || undefined;
               var fillRate = [];
               if($scope.orderFill !== undefined ){

                   if(!isUndefined($scope.orderFill.fillRate)){ //set visibility of order fill rate chart container to true
                       $scope.showOrderFill = true;
                       $scope.fillRates.openPanel = true;

                   }
                   fillRate.push([$filter('number')($scope.orderFill.fillRate, 0)]);

                   $timeout(function (){ //wait until the container fully visible

                       $scope.orderFillChart = {
                           'option':{
                               seriesDefaults:{
                                   renderer:$.jqplot.MeterGaugeRenderer,
                                   rendererOptions: {
                                       label: 'Order Fill Rate '+ $filter('number')($scope.orderFill.fillRate, 0)+'%',
                                      // labelPosition: 'bottom',
                                        labelHeightAdjust: -32,
                                       min: 0,
                                       max: 100,
                                       intervals:[0,50, 80, 100],
                                       intervalColors:[ '#00FF00','#FF0000', '#F6F404']
                                   }
                               }
                           },'data': fillRate
                       };

                   },100);

               }
           });
       }else{
           $scope.resetFillRates();
       }

    };

    $scope.setFilterData = function(){
        var data = {};
        $scope.filterObject = $scope.formFilter;
        angular.extend(data,$scope.filterObject);

        dashboardFiltersHistoryService.add($scope.$parent.currentTab,data);
    };

    $scope.loadFacilities = function(){
        if(isUndefined($scope.formFilter.zoneId) || isUndefined($scope.formFilter.programId)){
            $scope.allFacilities = null;
            return;
        }

        FacilitiesByGeographicZoneTree.get({zoneId: $scope.formFilter.zoneId, programId: $scope.formFilter.programId}, function(data){
            $scope.allFacilities = data.facilities;
            if(!isUndefined(data.facilities)){
                $scope.allFacilities.unshift({code:formInputValue.facilityOptionSelect});
            }

        });

    };

    $scope.changeSchedule = function(){
    if ($scope.formFilter.scheduleId !== undefined || $scope.formFilter.scheduleId === "") {
            $scope.filterObject.scheduleId = $scope.formFilter.scheduleId;
        }

        if(!isUndefined($scope.filterObject.scheduleId)){
            if(!isUndefined($scope.filterObject.year)){
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

        }
    };

    $scope.processZoneFilter = function(){
        $scope.filterObject.zoneId = $scope.formFilter.zoneId;
        $scope.loadFacilities();
    };

    $scope.processPeriodFilter = function (){
        $scope.filterObject.periodId = $scope.formFilter.periodId;
    };

    $scope.changeScheduleByYear = function (){

        if (!isUndefined($scope.formFilter.year)) {
            $scope.filterObject.year = $scope.formFilter.year;

        }
        $scope.changeSchedule();

    };

    $scope.$on('$routeChangeStart', function(){
        $scope.setFilterData();
    });


    $("#afloat1").bind("plotclick", function (event, pos, item) {

        if(item) {
            var barIndex =  item.datapoint[0];
            if (item.datapoint[0]) {
                $("#afloat1").css('cursor','crosshair');
            } else {
                $("#afloat1").css('cursor','auto');
            }
            if(++barIndex <=4){
                var tabId ='dashboard-tab-'+ barIndex;

                createTab(tabId);
            }
        }
    });

    function flotChartHoverCursorHandler(event,pos,item){

            if (item && !isUndefined(item.dataIndex)) {
                $(event.target).css('cursor','pointer');
            } else {
                $(event.target).css('cursor','auto');
            }
   }

    $("#afloat1").bind("plothover", function (event, pos, item) {
        flotChartHoverCursorHandler(event, pos, item);
    });
    /* End Bar Chart */

    /* Pie Chart */
    function setupReportingChartOption(){
        $scope.reportingPieChartOption = {
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
                container:$("#districtReportLegend"),
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

    }

    $scope.loadReportingPerformance = function(){
        if(isUndefined($scope.filterObject.programId) || isUndefined($scope.filterObject.periodId)){
            $scope.resetReportingPerformanceData();
            return;
        }

         ReportingPerformance.get({periodId :$scope.filterObject.periodId, zoneId: $scope.filterObject.zoneId,programId: $scope.filterObject.programId}, function(data){
                 $scope.reportingChartData = [];
                 if(!isUndefined(data.reportingPerformance)){
                     var reporting = data.reportingPerformance;

                     if(reporting.total > 0){
                         //var colors = {R:"#078B07",N:"#CC0505", L:"#FFFF05"};
                         $scope.reportingChartData[0] = {label: messageService.get('label.district.reporting.status.reporting'),
                             data: reporting.reporting,
                             color: "#078B07"};
                         $scope.reportingChartData[1] = {label: messageService.get('label.district.reporting.status.nonReporting'),
                             data: reporting.nonReporting,
                             color: "#CC0505"};
                         $scope.reportingRenderedData = {
                             status : [[0,"reporting"],[1,"nonReporting"]]

                         };

                     }

                     bindChartEvent("#district-reporting","plothover",flotChartHoverCursorHandler);
                     bindChartEvent("#district-reporting","plotclick",$scope.reportingPerformanceClickHandler);

                     setupReportingChartOption();

                 }
        });
    };

    $scope.reportingPerformanceClickHandler = function (event, pos, item){
        if(item){
            var status;
            if(!isUndefined($scope.reportingRenderedData.status)){
                 status = $scope.reportingRenderedData.status[item.seriesIndex][1];
            }
            var reportingPerformanceDetailPath = '/reporting-performance/program/'+$scope.filterObject.programId+'/period/'+$scope.filterObject.periodId;
            dashboardMenuService.addTab('menu.header.dashboard.reporting.performance.detail','/public/pages/dashboard/index.html#'+reportingPerformanceDetailPath,'REPORTING-PERFORMANCE-DETAIL',true, 7);
            $location.path(reportingPerformanceDetailPath).search("status="+status+"&zoneId="+$scope.filterObject.zoneId);
            $scope.setFilterData();
            $scope.$apply();
        }

    };


    $scope.resetReportingPerformanceData = function(){
        $scope.reportingChartData = null;
        $scope.reportingChartOption = null;
    };

    $scope.districtReporting = {};
    $scope.districtReporting.openPanel = true;

    /* End Pie Chart */




    /* event sources array*/

    /* End Calendar  */
   // $scope.itemFillRates = [55,45,-60];
    $scope.itemFillRates = [{option:{animate:3000, barColor:'#FEBA50', scaleColor:'#D7D5D5', lineWidth: 5},percent:55},{option: {animate:2500, barColor:'#E23E3E', scaleColor:'#D7D5D5', lineWidth: 5 }, percent:45}];

    /* Easy pie chart */
    $scaleColor = '#D7D5D5';
    $lineWidth = 5;
    $scope.option1  = {animate:3000, barColor:'#FEBA50', scaleColor:$scaleColor, lineWidth: $lineWidth};
    $scope.percent1 = 55;
    $scope.option2  = {animate:2500, barColor:'#E23E3E', scaleColor:$scaleColor, lineWidth: $lineWidth };
    $scope.percent2 = -46;
    $scope.option3  = {animate:2000, barColor:'#38AB49', scaleColor:$scaleColor, lineWidth: $lineWidth};
    $scope.percent3 = 85;
    $scope.option4  = { animate:1500, barColor:'#FFB848', scaleColor:$scaleColor, lineWidth: $lineWidth};
    $scope.percent4 = 55;

    /* End of Easy pie chart */


    $scope.loadStockingData = function(){
        if(!isUndefined($scope.filterObject.productIdList) &&
        !isUndefined($scope.filterObject.periodId) &&
        !isUndefined($scope.filterObject.zoneId)){
            StockEfficiency.get({
                periodId: $scope.filterObject.periodId,
                programId: $scope.filterObject.programId,
                zoneId: $scope.filterObject.zoneId,
                productListId: $scope.filterObject.productIdList
            },function (data){
                var stockingData =  data.stocking;
                adjustStockingEfficiencyDataForChart(stockingData);

              });
        }else{
            $scope.resetStockingChartData();
        }
       // $scope.setFilterData();
    };

    $scope.resetStockingChartData = function(){
        $scope.multiBarsRenderedData = undefined;
        $scope.multiBarsData = undefined;
        $scope.multipleBarsOption = undefined;

        //$scope.stocking.openPanel =  !$scope.stocking.openPanel;

    };

    var adjustStockingEfficiencyDataForChart = function(stockingData){
        if(isUndefined(stockingData) || stockingData.length === 0){
            $scope.resetStockingChartData();
            return;
        }
        $scope.multiBarsRenderedData = {
            products : _.pairs(_.object(_.range(stockingData.length), _.pluck(stockingData,'productId'))),
            filterParams : _.pick(stockingData[0],'rgroupId','programId', 'periodId')
        };
        var multiBarsColors = ["#F83103","#FAA702","#02A8FA","#37AC02"];
        var adequatelyStockedSeries =  _.pairs(_.object(_.range(stockingData.length), _.map(_.pluck(stockingData,'adequatelyStocked'),function(stat){ return stat;})));
        var stockedOutSeries =  _.pairs(_.object(_.range(stockingData.length), _.map(_.pluck(stockingData,'stockedOut'),function(stat){ return  stat;})));
        var overstockedSeries =  _.pairs(_.object(_.range(stockingData.length), _.map(_.pluck(stockingData,'overStocked'),function(stat){ return  stat;})));
        var understockedSeries =  _.pairs(_.object(_.range(stockingData.length), _.map(_.pluck(stockingData,'understocked'),function(stat){ return stat;})));
        var seriesLabel = ["Stocked out","Understocked","Overstocked","Adequately Stocked"];
        var dataSeries = [stockedOutSeries,understockedSeries,overstockedSeries,adequatelyStockedSeries];

        $scope.stocking.openPanel = true;

        $scope.multiBarsData = _.map(dataSeries, function(series, key){
            return {
            label: seriesLabel[key],
            data: series,
            bars: {
                order: key + 1
            },
            color: multiBarsColors[key]
        }; });

        var multiBarsTicks = _.pairs(_.object(_.range(stockingData.length), _.pluck(stockingData,'product')));
        $scope.multipleBarsOption = generateMultipleBarsOption(multiBarsTicks);

        //bind event
        bindChartEvent("#stocking-efficiency","plotclick",$scope.stockBarClickHandler);
        bindChartEvent("#stocking-efficiency","plothover", flotChartHoverCursorHandler);
    };

    function generateMultipleBarsOption(ticksLabel){
        return {
            series: {
                bars: {
                    show: true,
                    showNumbers: true,
                    numbers : {
                        yAlign: function(y) { if(y!==0){ return y ; }else{return null;}}
                        //show: true
                    },
                    fill: 0.8,
                    lineWidth: 0,
                    barWidth: 0.2
                }
            },
            xaxis: {
                tickLength: 0, // hide gridlines
                axisLabel: 'Product',
                axisLabelUseCanvas: false,
                ticks: ticksLabel,
                labelWidth: 20,
                reserveSpace: true

            } ,
            yaxis: {
                axisLabel: '# of Facilities',
                axisLabelUseCanvas: false
            },
            grid: {
                hoverable: true,
                clickable: true,
                borderWidth: 1,
                borderColor: "#d6d6d6"//,
               /* backgroundColor: {
                    colors: ["#FFF", "#CCC"]
                }*/
            },
            legend: {
                container:$("#multiBarsLegend"),
                noColumns: 0,
                labelBoxBorderColor: "none"
            },
            tooltip: true,
            tooltipOpts: {
                content: getTooltip,
                shifts: {
                    x: 10,
                    y: 20
                },
                defaultTheme: true
            }
        };

    }
    $scope.multipleBarsOption = {};
    $scope.multiBarsData =[];

     function getTooltip(label, xval, yval, flotItem){
         //alert('tooltip called '+label+', '+ xval+','+ yval+','+ JSON.stringify(flotItem))

         return flotItem.series.xaxis.ticks[xval].label+' '+yval+' '+'facilities'+' ' +label;
     }
    $scope.$on('$viewContentLoaded', function () {
        $scope.loadFacilities();

        $timeout(function(){
            $scope.search();
        },1000);

    });

    var getFilterValues = function () {
        $scope.formFilter.programName = getSelectedItemName($scope.formFilter.programId, $scope.programs);
        $scope.formFilter.periodName = getSelectedItemName($scope.formFilter.periodId,$scope.periods);
        $scope.formFilter.zoneName = getSelectedZoneName($scope.formFilter.zoneId, $scope.zones, $scope.geographicZones);
        $scope.formFilter.facilityName = getSelectedItemName($scope.formFilter.facilityId,$scope.allFacilities);
    };

    $scope.search = function(){
        getFilterValues();
        if($scope.rootZone == $scope.formFilter.zoneId){
            return;
        }
        $scope.loadStockingData();
        $scope.loadReportingPerformance();
        $scope.loadFillRates();

        //Alert Controller listens this event to update its own data
        $scope.$broadcast('dashboardFiltering', null);

    };

    $scope.$watch('formFilter.programId',function(){
        $scope.filterProductsByProgram();
    });
    $scope.$watch('formFilter.scheduleId', function(){
        $scope.changeSchedule();
    });

    $scope.stockBarClickHandler = function (event, pos, item){

        var stockData = {};
        if(item && !isUndefined(item.dataIndex)){
            var productIndex = item.dataIndex;
            if($scope.multiBarsRenderedData){
                $scope.multiBarsRenderedData.filterParams.productId = $scope.multiBarsRenderedData.products[productIndex][1];
                stockData = $scope.multiBarsRenderedData.filterParams;
                stockData.productIdList = $scope.filterObject.productIdList;
                stockData.year = $scope.filterObject.year;
                stockData.scheduleId = $scope.filterObject.scheduleId;
                stockData.status = item.seriesIndex;
                stockData.facilityId = $scope.filterObject.facilityId;

                stockData.zoneId = $scope.filterObject.zoneId;
                 var viewStockPath = '/view-stock-detail/'+stockData.programId+'/'+stockData.periodId+'/'+ stockData.productId;

                var params = {
                    "year": stockData.year,
                    "scheduleId": stockData.scheduleId,
                    "zoneId": stockData.zoneId,
                    "status":stockData.status
                };
                stockData.isNavigatedBack = true;
                $scope.setFilterData();
                $location.path(viewStockPath).search(params);
                $scope.$apply();

            }
        }
    };

    function bindChartEvent(elementSelector, eventType, callback){
        $(elementSelector).bind(eventType, callback);
    }

    $scope.showSyncDashboard = AuthorizationService.hasPermission('MANAGE_USER');

    $scope.syncDashboard = function(){
        $scope.inProgress = true;
        SyncDashboard.update({},function(data){

            $scope.inProgress = false;

        },function(errorMessage){

        });
        $location.path("/");
    };

    $scope.stocking = {};
    $scope.stocking.openPanel = true;
    /* End Custom Bar Chart */

}
