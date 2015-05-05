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

import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.util.logging.Slf4j


/**
 * Place holds a number of tokens
 */
@Slf4j
@ToString(includeNames = true, includePackage = false, includeSuper = true)
@CompileStatic
class Place  extends PNObject {

    public static final int UNLIMITED = -1;

    /**
     * The actual number of tokens the Place holds
     */
    int tokens = 0;
    
    /**
     * The maximum number of tokens the Place can hold
     */
    int maxTokens = UNLIMITED;

    /**
     * Returns the shortName of the Place e.g. p1 ("p"+index)
     */
    public String shortName() {
        return "p$index"
    }
    
    /**
     * Required to calculate if a Transition can fire or not. Check {@link org.cristalise.pnengine.Arc#canFire()}
     * 
     * @param threshold
     * @return
     */
    public boolean hasEnoughTokens(int threshold) {
        log.trace "hasEnoughTokens(threshold: $threshold) - $this"
        return (tokens >= threshold);
    }

    /**
     * Required to calculate if a Transition can fire or not. Check 
     * {@link org.cristalise.pnengine.Arc#canFire()}
     * 
     * @param newTokens
     * @return
     */
    public boolean maxTokensReached(int newTokens) {
        log.trace "maxTokensReached(newTokens: $newTokens) - $this"
        if (maxTokens == UNLIMITED) {
            return false;
        }
        return (tokens+newTokens > maxTokens);
    }

    /**
     * Sets the initial number tokens
     * 
     * @param initial the initial number tokens
     * @return this Place to use this as a fluent API
     */
    public Place withTokens(int initial) {
        tokens = initial
        return this
    }

    /**
     * Add the number if tokens when the Transition has fired. Check {@link org.cristalise.pnengine.Arc#fire()}
     * 
     * @param weight
     */
    public void addTokens(int weight) {
        tokens += weight;
    }

    /**
     * Remove the number of tokens when the Transition has fired. Check {@link org.cristalise.pnengine.Arc#fire()}
     * 
     * @param weight
     */
    public void removeTokens(int weight) {
        if(tokens - weight < 0) throw new RuntimeException("Place ${shortName()} has not got enough tokens (tokens:$tokens < weight: $weight)")
        tokens -= weight;
    }
}
