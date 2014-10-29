/**
 * Created by seifu on 10/19/2014.
 */


function HelpTopicCreateController($scope,$route, $location,messageService,CreateHelpTopic,IntializeHelpTopic) {
//    $scope.intializeHelpTopic();
    $scope.createHelpTopic = function () {
        //alert('here ii am');
        $scope.error = "";
        if ($scope.createHelpCategoryForm.$invalid) {
            $scope.showError = true;
            $scope.errorMessage = "The form you submitted is invalid. Please revise and try again.";
            return;
        }

        var createSuccessCallback = function (data) {
            $scope.$parent.message = 'New Help Topic created successfully';
            $location.path('/treeView');
            $scope.product = {};
        };

        var errorCallback = function (data) {
            $scope.showError = true;
            $scope.errorMessage = messageService.get(data.data.error);
        };

        $scope.error = "";
        $scope.helpTopic.category="true";
        CreateHelpTopic.save($scope.helpTopic, createSuccessCallback, errorCallback);
    };
    $scope.intializeHelpTopic=function(parentId){
        //alert('here intializing help topic and parent id is '+parentId);
        IntializeHelpTopic.get({}, function(data){
            //////alert('here intializing help topic');
            $scope.title="Help Topic Information";
            $scope.helpTopic = data.helpTopic;
            $scope.helpTopic.parentHelpTopic=parentId;
            ////alert('here intializing help topic and parent id is '+parentId);
//            $scope.helpTopic.roleList=data.helpTopic.roleList;

        });
    };
    $scope.intializeHelpTopic($route.current.params.id);
};
