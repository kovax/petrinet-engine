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
import groovy.util.logging.Slf4j

import org.cristalise.pnengine.Arc.Direction

/**
 * @author kovax
 *
 */
@Slf4j
@CompileStatic
class PetriNet {
    
    String name

    Map<String, Place>      places      = [:]
    Map<String, Transition> transitions = [:]
    Map<String, Arc>        arcs        = [:]

    PNObject cache = null

//    public String printJson() { println new JsonBuilder(this).toPrettyString() }

    /**
     *     
     * @param pno
     */
    public void add(PNObject pno) {
        if(pno instanceof Place) {
            if(places.containsKey(pno.name)) throw new RuntimeException("Place '${pno.name}' already exists")
            places[pno.name] = (Place)pno
        }
        else if(pno instanceof Transition) {
            if(transitions.containsKey(pno.name)) throw new RuntimeException("Transition '${pno.name}' already exists")
            transitions[pno.name] = (Transition)pno
        }
        else if(pno instanceof Arc) {
            if(arcs.containsKey(pno.name)) throw new RuntimeException("Arc '${pno.name}' already exists")
            arcs[pno.name] = (Arc)pno
        }
    }


    public List<Transition> listOfTransitionsAbleToFire() {
        List<Transition> list = []

        transitions.values().each { if (it.canFire()) list.add(it) }

        return list;
    }
    

    public Transition transition(String name) {
        Transition t = new Transition(name: name);
        transitions[name] = t;
        return t;
    }

    
    public Place place(String name) {
        Place p = new Place(name: name);
        places[name] = p;
        return p;
    }


    public Place place(String name, int initial) {
        Place p = new Place(name: name, tokens: initial);
        places[name] = p;
        return p;
    }


    public Arc connect(Place p, Transition t, int weight = 1) {
        String arcName = p.name+t.name
        Arc a = new Arc(name: arcName, place: p, transition: t, weight: weight, direction: Direction.Place2Transition);
        log.debug("Adding: $a.name")
        t.addIncoming(a);
        arcs[arcName] = a;
        return a;
    }


    public Arc connect(Transition t, Place p, int weight = 1) {
        String arcName = t.name+p.name
        Arc a = new Arc(name: arcName, place: p, transition: t, weight: weight, direction: Direction.Transition2Place);
        log.debug("Adding: $a.name")
        t.addOutgoing(a);
        arcs[arcName] = a;
        return a;
    }
    
    
    /**
     * DSL method: Setup the Closure to create and fire a PN as 
     * 
     * @param name
     * @param cl
     * @return
     */
    public static PetriNet builder(String name, Closure cl) {
        def pn = new PetriNet(name: name)

        cl.delegate = pn
        cl()
        
        return pn
    }
    

    /**
     * DSL method: it should be used before to()
     * 
     * @param p
     * @return
     */
    public PetriNet connect(Map<String,String> pno) {
        if(pno.transition) {
            if(transitions.containsKey(pno.transition)) return connect(transitions[pno.transition])
            else                                        return connect(transition(pno.transition))
        }
        else if(pno.place) {
            if(places.containsKey(pno.place)) return connect(places[pno.place])
            else                              return connect(place(pno.place))
        }
        throw new RuntimeException("${pno} is unknow")
    }

    /**
     * DSL method: it should be used before to() 
     * 
     * @param p
     * @return
     */
    public PetriNet connect(Place p) {
        cache = p
        return this
    }


    /**
     * DSL method: should be used before to() 
     * 
     * @param t
     * @return
     */
    public PetriNet connect(Transition t) {
        cache = t
        return this
    }


    /**
     * DSL method: should be used after connect() 
     * 
     * @param p
     * @return
     */
    public Arc to(Map<String,String> pno) {
        if(pno.transition) {
            if(transitions.containsKey(pno.transition)) return to(transitions[pno.transition])
            else                                        return to(transition(pno.transition))
        }
        else if(pno.place) {
            if(places.containsKey(pno.place)) return to(places[pno.place])
            else                              return to(place(pno.place))
        }
        throw new RuntimeException("${pno} is unknow")
    }

        
    /**
     * DSL method: should be used after connect() 
     * 
     * @param p
     * @return
     */
    public Arc to(Transition t) {
        if(cache != null && cache instanceof Place) {
            Arc a = connect((Place)cache, t)
            cache = null
            return a
        }
        else {
            cache = null
            throw new RuntimeException("Call connect(Place) before caling to(Transition)")
        }
    }


    /**
     * DSL method: should be used after connect() 
     * 
     * @param p
     * @return
     */
    public Arc to(Place p) {
        if(cache != null && cache instanceof Transition) {
            Arc a = connect((Transition)cache, p)
            cache = null
            return a
        }
        else {
            cache = null
            throw new RuntimeException("Call connect(Transition) before calling to(Place)")
        }
    }
}
