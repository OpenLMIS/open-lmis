
function HelpFileContentController($scope, $route, $location, $dialog, messageService, SettingsByKey, HelpTopicDetail) {
    ////alert('to view help content');

    $scope.startHelpContentView = function (helpContentId) {
        ////alert('loading help content for view');
        $scope.$parent.editProductMode = true;
        $scope.title = 'Help Content';
        $scope.AddEditMode = true;


        // now get a fresh copy of the product object from the server
        HelpTopicDetail.get({id: helpContentId}, function (data) {
            $scope.helpContentView = data.helpTopic;
//            if($scope.editHelpTopic.active === false){
//                $scope.disableAllFields();
//            }
        });
    };

     $scope.startHelpContentView($route.current.params.id);
}

