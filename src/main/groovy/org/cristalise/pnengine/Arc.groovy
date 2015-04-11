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
package org.cristalise.pnengine

import groovy.transform.Canonical
import groovy.transform.CompileStatic

/**
 * @author kovax
 *
 */
@Canonical
@CompileStatic
class Arc extends PNObject {

    Place place;
    Transition transition;
    Direction direction;
    int weight = 1;

    enum Direction {
        Place2Transition {
            @Override
            public boolean canFire(Place p, int weight) {
                return p.hasEnoughTokens(weight);
            }

            @Override
            public void fire(Place p, int weight) {
                p.removeTokens(weight);
            }
        },

        Transition2Place {
            @Override
            public boolean canFire(Place p, int weight) {
                return ! p.maxTokensReached(weight);
            }

            @Override
            public void fire(Place p, int weight) {
                p.addTokens(weight);
            }
        };

        public abstract boolean canFire(Place p, int weight);
        public abstract void fire(Place p, int weight);
    }

    public boolean canFire() {
        return direction.canFire(place, weight);
    }

    public void fire() {
        this.direction.fire(place, this.weight);
    }
}
