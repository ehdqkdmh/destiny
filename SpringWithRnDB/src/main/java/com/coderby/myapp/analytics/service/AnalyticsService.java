package com.coderby.myapp.analytics.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.Rengine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coderby.myapp.analytics.model.IrisVO;
import com.coderby.myapp.analytics.model.SampleVO;
import com.coderby.myapp.analytics.model.SummaryVO;
import com.coderby.myapp.upload.model.UploadFileVO;
import com.coderby.myapp.upload.service.IUploadFileService;

@Service
public class AnalyticsService implements IAnalyticsService {

	private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);

	@Autowired
	Rengine rEngine;

	@Autowired
	IUploadFileService fileService;

	@Override
	public ArrayList<IrisVO> getAvgPetalBySpecies() {
		ArrayList<IrisVO> irisList = new ArrayList<IrisVO>();
		try {
			String[] species = {"setosa", "versicolor", "virginica"};
			REXP result = rEngine.eval("tapply(iris$Petal.Length, iris$Species, mean)");
			REXP result2 = rEngine.eval("tapply(iris$Petal.Width, iris$Species, mean)");

			double resultList[] = result.asDoubleArray();
			double resultList2[] = result2.asDoubleArray();
			for(int i=0; i<resultList.length; i++) {
				IrisVO iris = new IrisVO();
				iris.setSpecies(species[i]);
				iris.setPetalLength(resultList[i]);
				iris.setPetalWidth(resultList2[i]);
				irisList.add(iris);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return irisList;

	}

	@Override
	public ArrayList<SampleVO> getAvgPetalBySpecies2() {
		ArrayList<SampleVO> irisList = new ArrayList<SampleVO>();
		try {
			//            String[] species = {"setosa", "versicolor", "virginica"};
			REXP result = rEngine.eval("tapply(iris$Petal.Length, iris$Species, mean)");
			REXP result2 = rEngine.eval("tapply(iris$Petal.Width, iris$Species, mean)");

			SampleVO sample1 = new SampleVO();
			sample1.setName("P.L mean");
			sample1.setType("column");
			sample1.setData(result.asDoubleArray());
			irisList.add(sample1);
			SampleVO sample2 = new SampleVO();
			sample2.setName("P.W mean");
			sample2.setType("column");
			sample2.setData(result2.asDoubleArray());
			irisList.add(sample2);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return irisList;
	}

	@Override
	public ArrayList<SampleVO> analyticsDatabase(int fileId) {
		ArrayList<SampleVO> irisList = new ArrayList<SampleVO>();
		UploadFileVO file = fileService.getFile(fileId);
		byte[] data = file.getFileData();
		try {
			long exp = rEngine.rniPutString(new String(data));
			rEngine.rniAssign("data", exp, 0);

			REXP rdata = rEngine.eval("(data <- read.table(text = data, sep =\",\", header = TRUE, stringsAsFactors = FALSE))");
			logger.info(rdata.toString());
//			
//			RList rdataList = rdata.asList();
//			String[] keys = rdataList.keys();
//			Object[] modelData = new Object[keys.length];
//			for(String key : keys) {
//				System.out.println(key);
//			}
//			System.out.println("----");
//			System.out.println(rdataList.toString());
//			System.out.println("=====");
//			for(int i=0; i<keys.length; i++) {
//				switch(rdataList.at(i).rtype) {
//				case 14:
//					modelData[i] = rdataList.at(i).asDoubleArray();
//					for(double col : rdataList.at(i).asDoubleArray()) {
//						System.out.print(col + "\t");
//					}
//					break;
//				case 16:
//					modelData[i] = rdataList.at(i).asStringArray();
//					for(String col : rdataList.at(i).asStringArray()) {
//						System.out.print(col + "\t");
//					}
//				}
//				System.out.println();
//			}
//			System.out.println("=====");
//			System.out.println();
//			double[] col1 = rdataList.at(1).asDoubleArray();
//			for(double col : col1) {
//				System.out.print(col + "\t");
//			}
//			System.out.println();
//			
//			REXP names = rEngine.eval("names(data)");
//			for(String colName : names.asStringArray()) {
//				System.out.println(colName);
//			}
			REXP result = rEngine.eval("tapply(data$Petal.Length, data$Species, mean)");
			REXP result2 = rEngine.eval("tapply(data$Petal.Width, data$Species, mean)");

			SampleVO sample1 = new SampleVO();
			sample1.setName("꽃잎 길이 평균");
			sample1.setType("column");
			sample1.setData(result.asDoubleArray());
			irisList.add(sample1);
			SampleVO sample2 = new SampleVO();
			sample2.setName("꽃잎 너비 평균");
			sample2.setType("column");
			sample2.setData(result2.asDoubleArray());
			irisList.add(sample2);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return irisList;
	}

	
	@Override
	public Map<String, Object> analyticsDatabaseInfo(int fileId) {
		Map<String, Object> rData = new Hashtable<String, Object>();
		
		UploadFileVO file = fileService.getFile(fileId);
		byte[] data = file.getFileData();
//		logger.info(new String(data));
		try {
			long exp = rEngine.rniPutString(new String(data, "UTF-8"));
			rEngine.rniAssign("data", exp, 0);
			
			rEngine.eval("Sys.setlocale(category=\"LC_ALL\", locale=\"English_United States.1252\")");
			REXP rdataFrame = rEngine.eval("(dataFrame <- read.table(text=data, sep=\",\", fill=TRUE, header=TRUE, stringsAsFactors=FALSE, skipNul=TRUE, encoding=\"UTF-8\", fileEncoding=\"UTF-8\"))");

			RList rdataList = rdataFrame.asList();
//			System.out.println(rdataList.toString());
			String[] keys = rdataList.keys();
			rData.put("colNames", keys);

			Object[] modelData = new Object[keys.length];
			for(String key : keys) {
				System.out.print(key + "\t");
			}
			System.out.println();
			
			for(int i=0; i<keys.length; i++) {
				switch(rdataList.at(i).rtype) {
				//rtype 은 https://github.com/s-u/rJava/blob/master/jri/REXP.java 참고
				case REXP.LGLSXP: //logical vectors, 10
					modelData[i] = rdataList.at(i).asBool();
					break;
				case REXP.INTSXP: //integer vectors, 13
					modelData[i] = rdataList.at(i).asIntArray();
					break;
				case REXP.REALSXP: //real variables, 14
					modelData[i] = rdataList.at(i).asDoubleArray();
					break;
				case REXP.STRSXP: //string vectos, 16
					modelData[i] = rdataList.at(i).asStringArray();
					break;
				}
				rData.put("data", modelData);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return rData;
	}

	@Override
	public Map<String, Object> getSummary(int fileId) {
		Map<String, Object> rData = new Hashtable<String, Object>();

		UploadFileVO file = fileService.getFile(fileId);
		byte[] data = file.getFileData();

		long exp;
		try {
			exp = rEngine.rniPutString(new String(data, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		rEngine.rniAssign("data", exp, 0);
		
		rEngine.eval("Sys.setlocale(category=\"LC_ALL\", locale=\"English_United States.1252\")");
		rEngine.eval("dataFrame <- read.table(text=data, sep=\",\", fill=TRUE, header=TRUE, stringsAsFactors=FALSE, skipNul=TRUE, encoding=\"UTF-8\", fileEncoding=\"UTF-8\")");

		REXP summary = rEngine.eval("(dfSummary <- summary(dataFrame))");
		if(summary == null)
			throw new RuntimeException("No data for summary");
		rData.put("summary", summary.asStringArray());
		
		REXP colNames = rEngine.eval("colnames(dfSummary)");
		rData.put("colNames", colNames.asStringArray());

		REXP nrow = rEngine.eval("nrow(dfSummary)");
		rData.put("nrow", nrow.asInt());
		
		return rData;
	}

	
	@Override
	public SummaryVO getSummaryList(int fileId) {
		
		UploadFileVO file = fileService.getFile(fileId);
		byte[] data = file.getFileData();

		long exp;
		try {
			exp = rEngine.rniPutString(new String(data, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		rEngine.rniAssign("data", exp, 0);
		
		rEngine.eval("Sys.setlocale(category=\"LC_ALL\", locale=\"English_United States.1252\")");
		rEngine.eval("dataFrame <- read.table(text=data, sep=\",\", fill=TRUE, header=TRUE, stringsAsFactors=FALSE, skipNul=TRUE, encoding=\"UTF-8\", fileEncoding=\"UTF-8\")");

		REXP rsummary = rEngine.eval("(dfSummary <- summary(dataFrame))");
		if(rsummary == null)
			throw new RuntimeException("No data for summary");
		
		SummaryVO summary = new SummaryVO();
		
		REXP rcolNames = rEngine.eval("colnames(dfSummary)");
		summary.setColNames(rcolNames.asStringArray());
		summary.setValues(rsummary.asStringArray());
		System.out.println(summary);
		return summary;
	}
}
