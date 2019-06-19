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

import org.ASUX.common.Tuple;
import org.ASUX.common.Output.OutputType;
import org.ASUX.common.Output;
import org.ASUX.common.Debug;

import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.InputStream;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.junit.Test;
import static org.junit.Assert.*;

/**
/**
 * <p> This org.ASUX.yaml GitHub.com project and the <a href="https://github.com/org-asux/org.ASUX.cmdline">org.ASUX.cmdline</a> GitHub.com projects.
 * </p>
 * <p> This is technically an independent class, but it is semantically a 'SUPER/PARENT' class of org.ASUX.yaml.CollectionsImpl.Cmd and org.ASUX.yaml.NodeImpl.Cmd</p>
 * <p> Those two subclasses helps process YAML files using (either the java.util Collections classes, by leveraging the EsotericSoftware's YamlBeans library, or the SnakeYaml library respectively).</p>
 * <p> This class is the "wrapper-processor" for the various "YAML-commands" (which traverse a YAML file to do what you want).</p>
 *
 * <p> The 4 YAML-COMMANDS are: <b>read/query, list, delete</b> and <b>replace</b>. </p>
 * <p> See full details of how to use these commands - in this GitHub project's wiki<br>
 * - or - in<br>
 * <a href="https://github.com/org-asux/org.ASUX.cmdline">org.ASUX.cmdline</a> GitHub.com project and its wiki.
 * </p>
 *
 * <p>
 * Example:
 * <code>java org.ASUX.yaml.Cmd --delete --yamlpath "paths.*.*.responses.200" -i $cwd/src/test/my-petstore-micro.yaml -o /tmp/output2.yaml  --double-quote</code><br>
 * Example: <b><code>java org.ASUX.yaml.Cmd</code></b> will show all command
 * line options supported.
 * </p>
 * 
 * @see org.ASUX.yaml.YAMLPath
 * @see org.ASUX.yaml.CmdLineArgs
 */
public class Cmd {

    public static final String CLASSNAME = Cmd.class.getName();

    // private static final String TMP FILE = System.getProperty("java.io.tmpdir") +"/org.ASUX.yaml.STDOUT.txt";

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /**
     * This is NOT testing code. It's actual means by which user's command line arguments are read and processed
     * @param args user's commandline arguments
     */
    public static void main( String[] args )
    {
        final String HDR = CLASSNAME +": main(): ";
        CmdLineArgsBasic cmdLineArgsBasic = null;
        final java.io.StringWriter stdoutSurrogate = new java.io.StringWriter();

        try {
            cmdLineArgsBasic = new CmdLineArgsBasic( args );
            if ( cmdLineArgsBasic.verbose )  System.out.println( HDR +"arguments ="+ cmdLineArgsBasic );

            String classNameStr = null;
            if ( YAML_Libraries.isCollectionsImpl( cmdLineArgsBasic.YAMLLibrary ) ) {
                classNameStr = "org.ASUX.yaml.CollectionsImpl.Cmd";
            } else if ( YAML_Libraries.isNodeImpl( cmdLineArgsBasic.YAMLLibrary ) ) {
                classNameStr = "org.ASUX.YAML.NodeImpl.Cmd";
            }
            assertTrue( classNameStr != null ); // :-) I'm relying on YAML_library ENUM-class to make sure this ass-ert does NOT throw

            //--------------------------------
            // returns: protected Class<?> -- throws ClassNotFoundException
            final Class<?> implClass = Cmd.class.getClassLoader().loadClass(classNameStr);
            // findClass() method of ClassLoader is NOT VISIBLE - its a protected method.
            // The findClass() method searches for the class in the current class loader, if the class wasn't found by the parent class loader.
            // i.e., findClass() will be invoked by loadClass(), after checking the parent class loader for the requested class.
            if ( cmdLineArgsBasic.verbose )  System.out.println( HDR +"classNameStr=["+classNameStr+"] successfully loaded using ClassLoader.");

            //--------------------------------
            // First check to see if a static method called Run(/* no parameters */) is defined.
            // If not, then try run( date, String, CrontabEntry, Timer )...
            final Class[] paramClassList = { String[].class };
            final Object[] methodArgs = { args };
            org.ASUX.common.GenericProgramming.invokeStaticMethod( implClass, "main", paramClassList, methodArgs );
            if ( cmdLineArgsBasic.verbose ) System.out.println( HDR +"returned from successfully invoking "+classNameStr+".main().");

        } catch ( org.apache.commons.cli.ParseException pe ) {
            // ATTENTION: If CmdLineArgs.java  and its subclasses threw an ParseException, they'll catch it themselves, showHelp(), and write debug output.
            // so.. do NOTHING in this class (Cmd.java)
            System.exit(9);
        } catch (ClassNotFoundException e2) {
            e2.printStackTrace(System.err); // main() unit-testing
            System.err.println( "\n\nInternal Error!\t"+ HDR +" error(ClassNotFoundException): '" + cmdLineArgsBasic + "'.");
            System.exit(6);
        } catch (Exception e) {
            e.printStackTrace(System.err); // main() unit-testing
            System.err.println( "\n\nInternal Error!\t"+ HDR +" for '" + cmdLineArgsBasic + "'.");
            System.exit(6);
        } catch (Throwable t) {
            t.printStackTrace(System.err); // main() unit-testing
            System.err.println( "\n\nInternal Error!\t"+ HDR +" for '" + cmdLineArgsBasic + "'.");
            System.exit(6);
        }

    }

}
