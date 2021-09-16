package wbc;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Controller {
	
	public static final String SITE_CRAWL = "www.nytimes.com";
	public static Logger logger = LoggerFactory.getLogger(Controller.class);

	public static void main(String[] args) throws Exception {
		String crawlStorageFolder = "C:\\Users\\13108\\Desktop\\info-retrieval\\web-crawler\\WebCrawler\\data\\crawl";
		String fetchcsv = "fetch_nytimes.csv";
		String visitcsv = "visit_nytimes.csv";
		String urlscsv = "urls_nytimes.csv";
		
		int numberOfCrawlers = 7;
		
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);
		
		// additional customizable parameters
		config.setMaxDepthOfCrawling(16);
		config.setMaxPagesToFetch(20000);				//change to 20000
		config.setPolitenessDelay(220);  				//assume in ms
		config.setUserAgentString("Emil");
		config.setIncludeBinaryContentInCrawling(true); //get pdf and doc etc
		config.setIncludeHttpsPages(true);
		config.setFollowRedirects(true);
		
		/*
		 * Instantiate the controller for this crawl
		 */
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

		/*
		 * For each crawl, add some seed URLs. These are the first URLs that are fetched and then crawler starts
		 * following links which are found in these pages
		 */
		
		controller.addSeed("https://"+SITE_CRAWL);
		
		/*
		 * Start the crawl. This is a blocking operation, meaning that the code will reach the line after this
		 * only when crawling is finished
		 */
		System.out.println("Begin crawling...");
		logger.info("Begin crawling...");
		
		CrawlController.WebCrawlerFactory<WebCrawler> factory1 = MyCrawler::new;
		//controller.start(factory1, numberOfCrawlers);
		controller.startNonBlocking(factory1, 7);
		controller.waitUntilFinish();
		
		ArrayList<FetchObj> fetchObjList = new ArrayList<>();			//fetch_nytimes.csv
		ArrayList<DownloadObj> downloadObjList = new ArrayList<>();		//visit_nytimes.csv
		ArrayList<DiscoverObj> discoverObjList = new ArrayList<>();		//urls_nytimes.csv
		
		/*
		 * Get data from each of the crawler and process the data
		 */
		
		List<Object> crawlers = controller.getCrawlersLocalData();
		for (Object myCrawler:crawlers) {
			MyCrawler myCrawler_cast = (MyCrawler)myCrawler;
			fetchObjList.addAll(myCrawler_cast.fetchObjList);
			downloadObjList.addAll(myCrawler_cast.downloadObjList);
			discoverObjList.addAll(myCrawler_cast.discoverObjList);
		}
		
		/*
		 * Write to CSV files for the three CSV files
		 */
		PrintWriter fout_fetch = null;
		try {
			fout_fetch = new PrintWriter(new FileOutputStream(new File(fetchcsv), true));
			fout_fetch.append("URL" +","+ "Status" + "\n");
			for (FetchObj fetchdata:fetchObjList) {
				fout_fetch.append(fetchdata.url +","+ fetchdata.statusCode + "\n");
			}
			fout_fetch.close();
		} catch (FileNotFoundException err) {
			logger.error("Error in CSV fetch.csv : " + err);
		}
		PrintWriter fout_visit = null;
		try {
			fout_visit = new PrintWriter(new FileOutputStream(new File(visitcsv), true));
			fout_visit.append("URL" +","+ "Size (bytes)" +","+ "Outgoing Links" +","+ "Content Type"+ "\n");
			for (DownloadObj downloaddata:downloadObjList) {
				fout_visit.append(downloaddata.url +","+ downloaddata.size +","+ downloaddata.outlinks +","+ downloaddata.contentType + "\n");
			}
			fout_visit.close();
		} catch (FileNotFoundException err) {
			logger.error("Error in CSV visit.csv : " + err);
		}
		
		
		PrintWriter fout_discover = null;
		try {
			fout_discover = new PrintWriter(new FileOutputStream(new File(urlscsv), true));
			fout_discover.append("URL" +","+ "OK_N_OK" + "\n");
			for (DiscoverObj discoverdata:discoverObjList) {
				fout_discover.append(discoverdata.url +","+ discoverdata.okFlag + "\n");
			}
			fout_discover.close();
		} catch (FileNotFoundException err) {
			logger.error("Error in CSV urls.csv : " + err);
		}
		
		System.out.println("DONEEEEEEEEEEEE");
	}

}
