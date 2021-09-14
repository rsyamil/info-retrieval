package wbc;

public class DownloadObj {
	public String url;
	public int outlinks;
	public int size;			//in bytes
	public String contentType;
	
	public DownloadObj(String url, int outlinks, int size, String contentType) {
		super();
		this.url = url;
		this.outlinks = outlinks;
		this.size = size;
		this.contentType = contentType;
	}

}
