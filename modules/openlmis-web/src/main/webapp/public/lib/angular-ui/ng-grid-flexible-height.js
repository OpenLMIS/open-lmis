ngGridFlexibleHeightPlugin = function() {
    var self = this;
    self.grid = null;
    self.scope = null;
    self.init = function(scope, grid, services) {
        self.grid = grid;
        self.scope = scope;
        self.services = services;
        var recalcHeightForData = function () { setTimeout(innerRecalcForData, 1); } ;
        var innerRecalcForData = function () {
           // self.grid.$canvas.css('margin-bottom', '30px');
           //

            // self.grid.$viewport.css('height', (self.grid.$viewport.height() + 30) + 'px');
            //self.grid.$canvas

            self.grid.$footerPanel.css('position', 'fixed');
            self.grid.$footerPanel.css('bottom', '0px');
                // self.grid.$footerPanel.css('left', '0px');
                // self.grid.$footerPanel.css('right', '0px');
                // self.grid.$footerPanel.css('width', '100%');
            self.grid.$footerPanel.css('z-index', '10');
            scope.$apply();
            self.grid.$viewport.css('height', (self.grid.$canvas.height()) + 'px');
            self.services.DomUtilityService.RebuildGrid(self.scope, self.grid);
            scope.$apply();
        };
        scope.$watch (grid.config.data, recalcHeightForData);

        scope.$watch('self.grid.$viewport.height()', function () {
            if(self.grid.$viewport.height() <= self.grid.$canvas.height() ){
                self.grid.$viewport.css('height', (self.grid.$canvas.height() + 2) + 'px');
            }
        });
        scope.$watch (grid.config.data, recalcHeightForData);
    };

};