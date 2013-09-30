function ListSettingController($scope, $routeParams, $location, $dialog, Settings, SettingUpdator, messageService) {

       Settings.get(function (data){
           $scope.settings = data.settings;
       });

    $scope.saveSettings = function(){
        SettingUpdator.post({}, $scope.settings, function (data){
            $location.path('');
            $scope.$parent.message = "The configuration changes were successfully updated.";
        });
    }
}
