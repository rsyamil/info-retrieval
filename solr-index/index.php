
<?php
	//browser sees this page as HTML utf-8 encoded
	header('Context-Type: text/html; charset=utf-8');
	
	$limit 		= 10;
	$results 	= false;
	$query 		= isset($_REQUEST['q']) ? $_REQUEST['q']: false;
	
	if ($query) {
		// The Apache Solr Client library should be on the include path
		// which is usually most easily accomplished by placing in the
		// same directory as this script ( . or current directory is a default
		// php include path entry in the php.ini)
		require_once('Apache/Solr/Service.php');
		
		// Create a new Solr Service instance - host, port and corename
		// paths (all defaults in this example)
		$solr = new Apache_Solr_Service('localhost', 8983, '/solr/myexample/'); 
		
		// If magic quotes is enabled then stripslashes are needed
		if (get_magic_quotes_gpc() == 1) {
			$query = stripslashes($query);
		}
		
		// In production code, use try/catch for any possible exceptions emitted 
		// by searching (i.e. connection problems or query parsing error)
		try {
			// Options to select Lucene or Pagerank
			if ($_REQUEST['algorithm'] == "lucene") {
				$results = $solr->search($query, 0, $limit);
			} elseif ($_REQUEST['algorithm'] == "pagerank"){
				$additionalParameters = array('sort' => 'pageRankFile desc');
				$results = $solr->search($query, 0, $limit, $additionalParameters);
			} else {
				// Future algorithms
			}
			
		} catch (Exception $e) {
			// In prod code, log or email error to an admin
			// show special message to the user but for this example
			// we show the full exceptions
			die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");			
		}
	}
?>

<html>
	<head>
		<title> PHP Solr Client Example </title>
	</head>
	
	<body>
		<h1> PHP Solr Client Example </h1>
		<form accept-charset="utf-8" method="get">
			<label for="q"> Search: </label>
			<input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>" />
			
			<input type="radio" name="algorithm" value="pagerank" <?php if (isset($_REQUEST['algorithm']) && $_REQUEST['algorithm'] == 'pagerank') {
						echo 'checked = "checked"';
					}
				?>> PageRank
			<input type="radio" name="algorithm" value="lucene" <?php if (isset($_REQUEST['algorithm']) && $_REQUEST['algorithm'] == 'lucene') {
						echo 'checked = "checked"';
					}
				?>> Lucene
			
			<input type="submit" />
		</form>
		
	<?php
	// display results
	if ($results) {

		$total = (int) $results->response->numFound;
		$start = min(1, $total);
		$end = min($limit, $total);
	?>
		<div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>
		<ol>
		<?php
		// iterate result documents
		foreach ($results->response->docs as $doc) {
		?>
		<li>
			<table style="border: 1px solid black; text-align: left; width:100%">
			<tr>
			<td>
				<?php
				$title = $doc->title;
				$url = $doc->og_url;
				//$url = $doc->url;
				$id = $doc->id;
				$desc = $doc->og_description;
				//$desc = $doc->description;
				
				if ($desc == "") {
					$desc = "null";
				}
				
				// Iteratre results documents
				$csv = array_map('str_getcsv', file('/home/emil/Desktop/NYTIMES/URLtoHTML_nytimes_news.csv'));
				
				// If URL not available, find from csv file
				if ($url == "") {                                   //update
					foreach ($csv as $rows => $row) {
						
						$filenamecsv = $row[0];
						$urlnamecsv = $row[1];
						$localpath = "/home/emil/Desktop/NYTIMES/nytimes/".$filenamecsv;
						
						//echo "verify</br>";
						//echo "Test file name $row[0]</br>";
						//echo "Test file name $localpath</br>";
						//echo "Test url name $row[1]</br>";
						
						if (strcmp((string)$localpath, (string)$id) == 0) {
							
							//echo strcmp((string)$localpath, (string)$id);
							//echo "same</br>";
							//echo "Local path:  $localpath</br>";
							//echo "Id:  $id</br>";
							
							$url = $urlnamecsv;
							break;
						}
					}
				}
				
				echo "Title 		: <a href = '$url'>$title</a></br>";
				echo "URL 			: <a href = '$url'>$url</a></br>";
				echo "ID			: $id</br>";
				echo "Description	: $desc</br>";
				?>
			</td>
			</tr>
			</table>
		</li>
		<?php
		}
		?>
		</ol>
	<?php
	}
	?>

	</body>
</html>










