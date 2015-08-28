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
function ContentCreateController($scope, $location, $route, messageService, CreateHelpTopic, HelpDocumentList) {
    $scope.cancelContentCreate = function () {


        $location.path('treeView');

    };
    $scope.disabled = false;
    $scope.htmlContent = $scope.htmlcontent;
    ////
//    Masquerade perfers the scope value over the innerHTML
//    Uncomment this line to see the effect:
    $scope.htmlcontenttwo = "Override originalContents";
    HelpDocumentList.get({}, function (data) {

        $scope.helpDocumentList = data.helpDocumentList;


    }, function (data) {


        $location.path($scope.$parent.sourceUrl);


    });
    $scope.createHelpContent = function () {

        $scope.error = "";
        if ($scope.createHelpContentForm.$invalid) {
            $scope.showError = true;
            $scope.errorMessage = "The form you submitted is invalid. Please revise and try again.";
            return;
        }

        var createSuccessCallback = function (data) {
            $scope.$parent.message = 'New Help Content created successfully';
            $location.path('/treeView');
            $scope.helpContent = {};
        };

        var errorCallback = function (data) {
            $scope.showError = true;
            $scope.errorMessage = messageService.get(data.data.error);
        };
        $scope.helpContent.category = "false";
        $scope.error = "";
        CreateHelpTopic.save($scope.helpContent, createSuccessCallback, errorCallback);
    };
    $scope.intializeHelpContent = function (parentId) {
//        var helpTopic =new function () {
//            this.parentHelpTopic = "";
//            this.name = "";
//            this.htmlContent = "";
//
//        }
        var helpTopic = {};
        helpTopic.parentHelpTopic = parentId;
        $scope.title = "Help Topic Information";
        $scope.helpContent = helpTopic;



    };

    $scope.intializeHelpContent($route.current.params.id);

}
