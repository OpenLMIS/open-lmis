describe("ForgotPasswordController", function() {

    var controller, scope;

    beforeEach(module('openlmis.services'));
    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();
        controller = $controller(ForgotPasswordController,{$scope:scope});
    }));

    it("User should enter a username or an email", function() {
        scope.username = "";
        scope.email = "";
        scope.sendForgotPasswordEmail();
        expect(scope.error).toEqual("Username not entered");
    });
});
