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

function AdjustmentBasisCreateController($scope, $location, messageService,AdjustmentFactors) {
    $scope.createAdjustmentBasisFormula = function () {

        $scope.error = "";
        if ($scope.adjustmentBasisForm.$invalid) {
            $scope.showError = true;

            $scope.errorMessage = "The form you submitted is invalid. Please revise and try again.";
            return;
        }

        var createSuccessCallback = function (data) {

            $scope.$parent.message = 'New Adjustment Basis created successfully';

            $scope.adjustmentFactor = {};
        };

        var errorCallback = function (data) {
            $scope.showError = true;

            $scope.errorMessage = messageService.get(data.data.error);
        };
        $scope.error = "";


        AdjustmentFactors.save( $scope.adjustmentFactor, createSuccessCallback, errorCallback);

        $location.path('/list_adustment_factor');
    };
    $scope.cancelCreate=function(){
        $location.path('/list_adustment_factor');
    };
}
