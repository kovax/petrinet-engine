/**
 * This file is part of the CRISTAL-iSE kernel.
 * Copyright (c) 2001-2015 The CRISTAL Consortium. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 3 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; with out even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 *
 * http://www.fsf.org/licensing/licenses/lgpl.html
 */
package org.cristalise.pnengine;

import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.util.logging.Slf4j


/**
 *
 */
@Slf4j
@CompileStatic
@ToString(includeNames = true, includePackage = false, includeSuper = true)
class Arc extends PNObject {

    Direction direction;

    String placeName;
    int weight = 1;

    public enum Direction { Place2Trans, Trans2Place }

    public boolean canFire() {
        log.debug "canFire() - $this";

        switch (direction) {
            case Direction.Place2Trans:
                return parent.places[placeName].hasEnoughTokens(weight);
                break
                
            case Direction.Trans2Place: 
                return ! parent.places[placeName].maxTokensReached(weight);
                break
                
            default:
                throw new IllegalArgumentException("Unhandled enum value of Arc.Direction:" +direction);
        }
    }

    public void fire() {
        log.debug "fire() - $this";

        switch (direction) {
            case Direction.Place2Trans: 
                parent.places[placeName].removeTokens(weight)
                break

            case Direction.Trans2Place:
                parent.places[placeName].addTokens(weight)
                break
    
            default:
                throw new IllegalArgumentException("Unhandled enum value of Arc.Direction:" +direction);
        }
    }
}
