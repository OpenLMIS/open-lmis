

/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 * + *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
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
