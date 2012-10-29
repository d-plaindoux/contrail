/*global require */

require([ "Utils/jUtils", "qunit" ], function(jUtils, QUnit) {
    /**
     * Test UUID generation
     */
    QUnit.test("Check UUID generation", function() {
        var uuid1 = jUtils.UUID(), uuid2 = jUtils.UUID();
        QUnit.notEqual(uuid1, uuid2 ,"Two fresh UUID must be dfferent");
    });
});