/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 2/10/14
 * Time: 1:18 AM
 */

app.directive('ngTableScroller', function() {
    return {
        restrict: 'A',
        replace: true,
        transclude: true,
        template:'<div><table style="width:100%" ng-transclude></table><div ng-show="showSpinner">loading</div></div>',
        link: function (scope, elem, attrs) {
            rawElement = elem[0];
            scope.showSpinner = false;

            scope.loadingFn = attrs.loadingMethod;

            scope.$watch(attrs.ngTable, function(newParams){

               scope.showSpinner = false;
            },true);

            elem.bind('scroll', function () {
                if((rawElement.scrollTop + rawElement.offsetHeight+5) >= rawElement.scrollHeight){

                    scope.$apply(scope.showSpinner = true);
                    scope.$apply(scope.loadingFn);

                }

                scope.$apply(scope.showSpinner = false);

            });
        }
    };
});
