/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
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

    $scope.highlightRequired = function (showError, value) {
      if (showError && (isUndefined(value))) {
        return "required-error";
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

    $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;

    $scope.page[$scope.visibleTab] = lineItemMap[$scope.visibleTab].slice($scope.pageSize * ($scope.currentPage - 1), $scope.pageSize * $scope.currentPage);
  };

  return{
    refreshGrid: refreshGrid,
    populateScope: populateScope,
    setErrorPages: setErrorPages,
    resetErrorPages: resetErrorPages
  };

});