// CURRENTLY NOT USED!


importPackage(Packages.il.ac.bgu.cs.bp.bpjs.model.eventsets);
importPackage(Packages.java.util);

/////////////////////////////////////////////////////////////////////
// BP related functions
/////////////////////////////////////////////////////////////////////

function Any(type) {
    return bp.EventSet("Any(" + type + ")", function (e) {
        return e.name == type
    })
}

function Event(name, data) {
    if(data)
        return bp.Event(name, data)
    return bp.Event(name)
}

function bthread(name, f) {
    var int = []
    try {
        int = bp.thread.data.interrupt
    } catch (e) {
    }
    bp.registerBThread(name,
        {interrupt: int, block: []},
        f)
}

function block(e, f) {
    if (Array.isArray(e)) {
        e.forEach(ev => bp.thread.data.block.push(ev))
    } else {
        bp.thread.data.block.push(e)
    }
    f();
    if (Array.isArray(e)) {
        e.forEach(ev => bp.thread.data.block.pop())
    } else {
        bp.thread.data.block.pop()
    }
}

function interrupt(e, f) {
    if (Array.isArray(e)) {
        e.forEach(ev => bp.thread.data.interrupt.push(ev))
    } else {
        bp.thread.data.interrupt.push(e)
    }
    f();
    if (Array.isArray(e)) {
        e.forEach(ev => bp.thread.data.interrupt.pop())
    } else {
        bp.thread.data.interrupt.pop()
    }
}
function sync(stmt, priority) {
    const appendToPart = function (stmt, target, origin) {
        if (stmt[target]) {
            if (Array.isArray(stmt[target])) {
                stmt[target] = stmt[target].concat(bp.thread.data[origin])
            } else {
                stmt[target] = [stmt[target]].concat(bp.thread.data[origin])
            }
        } else {
            stmt[target] = bp.thread.data[origin]
        }
    }

    // var stmt = assign({}, statement)
    appendToPart(stmt, 'waitFor', 'interrupt')
    appendToPart(stmt, 'block', 'block')

    var e = bp.sync(stmt, priority ? priority : 0)
    if (bp.thread.data.interrupt.find(es => es.contains(e))) {
        throw e
    }
    return e
}

function waitFor(e) {
    return sync({waitFor: e});
}