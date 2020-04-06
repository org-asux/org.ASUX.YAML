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

import com.fasterxml.jackson.annotation.JsonProperty;

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
/**
 * This enum class is a bit extensive, only because the ENNUMERATED VALUEs are strings.
 * For variations - see https://stackoverflow.com/questions/3978654/best-way-to-create-enum-of-strings
 */
public final class Enums
{
    public static final String CLASSNAME = Enums.class.getName();

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    public enum ScalarStyle {

        @JsonProperty("\"")
        DOUBLE_QUOTED('"'),

        @JsonProperty("'")
        SINGLE_QUOTED('\''),

        @JsonProperty("|")
        LITERAL( '|'),

        @JsonProperty(">")
        FOLDED('>'),

        @JsonProperty("none")
        PLAIN(null),   // <<------- null !!!!!!!  <<------- null !!!!!!!  <<------- null !!!!!!!  <<------- null !!!!!!!  <<------- null !!!!!!!

        @JsonProperty("undefined")
        UNDEFINED('?');

        public static final String CLASSNAME = Enums.class.getName();

        private Character styleChar;

        ScalarStyle(Character style) { this.styleChar = style; }

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
            if ( type == null || type.trim().equals("") )
                return PLAIN;
            for (ScalarStyle typeitem : ScalarStyle.values()) {
                if (typeitem.toString().equals(type)) {
                    return typeitem;
                }
            }
            throw new Exception ( CLASSNAME + ": fromString("+ type +"): Value should be one of the values: "+ list("\t") );
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
            final StringBuilder retval = new StringBuilder();
            for (ScalarStyle typeitem : ScalarStyle.values()) {
                retval.append( typeitem.toString() ).append( _delim );
            }
            return retval.toString();
        }

    } // Enum ScalarStyle

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /** Class that captures the ennumeration of the various commands supported by the org.ASUX.YAML implementation
     */
    public enum CmdEnum {

        @JsonProperty("read")
        READ("read"),

        @JsonProperty("list")
        LIST("list"),

        @JsonProperty("delete")
        DELETE("delete"),

        @JsonProperty("insert")
        INSERT("insert"),

        @JsonProperty("replace")
        REPLACE("replace"),

        @JsonProperty("table")
        TABLE("table"),

        @JsonProperty("macro")
        MACRO("macro"),

        @JsonProperty("macroyaml")
        MACROYAML("macroyaml"),

        @JsonProperty("batch")
        BATCH("batch"),

        UNKNOWN("unknown");

        private final String internalValue;
        public static final String CLASSNAME =CmdEnum.class.getName();

        //=================================
        /** <p>This constructor is private.  the right way to create new objects of this enum are via the {@link #valueOf(String)}  or {@link #fromString}.</p>
         * <p>For Enums based on strings, you need a constructor like this.  Only reason: To save the parameter as an internal variable.</p>
         * <p>Per Java language spec, this constructor is private (as I understand it)</p>
         * @param _i NotNull String, that should ideally be converted via {@link #valueOf(String)}  or {@link #fromString}
         */
        CmdEnum( final String _i ) {
            this.internalValue = _i;
        }

        //=================================
        /** @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return this.internalValue;
        }

        //=================================
        /**
         * Given a string, this method will help convert the string into the standard ENUM values of this class.  If, invalid input.. an exception is thrown.
         * @param type a string value that should be one of com.esotericsoftware.yamlbeans org.yaml.snakeyaml.Yaml org.ASUX.yaml
         * @return a valid ENUM value of this class
         * @throws Exception if string parameter is invalid
         */
        public static CmdEnum fromString(String type) throws Exception {
            if ( type == null || type.trim().equals("") )
                return UNKNOWN;
            for (CmdEnum typeitem : CmdEnum.values()) {
                if (typeitem.toString().equals(type)) {
                    return typeitem;
                }
            }
            throw new Exception ( CLASSNAME + ": fromString("+ type +"): should be one of the values: "+ list("\t") );
            // return YAML_Libraries.SNAKEYAML_Library; // Default.. or you can throw exception
        }

        //=================================
        /**
         * Use this method to define your REGEXPRESSIONS by providing a '|' as delimiter.
         * @param _delim any string you want
         * @return the valid values (com.esotericsoftware.yamlbeans org.yaml.snakeyaml.Yaml org.ASUX.yaml) separated by the delimiter
         */
        public static String list( final String _delim ) {
            // return ""+ ESOTERICSOFTWARE_Library +_delim+ SNAKEYAML_Library +_delim+ ASUXYAML_Library;
            final StringBuilder retval = new StringBuilder();
            for (CmdEnum typeitem : CmdEnum.values()) {
                retval.append( typeitem.toString() ).append( _delim );
            }
            return retval.toString();
        }
    }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

}
