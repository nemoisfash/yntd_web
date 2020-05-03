var app = angular.module('myApp', []);
app.controller('myCtrl', function($scope,$http) {
	$http({
		method: 'GET',
		url:"/activities/allActivities.json",
		cache:false,
		async:false}).then(function(res){
		$scope.items = res.data.data;
		console.info($scope.items);
	 })
	 
	$scope.addClass=function(evn,id){
		$(evn.target).addClass('on').siblings().removeClass('on');
		localStorage.setItem("activitiesId",id);
	}
	 
	$scope.submit=function(){
		 var currentSubsidiesTel = localStorage.getItem("currentSubsidiesTel");
		 var activitiesId = localStorage.getItem("activitiesId");
		 if(currentSubsidiesTel==null){
			 console.info("undefined");
			 window.location.href="/index";
		 }
		 
		$.ajax({
		  type: 'POST',
		  url: "/activities/update",
		  data: {"currentSubsidiesTel":currentSubsidiesTel,"activitiesId":activitiesId},
		  dataType: "JSON",
		  success: function(data){
			if(data.result){
				  localStorage.clear();
				  window.location.href="/activities/success.html";
			  }
			},
		 });
	}
	
})