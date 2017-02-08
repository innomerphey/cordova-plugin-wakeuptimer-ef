var exec = require("cordova/exec");

/**
 * This is a global variable called wakeup exposed by cordova
 */    
var Wakeup = function(){};

Wakeup.prototype.bind = function(success, error, options) {
    exec(success, error, "WakeupPlugin", "bind", [options]);
};

Wakeup.prototype.wakeup = function(success, error, options) {
    exec(success, error, "WakeupPlugin", "wakeup", [options]);
};

Wakeup.prototype.snooze = function(success, error, options) {
    exec(success, error, "WakeupPlugin", "snooze", [options]);
};

module.exports = new Wakeup();
