package com.stcet.model;

import com.stcet.model.Subject;

public class ElecSubject extends Subject{
	public ElecSubject(Subject x, Subject y){
		hours=x.hours;
		length=x.length;
		n_teachers=x.n_teachers;
		dept=x.dept;
		isPractical=x.isPractical;
		elecLab=new String[2];
		elecId=new String[2];
		elecLab[0]=x.lab;
		elecLab[1]=y.lab;
		elecId[0]=x.id;
		elecId[1]=y.id;
	}
	public String toString(){
		return this.elecId[0]+"/"+this.elecId[1];
	}
}
