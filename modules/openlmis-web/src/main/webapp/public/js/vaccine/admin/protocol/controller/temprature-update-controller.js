/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function TempratureUpdateController($scope, $location, $route, $dialog, messageService, CreateTemprature,
                                    UpdateTemprature,TempratureDetail,DeleteTemprature,TempratureList){
    $scope.startTempratureUpdate=function(id){
        TempratureDetail.get({id:id}, function(data){
            $scope.temprature = data.temprature;
        });
    };
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
    $scope.cancelUpdate=function(){
        $location.path('/temperature');
    };
    $scope.startTempratureUpdate($route.current.params.id);
}
