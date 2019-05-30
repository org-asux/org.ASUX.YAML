/*
 BSD 3-Clause License
 
 Copyright (c) 2019, Udaybhaskar Sarma Seetamraju
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 
 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.
 
 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.
 
 * Neither the name of the copyright holder nor the names of its
 contributors may be used to endorse or promote products derived from
 this software without specific prior written permission.
 
 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.ASUX.yaml;

import static org.junit.Assert.*;

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
/**
 * This enum class is a bit extensive, only because the ENNUMERATED VALUEs are strings.
 * For variations - see https://stackoverflow.com/questions/3978654/best-way-to-create-enum-of-strings
 */
public final class Enums
{
    public static final String CLASSNAME = Enums.class.getName();

    public enum ScalarStyle {
        DOUBLE_QUOTED('"'),
        SINGLE_QUOTED('\''),
        LITERAL( '|'),
        FOLDED('>'),
        PLAIN(null);

        public static final String CLASSNAME = Enums.class.getName();

        private Character styleChar;

        private ScalarStyle(Character style) { this.styleChar = style; }

        public Character getChar() { return styleChar; }

        public static ScalarStyle fromChar(Character style) throws Exception {
            if (style == null) {
                return PLAIN;
            } else {
                switch (style) {
                case '"':   return DOUBLE_QUOTED;
                case '\'':  return SINGLE_QUOTED;
                case '|':   return LITERAL;
                case '>':   return FOLDED;
                default:    throw new Exception( CLASSNAME +": ScalarStyle: Unknown scalar style character: [" + style +"]" );
                }
            }
        }

        //============================================================

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() { return (styleChar==null) ? "null" : styleChar.toString();  }

        //============================================================
        /**
         * Given a string, this method will help convert the string into the standard ENUM values of this class.  If, invalid input.. an exception is thrown.
         * @param type a string value that should be one of com.esotericsoftware.yamlbeans org.yaml.snakeyaml.Yaml org.ASUX.yaml
         * @return a valid ENUM value of this class
         * @throws Exception if string parameter is invalid
         */
        public static ScalarStyle fromString(String type) throws Exception {
            for (ScalarStyle typeitem : ScalarStyle.values()) {
                if (typeitem.toString().equals(type)) {
                    return typeitem;
                }
            }
            throw new Exception ( CLASSNAME + ": fromString("+ type +"): should be one of the values: "+ list("\t") );
            // return ScalarStyle.SNAKEYAML_Library; // Default.. or you can throw exception
        }

        //============================================================
        /**
         * Use this method to define your REGEXPRESSIONS by providing a '|' as delimiter.
         * @param _delim any string you want
         * @return the valid values (com.esotericsoftware.yamlbeans org.yaml.snakeyaml.Yaml org.ASUX.yaml) separated by the delimiter
         */
        public static String list( final String _delim ) {
            // return ""+ ESOTERICSOFTWARE_Library +_delim+ SNAKEYAML_Library +_delim+ ASUXYAML_Library;
            String retval = "";
            for (ScalarStyle typeitem : ScalarStyle.values()) {
                retval += typeitem.toString() + _delim;
            }
            return retval;
        }

    } // Enum ScalarStyle

};
