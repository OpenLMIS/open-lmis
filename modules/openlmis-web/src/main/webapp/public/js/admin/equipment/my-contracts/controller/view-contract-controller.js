/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
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
    Equipments.get(function(data){
       $scope.equipments = data.equipments;
    });

    ServiceTypes.get(function(data){
       $scope.service_types = data.service_type;
    });
    // facilities could be complicated, may have to depend on the program selection.


    $scope.cancel = function () {
        $location.path('');
    };
}