/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 3/30/14
 * Time: 1:23 AM
 * To change this template use File | Settings | File Templates.
 */
services.factory('dashboardMenuServiceNew',function($rootScope,$location){
    var dashboardMenuService = {};
    dashboardMenuService.tabs = [];
  /*  dashboardMenuService.tabs = [{header: 'menu.header.dashboard.summary', content:'/public/pages/dashboard/index.html', name:'SUMMARY', closable:false, displayOrder: 0},
        {header: 'menu.header.dashboard.stock.efficiency', content:'/public/pages/dashboard/index.html#/stock', name:'STOCK', closable:false, displayOrder: 1},
        {header: 'menu.header.dashboard.order.turn.around', content:'/public/pages/dashboard/index.html#/leadTime', name:'ORDER', closable: false, displayOrder: 2},
        {header: 'menu.header.dashboard.stocked.out', content:'/public/pages/dashboard/index.html#/stock-out', name:'STOCK-OUT', closable: false, displayOrder: 3},
        {header: 'menu.header.dashboard.notification', content:'/public/pages/dashboard/index.html#/notifications', name:'NOTIFICATION', closable: false, displayOrder: 4},
        {header: 'menu.header.dashboard.rnr.status.summary', content: '/public/pages/dashboard/index.html#/rnr-status-summary',name: 'RNR-STATUS-SUMMARY',closeable:false,displayOrder:5}];

*/
    dashboardMenuService.addTab = function(header, content, name, closable, displayOrder){
        var tab = isTabExists(name);
        var newTab = {header:header, content: content, name:name, closable:closable, displayOrder: displayOrder, id: displayOrder};

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

    dashboardMenuService.getTab = function(position){
      return dashboardMenuService.tabs[position];
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