app.directive('modal', function() {
  return {
    restrict: 'EA',
    link: function(scope, elm, attrs) {
      var shownExpr = attrs.modal || attrs.show;

      scope.$watch(shownExpr, function(isShown, oldShown) {
        setTimeout(function() {
          if (isShown) {
            var tabbables = elm.find(":tabbable");

            tabbables.first().focus();

            tabbables.last().bind("keydown", function(e) {
              if (e.which == 9 && !e.shiftKey) {
                tabbables.first().focus();
                e.preventDefault();
              }
            });

            tabbables.first().bind("keydown", function(e) {
              if (e.which == 9 && e.shiftKey) {
                tabbables.last().focus();
                e.preventDefault();
              }
            });

          } else {
            var tabbables = elm.find(":tabbable");
            tabbables.last().unbind("keydown");
            tabbables.first().unbind("keydown");
          }
        });

      });
    }
  };
});
