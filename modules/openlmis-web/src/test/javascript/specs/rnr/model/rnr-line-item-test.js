describe('RnrLineItem', function () {

    beforeEach(module('rnr'));


    describe('Fill consumption when it is marked as a computed column', function () {
        beforeEach(function () {
            programRnrColumnList = [
                {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
                {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
                {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
                {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
                {"indicator":"E", "name":"stockInHand", "source":{"name":"USER_INPUT"}}
            ];
        });

        it('should not calculate consumption when one of the dependant columns is not set', function () {
            var lineItem = {"id":1, "beginningBalance":1, "quantityReceived":2, "quantityDispensed":null, "totalLossesAndAdjustments":3, "stockInHand":null};
            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(null).toEqual(lineItem.quantityDispensed);
        });

        it('should calculate consumption', function () {
            var lineItem = {"id":1, "beginningBalance":5, "quantityReceived":20, "quantityDispensed":null, "totalLossesAndAdjustments":5, "stockInHand":10};
            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(20).toEqual(lineItem.quantityDispensed);
        });
    });

    describe('Fill stock in hand when it is marked as a computed column', function () {

        beforeEach(function () {
            programRnrColumnList = [
                {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
                {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
                {"indicator":"C", "name":"quantityDispensed", "source":{"name":"USER_INPUT"}},
                {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
                {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}}
            ];
        });

        it('should not calculate stock in hand when one of the dependant columns is not set', function () {
            var lineItem = {"id":1, "beginningBalance":1, "quantityReceived":2, "quantityDispensed":1, "totalLossesAndAdjustments":null, "stockInHand":null};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(null).toEqual(lineItem.stockInHand);
        });

        it('should calculate stock in hand when all values are 0 - NaN check', function () {
            var lineItem = {"id":1, "beginningBalance":0, "quantityReceived":0, "quantityDispensed":0, "totalLossesAndAdjustments":0, "stockInHand":null};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(0).toEqual(lineItem.stockInHand);
        });

        it('should calculate stock in hand', function () {
            var lineItem = {"id":1, "beginningBalance":1, "quantityReceived":10, "quantityDispensed":2, "totalLossesAndAdjustments":4, "stockInHand":null};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(13).toEqual(lineItem.stockInHand);
        });
    });

    describe('Fill normalized consumption', function () {

        beforeEach(function () {
            programRnrColumnList = [
                {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
                {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
                {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
                {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
                {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}},
                {"indicator":"F", "name":"newPatientCount", "source":{"name":"USER_INPUT"}},
                {"indicator":"X", "name":"stockOutDays", "source":{"name":"USER_INPUT"}}
            ];
        });

        it('should not fill normalized consumption when newPatientCount is displayed but not set', function () {
            var lineItem = {"id":1, "beginningBalance":1, "quantityReceived":10, "quantityDispensed":null, "totalLossesAndAdjustments":4, "stockInHand":2, "stockOutDays":5, "newPatientCount":null};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(null).toEqual(lineItem.normalizedConsumption);
        });

        it('should unset normalized consumption when newPatientCount is unset', function () {
            var lineItem = {"id":1, "beginningBalance":1, "quantityReceived":10, "quantityDispensed":null, "totalLossesAndAdjustments":4, "stockInHand":2, "stockOutDays":5, "newPatientCount":null, "dosesPerMonth":30, "dosesPerDispensingUnit":28, "normalizedConsumption":10};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(null).toEqual(lineItem.normalizedConsumption);
        });

        it('should calculate normalized consumption when facility is stocked out for the entire reporting period', function () {
            var lineItem = {"id":1, "beginningBalance":1, "quantityReceived":10, "quantityDispensed":null, "totalLossesAndAdjustments":4, "stockInHand":2, "stockOutDays":90, "newPatientCount":10, "dosesPerMonth":30, "dosesPerDispensingUnit":28};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(73).toEqual(lineItem.normalizedConsumption);
        });

        it('should not fill normalized consumption when stockOutDays is not set', function () {
            var lineItem = {"id":1, "beginningBalance":1, "quantityReceived":10, "quantityDispensed":null, "totalLossesAndAdjustments":4, "stockInHand":2, "stockOutDays":null, "newPatientCount":10};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(null).toEqual(lineItem.normalizedConsumption);
        });

        it('should unset normalized consumption when stockOutDays is unset', function () {
            var lineItem = {"id":1, "beginningBalance":1, "quantityReceived":10, "quantityDispensed":null, "totalLossesAndAdjustments":4, "stockInHand":2, "stockOutDays":null, "newPatientCount":10, "dosesPerMonth":30, "dosesPerDispensingUnit":28, "normalizedConsumption":10};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(null).toEqual(lineItem.normalizedConsumption);
        });

        it('should calculate normalized consumption when newPatientCount is displayed and set', function () {
            var lineItem = {"id":1, "beginningBalance":1, "quantityReceived":10, "quantityDispensed":null, "totalLossesAndAdjustments":-4, "stockInHand":2, "stockOutDays":5, "newPatientCount":10, "dosesPerMonth":30, "dosesPerDispensingUnit":28};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(65).toEqual(lineItem.normalizedConsumption);
        });

        it('should not fill normalized consumption when consumption column is empty', function () {
            var lineItem = {"id":1, "beginningBalance":1, "quantityReceived":2, "quantityDispensed":null, "totalLossesAndAdjustments":3, "stockInHand":null};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(null).toEqual(lineItem.normalizedConsumption);
        });

        it('should unset normalized consumption when consumption column is un-set', function () {
            var lineItem = {"id":1, "beginningBalance":null, "quantityReceived":10, "quantityDispensed":null, "totalLossesAndAdjustments":null, "stockInHand":2, "stockOutDays":5, "newPatientCount":10, "dosesPerMonth":30, "dosesPerDispensingUnit":28, "normalizedConsumption":10};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(null).toEqual(lineItem.normalizedConsumption);
        });

        it('should calculate normalized consumption when newPatientCount is not displayed', function () {
            programRnrColumnList = [
                {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
                {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
                {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
                {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
                {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}},
                {"indicator":"X", "name":"stockOutDays", "source":{"name":"USER_INPUT"}}
            ];
            var lineItem = {"id":1, "beginningBalance":1, "quantityReceived":10, "quantityDispensed":null,
                "totalLossesAndAdjustments":-4, "stockInHand":2, "stockOutDays":5, "newPatientCount":null, "dosesPerMonth":30, "dosesPerDispensingUnit":28};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(5).toEqual(lineItem.normalizedConsumption);
        });

    });

    describe('Fill AMC', function () {
        beforeEach(function () {
            programRnrColumnList = [
                {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
                {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
                {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
                {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
                {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}},
                {"indicator":"F", "name":"newPatientCount", "source":{"name":"USER_INPUT"}},
                {"indicator":"X", "name":"stockOutDays", "source":{"name":"USER_INPUT"}}
            ];
        });

        it('should set AMC to be equal to normalized consumption', function () {
            var lineItem = {"id":1, "beginningBalance":5, "quantityReceived":20, "quantityDispensed":null,
                "totalLossesAndAdjustments":5, "stockInHand":10, "stockOutDays":5, "newPatientCount":10, "dosesPerMonth":10, "dosesPerDispensingUnit":10};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(lineItem.normalizedConsumption).toEqual(lineItem.amc);
        });

        it('should un-set AMC when normalized consumption would be unset', function () {
            var lineItem = {"id":1, "beginningBalance":null, "quantityReceived":10, "quantityDispensed":null, "totalLossesAndAdjustments":null,
                "stockInHand":2, "dosesPerMonth":30, "dosesPerDispensingUnit":28, "normalizedConsumption":null};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(null).toEqual(lineItem.normalizedConsumption);
            expect(null).toEqual(lineItem.amc);
        });
    });

    describe('Fill Max Stock Quantity', function () {
        beforeEach(function () {
            programRnrColumnList = [
                {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
                {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
                {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
                {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
                {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}},
                {"indicator":"F", "name":"newPatientCount", "source":{"name":"USER_INPUT"}},
                {"indicator":"X", "name":"stockOutDays", "source":{"name":"USER_INPUT"}}
            ];
        });

        it('should set maxStockQuantity', function () {
            var lineItem = {"id":1, "beginningBalance":5, "quantityReceived":20, "quantityDispensed":null, "newPatientCount":0, "stockOutDays":0,
                "totalLossesAndAdjustments":-5, "stockInHand":10, "dosesPerMonth":10, "dosesPerDispensingUnit":10, "maxMonthsOfStock":3};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);

            expect(10).toEqual(lineItem.normalizedConsumption);
            expect(30).toEqual(lineItem.maxStockQuantity);
        });

        it('should not set maxStockQuantity if amc is not set', function () {
            var lineItem = {"id":1, "beginningBalance":null, "quantityReceived":10, "quantityDispensed":null, "totalLossesAndAdjustments":null, "newPatientCount":0, "stockOutDays":0,
                "stockInHand":2, "dosesPerMonth":30, "dosesPerDispensingUnit":28, "normalizedConsumption":null, "maxMonthsOfStock":3};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(null).toEqual(lineItem.amc);
            expect(null).toEqual(lineItem.maxStockQuantity);
        });

    });

    describe('Fill Calculated Order Quantity', function () {
        beforeEach(function () {
            programRnrColumnList = [
                {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
                {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
                {"indicator":"C", "name":"quantityDispensed", "source":{"name":"USER_INPUT"}},
                {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
                {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}},
                {"indicator":"F", "name":"newPatientCount", "source":{"name":"USER_INPUT"}},
                {"indicator":"X", "name":"stockOutDays", "source":{"name":"USER_INPUT"}}
            ];
        });

        it('should set calculatedOrderQuantity', function () {
            var lineItem = {"id":1, "beginningBalance":5, "quantityReceived":20, "quantityDispensed":15, "newPatientCount":0, "stockOutDays":0,
                "totalLossesAndAdjustments":-5, "stockInHand":null, "dosesPerMonth":10, "dosesPerDispensingUnit":10, "maxMonthsOfStock":3};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(40).toEqual(lineItem.calculatedOrderQuantity);
        });

        it('should not set calculatedOrderQuantity when stock in hand is not set', function () {
            var lineItem = {"id":1, "beginningBalance":5, "quantityReceived":20, "quantityDispensed":null, "newPatientCount":0, "stockOutDays":0,
                "totalLossesAndAdjustments":5, "stockInHand":null, "dosesPerMonth":10, "dosesPerDispensingUnit":10, "maxMonthsOfStock":3};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(null).toEqual(lineItem.calculatedOrderQuantity);
        });

        it('should set calculatedOrderQuantity to 0 when value goes negative', function () {
            var lineItem = {"id":1, "beginningBalance":40, "quantityReceived":10, "quantityDispensed":10, "newPatientCount":10, "stockOutDays":100,
                "totalLossesAndAdjustments":10, "stockInHand":null, "dosesPerMonth":30, "dosesPerDispensingUnit":10, "maxMonthsOfStock":3};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(0).toEqual(lineItem.calculatedOrderQuantity);
        });
    });

    describe('Fill Packs To Ship', function () {
        beforeEach(function () {
            programRnrColumnList = [
                {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
                {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
                {"indicator":"C", "name":"quantityDispensed", "source":{"name":"USER_INPUT"}},
                {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
                {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}},
                {"indicator":"F", "name":"newPatientCount", "source":{"name":"USER_INPUT"}},
                {"indicator":"X", "name":"stockOutDays", "source":{"name":"USER_INPUT"}}
            ];
        });

        it('should set packsToShip when calculated quantity is available and requested quantity is null', function () {
            var lineItem = {"id":1, "beginningBalance":5, "quantityReceived":20, "quantityDispensed":15, "newPatientCount":0,
                "stockOutDays":0, "totalLossesAndAdjustments":-5, "stockInHand":null, "dosesPerMonth":10,
                "dosesPerDispensingUnit":10, "maxMonthsOfStock":3, "packSize":12};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(3).toEqual(lineItem.packsToShip);
        });

        it('should set packsToShip when requested quantity is available', function () {
            var lineItem = {"id":1, "beginningBalance":5, "quantityReceived":20, "quantityDispensed":15, "newPatientCount":0,
                "stockOutDays":3, "totalLossesAndAdjustments":5, "stockInHand":null, "dosesPerMonth":10, "dosesPerDispensingUnit":10,
                "maxMonthsOfStock":3, "packSize":12, "quantityRequested":100};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(8).toEqual(lineItem.packsToShip);
        });

        it('should set packsToShip to one when rounded value of packsToShip is zero and roundToZero is false', function () {
            var lineItem = {"id":1, "beginningBalance":5, "quantityReceived":20, "quantityDispensed":15, "newPatientCount":0,
                "stockOutDays":3, "totalLossesAndAdjustments":5, "stockInHand":null, "dosesPerMonth":10, "dosesPerDispensingUnit":10,
                "maxMonthsOfStock":3, "packSize":10, "quantityRequested":4, "roundToZero":false};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(1).toEqual(lineItem.packsToShip);
        });

        it('should set packsToShip to zero when rounded value of packsToShip is zero and roundToZero is true', function () {
            var lineItem = {"id":1, "beginningBalance":5, "quantityReceived":20, "quantityDispensed":15, "newPatientCount":0,
                "stockOutDays":3, "totalLossesAndAdjustments":5, "stockInHand":null, "dosesPerMonth":10, "dosesPerDispensingUnit":10,
                "maxMornr is not definednthsOfStock":3, "packSize":10, "quantityRequested":4, "roundToZero":true};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(0).toEqual(lineItem.packsToShip);
        });
    });

    describe('Fill Cost', function () {
        beforeEach(function () {
            programRnrColumnList = [
                {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
                {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
                {"indicator":"C", "name":"quantityDispensed", "source":{"name":"USER_INPUT"}},
                {"indicator":"D", "name":"totalLossesAndAdjustments", "source":{"name":"USER_INPUT"}},
                {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}},
                {"indicator":"F", "name":"newPatientCount", "source":{"name":"USER_INPUT"}},
                {"indicator":"X", "name":"stockOutDays", "source":{"name":"USER_INPUT"}}
            ];
        });

        it('should set cost when pricePerPack and packsToShip are available', function () {
            var lineItem = {"id":1, "packSize":10, "quantityRequested":40, "roundToZero":true, "price":200};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(800).toEqual(lineItem.cost);
        });

        it('should un-set cost when packsToShip is not available', function () {
            var lineItem = {"id":1, "roundToZero":true, "price":200, "cost":2000};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(null).toEqual(lineItem.cost);
        });

        it('should un-set cost when price is not available', function () {
            var lineItem = {"id":1, "roundToZero":true, "packsToShip":10, "cost":2000};

            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.fill(null, programRnrColumnList);
            expect(null).toEqual(lineItem.cost);
        });
    });

    describe('Fill Full Supply Items Submitted Cost For Rnr', function () {
        beforeEach(function () {
            programRnrColumnList = [
                {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
                {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
                {"indicator":"C", "name":"quantityDispensed", "source":{"name":"USER_INPUT"}},
                {"indicator":"D", "name":"totalLossesAndAdjustments", "source":{"name":"USER_INPUT"}},
                {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}},
                {"indicator":"F", "name":"newPatientCount", "source":{"name":"USER_INPUT"}},
                {"indicator":"X", "name":"stockOutDays", "source":{"name":"USER_INPUT"}}
            ];
            lineItem1 = {"id":1, "packSize":10, "quantityRequested":40, "roundToZero":true, "price":30};
            lineItem2 = {"id":2, "packSize":5, "quantityRequested":null, "roundToZero":true, "price":20};
            lineItem3 = {"id":3, "packSize":15, "quantityRequested":25, "roundToZero":true, "price":200};
            rnr = new Object();
            rnr.lineItems = new Array(lineItem1, lineItem2, lineItem3);
        });

        it('should set fullSupplyItemsSubmittedCost', function () {
            var rnrLineItem1 = new RnrLineItem(lineItem1);
            rnrLineItem1.fill(rnr, programRnrColumnList);
            var rnrLineItem2 = new RnrLineItem(lineItem2);
            rnrLineItem2.fill(rnr, programRnrColumnList);
            var rnrLineItem3 = new RnrLineItem(lineItem3);
            rnrLineItem3.fill(rnr, programRnrColumnList);
            expect(320).toEqual(rnr.fullSupplyItemsSubmittedCost);
        });

    });

    describe('Losses and adjustment for line item', function () {

        it("should re evaluate total losses and adjustments for line item", function () {
            var lossAndAdjustment = {"type":{"name":"CLINIC_RETURN", "additive":true}, "quantity":45};
            var lineItem = {"id":"1", "totalLossesAndAdjustments":40, lossesAndAdjustments:[lossAndAdjustment]};
            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.reEvaluateTotalLossesAndAdjustments();
            expect(lineItem.totalLossesAndAdjustments).toEqual(45);
            expect(lineItem.lossesAndAdjustments).toEqual([lossAndAdjustment]);
        });

        it("should remove losses and adjustments for line item and update total losses and adjustments", function () {
            var lossAndAdjustment = {"type":{"name":"CLINIC_RETURN", "additive":true}, "quantity":45};
            var lineItem = {"id":"1", "totalLossesAndAdjustments":45, lossesAndAdjustments:[lossAndAdjustment]};
            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.removeLossAndAdjustment(lossAndAdjustment);
            expect(lineItem.totalLossesAndAdjustments).toEqual(0);
            expect(lineItem.lossesAndAdjustments).toEqual([]);
        });

        it("should add losses and adjustments for line item and update total losses and adjustments", function () {
            var lossAndAdjustment = {"type":{"name":"CLINIC_RETURN", "additive":true}, "quantity":45};
            var expectedLossAndAdjustment = {"type":{"name":"CLINIC_RETURN", "additive":true}, "quantity":45};
            var lineItem = {"id":"1", "totalLossesAndAdjustments":0, lossesAndAdjustments:[]};
            var rnrLineItem = new RnrLineItem(lineItem);
            rnrLineItem.addLossAndAdjustment(lossAndAdjustment);
            expect(lineItem.totalLossesAndAdjustments).toEqual(45);
            expect(lineItem.lossesAndAdjustments).toEqual([expectedLossAndAdjustment]);
        });
    });

    describe('Arithmetic validation', function () {
        var programRnrColumnList;
        beforeEach(function () {
            programRnrColumnList = [
                {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}, "formulaValidated":true}
            ];
        });


        it("should do arithmetic validations if on ", function () {
            var lineItem = {"id":"1", "beginningBalance":3, "quantityReceived":3, "quantityDispensed":3, "totalLossesAndAdjustments":-3, "stockInHand":3};
            var rnrLineItem = new RnrLineItem(lineItem);
            var arithmeticallyInvalid = rnrLineItem.arithmeticallyInvalid(programRnrColumnList);

            expect(arithmeticallyInvalid).toEqual(true);

            lineItem.quantityDispensed = 0;

            arithmeticallyInvalid = rnrLineItem.arithmeticallyInvalid(programRnrColumnList);
            expect(arithmeticallyInvalid).toEqual(false);

        });

        it("should return false arithmetic validations if off ", function () {
            programRnrColumnList[0].formulaValidated=false;
            var lineItem = {"id":"1", "beginningBalance":3, "quantityReceived":3, "quantityDispensed":3, "totalLossesAndAdjustments":-3, "stockInHand":3};
            var rnrLineItem = new RnrLineItem(lineItem);
            expect(rnrLineItem.arithmeticallyInvalid(programRnrColumnList)).toEqual(false);
        });

    });

    describe('Error message to be displayed', function () {
        var programRnrColumnList;
        beforeEach(function () {
            programRnrColumnList = [
                {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}, "formulaValidated":true}
            ];
        });


        it("should give error message for arithmetic validation error ", function () {
            var lineItem = {"id":"1", "beginningBalance":3, "quantityReceived":3, "quantityDispensed":3, "stockInHand":3};
            var rnrLineItem = new RnrLineItem(lineItem);
            spyOn(rnrLineItem,'arithmeticallyInvalid').andReturn("error");
            var errorMsg = rnrLineItem.getErrorMessage(programRnrColumnList);
            expect(errorMsg).toEqual("The entries are arithmetically invalid, please recheck");
        });

        it("should give error message for negative stock in hand", function () {
            var lineItem = {"id":"1", "beginningBalance":3, "quantityReceived":3, "quantityDispensed":33, "stockInHand":-3};
            var rnrLineItem = new RnrLineItem(lineItem);

            var errorMsg = rnrLineItem.getErrorMessage(programRnrColumnList);

            expect(errorMsg).toEqual("Stock On Hand is calculated to be negative, please validate entries");
        });

        it("should give error message for negative quantity dispensed ", function () {
          programRnrColumnList[0].formulaValidated = false;
          var lineItem = {"id":"1", "beginningBalance":3, "quantityReceived":3, "quantityDispensed":-3, "stockInHand":3};
          var rnrLineItem = new RnrLineItem(lineItem);
          var errorMsg = rnrLineItem.getErrorMessage(programRnrColumnList);

          expect(errorMsg).toEqual("Total Quantity Consumed is calculated to be negative, please validate entries");
        });

    });
});

