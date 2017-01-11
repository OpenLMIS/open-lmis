/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function ReviewDataController($scope, SynchronizedDistributions, ReviewDataFilters) {
  var empty = {};

  $scope.filters = ReviewDataFilters.get(empty, function () {
    $scope.filters.selected = {
      program: $scope.filters.filter.programs[0],
      order: {}
    };

    $scope.sort('synchronized');
  });

  $scope.reloadList = function () {
    $scope.distributionsList = SynchronizedDistributions.get(empty, $scope.filters.selected);
  };

  $scope.sort = function (column) {
    if ($scope.filters.selected.order.column === column) {
      $scope.filters.selected.order.descending = !$scope.filters.selected.order.descending;
    } else {
      $scope.filters.selected.order.column = column;
      $scope.filters.selected.order.descending = true;
    }

    $scope.reloadList();
  };

  $scope.isSort = function (column) {
    if ($scope.filters.selected && $scope.filters.selected.order.column === column) {
      if ($scope.filters.selected.order.descending) {
        return 'icon-angle-down';
      } else {
        return 'icon-angle-up';
      }
    }

    return 'hide';
  };

}

