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

import java.io.Serializable;
import java.util.LinkedHashMap;

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
/**
 *  <p>This class exists to abstract between the YAML implementations, allowing us to switch YAML-implementations much easier.</p>
 *  <p>This class is also conveniently the "Factory" class also.<br>
 *      The factory methods are:</p>
 *  <ul>
 *      <li>{@link #use(YAML_Libraries, YAMLImplementation)}</li>
 *      <li>{@link #create(boolean, YAML_Libraries)}</li>
 *      <li>{@link #setDefaultYAMLImplementation(YAML_Libraries)}</li>
 *      <li>{@link #getDefaultYAMLImplementation()}</li>
 *  </ul>
 */
public abstract class YAMLImplementation<T> implements Serializable
{
    private static final long serialVersionUID = 160L;

    public static final String CLASSNAME = YAMLImplementation.class.getName();

    //========================== STATIC VARIABLES only =============================

    //=============== WARNING!!! Ensure subclases do NOT add Instance-variables ======================

    protected static final LinkedHashMap<String,YAMLImplementation<?> > implementations = new LinkedHashMap<>();

    protected final YAML_Libraries yamlImpl_type;
    protected static YAML_Libraries defaultImpl_type = YAML_Libraries.UNDEFINED;

    //-----------------------------
	public boolean verbose;

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    public YAMLImplementation( final boolean _verbose, final YAML_Libraries _yl ) {
        this.verbose = _verbose;
        this.yamlImpl_type = YAML_Libraries.normalize( _yl );
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /**
     * know which YAML-parsing/emitting library was chosen by user.  Ideally used within a Batch-Yaml script / BatchYamlProcessor.java
     * @return the YAML-library in use. See {@link YAML_Libraries} for legal values to this parameter
     */
    public YAML_Libraries getYAMLLibrary() {
        return this.yamlImpl_type;
    }

    // /**
    //  * Allows you to set the YAML-parsing/emitting library of choice.  Ideally used within a Batch-Yaml script.
    //  * @param _l the YAML-library to use going forward. See {@link YAML_Libraries} for legal values to this parameter
    //  */
    // public abstract void setYAMLLibrary( final YAML_Libraries _l );

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /**
     * Because this class is a Generic&lt;T&gt;, compiler (for good reason) will Not allow me to type 'o instanceof T'.  Hence I am delegating this simple condition-check to the sub-classes.
     * @param o the object to check if it is an instance of T
     * @return true if 'o instanceof T' else false.
     */
    public abstract boolean instanceof_YAMLImplClass( Object o );

    /**
     *  For SnakeYAML Library based subclass of this, simply return 'NodeTools.Node2YAMLString(tempOutput)'.. or .. for EsotericSoftware.com-based LinkedHashMap-based library, simply return 'tools.Map2YAMLString(tempOutputMap)'
     *  @param _o (nullable) either the SnakeYaml library's org.yaml.snakeyaml.nodes.Node ( as generated by SnakeYAML library).. or.. EsotericSoftware Library's preference for LinkedHashMap&lt;String,Object&gt;, -- in either case, this object contains the entire Tree representing the YAML file.
     *  @return a Non-Null String or throws an exception
     *  @throws Exception Any issue whatsoever when dealing with convering YAML/JSON content into Strings
     */
    public abstract String toStringDebug( Object _o ) throws Exception;

    /**
     *  For SnakeYAML Library based subclass of this, simply return 'NodeTools.getEmptyYAML( this.dumperoptions )' .. or .. for EsotericSoftware.com-based LinkedHashMap-based library, simply return 'new LinkedHashMap&lt;&gt;()'
     *  @return either the SnakeYaml library's org.yaml.snakeyaml.nodes.Node ( as generated by SnakeYAML library).. or.. EsotericSoftware Library's preference for LinkedHashMap&lt;String,Object&gt;, -- in either case, this object contains the entire Tree representing the YAML file.
     */
    public abstract T getEmptyYAML();

    /**
     *  If any of the Read/List/Replace/Table/Batch commands returned "Empty YAML" (assuming the code retured {@link #getEmptyYAML()}), this is your SIMPLEST way of checking if the YAML is empty.
     *  @param _n Nullable value
     *  @return true if the YAML is is NULL - or - is "empty"  as in, it is ===  what's returned by {@link #getEmptyYAML()})
     */
    public abstract boolean isEmptyYAML( final T _n );

    /**
     *  For SnakeYAML Library based subclass of this, simply return 'NodeTools.getNewScalarNode( newRootElem, "", this.dumperoptions )' .. or .. for EsotericSoftware.com-based LinkedHashMap-based library, simply return the argument (as is)
     *  @param _val NotNull string 
     *  @return either the SnakeYaml library's org.yaml.snakeyaml.nodes.Node ( as generated by SnakeYAML library).. or.. EsotericSoftware Library's java.lang.String object
     */
    public abstract T getNewScalarEntry( final String _val );

    /**
     *  For SnakeYAML Library based subclass of this, simply return 'NodeTools.getNewSingleMap( newRootElem, _valElemStr, this.dumperoptions )' .. or .. for EsotericSoftware.com-based LinkedHashMap-based library, simply return 'new LinkedHashMap&lt;&gt;.put( newRootElem, "" )'
     *  @param _newRootElemStr the string representing 'lhs' in "lhs: rhs" single YAML entry
     *  @param _valElemStr the string representing 'rhs' in "lhs: rhs" single YAML entry
     *  @return either the SnakeYaml library's org.yaml.snakeyaml.nodes.Node ( as generated by SnakeYAML library).. or.. EsotericSoftware Library's preference for LinkedHashMap&lt;String,Object&gt;, -- in either case, this object contains the entire Tree representing the YAML file.
     */
    public abstract T getNewSingleYAMLEntry( final String _newRootElemStr, final String _valElemStr );

    /**
     *  <p>Assumes that the Node 'YAML-tree' passed in actually either a simple ScalarNode, or a SequenceNode with just one ScalarNode.</p>
     *  <p>If its not a valid assumption, either an Exception or an Assertion-RuntimeException is thrown</p>
     *  @param _n a NotNull Node object
     *  @return a Nullable simple String
     *  @throws Exception logic inside method will throw if the right YAML-structure is not provided.
     */
    public abstract String getScalarContent( final T _n ) throws Exception;

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    /**
     *  <p>Example: For SnakeYAML-library based subclass of this, this should return DumperOptions.class</p>
     *  <p>This is to be used primarily within org.ASUX.yaml.BatchCmdProcessor#onAnyCmd().</p>
     *  @return name of class of the object that subclasses of {@link CmdInvoker} use, to configure YAML-Output (example: SnakeYAML uses DumperOptions)
     */
    public abstract Class<?> getLibraryOptionsClass();

    /**
     *  <p>Example: For SnakeYAML-library based subclass of this, this should return the reference to the instance of the class DumperOption</p>
     *  <p>This is to be used primarily within org.ASUX.yaml.BatchCmdProcessor#onAnyCmd().</p>
     * @return instance/object that subclasses of {@link CmdInvoker} use, to configure YAML-Output (example: SnakeYAML uses DumperOptions objects)
     */
    public abstract Object getLibraryOptionsObject();

    /**
     *  <p>Example: For SnakeYAML-library based subclass of this, this should return the reference to the instance of the class DumperOption</p>
     *  <p>This is to be used primarily within org.ASUX.yaml.BatchCmdProcessor#onAnyCmd().</p>
     * @param _o A NotNull instance/object NotNull reference, to configure YAML-Output (example: SnakeYAML uses DumperOptions objects)
     */
    public abstract void setLibraryOptionsObject( final Object _o );

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /**
     * This method will use the YAML-Library specified via {@link #getYAMLLibrary()} and load the YAML content (pointed to by the _inreader paramater).
     * @param _inreader either a StringReader or a FileReader
     * @return NotNull instance of T.  Even Empty-YAML will come back as NotNull.
     * @throws Exception if the YAML libraries have any issues with ERRORs inthe YAML or other issues.
     */
    public abstract T load( final java.io.Reader _inreader ) throws Exception;

    /**
     *  This method takes the java.io.Writer (whether StringWriter or FileWriter) and prepares the YAML library to write to it.
     *  @param _javawriter StringWriter or FileWriter (cannot be null)
     * @param _output the content you want written out as a YAML file.
     *  @throws Exception if the YAML libraries have any issues with ERRORs inthe YAML or other issues.
     */
    public abstract void write( final java.io.Writer _javawriter, final Object _output ) throws Exception;

    /**
     * Call this in exactly the way you'd close a file after writing to it.  This method should be called ONLY after {@link #write} will no longer be invoked.
     * @throws Exception if the YAML libraries have any issues with ERRORs inthe YAML or other issues.
     */
    public abstract void close() throws Exception;

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    public abstract YAMLImplementation<T> deepClone() throws Exception;

    //=================================================================================
    /**
     *  <p>This is one of the factory methods</p>
     *  <p>will 'initialize' the factory, for the specific YAML-implementation denoted by argument #1 (_YAMLLibrary)</p>
     *  @param <T> either SnakeYAML's Node.class or LinkedHashMap&lt;String,Object&gt; for EsotericSoftware's YAML implementation
     *  @param _YAMLLibrary see {@link org.ASUX.yaml.YAML_Libraries}
     *  @param _impl must be NotNull (or exception will be thrown).  NotNull implies a subclass of this class (as this class is abstract).
     *  @throws Exception if _YAMLLibrary == YAML_Libraries.UNDEFINED  || _impl == null
     */
    public static <T> void use( final YAML_Libraries _YAMLLibrary, final YAMLImplementation<T> _impl ) throws Exception
    {   final String HDR = CLASSNAME + ": use("+ _YAMLLibrary.toString() +",_imp): ";
        if ( _YAMLLibrary != YAML_Libraries.UNDEFINED  && _impl != null )
            YAMLImplementation.implementations.put( YAML_Libraries.normalize( _YAMLLibrary ).toString(), _impl );
        else
            throw new Exception( HDR + "!! Serious Internal Error !! either YAML_Libraries.UNDEFINED or _impl == null" );
    }

    //=================================================================================

    /**
     *  <p>This is one of the factory methods</p>
     *  <p>creates a new object representing the YAML-implementation as specified as the single-function-argument</p>
     *  @param <T> either SnakeYAML's Node.class or LinkedHashMap&lt;String,Object&gt; for EsotericSoftware's YAML implementation
     *  @param _verbose Whether you want deluge of debug-output onto System.out.
     *  @param _YAMLLibrary see {@link org.ASUX.yaml.YAML_Libraries}
     *  @return a NotNull instance of the implementation of this abstract class.  If no implementation is found, an Exception is thrown.  But, Null is NEVER returned.
     *  @throws Exception if the implementation is Not defined/ not initialized/ does Not exist (Basically, no code invoked)
     */
    public static <T> YAMLImplementation<T> create( final boolean _verbose, YAML_Libraries _YAMLLibrary ) throws Exception
    {   final String HDR = CLASSNAME + ": create("+ _YAMLLibrary.toString() +"): ";
        // As a factory method, we're supposed to create a NEW instance of YAMLImplementation - everytime.
        // As things stand, YAMLImplementation.java has NO data (No instance variables).
        // The plan is YAMLImplementation and it's subclasses will CONTINUE to NOT have instance-variables, that represent 'state' (as in can distinguish between instances of the same implementation)
        // So, we're going to return the same object over-n-over for the same argument passed to this method.
        final YAMLImplementation<?> implObj = YAMLImplementation.implementations.get( YAML_Libraries.normalize( _YAMLLibrary ).toString() );
        if ( implObj == null ) {
            if (_verbose) System.out.println( HDR +"Currently "+ YAMLImplementation.implementations.size() +" YAML-implementation available for use" );
            for( String key: YAMLImplementation.implementations.keySet() ) {
                if (_verbose) System.out.println( HDR +"YAML-IMplementation for "+ key + " is available/ready." );
            }
            throw new Exception( HDR + "No YAML-implementation found for what you are seeking." );
        }
        @SuppressWarnings("unchecked")
        final YAMLImplementation<T> impl = (YAMLImplementation<T>) implObj;
        return impl;
    }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /**
     *  <p>This is one of the factory methods</p>
     *  <p>Since this library is meant to be used by end-user providing cmd-line options, and since {@link org.ASUX.yaml.CmdLineArgsCommon} offers the user exactly that functionality, this method is to make it convenient for the code to say: <em>get me the YAML-implementation library that the user specified on the command line</em></p>
     *  @param _YAMLLibrary see {@link org.ASUX.yaml.YAML_Libraries}
     */
    public static void setDefaultYAMLImplementation( final YAML_Libraries _YAMLLibrary ) {
        defaultImpl_type = YAML_Libraries.normalize( _YAMLLibrary );
    }

    //=================================================================================

    /**
     *  <p>This is one of the factory methods</p>
     *  @param <T> either SnakeYAML's Node.class or LinkedHashMap&lt;String,Object&gt; for EsotericSoftware's YAML implementation
     *  @return a NotNull instance of the implementation of this abstract class.  If no implementation is found, an Exception is thrown.  But, Null is NEVER returned.
     *  @throws Exception if 'default' implementation was Not defined, or other coding errors
     */
    public static <T> YAMLImplementation<T> getDefaultYAMLImplementation() throws Exception {
        return YAMLImplementation.create( false /* verbose */, defaultImpl_type );
    }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /**
     *  <p>This starts up the "Factory" for the specified YAML-library-type (argument #1)</p>
     *  <p>This method enables "isolation of implementation" for NON-YAML libraries a.k.a. org.ASUX github-project (example: org.ASUX.AWS-SDK and org.ASUX.AWS.CFN projects), which need a YAML implementation</p>
     *  <p>Those other github-projects do NOT care about what the YAML implementation is.<br>
     *      But they will need to "startup" (in a generic way) the factory-classes for the YAML-implementation libraries and rely exclusively on "agnostic" org.ASUX.yaml github-project.</p>
     *  @param <T> either SnakeYAML's Node.class or LinkedHashMap&lt;String,Object&gt; for EsotericSoftware's YAML implementation
     *  @param _yamllibrary_type The YAML-library to use. See {@link org.ASUX.yaml.YAML_Libraries} for legal values to this parameter
     *  @param _cmdLineArgs NotNull instance of a subclass of {@link org.ASUX.yaml.CmdLineArgsCommon}
     *  @param _cmdInvoker  NotNull instance of a subclass of {@link org.ASUX.yaml.CmdInvoker}
     *  @return a NotNull reference to a subclass
     */
    public static <T> YAMLImplementation<T> startupYAMLImplementationFactory( final YAML_Libraries _yamllibrary_type, final org.ASUX.yaml.CmdLineArgsCommon _cmdLineArgs, final CmdInvoker _cmdInvoker )
    {   final String HDR = CLASSNAME +": startupYAMLImplementationFactory("+ _yamllibrary_type +",_cmdLineArgs,_cmdInvoker): ";

        assertTrue( YAML_Libraries.normalize( _yamllibrary_type ) == YAML_Libraries.normalize( _cmdLineArgs.getYAMLLibrary() )  );
        try {
            String implMainEntryClassNameStr = null;
            if ( YAML_Libraries.isCollectionsImpl( _cmdLineArgs.getYAMLLibrary() ) ) {
                implMainEntryClassNameStr = "org.ASUX.yaml.CollectionsImpl.Cmd";
            } else if ( YAML_Libraries.isNodeImpl( _cmdLineArgs.getYAMLLibrary() ) ) {
                implMainEntryClassNameStr = "org.ASUX.YAML.NodeImpl.Cmd";
            }
            assertTrue( implMainEntryClassNameStr != null ); // :-) I'm relying on YAML_library ENUM-class to make sure this ass-ert does NOT throw

            //--------------------------------
            // returns: protected Class<?> -- throws ClassNotFoundException
            final Class<?> implMainEntryClass = Cmd.class.getClassLoader().loadClass(implMainEntryClassNameStr);
            // findClass() method of ClassLoader is NOT VISIBLE - its a protected method.
            // The findClass() method searches for the class in the current class loader, if the class wasn't found by the parent class loader.
            // i.e., findClass() will be invoked by loadClass(), after checking the parent class loader for the requested class.
            if ( _cmdLineArgs.verbose )  System.out.println( HDR +"implMainEntryClassNameStr=["+implMainEntryClassNameStr+"] successfully loaded using ClassLoader.");

            //--------------------------------
            // Invoke the static-method called <implMainEntryClass>.startYAMLImplementation(_cmdLineArgs, _cmdInvoker)
            final Class[] paramClassList = { org.ASUX.yaml.CmdLineArgsCommon.class, CmdInvoker.class };
            final Object[] methodArgs = { _cmdLineArgs, _cmdInvoker };
            final Object retObj = org.ASUX.common.GenericProgramming.invokeStaticMethod( implMainEntryClass, "startYAMLImplementation", paramClassList, methodArgs );
            if ( _cmdLineArgs.verbose ) System.out.println( HDR +"returned from successfully invoking "+implMainEntryClassNameStr+".startYAMLImplementation().");

            if ( retObj instanceof YAMLImplementation ) {
                @SuppressWarnings("unchecked")
                final YAMLImplementation<T> yi = (YAMLImplementation<T>) retObj;
                return yi;
            } else if ( retObj == null )
                throw new Exception( "Unexpected return value of NULL (for GenericProgramming.invokeStaticMethod).");
            else {
                System.err.println( HDR + "GenericProgramming.invokeStaticMethod(..) returned the value="+ retObj );
                throw new Exception( "Unexpected return value of of type "+ retObj.getClass().getName() +" (for GenericProgramming.invokeStaticMethod)." );
            }

        } catch (ClassNotFoundException e2) {
            e2.printStackTrace(System.err); // main() unit-testing
            System.err.println( "\n\nInternal Error!\t"+ HDR +" error(ClassNotFoundException): '" + _cmdLineArgs + "'.");
            System.exit(6);
        } catch (Exception e) {
            e.printStackTrace(System.err); // main() unit-testing
            System.err.println( "\n\nInternal Error!\t"+ HDR +" for '" + _cmdLineArgs + "'.");
            System.exit(6);
        } catch (Throwable t) {
            t.printStackTrace(System.err); // main() unit-testing
            System.err.println( "\n\nInternal Error!\t"+ HDR +" for '" + _cmdLineArgs + "'.");
            System.exit(6);
        }
        return null; // We shouldn't be getting here, because of the System.exit() in the catch-blocks above.

    }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

};
