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
function SeasonRationingAdjustmentTypeController($scope,$route, $location, $filter, $dialog, messageService, navigateBackService, ngTableParams, seasonalityRationingsList, SeasonalityRationingTypes) {


    $scope.disabled = false;
    $scope.seasonalityRationingType = {};

    $scope.seasonalityRationings = seasonalityRationingsList;

//    storage type search

    $scope.showSeasonalityRationingTypeSearch = function () {

        var query = $scope.query;

        var len = (query === undefined) ? 0 : query.length;

        if (len >= 3) {

            if ($scope.previousQuery.substr(0, 3) === query.substr(0, 3)) {
                $scope.previousQuery = query;

                filterSeasonalityRationingTypesByName(query);
                return true;
            }
            $scope.previousQuery = query;

            SeasonalityRationingTypes.get({param: $scope.query.substr(0, 3)}, function (data) {
                $scope.seasonalityRationingList = data.seasonalityRationingsList;
                filterSeasonalityRationingTypesByName(query);
            }, {});

            return true;
        } else {
            return false;
        }
    };

    $scope.previousQuery = '';
    $scope.query = navigateBackService.query;

    $scope.showSeasonalityRationingTypeSearch();

    var filterSeasonalityRationingTypesByName = function (query) {
        $scope.filteredSeasonalityRationingTypes = [];
        query = query || "";

        angular.forEach($scope.seasonalityRationingList, function (seasonalityRationing) {
            var name = seasonalityRationing.name.toLowerCase();

            if (name.indexOf() >= 0 ||
                name.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0) {
                $scope.filteredSeasonalityRationingTypes.push(seasonalityRationing);
            }
        });
        $scope.resultCount = $scope.filteredSeasonalityRationingTypes.length;
    };
//    end of search


    $scope.clearSearch = function () {
        $scope.query = "";
        $scope.resultCount = 0;
        angular.element("#searchSeaonalityRationingTypes").focus();
        $location.path('/list');
    };
    $scope.editSeaonalityRationingTypes = function (id) {
        if (id) {

            $location.path('/edit/' + id);
        }
    };
    $scope.deleteSeasonalityRationingType = function (result) {
        if (result) {

            var deleteSuccessCallback = function (data) {
                $scope.$parent.message = 'Seasonality/Rationing Adjustment Type Deleted Successfully';

                $scope.seasonalityRationingType = {};

                $scope.seasonalityRationings = seasonalityRationingsList;
                $location.path('/list');
                $route.reload();

            };

            var deleteErorCallback = function (data) {
                $scope.showError = true;

                $scope.errorMessage = messageService.get(data.data.error);


            };
            $adjId = $scope.seasonalityRationingType.id;

            SeasonalityRationingTypes.remove({id: $adjId}, deleteSuccessCallback, deleteErorCallback);

        }
    };
    $scope.showDeleteConfirmDialog = function (seasonalityRationingType) {
        $scope.seasonalityRationingType = seasonalityRationingType;
        var options = {
            id: "removeSeasonalityRationingTypesConfirmDialog",
            header: "Confirmation",
            body: "Are you sure you want to remove the Seasonality/Rationing Adjustment Type: " + seasonalityRationingType.name
        };
        OpenLmisDialog.newDialog(options, $scope.deleteSeasonalityRationingType, $dialog, messageService);
    };
    $scope.clearForm = function () {
        $scope.seasonalityRationingType = {};
        $location.path('/list');
    };
//start of pagination////////////////////////////////////////////////

    // the grid options
    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        total: 0,           // length of data
        count: 25           // count per page
    });

    $scope.paramsChanged = function (params) {
        // slice array data on pages

        $scope.seasonalityRationings = [];
        $scope.data = seasonalityRationingsList;
        params.total = $scope.data.length;

        var data = $scope.data;
        var orderedData = params.filter ? $filter('filter')(data, params.filter) : data;
        orderedData = params.sorting ? $filter('orderBy')(orderedData, params.orderBy()) : data;

        params.total = orderedData.length;
        $scope.seasonalityRationings = orderedData.slice((params.page - 1) * params.count, params.page * params.count);
        var i = 0;
        var baseIndex = params.count * (params.page - 1) + 1;

        while (i < $scope.seasonalityRationings.length) {

            $scope.seasonalityRationings[i].no = baseIndex + i;

            i++;

        }
    };

    // watch for changes of parameters
    $scope.$watch('tableParams', $scope.paramsChanged, false);

//    $scope.getPagedDataAsync = function (pageSize, page) {
//        // Clear the results on the screen
//        $scope.countries = [];
//        $scope.data = [];
//        var params =  {
//            "max" : 10000,
//            "page" : 1
//        };
//
//        $.each($scope.filterObject, function(index, value) {
//            if(value !== undefined)
//                params[index] = value;
//        });
//        $scope.paramsChanged($scope.tableParams);
////
//    };
}
SeasonRationingAdjustmentTypeController.resolve = {
    seasonalityRationingsList: function ($q, $timeout, SeasonalityRationingTypes) {

        var deferred = $q.defer();

        $timeout(function () {

            SeasonalityRationingTypes.get({param: ''}, function (data) {
                deferred.resolve(data.seasonalityRationingsList);
            }, {});

        }, 100);
        return deferred.promise;
    }
};



