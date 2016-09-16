/*
 * Copyright (C) 2016  Matteo Morena
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package mamo.vanillaVotifier.utils;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map.Entry;

public class SubstitutionUtils {
	public static StrSubstitutor buildStrSubstitutor(@Nullable Entry<String, Object>... substitutions) {
		HashMap<String, Object> substitutionsMap = new HashMap<String, Object>();
		if (substitutions != null) {
			for (Entry<String, Object> substitution : substitutions) {
				if (substitution.getValue() != null) {
					if (!(substitution.getValue() instanceof Throwable)) {
						substitutionsMap.put(substitution.getKey(), substitution.getValue());
					} else {
						substitutionsMap.put(substitution.getKey(), ExceptionUtils.getStackTrace((Throwable) substitution.getValue()));
					}
				} else {
					substitutionsMap.put(substitution.getKey(), "");
				}
			}
		}
		return new StrSubstitutor(substitutionsMap);
	}
}