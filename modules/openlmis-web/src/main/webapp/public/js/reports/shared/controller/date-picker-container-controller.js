function DatePickerContainerController($scope, $filter, DateFormatService, messageService) {
    var currentDate = new Date();
    var todayDateString = $filter('date')(new Date(), "yyyy-MM-dd");

    $scope.dateRange = {};

    var defaultPeriodStartDate = currentDate.getDate() < 21 ?
        new Date(currentDate.getFullYear(), currentDate.getMonth() - 1, 21) :
        new Date(currentDate.getFullYear(), currentDate.getMonth(), 21);

    $scope.dateRange.startTime = $scope.pickerType === "period" ?
        DateFormatService.formatDateWithStartDayOfPeriod(defaultPeriodStartDate) :
        DateFormatService.formatDateWithFirstDayOfMonth(new Date());

    var startTime = new Date($scope.dateRange.startTime);

    $scope.dateRange.endTime = $scope.pickerType === "period" ?
        DateFormatService.formatDateWithEndDayOfPeriod(new Date(startTime.getFullYear(), startTime.getMonth()+1)) :
        todayDateString;

    $scope.timeTagSelected = "month";
    $scope.periodTagSelected = "period";

    var timeOptions = {
        "month": new Date(),
        "3month": new Date().setMonth(currentDate.getMonth() - 2),
        "year": new Date().setMonth(currentDate.getMonth() - 11)
    };

    var periodOptions = {
        "period": defaultPeriodStartDate,
        "3periods": new Date().setMonth(defaultPeriodStartDate.getMonth() - 2),
        "year": new Date().setMonth(defaultPeriodStartDate.getMonth() - 11)
    };

    $scope.timeTags = Object.keys(timeOptions);
    $scope.periodTags = Object.keys(periodOptions);
    $scope.showDateRangeInvalidWarning = false;

    function baseTimePickerOptions() {
        return {
            dateFormat: 'dd/mm/yy',
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

    var monthNamesForStartPeriod = [];
    var monthNamesForEndPeriod = [];
    var monthNumbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];

    monthNumbers.forEach(function (month) {
        var monthNamesString = messageService.get("month.abbr." + month);

        monthNamesForStartPeriod.push("21 " + monthNamesString);
        monthNamesForEndPeriod.push("20 " + monthNamesString);
    });


    $scope.periodStartOptions = angular.extend(baseTimePickerOptions(), {
        maxDate: defaultPeriodStartDate,
        showMonthAfterYear: false,
        monthNamesShort: monthNamesForStartPeriod,
        onClose: function () {
            notHideCalendar();
            $scope.timeTagSelected = "";
            var selectedYear = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
            var selectedMonth = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
            $scope.$apply(function () {
                $scope.dateRange.startTime = selectedMonth === null ?
                    DateFormatService.formatDateWithStartDayOfPeriod(currentDate) :
                    DateFormatService.formatDateWithStartDayOfPeriod(new Date(selectedYear, selectedMonth));
            });
        }
    });

    $scope.periodEndOptions = angular.extend(baseTimePickerOptions(), {
        showMonthAfterYear: false,
        monthNamesShort: monthNamesForEndPeriod,
        onClose: function () {
            notHideCalendar();
            $scope.timeTagSelected = "";
            var selectedYear = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
            var selectedMonth = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
            $scope.$apply(function () {
                $scope.dateRange.endTime = selectedMonth === null ?
                    DateFormatService.formatDateWithEndDayOfPeriod(defaultStartTime.getFullYear, defaultStartTime.getMonth()+1) :
                    DateFormatService.formatDateWithEndDayOfPeriod(new Date(selectedYear, selectedMonth));
            });
        }
    });

    $scope.datePickerStartOptions = angular.extend(baseTimePickerOptions(), {
        maxDate: currentDate,
        onClose: function () {
            notHideCalendar();
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
            notHideCalendar();
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

    $scope.changePeriodOption = function (periodTag) {
        $scope.periodTagSelected = periodTag;
        $scope.dateRange.startTime = DateFormatService.formatDateWithStartDayOfPeriod(new Date(periodOptions[periodTag]));
        $scope.dateRange.endTime = DateFormatService.formatDateWithEndDayOfPeriod(new Date(startTime.getFullYear(), startTime.getMonth()+1));
    };

    $scope.$on("update-date-pickers", function (event, range) {
        $scope.dateRange = range;
    });

    $scope.$watch('dateRange.startTime', function () {
        $scope.getTimeRange({
            'dateRange': $scope.dateRange
        }, true);
    });

    $scope.$watch('dateRange.endTime', function () {
        $scope.checkCompletenessOfEndTime();

        $scope.getTimeRange({
            'dateRange': $scope.dateRange
        }, true);
    });

    $scope.checkCompletenessOfEndTime = function () {
        $scope.showIncompleteWarning = $scope.dateRange.endTime != DateFormatService.formatDateWithLastDayOfMonth(new Date($scope.dateRange.endTime));
    };

    var notHideCalendar = function () {
        $("#ui-datepicker-div").removeClass("hide-calendar");
        $("#ui-datepicker-div").removeClass('MonthDatePicker');
        $("#ui-datepicker-div").removeClass('HideTodayButton');
    };
}