package aliyun;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * This sample demonstrates how to get started with basic requests to Aliyun OSS
 * using the OSS SDK for Java.
 */
public class TestAliyunOSS {
    private final static String TEST_AVAILABLE_MODE = "t";
    private final static String DELETE_A_FILE_MODE = "dd";
    private final static String WHETHER_A_FILE_EXISTS_MODE = "e";
    private final static String GET_RULE_DETAIL_MODE = "r";
    private final static String LIST_OBJECT_BY_PREFIX_MODE = "l";
    private final static String GET_FILE_META_DATA_MODE = "m";
    private final static String GET_ALL_FILENAMES_BY_PREFIX_MODE = "a";
    private static OSSClient client = null;

    public static void main(String[] args) throws IOException {
        /*
         * Constructs a client instance with your account for accessing OSS
         */
        if(args.length < 5){
            System.out.println("Usage: mode endpoint accessKeyId accessKeySecret bucketName [argValue]");
            System.out.println("mode: 't' for test upload and download\n'dd' for delete a file(need argValue)\n " +
                    "'e' for judge a file exists(need argValue)\n 'r' for get lifecycle rules of this bucket\n" +
                    "'l' for list all objects by prefix\n'm' for get a file's metadata(need argValue)\n" +
                    "'a' for get all filenames by prefix(need argValue)\n");
            System.exit(0);
        }

        if(!TEST_AVAILABLE_MODE.equals(args[0]) && !DELETE_A_FILE_MODE.equals(args[0])
                && !WHETHER_A_FILE_EXISTS_MODE.equals(args[0]) && !GET_RULE_DETAIL_MODE.equals(args[0])
                && !LIST_OBJECT_BY_PREFIX_MODE.equals(args[0]) && !GET_FILE_META_DATA_MODE.equals(args[0])
                && !GET_ALL_FILENAMES_BY_PREFIX_MODE.equals(args[0])){
            System.out.println("mode  para input error");
            System.exit(0);
        }

        if((DELETE_A_FILE_MODE.equals(args[0]) || WHETHER_A_FILE_EXISTS_MODE.equals(args[0]) || GET_FILE_META_DATA_MODE.equals(args[0])
        || GET_ALL_FILENAMES_BY_PREFIX_MODE.equals(args[0])) && args.length < 6)
        {
            System.out.println("need argValue");
            System.exit(-1);
        }

        String mode = args[0];
        String endpoint = args[1];
        String accessKeyId = args[2];
        String accessKeySecret = args[3];
        String bucketName = args[4];
        String argValue = "";
        if(args.length >= 6){
            argValue = args[5];
        }

        client = new OSSClient(endpoint, accessKeyId, accessKeySecret);

        System.out.println("===========================================");
        System.out.println("Getting Started Tools with OSS SDK for Java");
        System.out.println("===========================================\n\n\n");
        ObjectListing objectListing;
        try {
            switch (mode){
                case GET_RULE_DETAIL_MODE:
                    System.out.println("start to check Lifecycle");
                    List<LifecycleRule> result = client.getBucketLifecycle(bucketName);
                    for (LifecycleRule r : result) {
                        System.out.println("===========Id  " + r.getId() + "======\n");
                        System.out.println("status: " + r.getStatus() + "\n");
                        System.out.println("prefix: " + r.getPrefix() + "\n");
                        if (r.hasCreatedBeforeDate()) {
                            System.out.println("createdBeforeDate: " + r.getCreatedBeforeDate().toString() + "\n");
                        }
                        if (r.hasExpirationDays()) {
                            System.out.println("expirationDays: " + r.getExpirationDays() + "\n");
                        }
                        if (r.hasExpirationTime()) {
                            System.out.println("expirationTime: " + r.getExpirationTime() + "\n");
                        }
                        if (r.hasAbortMultipartUpload()) {
                            System.out.println("abortMultipartUpload: " + r.getAbortMultipartUpload() + "\n");
                        }
                        System.out.println("=========== end of Id  " + r.getId() + "======\n\n");
                    }
                    break;

                case WHETHER_A_FILE_EXISTS_MODE:
                    /*
                     * Determine whether an object residents in your bucket
                     */
                    boolean exists = client.doesObjectExist(bucketName, argValue);
                    System.out.println("Does object " + argValue + " exist? " + exists + "\n");
                    break;

                case GET_ALL_FILENAMES_BY_PREFIX_MODE:

                    final int maxKeys = 100;
                    final String keyPrefix = argValue;
                    String nextMarker = null;

                    do {
                        objectListing = client.listObjects(new ListObjectsRequest(bucketName).
                                withPrefix(keyPrefix).withMarker(nextMarker).withMaxKeys(maxKeys));

                        List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
                        for (OSSObjectSummary s : sums) {
                            System.out.println("\t" + s.getKey());
                        }

                        nextMarker = objectListing.getNextMarker();

                    } while (objectListing.isTruncated());
                    break;

                case GET_FILE_META_DATA_MODE:
                    /*
                     *get file meta data
                     */
                    com.aliyun.oss.model.ObjectMetadata metaData = client.getObjectMetadata(bucketName, argValue);
                    Map<String, String> strMetaData = metaData.getUserMetadata();
                    for(String key: strMetaData.keySet()){
                        System.out.println( key + "\t:" + strMetaData.get(key));
                    }
                    try {
                        System.out.println("LastModified\t:" + metaData.getLastModified());
                        System.out.println("ExpiredTime\t:" + metaData.getExpirationTime());
                        System.out.println("ContentType\t:" + metaData.getContentType());
                        System.out.println("Length\t:" + metaData.getContentLength());

                    }catch(Exception e){

                    }
                    break;

                case DELETE_A_FILE_MODE:
                    System.out.println("Deleting an object " + argValue);
                    client.deleteObject(bucketName, argValue);
                    System.out.println("delete object succeed");
                    break;

                case LIST_OBJECT_BY_PREFIX_MODE:
            /*
             * List objects in your bucket by prefix
             */
                    System.out.println("Listing objects by prefix");
                    objectListing = client.listObjects(new ListObjectsRequest(bucketName)
                            .withPrefix(argValue));
                    for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                        System.out.println(" - " + objectSummary.getKey() + "  " +
                                "(size = " + objectSummary.getSize() + ")");
                    }
                    break;

                case TEST_AVAILABLE_MODE:
                    String key = "TestMyObjectKey:" + System.currentTimeMillis();
                /*
                 * Upload an object to your bucket
                 */
                    long start_tm = System.currentTimeMillis();
                    System.out.println("Uploading a new object to OSS from a file + " + key + "\n");
                    client.putObject(new PutObjectRequest(bucketName, key, createSampleFile()));
                    long end_tm = System.currentTimeMillis();
                    System.out.println("uploadFile cost " + (end_tm - start_tm) + "ms");
                /*
                 * Download an object from your bucket
                 */
                    System.out.println("Downloading an object " + key);
                    OSSObject object = client.getObject(new GetObjectRequest(bucketName, key));
                    System.out.println("Content-Type: " + object.getObjectMetadata().getContentType());
                    displayTextInputStream(object.getObjectContent());
                    break;
            }

        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message: " + oe.getErrorCode());
            System.out.println("Error Code:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as no2t being able to access the network.");
            System.out.println("Error Message: " + ce.getMessage());
        } finally {
            /*
             * Do not forget to shut down the client finally to release all allocated resources.
             */
            client.shutdown();
        }
    }

    private static File createSampleFile() throws IOException {
        File file = File.createTempFile("oss-java-sdk-", ".txt");
        file.deleteOnExit();

        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.write("0123456789011234567890\n");
        writer.close();

        return file;
    }

    private static void displayTextInputStream(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;

            System.out.println("    " + line);
        }
        System.out.println();

        reader.close();
    }
}
