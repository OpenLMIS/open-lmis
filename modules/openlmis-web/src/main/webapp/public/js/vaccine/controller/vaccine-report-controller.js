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
function VaccineReportPOCReportController($scope, VaccineMonthlyReport, VaccineUsageTrend, Period, messageService, VaccineReportLegendContent) {


    VaccineReportLegendContent.get({}, function(data){
        $scope.definitions = data.vaccineLegend;
        if(!isUndefined($scope.definitions)){
           $scope.consumptionLegend = _.findWhere($scope.definitions, {"name":"consumption.graph.legend"});
            $scope.sohLegend = _.findWhere($scope.definitions,{name:'soh.graph.legend'});
        }
    });


    $scope.OnFilterChanged = function() {

      /*  if(isUndefined($scope.filter.period) || $scope.filter.period == 0){
            $scope.filter.period = $scope.filter.defaultPeriodId;

        }*/
        // clear old data if there was any

        $scope.data = $scope.datarows = [];
        $scope.filter.max = 10000;

        Period.get({id: $scope.filter.period}, function(data){
            $scope.period = data.period;
        });

        if(isUndefined($scope.filter.zone) || messageService.get('report.filter.all.geographic.zones') == $scope.filter.zone){
            $scope.filter.zone = -1;
        }
        if(isUndefined($scope.filter.facility) || $scope.filter.facility === ""){
            $scope.filter.facility = 0;
            $scope.showOtherActivities = false;
        }else if($scope.filter.facility !== 0 ){
            $scope.showOtherActivities = true;
        }

        if($scope.filter.period !== null && $scope.filter.period !== 0
        ){
            VaccineMonthlyReport.get($scope.filter, function(data){
                $scope.data = data.vaccineData;

                if($scope.data !== null){
                    $scope.diseaseSurveillance = $scope.data.diseaseSurveillance;
                    $scope.coldChain = $scope.data.coldChain;
                    $scope.adverseEffect = $scope.data.adverseEffect;
                    $scope.vaccineCoverage = $scope.data.vaccineCoverage;
                    $scope.immunizationSession = $scope.data.immunizationSession;
                    $scope.vaccination = $scope.data.vaccination;
                    $scope.syringes = $scope.data.syringes;
                    $scope.vitamins = $scope.data.vitamins;
                    $scope.targetPopulation = $scope.data.targetPopulation;
                    $scope.vitaminSupplementation = $scope.data.vitaminSupplementation;
                    $scope.dropOuts = $scope.data.dropOuts;
                }
            });

        }

    };

    $scope.renderGraph = function(facilityCode, productCode){
        $scope.filter.facilityCode = isUndefined(facilityCode)? '' : facilityCode;
        $scope.filter.productCode =  isUndefined(productCode)? '': productCode;

        VaccineUsageTrend.get($scope.filter, function(data){
            $scope.trendingData = data.vaccineUsageTrend;

            $scope.periodTicks = _.pairs(_.object(_.range($scope.trendingData.length), _.pluck($scope.trendingData,'period_name')));
            $scope.amcChartData = _.pairs(_.object(_.range($scope.trendingData.length), _.pluck($scope.trendingData,'quantity_issued')));
            $scope.sohChartData = _.pairs(_.object(_.range($scope.trendingData.length), _.pluck($scope.trendingData,'closing_balance')));

            $scope.amcChartOption = generateBarsOption($scope.periodTicks, messageService.get('label.consumption'));
            $scope.sohChartOption = generateBarsOption($scope.periodTicks, messageService.get('label.stock.on.hand'));
            $scope.amcChart = [{
                label: messageService.get('label.consumption'),
                data:  $scope.amcChartData,
                color: "#faa732",
                bars: {
                    show: true,
                    showNumbers: true,
                    numbers : {
                        yAlign: function(y) { if(y!==0){ return y ; }else{return null;}}
                        //show: true
                    },

                    align: "center",
                    barWidth: 0.5,
                    fill: 0.9,
                    lineWidth:1
                }
            }];

            $scope.sohChart = [
                {
                    label: messageService.get('label.stock.on.hand'),
                    data:  $scope.sohChartData,
                    color: "#5eb95e",
                    bars: {
                        show: true,
                        showNumbers: true,
                        numbers : {
                            yAlign: function(y) { if(y!==0){ return y ; }else{return null;}}
                            //show: true
                        },

                         align: "center",
                         barWidth: 0.5,
                         fill: 0.9,
                         lineWidth:1
                    }
                }];

            $scope.renderIt = true;
        });
    };
    $scope.renderIt = false;

    $scope.colorify = function(value){
        if(!isUndefined(value)){
            if(value >= 95) return 'green';
            if(value < 0) return 'blue';
            if(value <= 50) return 'red';
        }

    };


    function generateBarsOption(tickLabel, yaxizLabel){
        return {
            legend: {
                position:"nw",
                noColumns: 1,
                labelBoxBorderColor: "none"
            },
            xaxis: {
                tickLength: 0, // hide gridlines
                // axisLabel: xaxisLabel,
                axisLabelUseCanvas: false,
                ticks: tickLabel,
                labelWidth: 10,
                reserveSpace: true

            } ,
            yaxes: [
                {
                    position: "left",
                    //max: 1070,
                    color: "#5eb95e",
                    axisLabel: yaxizLabel,
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 23
                }
            ],

            grid: {
                hoverable: true,
                borderWidth: 1,
                backgroundColor: { colors: ["#ffffff", "#EDF5FF"] }
            },
            tooltip: true,
            tooltipOpts: {
                content: "%s is %y",
                shifts: {
                    x: 20,
                    y: 0
                },
                defaultTheme: false
            }
        };

    }

}
