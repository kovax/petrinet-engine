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

import groovy.json.JsonBuilder
import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import org.cristalise.pnengine.Arc.Direction

/**
 * @author kovax
 *
 */
@Slf4j
@Canonical
@CompileStatic
class PetriNet extends PNObject {

    List<Place>        places      = new ArrayList<Place>();
    List<Transition>   transitions = new ArrayList<Transition>();
    List<Arc>          arcs        = new ArrayList<Arc>();
    List<InhibitorArc> inhibitors  = new ArrayList<InhibitorArc>();
    
    PNObject cache = null
    
    def print() { println new JsonBuilder(this).toPrettyString() }

    public static PetriNet petrinet(String name, Closure cl) {
        def pn = new PetriNet(name: name)

        cl.delegate = pn
        cl()
        
        return pn
    }

    public List<Transition> getTransitionsAbleToFire() {
        ArrayList<Transition> list = new ArrayList<Transition>();
        for (Transition t : transitions) {
            if (t.canFire()) {
                list.add(t);
            }
        }
        return list;
    }
    
    public Transition transition(String name) {
        Transition t = new Transition(name: name);
        transitions.add(t);
        return t;
    }
    
    public Place place(String name) {
        Place p = new Place(name: name);
        places.add(p);
        return p;
    }

    public Place place(String name, int initial) {
        Place p = new Place(name: name, tokens: initial);
        places.add(p);
        return p;
    }
    
    public Arc arc(String name, Place p, Transition t) {
        log.debug("Arc $name - direction ${Direction.Place2Transition}")
        Arc arc = new Arc(name: name, place: p, transition: t, direction: Direction.Place2Transition);
        t.addIncoming(arc);
        arcs.add(arc);
        return arc;
    }

    public Arc arc(String name, Transition t, Place p) {
        log.debug("Arc $name - direction ${Direction.Transition2Place}")
        Arc arc = new Arc(name: name, place: p, transition: t, direction: Direction.Transition2Place);
        t.addOutgoing(arc);
        arcs.add(arc);
        return arc;
    }
    
    public InhibitorArc inhibitor(String name, Place p, Transition t) {
        log.debug("Inhibitor Arc $name - direction ${Direction.Transition2Place}")
        InhibitorArc i = new InhibitorArc(name: name, place: p, transition: t, direction: Direction.Place2Transition);
        inhibitors.add(i);
        return i;
    }
    
    public PetriNet connect(Place p) {
        cache = p
        return this
    }

    public PetriNet connect(Transition p) {
        cache = p
        return this
    }

    public Arc to(Transition t) {
        if(cache != null && cache instanceof Place) {
            String arcName = cache.name + t.name 
            Arc a = arc(arcName, (Place)cache, t)
            cache = null
            return a
        }
        else {
            log.error("Call connect(Place) before caling to(Transition)")
            cache = null
            throw new RuntimeException()
        }
    }

    public Arc to(Place p) {
        if(cache != null && cache instanceof Transition) {
            String arcName = cache.name + p.name 
            Arc a = arc(arcName, (Transition)cache, p)
            cache = null
            return a
        }
        else {
            log.error("Call connect(Transition) befor calling to(Place)")
            cache = null
            throw new RuntimeException()
        }
    }
}
