import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class ExtractLinks {
	public static void main(String[] args) throws Exception {
		
		String filesFolderDir = "C:/Linux-Ubuntu/NYTIMES/nytimes";
		String csvDir = "C:/Linux-Ubuntu/NYTIMES/URLtoHTML_nytimes_news.csv";
		String outDir = "C:/Linux-Ubuntu/NYTIMES/edges.txt";
		
		File filesFolder = new File(filesFolderDir);
		FileReader csv = new FileReader(csvDir);

		Map<String, String> filenameToUrl = new HashMap<>();
        Map<String, String> urlToFilename = new HashMap<>();
        
        Scanner scanner = new Scanner(csv);
        while (scanner.hasNext()) {
        	//System.out.println(scanner.next());
        	String[] tokens = scanner.next().split(",");
        	filenameToUrl.put(tokens[0], tokens[1]);
        	urlToFilename.put(tokens[1], tokens[0]);
        }
        scanner.close();
        System.out.println("Scanning URLs");
        
        // get the links/edges
        int count = 0;
        Set<String> edges = new HashSet<>();
        for (File f: filesFolder.listFiles()) {
        	
        	System.out.println("File count : " + Integer.toString(count));
        	count = count + 1;
        	
        	Document doc = Jsoup.parse(f, "UTF-8", filenameToUrl.get(f.getName()));
        	Elements links = doc.select("a[href]");
        	
        	for (Element link: links) {
                String url = link.attr("abs:href").trim();
                if (urlToFilename.containsKey(url))
                    edges.add(f.getName() + " " + urlToFilename.get(url));
            }
        }
        System.out.println("Calculating edges");
        
        // write to file
        FileWriter writer = new FileWriter(outDir);
        for (String e: edges) {
        	writer.write(e +"\n");
        }
        writer.close();
        System.out.println("Done");
    }
}


