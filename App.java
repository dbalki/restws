package com.example.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.DescribeTextTranslationJobRequest;
import software.amazon.awssdk.services.translate.model.DescribeTextTranslationJobResponse;
import software.amazon.awssdk.services.translate.model.InputDataConfig;
import software.amazon.awssdk.services.translate.model.OutputDataConfig;
import software.amazon.awssdk.services.translate.model.StartTextTranslationJobRequest;
import software.amazon.awssdk.services.translate.model.StartTextTranslationJobResponse;
import software.amazon.awssdk.services.translate.model.TranslateException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
 
public class App {
//    private static final String REGION = "us-east-1";
    
    private static long sleepTime = 5;
 
    public static void main( String[] args ) throws IOException {
 
        // Create credentials using a provider chain. For more information, see
        // https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
    	System.setProperty("aws.accessKeyId", "");
    	System.setProperty("aws.secretKey", "");
    	AwsCredentialsProvider awsCreds = SystemPropertyCredentialsProvider.create();
    	TranslateClient translateClient = TranslateClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(awsCreds)
                .build();
    	S3Client s3Client = S3Client.builder().region(Region.US_EAST_1).build();
    	// Set this variable to an S3 bucket location with a folder."
        // Input files must be in a folder and not at the bucket root."
    	String s3BucketInput = createS3Bucket(s3Client);
    	String s3BucketOutput = createS3Bucket(s3Client);
        String s3InputUri = "s3://" + s3BucketInput+ "/WORD";
        String s3OutputUri = "s3://" + s3BucketOutput +"/";
        putS3Object(s3Client, s3BucketInput, "source_doc.docx", "c:/temp/source_doc.docx");

        // This role must have permissions to read the source bucket and to read and
        // write to the destination bucket where the translated text will be stored.
        String dataAccessRoleArn = "arn:aws:iam::0123456789ab:role/S3TranslateRole";
        
    	String id = translateDocuments(translateClient, s3InputUri, s3OutputUri, "mt-trans-job", dataAccessRoleArn);
        System.out.println("Translation job "+id + " is completed");
        translateClient.close();
        getObjectBytes(s3Client, s3BucketOutput, "target_doc.docx", "c:/temp/target_doc.docx");
        
//        EndpointConfiguration endpointConfiguration = new EndpointConfiguration("https://translate.us-east-1.amazonaws.com", REGION);
//		AmazonTranslate translate = AmazonTranslateClient.builder()
//                .withCredentials(new AWSStaticCredentialsProvider(awsCreds.getCredentials()))
////                .withRegion(REGION)
//                .withEndpointConfiguration(endpointConfiguration)
//                .build();
		
//		doTextract(awsCreds);
		
//		  translateText(translate);
		  
//		  listTerminologies(translate);
		  
//		  getTerminology(translate);
	
//		importTerminology(translate);
    }
    
