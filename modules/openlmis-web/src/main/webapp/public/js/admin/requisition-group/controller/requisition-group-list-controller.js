/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function RequisitionGroupListController($scope, sharedSpace, $location, navigateBackService, RequisitionGroupCompleteList) {
    sharedSpace.setShouldReloadTheList(false);
    $scope.$on('$viewContentLoaded', function () {
        $scope.$apply($scope.query = navigateBackService.query);
        $scope.showRequisitionGroupsList('txtFilterRequisitionGroups');
    });

    $scope.previousQuery = '';

    $scope.showRequisitionGroupsList = function (id) {

        RequisitionGroupCompleteList.get(function (data) {
            $scope.filteredRequisitionGroups = data.requisitionGroups;
            $scope.requisitionGroupsList = $scope.filteredRequisitionGroups;
        });

        var query = document.getElementById(id).value;
        $scope.query = query;

        filterRequisitionGroupsByName(query);
        return true;
    };

    $scope.editRequisitionGroup = function (id, programCount) {
        var data = {query: $scope.query};
        navigateBackService.setData(data);
        sharedSpace.setCountOfPrograms(programCount);
        $location.path('edit/' + id);
    };


    $scope.clearSearch = function () {
        $scope.query = "";
        $scope.resultCount = 0;
        angular.element("#txtFilterRequisitionGroups").focus();
    };

    var filterRequisitionGroupsByName = function (query) {
        query = query || "";

        if (query.length === 0) {
            $scope.filteredRequisitionGroups = $scope.requisitionGroupsList;
        }
        else {
            $scope.filteredRequisitionGroups = [];
            angular.forEach($scope.requisitionGroupsList, function (reqGroup) {

                if (reqGroup.name.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0) {
                    $scope.filteredRequisitionGroups.push(reqGroup);
                }
            });
            $scope.resultCount = $scope.filteredRequisitionGroups.length;
        }
    };

    $scope.filterRequisitionGroups = function (id) {
        var query = document.getElementById(id).value;
        $scope.query = query;
        filterRequisitionGroupsByName(query);
    };

    $scope.$watch('sharedSpace.getShouldReloadTheList()',function(){
        $scope.$apply($scope.query = navigateBackService.query);
        $scope.showRequisitionGroupsList('txtFilterRequisitionGroups');
    });
}