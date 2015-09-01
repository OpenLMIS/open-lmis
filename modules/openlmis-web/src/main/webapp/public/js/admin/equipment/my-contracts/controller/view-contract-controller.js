/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

function ViewServiceContractController($scope, $routeParams, $location, Contract, Equipments, ServiceTypes) {


  if ($routeParams.id === undefined) {
    $scope.current = {};
  } else {
    Contract.get({
      id: $routeParams.id
    }, function (data) {
      $scope.current = data.contract;
      $scope.current.startDate = data.contract.startDateString;
      $scope.current.endDate = data.contract.endDateString;
      $scope.current.contractDate = data.contract.contractDateString;

    });
  }

  // get the lookups that will be checked
  Equipments.get(function (data) {
    $scope.equipments = data.equipments;
  });

  ServiceTypes.get(function (data) {
    $scope.service_types = data.service_types;
  });
  // facilities could be complicated, may have to depend on the program selection.


  $scope.cancel = function () {
    $location.path('');
  };
}