
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
function AdjustmentBasisFormulaController($scope, $location, $filter, $dialog, messageService,navigateBackService,ngTableParams,adjustmentFactorList,AdjustmentFactors) {


    $scope.disabled = false;
    $scope.adjustmentFactor = {};

    $scope.adjustmentFactors = adjustmentFactorList;

//    storage type search

    $scope.showAdjustmentFactorSearch = function () {

        var query = $scope.query;

        var len = (query === undefined) ? 0 : query.length;

        if (len >= 3) {

            if ($scope.previousQuery.substr(0, 3) === query.substr(0, 3)) {
                $scope.previousQuery = query;

                filterAdjustmentFactorsByName(query);
                return true;
            }
            $scope.previousQuery = query;

            AdjustmentFactors.get({param: $scope.query.substr(0, 3)}, function (data) {
                $scope.adjustmentFactorsList = data.adjustmentFactorList;
                filterAdjustmentFactorsByName(query);
            }, {});

            return true;
        } else {
            return false;
        }
    };

    $scope.previousQuery = '';
    $scope.query = navigateBackService.query;

//    $scope.showAdjustmentBasisSearch();

    var filterAdjustmentFactorsByName = function (query) {
        $scope.filteredAdjustmentFactors = [];
        query = query || "";

        angular.forEach($scope.adjustmentFactorsList, function (adjustmentFactor) {
            var name = adjustmentFactor.name.toLowerCase();

            if (name.indexOf() >= 0 ||
                name.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0) {
                $scope.filteredAdjustmentFactors.push(adjustmentFactor);
            }
        });
        $scope.resultCount = $scope.filteredAdjustmentFactors.length;
    };
//    end of search


    $scope.clearSearch = function () {
        $scope.query = "";
        $scope.resultCount = 0;
        angular.element("#searchAdjustmentFactors").focus();
        $location.path('/list_adustment_factor');
    };
    $scope.editAdjustmentBasis = function (id) {
        if (id) {

            $location.path('/edit_adustment_factor/' + id);
        }
    };
    $scope.deleteAdjustmentFactor = function (result) {
        if (result) {

            var deleteSuccessCallback = function (data) {
                $scope.$parent.message = 'Adjustment Basis (Formula) Deleted Successfully';

                $scope.adjustmentFactor = {};

                $scope.adjustmentFactors = adjustmentFactorList;

            };

            var deleteErorCallback = function (data) {
                $scope.showError = true;

                $scope.errorMessage = messageService.get(data.data.error);

            };

            AdjustmentFactors.delete($scope.adjustmentFactor, deleteSuccessCallback, deleteErorCallback);

        }
    };
    $scope.showDeleteConfirmDialog = function (adjustmentFactor) {
        $scope.adjustmentFactor = adjustmentFactor;
        var options = {
            id: "removeSeasonalityRationingTypesConfirmDialog",
            header: "Confirmation",
            body: "Are you sure you want to remove the Adjustment Basis(Formula): " + adjustmentFactor.name
        };
        OpenLmisDialog.newDialog(options, $scope.deleteAdjustmentFactor, $dialog, messageService);
    };
    $scope.clearForm = function () {
        $scope.adjustmentFactor = {};
        $location.path('/list_adustment_factor');
    };
//start of pagination////////////////////////////////////////////////

    // the grid options
    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        total: 0,           // length of data
        count: 25           // count per page
    });

    $scope.paramsChanged = function(params) {
        // slice array data on pages

        $scope.adjustmentFactors = [];
        $scope.data = adjustmentFactorList;
        params.total =  $scope.data.length;

        var data = $scope.data;
        var orderedData = params.filter ? $filter('filter')(data, params.filter) : data;
        orderedData = params.sorting ?  $filter('orderBy')(orderedData, params.orderBy()) : data;

        params.total = orderedData.length;
        $scope.adjustmentFactors = orderedData.slice( (params.page - 1) * params.count,  params.page * params.count );
        var i = 0;
        var baseIndex = params.count * (params.page - 1) + 1;

        while(i < $scope.adjustmentFactors.length){

            $scope.adjustmentFactors[i].no = baseIndex + i;

            i++;

        }
    };

    // watch for changes of parameters
    $scope.$watch('tableParams', $scope.paramsChanged , false);

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
AdjustmentBasisFormulaController.resolve = {
    adjustmentFactorList: function ($q, $timeout, AdjustmentFactors) {

        var deferred = $q.defer();

        $timeout(function () {

            AdjustmentFactors.get({param: ''}, function(data){
                deferred.resolve( data.adjustmentFactorList );
            },{});

        }, 100);
        return deferred.promise;
    }
};



