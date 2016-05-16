services.factory('DateFormatService', function ($filter) {

    var formatDateWithFirstDayOfMonth = function (date) {
        return $filter('date')(new Date(date.getFullYear(), date.getMonth(), 1), "yyyy-MM-dd");
    };

    var formatDateWithLastDayOfMonth = function (date) {
        return $filter('date')(new Date(date.getFullYear(), date.getMonth() + 1, 0), "yyyy-MM-dd");
    };

    var capitalizeFirstLetter = function(string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    };

    var formatDateWithLocale = function (dateString) {
        var options = {month: 'short'};
        var date = new Date(dateString);
        
        //dd MMM yyyy
        return date.getDate() + " " + capitalizeFirstLetter(date.toLocaleDateString(locale, options)) + " " + date.getFullYear();
    };

    return {
        formatDateWithFirstDayOfMonth: formatDateWithFirstDayOfMonth,
        formatDateWithLastDayOfMonth: formatDateWithLastDayOfMonth,
        formatDateWithLocale: formatDateWithLocale
    };
});