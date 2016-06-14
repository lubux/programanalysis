if (!Adition_Environment) {
    var Adition_Environment = function() {
        var exports = {};
        var options = {
            ref : false,
            os : false,
            browser : false,
            screen_res : false,
            flash : false,
            prf : false,
            iframe : false,
            cb_initialized : false
        };
        /** @type {string} */
        var agent = navigator.userAgent.toLowerCase();
        var compiled = function() {
            var at;
            var ch;
            var escapee = {
                '"' : '"',
                "\\" : "\\",
                "/" : "/",
                b : "\b",
                f : "\f",
                n : "\n",
                r : "\r",
                t : "\t"
            };
            var text;
            /**
             * @param {string} msg
             * @return {?}
             */
            var error = function(msg) {
                throw{
                    name : "SyntaxError",
                    message : msg,
                    at : at,
                    text : text
                };
            };
            /**
             * @param {string} c
             * @return {?}
             */
            var next = function(c) {
                if (c && c !== ch) {
                    error("Expected '" + c + "' instead of '" + ch + "'");
                }
                ch = text.charAt(at);
                at += 1;
                return ch;
            };
            /**
             * @return {?}
             */
            var number = function() {
                var number;
                /** @type {string} */
                var string = "";
                if (ch === "-") {
                    /** @type {string} */
                    string = "-";
                    next("-");
                }
                for (;ch >= "0" && ch <= "9";) {
                    string += ch;
                    next();
                }
                if (ch === ".") {
                    string += ".";
                    for (;next() && (ch >= "0" && ch <= "9");) {
                        string += ch;
                    }
                }
                if (ch === "e" || ch === "E") {
                    string += ch;
                    next();
                    if (ch === "-" || ch === "+") {
                        string += ch;
                        next();
                    }
                    for (;ch >= "0" && ch <= "9";) {
                        string += ch;
                        next();
                    }
                }
                /** @type {number} */
                number = +string;
                if (!isFinite(number)) {
                    error("Bad number");
                } else {
                    return number;
                }
            };
            /**
             * @return {?}
             */
            var string = function() {
                var hex;
                var i;
                /** @type {string} */
                var string = "";
                var uffff;
                if (ch === '"') {
                    for (;next();) {
                        if (ch === '"') {
                            next();
                            return string;
                        } else {
                            if (ch === "\\") {
                                next();
                                if (ch === "u") {
                                    /** @type {number} */
                                    uffff = 0;
                                    /** @type {number} */
                                    i = 0;
                                    for (;i < 4;i += 1) {
                                        /** @type {number} */
                                        hex = parseInt(next(), 16);
                                        if (!isFinite(hex)) {
                                            break;
                                        }
                                        /** @type {number} */
                                        uffff = uffff * 16 + hex;
                                    }
                                    string += String.fromCharCode(uffff);
                                } else {
                                    if (typeof escapee[ch] === "string") {
                                        string += escapee[ch];
                                    } else {
                                        break;
                                    }
                                }
                            } else {
                                string += ch;
                            }
                        }
                    }
                }
                error("Bad string");
            };
            /**
             * @return {undefined}
             */
            var white = function() {
                for (;ch && ch <= " ";) {
                    next();
                }
            };
            /**
             * @return {?}
             */
            var word = function() {
                switch(ch) {
                    case "t":
                        next("t");
                        next("r");
                        next("u");
                        next("e");
                        return true;
                    case "f":
                        next("f");
                        next("a");
                        next("l");
                        next("s");
                        next("e");
                        return false;
                    case "n":
                        next("n");
                        next("u");
                        next("l");
                        next("l");
                        return null;
                }
                error("Unexpected '" + ch + "'");
            };
            var value;
            /**
             * @return {?}
             */
            var array = function() {
                /** @type {Array} */
                var array = [];
                if (ch === "[") {
                    next("[");
                    white();
                    if (ch === "]") {
                        next("]");
                        return array;
                    }
                    for (;ch;) {
                        array.push(value());
                        white();
                        if (ch === "]") {
                            next("]");
                            return array;
                        }
                        next(",");
                        white();
                    }
                }
                error("Bad array");
            };
            /**
             * @return {?}
             */
            var object = function() {
                var key;
                var object = {};
                if (ch === "{") {
                    next("{");
                    white();
                    if (ch === "}") {
                        next("}");
                        return object;
                    }
                    for (;ch;) {
                        key = string();
                        white();
                        next(":");
                        if (Object.hasOwnProperty.call(object, key)) {
                            error('Duplicate key "' + key + '"');
                        }
                        object[key] = value();
                        white();
                        if (ch === "}") {
                            next("}");
                            return object;
                        }
                        next(",");
                        white();
                    }
                }
                error("Bad object");
            };
            /**
             * @return {?}
             */
            value = function() {
                white();
                switch(ch) {
                    case "{":
                        return object();
                    case "[":
                        return array();
                    case '"':
                        return string();
                    case "-":
                        return number();
                    default:
                        return ch >= "0" && ch <= "9" ? number() : word();
                }
            };
            return function(textAlt, reviver) {
                var result;
                /** @type {Function} */
                text = textAlt;
                /** @type {number} */
                at = 0;
                /** @type {string} */
                ch = " ";
                result = value();
                white();
                if (ch) {
                    error("Syntax error");
                }
                return typeof reviver === "function" ? function walk(holder, key) {
                    var k;
                    var v;
                    var value = holder[key];
                    if (value && typeof value === "object") {
                        for (k in value) {
                            if (Object.prototype.hasOwnProperty.call(value, k)) {
                                v = walk(value, k);
                                if (v !== undefined) {
                                    value[k] = v;
                                } else {
                                    delete value[k];
                                }
                            }
                        }
                    }
                    return reviver.call(holder, key, value);
                }({
                    "" : result
                }, "") : result;
            };
        }();
        /**
         * @param {string} data
         * @return {?}
         */
        exports.parseJSON = function(data) {
            if (typeof data !== "string" || !data) {
                return null;
            }
            /** @type {string} */
            data = data.replace(/^\s+/, "");
            /** @type {number} */
            var idx = data.length - 1;
            for (;idx >= 0;idx--) {
                if (/\S/.test(data.charAt(idx))) {
                    /** @type {string} */
                    data = data.substring(0, idx + 1);
                    break;
                }
            }
            if (window.JSON && window.JSON.parse) {
                return window.JSON.parse(data);
            }
            return compiled(data);
        };
        /**
         * @return {?}
         */
        exports.getVersion = function() {
            return 3;
        };
        /**
         * @return {?}
         */
        exports.getRef = function() {
            if (options.ref === false) {
                /** @type {string} */
                options.ref = escape(document.referrer);
            }
            return options.ref;
        };
        /**
         * @return {?}
         */
        exports.getOS = function() {
            if (options.os === false) {
                /** @type {number} */
                var val = -1;
                if (agent.search(/android/) != -1) {
                    /** @type {number} */
                    val = 12;
                } else {
                    if (agent.search(/symbian/) != -1) {
                        /** @type {number} */
                        val = 13;
                    } else {
                        if (agent.search(/windows\snt\s10\.0/) != -1) {
                            /** @type {number} */
                            val = 17;
                        } else {
                            if (agent.search(/windows\sce/) != -1) {
                                /** @type {number} */
                                val = 11;
                            } else {
                                if (agent.search(/iphone|ipad/) != -1) {
                                    /** @type {number} */
                                    val = 14;
                                } else {
                                    if (agent.search(/windows\snt\s6\.2/) != -1) {
                                        /** @type {number} */
                                        val = 15;
                                    } else {
                                        if (agent.search(/windows\snt\s6\.3/) != -1) {
                                            /** @type {number} */
                                            val = 16;
                                        } else {
                                            if (agent.search(/mac\sos\sx|macintosh|mac\x5fpowerpc/) != -1) {
                                                /** @type {number} */
                                                val = 5;
                                            } else {
                                                if (agent.search(/unix/) != -1) {
                                                    /** @type {number} */
                                                    val = 7;
                                                } else {
                                                    if (agent.search(/windows\snt\s6\.1/) != -1) {
                                                        /** @type {number} */
                                                        val = 10;
                                                    } else {
                                                        if (agent.search(/linux/) != -1) {
                                                            /** @type {number} */
                                                            val = 6;
                                                        } else {
                                                            if (agent.search(/windows\snt\s6\.0/) != -1) {
                                                                /** @type {number} */
                                                                val = 9;
                                                            } else {
                                                                if (agent.search(/windows\snt\s5\.2/) != -1) {
                                                                    /** @type {number} */
                                                                    val = 8;
                                                                } else {
                                                                    if (agent.search(/windows\snt\s5\.1/) != -1) {
                                                                        /** @type {number} */
                                                                        val = 3;
                                                                    } else {
                                                                        if (agent.search(/windows\snt\s5\.0/) != -1) {
                                                                            /** @type {number} */
                                                                            val = 2;
                                                                        } else {
                                                                            if (agent.search(/windows\snt\s4\.0/) != -1) {
                                                                                /** @type {number} */
                                                                                val = 4;
                                                                            } else {
                                                                                if (agent.search(/windows\s95|windows\sme|win98|windows\s98/) != -1) {
                                                                                    /** @type {number} */
                                                                                    val = 1;
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                /** @type {number} */
                options.os = val;
            }
            return options.os;
        };
        /**
         * @return {?}
         */
        exports.getBrowser = function() {
            if (options.browser === false) {
                /** @type {number} */
                var POST = -1;
                if (agent.search(/edge/) != -1) {
                    /** @type {number} */
                    POST = 15;
                } else {
                    if (agent.search(/trident\/(7\.0)/) != -1) {
                        /** @type {number} */
                        POST = 14;
                    } else {
                        if (agent.search(/msie\s10/) != -1) {
                            /** @type {number} */
                            POST = 13;
                        } else {
                            if (agent.search(/msie\s9/) != -1) {
                                /** @type {number} */
                                POST = 12;
                            } else {
                                if (agent.search(/chrome\//) != -1) {
                                    /** @type {number} */
                                    POST = 11;
                                } else {
                                    if (agent.search(/gecko\//) != -1) {
                                        /** @type {number} */
                                        POST = 6;
                                    } else {
                                        if (agent.search(/opera/) != -1) {
                                            /** @type {number} */
                                            POST = 7;
                                        } else {
                                            if (agent.search(/msie\s8/) != -1) {
                                                /** @type {number} */
                                                POST = 10;
                                            } else {
                                                if (agent.search(/msie\s7/) != -1) {
                                                    /** @type {number} */
                                                    POST = 9;
                                                } else {
                                                    if (agent.search(/konqueror|safari/) != -1) {
                                                        /** @type {number} */
                                                        POST = 8;
                                                    } else {
                                                        if (agent.search(/msie\s6/) != -1) {
                                                            /** @type {number} */
                                                            POST = 3;
                                                        } else {
                                                            if (agent.search(/netscape6|netscape\/(7\.\d*)/) != -1) {
                                                                /** @type {number} */
                                                                POST = 5;
                                                            } else {
                                                                if (agent.search(/netscape4/) != -1) {
                                                                    /** @type {number} */
                                                                    POST = 4;
                                                                } else {
                                                                    if (agent.search(/msie\s5/) != -1) {
                                                                        /** @type {number} */
                                                                        POST = 2;
                                                                    } else {
                                                                        if (agent.search(/msie\s4/) != -1) {
                                                                            /** @type {number} */
                                                                            POST = 1;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                /** @type {number} */
                options.browser = POST;
            }
            return options.browser;
        };
        /**
         * @return {?}
         */
        exports.getScreenRes = function() {
            if (options.screen_res === false) {
                /** @type {number} */
                var POST = -1;
                var resList = {
                    "1024x576" : 164,
                    "1024x600" : 165,
                    "1024x768" : 3,
                    "1072x600" : 166,
                    "1152x768" : 167,
                    "1152x864" : 4,
                    "1152x870" : 168,
                    "1152x900" : 169,
                    "1200x800" : 170,
                    "1200x900" : 171,
                    "1280x1024" : 5,
                    "1280x720" : 172,
                    "1280x768" : 9,
                    "1280x800" : 10,
                    "1280x854" : 173,
                    "1280x960" : 7,
                    "1360x1024" : 179,
                    "1360x768" : 174,
                    "1366x1024" : 180,
                    "1366x768" : 175,
                    "1376x768" : 176,
                    "1400x1050" : 8,
                    "1400x900" : 177,
                    "1440x900" : 11,
                    "1440x960" : 178,
                    "1600x1024" : 183,
                    "1600x1200" : 6,
                    "1600x768" : 181,
                    "1600x900" : 182,
                    "1680x1050" : 12,
                    "1920x1080" : 184,
                    "1920x1200" : 13,
                    "1920x1400" : 185,
                    "1920x1440" : 186,
                    "2048x1152" : 187,
                    "2048x1536" : 188,
                    "2560x1440" : 190,
                    "2560x1600" : 191,
                    "2560x2048" : 192,
                    "2800x2100" : 193,
                    "640x480" : 1,
                    "720x400" : 153,
                    "768x1024" : 199,
                    "800x480" : 154,
                    "800x600" : 2,
                    "832x624" : 159,
                    "848x480" : 155,
                    "852x480" : 156,
                    "858x484" : 158,
                    "864x480" : 157,
                    "960x540" : 160,
                    "960x640" : 162,
                    "960x720" : 163,
                    "964x544" : 161,
                    "Sonstige" : 0
                };
                try {
                    POST = resList[screen.width + "x" + screen.height];
                } catch (e) {
                    /** @type {number} */
                    POST = -1;
                }
                options.screen_res = POST;
            }
            return options.screen_res;
        };
        /**
         * @return {?}
         */
        exports.getFlashVersion = function() {
            if (options.flash === false) {
                /** @type {string} */
                var url = "";
                /** @type {(Navigator|null)} */
                var n = navigator;
                /** @type {null} */
                var route = null;
                if (n.plugins && n.plugins.length) {
                    /** @type {number} */
                    var j = 0;
                    for (;j < n.plugins.length;j++) {
                        if (n.plugins[j].name.indexOf("Shockwave Flash") != -1) {
                            route = n.plugins[j];
                            url = route.description.split("Shockwave Flash ")[1];
                            i = url.indexOf(".");
                            url = url.substr(0, i);
                            break;
                        }
                    }
                } else {
                    if (window.ActiveXObject) {
                        /** @type {number} */
                        j = 20;
                        for (;j >= 2;j--) {
                            try {
                                /** @type {*} */
                                var jsonObj = eval("new ActiveXObject('ShockwaveFlash.ShockwaveFlash." + j + "');");
                                if (jsonObj) {
                                    /** @type {number} */
                                    url = j;
                                    break;
                                }
                            } catch (e) {
                            }
                        }
                    }
                }
                options.flash = url;
            }
            return options.flash;
        };
        /**
         * @return {?}
         */
        exports.isFlashBlocked = function() {
            /** @type {boolean} */
            var isFlashBlocked = false;
            /** @type {boolean} */
            var is_firefox = navigator.userAgent.toLowerCase().indexOf("firefox") > -1;
            if (is_firefox) {
                var m = navigator.plugins["Shockwave Flash"];
                if (!m) {
                    return false;
                }
                var reqVerArray = m.version.split(".");
                if (reqVerArray.length === 4) {
                    /** @type {number} */
                    var charCodeToReplace = parseInt(reqVerArray[0], 10);
                    /** @type {number} */
                    var major = parseInt(reqVerArray[1], 10);
                    /** @type {number} */
                    var min = parseInt(reqVerArray[2], 10);
                    /** @type {number} */
                    var n = parseInt(reqVerArray[3], 10);
                    if (charCodeToReplace < 18 || charCodeToReplace === 18 && (major === 0 && (min === 0 && n <= 203))) {
                        /** @type {boolean} */
                        isFlashBlocked = true;
                    }
                }
            }
            return isFlashBlocked;
        };
        /**
         * @param {string} core
         * @return {?}
         */
        exports.getPrf = function(core) {
            /** @type {string} */
            var optsData = "";
            try {
                optsData = Adition_Prfstr(core);
            } catch (e) {
            }
            return optsData;
        };
        /**
         * @return {?}
         */
        exports.getIframe = function() {
            if (options.iframe === false) {
                /** @type {number} */
                var POST = 0;
                try {
                    if (window.top !== window) {
                        /** @type {number} */
                        POST = 1;
                    }
                } catch (e) {
                    /** @type {number} */
                    POST = 1;
                }
                /** @type {number} */
                options.iframe = POST;
            }
            return options.iframe;
        };
        /**
         * @param {string} core
         * @return {?}
         */
        exports.getAllAsQueryString = function(core) {
            var getAllAsQueryString = exports.getPrf(core);
            getAllAsQueryString += "&os=" + exports.getBrowser();
            getAllAsQueryString += "&screen_res=" + exports.getScreenRes();
            getAllAsQueryString += "&ref=" + exports.getRef();
            getAllAsQueryString += "&fvers=" + exports.getFlashVersion();
            getAllAsQueryString += "&iframe=" + exports.getIframe();
            return getAllAsQueryString;
        };
        /**
         * @param {?} dataAndEvents
         * @return {undefined}
         */
        exports.initCB = function(dataAndEvents) {
        };
        /**
         * @param {string} key
         * @param {string} text
         * @return {?}
         */
        var fn = function(key, text) {
            return typeof options[key] != "undefined" ? options[key] : typeof text != "undefined" ? text : null;
        };
        /**
         * @param {string} key
         * @param {?} value
         * @return {undefined}
         */
        var callback = function(key, value) {
            options[key] = value;
        };
        /**
         * @param {(HTMLFrameElement|HTMLIFrameElement)} el
         * @param {?} obj
         * @return {undefined}
         */
        var log = function(el, obj) {
            switch(exports.getBrowser()) {
                case 3:
                    break;
                case 9:
                    break;
                default:
                    el.contentWindow.postMessage(obj, "*");
            }
        };
        /**
         * @param {?} i
         * @return {?}
         */
        exports.getYieldProbe = function(i) {
            var o = adition_yieldlab[i];
            var node = yl.YpResult.get(o.adslotId);
            node.checkId = o.checkId;
            return node;
        };
        /**
         * @param {?} el
         * @return {?}
         */
        exports.H5 = function(el) {
            if (typeof el != "undefined" && fn("uid_ifr", false)) {
                log(fn("uid_ifr"), el);
                if (fn("uid", "-1") == "-1") {
                    window.setTimeout(function() {
                        log(fn("uid_ifr"), el);
                    }, 100);
                }
            }
            if (!fn("uid_init_time", false)) {
                callback("uid_init_time", new Date);
            }
            var matched = fn("uid_cnt", 0);
            callback("uid_cnt", 0);
            return[fn("uid", "-1"), matched];
        };
        (function init(position) {
            if (position == 3 || position == 9) {
                callback("uid", "-2");
                callback("uid_cnt", 1);
                return;
            }
            if (!fn("uid_ifr", false)) {
                /**
                 * @return {undefined}
                 */
                var add = function() {
                    try {
                        /** @type {Element} */
                        var node = document.createElement("iframe");
                        node.setAttribute("charset", "utf-8");
                        node.setAttribute("src", (window.location.protocol.match(/http/i) ? window.location.protocol : "http:") + "//imagesrv.adition.com/js/acb/uid.html");
                        if (position == 9 || (document.compatMode && document.compatMode == "BackCompat" || document.documentMode && document.documentMode == 7)) {
                            node.setAttribute("width", "0");
                            node.setAttribute("height", "0");
                            node.setAttribute("border", "0");
                            /** @type {string} */
                            node.style.position = "absolute";
                            /** @type {string} */
                            node.style.top = "-200px";
                        } else {
                            node.setAttribute("style", "width:0px;height:0px;border:0px;position:absolute;min-width:0px;min-height:0px");
                        }
                        /**
                         * @param {string} p
                         * @return {undefined}
                         */
                        var init = function(p) {
                            if (p.source == node.contentWindow) {
                                /** @type {Date} */
                                var val = new Date;
                                var s = fn("uid_init_time", val);
                                /** @type {number} */
                                var udataCur = val <= s ? 1 : val - s;
                                callback("uid", "" + p.data);
                                callback("uid_cnt", udataCur);
                                respond();
                            }
                        };
                        /**
                         * @return {undefined}
                         */
                        var respond = function() {
                            if (window.removeEventListener) {
                                window.removeEventListener("message", init);
                            } else {
                                if (window.detachEvent) {
                                    window.detachEvent("onmessage", init);
                                }
                            }
                        };
                        if (window.addEventListener) {
                            window.addEventListener("message", init, false);
                        } else {
                            if (window.attachEvent) {
                                window.attachEvent("onmessage", init);
                            }
                        }
                        document.body.insertBefore(node, document.body.firstChild);
                        callback("uid_ifr", node);
                    } catch (e) {
                    }
                };
                if (document.body != null) {
                    window.setTimeout(add, 1);
                } else {
                    /** @type {number} */
                    var poll = setInterval(function() {
                        if (document.body != null) {
                            add();
                            clearInterval(poll);
                        }
                    }, 5)
                }
            }
        })(exports.getBrowser());
        return exports;
    }();
    /**
     * @return {?}
     */
    var Adition_OSId = function() {
        return Adition_Environment.getOS();
    };
    /**
     * @return {?}
     */
    var Adition_BrowserId = function() {
        return Adition_Environment.getBrowser();
    };
    /**
     * @return {?}
     */
    var Adition_ResId = function() {
        return Adition_Environment.getScreenRes();
    };
    /**
     * @return {?}
     */
    var Adition_Referrer = function() {
        return[Adition_Environment.getIframe(), Adition_Environment.getRef()];
    };
    /**
     * @return {?}
     */
    var Adition_Flash = function() {
        return Adition_Environment.getFlashVersion();
    };
    /**
     * @return {?}
     */
    var Adition_isFlashBlocked = function() {
        return Adition_Environment.isFlashBlocked();
    };
}
if (!Adition_Jsonp_Manager) {
    var Adition_Jsonp_Manager = function() {
        var stage = {};
        var map = {};
        /**
         * @param {string} search
         * @param {?} target
         * @param {Object} options
         * @return {?}
         */
        var init = function(search, target, options) {
            try {
                /** @type {string} */
                var core = (new RegExp(/(?:sid=)([0-9]+)/)).exec(search)[1];
            } catch (e) {
                throw new Error("Could not match Contentunit ID");
            }
            if (typeof target != "function") {
                throw new Error("Callback has to be a function");
            }
            search = search.match(/wpt=X/i) != null ? search : search + "&wpt=X";
            if (typeof options == "object") {
                if (typeof options.keyword == "string") {
                    search += "&keyword=" + options.keyword;
                }
                if (typeof options.profile == "object") {
                    var key;
                    for (key in options.profile) {
                        search += "&p[" + key + "]=" + options.profile[key];
                    }
                }
                if (typeof options.data == "object") {
                    for (key in options.data) {
                        search += "&data[" + key + "]=" + options.data[key];
                    }
                }
            }
            if (Adition_Environment) {
                search += Adition_Environment.getAllAsQueryString(core);
            }
            return{
                "wpId" : core,
                "adUrl" : search,
                "params" : options,
                /** @type {Function} */
                "callBack" : target
            };
        };
        /**
         * @param {string} id
         * @param {?} i
         * @param {Object} opts
         * @return {undefined}
         */
        stage.get = function(id, i, opts) {
            var d = init(id, i, opts);
            map[d.wpId] = d;
            /** @type {Element} */
            var scriptEl = document.createElement("script");
            scriptEl.setAttribute("type", "text/javascript");
            scriptEl.setAttribute("src", d.adUrl);
            document.getElementsByTagName("head")[0].appendChild(scriptEl);
        };
        /**
         * @param {?} element
         * @param {?} arr
         * @return {undefined}
         */
        stage.insert = function(element, arr) {
            try {
                map[element].callBack(arr);
            } catch (e) {
            }
        };
        return stage;
    }()
}
if (window.Adition_VT_Area === undefined) {
    /** @type {Object} */
    window.Adition_VT_Area = new Object;
    (function(dataAndEvents) {
        dataAndEvents.getWindowViewportSize = function() {
            if (window.document.compatMode) {
                /** @type {(Element|null)} */
                var D = window.document.compatMode == "BackCompat" ? window.document.body : window.document.documentElement;
                return function() {
                    return{
                        width : D.clientWidth,
                        height : D.clientHeight
                    };
                };
            } else {
                return function() {
                    return{
                        width : window.innerWidth,
                        height : window.innerHeight
                    };
                };
            }
        }();
        /**
         * @param {Object} el
         * @return {?}
         */
        dataAndEvents.measureVisibleAreaPercentage = function(el) {
            var bb = dataAndEvents.getWindowViewportSize();
            var me = el.getBoundingClientRect();
            /** @type {number} */
            var width = me.right - me.left;
            /** @type {number} */
            var l = me.bottom - me.top;
            /** @type {number} */
            var x = Math.min(Math.max(me.right, 0), bb.width) - Math.min(Math.max(me.left, 0), bb.width);
            /** @type {number} */
            var y2 = Math.min(Math.max(me.bottom, 0), bb.height) - Math.min(Math.max(me.top, 0), bb.height);
            /** @type {number} */
            var eps = width * l;
            /** @type {number} */
            var xy = x * y2;
            /** @type {number} */
            var df = 100 * xy / eps;
            return Math.round(df);
        };
    })(window.Adition_VT_Area);
}
if (!Adition_VT_API) {
    var Adition_VT_API = {
        /**
         * @param {?} addObservedElement
         * @return {undefined}
         */
        trackViewtime : function(addObservedElement) {
            Adition_VT_Manager.addObservedElement(addObservedElement.nodeId, addObservedElement.logId, addObservedElement.networkId, addObservedElement.maxViewtime, addObservedElement.minChange, addObservedElement.maxTrans, addObservedElement.serverURI, addObservedElement.preFactor || 100, addObservedElement.userArea, addObservedElement.userTime);
        }
    }
}
if (!Adition_VT_Manager) {
    var Adition_VT_Manager = function() {
        /**
         * @param {Element} result
         * @return {?}
         */
        function callback(result) {
            if (successCallback(result)) {
                throw "Top window passed";
            }
            for (;!successCallback(result.parent);) {
                result = result.parent;
            }
            return result;
        }
        /**
         * @param {Element} result
         * @return {?}
         */
        function successCallback(result) {
            return result.top == result;
        }
        /**
         * @param {string} eventType
         * @param {Function} handler
         * @param {Object} target
         * @return {undefined}
         */
        function bind(eventType, handler, target) {
            target = target || window;
            if (navigator.appVersion.indexOf("MSIE") != -1) {
                target.attachEvent("on" + eventType, handler);
            } else {
                target.addEventListener(eventType, handler, false);
            }
        }
        /**
         * @param {number} dataAndEvents
         * @return {?}
         */
        function clone(dataAndEvents) {
            var s = new Adition_VT_MeasurementContainer;
            s.registerItem(new Adition_VT_MeasurementItem("vt_50", 50));
            s.registerItem(new Adition_VT_MeasurementItem("vt_60", 60));
            s.registerItem(new Adition_VT_MeasurementItem("vt_100", 100));
            if (dataAndEvents > 0) {
                s.registerItem(new Adition_VT_MeasurementItem(r, dataAndEvents));
            }
            return s;
        }
        /**
         * @return {undefined}
         */
        function init() {
            /**
             * @return {undefined}
             */
            var pause = function() {
                /** @type {Date} */
                self.lastUserAction = new Date;
            };
            bind("mousemove", pause, document);
            bind("mousedown", pause, document);
            bind("scroll", pause);
            bind("resize", pause);
            bind("scroll", self.measure);
            bind("resize", self.measure);
            bind("unload", self.trackAll);
            if (window.postMessage) {
                bind("message", self.postMessageHandler);
            }
            var vis = {
                hidden : "visibilitychange",
                mozHidden : "mozvisibilitychange",
                webkitHidden : "webkitvisibilitychange",
                msHidden : "msvisibilitychange"
            };
            var tail = function() {
                for (ev in vis) {
                    if (vis.hasOwnProperty(ev) && ev in document) {
                        return[ev, vis[ev]];
                    }
                }
                return false;
            }();
            /** @type {Array} */
            var codeSegments = ["focus", "blur", "focusin", "focusout"];
            if (tail) {
                codeSegments.push(tail[1]);
            }
            /** @type {number} */
            var i = 0;
            for (;i < codeSegments.length;i++) {
                bind(codeSegments[i], function(e) {
                    /** @type {Date} */
                    self.tabActiveStateDate = new Date;
                    if (e.type == "focus" || e.type == "focusin") {
                        /** @type {boolean} */
                        self.tabActiveState = true;
                    } else {
                        if (e.type == "blur" || e.type == "focusout") {
                            /** @type {boolean} */
                            self.tabActiveState = false;
                        } else {
                            /** @type {boolean} */
                            self.tabActiveState = document[tail[0]] ? false : true;
                        }
                    }
                });
            }
            self.setMeasureInterval();
        }
        var self = {};
        /** @type {number} */
        var imageData = 6;
        /** @type {number} */
        var version = parseInt("2");
        /** @type {string} */
        var base = (window.location.protocol.match(/http/i) ? window.location.protocol : "http:") + "//";
        /** @type {string} */
        var r = "vt_cust";
        /** @type {string} */
        var fx = "vt.adition.com/d";
        var opts = {
            "MEASUREMENT_POSSIBLE" : 0,
            "MEASUREMENT_NOT_POSSIBLE" : 1,
            "MEASUREMENT_ELEMENT_HIDDEN" : 2,
            "MEASUREMENT_ELEMENT_OUT_OF_BOUNDS" : 3,
            "ELEMENT_INVISIBLE" : 1,
            "ELEMENT_VISIBLE" : 2
        };
        /** @type {number} */
        var blockSize = 6E4;
        /** @type {Array} */
        var codeSegments = [];
        /** @type {boolean} */
        self.interval = false;
        /** @type {boolean} */
        self.tabActiveState = true;
        /** @type {Date} */
        self.tabActiveStateDate = new Date;
        /** @type {Date} */
        self.lastUserAction = new Date;
        /**
         * @return {?}
         */
        self.userIsActive = function() {
            /** @type {boolean} */
            var isFinal = new Date - self.lastUserAction < blockSize;
            if (isFinal && self.tabActiveState) {
                return true;
            }
            return false;
        };
        /**
         * @return {undefined}
         */
        self.setMeasureInterval = function() {
            if (self.interval) {
                window.clearInterval(self.interval);
                /** @type {boolean} */
                self.interval = false;
            }
            /** @type {number} */
            self.interval = window.setInterval(self.measure, 1E3);
        };
        /**
         * @return {?}
         */
        self.getViewportDimension = function() {
            if (document.compatMode) {
                /** @type {(Element|null)} */
                var popup = document.compatMode == "BackCompat" ? window.document.body : window.document.documentElement;
                /** @type {number} */
                var windowWidth = popup.clientWidth;
                /** @type {number} */
                var windowHeight = popup.clientHeight;
            } else {
                /** @type {number} */
                windowWidth = window.innerWidth;
                /** @type {number} */
                windowHeight = window.innerHeight;
            }
            return{
                width : windowWidth,
                height : windowHeight
            };
        };
        /**
         * @return {undefined}
         */
        self.measure = function() {
            window.clearInterval(self.interval);
            /** @type {boolean} */
            self.interval = false;
            /** @type {number} */
            var i = 0;
            for (;i < codeSegments.length;i++) {
                codeSegments[i].periodicMeasure();
            }
            self.setMeasureInterval();
        };
        /**
         * @return {undefined}
         */
        self.trackAll = function() {
            /** @type {number} */
            var i = 0;
            for (;i < codeSegments.length;i++) {
                codeSegments[i].closeMeasure();
            }
        };
        /**
         * @param {Node} el
         * @return {?}
         */
        self.checkVisibility = function(el) {
            if (el.currentStyle) {
                if (el.currentStyle.visibility == "hidden" || (el.currentStyle.display == "none" || ((el.currentStyle.overflowY == "hidden" || el.nodeName.toLowerCase() == "iframe") && el.currentStyle.height == "0px" || (el.currentStyle.overflowX == "hidden" || el.nodeName.toLowerCase() == "iframe") && el.currentStyle.width == "0px"))) {
                    return false;
                }
            } else {
                if (window.getComputedStyle(el, null).getPropertyValue("visibility") == "hidden" || (window.getComputedStyle(el, null).getPropertyValue("display") == "none" || ((window.getComputedStyle(el, null).getPropertyValue("overflow-y") == "hidden" || el.nodeName.toLowerCase() == "iframe") && window.getComputedStyle(el, null).getPropertyValue("height") == "0px" || (window.getComputedStyle(el, null).getPropertyValue("overflow-x") == "hidden" || el.nodeName.toLowerCase() == "iframe") && window.getComputedStyle(el,
                        null).getPropertyValue("width") == "0px"))) {
                    return false;
                }
            }
            return true;
        };
        /**
         * @param {HTMLElement} dom
         * @param {string} err
         * @param {number} funcToCall
         * @param {number} deepDataAndEvents
         * @param {number} ignoreMethodDoesntExist
         * @param {number} opt_locale
         * @param {Object} type
         * @param {number} textAlt
         * @param {number} dataAndEvents
         * @param {number} details
         * @return {undefined}
         */
        self.addObservedElement = function(dom, err, funcToCall, deepDataAndEvents, ignoreMethodDoesntExist, opt_locale, type, textAlt, dataAndEvents, details) {
            dataAndEvents = dataAndEvents || 0;
            details = details || 0;
            type = type || fx;
            /** @type {number} */
            var i = 0;
            for (;i < codeSegments.length;i++) {
                if (codeSegments[i].lid == err) {
                    return;
                }
            }
            if (typeof dom == "string") {
                /** @type {(HTMLElement|null)} */
                dom = document.getElementById(dom);
            }
            if (dom && dom.nodeName) {
                /** @type {string} */
                var target = Adition_Environment.getBrowser() == 11 || Adition_Environment.getBrowser() == 6 ? "embed" : "object";
                if (dom.getElementsByTagName(target).length == 1) {
                    dom = dom.getElementsByTagName(target)[0];
                }
                if (!dom.id) {
                    /** @type {number} */
                    dom.id = Math.random();
                }
                var data = {
                    customTime : details,
                    isTracking : false,
                    initInterval : null,
                    initCount : 0,
                    maxvt : deepDataAndEvents ? deepDataAndEvents : 60,
                    minc : ignoreMethodDoesntExist ? ignoreMethodDoesntExist : 5,
                    maxt : opt_locale ? opt_locale : 5,
                    serverAddress : type,
                    measurementState : opts.MEASUREMENT_POSSIBLE,
                    dom : dom,
                    state : 0,
                    lid : err,
                    n : funcToCall ? funcToCall : 0,
                    initTime : false,
                    lastMeasureTime : new Date,
                    vt : 0,
                    vtContainer : clone(dataAndEvents),
                    ma : 0,
                    suvt : 0,
                    hpos : 0,
                    vpos : 0,
                    sfac : textAlt ? textAlt / 100 : 1,
                    surface : 0,
                    lastTrack : {
                        vt : 0,
                        ma : 0,
                        suvt : 0,
                        hpos : 0,
                        vpos : 0,
                        time : new Date,
                        count : 0
                    },
                    /**
                     * @return {undefined}
                     */
                    periodicMeasure : function() {
                        if (this.initTime == false) {
                            return;
                        }
                        this.measure();
                        /** @type {Date} */
                        var against = new Date;
                        if (against - this.initTime >= 28E4 && this.vt > this.lastTrack.vt) {
                            this.track();
                            self.removeElement(this);
                        } else {
                            if (this.isTrackable() && this.isTracking == false) {
                                /** @type {boolean} */
                                this.isTracking = true;
                                this.track();
                            }
                        }
                    },
                    /**
                     * @return {undefined}
                     */
                    closeMeasure : function() {
                        this.measure();
                        this.track();
                    },
                    /**
                     * @return {undefined}
                     */
                    measure : function() {
                        /** @type {(HTMLElement|null)} */
                        var failuresLink = document.getElementById(this.dom.id);
                        var count = Adition_VT_Area.measureVisibleAreaPercentage(failuresLink);
                        /** @type {number} */
                        var spyCall = count > 0 ? opts.ELEMENT_VISIBLE : opts.ELEMENT_INVISIBLE;
                        count = count > 100 ? 100 : count;
                        this.update(spyCall, (count > 0 ? count : 0) * this.sfac);
                    },
                    /**
                     * @param {Object} val
                     * @param {number} bytes
                     * @return {undefined}
                     */
                    update : function(val, bytes) {
                        var b = this.lastMeasureTime;
                        /** @type {Date} */
                        var a = new Date;
                        /** @type {number} */
                        var temp = (a - b) / 1E3;
                        /** @type {Date} */
                        this.lastMeasureTime = a;
                        if (this.state === opts.ELEMENT_VISIBLE && self.userIsActive()) {
                            this.vt += temp;
                            this.vtContainer.update(bytes, temp);
                            this.suvt += temp * (this.surface / 100);
                        }
                        /** @type {Object} */
                        this.state = val;
                        /** @type {number} */
                        this.surface = Math.round(bytes);
                        if (this.surface > this.ma) {
                            /** @type {number} */
                            this.ma = this.surface;
                        }
                    },
                    /**
                     * @return {?}
                     */
                    getSecondsSinceInit : function() {
                        return Math.round((new Date - this.initTime) / 1E3);
                    },
                    /**
                     * @return {?}
                     */
                    isTrackable : function() {
                        if (this.lastTrack.count == 0) {
                            return true;
                        } else {
                            if (this.vt == 0 || (this.lastTrack.count >= this.maxt || this.lastTrack.vt > this.maxvt)) {
                                return false;
                            } else {
                                if (this.lastTrack.count > 1 && this.lastTrack.vt + this.minc > this.vt) {
                                    return false;
                                } else {
                                    if (this.vt - this.lastTrack.vt < (this.maxvt - this.lastTrack.vt) / (this.maxt - this.lastTrack.count)) {
                                        return false;
                                    }
                                }
                            }
                        }
                        return true;
                    },
                    /**
                     * @return {undefined}
                     */
                    track : function() {
                        var countInfo = this.lastTrack.count + 1;
                        this.lastTrack = {
                            vt : this.vt,
                            ma : this.ma,
                            suvt : Math.round(this.suvt),
                            hpos : this.hpos,
                            vpos : this.vpos,
                            time : new Date,
                            count : countInfo
                        };
                        self.track(this.lid, this.n, this.measurementState, this);
                        /** @type {boolean} */
                        this.isTracking = false;
                    },
                    /**
                     * @return {?}
                     */
                    getUrl : function() {
                        var obj = {
                            n : this.n,
                            vt : Math.ceil(this.vt),
                            elapsed : this.getSecondsSinceInit(),
                            ma : this.ma,
                            suvt : Math.round(this.suvt),
                            h : this.hpos,
                            v : this.vpos
                        };
                        var source = this.vtContainer.getData();
                        /** @type {number} */
                        source[r] = this.customTime > 0 && source[r] >= this.customTime ? 1 : 0;
                        var prop;
                        for (prop in source) {
                            obj[prop] = source[prop];
                        }
                        /** @type {string} */
                        var optsData = "";
                        for (prop in obj) {
                            optsData += "&" + prop + "=" + obj[prop];
                        }
                        return this.serverAddress + "?lid=" + this.lid + optsData;
                    },
                    /**
                     * @return {?}
                     */
                    init : function() {
                        /** @type {number} */
                        this.measurementState = opts.MEASUREMENT_POSSIBLE;
                        this.initCount++;
                        /** @type {number} */
                        var left = 0;
                        /** @type {number} */
                        var top = 0;
                        var cz = Adition_BrowserId();
                        if (this.dom.id != null && this.dom != document.getElementById(this.dom.id)) {
                            /** @type {(HTMLElement|null)} */
                            this.dom = document.getElementById(this.dom.id);
                        }
                        if (this.dom.offsetParent) {
                            var dom = this.dom;
                            for (;dom;) {
                                left += dom.offsetLeft;
                                top += dom.offsetTop;
                                dom = dom.offsetParent;
                            }
                        }
                        if (this.dom.parentElement) {
                            dom = this.dom;
                            for (;dom.parentElement;) {
                                if (!self.checkVisibility(dom)) {
                                    /** @type {number} */
                                    this.measurementState = opts.MEASUREMENT_ELEMENT_HIDDEN;
                                }
                                dom = dom.parentElement;
                            }
                        }
                        var image = self.getViewportDimension();
                        /** @type {number} */
                        this.hpos = Math.round(left / image.width * 100) > 9999 ? 9999 : Math.round(left / image.width * 100);
                        /** @type {number} */
                        this.vpos = Math.round(top / image.height * 100) > 9999 ? 9999 : Math.round(top / image.height * 100);
                        if (this.hpos < 0 || this.vpos < 0) {
                            /** @type {(ClientRect|null)} */
                            var brect = document.getElementById(this.dom.id).getBoundingClientRect();
                            /** @type {number} */
                            var bottom = brect.bottom - brect.top;
                            /** @type {number} */
                            var width = brect.right - brect.left;
                            if (left < 0) {
                                if (left + width <= 0) {
                                    /** @type {number} */
                                    this.measurementState = opts.MEASUREMENT_ELEMENT_OUT_OF_BOUNDS;
                                } else {
                                    /** @type {number} */
                                    this.hpos = 0;
                                }
                            }
                            if (top < 0) {
                                if (top + bottom <= 0) {
                                    /** @type {number} */
                                    this.measurementState = opts.MEASUREMENT_ELEMENT_OUT_OF_BOUNDS;
                                } else {
                                    /** @type {number} */
                                    this.vpos = 0;
                                }
                            }
                            if (this.measurementState == opts.MEASUREMENT_ELEMENT_OUT_OF_BOUNDS) {
                                /** @type {string} */
                                this.hpos = "";
                                /** @type {string} */
                                this.vpos = "";
                            }
                        }
                        if (window.top != window || (cz == 2 || cz == 1)) {
                            self.trackError(this.lid, this.n, opts.MEASUREMENT_NOT_POSSIBLE, this.serverAddress);
                            self.removeElement(this);
                            return true;
                        } else {
                            if (this.measurementState != opts.MEASUREMENT_POSSIBLE) {
                                return false;
                            } else {
                                this.measure();
                                /** @type {Date} */
                                this.initTime = new Date;
                                return true;
                            }
                        }
                    }
                };
                codeSegments.push(data);
                if (!data.init()) {
                    /** @type {number} */
                    data.initInterval = window.setInterval(function() {
                        if (data.init() || data.initCount > 50) {
                            window.clearInterval(data.initInterval);
                            if (data.initCount > 50) {
                                self.track(data.lid, data.n, data.measurementState, data);
                                self.removeElement(data);
                            }
                        }
                    }, 100);
                }
            } else {
                self.trackError(err, funcToCall, opts.MEASUREMENT_NOT_POSSIBLE, type);
            }
        };
        /**
         * @param {?} element
         * @return {undefined}
         */
        self.removeElement = function(element) {
            /** @type {number} */
            var i = 0;
            for (;i < codeSegments.length;i++) {
                if (element == codeSegments[i]) {
                    codeSegments.splice(i, 1);
                }
            }
        };
        /**
         * @param {string} context
         * @param {string} reason
         * @param {number} event
         * @param {?} model
         * @return {undefined}
         */
        self.track = function(context, reason, event, model) {
            switch(event) {
                case opts.MEASUREMENT_ELEMENT_HIDDEN:
                    ;
                case opts.MEASUREMENT_ELEMENT_OUT_OF_BOUNDS:
                    ;
                case opts.MEASUREMENT_NOT_POSSIBLE:
                    update(model.serverAddress + "?lid=" + context + "&f=" + event + "&n=" + reason);
                    break;
                case opts.MEASUREMENT_POSSIBLE:
                    update(model.getUrl());
                    break;
            }
        };
        /**
         * @param {string} err
         * @param {number} funcToCall
         * @param {number} result
         * @param {string} keepData
         * @return {undefined}
         */
        self.trackError = function(err, funcToCall, result, keepData) {
            update(keepData + "?lid=" + err + "&f=" + result + "&n=" + funcToCall);
        };
        /**
         * @param {string} post
         * @return {undefined}
         */
        var update = function(post) {
            /** @type {Image} */
            var image = new Image;
            /** @type {string} */
            image.src = base + post + "&version=" + imageData;
        };
        /**
         * @param {Object} event
         * @return {undefined}
         */
        self.postMessageHandler = function(event) {
            if (event.source === window) {
                return;
            }
            try {
                var data = Adition_Environment.parseJSON(event.data);
            } catch (e) {
                /** @type {null} */
                data = null;
            }
            if (data && (data.service && (data.service == "Adition_VT_Manager" && data.networkId))) {
                var current = callback(event.source);
                if (current === undefined) {
                    return;
                }
                /** @type {null} */
                var button = null;
                /** @type {NodeList} */
                var codeSegments = document.getElementsByTagName("iframe");
                /** @type {number} */
                var i = 0;
                for (;i < codeSegments.length;i++) {
                    if (codeSegments[i].contentWindow === current) {
                        button = codeSegments[i];
                        break;
                    }
                }
                var msg = finished(data);
                if (!msg.preFactor || msg.preFactor <= 0) {
                    self.trackError(msg.logId, msg.networkId, opts.MEASUREMENT_ELEMENT_OUT_OF_BOUNDS, msg.serverURI);
                } else {
                    data.nodeId = button;
                    Adition_VT_API.trackViewtime(data);
                }
                event.source.postMessage('{"service":"Adition_VT_Manager","state":"ok","logId":"' + data.logId + '"}', "*");
            }
        };
        /**
         * @param {Object} data
         * @return {?}
         */
        var finished = function(data) {
            var params;
            if (data.version && data.version == version) {
                /** @type {Object} */
                params = data;
                delete params.version;
            } else {
                /** @type {Object} */
                params = new Object;
                params.service = data.service;
                params.networkId = data.networkId;
                params.logId = data.logId;
                params.maxViewtime = data.maxViewtime;
                params.minChange = data.minChange;
                params.maxTrans = data.maxTrack;
                params.preFactor = data.max_surface;
                /** @type {string} */
                params.serverURI = fx;
            }
            return params;
        };
        init();
        return self;
    }();
    /**
     * @return {undefined}
     */
    Adition_VT_MeasurementContainer = function() {
        /** @type {Array} */
        this.items = [];
    };
    /**
     * @param {?} item
     * @return {undefined}
     */
    Adition_VT_MeasurementContainer.prototype.registerItem = function(item) {
        this.items.push(item);
    };
    /**
     * @param {number} val
     * @param {number} bytes
     * @return {undefined}
     */
    Adition_VT_MeasurementContainer.prototype.update = function(val, bytes) {
        /** @type {number} */
        var i = 0;
        var part;
        var len = this.items.length;
        for (;i < len;i++) {
            part = this.items[i];
            if (part.minSurface < 0) {
                continue;
            }
            if (val >= part.minSurface) {
                part.time += bytes;
            }
        }
    };
    /**
     * @return {?}
     */
    Adition_VT_MeasurementContainer.prototype.getData = function() {
        var data = {};
        /** @type {number} */
        var i = 0;
        var point;
        var len = this.items.length;
        for (;i < len;i++) {
            point = this.items[i];
            /** @type {number} */
            data[point.name] = Math.ceil(point.time);
        }
        return data;
    };
    /**
     * @param {string} tmplName
     * @param {number} dataAndEvents
     * @return {undefined}
     */
    Adition_VT_MeasurementItem = function(tmplName, dataAndEvents) {
        /** @type {string} */
        this.name = tmplName;
        /** @type {number} */
        this.minSurface = dataAndEvents;
        /** @type {number} */
        this.time = 0;
    };
}
if (typeof window.Adition_Prfstr == "undefined") {
    /**
     * @param {?} program
     * @return {?}
     */
    window.Adition_Prfstr = function(program) {
        var expires;
        try {
            var result = Adition_Environment.getYieldProbe(program);
            /** @type {string} */
            expires = "&p[_yl_adslotId:" + result.id + ",_yl_checkId:" + result.checkId + ",_yl_price:" + result.price + ",_yl_advertiser:" + escape(result.advertiser) + ",_yl_curl:" + escape(result.curl);
            if (result.pid !== undefined) {
                expires += ",_yl_pid:" + escape(result.pid);
            }
            if (result.prio !== undefined) {
                expires += ",_yl_prio:" + escape(result.prio);
            }
            expires += "]";
        } catch (e) {
            /** @type {string} */
            expires = "";
        }
        return expires;
    };
}
if (!Adition_PostMessageService) {
    var Adition_PostMessageService = function() {
        var self = {};
        /**
         * @param {?} response
         * @return {?}
         */
        self.handleRequest = function(response) {
            try {
                /** @type {*} */
                var result = JSON.parse(response);
            } catch (exception) {
                return null;
            }
            if (typeof result.method == "string") {
                if (typeof self.serviceMethods[result.method] == "function") {
                    var v0 = self.serviceMethods[result.method]();
                    if (result["response"]) {
                        var i;
                        for (i in result["response"]) {
                            if (result["response"][i] == "#result#") {
                                result["response"][i] = v0;
                                break;
                            }
                        }
                        /** @type {string} */
                        var emitter = JSON.stringify(result["response"]);
                        return emitter;
                    }
                }
            }
            return null;
        };
        self.serviceMethods = {
            /**
             * @return {?}
             */
            getLocation : function() {
                var location = {
                    location : window.location.href,
                    referrer : document.referrer
                };
                return location;
            },
            /**
             * @return {undefined}
             */
            turnOff : function() {
                Adition_PostMessageService.turnOff();
            }
        };
        /**
         * @param {Object} e
         * @return {?}
         */
        self.serviceListener = function(e) {
            /** @type {Array} */
            var codeSegments = [/^http[s]?:\/\/[\w\.]*t4ft\.de$/i, /^http[s]?:\/\/[\w\.]*adition\.com$/i, /^http[s]?:\/\/dmp\.theadex\.com$/i];
            /** @type {boolean} */
            var validOrigin = false;
            if (window == e.source) {
                return null;
            }
            /** @type {number} */
            var i = 0;
            for (;i < codeSegments.length;i++) {
                if (codeSegments[i].test(e.origin)) {
                    /** @type {boolean} */
                    validOrigin = true;
                    break;
                }
            }
            if (validOrigin) {
                var key = e.data;
                var camelKey = self.handleRequest(key);
                if (camelKey) {
                    e.source.postMessage(camelKey, e.origin);
                }
            }
        };
        /**
         * @return {undefined}
         */
        self.turnOn = function() {
            if (window.addEventListener) {
                window.addEventListener("message", self.serviceListener, false);
            } else {
                if (window.attachEvent) {
                    window.attachEvent("onmessage", self.serviceListener);
                }
            }
        };
        /**
         * @return {undefined}
         */
        self.turnOff = function() {
            if (window.removeEventListener) {
                window.removeEventListener("message", self.serviceListener);
            } else {
                window.detachEvent("onmessage", self.serviceListener);
            }
        };
        if (window != top) {
            self.turnOff();
        } else {
            self.turnOn();
        }
        return self;
    }()
}
Adition;
