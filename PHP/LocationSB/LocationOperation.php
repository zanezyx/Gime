<?php
class LocationOperation {
  /* 成员变量 */
  var $imei;
  var $type;
  var $number;
  var $latitude;
  var $longitude;
  var $locationStatus;


  /* 成员函数 */
  function setImei($par){
     $this->$imei = $par;
  }
  
  function getImei(){
     echo $this->$imei . PHP_EOL;
  }
  
  function setType($par){
     $this->$type = $par;
  }
  
  function getType(){
     echo $this->$type . PHP_EOL;
  }
  
  function setNumber($par){
     $this->$number = $par;
  }
  
  function getNumber(){
     echo $this->$number . PHP_EOL;
  }
  
  function setLatitude($par){
     $this->$latitude = $par;
  }
  
  function getLatitude(){
     echo $this->$latitude . PHP_EOL;
  }
  
  
  function setLongitude($par){
     $this->$longitude = $par;
  }
  
  function getLongitude(){
     echo $this->$longitude . PHP_EOL;
  }
  
  
  function setLocationStatus($par){
     $this->$locationStatus = $par;
  }
  
  function getLocationStatus(){
     echo $this->$locationStatus . PHP_EOL;
  }
}
?>





