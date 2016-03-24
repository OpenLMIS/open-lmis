function DatePickerContainerController($scope, $filter, DateFormatService) {
    var currentDate = new Date();
    var todayDateString = $filter('date')(new Date(), "yyyy-MM-dd");

    $scope.dateRange = {};
    $scope.dateRange.startTime = DateFormatService.formatDateWithFirstDayOfMonth(new Date());
    $scope.dateRange.endTime = todayDateString;
    $scope.timeTagSelected = "month";

    var timeOptions = {
        "month": new Date(),
        "3month": new Date().setMonth(currentDate.getMonth() - 2),
        "year": new Date().setMonth(currentDate.getMonth() - 11)
    };
    $scope.timeTags = Object.keys(timeOptions);
    $scope.showDateRangeInvalidWarning = false;

    function baseTimePickerOptions() {
        return {
            dateFormat: 'yy-mm-dd',
            changeYear: true,
            changeMonth: true,
            showMonthAfterYear: true,
            beforeShow: function (e, t) {
                $("#ui-datepicker-div").addClass("hide-calendar");
                $("#ui-datepicker-div").addClass('MonthDatePicker');
                $("#ui-datepicker-div").addClass('HideTodayButton');
            }
        };
    }

    $scope.datePickerStartOptions = angular.extend(baseTimePickerOptions(), {
        maxDate: currentDate,
        onClose: function () {
            $scope.timeTagSelected = "";
            var selectedYear = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
            var selectedMonth = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
            $scope.$apply(function () {
                $scope.dateRange.startTime = selectedMonth === null ? DateFormatService.formatDateWithFirstDayOfMonth(currentDate) : $filter('date')(new Date(selectedYear, selectedMonth, 01), "yyyy-MM-dd");
            });
        }
    });

    $scope.datePickerEndOptions = angular.extend(baseTimePickerOptions(), {
        maxDate: DateFormatService.formatDateWithLastDayOfMonth(currentDate),
        onClose: function () {
            $scope.timeTagSelected = "";
            var selectedYear = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
            var selectedMonth = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
            $scope.$apply(function () {
                $scope.dateRange.endTime = selectedMonth === null ? todayDateString : DateFormatService.formatDateWithLastDayOfMonth(new Date(selectedYear, selectedMonth));
            });
        }
    });

    $scope.changeTimeOption = function (timeTag) {
        $scope.timeTagSelected = timeTag;
        $scope.dateRange.startTime = DateFormatService.formatDateWithFirstDayOfMonth(new Date(timeOptions[timeTag]));
        $scope.dateRange.endTime = todayDateString;
    };

    //function formatDateWithFirstDayOfMonth(date) {
    //    return $filter('date')(new Date(date.getFullYear(), date.getMonth(), 1), "yyyy-MM-dd");
    //}
    //
    //function formatDateWithLastDayOfMonth(date) {
    //    return $filter('date')(new Date(date.getFullYear(), date.getMonth() + 1, 0), "yyyy-MM-dd");
    //}

    $scope.$watch('dateRange.startTime', function(){
        $scope.getTimeRange({
            'dateRange': $scope.dateRange
        },true);
    });

    $scope.$watch('dateRange.endTime', function(){
        $scope.getTimeRange({
            'dateRange': $scope.dateRange
        },true);
    });
}