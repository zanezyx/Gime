
<?php
$mysql_server_name = "localhost";

$mysql_username = "root";

$mysql_password = "root";

$mysql_database = "balilai";

$conn = mysql_connect($mysql_server_name, $mysql_username, $mysql_password);

mysql_query("set names 'utf8'");
?>