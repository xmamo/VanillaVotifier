/* 
 * Copyright (C) 2015 Matteo Morena
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package co.virtualdragon.vanillaVotifier;

import co.virtualdragon.vanillaVotifier.event.Event;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface Server {

	void start() throws IOException, GeneralSecurityException;

	void stop() throws IOException;

	boolean isRunning();

	List<Listener> getListeners();

	void notifyListeners(Event event);
}
