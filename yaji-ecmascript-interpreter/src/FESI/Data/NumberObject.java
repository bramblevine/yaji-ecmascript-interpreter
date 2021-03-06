// NumberObject.java
// FESI Copyright (c) Jean-Marc Lugrin, 1999
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

import java.text.NumberFormat;

import FESI.Exceptions.EcmaScriptException;
import FESI.Exceptions.ProgrammingError;
import FESI.Exceptions.RangeError;
import FESI.Exceptions.TypeError;
import FESI.Interpreter.Evaluator;

/**
 * Implemements the EcmaScript Number singleton.
 */
public class NumberObject extends BuiltinFunctionObject {
    private static final long serialVersionUID = -4773796736269945208L;

    private NumberObject(ESObject prototype, Evaluator evaluator) throws EcmaScriptException {
        super(prototype, evaluator, "Number", 1);
    }

    // overrides
    @Override
    public String toString() {
        return "<Number>";
    }

    // overrides
    @Override
    public ESValue callFunction(ESValue thisObject, ESValue[] arguments)
            throws EcmaScriptException {
        if (arguments.length == 0) {
            return ESNumber.valueOf(0.0);
        }
        return arguments[0].toESNumber();

    }

    // overrides
    @Override
    public ESObject doConstruct(ESValue[] arguments)
            throws EcmaScriptException {
        NumberPrototype theObject = null;
        ESObject np = getEvaluator().getNumberPrototype();
        theObject = new NumberPrototype(np, getEvaluator());
        if (arguments.length > 0) {
            theObject.value = ESNumber.valueOf(arguments[0].toESNumber().doubleValue());
        } else {
            theObject.value = ESNumber.valueOf(0.0);
        }
        return theObject;
    }


    // For numberPrototype
    private static abstract class NumberPrototypeFunctionObject extends BuiltinFunctionObject {
        private static final long serialVersionUID = -1279666619140046881L;

        public NumberPrototypeFunctionObject(FunctionPrototype fp, Evaluator evaluator, String name, int length) throws EcmaScriptException {
            super(fp,evaluator,name,length);
        }
        
        @Override
        public ESValue callFunction(ESValue thisObject, ESValue[] arguments)
                throws EcmaScriptException {
            return invoke((NumberPrototype)thisObject, arguments);
        }

        protected abstract ESValue invoke(NumberPrototype thisObject, ESValue[] arguments) throws EcmaScriptException;
    }
    
    private static class NumberPrototypeToString extends BuiltinFunctionObject {
        private static final long serialVersionUID = 1L;

        NumberPrototypeToString(String name, Evaluator evaluator,
                FunctionPrototype fp) throws EcmaScriptException {
            super(fp, evaluator, name, 1);
        }

        @Override
        public ESValue callFunction(ESValue thisObject,
                ESValue[] arguments) throws EcmaScriptException {
            if ( ! (thisObject instanceof ESNumber) 
                    && ! (thisObject instanceof NumberPrototype)) {
                throw new TypeError("Cannot apply Number.prototype.toString to an object which is not a Number");
            }
            ESValue radix = getArg(arguments,0);
            String s;
            if (radix.getTypeOf() != EStypeUndefined) {
                double d = radix.toInteger();
                if (d < 2 || d > 36) {
                    throw new RangeError("Number.prototype.toString(radix) : radix must be between 2 and 36 inclusive");
                }
                double doubleValue = thisObject.doubleValue();
                if (!Double.isNaN(doubleValue) && !Double.isInfinite(doubleValue)) {
                    s = Long.toString(((long) doubleValue), (int) d);
                } else {
                    s = thisObject.toString();
                }
            } else {
                s = thisObject.toString();
            }
            return new ESString(s);
        }
    }
    
    private static class NumberPrototypeToLocaleString extends BuiltinFunctionObject {
        private static final long serialVersionUID = 1L;

        NumberPrototypeToLocaleString(String name, Evaluator evaluator,
                FunctionPrototype fp) throws EcmaScriptException {
            super(fp, evaluator, name, 1);
        }

        @Override
        public ESValue callFunction(ESValue thisObject, ESValue[] arguments) throws EcmaScriptException {
            double d = thisObject.doubleValue();
            String string = NumberFormat.getNumberInstance(getEvaluator().getDefaultLocale()).format(d);
            return new ESString(string);
        }
    }
    
    private static class NumberPrototypeToExponential extends BuiltinFunctionObject {
        private static final long serialVersionUID = 1L;

        NumberPrototypeToExponential(String name, Evaluator evaluator,
                FunctionPrototype fp) throws EcmaScriptException {
            super(fp, evaluator, name, 1);
        }

        @Override
        public ESValue callFunction(ESValue thisObject,
                ESValue[] arguments) throws EcmaScriptException {
            if (! (thisObject instanceof NumberPrototype) ) {
                throw new TypeError("toPrecision can only be applied to Numbers");
            }
            return new ESString(((NumberPrototype)thisObject).toExponential(getArg(arguments,0)));
        }
    }
    
