package com.stcet.model;

import java.util.ArrayList;

public class SubjectTeacherCombo{
	public String subject;
	
	public ArrayList<String> teachers;
	public SubjectTeacherCombo(){
		teachers=new ArrayList<>();	
	}
	public ArrayList<String> getTeachers() {
		return teachers;
	}
	public SubjectTeacherCombo(SubjectTeacherCombo a){
		if(a.subject!=null)
			subject=new String(a.subject);
		this.teachers=new ArrayList<>();
		for(String i: a.teachers)
			this.teachers.add(i);
	}
	public SubjectTeacherCombo(String s,String t1,String t2){
		teachers=new ArrayList<>();
		subject=new String(s.split(" ")[0]);
		teachers.add(t1);
		teachers.add(t2);	
	}
	public SubjectTeacherCombo(String s,String t1){
		teachers=new ArrayList<>();
		subject=new String(s.split(" ")[0]);
		teachers.add(t1);
	}
	public void addTeacher(String t){
		teachers.add(t);
	}
	public String toString(){
		if(subject==null)
			return "";
		if(teachers.get(0).equals(""))
			return subject;
		return subject+"/"+teachers.toString();
	}
	public String getSubject() {
		return subject;
	}
	public boolean isEmpty(){
		if(subject==null)
			return true;
		return false;
	}
}