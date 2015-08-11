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
function HelpContentCreateController($scope, $location,messageService,CreateHelpContent,HelpTopicList) {
    HelpTopicList.get({}, function (data) {
        $scope.helpTopicList = data.helpTopicList;
    }, function (data) {
        $location.path($scope.$parent.sourceUrl);
    });
    $scope.disabled = false;
    $scope.htmlContent=$scope.htmlcontent;
    ////
//    Masquerade perfers the scope value over the innerHTML
//    Uncomment this line to see the effect:
    $scope.htmlcontenttwo = "Override originalContents";

    $scope.createHelpContent = function () {
        //////alert('here ii am');
        $scope.error = "";
        if ($scope.createHelpContentForm.$invalid) {
            $scope.showError = true;
            $scope.errorMessage = "The form you submitted is invalid. Please revise and try again.";
            return;
        }

        var createSuccessCallback = function (data) {
            $scope.$parent.message = 'New Help Content created successfully';
            $location.path('');
            $scope.product = {};
        };

        var errorCallback = function (data) {
            $scope.showError = true;
            $scope.errorMessage = messageService.get(data.data.error);
        };

        $scope.error = "";
        CreateHelpContent.save($scope.helpContent, createSuccessCallback, errorCallback);
    };
}