    private static class NumberPrototypeValueOf extends BuiltinFunctionObject {
        private static final long serialVersionUID = 1L;

        NumberPrototypeValueOf(String name, Evaluator evaluator,
                FunctionPrototype fp) throws EcmaScriptException {
            super(fp, evaluator, name, 1);
        }

        @Override
        public ESValue callFunction(ESValue thisObject,
                ESValue[] arguments) throws EcmaScriptException {
            if (thisObject instanceof ESNumber) {
                return thisObject;
            }
            if (thisObject instanceof NumberPrototype) {
                return ((NumberPrototype) thisObject).value;
            }
            throw new TypeError("Cannot apply Number.prototype.valueOf to an object which is not a Number");
        }
    }
    
    private static class NumberPrototypeToPrecision extends NumberPrototypeFunctionObject {
        private static final long serialVersionUID = 1L;

        NumberPrototypeToPrecision(String name, Evaluator evaluator,
                FunctionPrototype fp) throws EcmaScriptException {
            super(fp, evaluator, name, 1);
        }

        @Override
        public ESValue invoke(NumberPrototype thisNumber,
                ESValue[] arguments) throws EcmaScriptException {
            double d = thisNumber.doubleValue();
            ESValue precision = getArg(arguments,0);
            String s;
            if (Double.isNaN(d) || Double.isInfinite(d) || precision.getTypeOf() == EStypeUndefined) {
                s = thisNumber.toString();
            } else {
                int p = precision.toInt32();
                if (p<1 || p > 21) {
                    throw new RangeError("precision should be in range 1-21");
                }
                s = thisNumber.toString(p);
            }
            return new ESString(s);
        }
    }

    private static class NumberPrototypeToFixed extends NumberPrototypeFunctionObject {
        private static final long serialVersionUID = 1L;

        NumberPrototypeToFixed(String name, Evaluator evaluator,
                FunctionPrototype fp) throws EcmaScriptException {
            super(fp, evaluator, name, 1);
        }

        @Override
        public ESValue invoke(NumberPrototype thisNumber,
                ESValue[] arguments) throws EcmaScriptException {
            double fractionDigits = getArg(arguments,0).toInteger();
            if (fractionDigits < 0 || fractionDigits > 20) {
                throw new RangeError("fractionDigits should be in range 0-20");
            }
            return new ESString(thisNumber.toFixed((int) fractionDigits));
        }
    }
    
    /**
     * Utility function to create the single Number object
     * 
     * @param evaluator
     *            the Evaluator
     * @param objectPrototype
     *            The Object prototype attached to the evaluator
     * @param functionPrototype
     *            The Function prototype attached to the evaluator
     * 
     * @return the Number singleton
     * @throws EcmaScriptException 
     */
    public static NumberObject makeNumberObject(Evaluator evaluator,
            ObjectPrototype objectPrototype, FunctionPrototype functionPrototype) throws EcmaScriptException {

        NumberPrototype numberPrototype = new NumberPrototype(objectPrototype,
                evaluator);
        NumberObject numberObject = new NumberObject(functionPrototype,
                evaluator);

        try {
            

            numberObject.putProperty(StandardProperty.PROTOTYPEstring, 0, numberPrototype);
            numberObject.putProperty("length", 0, ESNumber.valueOf(1));
            numberObject.putProperty("MAX_VALUE", 0, ESNumber.valueOf(Double.MAX_VALUE));
            numberObject.putProperty("MIN_VALUE", 0, ESNumber.valueOf(Double.MIN_VALUE));
            numberObject.putProperty("NaN", 0, ESNumber.valueOf(Double.NaN));
            numberObject.putProperty("NEGATIVE_INFINITY", 0, ESNumber.valueOf(Double.NEGATIVE_INFINITY));
            numberObject.putProperty("POSITIVE_INFINITY", 0, ESNumber.valueOf(Double.POSITIVE_INFINITY));

            numberPrototype.putHiddenProperty("constructor", numberObject);
            numberPrototype.putHiddenProperty("toString",
                    new NumberPrototypeToString("toString", evaluator,
                            functionPrototype));
            numberPrototype.putHiddenProperty("valueOf",
                    new NumberPrototypeValueOf("valueOf", evaluator,
                            functionPrototype));
            numberPrototype.putHiddenProperty("toPrecision",
                    new NumberPrototypeToPrecision("toPrecision", evaluator,
                            functionPrototype));
            numberPrototype.putHiddenProperty("toFixed",
                    new NumberPrototypeToFixed("toFixed", evaluator,
                            functionPrototype));
            numberPrototype.putHiddenProperty("toExponential",
                    new NumberPrototypeToExponential("toExponential", evaluator,
                            functionPrototype));
            numberPrototype.putHiddenProperty("toLocaleString",
                    new NumberPrototypeToLocaleString("toLocaleString", evaluator,
                            functionPrototype));
        } catch (EcmaScriptException e) {
            e.printStackTrace();
            throw new ProgrammingError(e.getMessage());
        }

        evaluator.setNumberPrototype(numberPrototype);

        return numberObject;
    }
}
