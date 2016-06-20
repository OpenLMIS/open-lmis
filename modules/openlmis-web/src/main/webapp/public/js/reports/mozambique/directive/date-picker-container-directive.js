function datePickerContainer(){
    return {
        restrict: 'E',
        controller: 'DatePickerContainerController',
        scope: {
            getTimeRange: '&',
            pickerType: '@'
        },
        templateUrl:'date-picker-container.html'
    };
}