package com.stcet.model;
public class Teacher{
	public String id;
	public int hours;
	public Teacher(String id){
		this.id=id;
		hours=0;
	}
	public Teacher(){}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public String toString(){
		return this.id;
	}
}