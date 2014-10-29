/**
 * Created by seifu on 10/19/2014.
 */


function ContentCreateController($scope, $location, $route, messageService, CreateHelpTopic) {

    $scope.disabled = false;
    $scope.htmlContent = $scope.htmlcontent;
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
        var helpTopic =new function () {
            this.parentHelpTopic = "";
            this.name = "";
            this.htmlContent = "";

        };
        helpTopic.parentHelpTopic = parentId;
        ////alert('here intializing help topic and parent id is ' + parentId);

        //////alert('here intializing help topic');
        $scope.title = "Help Topic Information";
        $scope.helpContent = helpTopic;
        ////alert('here intializing help topic and parent id is ' + parentId);
//            $scope.helpTopic.roleList=data.helpTopic.roleList;


    };
    $scope.intializeHelpContent($route.current.params.id);
};


