package wbc;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyCrawler extends WebCrawler{

	public static Logger logger = LoggerFactory.getLogger(MyCrawler.class);
	
    public final static Pattern FILTERS_ALLOWED = Pattern.compile(".*(\\.(html|doc|pdf|jpg|png|bmp|gif))$");
    public final static Pattern FILTERS_END = Pattern.compile("(^$|.*\\/[^(\\/\\.)]*$)"); //for the link with no extension
    
    public ArrayList<FetchObj> fetchObjList = new ArrayList<>();			//fetch_nytimes.csv
    public ArrayList<DownloadObj> downloadObjList = new ArrayList<>();		//visit_nytimes.csv
    public ArrayList<DiscoverObj> discoverObjList = new ArrayList<>();		//urls_nytimes.csv
    
	@Override
	public MyCrawler getMyLocalData() {
		return this;
	}
	
	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		fetchObjList.add(new FetchObj(webUrl.getURL(), statusCode));
	}
    
	/*
	 * This method receives two parameters. The first parameter is the page
	 * in which we have discovered this new url and the second parameter is
	 * the new url. You should implement this function to specify whether
	 * the given url should be crawled or not (based on your crawling logic).
	 * In this example, we are instructing the crawler to ignore urls that
	 * have css, js, git, ... extensions and to only accept urls that start
	 * with "http://www.viterbi.usc.edu/". In this case, we didn't need the
	 * referringPage parameter to make the decision.
	 * !page.getContentType().contains(“application.json”)   //if they dont filter json because the link has no extension
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		URL verifyUrl = null;
		String href = url.getURL().toLowerCase();
		
		try {
			verifyUrl = new URL(href);
		} catch(Exception err) {
			logger.error("URL not valid : " + href + err);
		}
		String verifyUrlHost = verifyUrl.getHost();
		if (!verifyUrlHost.equals(Controller.SITE_CRAWL)) {
			return false;
		}
		String verifyPath = url.getPath();
		if (FILTERS_END.matcher(verifyPath).matches()) {
			return true;
		}
        return FILTERS_ALLOWED.matcher(verifyPath).matches();
	}
	
	/*
	 * This function is called when a page is fetched and ready
	 * to be processed by your program.
	 */
	@Override
	public void visit(Page page) {
		
		PrintWriter fout = null;
		try {
			fout = new PrintWriter(new FileOutputStream(new File("test.txt"), true));
			fout.append("TEST OPEN");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String url = page.getWebURL().getURL();
		int nBytes = page.getContentData().length;				//in bytes
		int nOutlinks = page.getParseData().getOutgoingUrls().size();
		String contentType = page.getContentType();
		if (contentType.toLowerCase().startsWith("text/html")) {
			contentType = "text/html";
		} 
		
		fout.append("URL: " + url +"\n");

		downloadObjList.add(new DownloadObj(url, nOutlinks, nBytes, contentType));
		
		Set<WebURL> webUrlLinks = page.getParseData().getOutgoingUrls();
		for (WebURL webUrl:webUrlLinks) {
			//String processUrl = webUrl.getURL().toLowerCase();
			String processUrl = webUrl.getURL();
			String okFlag = "N_OK";
			
			try {
				URL verifyUrl = new URL(processUrl);
				String verifyUrlHost = verifyUrl.getHost();
				if (verifyUrlHost.equals(Controller.SITE_CRAWL)) {
					okFlag = "OK";
				}
			} catch (MalformedURLException err) {
				// TODO Auto-generated catch block
				logger.error("URL not OK : " + processUrl + err);
				if (processUrl.startsWith("https://" + Controller.SITE_CRAWL)) {
					okFlag = "OK";
				}
				if (processUrl.startsWith("http://" + Controller.SITE_CRAWL)) {
					okFlag = "OK";
				}
			}
			
			discoverObjList.add(new DiscoverObj(processUrl, okFlag));
		}

		// QC
		boolean printToTxtDebug = false;
		if (printToTxtDebug) {
			if (page.getParseData() instanceof HtmlParseData || page.getParseData() instanceof BinaryParseData) {
				HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
				String text = htmlParseData.getText();
				String html = htmlParseData.getHtml();
				Set<WebURL> links = htmlParseData.getOutgoingUrls();
				
				fout.append("Text length: " + text.length() +"\n");
				fout.append("Html length: " + html.length() +"\n");
				fout.append("Number of outgoing links: " + links.size() +"\n");
			}
		}
		fout.close();
		
	}
	
	
}


