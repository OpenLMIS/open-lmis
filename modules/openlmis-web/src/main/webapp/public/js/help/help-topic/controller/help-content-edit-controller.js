/**
 * Created by seifu on 10/26/2014.
 */

function HelpContentEditCotntroller($scope, $route, $location, $dialog, messageService, SettingsByKey, HelpTopicDetail, HelpTopicList,UpdateHelpTopic) {


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
}
