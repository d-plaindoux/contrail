/*global require */

require([ "Core/jObj", "Utils/jStrict", "qunit" ],
    function (jObj, jStrict, QUnit) {
        "use strict";

        /**
         * Test Type Checking
         */
        function A() {
            jObj.bless(this);
        }

        QUnit.test("Check Subtype a:A <? A", function () {
            var a = new A();
            jStrict.assertType(a, "A");
            QUnit.equal(true, true, " a is an instance of A");
        });

        QUnit.test("Check Subtype a:A <? B", function () {
            var a = new A();
            try {
                jStrict.assertType(a, "B");
                QUnit.equal(true, false, "a is not an instance of B");
            } catch (e) {
                QUnit.equal(jObj.instanceOf(e, "AssertTypeError"), true, "Checking throws error to be a TypeError");
            }
        });
    });

