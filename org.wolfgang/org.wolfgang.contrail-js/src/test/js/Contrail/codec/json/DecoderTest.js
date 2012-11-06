/*global require */

require([ "Core/jObj", "Codec/Factory", "qunit" ], function (jObj, Factory, QUnit) {
    "use strict";

    QUnit.test("Object decoding", function () {
        var string = '{"a":true}', decoder, result;
        decoder = Factory.json.decoder();
        result = decoder.transform(string);
        QUnit.equal(result.length, 1, "Checking result length");
        QUnit.equal(jObj.instanceOf(result[0], jObj.types.Object), true, "Checking result type");
        QUnit.equal(result[0].a, true, "Checking decoding value");
    });
});

