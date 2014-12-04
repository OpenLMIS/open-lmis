/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 2/10/14
 * Time: 1:18 AM
 */


app.directive('aFloat', function() {
    function link(scope, element, attrs){
        scope.$watch('afData', function(){
            init(scope.afData,scope.afOption);
        });
        scope.$watch('afOption', function(){
            init(scope.afData,scope.afOption);
        });

        scope.$watch('afRender', function(){
            if(scope.afRender){

               // alert('render chart is '+scope.afRender);
                $.plot(element,scope.afData,scope.afOption);
               // init(scope.afData,scope.afOption);
            }

        },true);


        function init(o,d){
            var totalWidth = element.width(), totalHeight = element.height();

            if (totalHeight === 0 || totalWidth === 0) {
                throw new Error('Please set height and width for the aFloat element'+'width is '+element);
            }

            if(element.is(":visible") && !isUndefined(d) && !isUndefined(o)){
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
            afData: '=',
            afRender:'='
        }
    };
});