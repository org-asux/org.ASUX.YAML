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

// import java.util.Map;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Properties;
//import java.util.regex.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

// https://github.com/eugenp/tutorials/tree/master/aws/src/main/java/com/baeldung
// https://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_Region.html
// https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/ec2/model/package-summary.html
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;

import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.ec2.AmazonEC2Client;
// import com.amazonaws.services.ec2.model.Region;
// import com.amazonaws.services.ec2.model.AvailabilityZone;
    // https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/ec2/model/AvailabilityZone.html
// import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
// import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
// import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
// import com.amazonaws.services.ec2.model.CreateKeyPairResult;
// import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
// import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
// import com.amazonaws.services.ec2.model.DescribeInstancesResult;
// import com.amazonaws.services.ec2.model.DescribeKeyPairsRequest;
// import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
// import com.amazonaws.services.ec2.model.IpPermission;
// import com.amazonaws.services.ec2.model.IpRange;
// import com.amazonaws.services.ec2.model.MonitorInstancesRequest;
// import com.amazonaws.services.ec2.model.RebootInstancesRequest;
// import com.amazonaws.services.ec2.model.RunInstancesRequest;
// import com.amazonaws.services.ec2.model.StartInstancesRequest;
// import com.amazonaws.services.ec2.model.StopInstancesRequest;
// import com.amazonaws.services.ec2.model.UnmonitorInstancesRequest;

// import static org.junit.Assert.*;

/**
 *  
 */
public class AWSSDK {

    public static final String CLASSNAME = "org.ASUX.yaml.AWSSDK";

    /** <p>Whether you want deluge of debug-output onto System.out.</p><p>Set this via the constructor.</p>
     *  <p>It's read-only (final data-attribute).</p>
     */
    public final boolean verbose;

    /**
     *  This constructor allows us to centralize the authentication.  But then again.. what if Classses have to pass this object around?  For that, use the Static Factory function connect()
     *  @param _verbose Whether you want deluge of debug-output onto System.out.
     *  @param _AWSAccessKeyId your AWS credential with API-level access as appropriate
     *  @param _AWSSecretAccessKey your AWS credential with API-level access as appropriate
     */
    public AWSSDK(boolean _verbose, final String _AWSAccessKeyId, final String _AWSSecretAccessKey) {
        this.verbose = _verbose;
        this.AWSAuthenticate( _AWSAccessKeyId, _AWSSecretAccessKey );
    }

    private AWSSDK() {
        this.verbose = false;
    }

    //------------------------------------------------------------------------------
    private static class MyAWSException extends Exception {
        private static final long serialVersionUID = 99L;
        public MyAWSException(String _s) { super(_s); }
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    private boolean bTried2Authenticate = false;
    private AWSCredentials aws_credentials = null;
    private AWSStaticCredentialsProvider AWSAuthenticationHndl = null;

    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private static AWSSDK singleton = null;

    /**
     *  This constructor allows us to centralize the authentication.  But then again.. what if Classses have to pass this object around?  For that, use the Static Factory function getConnection()
     *  @param _verbose Whether you want deluge of debug-output onto System.out.
     *  @param _AWSAccessKeyId your AWS credential with API-level access as appropriate
     *  @param _AWSSecretAccessKey your AWS credential with API-level access as appropriate
     *  @return the singleton instance of this AWSSDK class
     *  @throws MyAWSException if code has Not yet logged in with AWS credentials
     */
    public static AWSSDK getConnection(boolean _verbose, final String _AWSAccessKeyId, final String _AWSSecretAccessKey) throws MyAWSException
    {
        if ( singleton != null )
            throw new MyAWSException( CLASSNAME +": static factory getConnection(): Someone already invoked this function!" );

        singleton = new AWSSDK(_verbose, _AWSAccessKeyId, _AWSSecretAccessKey);
        return singleton;
    }

