package us.exultant.beard;

import netscape.javascript.*;

abstract class BeardImpl implements Beard {
	abstract Object call(JSObject $context, String $function, Object... $args);
	abstract JSObject jsb();
}
