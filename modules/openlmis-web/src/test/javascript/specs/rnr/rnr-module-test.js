describe('RnrModuleTest', function () {

    beforeEach(module('rnr'));

    it('should validate  integer field', function () {
        expect(rnrModule.positiveInteger(100)).toEqual(true);
        expect(rnrModule.positiveInteger(0)).toEqual(true);
        expect(rnrModule.positiveInteger(-1)).toEqual(false);
        expect(rnrModule.positiveInteger('a')).toEqual(false);
        expect(rnrModule.positiveInteger(5.5)).toEqual(false);
    });

});