    /**
     *  This constructor allows us to centralize the authentication.  But then again.. what if Classses have to pass this object around?  For that, use the Static Factory function getConnection()
     *  @return the singleton instance of this AWSSDK class
     *  @throws MyAWSException if code has Not yet logged in with AWS credentials
     */
    public static AWSSDK getConnection() throws MyAWSException
    {
        if ( singleton == null )
            throw new MyAWSException( CLASSNAME +": static factory getConnection(No-ARG): FORGOT to call the 3-arg POLYMORPHIC variant of getConnection()????" );

        return singleton;
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /**
     * If already logged in, get me a handle - to pass to my own AWS SDK invocations.
     * If not logged in already, an Exception will be thrown
     * @return the handle to a previously successful AWS login-connection
     * @throws MyAWSException if code has Not yet logged in with AWS credentials
     */
    private AWSStaticCredentialsProvider getAWSAuthenticationHndl() throws MyAWSException {
        if ( this.AWSAuthenticationHndl == null )
            throw new MyAWSException( CLASSNAME +": getAWSAuthenticationHndl(no-arg): code hasn't !!!!SUCCESSFULLY!!!! invoked the AWSAuthenticate(AccessKey,SecretKey) function." );
        else 
            return this.AWSAuthenticationHndl;
    }

    /**
     *  Login into AWS using the credentials provided.  This invocation is a pre-requisite before invoking any other non-static method in this class.
     *  @param _AWSAccessKeyId your AWS credential with API-level access as appropriate
     *  @param _AWSSecretAccessKey your AWS credential with API-level access as appropriate
     */
    private void AWSAuthenticate( final String _AWSAccessKeyId, final String _AWSSecretAccessKey ) {
        this.bTried2Authenticate = true;
        // Authenticate into AWS
        // https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/credentials.html
        this.aws_credentials = new BasicAWSCredentials( _AWSAccessKeyId, _AWSSecretAccessKey );
        this.AWSAuthenticationHndl = new AWSStaticCredentialsProvider( this.aws_credentials );
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    private AmazonEC2 getAWSEC2Hndl( final String _regionStr ) {
        // https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-ec2-regions-zones.html
        // final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withCredentials( this.AWSAuthenticationHndl ).withRegion( _regionStr==null?"us-east-2":_regionStr ).build();
        // To use the default credential/region provider chain 
        // Ec2Client ec2 = Ec2Client.create(); // AWS_REGION is checked .. ~/.aws/config default profile .. aws.profile system property
        return ec2;
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /**
     * Pass in a region-name and get back the output of the cmdline as JSON (cmdline being:- aws ec2 describe-regions --profile ______ --output json)
     * @return An array of YAML-Maps.  Its exactly === cmdline output of: aws ec2 describe-regions --profile ______ --output json
     */
    public ArrayList<String> getRegions( ) {
        final AmazonEC2 ec2 = this.getAWSEC2Hndl( null );
        final DescribeRegionsResult regions_response = ec2.describeRegions();
        final ArrayList<String> retarr = new ArrayList<String>();
        for(Region region : regions_response.getRegions()) {
            // System.out.printf( "Found region %s with endpoint %s\n", region.getRegionName(), region.getEndpoint());
            retarr.add( region.getRegionName() );
        }
        return retarr;
    }

    /**
     * Pass in a region-name and get back ONLY THE AZ-NAMES in the output of the cmdline as JSON (cmdline being:- aws ec2 describe-availability-zones --region us-east-2 --profile ______ --output json)
     * @param _regionStr pass in valid AWS region names like 'us-east-2', 'us-west-1', 'ap-northeast-1' ..
     * @return An array of Strings.
     */
    public ArrayList<String>  getAZs( final String _regionStr ) {
        final AmazonEC2 ec2 = this.getAWSEC2Hndl( _regionStr );
        DescribeAvailabilityZonesResult zones_response = ec2.describeAvailabilityZones();
        final ArrayList<String> retarr = new ArrayList<String>();
        for(AvailabilityZone zone : zones_response.getAvailabilityZones()) {
            // System.out.printf( "Found availability zone %s with status %s in region %s\n", zone.getZoneName(), zone.getState(), zone.getRegionName());
            if ( zone.getState().equals("available") ) retarr.add( zone.getZoneName() );
        }
        return retarr;
    }

    /**
     * Pass in a region-name and get back the output of the cmdline as JSON (cmdline being:- aws ec2 describe-availability-zones --region us-east-2 --profile ______ --output json)
     * @param _regionStr pass in valid AWS region names like 'us-east-2', 'us-west-1', 'ap-northeast-1' ..
     * @return An array of YAML-Maps.  Its exactly === cmdline output of: aws ec2 describe-availability-zones --region us-east-2 --profile ______ --output json
     * @throws Exception any runtime Exception
     */
    public ArrayList< LinkedHashMap<String,Object> >  describeAZs( final String _regionStr ) throws Exception {
        final AmazonEC2 ec2 = this.getAWSEC2Hndl( _regionStr );
        DescribeAvailabilityZonesResult zones_response = ec2.describeAvailabilityZones();
        final ArrayList< LinkedHashMap<String,Object> > retarr = new ArrayList<>();

        // https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/ec2/model/AvailabilityZone.html
        for(AvailabilityZone zone : zones_response.getAvailabilityZones()) {
            // System.out.printf( "Found availability zone %s with status %s in region %s\n", zone.getZoneName(), zone.getState(), zone.getRegionName());
            // ASSUMPTION: I printed zone.toString() to STDOUT, and noted it's proper JSON.
            // String s = zone.toString().replaceAll("=",":");
            // above zone.toString returns INVALID JSON.. that causes problems elsewhere.. when loading these strings.
            // So, manually have to create JSON here.
            // String s = "{ ";
            // s+= "ZoneName: '"+ zone.getZoneName()   +"',";
            // s+= "ZoneId: '"  + zone.getZoneId()     +"',";
            // s+= "State: '"   + zone.getState()      +"',";
            // s+= "RegionName: '"+ zone.getRegionName() +"',";
            // String sm = "";
            // for( AvailabilityZoneMessage azm: zone.getMessages() ) {
            //     if (   !   sm.equals("") ) sm += ",";
            //     s += "'"+ azm.getMessage() + "'";
            // }
            // s+= "Messages: [" + sm + "] }";
            // if ( this.verbose ) System.out.println( CLASSNAME +": describeAZs(): aws ec2 describe-az command output corrected to be JSON-compatible as ["+ s +"]" );
            // final LinkedHashMap<String,Object> map = new Tools(this.verbose).JSONString2YAML( s );
            // if ( this.verbose ) System.out.println( map.toString() );
            // retarr.add( map );

            final LinkedHashMap<String,Object> oneZone = new LinkedHashMap<>();
            oneZone.put( "ZoneName",    zone.getZoneName() );
            oneZone.put( "ZoneId",      zone.getZoneId() );
            oneZone.put( "State",       zone.getState() );
            oneZone.put( "RegionName",  zone.getRegionName() );
            final ArrayList<String> sm = new ArrayList<>();
            for( AvailabilityZoneMessage azm: zone.getMessages() ) {
                sm.add( azm.getMessage() );
            }
            oneZone.put( "Messages", sm );
            if ( this.verbose ) System.out.println( CLASSNAME +": describeAZs(): aws ec2 describe-az command output in JSON-compatible form is ["+ oneZone.toString() +"]" );

            retarr.add( oneZone );

        }
        return retarr;
    }


    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /**
     * Initialize &amp; Connect into AWS, by leveraging the AWS-credentials stored in a file called 'profile' in the current working directory from which this code is being run
     *  @param _verbose Whether you want deluge of debug-output onto System.out.
     *  @return return a handle to the SDK - for further calls to methods within this class
     */
    public static AWSSDK AWSCmdline( final boolean _verbose ) {
        try {
            final Properties p = new Properties();
            p.load( new FileInputStream( AWSProfileFileName ) );
            System.getProperties().load( new FileInputStream( "profile" ) );
            final String AWSAccessKeyId = System.getProperty( "aws.accessKeyId");
            final String AWSSecretAccessKey = System.getProperty( "aws.secretAccessKey");
            // System.out.println( "AWSAccessKeyId=["+ AWSAccessKeyId +" AWSSecretAccessKey=["+ AWSSecretAccessKey +"]" );

            final AWSSDK awssdk = AWSSDK.getConnection( _verbose, AWSAccessKeyId, AWSSecretAccessKey );
            return awssdk;

        } catch(FileNotFoundException fe) {
            fe.printStackTrace(System.err);
            System.err.println( CLASSNAME + ": main(): \n\nUnable to load the AWS Profile file named ["+ AWSProfileFileName +"]" );
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.err.println( CLASSNAME + ": main():  \n\nSee details in the lines above.");
            // System.exit(103);
        }
        return null;
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================
    public final static String AWSProfileFileName = "profile";

    public static void main(String[] args) {
        try {
            final AWSSDK awssdk = AWSCmdline( true );
            awssdk.getRegions( ).forEach( s -> System.out.println(s) );
            System.out.println("\n\n");
            awssdk.getAZs( args[0] ).forEach( s -> System.out.println(s) );

        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.err.println( CLASSNAME + ": main():  \n\nSee details in the lines above.");
            // System.exit(103);
        }

    }


    private void  CLIPBOARD( final String _regionStr ) {
        // https://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_Region.html
        final Region myRegion = new Region().withRegionName( _regionStr );
        // AmazonEC2Client.serviceMetadata().regions().forEach(System.out::println);
    }
}
