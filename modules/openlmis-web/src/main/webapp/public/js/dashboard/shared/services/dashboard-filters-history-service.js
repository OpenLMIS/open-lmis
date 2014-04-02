/**
 * Created by issa on 4/2/14.
 */
services.factory('dashboardFiltersHistoryService',function(localStorageService){
    var dashboardFiltersHistoryService = {};
    dashboardFiltersHistoryService.get = function(pageCategory){

        var value = localStorageService.get(localStorageKeys.DASHBOARD_FILTERS[pageCategory]);

        var parsedValue;

        try{
            parsedValue = JSON.parse(value);
        }catch (e){
            parsedValue = undefined;
        }

        return parsedValue;
    };

    dashboardFiltersHistoryService.add = function(pageCategory, filterHistory){
        if(typeof filterHistory === 'object' && !Array.isArray(filterHistory)){

            localStorageService.add(localStorageKeys.DASHBOARD_FILTERS[pageCategory],JSON.stringify(filterHistory));
        }else{

            localStorageService.add(localStorageKeys.DASHBOARD_FILTERS[pageCategory],filterHistory);
        }

    };

    return dashboardFiltersHistoryService;

});