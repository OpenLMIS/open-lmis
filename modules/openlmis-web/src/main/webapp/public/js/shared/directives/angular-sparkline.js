/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 2/10/14
 * Time: 1:18 AM
 */

app.directive('sparkLine', function() {
    function link(scope, element, attrs){
        scope.$watch('afData', function(){
            init(scope.afData,scope.afOption);
        });
        scope.$watch('afOption', function(){
            init(scope.afData,scope.afOption);
        });



        function init(d,o){
            //alert('data = '+JSON.stringify(d))
            element.sparkline(d,o);


        }
    }
    return {
        restrict: 'EA',
        template: '<div></div>',
        link: link,
        replace:true,
        scope: {
            afOption: '=',
            afData: '=',
            afRender:'='
        }
    };
});
/*
app.directive('sparkLine', function() {
        return {
            restrict: 'EACM',
            template: '<div></div>',
            replace: true,
            link: function (scope, elem, attrs) {
                var renderChart = function () {

                    alert('data '+JSON.stringify(attrs.sparkLine))
                    var data = scope.$eval(attrs.sparkLine);
                    elem.html('');
                    if (!angular.isArray(data)) {
                        return;
                    }

                    var opts = {};
                    if (!angular.isUndefined(attrs.chartOptions)) {
                        opts = scope.$eval(attrs.chartOptions);
                        if (!angular.isObject(opts)) {
                            throw 'Invalid ui.chart options attribute';
                        }
                    }

                    elem.sparkline(data, opts);
                };

                scope.$watch(attrs.sparkLine, function () {
                    //alert('data chart '+JSON.stringify(attrs.sparkLine))
                    renderChart();
                }, true);

                scope.$watch(attrs.chartOptions, function () {
                    renderChart();
                });
            }
        };
    });*/
