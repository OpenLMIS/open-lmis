/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function TempratureLookupController($scope, $location, $route, $dialog, messageService, CreateTemprature,
                                    UpdateTemprature,TempratureDetail,DeleteTemprature,TempratureList) {




    $scope.disabled = false;
    $scope.temprature = {};
    TempratureList.get({}, function (data) {
        $scope.tempratureList = data.temperatureList;
    }, function (data) {
        $location.path($scope.$parent.sourceUrl);
    });

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

    };

    $scope.editTemprature=function(id){
        if(id){
            TempratureDetail.get({id:id}, function(data){
                $scope.temprature = data.temprature;
//            if($scope.editHelpTopic.active === false){
//                $scope.disableAllFields();
//            }
            });
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
}


