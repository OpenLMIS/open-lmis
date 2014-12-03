/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function CountriesLookupController($scope, $location,  $dialog, messageService,navigateBackService, countriesList,Countries) {


    $scope.disabled = false;
    $scope.country = {};

    $scope.countriesList = countriesList;

//    storage type search

    $scope.showCountriesSearch = function () {

        var query = $scope.query;

        var len = (query === undefined) ? 0 : query.length;

        if (len >= 3) {

            if ($scope.previousQuery.substr(0, 3) === query.substr(0, 3)) {
                $scope.previousQuery = query;

                filterCountiresByName(query);
                return true;
            }
            $scope.previousQuery = query;

            Countries.get({param: $scope.query.substr(0, 3)}, function (data) {
                $scope.countries = data.countriesList;
                filterCountiresByName(query);
            }, {});

            return true;
        } else {
            return false;
        }
    };

    $scope.previousQuery = '';
    $scope.query = navigateBackService.query;

    $scope.showCountriesSearch();

    var filterCountiresByName = function (query) {
        $scope.filteredCountries = [];
        query = query || "";

        angular.forEach($scope.countries, function (country) {
            var name = country.name.toLowerCase();

            if (name.indexOf() >= 0 ||
                name.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0 ) {
                $scope.filteredCountries.push(country);
            }
        });
        $scope.resultCount = $scope.filteredCountries.length;
    };
//    end of search
    $scope.createCountries = function () {

        $scope.error = "";
        if ($scope.countriesForm.$invalid) {
            $scope.showError = true;

            $scope.errorMessage = "The form you submitted is invalid. Please revise and try again.";
            return;
        }

        var createSuccessCallback = function (data) {
            $scope.countries = countriesList;
            $scope.$parent.message = 'New Vaccine Storage Type created successfully';

            $scope.country = {};
        };

        var errorCallback = function (data) {
            $scope.showError = true;

            $scope.errorMessage = messageService.get(data.data.error);
        };
        $scope.error = "";


        Countries.save($scope.country, createSuccessCallback, errorCallback);

        $location.path('/countries');
    };
    $scope.cancelEdit=function(){
        $location.path('/countries');
    };
    $scope.clearSearch = function () {
        $scope.query = "";
        $scope.resultCount = 0;
        angular.element("#searchCountries").focus();
        $location.path('/countries');
    };
    $scope.editCountries = function (id) {
        if (id) {

            $location.path('/countries-update/'+id);
        }
    };
    $scope.deleteCountries = function (result) {
        if (result) {

            var deleteSuccessCallback = function (data) {
                $scope.$parent.message = 'Country Deleted Successfully';

                $scope.country = {};

                    $scope.countries = countriesList;

            };

            var deleteErorCallback = function (data) {
                $scope.showError = true;

                $scope.errorMessage = messageService.get(data.data.error);
            };

            Countries.remove($scope.country, deleteSuccessCallback, deleteErorCallback);

        }
    };
    $scope.showDeleteConfirmDialog = function (country) {
        $scope.country = country;
        var options = {
            id: "removeCountriesConfirmDialog",
            header: "Confirmation",
            body: "Are you sure you want to remove the Countries: " + country.name
        };
        OpenLmisDialog.newDialog(options, $scope.deleteCountries, $dialog, messageService);
    };
    $scope.clearForm = function () {
        $scope.country = {};
        $location.path('/countries');
    };


}
CountriesLookupController.resolve = {
    countriesList: function ($q, $timeout, Countries) {
        var deferred = $q.defer();

        $timeout(function () {
            // show the list of users by a default
            Countries.get({param: ''}, function(data){
                deferred.resolve( data.countriesList );
            },{});

        }, 100);
        return deferred.promise;
    }
};



