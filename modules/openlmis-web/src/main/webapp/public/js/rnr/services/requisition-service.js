/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

services.factory('requisitionService', function (messageService) {

  var NON_FULL_SUPPLY = 'nonFullSupply';
  var FULL_SUPPLY = 'fullSupply';
  var REGIMEN = 'regimen';

  var populateScope = function ($scope, $location, $routeParams) {
    $scope.visibleTab = $routeParams.supplyType;
    $scope.currency = messageService.get('label.currency.symbol');
    $scope.requisitionType = $scope.rnr.emergency ? "requisition.type.emergency" : "requisition.type.regular";

    $scope.switchSupplyType = function (supplyType) {
      if (supplyType === $scope.visibleTab)
        return;
      $location.search({page: 1, supplyType: supplyType});
    };

    $scope.showCategory = function (index) {
      return !((index > 0 ) && ($scope.page[$scope.visibleTab][index].productCategory == $scope.page[$scope.visibleTab][index - 1].productCategory));
    };

    $scope.goToPage = function (page, event) {
      angular.element(event.target).parents(".dropdown").click();
      $location.search('page', page);
    };

    $scope.highlightRequired = function (showError, value, skipped) {
      if (showError && isUndefined(value) && !skipped) {
        return "required-error";
      }
      return null;
    };

    $scope.highlightWarningBasedOnField = function (showError, value, field, skipped) {
      if (showError && (isUndefined(value) || value === false) && field && skipped === false) {
        return "warning-error";
      }
      return null;
    };

  };


  var setErrorPages = function ($scope) {
    $scope.errorPages = $scope.rnr.getErrorPages($scope.pageSize);
    $scope.fullSupplyErrorPagesCount = $scope.errorPages.fullSupply.length;
    $scope.nonFullSupplyErrorPagesCount = $scope.errorPages.nonFullSupply.length;
  };

  var resetErrorPages = function ($scope) {
    $scope.errorPages = {fullSupply: [], nonFullSupply: []};
  };

  var refreshGrid = function ($scope, $location, $routeParams, save) {
    var lineItemMap = {
      'nonFullSupply': $scope.rnr.nonFullSupplyLineItems,
      'fullSupply': $scope.rnr.fullSupplyLineItems,
      'regimen': $scope.rnr.regimenLineItems
    };
    if (save) $scope.saveRnr();

    $scope.page = {fullSupply: [], nonFullSupply: [], regimen: []};
    $scope.visibleTab = ($routeParams.supplyType === NON_FULL_SUPPLY) ? NON_FULL_SUPPLY : ($routeParams.supplyType === REGIMEN && $scope.regimenCount) ? REGIMEN : FULL_SUPPLY;

    $location.search('supplyType', $scope.visibleTab);

    if ($scope.visibleTab != REGIMEN) {
      $scope.numberOfPages = Math.ceil(lineItemMap[$scope.visibleTab].length / $scope.pageSize) || 1;
    } else {
      $scope.numberOfPages = 1;
    }

    $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page,
      10) : 1;

    $scope.page[$scope.visibleTab] = lineItemMap[$scope.visibleTab].slice($scope.pageSize * ($scope.currentPage - 1),
      $scope.pageSize * $scope.currentPage);

  };

  function getMappedVisibleColumns(rnrColumns, fixedColumns, skipped) {
    skipped = skipped || [];
    var filteredColumns = _.reject(rnrColumns, function (column) {
      return (skipped.indexOf(column.name) !== -1) || (column.visible !== true);
    });

    var fullSupplyVisibleColumns = _.groupBy(filteredColumns, function (column) {
      if ((fixedColumns.indexOf(column.name) > -1))
        return 'fixed';

      return 'scrollable';
    });

    var nfsColumns = {fixed: [], scrollable: []}; // non-full supply columns
    nfsColumns.fixed = _.filter(fullSupplyVisibleColumns.fixed, function (column) {
      return _.contains(['product', 'productCode'], column.name);
    });

    nfsColumns.scrollable = _.filter(fullSupplyVisibleColumns.scrollable, function (column) {
      return _.contains(RegularRnrLineItem.visibleForNonFullSupplyColumns, column.name);
    });

    // find columns needed for non-full supply products: requested quantity and the reason for the request
    // these are needed/displayed regardless of how the R&R form is setup - ie all non-full supply product requests
    // always have a requested quantity and the associated reason for the request
    // ensure these are in the list of scrollable columns.
    var nfsReqQuantityColumns = _.filter(rnrColumns, function (column) {
      return _.contains(['quantityRequested', 'reasonForRequestedQuantity'], column.name);
    });
    if(nfsReqQuantityColumns.length === 0) throw new Error('Requested Quantity column(s) not found');
    nfsColumns.scrollable = _.union(nfsColumns.scrollable, nfsReqQuantityColumns);

    return {
      fullSupply: fullSupplyVisibleColumns,
      nonFullSupply: nfsColumns
    };
  }

  return{
    refreshGrid: refreshGrid,
    populateScope: populateScope,
    setErrorPages: setErrorPages,
    resetErrorPages: resetErrorPages,
    getMappedVisibleColumns: getMappedVisibleColumns
  };
});