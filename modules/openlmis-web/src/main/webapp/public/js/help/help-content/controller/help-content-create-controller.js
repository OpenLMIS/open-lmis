/**
 * Created by seifu on 10/19/2014.
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
};

