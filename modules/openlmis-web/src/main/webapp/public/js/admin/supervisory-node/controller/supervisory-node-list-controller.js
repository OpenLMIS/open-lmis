/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function SupervisoryNodeListController($scope, $location, navigateBackService, SupervisoryNodeCompleteList) {
    $scope.reloadTheList = false;

    $scope.$on('$viewContentLoaded', function () {
        $scope.$apply($scope.query = navigateBackService.query);
        $scope.showSupervisoryNodesList('txtFilterSupervisoryNodes');
    });
    $scope.previousQuery = '';

    $scope.showSupervisoryNodesList = function (id) {

        SupervisoryNodeCompleteList.get(function(data){
            $scope.filteredSupervisoryNodes = data.supervisoryNodes;
            $scope.supervisoryNodesList = $scope.filteredSupervisoryNodes;
        });

        var query = document.getElementById(id).value;
        $scope.query = query;

        filterSupervisoryNodesByName(query);
        return true;
    };

    $scope.editSupervisoryNode = function (id) {
        var data = {query: $scope.query};
        navigateBackService.setData(data);
        $location.path('edit/' + id);
    };

    $scope.clearSearch = function () {
        $scope.query = "";
        $scope.resultCount = 0;
        angular.element("#txtFilterSupervisoryNodes").focus();
    };

    var filterSupervisoryNodesByName = function (query) {
        query = query || "";

        if (query.length === 0) {
            $scope.filteredSupervisoryNodes = $scope.supervisoryNodesList;
        }
        else {
            $scope.filteredSupervisoryNodes = [];
            angular.forEach($scope.supervisoryNodesList, function (supervisoryNode) {

                if (supervisoryNode.name.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0) {
                    $scope.filteredSupervisoryNodes.push(supervisoryNode);
                }
            });
            $scope.resultCount = $scope.filteredSupervisoryNodes.length;
        }
    };

    $scope.filterSupervisoryNodes = function (id) {
        var query = document.getElementById(id).value;
        $scope.query = query;
        filterSupervisoryNodesByName(query);
    };

    $scope.$watch('reloadTheList',function(){
        $scope.$apply($scope.query = navigateBackService.query);
        $scope.showSupervisoryNodesList('txtFilterSupervisoryNodes');
    });

}