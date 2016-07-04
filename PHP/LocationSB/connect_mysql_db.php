
<?php
$mysql_server_name = "localhost";

$mysql_username = "gime";

$mysql_password = "gime";

$mysql_database = "location";

$conn = mysql_connect($mysql_server_name, $mysql_username, $mysql_password);

mysql_query("set names 'utf8'");
?>