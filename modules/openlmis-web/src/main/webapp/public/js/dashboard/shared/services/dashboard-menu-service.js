/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 3/30/14
 * Time: 1:23 AM
 * To change this template use File | Settings | File Templates.
 */
services.factory('dashboardMenuService',function($rootScope,$location){
    var dashboardMenuService = {};

    dashboardMenuService.tabs = [{header: 'menu.header.dashboard.summary', content:'/public/pages/dashboard/index.html', name:'SUMMARY', closable:false},
        {header: 'menu.header.dashboard.stock.efficiency', content:'/public/pages/dashboard/index.html#/stock', name:'STOCK', closable:false},
        {header: 'menu.header.dashboard.order.turn.around', content:'/public/pages/dashboard/index.html#/leadTime', name:'ORDER', closable: false},
        {header: 'menu.header.dashboard.stocked.out', content:'/public/pages/dashboard/index.html#/stock-out', name:'STOCK-OUT', closable: false}];

    dashboardMenuService.addTab = function(header, content, name, closable){
        var tab = _.findWhere(dashboardMenuService.tabs, {name:name});
        var newTab = {header:header, content: content, name:name, closable:closable};

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
        var tabIndex = isTabExists(tabName);
        dashboardMenuService.tabs = _.reject(dashboardMenuService.tabs, function(tab){return tab.name == tabName && tab.closable == true;});
        var tabToShow = '';
        if(tabIndex > 0){
            var previousTab = dashboardMenuService.tabs[tabIndex-1].content;
            tabToShow += previousTab.slice(previousTab.indexOf('#')+1);
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
        return _.indexOf(dashboardMenuService.tabs,tab);
    };

    return dashboardMenuService;

});