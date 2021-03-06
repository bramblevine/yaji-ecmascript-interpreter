// ESUndefined.java
// FESI Copyright (c) Jean-Marc Lugrin, 1999
// Advanced FESI Copyright (c) Graham Technology, 2002
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2 of the License, or (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.

// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

package FESI.Data;

import java.io.ObjectStreamException;

import FESI.Exceptions.EcmaScriptException;
import FESI.Exceptions.TypeError;
import FESI.Interpreter.Evaluator;

/**
 * Implements the Undefined primitive value.
 * <P>
 * There is a single Undefined value reached by ESUndefined.theUndefined
 * <P>
 * The primitive Undefined, null and the java null are not equivallent and must
 * be used in the appropriate context
 */
public final class ESUndefined extends ESPrimitive {

    /**
     * the READ-ONLY undefined value
     */
    public static ESUndefined theUndefined = new ESUndefined();

    private ESUndefined() {
        // do nothing
    }

    /**
     * Implements a specific error message if an undfined value is called
     * 
     * @param thisObject
     *            The object on which the call is made
     * @param arguments
     *            The arguments of the function
     * @exception EcmaScriptException
     *                Thrown to indicate call on undefined value
     * @return never
     */
    @Override
    public ESValue callFunction(ESValue thisObject, ESValue[] arguments)
            throws EcmaScriptException {
        throw new TypeError(
                "Function called on undefined value or property");
    }

    /**
     * Implements a specific error message if an undfined value is called via
     * new
     * @param arguments
     *            The arguments of the function
     * 
     * @exception EcmaScriptException
     *                Thrown to indicate call on undefined value
     * @return never
     */
    @Override
    public ESObject doConstruct(ESValue[] arguments)
            throws EcmaScriptException {
        throw new TypeError(
                "'new' called on undefined value or property");
    }

    // overrides
    @Override
    public String toDetailString() {
        return "ES:<undefined>";
    }

    // overrides
    @Override
    public int getTypeOf() {
        return EStypeUndefined;
    }

    // overrides
    @Override
    public String getTypeofString() {
        return "undefined";
    }

    // overrides
    @Override
    public String toString() {
        return "undefined";
    }

    // overrides
    @Override
    public double doubleValue() {
        return Double.NaN;
    }

    // overrides
    @Override
    public boolean booleanValue() {
        return false;
    }

    // overrides
    @Override
    public Object toJavaObject() {
        return null; // should throw an error
    }

    /**
     * Advanced FESI GT Modified: 5/10/2002 Serialisation of ESUndefined
     * objects. writeReplace() and readResolve ensure that the reinstantiated
     * ESUndefined will be the ESUndefined.theUndefined object
     * 
     * @throws ObjectStreamException
     */
    public Object writeReplace() throws ObjectStreamException {
        return new ESUndefinedReplace();
    }

    private static class ESUndefinedReplace implements java.io.Serializable {
        private static final long serialVersionUID = -1475304067161497230L;

        ESUndefinedReplace() {
            // do nothing
        }

        /**
         * @throws ObjectStreamException
         */
        public Object readResolve() throws ObjectStreamException {
            return ESUndefined.theUndefined;
        }
    }

    @Override
    public boolean equals(Object obj) {
        // we mean this - there is only one undefined
        return super.equals(obj);
    }
    
    /**
     * Advanced FESI GT Modified: 12/10/2004 Support for subtypes (storing
     * values in hashset)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equalsSameType(ESValue v2) {
        return true;
    }

    @Override
    boolean isObjectCoercible() {
        return false;
    }

    @Override
    public ESObject toESObject(Evaluator evaluator) throws EcmaScriptException {
        throw new TypeError("'undefined' cannot be converted to Object");
    }
    
    @Override
    protected boolean sameValueTypeChecked(ESValue other) {
        return true;
    }
}
