function ListSettingController($scope, $routeParams, $location, $dialog, Settings , messageService) {

       Settings.get(function (data){
           $scope.settings = data.settings;
       });

    $scope.saveSettings = function(){
        alert("I have saved it");
    }
}
