app.controller('myCtrl', function($scope,$http,$interval,$timeout) {
	$http({
		method: 'GET',
		url:"/activities/allActivities.json",
		cache:false,
		async:false}).then(function(res){
		var items = res.data.result;
		
	 })
	 
	 $scope.submit=function(obj){
		 var currentSubsidiesTel = localStorage.getItem("currentSubsidiesTel");
		 var activitiesId = localStorage.getItem("activitiesId");
		 $http({
			method: 'POST',
			url:"/activities/update.json",
			cache:false,
			async:false}).then(function(res){
			var items = res.data.result;
			window.location.href="/activities/success.html";
			
		 })
	 }
})