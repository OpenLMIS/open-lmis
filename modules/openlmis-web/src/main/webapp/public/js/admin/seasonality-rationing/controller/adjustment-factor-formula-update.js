/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
function AdjustementBasisEditCotntroller($scope, $location, $route, messageService, AdjustmentFactors) {

    $scope.startAdjustmentFactorEdit = function (id) {

        AdjustmentFactors.get({id: id}, function (data) {
            $scope.adjustmentBasisEdit = data.adjustmentFactor;

        });
    };
    $scope.updateAdjustmentFactor = function () {

        $scope.error = "";
        if ($scope.adjustmentBasisEditForm.$invalid) {
            $scope.showError = true;

            $scope.errorMessage = "The form you submitted is invalid. Please revise and try again.";
            return;
        }

        var createSuccessCallback = function (data) {



            $scope.$parent.message = ' Adjustment Basis Updated successfully';

            $scope.adjustmentBasisEdit = {};
        };

        var errorCallback = function (data) {
            $scope.showError = true;

            $scope.errorMessage = messageService.get(data.data.error);
        };
        $scope.error = "";
        if ($scope.adjustmentBasisEdit.id) {

            AdjustmentFactors.update($scope.adjustmentBasisEdit, createSuccessCallback, errorCallback);
        }

        $location.path('/list_adustment_factor');
    };
    $scope.cancelEdit=function(){
        $location.path('/list_adustment_factor');
    };
    $scope.startAdjustmentFactorEdit($route.current.params.id);
}
