services.factory('DateFormatService', function ($filter, messageService) {

    var formatDateWithFirstDayOfMonth = function (date) {
        return $filter('date')(new Date(date.getFullYear(), date.getMonth(), 1), "yyyy-MM-dd");
    };

    var formatDateWithLastDayOfMonth = function (date) {
        return $filter('date')(new Date(date.getFullYear(), date.getMonth() + 1, 0), "yyyy-MM-dd");
    };

    var formatDateWithStartDayOfPeriod = function (date) {
        return $filter('date')(new Date(date.getFullYear(), date.getMonth(), 21), "yyyy-MM-dd");
    };

    var formatDateWithEndDayOfPeriod = function (date) {
        return $filter('date')(new Date(date.getFullYear(), date.getMonth(), 20), "yyyy-MM-dd");
    };

    var formatDateWithLocale = function (dateString) {
        return formatDate(dateString, true);
    };

    var formatDateWithLocaleNoDay = function (dateString) {
        return formatDate(dateString, false);
    };

    function formatDate(dateString, withDay) {
        var date = new Date(dateString);
        var monthYear = messageService.get('month.abbr.' + (date.getMonth() + 1)) + " " + date.getFullYear();

        if (withDay) {
            return date.getDate() + " " + monthYear;
        } else {
            return monthYear;
        }
    }

    return {
        formatDateWithFirstDayOfMonth: formatDateWithFirstDayOfMonth,
        formatDateWithLastDayOfMonth: formatDateWithLastDayOfMonth,
        formatDateWithLocale: formatDateWithLocale,
        formatDateWithLocaleNoDay: formatDateWithLocaleNoDay,
        formatDateWithStartDayOfPeriod: formatDateWithStartDayOfPeriod,
        formatDateWithEndDayOfPeriod: formatDateWithEndDayOfPeriod
    };
});