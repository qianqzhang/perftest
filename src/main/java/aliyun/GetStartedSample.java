package aliyun;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;

import java.io.*;
import java.util.List;

/**
 * This sample demonstrates how to get started with basic requests to Aliyun OSS
 * using the OSS SDK for Java.
 */
public class GetStartedSample {

    private static String endpoint = "http://oss-cn-beijing.aliyuncs.com";
    private static String accessKeyId = "6VERWRSs2aafD1TR";
    private static String accessKeySecret = "cAeICRF6OpurliLRZnF2DNu4Cnhknj";

    private static OSSClient client = null;

    public static void main1(String[] args) throws IOException {
        /*
         * Constructs a client instance with your account for accessing OSS
         */
        long start_tm = System.currentTimeMillis();
        client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        long end_tm = System.currentTimeMillis();
        System.out.println("new cost " + (end_tm - start_tm) + "ms");
        String bucketName = "qianqzhang-test-bj";
        String key = "MyObjectKey";

        System.out.println("===========================================");
        System.out.println("Getting Started with OSS SDK for Java");
        System.out.println("===========================================\n");

        try {
            /*
             * Create a new OSS bucket
             */
//            System.out.println("Creating bucket " + bucketName + "\n");
//            client.createBucket(bucketName);


            /*
             * Determine whether the newly bucket exists
             */
            boolean exists = client.doesBucketExist(bucketName);
            System.out.println("Does bucket " + bucketName + " exist? " + exists + "\n");

            /*
             * List the buckets in your account
             */
            System.out.println("Listing buckets");
            for (Bucket bucket : client.listBuckets()) {
                System.out.println(" - " + bucket.getName());
            }
            System.out.println();

            /*
             * Upload an object to your bucket
             */
            start_tm = System.currentTimeMillis();
            System.out.println("Uploading a new object to OSS from a file\n");
            client.putObject(new PutObjectRequest(bucketName, key, createSampleFile()));
            end_tm = System.currentTimeMillis();
            System.out.println("new cost " + (end_tm - start_tm) + "ms");

            /*
             * Determine whether an object residents in your bucket
             */
            exists = client.doesObjectExist(bucketName, key);
            System.out.println("Does object " + bucketName + " exist? " + exists + "\n");

            /*
             * Download an object from your bucket
             */
            System.out.println("Downloading an object");
            OSSObject object = client.getObject(new GetObjectRequest(bucketName, key));
            System.out.println("Content-Type: "  + object.getObjectMetadata().getContentType());
            displayTextInputStream(object.getObjectContent());

            /*
             * List objects in your bucket by prefix
             */
            System.out.println("Listing objects");
            ObjectListing objectListing = client.listObjects(new ListObjectsRequest(bucketName)
                    .withPrefix("My"));
            for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                System.out.println(" - " + objectSummary.getKey() + "  " +
                        "(size = " + objectSummary.getSize() + ")");
            }
            System.out.println();

            /*
             * Delete an object
             */
            System.out.println("Deleting an object\n");
            client.deleteObject(bucketName, key);

            List<LifecycleRule> result = client.getBucketLifecycle(bucketName);
            for(LifecycleRule r: result){
                System.out.println("===========Id is " + r.getId() + "======\n");
                System.out.println("Status is " + r.getStatus() + "\n");
                System.out.println("Prefix is " + r.getPrefix() +"\n");
                if(r.hasCreatedBeforeDate()) {
                    System.out.println("CreatedBeforeDate is " + r.getCreatedBeforeDate().toString() + "\n");
                }
                if(r.hasExpirationDays()) {
                    System.out.println("ExpirationDays is " + r.getExpirationDays() + "\n");
                }
                if(r.hasExpirationTime()) {
                    System.out.println("ExpirationTime is " + r.getExpirationTime() + "\n");
                }
                if(r.hasAbortMultipartUpload()){
                    System.out.println("AbortMultipartUpload is " + r.getAbortMultipartUpload()+ "\n");
                }
                System.out.println("Status is " + r.getStatus() + "\n");
                System.out.println("=========== end of Id is " + r.getId() + "======\n\n");
            }

            SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(bucketName);
            for(int i = 0; i < 1 ; i++){
                String ruleId = "rule_" + i;
                String matchPrefix = "appTestPrefix_" + i + "/";
                int ttl = 100;
                request.AddLifecycleRule(new LifecycleRule(ruleId, matchPrefix, LifecycleRule.RuleStatus.Enabled, ttl));
            }
            client.deleteBucketLifecycle(request);

//            List<LifecycleRule> result = client.getBucketLifecycle(request);
//            for(LifecycleRule r: result){
//                System.out.println("id is " + r.getId());
//            }



            /*
             * Delete a bucket
             */
         //   System.out.println("Deleting bucket " + bucketName + "\n");
       //     client.deleteBucket(bucketName);
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
