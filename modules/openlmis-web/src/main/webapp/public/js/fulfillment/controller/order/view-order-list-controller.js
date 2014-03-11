/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function ViewOrderListController($scope, Orders, messageService, $location, $routeParams) {

  function refreshGrid() {
    $scope.currentPage = $routeParams.page ? utils.parseIntWithBaseTen($routeParams.page) : 1;

    Orders.get({page: $scope.currentPage}, function (data) {
      if ((!data.orders || data.orders.length === 0) && $routeParams.page != 1) {
        $location.search('page', 1);
        return;
      }
      $scope.orders = data.orders || [];
      $scope.pageSize = data.pageSize;
      $scope.numberOfPages = data.numberOfPages || 1;
    });
  }

  $scope.$on('$routeUpdate', refreshGrid);

  refreshGrid();

  $scope.gridOptions = { data: 'orders',
    showFooter: false,
    showColumnMenu: false,
    showFilter: false,
    enableColumnResize: true,
    enableSorting: false,
    columnDefs: [
      {field: 'id', displayName: messageService.get("label.order.no"), width: 70 },
      {field: 'facilityCode', displayName: messageService.get("label.facility.code.name"), cellTemplate: "<div class='ngCellText'><span ng-cell-text>{{row.entity.rnr.facilityCode}} - {{row.entity.rnr.facilityName}}</span></div>"},
      {field: 'rnr.programName', displayName: messageService.get("label.program")},
      {field: 'periodName', displayName: messageService.get("label.period"), cellTemplate: "<div class='ngCellText'><span ng-cell-text>{{row.entity.rnr.periodName}} ({{row.entity.rnr.stringPeriodStartDate}} - {{row.entity.rnr.stringPeriodEndDate}})</span></div>"},
      {field: 'supplyLine.supplyingFacility.name', displayName: messageService.get("label.supplying.depot")},
      {field: 'stringCreatedDate', displayName: messageService.get("label.order.date.time")},
      {field: 'status', displayName: messageService.get("label.order.status"),
        cellTemplate: "<div class='ngCellText orderStatusCellText'><span ng-cell-text><div id=\"orderStatus\"><a href='' class='custom-tooltip shipment-error'><i class='icon-warning-sign' ng-show='row.entity.shipmentError'></i><span class='custom-tooltip-msg' openlmis-message='error.shipment.file'></span></a>  <span ng-bind=\"getStatus(row.entity.status)\"></span></div> "},
      {field: 'ftpComment', displayName: messageService.get("label.comment"),
        cellTemplate: "<div class=''><span ng-cell-text><div id=\"ftpComment\" class='ngCellText'> <span ng-show='row.entity.ftpComment' openlmis-message='row.entity.ftpComment'></span></div>"},
      {field: 'emergency', displayName: messageService.get("requisition.type.emergency"),
        cellTemplate: '<div class="ngCellText checked"><i ng-class="{\'icon-ok\': row.entity.rnr.emergency}"></i></div>',
        width: 90 },
      {cellTemplate: "<div class='ngCellText'><a ng-show=\"row.entity.productsOrdered\" ng-href='/orders/{{row.entity.id}}/download.csv' openlmis-message='link.download.csv'></a>" +
        "<span ng-show=\"!row.entity.productsOrdered\" openlmis-message='msg.no.product.in.order' ng-cell-text></span></div>", width: 180}
    ]
  };

  $scope.getStatus = function (status) {
    return messageService.get("label.order." + status);
  };

  $scope.$watch('currentPage', function () {
    $location.search('page', $scope.currentPage);
  });

}

