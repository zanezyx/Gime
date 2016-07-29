<?php
include 'header.php';
include 'jsonUtil.php';
include 'connect_mysql_db.php';

//$imei = file_get_contents("php://input");
//echo $imei;
$json_string = file_get_contents("php://input");
$obj = json_decode($json_string);
//echo $json_string;
$imei = $obj -> imei;
$ids = $obj -> ids;
//print_r($ids);
$strsql = "select * from location_operation where imei="."\"".$imei."\""." and 	locationStatus="."2" ;
//$strsql = "select * from location_operation where imei="."\"".$imei."\"";

//print $strsql;

$result = mysql_db_query($mysql_database, $strsql, $conn);


/*$row = mysql_fetch_row($result);*/
//print_r($result);
if($result)
{
	/*数据集 */
	$locationlist=array();
	$i=0;
//	echo "step";
	while($row=mysql_fetch_array($result,MYSQL_ASSOC)){

//		echo $row['id'].'-----'.'</br>';
//		echo count($ids);
		for($j=0;$j<count($ids);$j++)
		{
//			echo $row['id'].'-----------'.$ids[$j]."line";
			if($row['id']==$ids[$j])
			{
				$locationlist[$i]=$row;
				$i++;
			}
		}
	}
	if (count($locationlist)==0) {
		echo "none";
	}else{
		echo JSON(array('locationlist'=>$locationlist));
	}
	
}else{
	echo "none";
}

mysql_free_result($result);
mysql_close();

?>


