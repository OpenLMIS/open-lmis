/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('RnrModuleTest', function () {

  xit('should validate  integer field', function () {
    expect(rnrModule.positiveInteger(100)).toEqual(true);
    expect(rnrModule.positiveInteger(0)).toEqual(true);
    expect(rnrModule.positiveInteger(-1)).toEqual(false);
    expect(rnrModule.positiveInteger('a')).toEqual(false);
    expect(rnrModule.positiveInteger(5.5)).toEqual(false);
  });
});

