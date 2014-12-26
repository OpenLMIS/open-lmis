/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('ProgramProduct', function () {
  var programProduct, progProduct;
  it('should set isa amount as -- if calculated and overridden isa both not avaialble', function () {
    progProduct = {};
    var facility = {};
    var period = {};
    programProduct = new ProgramProduct(progProduct);

    programProduct.calculateISA(facility, period);

    expect(programProduct.isaAmount).toEqual("--");
  });


  it('should set isa amount to overridden isa divided by pack size times number of months in period if available', function () {
    progProduct = {
      overriddenIsa: 400,
      product: {
        packSize: 22
      }
    };
    var facility = {};
    var period = {
      numberOfMonths: 2
    };
    programProduct = new ProgramProduct(progProduct);

    programProduct.calculateISA(facility, period);

    expect(programProduct.isaAmount).toEqual(37);
  });

  it('should set isa amount to overridden isa 0 if overridden by 0', function () {
    progProduct = {
      overriddenIsa: 0,
      product: {
        packSize: 22
      }
    };
    var facility = {};
    var period = {
      numberOfMonths: 2
    };
    programProduct = new ProgramProduct(progProduct);

    programProduct.calculateISA(facility, period);

    expect(programProduct.isaAmount).toEqual(0);
  });

  it('should calculate isa amount on basis of facility catchment population if overridden isa is not available', function () {
    var programProductIsa = {};
    progProduct = {
      programProductIsa: programProductIsa,
      product: {
        packSize: 22
      }
    };
    var facility = {};

    var period = {
      numberOfMonths: 2
    };
    var productIsa = new ProgramProductISA(programProductIsa);
    productIsa.init(programProductIsa);
    spyOn(window, 'ProgramProductISA').andReturn(productIsa);
    var spyOnCalculate = spyOn(productIsa, 'calculate').andReturn(200);

    programProduct = new ProgramProduct(progProduct);

    programProduct.calculateISA(facility, period);

    expect(spyOnCalculate).toHaveBeenCalled();
    expect(programProduct.isaAmount).toEqual(19);

  });

  it('should set isa amount as 0 if overridden isa is not available and calculated comes as undefined', function () {
    var programProductIsa = {};
    progProduct = {
      programProductIsa: programProductIsa,
      product: {
        packSize: 22
      }
    };
    var facility = {};

    var period = {
      numberOfMonths: 2
    };
    var productIsa = new ProgramProductISA(programProductIsa);
    productIsa.init(programProductIsa);
    spyOn(window, 'ProgramProductISA').andReturn(productIsa);
    var spyOnCalculate = spyOn(productIsa, 'calculate').andReturn(undefined);

    programProduct = new ProgramProduct(progProduct);

    programProduct.calculateISA(facility, period);

    expect(spyOnCalculate).toHaveBeenCalled();
    expect(programProduct.isaAmount).toEqual(0);
  });

  it('should group program products by product group name', function () {
    var product1 = {
      name: "product1",
      productGroup: {
        name: 'group1'
      }
    };
    var product2 = {
      name: "product2",
      productGroup: {
        name: 'group2'
      }
    };
    var facility = {
      supportedPrograms: [
        {
          programProducts: [
            {
              product: product1
            },
            {
              product: {
                productGroup: {
                  name: 'group1'
                }
              }
            },
            {
              product: {
                productGroup: {
                  name: 'group2'
                }
              }
            },
            {
              product: product2
            },
            {
              product: {}
            }
          ]
        }
      ]
    }

    var mapOfProductsByGroup = ProgramProduct.groupProductsMapByName(facility, "");

    expect(Object.keys(mapOfProductsByGroup).length).toEqual(3);
    expect(mapOfProductsByGroup["group1"][0]).toEqual({product: product1});
    expect(mapOfProductsByGroup["group2"][1]).toEqual({product: product2});

  });

  it('should calculate total for product isa as aggregate isa when existing is blank', function () {

    var aggregareProduct = {isaAmount: 100};
    var productTotal = {isaAmount: "--"};
    ProgramProduct.calculateProductIsaTotal(aggregareProduct, productTotal);

    expect(productTotal.isaAmount).toEqual(100);
  });

  it('should calculate total for product isa as sum of product total and aggregate isa', function () {

    var aggregareProduct = {isaAmount: 100};
    var productTotal = {isaAmount: 450};
    ProgramProduct.calculateProductIsaTotal(aggregareProduct, productTotal);

    expect(productTotal.isaAmount).toEqual(550);
  });

})
