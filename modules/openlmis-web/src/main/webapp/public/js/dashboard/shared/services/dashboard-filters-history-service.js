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

var getSelectedItemName = function(id,itemList){
    var selectedItem = "None";
    angular.forEach(itemList, function(item, idx){
        if(item.id == id){
            selectedItem = item.name;
        }
    });

    return selectedItem;

};
var isItemWithIdExists = function(id, listObject){
    var isEq = false;
    angular.forEach(listObject,function(item,idx){
        if(!isUndefined(item)){
            isEq = item.id == id;
        }
    });
    return isEq;
};

