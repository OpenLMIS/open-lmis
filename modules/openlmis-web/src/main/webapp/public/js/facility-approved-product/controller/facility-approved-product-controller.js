/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function FacilityApprovedProductController($scope, programs, facilityTypes, FacilityApprovedProductsSearch) {

  $scope.programs = programs;
  $scope.facilityTypes = facilityTypes;

  $scope.showResults = false;
  $scope.currentPage = 1;

  $scope.loadProducts = function (page) {
    if (!$scope.program || !$scope.facilityType) return;

    $scope.searchedQuery = $scope.query || "";
    FacilityApprovedProductsSearch.get({page: page, searchParam: $scope.searchedQuery, programId: $scope.program.id, facilityTypeId: $scope.facilityType.id}, function (data) {
      $scope.facilityApprovedProducts = data.facilityApprovedProducts;
      $scope.pagination = data.pagination;
      $scope.totalItems = $scope.pagination.totalRecords;
      $scope.currentPage = $scope.pagination.page;
      $scope.showResults = true;
    }, {});
  };

  $scope.$watch('currentPage', function () {
    if ($scope.currentPage !== 0)
      $scope.loadProducts($scope.currentPage);
  });

  $scope.triggerSearch = function (event) {
    if (event.keyCode === 13) {
      $scope.loadProducts(1);
    }
  };

  $scope.showCategory = function (index) {
    return !((index > 0 ) && ($scope.facilityApprovedProducts[index].programProduct.productCategory.name === $scope.facilityApprovedProducts[index - 1].programProduct.productCategory.name));
  };

  $scope.clearSearch = function () {
    $scope.query = "";
    $scope.loadProducts(1);
  };
}

FacilityApprovedProductController.resolve = {
  programs: function ($q, $timeout, Programs) {
    var deferred = $q.defer();

    $timeout(function () {
      Programs.get({type: "pull"}, function (data) {
        deferred.resolve(data.programs);
      }, {});
    }, 100);
    return deferred.promise;
  },

  facilityTypes: function ($q, $route, $timeout, FacilityTypes) {
    var deferred = $q.defer();

    $timeout(function () {
      FacilityTypes.get({}, function (data) {
        deferred.resolve(data.facilityTypeList);
      }, {});
    }, 100);
    return deferred.promise;
  }
};