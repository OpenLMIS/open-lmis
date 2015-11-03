/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function ManagePODController($scope, OrdersForManagePOD, messageService, OrderPOD, CreateRequisitionProgramList, UserSupervisedFacilitiesForProgram, $location) {

  $scope.option = {all: false};

  CreateRequisitionProgramList.get(function(data){
    $scope.programs = data.programList;
  });

  $scope.onParamChanged = function(){
    $scope.orders = [];
    if(!angular.isUndefined($scope.filter.program) && $scope.filter.program !== null){
      UserSupervisedFacilitiesForProgram.get({programId: $scope.filter.program}, function (data) {
        $scope.facilities = data.facilities;
      });

      if($scope.option.all){
        OrdersForManagePOD.get({program: $scope.filter.program}, function (data) {
          $scope.orders = data.ordersForPOD;
        });
      }
    }
  };

  $scope.onFacilityChanged = function(){
    OrdersForManagePOD.get({program: $scope.filter.program, facility: $scope.filter.facility}, function (data) {
      $scope.orders = data.ordersForPOD;
    });
  };

  $scope.gridOptions = { data: 'orders',
    showFooter: false,
    showColumnMenu: false,
    showFilter: false,
    enableColumnResize: true,
    enableSorting: false,
    afterSelectionChange: function (rowItem) {
      $scope.createPOD(rowItem.entity.id);
    },
    columnDefs: [
      {field: 'orderNumber', displayName: messageService.get("label.order.no"), width: 70, cellTemplate: "<div class='ngCellText'><span id = 'order{{row.rowIndex}}' class='orderNumber'>{{row.entity.orderNumber}}</span></div>"},
      {field: 'supplyLine.supplyingFacility.name', displayName: messageService.get("label.supplying.depot")},
      {field: 'facilityCode', displayName: messageService.get("label.facility.code.name"), cellTemplate: "<div class='ngCellText'><span ng-cell-text>{{row.entity.rnr.facilityCode}} - {{row.entity.rnr.facilityName}}</span></div>"},
      {field: 'rnr.programName', displayName: messageService.get("label.program"), cellTemplate: "<div class='ngCellText'><span id = 'program{{row.rowIndex}}'>{{row.entity.rnr.programName}}</span></div>"},
      {field: 'periodName', displayName: messageService.get("label.period"), cellTemplate: "<div class='ngCellText'><span ng-cell-text>{{row.entity.rnr.periodName}} ({{row.entity.rnr.stringPeriodStartDate}} - {{row.entity.rnr.stringPeriodEndDate}})</span></div>"},
      {field: 'stringCreatedDate', displayName: messageService.get("label.order.date.time")},
      {field: 'status', displayName: messageService.get("label.order.status"),
        cellTemplate: "<div class='ngCellText'><span ng-cell-text ng-bind=\"getStatus(row.entity.status)\"></span></div> "},
      {field: 'emergency', displayName: messageService.get("requisition.type.emergency"),
        cellTemplate: '<div class="ngCellText checked"><i ng-class="{\'icon-ok\': row.entity.rnr.emergency}"></i></div>',
        width: 90 },
      {cellTemplate: "<div class='ngCellText'><a href='' id='updatePod{{row.rowIndex}}' openlmis-message='link.update.pod'></a></div>", width: 180}
    ]
  };

  $scope.createPOD = function (orderId) {
    OrderPOD.save({orderId: orderId}, {}, function (data) {
      $location.url('/pods/' + data.orderPOD.id + '?page=1');
    }, {});
  };

  $scope.getStatus = function (status) {
    return messageService.get("label.order." + status);
  };

}