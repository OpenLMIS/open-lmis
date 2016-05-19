services.factory('DateFormatService', function ($filter, messageService) {

    var formatDateWithFirstDayOfMonth = function (date) {
        return $filter('date')(new Date(date.getFullYear(), date.getMonth(), 1), "yyyy-MM-dd");
    };

    var formatDateWithLastDayOfMonth = function (date) {
        return $filter('date')(new Date(date.getFullYear(), date.getMonth() + 1, 0), "yyyy-MM-dd");
    };

    var formatDateWithLocale = function (dateString) {
        var date = new Date(dateString);

        //dd MMM yyyy
        var month = 'month.abbr.' + (date.getMonth() + 1);
        return date.getDate() + " " + messageService.get(month) + " " + date.getFullYear();
    };

    return {
        formatDateWithFirstDayOfMonth: formatDateWithFirstDayOfMonth,
        formatDateWithLastDayOfMonth: formatDateWithLastDayOfMonth,
        formatDateWithLocale: formatDateWithLocale
    };
});