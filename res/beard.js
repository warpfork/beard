
window.beard.bus_bind = function(sink, selector, type, routekey) {
	var x = $(selector);
	if (x.length==0) return null;
	var fn = function(evt) {
		sink.write(
				routekey,
			// decompose event before passing across pass,
			//  since otherwise the java side of the bridge gets stuck with the overhead of numerous round trips back across the bridge,
			//  one for every single property that needed to be obtained.
				evt.type,
				evt.target.id,
				evt.timeStamp,
				evt.screenX,
				evt.screenY,
				evt.pageX,
				evt.pageY,
				evt.clientX,
				evt.clientY,
				evt.shiftKey,
				evt.metaKey,
				evt.ctrlKey,
				evt.altKey,
				evt.button,
				evt.which
		);
	};
	x.bind(type, fn);
	return fn;
};

window.beard.bus_unbind = function(selector, type, fnptr) {
	$(selector).unbind(type, fnptr);
};

