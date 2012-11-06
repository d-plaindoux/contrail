/*global require */

require([ "Contrail/Factory", "qunit" ],
    function (Factory, QUnit) {
        /**
         * Test generation
         */
        QUnit.test("Check Component generation", function () {
            var c1 = Factory.component.basic.component(),
                c2 = Factory.component.basic.component();

            QUnit.notEqual(c1.getComponentId(), c2.getComponentId(), "Two fresh components must be different");
        });
    });