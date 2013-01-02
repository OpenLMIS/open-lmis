function ScheduleController($scope, AllSchedules, Schedule) {
    AllSchedules.get({}, function (data) {
        $scope.schedules = data.schedules;
    }, function () {
        $location.path($scope.$parent.sourceUrl);
    });


    $scope.saveSchedule = function(scheduleRow){
        function successHandler(data){
            $scope.message = "Schedule Saved Successfully";
            $scope.error = "";
        }

        function errorHandler(){
            $scope.message = "";
            $scope.error = "Save failed";
        }

        $scope.message = "";
        Schedule.save({}, scheduleRow, successHandler, errorHandler);
    }
}