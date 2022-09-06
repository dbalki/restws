package com.example.demo;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClient;
import com.amazonaws.services.translate.model.GetTerminologyRequest;
import com.amazonaws.services.translate.model.GetTerminologyResult;
import com.amazonaws.services.translate.model.ImportTerminologyRequest;
import com.amazonaws.services.translate.model.ListTerminologiesRequest;
import com.amazonaws.services.translate.model.ListTerminologiesResult;
import com.amazonaws.services.translate.model.MergeStrategy;
import com.amazonaws.services.translate.model.TerminologyData;
import com.amazonaws.services.translate.model.TerminologyDataLocation;
import com.amazonaws.services.translate.model.TerminologyProperties;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
 
public class App {
    private static final String REGION = "us-east-1";
 
    public static void main( String[] args ) {
 
        // Create credentials using a provider chain. For more information, see
        // https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
    	System.setProperty("aws.accessKeyId", "AKIA6ASNI6CAKM47R2MB");
    	System.setProperty("aws.secretKey", "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");
        AWSCredentialsProvider awsCreds = DefaultAWSCredentialsProviderChain.getInstance();
        
        AmazonTranslate translate = AmazonTranslateClient.builder()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds.getCredentials()))
                .withRegion(REGION)
                .build();
		
		
//		  translateText(translate);
		  
//		  listTerminologies(translate);
		  
		  getTerminology(translate);
	
//		importTerminology(translate);
    }

	private static void importTerminology(AmazonTranslate translate) {
		List<CsvData> csvList = new ArrayList<>();
		CsvData csvData1 = new CsvData();
		csvData1.setSourceText("Child Tax Credit");
		csvData1.setTargetText("Crédit d'impôt enfants la bella");
		csvList.add(csvData1);
		CsvData csvData2 = new CsvData();
		csvData2.setSourceText("family");
		csvData2.setTargetText("famille le bak");
		csvList.add(csvData2);
		StringBuilder builder = new StringBuilder();
		builder.append("en,fr\n");
		for (CsvData csvData : csvList) {
			builder.append(csvData.getSourceText())
			.append(",")
			.append(csvData.getTargetText())
			.append("\n");
		}
		
//		String s = "en,fr\nChild Tax Credit,Crédit d'impôt enfants Bal\nfamily,famille bak";
		String s = builder.toString();
		System.out.println("CSV Data to be sent:" + s);
		byte[] b = s.getBytes(StandardCharsets.UTF_8);
		ByteBuffer file = ByteBuffer.wrap(b);
		TerminologyData terminologyData = new TerminologyData();
		terminologyData.withFormat("CSV").withFile(file);
		ImportTerminologyRequest importTerminologyRequest = new ImportTerminologyRequest();
		importTerminologyRequest.withName("Mt_EnglishToFrench_Term1")
				.withDescription("Custom terms uploaded using Java API").withTerminologyData(terminologyData)
				.withMergeStrategy(MergeStrategy.OVERWRITE);
		translate.importTerminology(importTerminologyRequest);
	}

	private static void translateText(AmazonTranslate translate) {
		TranslateTextRequest request = new TranslateTextRequest()
				.withText("IRS says the Child Tax Credit helps family with qualifying children get a tax break. You may be able to claim the credit even if you don't normally file a tax return")
				.withSourceLanguageCode("en")
				.withTargetLanguageCode("fr")
				.withTerminologyNames("Mt_EnglishToFrench_Term1");
		TranslateTextResult result = translate.translateText(request);
		System.out.println(result.getTranslatedText());
	}

	private static void listTerminologies(AmazonTranslate translate) {
		ListTerminologiesRequest listTerminologiesRequest = new ListTerminologiesRequest().withMaxResults(10);
		ListTerminologiesResult listTerminologies = translate.listTerminologies(listTerminologiesRequest);
		List<TerminologyProperties> terminologyPropertiesList = listTerminologies.getTerminologyPropertiesList();
		for (TerminologyProperties terminologyProperties : terminologyPropertiesList) {
			printResponse(terminologyProperties);
		}
	}

	private static void getTerminology(AmazonTranslate translate) {
		GetTerminologyRequest getTerminologyRequest = new GetTerminologyRequest();
//		getTerminologyRequest.withName("Mt_EnglishToFrench_Term1");
		getTerminologyRequest.withName("Mt_EnglishToSpanish_Term1");
		GetTerminologyResult terminology = translate.getTerminology(getTerminologyRequest);
		TerminologyDataLocation terminologyDataLocation = terminology.getTerminologyDataLocation();
		System.out.println("Data location:" + terminologyDataLocation.getLocation());
		System.out.println("Repo type:" + terminologyDataLocation.getRepositoryType());
		TerminologyProperties terminologyProperties = terminology.getTerminologyProperties();
		printResponse(terminologyProperties);
	}

	private static void printResponse(TerminologyProperties terminologyProperties) {
		System.out.println("Terminology name:" + terminologyProperties.getName());
		System.out.println("Term count:" + terminologyProperties.getTermCount());
		System.out.println("Term created at:" + terminologyProperties.getCreatedAt());
		System.out.println("Term Last updated at:" + terminologyProperties.getLastUpdatedAt());
		System.out.println("Term Source language code:" + terminologyProperties.getSourceLanguageCode());
		List<String> targetLanguageCodes = terminologyProperties.getTargetLanguageCodes();
		for (String targetLanguage : targetLanguageCodes) {
			System.out.println("Target lang code:" + targetLanguage);
		}
		System.out.println("Term size:" + terminologyProperties.getSizeBytes());
	}
}