<?php
include 'header.php';
include 'jsonUtil.php';
include 'connect_mysql_db.php';

$imei = file_get_contents("php://input");

//echo $imei;
//$strsql = "select * from location_operation where imei="."\"".$imei."\""." and type="."2" ;
$strsql = "select * from location_operation where imei="."\"".$imei."\"";

#print $strsql;

$result = mysql_db_query($mysql_database, $strsql, $conn);


/*$row = mysql_fetch_row($result);*/

if($result)   
{   
    /*数据集 */
    $locationlist=array(); 
    $i=0; 
    while($row=mysql_fetch_array($result,MYSQL_ASSOC)){ 
 
            //echo $row['id'].'-----------'.$row['name'].$row['password'].'</br>'; 
            $locationlist[$i]=$row; 
            $i++; 
 
    } 
    echo JSON(array('locationlist'=>$locationlist)); 

}else{
	echo "none";
}  

mysql_free_result($result);
mysql_close();



?>