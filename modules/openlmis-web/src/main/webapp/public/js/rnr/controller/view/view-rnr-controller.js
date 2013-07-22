/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ViewRnrController($scope, requisition, rnrColumns, regimenTemplate, $location, currency, pageSize, $routeParams) {
  $scope.visibleTab = $routeParams.supplyType;
  $scope.rnr = new Rnr(requisition, rnrColumns);
  $scope.rnrColumns = rnrColumns;
  $scope.regimenColumns = regimenTemplate ? regimenTemplate.columns : [];
  $scope.currency = currency;
  $scope.pageSize = pageSize;
  $scope.visibleColumns = _.where(rnrColumns, {'visible': true});
  $scope.regimenCount = $scope.rnr.regimenLineItems.length;

  var APPROVED = "APPROVED";
  var RELEASED = "RELEASED";
  var NON_FULL_SUPPLY = 'non-full-supply';
  var FULL_SUPPLY = 'full-supply';
  var REGIMEN = 'regimen';

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

  $scope.currentPage = ($routeParams.page) ? parseInt($routeParams.page) || 1 : 1;

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

  currency: function ($q, $timeout, ReferenceData) {
    var deferred = $q.defer();
    $timeout(function () {
      ReferenceData.get({}, function (data) {
        deferred.resolve(data.currency);
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
