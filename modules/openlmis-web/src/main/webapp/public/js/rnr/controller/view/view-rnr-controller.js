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
  var NON_FULL_SUPPLY = 'non-full-supply';
  var FULL_SUPPLY = 'full-supply';
  var REGIMEN = 'regimen';

  $scope.requisitionType =
    messageService.get($scope.rnr.emergency ? "requisition.type.emergency" : "requisition.type.regular");

  if (!($scope.rnr.status == APPROVED || $scope.rnr.status == RELEASED))
    $scope.visibleColumns = _.filter($scope.visibleColumns, function (column) {
      return column.name != "quantityApproved";
    });

  $scope.pageLineItems = [];

  $scope.showCategory = function (index) {
    return !((index > 0 ) && ($scope.pageLineItems[index].productCategory == $scope.pageLineItems[index - 1].productCategory));
  };

  $scope.$broadcast('$routeUpdate');

  function fillPageData() {
    var pageLineItems = $scope.visibleTab == NON_FULL_SUPPLY ? $scope.rnr.nonFullSupplyLineItems : $scope.visibleTab == FULL_SUPPLY ? $scope.rnr.fullSupplyLineItems : [];
    $scope.numberOfPages = Math.ceil(pageLineItems.length / $scope.pageSize) ? Math.ceil(pageLineItems.length / $scope.pageSize) : 1;
    $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;
    $scope.pageLineItems = pageLineItems.slice(($scope.pageSize * ($scope.currentPage - 1)), $scope.pageSize * $scope.currentPage);
  }

  fillPageData();

  $scope.currentPage = ($routeParams.page) ? utils.parseIntWithBaseTen($routeParams.page) || 1 : 1;

  $scope.$watch("currentPage", function () {
    $location.search("page", $scope.currentPage);
  });

  $scope.switchSupplyType = function (supplyType) {
    $scope.visibleTab = supplyType;
    $location.search('page', 1);
    $location.search('supplyType', supplyType);
  };

  $scope.$on('$routeUpdate', function () {
    $scope.visibleTab = $routeParams.supplyType == NON_FULL_SUPPLY ? NON_FULL_SUPPLY : ($routeParams.supplyType == REGIMEN && $scope.regimenCount) ? REGIMEN : FULL_SUPPLY;
    $location.search('supplyType', $scope.visibleTab);

    if (!utils.isValidPage($routeParams.page, $scope.numberOfPages)) {
      $location.search('page', 1);
      return;
    }
    fillPageData();
  });

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
