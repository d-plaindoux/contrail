/*global require */

require([ "./Utils", "qunit" ], function(Utils, QUnit) {
    /**
     * Test UUID generation
     */
    QUnit.test("Check UUID generation", function() {
        var uuid1 = Utils.UUID(), uuid2 = Utils.UUID();
        QUnit.notEqual(uuid1,uuid2, "Two fresh UUID must be dfferent");
    });
});