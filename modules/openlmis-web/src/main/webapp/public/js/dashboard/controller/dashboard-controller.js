
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function AdminDashboardController($scope,$timeout,$filter,$location,dashboardMenuService,messageService,FlatGeographicZoneList,dashboardFiltersHistoryService,UserGeographicZoneTree,programsList,ReportingPerformance,GetPeriod, userPreferredFilterValues,formInputValue,ReportSchedules, ReportPeriods, ReportProductsByProgram, OperationYears, ReportPeriodsByScheduleAndYear, FacilitiesByGeographicZoneTree, OrderFillRate, ItemFillRate, StockEfficiency, SyncDashboard,AuthorizationService) {

    $scope.filterObject = {};

    $scope.formFilter = {
    };
    $scope.formPanel = {openPanel:true};

    $scope.alertsPanel = {openAlertPanel:true, openStockPanel:true};

    initialize();

    function initialize() {
        $scope.productSelectOption = {maximumSelectionSize : 4};
        $scope.$parent.currentTab = 'SUMMARY';
        $scope.showProductsFilter = true;
    }

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
      UserGeographicZoneTree.get({programId:$scope.formFilter.programId}, function(data){
         $scope.zones = data.zone;
      });
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
        $scope.formFilter.facilityName = getSelectedItemName($scope.formFilter.facilityId, $scope.allFacilities);
        $scope.loadFillRates();
    };

    $scope.filterProductsByProgram = function (){

        $scope.loadGeoZones();

        if(isUndefined($scope.formFilter.programId)){
            $scope.products = null;
            $scope.requisitionGroups  = null;
            $scope.resetFillRates();
            $scope.resetStockingChartData();
            $scope.resetReportingPerformanceData();
           return;
        }
        $scope.filterObject.programId = $scope.formFilter.programId;

        $scope.formFilter.programName = getSelectedItemName($scope.formFilter.programId, $scope.programs);

        ReportProductsByProgram.get({programId:  $scope.filterObject.programId}, function(data){
            $scope.products = data.productList;
        });
        $scope.loadStockingData();
        $scope.loadReportingPerformance();


    };

    $scope.processProductsFilter = function (){

        $scope.filterObject.productIdList = $scope.formFilter.productIdList;
        $scope.formFilter.productNamesList = getSelectedItemNames($scope.filterObject.productIdList, $scope.products);
        $scope.loadFillRates();
        $scope.loadStockingData();

    };

    $scope.fillRates = {openPanel:true};
    $scope.resetFillRates = function(){
        $scope.productItemFillRates = null;
        $scope.orderFillChart = null;

    };
    $scope.loadFillRates = function(){
        //For managing visibility of chart rendering container. All most all javascript chart rendering tools needs the container to be visible before the render process starts.

        getFilterValues();

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
                           $scope.productItemFillRates.push({'option': {animate:3000, barColor: barColor, scaleColor: $scaleColor, lineWidth: $lineWidth}, 'percent': $filter('number')( idx.fillRate, 0), 'name': idx.product});
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
        FacilitiesByGeographicZoneTree.get({zoneId: $scope.filterObject.zoneId}, function(data){
            $scope.allFacilities = data.facilities;
            if(!isUndefined($scope.allFacilities)){
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
        $scope.loadStockingData();
    };

    $scope.processZoneFilter = function(){
        $scope.filterObject.zoneId = $scope.formFilter.zoneId;
        $scope.formFilter.zoneName = getSelectedZoneName($scope.formFilter.zoneId, $scope.zones, $scope.geographicZones);

        $scope.loadFacilities();
        $scope.loadStockingData();
        $scope.loadReportingPerformance();
    };

    $scope.processPeriodFilter = function (){
        $scope.filterObject.periodId = $scope.formFilter.periodId;

        $scope.formFilter.periodName = getSelectedItemName($scope.formFilter.periodId, $scope.periods);

        $scope.loadFillRates();
        $scope.loadStockingData();
        $scope.loadReportingPerformance();
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

    /* Bar Chart */
    var barChartTicks = [[1, "Tab1"], [2, "Tab2"], [3, "Tab3"],[4, "Tab4"],[5, "Tab5"]];

    function GenerateSeries(added){
        var data = [];
        var start = 0 + added;
        var end = 100 + added;

        for(i=1;i<=5;i++){
            var d = Math.floor(Math.random() * (end - start + 1) + start);
            data.push([i, d]);
            start++;
            end++;
        }

        return data;
    }

    $scope.barChartData =  [{ label: "Random Tabs Data Size", data:  GenerateSeries(0), color: "#5482FF" }];
    $scope.barChartOption = {
        series: {
            bars: {show: true,
                align: "center",
                lineWidth:0,
                fill: 0.6,
                barWidth: 0.3
            }
        },
        xaxis: {
            axisLabel: "Sample tabs",
            axisLabelUseCanvas: false,
            ticks: barChartTicks
        },
        yaxis: {
            axisLabel: "Data Size",
            axisLabelUseCanvas: false,
            tickFormatter: function (v, axis) {
                return v + "kb";
            }
        },
        legend: {
            container:$("#barChartLegend"),
            noColumns: 0
        },
        grid:{
            clickable:true,
            hoverable: true,
            //autoHighlight: true,
            borderWidth: 1,
            borderColor: "#d6d6d6",
            minBorderMargin: 20,
            labelMargin: 10,
            backgroundColor: {
                colors: ["#FFF", "#CCC"]
            }
        },tooltip: true,
        tooltipOpts: {
            content: "%s of %x.0 is %y",
            shifts: {
                x: 20,
                y: 0
            },
            defaultTheme: false
        }
    };

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

                     var colors = {R:"#078B07",N:"#CC0505", L:"#FFFF05"};

                     for(var i=0; i < reporting.length; i++){
                         var labelKey = 'label.district.reporting.status.'+reporting[i].status;
                         var label = messageService.get(labelKey);
                         $scope.reportingChartData[i] = {label: label,
                             data: reporting[i].total,
                             color: colors[reporting[i].status]};
                     }
                     $scope.reportingRenderedData = {
                         status : _.pairs(_.object(_.range(reporting.length), _.pluck(reporting,'status')))

                     };

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

   /* Calendar  */

    var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();

    /* event source that contains custom events on the scope */
    $scope.events = [
        {title: 'All Day Event',start: new Date(y, m, 1)},
        {title: 'Long Event',start: new Date(y, m, d - 5),end: new Date(y, m, d - 2)},
        {id: 999,title: 'Repeating Event',start: new Date(y, m, d - 3, 16, 0),allDay: false},
        {id: 999,title: 'Repeating Event',start: new Date(y, m, d + 4, 16, 0),allDay: false},
        {title: 'Birthday Party',start: new Date(y, m, d + 1, 19, 0),end: new Date(y, m, d + 1, 22, 30),allDay: false},
        {title: 'Click for Google',start: new Date(y, m, 28),end: new Date(y, m, 29),url: 'http://google.com/'}
    ];

    /* alert on eventClick */
    $scope.alertOnEventClick = function( event, allDay, jsEvent, view ){
        alert(event.title + 'was clicked');

    };
    /* alert on Drop */
    $scope.alertOnDrop = function(event, dayDelta, minuteDelta, allDay, revertFunc, jsEvent, ui, view){
        alert('Event Droped to make dayDelta ' + dayDelta);

    };
    /* alert on Resize */
    $scope.alertOnResize = function(event, dayDelta, minuteDelta, revertFunc, jsEvent, ui, view ){
        alert('Event Resized to make dayDelta ' + minuteDelta);
    };

    /* add custom event*/
    $scope.addEvent = function() {
        $scope.events.push({
            title: 'eLMIS V2 kickoff meeting',
            start: new Date(y, m, 28),
            end: new Date(y, m, 29)
        });
    };
    /* remove event */
    $scope.remove = function(index) {
        $scope.events.splice(index,1);
    };
    /* Change View */
    $scope.changeView = function(view,calendar) {
        calendar.fullCalendar('changeView',view);
    };

    /* config object */
    $scope.uiConfig = {
        calendar:{
            height: 450,
            editable: true,
            header:{
                left: 'title',
                center: '',
                right: 'today prev,next'
            },
            eventClick: $scope.alertOnEventClick,
            eventDrop: $scope.alertOnDrop,
            eventResize: $scope.alertOnResize
        }
    };


    /* event sources array*/
    $scope.eventSources = [$scope.events];

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

        if(!isUndefined($scope.filterObject.productIdList)){
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
        var filterHistory = dashboardFiltersHistoryService.get($scope.$parent.currentTab);

        if(isUndefined(filterHistory)){
            if(!_.isEmpty(userPreferredFilterValues)){
                var date = new Date();

                $scope.filterObject.programId = isItemWithIdExists(userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_PROGRAM], $scope.programs) ?
                     userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_PROGRAM] : $scope.filterObject.programId;

                $scope.filterObject.periodId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_PERIOD];

                if(!isUndefined($scope.filterObject.periodId)){

                    GetPeriod.get({id:$scope.filterObject.periodId}, function(period){
                        if(!isUndefined(period.year)){
                            $scope.filterObject.year = period.year;
                        }else{
                            $scope.filterObject.year = date.getFullYear() - 1;
                        }
                    });
                }
                $scope.filterObject.scheduleId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_SCHEDULE];

                $scope.filterObject.zoneId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_GEOGRAPHIC_ZONE];

                $scope.filterObject.productIdList = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_PRODUCTS].split(',');
                $scope.loadFacilities();
                $scope.filterObject.facilityId = $scope.formFilter.facilityId = isItemWithIdExists(userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_FACILITY],$scope.allFacilities) ?
                    userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_FACILITY] : $scope.formFilter.facilityId;

                $scope.filterObject.facilityId = $scope.formFilter.facilityId  = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_FACILITY];
                $scope.registerWatches();

                $scope.formFilter = $scope.filterObject;

            }
        }else{
            $scope.registerWatches();
            $scope.formFilter = $scope.filterObject = filterHistory;

        }

     });
    $scope.registerWatches = function(){

        $scope.$watch('formFilter.programId',function(){
            $scope.filterProductsByProgram();
        });
        $scope.$watch('formFilter.scheduleId', function(){
            $scope.changeSchedule();
        });

    };

    var getFilterValues = function(){

        $scope.formFilter.periodName = getSelectedItemName($scope.formFilter.periodId,$scope.periods);
        $scope.formFilter.programName = getSelectedItemName($scope.formFilter.programId,$scope.programs);
        $scope.formFilter.facilityName = getSelectedItemName($scope.formFilter.facilityId,$scope.allFacilities);

        $scope.formFilter.zoneName = getSelectedZoneName($scope.formFilter.zoneId, $scope.zones, $scope.geographicZones);
        $scope.formFilter.productNamesList = getSelectedItemNames($scope.filterObject.productIdList, $scope.products);
        $scope.filterObject = $scope.formFilter;

    };


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

    /* End Custom Bar Chart */

    /* Bootstrap Dynamic Tab Utility  */
    function createTab(tabId){
        var tabNum = tabId.substr(tabId.length - 1);
        var contentId = tabId +'-'+ tabNum;

        if($('#'+tabId).length === 0){ //tab does not exist
            $('.nav-tabs').prepend('<li id="'+tabId+'"><a href="#' + contentId + '" data-toggle="tab"'+'><button class="close closeTab" type="button" >×</button>Tab '+tabNum +'</a></li>');
            showTab(tabId);

            registerCloseEvent();

        }else{
            showTab(tabId);
        }
    }

    function registerCloseEvent() {
        $('#dashboard-tabs').on('click', ' li a .close', function(e) {
            e.preventDefault();
            $(this).parents('li').remove('li');
            $('#dashboard-tabs a:first').tab('show');
        });
    }
    function showTab(tabId) {

        $('#dashboard-tabs #' + tabId + ' a').tab('show');
    }

    $scope.oneAtATime = true;

    $scope.stocking = {};
    $scope.stocking.openPanel = true;
    //$scope.$watch()
    $scope.groups = [
        {
            title: "Dynamic Group Header - 1",
            content: "Dynamic Group Body - 1"
        },
        {
            title: "Dynamic Group Header - 2",
            content: "Dynamic Group Body - 2"
        }
    ];

    $scope.items = ['Item 1', 'Item 2', 'Item 3'];

    $scope.addItem = function() {
        var newItemNo = $scope.items.length + 1;
        $scope.items.push('Item ' + newItemNo);
    };

}
