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
function RepairManagementController($scope,ngTableParams,messageService,CCERepairManagement,$location,CCERepairManagementEquipmentList){

    $scope.pieChart=false;
    $scope.equipmentDialogModal=false;

    // the grid options
    $scope.tableParams = new ngTableParams({
            page: 1,            // show first page
            total: 0,           // length of data
            count: 15           // count per page
     });

    $scope.OnFilterChanged = function(){
          $scope.resetRepairManagementData();
          $scope.filter.max = 10000;
          $scope.data = $scope.datarows = [];
          CCERepairManagement.get($scope.filter, function(data) {
          if (data.pages !== undefined && data.pages.rows !== undefined && data.pages.rows[0]!==null) {
                     $scope.data =$scope.datarows= data.pages.rows;
                     $scope.paramsChanged($scope.tableParams);
            }

          if($scope.filter.aggregate ==='TRUE'){ $scope.aggregate=true;}
          else{ $scope.aggregate=false;}
          $scope.aggregateType = $("facility-level-filter select option:selected").html();

                });
     };

     var list=messageService.get('label.repair.management.list');
     var aggregate=messageService.get('label.repair.management.aggregate');
     $scope.types = [
             {'name': list, 'value': "FALSE"},
             {'name': aggregate, 'value': "TRUE"}
         ];

     $scope.loadPieChart = function(row){

         $scope.resetRepairManagementData();
         $scope.RepairManagementPieChartData = [];
         $scope.RepairManagementNotFunctionalPieChartData=[];
         $scope.selectedData=row;
         $scope.notFunctional=false;

         var pieChartData= [];
         var notFunctionalPieChartData=[];
         var functional = row.functional;
         var notFunctional = row.not_functional;
         var functionalNotInstalled = row.functional_not_installed;

         var colors=['#00e500','#e50000','#e5e500'];
         pieChartData = [{label:'functional',total:functional},{label:'not_functional',total:notFunctional},{label:'functional_not_installed',total:functionalNotInstalled}];

         for(var i =0; i<pieChartData.length;i++){
             $scope.RepairManagementPieChartData [i]={
                 label: pieChartData[i].label,
                 data: pieChartData[i].total,
                 color: colors[i]
              };
         }

         //Not Functional Pie Chart
         if (row.not_functional !== 0)
         {
             $scope.notFunctional=true;
             var obsolete = row.obsolete;
             var waitForRepair = row.waiting_For_Repair;
             var waitingForSpareParts = row.waiting_For_Spare_Parts;
             var notFunctionalColors=['#005e00','#5e0000','#5e5e00'];
             notFunctionalPieChartData = [{label:'Obsolete',total:obsolete},{label:'Waiting For Repair',total:waitForRepair},{label:'Waiting For Spare Parts',total:waitingForSpareParts}];

                 for(var j =0; j<notFunctionalPieChartData.length;j++){
                     $scope.RepairManagementNotFunctionalPieChartData [j]={
                              label: notFunctionalPieChartData[j].label,
                              data: notFunctionalPieChartData[j].total,
                              color: notFunctionalColors[j]
                      };
             }
         }

         $scope.RepairManagementPieChartOptionFunction();
         $scope.pieChart=true;
         $scope.facilityName=row.facility_name;
    };

   $scope.resetRepairManagementData = function () {
        $scope.RepairManagementPieChartData = null;
        $scope.RepairManagementPieChartOption = null;
        $scope.RepairManagementNotFunctionalPieChartData=null;
        $scope.RepairManagementNotFunctionalPieChartOption=null;
        $scope.dataRows = null;
        $scope.RepairManagementRenderedData = null;
        $scope.selectedData=null;
        $scope.pieChart=false;
    };
   $scope.repairManagementChartClickHandler = function (event, pos, item) {
      $scope.equipmentDialogModal = true;
      var facilityType=$scope.filter.facilityType;
      var facilityId=$scope.selectedData.facility_id;
      var aggregate=$scope.filter.aggregate;
      var workingStatus=item.series.label;

      $scope.filter.workingStatus=workingStatus;
      $scope.filter.facilityId=facilityId;
      $scope.$apply();
      CCERepairManagementEquipmentList.get($scope.filter, function(data) {
            if (data.pages !== undefined && data.pages.rows !== undefined && data.pages.rows[0]!==null) {
                 $scope.equipments = data.pages;
                 // Capacity by functionality
                    var groups = _(data.pages.rows).groupBy('working_status');
                    $scope.capacity = _(groups).map(function(g, key) {
                      return { status: key,
                               totalCapacity: _(g).reduce(function(m,x) { return m + x.capacity; }, 0) };
                    });

              }
              else{
                 $scope.equipments=[];
              }
              });
         $scope.$apply();
    };


   $scope.closeModal = function () {
      $scope.equipmentDialogModal = false;
   };

   function flotChartHoverCursorHandler(event, pos, item) {

        if (item && !isUndefined(item.dataIndex)) {
            $(event.target).css('cursor', 'pointer');
        } else {
            $(event.target).css('cursor', 'auto');
        }
    }

   function bindChartEvent(elementSelector, eventType, callback) {
        $(elementSelector).bind(eventType, callback);
    }


    $scope.RepairManagementPieChartOptionFunction = function () {

            $scope.RepairManagementPieChartOption = {
                series: {
                    pie: {
                        show: true,
                        align:"left",
                        radius: 1,
                        label: {
                            show: true,
                            radius: 2 / 4,
                            formatter: function (label, series) {
                                return '<div style="font-size:8pt;text-align:center;padding:1px;color:#FFFFFF;">' + Math.round(series.percent) + '%</div>';
                            },
                            threshold: 0.1
                        }
                    }
                },
                legend: {
                    container: $("#repairManagementReportLegend"),
                    noColumns: 0,
                    labelBoxBorderColor: "none",
                    // width: 20,
                    align:"left"
                },
                grid: {
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

            $scope.RepairManagementNotFunctionalPieChartOption = {
                            series: {
                                pie: {
                                    show: true,
                                    align:"left",
                                    radius: 1,
                                    label: {
                                        show: true,
                                        radius: 2 / 4,
                                        formatter: function (label, series) {
                                            return '<div style="font-size:8pt;text-align:center;padding:1px;color:#FFFFFF;">' + Math.round(series.percent) + '%</div>';
                                        },
                                        threshold: 0.1
                                    }
                                }
                            },
                            legend: {
                                container: $("#repairManagementNotFunctionalReportLegend"),
                                noColumns: 0,
                                labelBoxBorderColor: "none",
                                // width: 20,
                                align:"left"
                            },
                            grid: {
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
        };
    bindChartEvent("#repair-management", "plotclick", $scope.repairManagementChartClickHandler);
     //  bindChartEvent("#order-fill-rate-summary", flotChartHoverCursorHandler);


    $scope.exportReport   = function (type){
        $scope.filter.pdformat = 1;
                  var params = jQuery.param($scope.filter);
                  var url = '/reports/download/cce_repair_management/' + type +'?' + params;
                  window.open(url);
     };

     $scope.exportList   = function (type){
             $scope.filter.pdformat = 1;
                       var params = jQuery.param($scope.filter);
                       var url = '/reports/download/cce_repair_management_equipment_list/' + type +'?' + params;
                       window.open(url);
          };
}
