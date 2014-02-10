/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 2/10/14
 * Time: 1:18 AM
 * To change this template use File | Settings | File Templates.
 */


app.directive('aFloat', function() {
    function link(scope, element, attrs){
        var data = scope.afData;
        var options = scope.afOption;

        var totalWidth = element.width(), totalHeight = element.height();

        if (totalHeight === 0 || totalWidth === 0) {
            throw new Error('Please set height and width for the aFloat element'+'width is '+ele);
        }

        function init(o,d){
            $.plot(element, o , d);
        }

        scope.$watch('afOption', function (o){
          init(o,data);
        });
        scope.$watch('afData', function (d){
           init(d,options);
        });

    }
    return {
        restrict: 'EA',
        template: '<div></div>',
        link: link,
        replace:true,
        transclude: false,
        scope: {
            afOption: '=',
            afData: '='
        }
    };
});