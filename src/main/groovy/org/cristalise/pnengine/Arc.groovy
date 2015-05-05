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
 * Arc represents the connection between Place and Transition. The Arc has direction (e.g. Place to Transition)
 * and weight, i.e. how many tokens it consumes/produces from/to a Place.
 */
@Slf4j
@CompileStatic
@ToString(includeNames = true, includePackage = false, includeSuper = true)
class Arc extends PNObject {

    Direction direction;

    int placeIndex;
    int transIndex;
    int weight = 1;

    /**
     * Enumeration for the direction of the Arc
     */
    public enum Direction { 
        Place2Trans, 
        P2T,          //shorter form for Place2Trans
        Trans2Place, 
        T2P           //shorter from of Trans2Place
    }

    /**
     * Returns the shortName of the Arc, e.g. t1p1. Depending of the direction it is the shortName of the Place
     * concatenated with the shortName of the Transition
     * 
     * @return the shortName of the Arc
     */
    public String shortName() {
        switch (direction) {
            case Direction.Place2Trans:
            case Direction.P2T:
                return "p"+placeIndex+"t"+transIndex;
                break
                
            case Direction.Trans2Place: 
            case Direction.T2P:
                return "t"+transIndex+"p"+placeIndex;
                break
                
            default:
                throw new IllegalArgumentException("Unhandled enum value of Arc.Direction:" +direction);
        }
    }
    
    /**
     * Calculates if the Arc can fire or not. The logic is dependent of its Direction.
     * 
     * @return whether the Arc can fire or not
     */
    public boolean canFire() {
        log.trace "canFire() - $this";

        switch (direction) {
            case Direction.Place2Trans:
            case Direction.P2T:
                return parent.places["p"+placeIndex].hasEnoughTokens(weight);
                break

            case Direction.Trans2Place: 
            case Direction.T2P:
                return ! parent.places["p"+placeIndex].maxTokensReached(weight);
                break

            default:
                throw new IllegalArgumentException("Unhandled enum value of Arc.Direction:" +direction);
        }
    }

    /**
     * Fire the Arc
     */
    public void fire() {
        log.trace "fire() - $this";

        switch (direction) {
            case Direction.Place2Trans: 
            case Direction.P2T:
                parent.places["p"+placeIndex].removeTokens(weight)
                break

            case Direction.Trans2Place:
            case Direction.T2P:
                parent.places["p"+placeIndex].addTokens(weight)
                break
    
            default:
                throw new IllegalArgumentException("Unhandled enum value of Arc.Direction:" +direction);
        }
    }
}
