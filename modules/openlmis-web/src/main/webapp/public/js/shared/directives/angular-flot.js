/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 2/10/14
 * Time: 1:18 AM
 */


app.directive('aFloat', function() {
    function link(scope, element, attrs){
        scope.$watch('afData + afOption', function(){
            init(scope.afData,scope.afOption);
        });

        function init(o,d){

            var totalWidth = element.width(), totalHeight = element.height();

            if (totalHeight === 0 || totalWidth === 0) {
                throw new Error('Please set height and width for the aFloat element'+'width is '+ele);
            }

            if(element.is(":visible") && !isUndefined(d)){
                $.plot(element, o , d);
            }
        }
    }
    return {
        restrict: 'EA',
        template: '<div></div>',
        link: link,
        replace:true,
        scope: {
            afOption: '=',
            afData: '='
        }
    };
});