	private static String createS3Bucket(S3Client s3Client) {
		String bucket = "bucket" + System.currentTimeMillis();
		try {
			s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket)
					.createBucketConfiguration(
							CreateBucketConfiguration.builder().locationConstraint(Region.US_EAST_1.id()).build())
					.build());
			System.out.println("Creating bucket: " + bucket);
			s3Client.waiter().waitUntilBucketExists(HeadBucketRequest.builder().bucket(bucket).build());
			System.out.println(bucket + " is ready.");
			System.out.printf("%n");
		} catch (S3Exception e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
		return bucket;
	}
	// snippet-start:[s3.java2.s3_object_upload.main]
    public static String putS3Object(S3Client s3, String bucketName, String objectKey, String objectPath) {

        try {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("x-amz-meta-myVal", "test");
            PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .metadata(metadata)
                .build();

            PutObjectResponse response = s3.putObject(putOb, RequestBody.fromBytes(getObjectFile(objectPath)));
            return response.eTag();

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return "";
    }

    // snippet-start:[s3.java2.getobjectdata.main]
    public static void getObjectBytes (S3Client s3, String bucketName, String keyName, String path) {

        try {
            GetObjectRequest objectRequest = GetObjectRequest
                .builder()
                .key(keyName)
                .bucket(bucketName)
                .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
            byte[] data = objectBytes.asByteArray();

            // Write the data to a local file.
            File myFile = new File(path );
            OutputStream os = new FileOutputStream(myFile);
            os.write(data);
            System.out.println("Successfully obtained bytes from an S3 object");
            os.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    
    // Return a byte array.
    private static byte[] getObjectFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {
            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bytesArray;
    }
    // snippet-end:[s3.java2.s3_object_upload.main]
    
    // snippet-start:[translate.java2._batch.main]
    public static String translateDocuments(TranslateClient translateClient,
                                          String s3Uri,
                                          String s3UriOut,
                                          String jobName,
                                          String dataAccessRoleArn) {

        try {
            InputDataConfig dataConfig = InputDataConfig.builder()
                .s3Uri(s3Uri)
                .contentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                .build();

            OutputDataConfig outputDataConfig = OutputDataConfig.builder()
                .s3Uri(s3UriOut)
                .build();

            StartTextTranslationJobRequest textTranslationJobRequest = StartTextTranslationJobRequest.builder()
                .jobName(jobName)
                .dataAccessRoleArn(dataAccessRoleArn)
                .inputDataConfig(dataConfig)
                .outputDataConfig(outputDataConfig)
                .sourceLanguageCode("en")
                .targetLanguageCodes("es")
                .build();

            StartTextTranslationJobResponse textTranslationJobResponse = translateClient.startTextTranslationJob(textTranslationJobRequest);

            //Keep checking until job is done
            boolean jobDone = false;
            String jobStatus;
            String jobId = textTranslationJobResponse.jobId();

            DescribeTextTranslationJobRequest jobRequest = DescribeTextTranslationJobRequest.builder()
                .jobId(jobId)
                .build();

            while (!jobDone) {

                //Check status on each loop
                DescribeTextTranslationJobResponse response = translateClient.describeTextTranslationJob(jobRequest);
                jobStatus = response.textTranslationJobProperties().jobStatusAsString();
                System.out.println(jobStatus);

                if (jobStatus.contains("COMPLETED"))
                    jobDone = true;
                else {
                    System.out.print(".");
                    Thread.sleep(sleepTime * 1000);
                }
            }
            return textTranslationJobResponse.jobId();

        } catch (TranslateException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
     return "";
    }
    // snippet-end:[translate.java2._batch.main]

    // Create a bucket by using a S3Waiter object
//    public static void createBucket( AmazonS3 s3Client, String bucketName) {
//
//		CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
//		Bucket bucket = s3Client.createBucket(createBucketRequest);
//
//		AmazonS3Waiters waiters = s3Client.waiters();
//		Waiter<HeadBucketRequest> bucketExists = waiters.bucketExists();
//		WaiterParameters<HeadBucketRequest> waiterParameters = new WaiterParameters<HeadBucketRequest>();
//		bucketExists.run(waiterParameters);
//		// Wait until the bucket is created and print out the response.
//		System.out.println(bucketName + " is ready");
//
//    }
//
//    
//    private static void translateDocument(AWSCredentialsProvider awsCreds) {
//        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(awsCreds)
//                .withRegion(Regions.US_EAST_1)
//                .build();
//        String bucketName = "mt_bucket_1";
//		createBucket(s3, bucketName);
//        PutObjectRequest objectRequest = new PutObjectRequest(bucketName, "1234", new File("c:/temp/test.docx"));
//        PutObjectResult result = s3.putObject(objectRequest);
//
//    	InputDataConfig inputDataConfig = new InputDataConfig().withContentType("text/docx").withS3Uri("");
//		OutputDataConfig outputDataConfig = new OutputDataConfig().withS3Uri("");
//		StartTextTranslationJobRequest req = new StartTextTranslationJobRequest()
//				.withInputDataConfig(inputDataConfig)
//				.withSourceLanguageCode("en")
//				.withTargetLanguageCodes("es")
//				.withOutputDataConfig(outputDataConfig)
//				.withJobName("mt_translate_job");
//		EndpointConfiguration endpointConfiguration = new EndpointConfiguration(
//				"https://translate.us-east-1.amazonaws.com", REGION);
//		AmazonTranslate translate = AmazonTranslateClient.builder()
//				.withCredentials(new AWSStaticCredentialsProvider(awsCreds.getCredentials()))
//				.withEndpointConfiguration(endpointConfiguration).build();
//		StartTextTranslationJobResult translationJobResult = translate.startTextTranslationJob(req);
//		System.out.println("Job status:" + translationJobResult.getJobStatus());
//    }
//
//	private static void doTextract(AWSCredentialsProvider awsCreds) throws IOException {
//		AmazonTextract amazonTextract = AmazonTextractClient.builder().withCredentials(awsCreds).withRegion(REGION).build();
//		AnalyzeDocumentRequest analyzeDocumentRequest = new AnalyzeDocumentRequest();
//		Document document = new Document();
//		InputStream sourceStream = new FileInputStream(new File("c:/temp/fw4.png"));
//		document.withBytes(ByteBuffer.wrap(sourceStream.readAllBytes()));
//		sourceStream.close();
//		analyzeDocumentRequest.setDocument(document);
//        analyzeDocumentRequest.withFeatureTypes(FeatureType.FORMS, FeatureType.TABLES);
//		AnalyzeDocumentResult documentResult = amazonTextract.analyzeDocument(analyzeDocumentRequest);
//		DocumentMetadata documentMetadata = documentResult.getDocumentMetadata();
//		System.out.println("Number of pages:" + documentMetadata.getPages());
//		System.out.println("Analyze doc model version:" + documentResult.getAnalyzeDocumentModelVersion());
//		List<Block> blocks = documentResult.getBlocks();
//		for (Block block : blocks) {
//			if(block.getConfidence() != null && block.getConfidence() >= 90) {
//				System.out.println("block type:" + block.getBlockType());
//				System.out.println("block text:" + block.getText());
//			}
//		}
//	}
//
//	private static void importTerminology(AmazonTranslate translate) {
//		List<CsvData> csvList = new ArrayList<>();
//		CsvData csvData1 = new CsvData();
//		csvData1.setSourceText("Child Tax Credit");
//		csvData1.setTargetText("Crédit d'impôt enfants la bella");
//		csvList.add(csvData1);
//		CsvData csvData2 = new CsvData();
//		csvData2.setSourceText("family");
//		csvData2.setTargetText("famille le bak");
//		csvList.add(csvData2);
//		StringBuilder builder = new StringBuilder();
//		builder.append("en,fr\n");
//		for (CsvData csvData : csvList) {
//			builder.append(csvData.getSourceText())
//			.append(",")
//			.append(csvData.getTargetText())
//			.append("\n");
//		}
//		
////		String s = "en,fr\nChild Tax Credit,Crédit d'impôt enfants Bal\nfamily,famille bak";
//		String s = builder.toString();
//		System.out.println("CSV Data to be sent:" + s);
//		byte[] b = s.getBytes(StandardCharsets.UTF_8);
//		ByteBuffer file = ByteBuffer.wrap(b);
//		TerminologyData terminologyData = new TerminologyData();
//		terminologyData.withFormat("CSV").withFile(file);
//		ImportTerminologyRequest importTerminologyRequest = new ImportTerminologyRequest();
//		importTerminologyRequest.withName("Mt_EnglishToFrench_Term1")
//				.withDescription("Custom terms uploaded using Java API").withTerminologyData(terminologyData)
//				.withMergeStrategy(MergeStrategy.OVERWRITE);
//		translate.importTerminology(importTerminologyRequest);
//	}
//
//	private static void translateText(AmazonTranslate translate) {
//		TranslateTextRequest request = new TranslateTextRequest()
//				.withText("IRS says the Child Tax Credit helps family with qualifying children get a tax break. You may be able to claim the credit even if you don't normally file a tax return")
//				.withSourceLanguageCode("en")
//				.withTargetLanguageCode("fr")
//				.withTerminologyNames("Mt_EnglishToFrench_Term1");
//		TranslateTextResult result = translate.translateText(request);
//		System.out.println(result.getTranslatedText());
//	}
//
//	private static void listTerminologies(AmazonTranslate translate) {
//		ListTerminologiesRequest listTerminologiesRequest = new ListTerminologiesRequest().withMaxResults(10);
//		ListTerminologiesResult listTerminologies = translate.listTerminologies(listTerminologiesRequest);
//		List<TerminologyProperties> terminologyPropertiesList = listTerminologies.getTerminologyPropertiesList();
//		for (TerminologyProperties terminologyProperties : terminologyPropertiesList) {
//			printResponse(terminologyProperties);
//		}
//	}
//
//	private static void getTerminology(AmazonTranslate translate) {
//		GetTerminologyRequest getTerminologyRequest = new GetTerminologyRequest();
////		getTerminologyRequest.withName("Mt_EnglishToFrench_Term1");
//		getTerminologyRequest.withName("Mt_EnglishToSpanish_Term1");
//		GetTerminologyResult terminology = translate.getTerminology(getTerminologyRequest);
//		TerminologyDataLocation terminologyDataLocation = terminology.getTerminologyDataLocation();
//		System.out.println("Data location:" + terminologyDataLocation.getLocation());
//		System.out.println("Repo type:" + terminologyDataLocation.getRepositoryType());
//		TerminologyProperties terminologyProperties = terminology.getTerminologyProperties();
//		printResponse(terminologyProperties);
//	}
//
//	private static void printResponse(TerminologyProperties terminologyProperties) {
//		System.out.println("Terminology name:" + terminologyProperties.getName());
//		System.out.println("Term count:" + terminologyProperties.getTermCount());
//		System.out.println("Term created at:" + terminologyProperties.getCreatedAt());
//		System.out.println("Term Last updated at:" + terminologyProperties.getLastUpdatedAt());
//		System.out.println("Term Source language code:" + terminologyProperties.getSourceLanguageCode());
//		List<String> targetLanguageCodes = terminologyProperties.getTargetLanguageCodes();
//		for (String targetLanguage : targetLanguageCodes) {
//			System.out.println("Target lang code:" + targetLanguage);
//		}
//		System.out.println("Term size:" + terminologyProperties.getSizeBytes());
//	}
}