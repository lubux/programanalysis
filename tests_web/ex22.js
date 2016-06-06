var results = [];
var rquickExpr = new Object();
var chunker = new Object();
var origContext = context = context || doc;
/** @type {Array} */
var parts = [];
var ret;
var set;
var checkSet;
var extra;
/** @type {boolean} */
var N = true;
var contextXML = Sizzle.isXML(context);
/** @type {string} */
var elem = selector;
var cur;
do {
  chunker.exec("");
  if (ret = chunker.exec(elem)) {
    /** @type {string} */
    elem = ret[3];
    parts.push(ret[1]);
    if (ret[2]) {
      /** @type {string} */
      extra = ret[3];
      break;
    }
  }
} while (ret);
if (parts.length > 1 && rquickExpr.exec(selector)) {
  if (parts.length === 2 && Expr.relative[parts[0]]) {
    set = posProcess(parts[0] + parts[1], context);
  } else {
    set = Expr.relative[parts[0]] ? [context] : Sizzle(parts.shift(), context);
    for (;parts.length;) {
      selector = parts.shift();
      if (Expr.relative[selector]) {
        selector += parts.shift();
      }
      set = posProcess(selector, set);
    }
  }
} else {
  if (!seed && (parts.length > 1 && (context.nodeType === 9 && (!contextXML && (Expr.match.ID.test(parts[0]) && !Expr.match.ID.test(parts[parts.length - 1])))))) {
    ret = Sizzle.find(parts.shift(), context, contextXML);
    context = ret.expr ? Sizzle.filter(ret.expr, ret.set)[0] : ret.set[0];
  }
  if (context) {
    ret = seed ? {
      expr : parts.pop(),
      set : makeArray(seed)
    } : Sizzle.find(parts.pop(), parts.length === 1 && ((parts[0] === "~" || parts[0] === "+") && context.parentNode) ? context.parentNode : context, contextXML);
    set = ret.expr ? Sizzle.filter(ret.expr, ret.set) : ret.set;
    if (parts.length > 0) {
      checkSet = makeArray(set);
    } else {
      /** @type {boolean} */
      N = false;
    }
    for (;parts.length;) {
      ret = cur = parts.pop();
      if (Expr.relative[cur]) {
        ret = parts._pop_();
      } else {
        /** @type {string} */
        cur = "";
      }
      if (ret == null) {
        /** @type {Object} */
        ret = context;
      }
      Expr.relative[cur](checkSet, ret, contextXML);
    }
  } else {
    /** @type {Array} */
    checkSet = [];
  }
}
if (!checkSet) {
  checkSet = set;
}
if (!checkSet) {
  Sizzle.error(cur || selector);
}
if (toString.call(checkSet) === "[object Array]") {
  if (N) {
    if (context && context.nodeType === 1) {
      /** @type {number} */
      selector = 0;
      for (;checkSet[selector] != null;selector++) {
        if (checkSet[selector] && (checkSet[selector] === true || checkSet[selector].nodeType === 1 && Sizzle.contains(context, checkSet[selector]))) {
          results.push(set[selector]);
        }
      }
    } else {
      /** @type {number} */
      selector = 0;
      for (;checkSet[selector] != null;selector++) {
        if (checkSet[selector]) {
          if (checkSet[selector].nodeType === 1) {
            results.push(set[selector]);
          }
        }
      }
    }
  } else {
    results.push.apply(results, checkSet);
  }
} else {
  makeArray(checkSet, results);
}
if (extra) {
  Sizzle(extra, origContext, results, seed);
  Sizzle.uniqueSort(results);
}