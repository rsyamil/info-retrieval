import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

import org.apache.tika.Tika;

public class ExtractWords {
	public static void main(String[] args) throws Exception {
		
		Tika tika = new Tika();
		
		String filesFolderDir = "C:/Linux-Ubuntu/NYTIMES/nytimes";
		String outDir = "C:/Linux-Ubuntu/NYTIMES/big.txt";
		
		File filesFolder = new File(filesFolderDir);
		FileWriter writer = new FileWriter(outDir);
		
		int count = 0;
		for (File f: filesFolder.listFiles()) {
			
			System.out.println("File count : " + Integer.toString(count));
			count = count + 1;
			
			//parse file
			String text = tika.parseToString(f);
			text = text.replaceAll("\\s", " ");
			writer.write(text +"\n");
		}
		
        writer.close();
        System.out.println("Done");
		
	}
}
