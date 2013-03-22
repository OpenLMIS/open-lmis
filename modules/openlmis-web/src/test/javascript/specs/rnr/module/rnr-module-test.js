/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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

