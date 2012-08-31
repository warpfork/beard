/*
 * Copyright 2012 Eric Myhre <http://exultant.us>
 * 
 * This file is part of Beard.
 *
 * Beard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at the original copyright holder's option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package us.exultant.beard;

import java.lang.reflect.*;

class BeardBootstrap {
	@SuppressWarnings("unchecked")
	static Beardlet load(String $hopefullyBeardletName) {
		try {
			Class<?> $hopefullyBeardlet = Class.forName($hopefullyBeardletName);
			if (!Beardlet.class.isAssignableFrom($hopefullyBeardlet)) {
				System.err.println("starting a beard application requires specifying a Beardlet class for the application; a class name was given, but it doesn't implement the required interface.");
				System.exit(4);
			}
			return load((Class<? extends Beardlet>)$hopefullyBeardlet);
		} catch (ClassNotFoundException $e) {
			System.err.println("starting a beard application this way requires the name of the applications Beardlet class. \""+$hopefullyBeardletName+"\" was not found.");
			System.exit(3);
		}
		return null;	// unreachable, System.exit would have happened, because I only expect to use this method during application startup.
	}
	
	static Beardlet load(Class<? extends Beardlet> $beardletClass) {
		try {
			Constructor<?> $constr = $beardletClass.getConstructor();
			return (Beardlet) $constr.newInstance();
		} catch (NoSuchMethodException $e) {
			throw new Error("starting a beard application requires specifying a Beardlet class for the application; a class name was given, but it doesn't implement the required interface.", $e);
		} catch (SecurityException $e) {
			throw new Error($e);
		} catch (InstantiationException $e) {
			throw new Error($e);
		} catch (IllegalAccessException $e) {
			throw new Error($e);
		} catch (IllegalArgumentException $e) {
			throw new Error($e);
		} catch (InvocationTargetException $e) {
			throw new Error($e);
		}
	}
}
