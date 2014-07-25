/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */


function MyRequestsController($scope, PendingRequests, SaveMaintenanceRequest, SaveAndLogMaintenanceRequest, messageService, $routeParams, $location) {

    $scope.$parent.message = '';

    //The following variables store information for the currently selected maintenance request
    $scope.servicePerformedForCurrent = null;
    $scope.findingForCurrent = null;
    $scope.nextDateOfServiceForCurrent = null;

    PendingRequests.get(function(data){
       $scope.list  = data.logs;
    });

    $scope.respondToRequest = function (maintenanceRequest){
        $scope.currentRequest = maintenanceRequest;
        $scope.maintenanceRequestResponseModal = true;
    };

    $scope.closeModal = function(){
        $scope.maintenanceRequestResponseModal = false;
        $scope.currentRequest = null;
    };

    $scope.saveResponse = function () {
        var successHandler = function (response) {
            $scope.error = "";
            $location.path('#/');
            $scope.closeModal();
        };

        var errorHandler = function (response) {
            $scope.error = messageService.get(response.data.error);
        };
        $scope.currentRequest.resolved = true;
        SaveMaintenanceRequest.save($scope.currentRequest, successHandler, errorHandler);
        SaveAndLogMaintenanceRequest.save($scope.currentRequest, successHandler, errorHandler);
    };

}