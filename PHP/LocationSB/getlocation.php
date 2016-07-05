
<!DOCTYPE html> 
<html> 
<head> 
    <meta charset="utf-8"/> 
    <title>基于浏览器的HTML5查找地理位置<?php
echo  "hello!!!"
?></title>
    <!-- 百度API -->
 <script src="http://api.map.baidu.com/api?v=1.2" type="text/javascript"></script>
  <script>

           function getLocation(){
               var options={
                   enableHighAccuracy:true,
                   maximumAge:1000
               }
               if(navigator.geolocation){
                   //浏览器支持geolocation
                   navigator.geolocation.getCurrentPosition(onSuccess,onError,options);
               }else{
                   //浏览器不支持geolocation
                   alert('您的浏览器不支持地理位置定位');
               }
           }

           //成功时
           function onSuccess(position){
               //返回用户位置
               //经度
               var longitude =position.coords.longitude;
               //纬度
               var latitude = position.coords.latitude;

    alert('经度'+longitude+'，纬度'+latitude);
              
               //根据经纬度获取地理位置，不太准确，获取城市区域还是可以的
    var map = new BMap.Map("allmap");
    var point = new BMap.Point(longitude,latitude);
    var gc = new BMap.Geocoder();
    gc.getLocation(point, function(rs){
       var addComp = rs.addressComponents;
       alert(addComp.province + ", " + addComp.city + ", " + addComp.district + ", " + addComp.street + ", " + addComp.streetNumber);
    });

           }

           //失败时
           function onError(error){
               switch(error.code){
                   case 1:
                   alert("位置服务被拒绝");
                   break;

                   case 2:
                   alert("暂时获取不到位置信息");
                   break;

                   case 3:
                   alert("获取信息超时");
                   break;

                   case 4:
                    alert("未知错误");
                   break;
               }

           }

           window.onload=getLocation;
        
   </script>
</head>
<body>
</body>
</html>