/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
            return selectedItem;
        }
    });
    return selectedItem;
};
var getSelectedItemNames = function(ids,itemList){
    var selectedItems = [];
    var item = null;
    angular.forEach(ids, function(id){
        item =  getSelectedItemName(id,itemList);
        if(item !== "None"){
            selectedItems.push(item);
        }
    });
    if(selectedItems.length === 0) selectedItems.push("None");
    return selectedItems;
};

var getSelectedZoneName = function(id,zoneTreeList, zoneFlatList){
    var selectedItem = "None";
    var zoneRootId = null;
    if(!isUndefined(zoneTreeList)){
        zoneRootId = zoneTreeList.id;
    }
    angular.forEach(zoneFlatList, function(item, idx){
        if(item.id == id){
            if(item.id == zoneRootId){
                selectedItem = "--National--";
            }else{
                selectedItem = item.name;
            }
        }
    });

    return selectedItem;

};

var isItemWithIdExists = function(id, listObject){
    var isEq = false;
    angular.forEach(listObject,function(item,idx){
        if(!isUndefined(item)){
            if(item.id == id){
                isEq = true;
            }
        }
    });
    return isEq;
};
