
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
		require_once('Apache/Solr/Service.php')             						//UPDATE THIS
		
		// Create a new Solr Service instance - host, port and corename
		// paths (all defaults in this example)
		$solr = new Apache_Solr_Service('localhost', 8983, '/solr/core_name/');   	//UPDATE THIS
		
		// If magic quotes is enabled then stripslashes are needed
		if (get_magic_quotes_gpc() == 1) {
			$query = stripslashes($query);
		}
		
		// In production code, use try/catch for any possible exceptions emitted 
		// by searching (i.e. connection problems or query parsing error)
		try {
			$results = $solr->search($query, 0, $limit);
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
			<input type="submit" />
		</form>
	</body>
</html>

