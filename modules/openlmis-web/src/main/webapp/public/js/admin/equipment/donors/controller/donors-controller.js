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

function DonorController($scope, sharedSpace, $routeParams, $location, SaveDonor, GetDonor, $dialog, messageService, RemoveDonor) {
  $scope.donor = {};
  $scope.message = {};

  if ($routeParams.donorId) {
    GetDonor.get({id: $routeParams.donorId}, function (data) {
      $scope.donor = data.donor;
      $scope.showError = true;
    }, {});
  }

  $scope.cancelAddEdit = function () {
    $scope.$parent.message = {};
    $scope.$parent.donorId = null;
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

    if (!$scope.donorForm.$invalid) {
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
    if (sharedSpace.getCountOfDonations() > 0) {
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

      RemoveDonor.get({id: $scope.selectedDonor.id}, function (data) {
        $scope.$parent.message = messageService.get(data.success);
        $location.path('#/list');
      }, function () {
        $scope.error = messageService.get(data.error);
      });

    }
    $scope.selectedDonor = undefined;
  };


}

