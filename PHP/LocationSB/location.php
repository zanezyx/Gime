<?php


include 'header.php';
include 'jsonUtil.php';
include 'LocationOperation.php';
include 'connect_mysql_db.php';
$operation = new LocationOperation();

$json_string = file_get_contents("php://input");
$obj = json_decode($json_string);
//echo $json_string;
$imei = $obj -> imei;
$type = $obj -> type;
$number = $obj -> number;

/*$operation->setImei($imei);
$operation->setType($type);
$operation->setNumber($number);
$operation->setLatitude(0.0);
$operation->setLongitude(0.0);
$operation->setLocationStatus(LOCATION_STATE_LOCATION_WAIT);*/


//$strsql = "select * from location_operation where number="."\"".$number."\""." and type=".$type ;
//print $strsql;
//
//$result = mysql_db_query($mysql_database, $strsql, $conn);
//
//
//$row = mysql_fetch_row($result);
//
//if ($row) {
//	echo "exist";
//} else {
	$strsql = "insert into location_operation(imei,type,number) values("."\"".$imei."\"".","."\"".$type."\"".","."\"".$number."\"".")";
	echo $strsql;
	mysql_select_db($mysql_database, $conn);
	if(mysql_query($strsql))
	{
		$id = mysql_insert_id();
		echo $id;
	}else{
		echo "fail";
	}
//}


mysql_free_result($result);
mysql_close();

?>



