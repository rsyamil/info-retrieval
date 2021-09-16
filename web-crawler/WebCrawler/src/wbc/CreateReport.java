package wbc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.*;

public class CreateReport {
	public static String fetchcsv = "fetch_nytimes.csv";
	public static String visitcsv = "visit_nytimes.csv";
	public static String urlscsv = "urls_nytimes.csv";
	
    public ArrayList<FetchObj> fetchObjList = new ArrayList<>();			//fetch_nytimes.csv
    public ArrayList<DownloadObj> downloadObjList = new ArrayList<>();		//visit_nytimes.csv
    public ArrayList<DiscoverObj> discoverObjList = new ArrayList<>();		//urls_nytimes.csv
	
	public static String fileReportTxt = "CrawlReport_nytimes.txt";
	
	public CreateReport() {
		super();
	}
	
	public static ArrayList<FetchObj> readCSVfetch(String csvFilename) throws IOException {
		ArrayList<FetchObj> fetchObjList = new ArrayList<>();
	    try(BufferedReader br = new BufferedReader(new FileReader(csvFilename))) {
	        String line = "";
	        String header = br.readLine();				//do nothing 
	        while ((line = br.readLine()) != null) {
	        	List<String> tokens = Arrays.asList(line.split(","));
	        	fetchObjList.add(new FetchObj(tokens.get(0), Integer.parseInt(tokens.get(1))));   
	        }
	    } catch (FileNotFoundException e) {
	      //Some error logging
	    }
	    return fetchObjList;
	}
	
	public static ArrayList<DownloadObj> readCSVvisit(String csvFilename) throws IOException {
		ArrayList<DownloadObj> downloadObjList = new ArrayList<>();
	    try(BufferedReader br = new BufferedReader(new FileReader(csvFilename))) {
	        String line = "";
	        String header = br.readLine();				//do nothing 
	        while ((line = br.readLine()) != null) {
	        	List<String> tokens = Arrays.asList(line.split(","));
	        	downloadObjList.add(new DownloadObj(tokens.get(0), Integer.parseInt(tokens.get(2)), Integer.parseInt(tokens.get(1)), tokens.get(3)));   
	        }
	    } catch (FileNotFoundException e) {
	      //Some error logging
	    }
	    return downloadObjList;
	}
	
	public static ArrayList<DiscoverObj> readCSVurls(String csvFilename) throws IOException {
		ArrayList<DiscoverObj> discoverObjList = new ArrayList<>();
	    try(BufferedReader br = new BufferedReader(new FileReader(csvFilename))) {
	        String line = "";
	        String header = br.readLine();				//do nothing 
	        while ((line = br.readLine()) != null) {
	        	List<String> tokens = Arrays.asList(line.split(","));
	        	discoverObjList.add(new DiscoverObj(tokens.get(0), tokens.get(1)));   
	        }
	    } catch (FileNotFoundException e) {
	      //Some error logging
	    }
	    return discoverObjList;
	}
	
	public static void tabulateStatusCode(List<FetchObj> fetchObjList) {
		Set<String> list_status_code = new HashSet<String>();
		Map<String, Integer> dictionary_status_code = new HashMap<String, Integer>();
		for (FetchObj fo:fetchObjList) {
			String scode = Integer.toString(fo.statusCode);
			list_status_code.add(scode);
			
			if (dictionary_status_code.containsKey(scode)) {
				int val = dictionary_status_code.get(scode);
				dictionary_status_code.put(scode, val + 1);
			} else {
				dictionary_status_code.put(scode, 1);
			}
		}
		System.out.println("Unique status code: " + list_status_code);
		System.out.println("Unique status code with count: " + dictionary_status_code);
	}
	
	public static List<DiscoverObj> getUniqueUrls(List<DiscoverObj> discoverObjList) {
		Set<String> uniqueUrls = new HashSet<String>();
		int OK_unique = 0;
		int N_OK_unique = 0;
		for (DiscoverObj doo:discoverObjList) {
			String url = doo.url;
			String okFlag = doo.okFlag;
			//remove http:// or https://
			if (url.startsWith("http://")) {
				url = url.substring("http://".length());
			}
			if (url.startsWith("https://")) {
				url = url.substring("https://".length());
			}
			if (uniqueUrls.contains(url)) {
				// do nothing since it has been seen
			} else {
				uniqueUrls.add(url);
				if (okFlag.equals("OK")) {
					OK_unique = OK_unique + 1;
				} else {
					N_OK_unique = N_OK_unique + 1;
				}
			}
		}
		System.out.println("Unique URLs: " + uniqueUrls.size());
		System.out.println("Unique okFlag with count: OK " + OK_unique + " N_OK " + N_OK_unique);
		return null;
	}
	
	public static void tabulateOkFlag(List<DiscoverObj> discoverObjList) {
		int OK = 0;
		int N_OK = 0;
		for (DiscoverObj doo:discoverObjList) {
			String okFlag = doo.okFlag;
			if (okFlag.equals("OK")) {
				OK = OK + 1;
			} else {
				N_OK = N_OK + 1;
			}
		}
		System.out.println("OkFlag with count: OK " + OK + " N_OK " + N_OK);
	}
	
	public static void tabulateContentType(List<DownloadObj> downloadObjList) {
		Set<String> list_content_type = new HashSet<String>();
		Map<String, Integer> dictionary_content_type = new HashMap<String, Integer>();
		for (DownloadObj dw:downloadObjList) {
			String contentType = dw.contentType;
			list_content_type.add(contentType);
			
			if (dictionary_content_type.containsKey(contentType)) {
				int val = dictionary_content_type.get(contentType);
				dictionary_content_type.put(contentType, val + 1);
			} else {
				dictionary_content_type.put(contentType, 1);
			}
		}
		System.out.println("Unique content type: " + list_content_type);
		System.out.println("Unique content type with count: " + dictionary_content_type);
	}
	
	public static void tabulateSizes(List<DownloadObj> downloadObjList) {
		int tier1 = 0;
		int tier2 = 0;
		int tier3 = 0;
		int tier4 = 0;
		int tier5 = 0;		
		int sum_outgoinglinks = 0;
		for (DownloadObj dw:downloadObjList) {
			sum_outgoinglinks = sum_outgoinglinks + dw.outlinks;
			int size = dw.size;
			if (size < 1024) {
				tier1 = tier1 + 1;
			} else if (size >= 1024 && size < 10240) {
				tier2 = tier2 + 1;
			} else if (size >= 10240 && size < 102400) {
				tier3 = tier3 + 1;
			} else if (size >= 102400 && size < 1024000) {
				tier4 = tier4 + 1;
			} else {
				tier5 = tier5 + 1;
			}
		}
		System.out.println("Unique sizes with count: t1 " + tier1 + " t2 " + tier2 + " t3 " + tier3 + " t4 " + tier4 + " t5 " + tier5);
		System.out.println("Sum of outgoing links: " + sum_outgoinglinks);
	}
	
	public static void main(String[] args) throws Exception {
		
		List<FetchObj> fetchObjList = readCSVfetch(fetchcsv);
		List<DownloadObj> downloadObjList = readCSVvisit(visitcsv);
		List<DiscoverObj> discoverObjList = readCSVurls(urlscsv);
		
		System.out.println(fetchObjList.size());
		System.out.println(downloadObjList.size());
		System.out.println(discoverObjList.size());

		/*
		 * Count Status codes
		 */
		tabulateStatusCode(fetchObjList);
		tabulateOkFlag(discoverObjList);
		tabulateContentType(downloadObjList);
		tabulateSizes(downloadObjList);
		getUniqueUrls(discoverObjList);

	}
}
