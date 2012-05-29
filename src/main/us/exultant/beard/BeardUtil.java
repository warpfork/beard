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

import us.exultant.ahs.util.*;
import us.exultant.ahs.anno.*;

public class BeardUtil {
	/**
	 * Produces a string from a byte array that can be embedded directly in an HTML
	 * document. An image encoded in this way can be used as the "{@code src}"
	 * parameter to an {@code <img>} tag, for example. See <a
	 * href="https://en.wikipedia.org/wiki/Data_URI_scheme">Data_URI_scheme</a> in
	 * Wikipedia for more information and examples of using this in a web document.
	 * 
	 * @param $mime
	 *                the MIME type to mark the data as. "image/png" or "text/plain"
	 *                are valid examples.
	 * @param $data
	 *                the binary data to encode.
	 * @return a string resembling "data:$mime;base64,b64encode($data)"
	 */
	public static String formatDataForEmbed(String $mime, byte[] $data) {
		return formatDataForEmbed(new StringBuilder(), $mime, $data).toString();
	}
	
	/**
	 * Exactly as per {@link #formatDataForEmbed(String,byte[])}. {@code $buffer} is
	 * used to store the output.
	 */
	@ChainableInvocation
	public static StringBuilder formatDataForEmbed(StringBuilder $buffer, String $mime, byte[] $data) {
		// data:application/x-oleobject;base64, ...base64 data...
		$buffer.append("data:").append($mime).append(";base64,");
		$buffer.append(Base64.encode($data));
		return $buffer;
	}
}
