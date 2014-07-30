/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function DonorController($scope, sharedSpace, $routeParams, $location, SaveDonor, GetDonor, $dialog, messageService, RemoveDonor) {
    $scope.donor = {};
    $scope.message={};

    if ($routeParams.donorId) {
        GetDonor.get({id: $routeParams.donorId}, function (data) {
          $scope.donor = data.donor;
          $scope.showError = true;
        }, {});
    }

    $scope.cancelAddEdit = function(){
        $scope.$parent.message={};
        $scope.$parent.donorId=null;
        $location.path('#/list');
    };

    $scope.saveDonor = function () {
        var successHandler = function (response) {
            $scope.donor = response.donor;
            $scope.showError = false;
            $scope.error = "";
            $scope.$parent.message = response.success;
            $scope.$parent.donorId = $scope.donor.id;
            $location.path('');
        };

        var errorHandler = function (response) {
            $scope.showError = true;
            $scope.error = messageService.get(response.data.error);
        };

      if(!$scope.donorForm.$invalid){
        SaveDonor.save($scope.donor, successHandler, errorHandler);
      }

        return true;
    };

    $scope.validateDonorShortName = function () {
        $scope.donorShortNameInvalid = $scope.donor.shortName === null;
    };

    $scope.validateDonorLongName = function () {
        $scope.donorLongNameInvalid = $scope.donor.longName === null;
    };

    $scope.showRemoveDonorConfirmDialog = function () {
        if(sharedSpace.getCountOfDonations() > 0){
            $scope.showError = true;
            $scope.error = "Donations have been recorded by this donor. It can't be removed.";
            return false;
        }

        $scope.selectedDonor = $scope.donor;
        var options = {
            id: "removeDonorMemberConfirmDialog",
            header: "Confirmation",
            body: "Are you sure you want to remove the donor: " + $scope.selectedDonor.shortName
        };
        OpenLmisDialog.newDialog(options, $scope.removeDonorConfirm, $dialog, messageService);
    };

    $scope.removeDonorConfirm = function (result) {
        if (result) {

          RemoveDonor.get({id: $scope.selectedDonor.id}, function (){
            // on success
            $scope.$parent.message = "Donor: " + $scope.selectedDonor.shortName + " has been successfully removed. ";
            $location.path('#/list');
          }, function(){
            $scope.error = 'This Donor cannot be deleted because donations have been recorded under it. Please remove the donations before deleting this donor';
          });


        }
        $scope.selectedDonor = undefined;
    };



}

