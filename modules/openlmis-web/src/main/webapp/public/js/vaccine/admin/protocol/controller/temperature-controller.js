/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function TempratureLookupController($scope, $location, $filter,ngTableParams, $dialog, messageService, CreateTemprature,
                                    UpdateTemprature,navigateBackService,DeleteTemprature,TempratureList,Tempratures,tempratureList) {




    $scope.disabled = false;
    $scope.temprature = {};

        $scope.tempratureList = tempratureList;


    $scope.createTemperature = function () {

        $scope.error = "";
        if ($scope.tempratureForm.$invalid) {
            $scope.showError = true;

            $scope.errorMessage = "The form you submitted is invalid. Please revise and try again.";
            return;
        }

        var createSuccessCallback = function (data) {
            TempratureList.get({}, function (data) {
                $scope.tempratureList = data.temperatureList;
            }, function (data) {
                $location.path($scope.$parent.sourceUrl);
            });
            $scope.$parent.message = 'Temperature created successfully';

            $scope.temprature = {};
        };

        var errorCallback = function (data) {
            $scope.showError = true;

            $scope.errorMessage = messageService.get(data.data.error);
        };
        $scope.error = "";
        if($scope.temprature.id){

            UpdateTemprature.save($scope.temprature, createSuccessCallback, errorCallback);
        }
        else{

            CreateTemprature.save($scope.temprature, createSuccessCallback, errorCallback);
        }
        $location.path('/temperature');
    };
$scope.cancelCreate=function(){
    $location.path('/temperature');
};
    $scope.editTemprature=function(id){
        if(id){
            $location.path('/temperature-update/'+id);
        }
    };
    $scope.deleteTemprature=function(result){
        if(result){

            var deleteSuccessCallback = function (data) {
                $scope.$parent.message = 'Temprature Deleted Successfully';

                $scope.temprature = {};
                TempratureList.get({}, function (data) {
                    $scope.tempratureList = data.temperatureList;
                }, function (data) {
                    $location.path($scope.$parent.sourceUrl);
                });
            };

            var deleteErorCallback = function (data) {
                $scope.showError = true;

                $scope.errorMessage = messageService.get(data.data.error);
            };
            DeleteTemprature.save( $scope.temprature,deleteSuccessCallback,deleteErorCallback);
        }
    };
    $scope.showDeleteConfirmDialog = function (temprature) {
        $scope.temprature=temprature;
        var options = {
            id: "removeTempratureConfirmDialog",
            header: "Confirmation",
            body: "Are you sure you want to remove the Temperature: "+temprature.tempratureName
        };
        OpenLmisDialog.newDialog(options,$scope.deleteTemprature, $dialog, messageService);
    };
    $scope.clearForm=function(){
        $scope.temprature = {};
    };
//    temrature search
    $scope.showTempratureSearch = function () {

        var query = $scope.query;

        var len = (query === undefined) ? 0 : query.length;

        if (len >= 3) {

            if ($scope.previousQuery.substr(0, 3) === query.substr(0, 3)) {
                $scope.previousQuery = query;

                filterTempratureByName(query);
                return true;
            }
            $scope.previousQuery = query;
            Tempratures.get({param: $scope.query.substr(0, 3)}, function (data) {
                $scope.tempratureList1 = data.temperatureList;
                filterTempratureByName(query);
            }, {});

            return true;
        } else {
            return false;
        }
    };

    $scope.previousQuery = '';
    $scope.query = navigateBackService.query;
    $scope.showTempratureSearch();
    var filterTempratureByName = function (query) {
        $scope.filteredTempratureList = [];
        query = query || "";

        angular.forEach($scope.tempratureList1, function (temprature) {
            var tempratureName = temprature.tempratureName.toLowerCase();

            if (tempratureName.indexOf() >= 0 ||
                tempratureName.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0 ) {
                $scope.filteredTempratureList.push(temprature);
            }
        });
        $scope.resultCount = $scope.filteredTempratureList.length;
    };
    $scope.clearSearch = function () {
        $scope.query = "";
        $scope.resultCount = 0;
        angular.element("#searchTemprature").focus();

    };
//    end of search
    //start of pagination////////////////////////////////////////////////

    // the grid options
    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        total: 0,           // length of data
        count: 25           // count per page
    });

    $scope.paramsChanged = function (params) {
        // slice array data on pages

        $scope.tempratureList = [];
        $scope.data = tempratureList;

        params.total = $scope.data.length;

        var data = $scope.data;
        var orderedData = params.filter ? $filter('filter')(data, params.filter) : data;
        orderedData = params.sorting ? $filter('orderBy')(orderedData, params.orderBy()) : data;

        params.total = orderedData.length;
        $scope.tempratureList = orderedData.slice((params.page - 1) * params.count, params.page * params.count);
        var i = 0;
        var baseIndex = params.count * (params.page - 1) + 1;

        while (i < $scope.tempratureList.length) {

            $scope.tempratureList[i].no = baseIndex + i;

            i++;

        }
    };

    // watch for changes of parameters
    $scope.$watch('tableParams', $scope.paramsChanged, true);

    $scope.getPagedDataAsync = function (pageSize, page) {
        // Clear the results on the screen
        $scope.tempratureList = [];
        $scope.data = [];
        var params = {
            "max": 10000,
            "page": 1
        };

        $.each($scope.filterObject, function (index, value) {
            if (value !== undefined)
                params[index] = value;
        });
        $scope.paramsChanged($scope.tableParams);
    };
// end of pagination
}
///////////////////////////
TempratureLookupController.resolve = {
    tempratureList: function ($q, $timeout, Tempratures) {
        var deferred = $q.defer();

        $timeout(function () {
            // show the list of users by a default
            Tempratures.get({param: ''}, function(data){
                deferred.resolve( data.temperatureList );
            },{});

        }, 100);
        return deferred.promise;
    }
};

