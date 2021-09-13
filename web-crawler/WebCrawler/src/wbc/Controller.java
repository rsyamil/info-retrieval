package wbc;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {

	public static void main(String[] args) throws Exception {
		String crawlStorageFolder = "/data/crawl";
		int numberOfCrawlers = 7;
		
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);
		
		// additional customizable parameters
		config.setMaxDepthOfCrawling(3);
		config.setMaxPagesToFetch(10);
		config.setPolitenessDelay(500);  //assume in ms
		config.setUserAgentString("Emil");
		
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
		
		controller.addSeed("http://www.viterbi.usc.edu/");
		
		/*
		 * Start the crawl. This is a blocking operation, meaning that the code will reach the line after this
		 * only when crawling is finished
		 */
		
		controller.start(MyCrawler.class, numberOfCrawlers);
	}

}
