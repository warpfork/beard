package us.exultant.beard;

import us.exultant.ahs.core.*;

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

	
	
	/**
	 * Not intended to be used by client code. This constructs a java DomEvent from
	 * fields expected in a javascript event already normalized by jQuery; absense or
	 * oddness of any of the expected fields is considered exceptional and throws.
	 * Instances of this class can only be constructed by BeardBus in the course of
	 * its duties binding a new event pipe; the class itself and its write method are
	 * public so that they can be called from js.
	 */
	public static class Translator {
		Translator(WriteHead<DomEvent> $writeHead) { this.$wh = $writeHead; }
		private WriteHead<DomEvent> $wh;
		/**
		 * @param $type
		 * @param $srcElementId
		 * @param $timestamp
		 * @param $screenX
		 * @param $screenY
		 * @param $pageX
		 * @param $pageY
		 * @param $clientX
		 * @param $clientY
		 * @param $shiftKey
		 * @param $metaKey
		 * @param $ctrlKey
		 * @param $altKey
		 * @param $button
		 * @param $key
		 */
		public void write(BeardBus.Route $routekey, String $type, String $srcElementId, long $timestamp, Integer $screenX, Integer $screenY, Integer $pageX, Integer $pageY, Integer $clientX, Integer $clientY, Boolean $shiftKey, Boolean $metaKey, Boolean $ctrlKey, Boolean $altKey, Integer $button, Integer $key) {
			DomEvent $v = new DomEvent();
			$v.routekey = $routekey;
			$v.type = Type.valueOf($type.toUpperCase());
			$v.srcElementId = $srcElementId;
			$v.timestamp = $timestamp;
			$v.screenX = reify($screenX);
			$v.screenY = reify($screenY);
			$v.pageX = reify($pageX);
			$v.pageY = reify($pageY);
			$v.clientX = reify($clientX);
			$v.clientY = reify($clientY);
			$v.shiftKey = reify($shiftKey);
			$v.metaKey = reify($metaKey);
			$v.ctrlKey = reify($ctrlKey);
			$v.altKey = reify($altKey);
			$v.button = reify($button);
			$v.key = reify($key);
			$wh.write($v);
		}

		private static final int reify(Integer $x) { return reify($x, 0); }
		private static final int reify(Integer $x, int $default) { return $x == null ? $default : $x.intValue(); }
		private static final boolean reify(Boolean $x) { return reify($x, false); }
		private static final boolean reify(Boolean $x, boolean $default) { return $x == null ? $default : $x.booleanValue(); }
	}
	
	BeardBus.Route	routekey;
	
	public Type	type;
	public String	srcElementId;	// from evt.target.id
	public long	timestamp;	// only trust this to be relative, not at all absolute.  can be zero sometimes!!!  observed on MOUSEOUT, MOUSELEAVE, MOUSEOVER, MOUSEENTER in Firefox; timestamp exists for those events on Chrome.
	public int	screenX;
	public int	screenY;
	public int	pageX;
	public int	pageY;
	public int	clientX;
	public int	clientY;
	public boolean	shiftKey;
	public boolean	metaKey;	// note: this is unreliably available on some OS/browser combinations.
	public boolean	ctrlKey;
	public boolean	altKey;		// note: this is unreliably available on some OS/browser combinations (right now I can't generates clicks at all with alt down in firefox on linux, no idea why; mouse moving gets altKey=true).
	public int	button;		// 0:left, 1:middle, 2:right; we should probably reify this into an enum.
	public int	key;		// from evt.which ...not entirely sure what type is most correct here, or if we should also observe charCode or keyCode in any way (jQuery doesn't appear to).
	
	/*
	 * still unaccounted for:
	 * 
	view: [object Window]
	relatedTarget: null
	currentTarget: [object HTMLDivElement]
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
	
	public String toString() {
		return String.format("DomEvent[type=%s, srcElementId=%s, timestamp=%s, screenX=%s, screenY=%s, pageX=%s, pageY=%s, clientX=%s, clientY=%s, shiftKey=%s, metaKey=%s, ctrlKey=%s, altKey=%s, button=%s, key=%s]", this.type, this.srcElementId, this.timestamp, this.screenX, this.screenY, this.pageX, this.pageY, this.clientX, this.clientY, this.shiftKey, this.metaKey, this.ctrlKey, this.altKey, this.button, this.key);
	}
}
