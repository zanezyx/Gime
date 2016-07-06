<?php

include 'header.php';
include 'jsonUtil.php';
include 'LocationOperation.php';
include 'connect_mysql_db.php';

$id = $_GET["id"];
$latitude = $_GET["latitude"];
$longitude = $_GET["longitude"];



$strsql = "select * from location_operation where id=".$id;
#print $strsql;

$result = mysql_db_query($mysql_database, $strsql, $conn);

$row = mysql_fetch_row($result);

if ($row) {
	$strsql ="update location_operation set latitude=" .$latitude.",longitude=".$longitude.",locationStatus=2"." where id=" . $id;
	echo $strsql;
	mysql_select_db($mysql_database, $conn);
	if(mysql_query($strsql))
	{
		echo "success";
	}else{
		echo "fail";
	}
} else {
	echo "none";
}


mysql_free_result($result);
mysql_close();


?>






