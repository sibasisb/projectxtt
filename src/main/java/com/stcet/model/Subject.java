package com.stcet.model;


public class Subject{
	public String id;
	public int length;
	public int hours;
	public int n_teachers;
	public String dept;
	public String isPractical;
	public String lab;
	public String elecLab[];
	public String elecId[];
	public String teachersList;
	
	public Subject(){
	}
	public Subject(String id,String dept,int length,int hours,String isPractical,String lab,int n_teachers){
		this.id=id;
		this.dept=dept;
		this.n_teachers=n_teachers;
		this.length=length;
		this.hours=hours;
		this.isPractical=isPractical;
		this.lab=lab;
	}
	public String toString(){
		return this.id;
	}
	public String getPractical() {
		return isPractical;
	}
	
	public void setPractical(String practical) {
		this.isPractical = practical;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getHours() {
		return hours;
	}
	public void setHours(int hours) {
		this.hours = hours;
	}
	public int getN_teachers() {
		return n_teachers;
	}
	public void setN_teachers(int n_teachers) {
		this.n_teachers = n_teachers;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getLab() {
		return lab;
	}
	public void setLab(String lab) {
		this.lab = lab;
	}
	
	public String getTeachersList() {
		return teachersList;
	}
	public void setTeachersList(String teachersList) {
		this.teachersList = teachersList;
	}
	
}