document.getElementById("default").click();
function openPanel(evt, x) {
  var i, tabcontent, tablinks;
  tabcontent = document.getElementsByClassName("tabcontent");
  for (i = 0; i < tabcontent.length; i++) {
    tabcontent[i].style.display = "none";
  }
  tablinks = document.getElementsByClassName("tablinks");
  for (i = 0; i < tablinks.length; i++) {
    tablinks[i].className = tablinks[i].className.replace(" active", "");
  }
  document.getElementById(x).style.display = "block";
  evt.currentTarget.className += " active";
}

function clearTeacherList(){
	var t=document.getElementById("teachersList").value.split(",");
	if(t.length==1){
		document.getElementById("teachersList").value="";
	}
	else{
		document.getElementById("teachersList").value=t[0];
		for(i=1;i<t.length-1;i++)
			document.getElementById("teachersList").value+=(","+t[i]);
	}
}
function addTeacherToList(){
	var e=document.getElementById("selectTeachers");
	var t=document.getElementById("teachersList");
	if(t.value=="")
		t.value=e.options[e.selectedIndex].value;
	else
		t.value+=","+e.options[e.selectedIndex].value;
}
var app = angular.module("SubjectManagement", []);

// Controller Part
app.controller("SubjectController", function($scope, $http) {
	
	$scope.create=-1;
    $scope.subjects = [];
    $scope.subjectForm = {
        id: "",
        hours: 1,
        length:1,
        practical: "false",
        lab: "",
        n_teachers: 1,
        teachersList: "",
        dept: "CSE"
    };
    // Now load the data from server
    _refreshSubjectData();
    _refreshTeacherData();
    function _refreshTeacherData() {
        $http({
            method: 'GET',
            url: '/teachers'
        }).then(
            function(res) { // success
                $scope.teachers = res.data;
            },
            function(res) { // error
                console.log("Error: " + res.status + " : " + res.data);
            }
        );
        for(var i in $scope.teachers)
    		console.log($scope.teachers[i]);
    }
    // HTTP POST/PUT methods for add/edit employee  
    // Call: http://localhost:8080/employee
    $scope.submitSubject = function() {
    	$scope.subjectForm.teachersList=document.getElementById("teachersList").value;
        var method = "";
        var url = "";
 
        if ($scope.create == -1) {
            method = 'POST';
            url = '/subject';
        } else {
            method = 'PUT';
            url = '/subject';
            $scope.create=-1;
        }
        console.log(angular.toJson($scope.subjectForm));
        $http({
            method: method,
            url: url,
            data: angular.toJson($scope.subjectForm),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(_success, _error);
    };
 
    $scope.clear = function() {
        _clearSubjectFormData();
        _clearTeacherFormData();
    }
 
    // HTTP DELETE- delete employee by Id
    // Call: http://localhost:8080/employee/{empId}
    $scope.deleteSubject = function(subject) {
        $http({
            method: 'DELETE',
            url: '/subject/' + subject.id +'/' + subject.dept
        }).then(_success, _error);
    };
 
    // In case of edit
    $scope.editSubject = function(subject) {
        $scope.subjectForm.id = subject.id;
        $scope.subjectForm.dept = subject.dept;
        $scope.subjectForm.hours = subject.hours;
        $scope.subjectForm.length = subject.length;
        $scope.subjectForm.practical = subject.practical;
        $scope.subjectForm.lab = subject.lab;
        $scope.subjectForm.n_teachers = subject.n_teachers;
        $scope.subjectForm.teachersList = subject.teachersList;
        $scope.create=0;
    };
 
    // Private Method  
    // HTTP GET- get all employees collection
    // Call: http://localhost:8080/employees
    function _refreshSubjectData() {
        $http({
            method: 'GET',
            url: '/subjects'
        }).then(
            function(res) { // success
                $scope.subjects = res.data;
            },
            function(res) { // error
                console.log("Error: " + res.status + " : " + res.data);
            }
        );
        for(var i in $scope.subjects)
    		console.log($scope.subjects[i]);
    }
 
    function _success(res) {
        _refreshSubjectData();
        _refreshTeacherData();
        _clearSubjectFormData();
        _clearTeacherFormData();
    }
 
    function _error(res) {
        var data = res.data;
        var status = res.status;
        var header = res.header;
        var config = res.config;
        alert("Error: " + status + ":" + data);
    }
 
    // Clear the form
    function _clearSubjectFormData() {
    	$scope.create=-1;
        $scope.subjectForm.id = "";
        $scope.subjectForm.length=1;
        $scope.subjectForm.dept="CSE";
        $scope.subjectForm.hours=1;
        $scope.subjectForm.practical="false";
        $scope.subjectForm.lab="";
        $scope.subjectForm.n_teachers=1;
        $scope.subjectForm.teachersList="";
        document.getElementById("teachersList").value="";
    };
    $scope.created=-1;
    $scope.teachers = [];
    $scope.teacherForm = {
        id: "AH"
    };
 

    // HTTP POST/PUT methods for add/edit employee  
    // Call: http://localhost:8080/employee
    $scope.submitTeacher = function() {
 
        var method = "";
        var url = "";
 
        if ($scope.created == -1) {
            method = 'POST';
            url = '/teacher';
        } else {
            method = 'PUT';
            url = '/teacher';
            $scope.created=-1;
        }
        console.log(angular.toJson($scope.teacherForm));
        $http({
            method: method,
            url: url,
            data: angular.toJson($scope.teacherForm),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(_success, _error);
    };

 
    // HTTP DELETE- delete employee by Id
    // Call: http://localhost:8080/employee/{empId}
    $scope.deleteTeacher = function(teacher) {
        $http({
            method: 'DELETE',
            url: '/deleteteacher/' + teacher.id 
        }).then(_success, _error);
    };
 
    // In case of edit
    $scope.editTeacher = function(teacher) {
        $scope.teacherForm.id = teacher.id;
        $scope.created=0;
    };
 
    // Private Method  
    // HTTP GET- get all employees collection
    // Call: http://localhost:8080/employees
    function _refreshTeacherData() {
        $http({
            method: 'GET',
            url: '/teachers'
        }).then(
            function(res) { // success
                $scope.teachers = res.data;
            },
            function(res) { // error
                console.log("Error: " + res.status + " : " + res.data);
            }
        );
        for(var i in $scope.teachers)
    		console.log($scope.teachers[i]);
    }
 
 
    // Clear the form
    function _clearTeacherFormData() {
    	$scope.created=-1;
        $scope.teacherForm.id = "";    
    };
});
