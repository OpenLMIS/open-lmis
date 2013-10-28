/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
   *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function ViewRnrController($scope, requisition, rnrColumns, regimenTemplate, $location, messageService, pageSize, $routeParams) {
  $scope.visibleTab = $routeParams.supplyType;
  $scope.rnr = new Rnr(requisition, rnrColumns);
  $scope.rnrColumns = rnrColumns;
  $scope.regimenColumns = regimenTemplate ? regimenTemplate.columns : [];
  $scope.currency = messageService.get('label.currency.symbol');
  $scope.pageSize = pageSize;
  $scope.visibleColumns = _.where(rnrColumns, {'visible': true});
  $scope.regimenCount = $scope.rnr.regimenLineItems.length;

  var APPROVED = "APPROVED";
  var RELEASED = "RELEASED";
  var NON_FULL_SUPPLY = 'nonFullSupply';
  var FULL_SUPPLY = 'fullSupply';
  var REGIMEN = 'regimen';

  var lineItemMap = {
    'nonFullSupply': $scope.rnr.nonFullSupplyLineItems,
    'fullSupply': $scope.rnr.fullSupplyLineItems,
    'regimen': $scope.rnr.regimenLineItems
  };

  $scope.requisitionType = $scope.rnr.emergency ? "requisition.type.emergency" : "requisition.type.regular";

  if (!($scope.rnr.status == APPROVED || $scope.rnr.status == RELEASED))
    $scope.visibleColumns = _.filter($scope.visibleColumns, function (column) {
      return column.name != "quantityApproved";
    });

  $scope.showCategory = function (index) {
    return !((index > 0 ) && ($scope.page[$scope.visibleTab][index].productCategory == $scope.page[$scope.visibleTab][index - 1].productCategory));
  };

  var refreshGrid = function () {
    $scope.page = {fullSupply: [], nonFullSupply: [], regimen: []};
    $scope.visibleTab = ($routeParams.supplyType === NON_FULL_SUPPLY) ? NON_FULL_SUPPLY : ($routeParams.supplyType === REGIMEN && $scope.regimenCount) ? REGIMEN : FULL_SUPPLY;

    $location.search('supplyType', $scope.visibleTab);

    if ($scope.visibleTab != REGIMEN) {
      $scope.numberOfPages = Math.ceil(lineItemMap[$scope.visibleTab].length / $scope.pageSize) || 1;
    } else {
      $scope.numberOfPages = 1;
    }

    $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;

    $scope.page[$scope.visibleTab] = lineItemMap[$scope.visibleTab].slice($scope.pageSize * ($scope.currentPage - 1), $scope.pageSize * $scope.currentPage);
  };

  $scope.$on('$routeUpdate', refreshGrid);

  refreshGrid();

  $scope.$watch("currentPage", function () {
    $location.search("page", $scope.currentPage);
  });

  $scope.switchSupplyType = function (supplyType) {
    if (supplyType === $scope.visibleTab)
      return;
    $location.search({page: 1, supplyType: supplyType});
  };

  $scope.getId = function (prefix, parent) {
    return prefix + "_" + parent.$parent.$index;
  };
}

ViewRnrController.resolve = {

  requisition: function ($q, $timeout, Requisitions, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      Requisitions.get({id: $route.current.params.rnr}, function (data) {
        deferred.resolve(data.rnr);
      }, {});
    }, 100);
    return deferred.promise;
  },

  rnrColumns: function ($q, $timeout, ProgramRnRColumnList, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      ProgramRnRColumnList.get({programId: $route.current.params.program}, function (data) {
        deferred.resolve(data.rnrColumnList);
      }, {});
    }, 100);
    return deferred.promise;
  },

  pageSize: function ($q, $timeout, LineItemPageSize) {
    var deferred = $q.defer();
    $timeout(function () {
      LineItemPageSize.get({}, function (data) {
        deferred.resolve(data.pageSize);
      }, {});
    }, 100);
    return deferred.promise;
  },

  regimenTemplate: function ($q, $timeout, $route, ProgramRegimenTemplate) {
    var deferred = $q.defer();
    $timeout(function () {
      ProgramRegimenTemplate.get({programId: $route.current.params.program}, function (data) {
        deferred.resolve(data.template);
      }, {});
    }, 100);
    return deferred.promise;
  }
};
