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
 *
 */
@Slf4j
@ToString(includeNames = true, includePackage = false, includeSuper = true)
@CompileStatic
class Place  extends PNObject {

    public static final int UNLIMITED = -1;

    int tokens = 0;
    int maxTokens = UNLIMITED;

    public boolean hasEnoughTokens(int threshold) {
        log.trace "hasEnoughTokens(threshold: $threshold) - $this"
        return (tokens >= threshold);
    }

    public boolean maxTokensReached(int newTokens) {
        log.trace "maxTokensReached(newTokens: $newTokens) - $this"
        if (hasUnlimitedTokens()) {
            return false;
        }
        return (tokens+newTokens > maxTokens);
    }

    private boolean hasUnlimitedTokens() {
        return maxTokens == UNLIMITED;
    }

    public Place withTokens(int initial) {
        tokens = initial
        return this
    }
        
    public void addTokens(int weight) {
        this.tokens += weight;
    }

    public void removeTokens(int weight) {
        this.tokens -= weight;
    }
}
