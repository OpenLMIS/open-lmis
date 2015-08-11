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
function HelpContentEditCotntroller($scope, $route, $location, $dialog, messageService, SettingsByKey, HelpTopicDetail, HelpTopicList,UpdateHelpTopic,HelpDocumentList) {


    $scope.startHelpContentEdit = function (id) {


        $scope.$parent.editProductMode = true;
        $scope.title='Edit Help Content';
        $scope.AddEditMode = true;


        // now get a fresh copy of the product object from the server
        HelpTopicDetail.get({id:id}, function(data){
            $scope.editHelpContent = data.helpTopic;
//            if($scope.editHelpTopic.active === false){
//                $scope.disableAllFields();
//            }
        });


    };
    HelpDocumentList.get({}, function (data) {

        $scope.helpDocumentList = data.helpDocumentList;



    }, function (data) {


        $location.path($scope.$parent.sourceUrl);


    });
    $scope.updateHelpContent = function () {
        ////alert('loading help topic for edit');
        $scope.error = "";
        if ($scope.updateHelpContentForm.$invalid) {
            $scope.showError = true;
            $scope.errorMessage = "The form you submitted is invalid. Please revise and try again.";
            return;
        }

        var updateSuccessCallback = function (data) {
            $scope.$parent.message = 'update help Content done successfully';
            $location.path('/treeView');
            $scope.editHelpContent = {};
        };

        var errorCallback = function (data) {
            $scope.showError = true;
            $scope.errorMessage = messageService.get(data.data.error);
        };
        ////alert('here i am ');
        $scope.error = "";
        UpdateHelpTopic.save($scope.editHelpContent, updateSuccessCallback, errorCallback);
    };


    $scope.startHelpContentEdit($route.current.params.id);
    $scope.cancelEditContent=function(){
        $location.path('/treeView');
    };
}
