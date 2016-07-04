<?php


include 'header.php';
include 'jsonUtil.php';
include 'connect_mysql_db.php';


#$json_string = file_get_contents("php://input");
#$obj = json_decode($json_string);
#$username = $obj -> username;
#$password = $obj -> password;

$username = $_GET['mobile'] ;
$password = $_GET['password'] ;


$strsql = "select * from users where mobile="."\"".$username."\"";
//print $strsql;

$result = mysql_db_query($mysql_database, $strsql, $conn);


$row = mysql_fetch_row($result);

if ($row) {
	echo "1";
} else {
	$strsql = "insert into users(mobile,password) values("."\"".$username."\"".","."\"".$password."\"".")";
	
	mysql_select_db($mysql_database, $conn);
	// echo $strsql;
	if(mysql_query($strsql))
	{
		echo "0";
	}else{
		echo "2";
	}
}


mysql_free_result($result);


mysql_close();
?>