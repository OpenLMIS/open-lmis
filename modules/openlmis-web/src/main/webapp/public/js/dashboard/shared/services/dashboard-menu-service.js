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
services.factory('dashboardMenuService',function($rootScope,$location){
    var dashboardMenuService = {};

    dashboardMenuService.tabs = [{header: 'menu.header.dashboard.summary', content:'/public/pages/dashboard/index.html', name:'SUMMARY', closable:false, displayOrder: 0},
        {header: 'menu.header.dashboard.stock.efficiency', content:'/public/pages/dashboard/index.html#/stock', name:'STOCK', closable:false, displayOrder: 1},
        {header: 'menu.header.dashboard.order.turn.around', content:'/public/pages/dashboard/index.html#/leadTime', name:'ORDER', closable: false, displayOrder: 2},
        {header: 'menu.header.dashboard.stocked.out', content:'/public/pages/dashboard/index.html#/stock-out', name:'STOCK-OUT', closable: false, displayOrder: 3},
        {header: 'menu.header.dashboard.notification', content:'/public/pages/dashboard/index.html#/notifications', name:'NOTIFICATION', closable: false, displayOrder: 4},
        {header: 'menu.header.dashboard.rnr.status.summary', content: '/public/pages/dashboard/index.html#/rnr-status-summary',name: 'RNR-STATUS-SUMMARY',closeable:false,displayOrder:5}];


    dashboardMenuService.addTab = function(header, content, name, closable, displayOrder){
        var tab = isTabExists(name);
        var newTab = {header:header, content: content, name:name, closable:closable, displayOrder: displayOrder};

        if(_.isEqual(tab, newTab)){
            return;
        }
        if(_.isUndefined(tab)){
            dashboardMenuService.tabs.push(newTab);
        }else if(tab.name === newTab.name){ //replace
            dashboardMenuService.tabs[_.indexOf(dashboardMenuService.tabs,tab)] = newTab;
        }

        broadcastUpdate();
    };

    dashboardMenuService.closeTab = function(tabName){
        var tab = isTabExists(tabName);

        var closedTabIndex = _.indexOf(dashboardMenuService.tabs,tab);

        dashboardMenuService.tabs = _.reject(dashboardMenuService.tabs, function(tab){return tab.name === tabName && tab.closable === true;});

        var tabToShow = '';

        if(!isUndefined(tab)){

            var visibleTab;
            var nextTab = _.findWhere(dashboardMenuService.tabs,{displayOrder: tab.displayOrder + 1});

            if(nextTab !== undefined){
                visibleTab = nextTab.content;

            }else if(closedTabIndex > 0){
                visibleTab = dashboardMenuService.tabs[closedTabIndex-1].content;
            }

            tabToShow += visibleTab.slice(visibleTab.indexOf('#')+1);
        }

        broadcastUpdate();
        $location.path(tabToShow);
    };

    var broadcastUpdate = function(){
        $rootScope.$broadcast('dashboardTabUpdated');
    };

    var isTabExists = function(tabName){
        var tab = _.findWhere(dashboardMenuService.tabs, {name:tabName});
        if(_.isUndefined(tab)){
            return undefined;
        }
        return tab;
    };

    return dashboardMenuService;

});
