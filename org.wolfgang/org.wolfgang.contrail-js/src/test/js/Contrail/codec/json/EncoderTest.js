/*global require */

require([ "Core/jObj", "Codec/Factory", "qunit" ], function (jObj, Factory, QUnit) {
    "use strict";

    QUnit.test("Object encoding", function () {
        var object = { a:true }, encoder, result;
        encoder = Factory.json.encoder();
        result = encoder.transform(object);
        QUnit.equal(result.length, 1, "Checking result length");
        QUnit.equal(jObj.instanceOf(result[0], jObj.types.String), true, "Checking result type");
        QUnit.equal(result[0], '{"a":true}', "Checking encoding length");
    });
});

