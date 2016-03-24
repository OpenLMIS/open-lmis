function datePickerContainer(){
    return {
        restrict: 'E',
        controller: 'DatePickerContainerController',
        scope: {
            getTimeRange: '&'
        },
        templateUrl:'date-picker-container.html'
    };
}