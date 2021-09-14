package wbc;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
	
	public static final String SITE_CRAWL = "https://www.foxnews.com/";

	public static void main(String[] args) throws Exception {
		String crawlStorageFolder = "/data/crawl";
		String fetchcsv = "fetch_nytimes.csv";
		String visitcsv = "visit_nytimes.csv";
		String urlscsv = "urls_nytimes.csv";
		
		int numberOfCrawlers = 2;
		
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);
		
		// additional customizable parameters
		config.setMaxDepthOfCrawling(16);
		config.setMaxPagesToFetch(50);					//change to 20000
		config.setPolitenessDelay(2000);  				//assume in ms
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
		
		controller.addSeed(SITE_CRAWL);
		
		/*
		 * Start the crawl. This is a blocking operation, meaning that the code will reach the line after this
		 * only when crawling is finished
		 */
		System.out.println("Begin crawling...");
		CrawlController.WebCrawlerFactory<WebCrawler> factory = MyCrawler::new;
		controller.start(factory, numberOfCrawlers);
		
		System.out.println("DONEEEEEEEEEEEE");
	}

}
