/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function CountriesUpdateController($scope, $location, $route, messageService,countriesList, Countries) {

    $scope.startCountiesEdit = function (id) {

        Countries.get({id: id}, function (data) {
            $scope.country = data.countries;

        });



    };
    $scope.updateCountries = function () {

        $scope.error = "";
        if ($scope.countriesForm.$invalid) {
            $scope.showError = true;

            $scope.errorMessage = "The form you submitted is invalid. Please revise and try again.";
            return;
        }

        var createSuccessCallback = function (data) {

                $scope.countries = countriesList;

            $scope.$parent.message = 'Countries Updated successfully';

            $scope.country = {};
        };

        var errorCallback = function (data) {
            $scope.showError = true;

            $scope.errorMessage = messageService.get(data.data.error);
        };
        $scope.error = "";
        if ($scope.country.id) {

            Countries.update($scope.country, createSuccessCallback, errorCallback);
        }

        $location.path('/countries');
    };
    $scope.cancelEdit=function(){
        $location.path('/countries');
    };
    $scope.startCountiesEdit($route.current.params.id);
}
CountriesUpdateController.resolve = {
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