/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ViewRnrController($scope, requisition, rnrColumns, $location, currency, $routeParams) {
  $scope.rnr = new Rnr(requisition, rnrColumns);
  $scope.rnrColumns = rnrColumns;
  $scope.currency = currency;
  $scope.visibleColumns = _.where(rnrColumns, {'visible': true});

  var APPROVED = "APPROVED";
  var RELEASED = "RELEASED";
  var NON_FULL_SUPPLY = 'non-full-supply';
  var FULL_SUPPLY = 'full-supply';

  if (!($scope.rnr.status == APPROVED || $scope.rnr.status == RELEASED))
    $scope.visibleColumns = _.filter($scope.visibleColumns, function (column) {
      return column.name != "quantityApproved";
    });

  $scope.pageLineItems = [];

  function updateSupplyType() {
    $scope.showNonFullSupply = !!($routeParams.supplyType == NON_FULL_SUPPLY);
  }

  $scope.showCategory = function (index) {
    return !((index > 0 ) && ($scope.pageLineItems[index].productCategory == $scope.pageLineItems[index - 1].productCategory));
  };

  $scope.$broadcast('$routeUpdate');

  function fillPageData() {
    var pageLineItems = $scope.showNonFullSupply ? $scope.rnr.nonFullSupplyLineItems : $scope.rnr.fullSupplyLineItems;
    $scope.numberOfPages = Math.ceil(pageLineItems.length / $scope.pageSize) ? Math.ceil(pageLineItems.length / $scope.pageSize) : 1;
    $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;
    $scope.pageLineItems = pageLineItems.slice(($scope.pageSize * ($scope.currentPage - 1)), $scope.pageSize * $scope.currentPage);
  }

  updateSupplyType();
  fillPageData();


  $scope.$watch("currentPage", function () {
    if (!$routeParams.supplyType) {
      $location.search('supplyType', FULL_SUPPLY);
    }
    $location.search("page", $scope.currentPage);
  });

  $scope.switchSupplyType = function (supplyType) {
    $location.search('page', 1);
    $location.search('supplyType', supplyType);
  };

  $scope.$on('$routeUpdate', function () {
    if ($routeParams.supplyType != 'full-supply' && $routeParams.supplyType != 'non-full-supply') {
      $location.url("requisition/" + $routeParams.rnr + '/' + $routeParams.program + '?supplyType=full-supply&page=1');
      return;
    }
    if (!utils.isValidPage($routeParams.page, $scope.numberOfPages)) {
      $location.search('page', 1);
      return;
    }
    updateSupplyType();
    fillPageData();
  });

  $scope.getId = function (prefix, parent) {
    return prefix + "_" + parent.$parent.$index;
  };

}

ViewRnrController.resolve = {

  requisition: function ($q, $timeout, RequisitionById, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      RequisitionById.get({id: $route.current.params.rnr}, function (data) {
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
  }
};
