function DatePickerContainerController($scope, $filter, DateFormatService, messageService) {
    var currentDate = new Date();
    var todayDateString = $filter('date')(new Date(), "yyyy-MM-dd");

    $scope.dateRange = {};

    $scope.timeTagSelected = "month";
    $scope.periodTagSelected = $scope.pickerType === "period-no-current" ? "3periods" : "period";

    var defaultPeriodStartDate = currentDate.getDate() < 21 ?
        new Date(currentDate.getFullYear(), currentDate.getMonth() - 1, 21) :
        new Date(currentDate.getFullYear(), currentDate.getMonth(), 21);

    var threeMonthsAgo = new Date().setMonth(new Date().getMonth() - 2);
    var aYearAgo = new Date().setMonth(new Date().getMonth() - 11);

    var threePeriodsBeforeExcludingCurrent = new Date().setMonth(defaultPeriodStartDate.getMonth() - 3);
    if (defaultPeriodStartDate.getMonth() - 3 > currentDate.getMonth()) {
        threePeriodsBeforeExcludingCurrent = new Date(threePeriodsBeforeExcludingCurrent).setYear(currentDate.getFullYear() - 1);
    }

    var threePeriodsBeforeIncludingCurrent = new Date().setMonth(defaultPeriodStartDate.getMonth() - 2);
    if (defaultPeriodStartDate.getMonth() - 2 > currentDate.getMonth()) {
        threePeriodsBeforeIncludingCurrent = new Date(threePeriodsBeforeIncludingCurrent).setYear(currentDate.getFullYear() - 1);
    }

    var yearBeforeExcludingCurrent = new Date().setYear(defaultPeriodStartDate.getFullYear() - 1);
    yearBeforeExcludingCurrent = new Date(yearBeforeExcludingCurrent).setMonth(defaultPeriodStartDate.getMonth());

    var yearBeforeIncludingCurrent = new Date().setMonth(defaultPeriodStartDate.getMonth() - 11);
    if (defaultPeriodStartDate.getMonth() - 11 > currentDate.getMonth()) {
        yearBeforeIncludingCurrent = new Date(yearBeforeIncludingCurrent).setYear(currentDate.getFullYear() - 2);
    } else {
        yearBeforeIncludingCurrent = new Date(yearBeforeIncludingCurrent).setYear(currentDate.getFullYear() - 1);
    }

    if ($scope.pickerType === "period") {
        $scope.dateRange.startTime = DateFormatService.formatDateWithStartDayOfPeriod(defaultPeriodStartDate);
        $scope.dateRange.endTime = DateFormatService.formatDateWithEndDayOfPeriod(new Date(new Date(defaultPeriodStartDate.getFullYear(), defaultPeriodStartDate.getMonth() + 1)));
    } else if ($scope.pickerType === "period-no-current") {
        $scope.dateRange.startTime = DateFormatService.formatDateWithStartDayOfPeriod(new Date(threePeriodsBeforeExcludingCurrent));
        $scope.dateRange.endTime = DateFormatService.formatDateWithEndDayOfPeriod(defaultPeriodStartDate);
    } else {
        $scope.dateRange.startTime = DateFormatService.formatDateWithFirstDayOfMonth(new Date());
        $scope.dateRange.endTime = todayDateString;
    }

    var defaultEndTime = new Date($scope.dateRange.endTime);

    var timeOptions = {
        "month": new Date(),
        "3month": threeMonthsAgo,
        "year": aYearAgo
    };

    var periodOptions = $scope.pickerType === "period-no-current" ?
    {
        "3periods": threePeriodsBeforeExcludingCurrent,
        "year": yearBeforeExcludingCurrent
    } :
    {
        "period": defaultPeriodStartDate,
        "3periods": threePeriodsBeforeIncludingCurrent,
        "year": yearBeforeIncludingCurrent
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

    $scope.periodNoCurrentStartOptions = angular.extend(baseTimePickerOptions(), {
        maxDate: new Date(defaultEndTime.getFullYear(),defaultEndTime.getMonth()-1,21),
        showMonthAfterYear: false,
        monthNamesShort: monthNamesForStartPeriod,
        onClose: function () {
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
            $scope.timeTagSelected = "";
            var selectedYear = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
            var selectedMonth = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
            $scope.$apply(function () {
                $scope.dateRange.endTime = selectedMonth === null ?
                    DateFormatService.formatDateWithEndDayOfPeriod(defaultStartTime.getFullYear, defaultStartTime.getMonth() + 1) :
                    DateFormatService.formatDateWithEndDayOfPeriod(new Date(selectedYear, selectedMonth));
            });
        }
    });

    $scope.periodNoCurrentEndOptions = angular.extend($scope.periodEndOptions, {
        maxDate: defaultEndTime
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
        if ($scope.pickerType === "period-no-current") {
            $scope.dateRange.startTime = DateFormatService.formatDateWithStartDayOfPeriod(new Date(periodOptions[periodTag]));
            $scope.dateRange.endTime = DateFormatService.formatDateWithEndDayOfPeriod(defaultEndTime);
        } else {
            $scope.dateRange.startTime = DateFormatService.formatDateWithStartDayOfPeriod(new Date(periodOptions[periodTag]));
            $scope.dateRange.endTime = DateFormatService.formatDateWithEndDayOfPeriod(defaultEndTime);
        }
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