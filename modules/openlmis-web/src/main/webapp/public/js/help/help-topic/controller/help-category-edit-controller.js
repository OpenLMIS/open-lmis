/**
 * Created by seifu on 10/19/2014.
 */
function HelpCategoryEditController($scope, $route, $location, $dialog, messageService, SettingsByKey, HelpTopicDetail, HelpTopicList,UpdateHelpTopic) {
    //////alert('to edit');
    $scope.startHelpTopicEdit = function (id) {
        //////alert('loading help topic for edit');
        $scope.$parent.editProductMode = true;
        $scope.title='Edit Help Topic';
        $scope.AddEditMode = true;


        // now get a fresh copy of the product object from the server
        HelpTopicDetail.get({id:id}, function(data){
            $scope.editHelpTopic = data.helpTopic;
//            if($scope.editHelpTopic.active === false){
//                $scope.disableAllFields();
//            }
        });


    };
    $scope.updateHelpTopic = function () {

        $scope.error = "";
        if ($scope.editHelpCategoryForm.$invalid) {
            $scope.showError = true;
            $scope.errorMessage = "The form you submitted is invalid. Please revise and try again.";
            return;
        }

        var updateSuccessCallback = function (data) {
            $scope.$parent.message = 'update help topic done successfully';
            $location.path('/treeView');
            $scope.product = {};
        };

        var errorCallback = function (data) {
            $scope.showError = true;
            $scope.errorMessage = messageService.get(data.data.error);
        };
////alert('here i am ');
        $scope.error = "";
        UpdateHelpTopic.save($scope.editHelpTopic, updateSuccessCallback, errorCallback);
    };


$scope.startHelpTopicEdit($route.current.params.id);
}