package us.exultant.beard.msg;

public class DomEvent {
	public static enum Type {
		// MOUSE
		CLICK,
		DBLCLICK,
		MOUSEDOWN,
		MOUSEUP,
		MOUSEOVER,
		MOUSEOUT,
		MOUSEENTER,	// jquery invention -- like mouseover, but unwanted noise events due to bubbling... aren't.
		MOUSELEAVE,	// jquery invention -- like mouseout, but unwanted noise events due to bubbling... aren't.
		MOUSEMOVE,
		
		// FORM/FOCUS
		CHANGE,
		BLUR,
		FOCUS,
		SELECT,
		SUBMIT,
		FOCUSIN,	// jquery invention -- fires when any child of an element gets focus 
		FOCUSOUT,	// jquery invention -- fires when any child of an element loses focus
		
		// KEYBOARD
		KEYDOWN,
		KEYPRESS,
		KEYUP,
		
		// EH?
		RESIZE,
		SCROLL,
		
		// DOCUMENT
		LOAD,
		READY,
		UNLOAD
		
		// jquery also invents the "hover" and "toggle" events, but these are not useful in the context of beard.
	}
	
	Type $type;
	String $selector;	// unclear if this goes in the event, it just shows up in the Ingress function args.  or wait, perhaps not even there?  but it might be nice for the application's event handler to be able to remind itself of.
	String $srcElementId;
	//JSObject $srcFnPtr;	// secret.  used for demuxing.  unclear how to get this on js side.	// bs, this doesn't go in the event, it just shows up in the Ingress function args
	// we could let you give a list of properties on the event source object that you want copied to you.  but i dunno what good could come of that.
	
	
	
	/*
	 * An example click object:			some fields differ from chrome:
	 * 
	originalEvent: [object MouseEvent]
	type: click
	timeStamp: 60622330				actually looks like a unix timestamp, last 3 digits ms
	jQuery1720437917927164456: true
	toElement: undefined				toElement: HTMLDivElement	// no page in MDN.
	screenY: 736
	screenX: 323
	pageY: 650
	pageX: 52
	offsetY: undefined				has int value			// no page in MDN.
	offsetX: undefined				has int value			// no page in MDN.
	fromElement: undefined				fromElement: null		// no page in MDN.
	clientY: 650
	clientX: 52
	buttons: undefined
	button: 0
	which: 1
	view: [object Window]
	target: [object HTMLDivElement]
	shiftKey: false
	relatedTarget: null
	metaKey: false
	eventPhase: 2					eventPhase: 3
	currentTarget: [object HTMLDivElement]
	ctrlKey: false
	cancelable: true
	bubbles: true
	altKey: false
	srcElement: undefined				srcElement: HTMLDivElement	// no page in MDN.
	relatedNode: undefined
	attrName: undefined
	attrChange: undefined
	delegateTarget: [object HTMLDivElement]
	data: null
	handleObj: [object Object]
	 * 
	 * So basically jquery normalization is creating a lot of fields that aren't terribly reliable.
	 * 
	 */
}
