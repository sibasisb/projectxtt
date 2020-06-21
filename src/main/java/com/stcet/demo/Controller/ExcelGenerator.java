package com.stcet.demo.Controller;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.catalina.Context;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.stcet.model.*;
import com.stcet.spring.config.AppConfig;
import com.stcet.spring.dao.SubjectsDAO;
import com.stcet.spring.dao.TeachersDAO;

@Component
public class ExcelGenerator {
	@Autowired
	private SubjectsDAO subjectsDAO;
	@Autowired
	private TeachersDAO teachersDAO;
	
	@Autowired
	private ExcelGenerator d;
	static int days=5;
	static int slots=7;
	static SubjectTeacherCombo[][][][] week_final;
	static boolean[][][] available_week_final;
	static HashMap<String,boolean[][]> available_teacher_final;
	static HashMap<String,boolean[][]> available_lab_final;
	ArrayList<ArrayList<Subject>> subject_repo;
	ArrayList<Teacher> teacher_repo;
	ArrayList<ArrayList<Subject>> subjectsFromDb;
	ArrayList<Teacher> teachersFromDb;
	ArrayList<Subject> subjectsList;
	HashMap<String,ArrayList<String>> teaches;
	HashMap<String,Subject> id_subjectMap;
	HashMap<String,Teacher> id_teacherMap;
	ArrayList<Subject> tutorial;
	ArrayList<Subject> h2;
	ArrayList<Subject> h3;
	ArrayList<Subject> h4; 
	SubjectTeacherCombo[][][][] week;
	boolean[][][] available_week;
	HashMap<String,ArrayList<SubjectTeacherCombo>> sub_TeacherComboMap;
	HashMap<String,boolean[][]> available_teacher;
	HashMap<String,boolean[][]> available_lab;
	
	public void populateFromDb() {
		subjectsFromDb=new ArrayList<ArrayList<Subject>>();
		for(int k=0;k<16;k++) {
			subjectsFromDb.add(new ArrayList<Subject>());
		}
		String dname="CSE";
		int i=0,j=2;
		for(i=0;i<=15;i=i+4){
			String sem="^[A-Z]*" + j + "[0-9]{2}";
			for(Subject s: subjectsDAO.getSubjectsById(dname,sem)){
				subjectsFromDb.get(i).add(s);
			}
			j=j+2;
		}
		dname="IT";
		j=2;
		for(i=1;i<=15;i=i+4){
			String sem="^[A-Z]*" + String.valueOf(j) + "[0-9]{2}";
			for(Subject s:subjectsDAO.getSubjectsById(dname,sem)){
				subjectsFromDb.get(i).add(s);
			}
			j=j+2;
		}

		dname="ECE";
		j=2;
		for(i=2;i<=15;i=i+4){
			String sem="^[A-Z]*" + String.valueOf(j) + "[0-9]{2}";
			for(Subject s:subjectsDAO.getSubjectsById(dname,sem)){
				subjectsFromDb.get(i).add(s);
			}
			j=j+2;
		}

		dname="EE";
		j=2;
		for(i=3;i<=15;i=i+4){
			String sem="^[A-Z]*" + String.valueOf(j) + "[0-9]{2}";
			for(Subject s:subjectsDAO.getSubjectsById(dname,sem)){
				subjectsFromDb.get(i).add(s);
			}
			j=j+2;
		}
		teachersFromDb=new ArrayList<Teacher>();
		for(Teacher tr:teachersDAO.getAllTeachers()) {
			teachersFromDb.add(tr);
		}
		subjectsList=new ArrayList<Subject>();
		for(Subject s:subjectsDAO.getAllSubjects()) {
			subjectsList.add(s);
		}
		
	}
	
 	public void initialize(){
 		tutorial=new ArrayList<>();
 		h2=new ArrayList<>();
 		h3=new ArrayList<>();
 		h4=new ArrayList<>();
		subject_repo=new ArrayList<ArrayList<Subject>>();
		for(int i=0;i<16;i++) 
			subject_repo.add(new ArrayList<Subject>());
		
		this.defaultPopulateSubjects();
		available_lab=new HashMap<>();
		available_lab_final=new HashMap<>();
		for(ArrayList<Subject> i:subject_repo)
			for(Subject j:i)
				if(!available_lab.containsKey(j.lab)){
					available_lab.put(j.lab,new boolean[days][slots]);
					available_lab_final.put(j.lab,new boolean[days][slots]);
				}
		this.mergeElecSubjects();
		teacher_repo=new ArrayList<Teacher>();
		this.defaultPopulateTeachers();
		available_teacher=new HashMap<>();
		available_teacher_final=new HashMap<>();
		for(Teacher i: teacher_repo){
			available_teacher.put(i.id,new boolean[days][slots]);
			available_teacher_final.put(i.id,new boolean[days][slots]);
		}
		teaches=new HashMap<>();
		this.defaultPopulateTeaches();
		week=new SubjectTeacherCombo[16][days][slots][2];
		week_final=new SubjectTeacherCombo[16][days][slots][2];
		for(int i=0;i<16;i++)
			for(int j=0;j<days;j++)
				for(int k=0;k<slots;k++){
					week[i][j][k][0]=new SubjectTeacherCombo();
					week[i][j][k][1]=new SubjectTeacherCombo();
					week_final[i][j][k][0]=new SubjectTeacherCombo();
					week_final[i][j][k][1]=new SubjectTeacherCombo();
				}
		available_week=new boolean[16][days][slots];
		available_week_final=new boolean[16][days][slots];
	}
	private void defaultPopulateSubjects(){
		/*subject_repo.get(0).add(new Subject("CH201","CSE",1,3,"false","",1));
		subject_repo.get(0).add(new Subject("M201","CSE",1,3,"false","",1));
		subject_repo.get(0).add(new Subject("M201(T)","CSE",1,1,"false","",1));
		tutorial.add(new Subject("M201(T)","CSE",1,1,"false","",1));
		subject_repo.get(0).add(new Subject("CS201","CSE",1,3,"false","",1));
		subject_repo.get(0).add(new Subject("HU201","CSE",1,2,"false","",1));
		subject_repo.get(0).add(new Subject("CH291","CSE",3,3,"true","Chem",2));
		subject_repo.get(0).add(new Subject("CS291","CSE",4,4,"true","C3",2));
		subject_repo.get(0).add(new Subject("ME291","CSE",5,5,"false","Drawing Hall",2));
		subject_repo.get(0).add(new Subject("HU291","CSE",2,2,"true","",1));
		h2.add(new Subject("HU291","CSE",2,2,"true","",1));
		h3.add(new Subject("CH291","CSE",3,3,"true","Chem",2));
		h4.add(new Subject("CS291","CSE",4,4,"true","C3",2));
		subject_repo.get(1).add(new Subject("CH201","IT",1,3,"false","",1));
		subject_repo.get(1).add(new Subject("M201","IT",1,3,"false","",1));
		subject_repo.get(1).add(new Subject("M201(T)","IT",1,1,"false","",1));
		tutorial.add(new Subject("M201(T)","IT",1,1,"false","",1));
		subject_repo.get(1).add(new Subject("M201","IT",1,3,"false","",1));
		subject_repo.get(1).add(new Subject("IT201","IT",1,3,"false","",1));
		subject_repo.get(1).add(new Subject("HU201","IT",1,2,"false","",1));
		subject_repo.get(1).add(new Subject("IT291","IT",4,4,"true","MWT",2));
		subject_repo.get(1).add(new Subject("CH291","IT",3,3,"true","Chem",2));
		subject_repo.get(1).add(new Subject("ME291","IT",5,5,"false","Drawing Hall",2));
		subject_repo.get(1).add(new Subject("HU291","IT",2,2,"true","",1));
		h2.add(new Subject("HU291","IT",2,2,"true","",1));
		h3.add(new Subject("CH291","IT",3,3,"true","Chem",2));
		h4.add(new Subject("IT291","IT",4,4,"true","MWT",2));
		subject_repo.get(2).add(new Subject("PH201","ECE",1,3,"false","",1));
		subject_repo.get(2).add(new Subject("M201","ECE",1,3,"false","",1));
		subject_repo.get(2).add(new Subject("M201(T)","ECE",1,1,"false","",1));
		tutorial.add(new Subject("M201(T)","ECE",1,1,"false","",1));
		subject_repo.get(2).add(new Subject("ECE201","ECE",1,3,"false","",1));
		subject_repo.get(2).add(new Subject("HU201","ECE",1,2,"false","",1));
		subject_repo.get(2).add(new Subject("ME291","ECE",5,5,"false","Workshop",1));
		subject_repo.get(2).add(new Subject("PH291","ECE",3,3,"true","Phy",2));
		subject_repo.get(2).add(new Subject("ECE291","ECE",4,4,"true","SD",2));
		h3.add(new Subject("PH291","ECE",3,3,"true","Phy",2));
		h4.add(new Subject("ECE291","ECE",4,4,"true","SD",2));
		subject_repo.get(2).add(new Subject("HU291","ECE",2,2,"true","",1));
		h2.add(new Subject("HU291","ECE",2,2,"true","",1));
		subject_repo.get(2).add(new Subject("PH201(T)","ECE",1,1,"false","",2));
		subject_repo.get(3).add(new Subject("PH201","EE",1,3,"false","",1));
		subject_repo.get(3).add(new Subject("EE201","EE",1,3,"false","",1));
		subject_repo.get(3).add(new Subject("M201","EE",1,3,"false","",1));
		subject_repo.get(3).add(new Subject("M201(T)","EE",1,1,"false","",1));
		subject_repo.get(3).add(new Subject("HU201","EE",1,2,"false","",1));
		tutorial.add(new Subject("M201(T)","EE",1,1,"false","",1));
		subject_repo.get(3).add(new Subject("PH291","EE",3,3,"true","Phy",2));
		subject_repo.get(3).add(new Subject("EE291","EE",4,4,"true","C2",2));
		subject_repo.get(3).add(new Subject("ME291","EE",5,5,"false","Workshop",1));
		subject_repo.get(3).add(new Subject("HU291","EE",2,2,"true","",1));
		subject_repo.get(3).add(new Subject("PH201(T)","EE",1,1,"false","",2));
		h2.add(new Subject("HU291","EE",2,2,"true","",1));
		h3.add(new Subject("PH291","EE",3,3,"true","Phy",2));
		h4.add(new Subject("EE291","EE",4,4,"true","C2",2));
		subject_repo.get(4).add(new Subject("CS401","CSE",1,3,"false","",1));
		subject_repo.get(4).add(new Subject("CS402","CSE",1,3,"false","",1));
		subject_repo.get(4).add(new Subject("CS403","CSE",1,3,"false","",1));
		subject_repo.get(4).add(new Subject("CH401","CSE",1,3,"false","",1));
		subject_repo.get(4).add(new Subject("M401","CSE",1,3,"false","",1));
		subject_repo.get(4).add(new Subject("B401","CSE",1,3,"false","",1));
		subject_repo.get(4).add(new Subject("M401(T)","CSE",1,1,"false","",2));
		subject_repo.get(4).add(new Subject("CS491","CSE",4,4,"true","C1",2));
		subject_repo.get(4).add(new Subject("CS492","CSE",4,4,"true","C2",2));
		subject_repo.get(5).add(new Subject("IT401","IT",1,3,"false","",1));
		subject_repo.get(5).add(new Subject("IT402","IT",1,3,"false","",1));
		subject_repo.get(5).add(new Subject("IT403","IT",1,3,"false","",1));
		subject_repo.get(5).add(new Subject("CH401","IT",1,3,"false","",1));
		subject_repo.get(5).add(new Subject("M401","IT",1,3,"false","",1));
		subject_repo.get(5).add(new Subject("B401","IT",1,3,"false","",1));
		subject_repo.get(5).add(new Subject("IT491","IT",4,4,"true","MWT",2));
		subject_repo.get(5).add(new Subject("IT492","IT",4,4,"true","SD",2));
		subject_repo.get(6).add(new Subject("ECE401","ECE",1,3,"false","",1));
		subject_repo.get(6).add(new Subject("ECE402","ECE",1,3,"false","",1));
		subject_repo.get(6).add(new Subject("ECE403","ECE",1,3,"false","",1));
		subject_repo.get(6).add(new Subject("ECE404","ECE",1,3,"false","",1));
		subject_repo.get(6).add(new Subject("M401","ECE",1,3,"false","",1));
		subject_repo.get(6).add(new Subject("B401","ECE",1,3,"false","",1));
		subject_repo.get(6).add(new Subject("ECE491","ECE",3,3,"true","E1",2));
		subject_repo.get(6).add(new Subject("ECE492","ECE",3,3,"true","E2",2));
		subject_repo.get(6).add(new Subject("ECE493","ECE",3,3,"true","E3",2));
		subject_repo.get(6).add(new Subject("HU491","ECE",2,2,"true","",1));
		subject_repo.get(6).add(new Subject("M491","ECE",2,2,"true","",2));
		subject_repo.get(7).add(new Subject("EE401","EE",1,3,"false","",1));
		subject_repo.get(7).add(new Subject("EE402","EE",1,3,"false","",1));
		subject_repo.get(7).add(new Subject("EE403","EE",1,3,"false","",1));
		subject_repo.get(7).add(new Subject("HU401","EE",1,3,"false","",1));
		subject_repo.get(7).add(new Subject("EE404","EE",1,3,"false","",1));
		subject_repo.get(7).add(new Subject("EE401","EE",1,3,"false","",1));
		subject_repo.get(7).add(new Subject("CH401","EE",1,3,"false","",1));
		subject_repo.get(7).add(new Subject("EE491","EE",3,3,"true","EE1",2));
		subject_repo.get(7).add(new Subject("EE492","EE",3,3,"true","EE2",2));
		subject_repo.get(7).add(new Subject("EE493","EE",3,3,"true","EE3",2));
		subject_repo.get(7).add(new Subject("EE494","EE",3,3,"true","EE4",2));
		subject_repo.get(8).add(new Subject("CS601","CSE",1,3,"false","",1));
		subject_repo.get(8).add(new Subject("CS602","CSE",1,3,"false","",1));
		subject_repo.get(8).add(new Subject("CS603","CSE",1,3,"false","",1));
		subject_repo.get(8).add(new Subject("CS604A","CSE",1,3,"false","",1));
		subject_repo.get(8).add(new Subject("CS604B","CSE",1,3,"false","",1));
		subject_repo.get(8).add(new Subject("CS605A","CSE",1,3,"false","",1));
		subject_repo.get(8).add(new Subject("CS605B","CSE",1,3,"false","",1));
		subject_repo.get(8).add(new Subject("HU601","CSE",1,2,"false","",1));
		subject_repo.get(8).add(new Subject("CS691","CSE",3,3,"true","C3",2));
		subject_repo.get(8).add(new Subject("CS692","CSE",3,3,"true","C2",2));
		subject_repo.get(8).add(new Subject("CS693","CSE",3,3,"true","C1",2));
		subject_repo.get(9).add(new Subject("IT601A","IT",1,3,"false","",1));
		subject_repo.get(9).add(new Subject("IT601B","IT",1,3,"false","",1));
		subject_repo.get(9).add(new Subject("IT602","IT",1,3,"false","",1));
		subject_repo.get(9).add(new Subject("IT603","IT",1,3,"false","",1));
		subject_repo.get(9).add(new Subject("IT604A","IT",1,3,"false","",1));
		subject_repo.get(9).add(new Subject("IT604B","IT",1,3,"false","",1));
		subject_repo.get(9).add(new Subject("IT605","IT",1,3,"false","",1));
		subject_repo.get(9).add(new Subject("HU685","IT",3,3,"false","",2));
		subject_repo.get(9).add(new Subject("IT692","IT",3,3,"true","PD",2));
		subject_repo.get(9).add(new Subject("IT693","IT",3,3,"true","SD",2));
		subject_repo.get(9).add(new Subject("IT695","IT",3,3,"true","MWT",3));
		subject_repo.get(9).add(new Subject("HU601","IT",1,2,"false","",1));
		subject_repo.get(10).add(new Subject("ECE601","ECE",1,3,"false","",1));
		subject_repo.get(10).add(new Subject("ECE602","ECE",1,3,"false","",1));
		subject_repo.get(10).add(new Subject("ECE603A","ECE",1,3,"false","",1));
		subject_repo.get(10).add(new Subject("ECE603B","ECE",1,3,"false","",1));
		subject_repo.get(10).add(new Subject("ECE605A","ECE",1,3,"false","",1));
		subject_repo.get(10).add(new Subject("HU601","ECE",1,2,"false","",1));
		subject_repo.get(10).add(new Subject("ECE604","ECE",1,3,"false","",1));
		subject_repo.get(10).add(new Subject("ECE695","ECE",3,3,"true","MWT",1));
		subject_repo.get(10).add(new Subject("ECE694","ECE",3,3,"true","E1",1));
		subject_repo.get(10).add(new Subject("ECE692","ECE",3,3,"true","E2",1));
		subject_repo.get(10).add(new Subject("HU685","ECE",3,3,"false","",2));
		subject_repo.get(11).add(new Subject("EE601","EE",1,3,"false","",1));
		subject_repo.get(11).add(new Subject("EE601(T)","EE",1,1,"false","",2));
		subject_repo.get(11).add(new Subject("EE602","EE",1,3,"false","",1));
		subject_repo.get(11).add(new Subject("EE602(T)","EE",1,1,"false","",2));
		subject_repo.get(11).add(new Subject("EE603","EE",1,3,"false","",1));
		subject_repo.get(11).add(new Subject("EE603(T)","EE",1,1,"false","",2));
		subject_repo.get(11).add(new Subject("EE604A","EE",1,3,"false","",1));
		subject_repo.get(11).add(new Subject("EE604B","EE",1,3,"false","",1));
		subject_repo.get(11).add(new Subject("EE694B","EE",3,3,"true","CC",2));
		subject_repo.get(11).add(new Subject("EE691","EE",3,3,"true","EE1",2));
		subject_repo.get(11).add(new Subject("EE692","EE",3,3,"true","EE2",2));
		subject_repo.get(11).add(new Subject("EE693","EE",3,3,"true","EE3",2));
		subject_repo.get(11).add(new Subject("EE605A","EE",1,3,"false","",1));
		subject_repo.get(11).add(new Subject("EE605B","EE",1,3,"false","",1));
		subject_repo.get(12).add(new Subject("HU801","CSE",1,2,"false","",1));
		subject_repo.get(12).add(new Subject("CS801A","CSE",1,3,"false","",1));
		subject_repo.get(12).add(new Subject("CS802A","CSE",1,3,"false","",1));
		subject_repo.get(12).add(new Subject("CS802B","CSE",1,3,"false","",1));
		subject_repo.get(12).add(new Subject("CS891","CSE",3,6,"false","C3",2));
		subject_repo.get(12).add(new Subject("CS894","CSE",3,12,"false","",1));
		subject_repo.get(13).add(new Subject("HU801","IT",1,2,"false","",1));
		subject_repo.get(13).add(new Subject("IT801A","IT",1,3,"false","",1));
		subject_repo.get(13).add(new Subject("IT802A","IT",1,3,"false","",1));
		subject_repo.get(13).add(new Subject("IT891","IT",3,6,"false","PD",2));
		subject_repo.get(13).add(new Subject("IT894","IT",3,12,"false","PD",1));
		subject_repo.get(14).add(new Subject("HU801","ECE",1,2,"false","",1));
		subject_repo.get(14).add(new Subject("ECE801A","ECE",1,3,"false","",1));
		subject_repo.get(14).add(new Subject("ECE802A","ECE",1,3,"false","",1));
		subject_repo.get(14).add(new Subject("ECE891","ECE",3,6,"false","E1",2));
		subject_repo.get(14).add(new Subject("ECE894","ECE",3,12,"false","E2",1));
		subject_repo.get(15).add(new Subject("HU801","EE",1,2,"false","",1));
		subject_repo.get(15).add(new Subject("EE801A","EE",1,3,"false","",1));
		subject_repo.get(15).add(new Subject("EE802A","EE",1,3,"false","",1));
		subject_repo.get(15).add(new Subject("EE891","EE",3,6,"false","EE1",2));
		subject_repo.get(15).add(new Subject("EE894","EE",3,12,"false","EE2",1));*/
		
		
		for(int i=0;i<16;i++) {
			for(Subject s:subjectsFromDb.get(i)) {
				subject_repo.get(i).add(s);
			}
		}
		
		//populate h2,h3,h4
		for(int i=0;i<=3;i=i+1){
			for(Subject su:subject_repo.get(i)){
				if(su.getPractical().equalsIgnoreCase("true") && su.getLength()==2) {
					h2.add(su);
				}
				if(su.getPractical().equalsIgnoreCase("true") && su.getLength()==3) {
					h3.add(su);
				}
				if(su.getPractical().equalsIgnoreCase("true") && su.getLength()==4) {
					h4.add(su);
				}
			}
		}
		
		//populate tutorial
		for(int i=0;i<=3;i++){
			for(Subject su:subject_repo.get(i)){
				String scode=su.getId();
				String tut=scode.substring(scode.length()-3);
				if(tut.equals("(T)"))
					tutorial.add(su);
			}
		}
		
		id_subjectMap=new HashMap<>();
		for(ArrayList<Subject> sr:subject_repo){
			for(Subject su:sr){
				id_subjectMap.put(su.id+" "+su.dept,su);
			}
		}
	}
	private void mergeElecSubjects(){
		HashMap<String,Subject[]> temp=new HashMap<>();
		ArrayList<Subject> newList=new ArrayList<>();
		for(int i=0;i<subject_repo.size();i++){
			if(subject_repo.get(i).size()==0)
				continue;
			ArrayList<Subject> x=subject_repo.get(i);
			for(Subject j: x){
				char t=j.id.charAt(j.id.length()-1);
				if(t>='0'&&t<='9'){
					newList.add(j);
					continue;
				}
				String eID=j.id.substring(0,j.id.length()-1);
				if(temp.containsKey(eID))
					temp.get(eID)[1]=j;
				else{
					temp.put(eID,new Subject[2]);
					temp.get(eID)[0]=j;
				}
			}
			for(String j: temp.keySet()){
				Subject[] eList=temp.get(j);
				if(eList[1]==null){
					newList.add(eList[0]);
					continue;
				}
				newList.add(new ElecSubject(eList[0],eList[1]));
			}
			subject_repo.set(i,new ArrayList<Subject>(newList));
			newList.clear();
			temp.clear();
		}
	}
	public void printSubjects(){
		for(int i=0;i<subject_repo.size();i++){
			if(subject_repo.get(i).size()==0)
				continue;
			int d=i%4;
			int y=i/4;
			if(d==0)
				System.out.print("CSE ");
			else if(d==1)
				System.out.print("IT ");
			else if(d==2)
				System.out.print("ECE ");
			else
				System.out.print("EE ");
			if(y==0)
				System.out.println("1st year");
			else if(y==1)
				System.out.println("2nd year");
			else if(y==2)	
				System.out.println("3rd year");
			else
				System.out.println("4th year");
			for(Subject j: subject_repo.get(i)){
				if(j.id==null)
					System.out.println(j.elecId[0]+" "+j.elecId[1]);
				else
					System.out.println(j.id);
			}
			System.out.println();
			System.out.println();
			System.out.println();
		}
	}
	public void defaultPopulateTeachers(){
		/*teacher_repo.add(new Teacher("AP"));
		teacher_repo.add(new Teacher("GY"));
		teacher_repo.add(new Teacher("AH"));
		teacher_repo.add(new Teacher("BDu"));
		teacher_repo.add(new Teacher("MDu"));
		teacher_repo.add(new Teacher("DC"));
		teacher_repo.add(new Teacher("SMa"));
		teacher_repo.add(new Teacher("SSK"));
		teacher_repo.add(new Teacher("AP"));
		teacher_repo.add(new Teacher("SDe"));
		teacher_repo.add(new Teacher("SDa"));
		teacher_repo.add(new Teacher("ACh"));
		teacher_repo.add(new Teacher("SoM"));
		teacher_repo.add(new Teacher("AT"));
		teacher_repo.add(new Teacher("SC"));
		teacher_repo.add(new Teacher("PD"));
		teacher_repo.add(new Teacher("AnD"));
		teacher_repo.add(new Teacher("SRU"));
		teacher_repo.add(new Teacher("KB"));
		teacher_repo.add(new Teacher("RCh"));
		teacher_repo.add(new Teacher("KDa"));
		teacher_repo.add(new Teacher("SRU"));
		teacher_repo.add(new Teacher("FR"));
		teacher_repo.add(new Teacher("ABj"));
		teacher_repo.add(new Teacher("SCh"));
		teacher_repo.add(new Teacher("TR"));
		teacher_repo.add(new Teacher("TKG"));
		teacher_repo.add(new Teacher("SLa"));
		teacher_repo.add(new Teacher("AKS"));
		teacher_repo.add(new Teacher("RG"));
		teacher_repo.add(new Teacher("ArC"));
		teacher_repo.add(new Teacher("BDC"));
		teacher_repo.add(new Teacher("SS"));
		teacher_repo.add(new Teacher("SL"));
		teacher_repo.add(new Teacher("SSR"));
		teacher_repo.add(new Teacher("SU"));
		teacher_repo.add(new Teacher("RG"));
		teacher_repo.add(new Teacher("SMC"));
		teacher_repo.add(new Teacher("SG"));
		teacher_repo.add(new Teacher("PP"));
		teacher_repo.add(new Teacher("PC"));
		teacher_repo.add(new Teacher("DK"));
		teacher_repo.add(new Teacher("JA"));
		teacher_repo.add(new Teacher("SRC"));
		teacher_repo.add(new Teacher("BC"));
		teacher_repo.add(new Teacher("ArD"));
		teacher_repo.add(new Teacher("RND"));
		teacher_repo.add(new Teacher("KR"));
		teacher_repo.add(new Teacher("NCS"));
		teacher_repo.add(new Teacher("IB"));
		teacher_repo.add(new Teacher("SDG"));
		teacher_repo.add(new Teacher("SA"));
		teacher_repo.add(new Teacher("AGh"));
		teacher_repo.add(new Teacher("AB"));
		teacher_repo.add(new Teacher("AD"));
		teacher_repo.add(new Teacher("AnC"));
		teacher_repo.add(new Teacher("SD"));
		teacher_repo.add(new Teacher("TD"));
		teacher_repo.add(new Teacher("SSG"));
		teacher_repo.add(new Teacher("PG"));
		teacher_repo.add(new Teacher("ASG"));
		teacher_repo.add(new Teacher("ABo"));
		teacher_repo.add(new Teacher("SKB"));
		teacher_repo.add(new Teacher("SDC"));
		teacher_repo.add(new Teacher("SMo"));
		teacher_repo.add(new Teacher("KR"));
		teacher_repo.add(new Teacher("MB"));*/
		id_teacherMap=new HashMap<>();
		
		for(Teacher s:teachersFromDb) {
			teacher_repo.add(s);
		}
		
		for(Teacher i:teacher_repo)
			id_teacherMap.put(i.id,i);
	}
	public void defaultPopulateTeaches(){
		/*ArrayList<String> temp=new ArrayList<>();
		temp.add("SC");
		temp.add("PD");
		teaches.put("CH201 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("ABj");
		teaches.put("M201 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SCh");
		temp.add("ABj");
		teaches.put("M201(T) CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("GY");
		teaches.put("CS201 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SDa");
		teaches.put("HU201 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SC");
		temp.add("FR");
		teaches.put("CH291 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("GY");
		temp.add("AH");
		teaches.put("CS291 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("TR");
		temp.add("TKG");
		teaches.put("ME291 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SDa");
		teaches.put("HU291 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SC");
		temp.add("PD");
		teaches.put("CH201 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("ABj");
		teaches.put("M201 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("SCh");
		teaches.put("M201(T) IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("ABj");
		teaches.put("M201 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("AKS");
		teaches.put("IT201 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("SDa");
		teaches.put("HU201 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("SC");
		temp.add("FR");
		teaches.put("CH291 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("AKS");
		temp.add("RG");
		temp.add("ArC");
		teaches.put("IT291 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("SDa");
		teaches.put("HU291 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("TR");
		temp.add("TKG");
		teaches.put("ME291 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("SDe");
		teaches.put("ECE201 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SMa");
		teaches.put("ECE201 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SoM");
		teaches.put("PH201 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("AT");
		temp.add("SoM");
		teaches.put("PH201(T) ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SCh");
		teaches.put("M201 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SCh");
		teaches.put("M201(T) ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SDa");
		teaches.put("HU201 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("BDC");
		teaches.put("ME291 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("AT");
		temp.add("SS");
		teaches.put("PH291 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SDe");
		temp.add("SSK");
		teaches.put("ECE291 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SDa");
		teaches.put("HU291 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("AT");
		teaches.put("PH201 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SS");
		temp.add("SoM");
		teaches.put("PH201(T) EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SL");
		teaches.put("EE201 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SCh");
		teaches.put("M201 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SDa");
		teaches.put("HU201 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("AT");
		teaches.put("PH201 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SDa");
		teaches.put("HU291 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("AKS");
		temp.add("SL");
		teaches.put("EE291 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("AT");
		temp.add("SS");
		teaches.put("PH291 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SCh");
		teaches.put("M201(T) EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("BDC");
		teaches.put("ME291 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("AH");
		teaches.put("CS401 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("AP");
		teaches.put("CS402 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("MDu");
		teaches.put("CS403 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SC");
		teaches.put("CH401 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("PD");
		teaches.put("B401 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SMa");
		teaches.put("M401 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("AH");
		temp.add("MDu");
		teaches.put("CS491 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("AP");
		temp.add("BDu");
		teaches.put("CS492 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("DC");
		temp.add("GY");
		teaches.put("M401(T) CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("RCh");
		teaches.put("IT401 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("SSR");
		teaches.put("IT402 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("SC");
		teaches.put("CH401 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("SU");
		teaches.put("IT403 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("ArC");
		teaches.put("M401 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("PD");
		teaches.put("B401 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("SSR");
		temp.add("KDa");
		teaches.put("IT491 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("RCh");
		temp.add("RG");
		teaches.put("IT492 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("SMC");
		teaches.put("ECE401 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SG");
		teaches.put("ECE402 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("PP");
		teaches.put("ECE403 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("MDu");
		temp.add("SDe");
		teaches.put("ECE404 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SLa");
		teaches.put("M401 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("PD");
		teaches.put("B401 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("PC");
		temp.add("SMC");
		teaches.put("ECE491 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("DK");
		temp.add("JA");
		teaches.put("ECE492 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("PP");
		temp.add("BC");
		teaches.put("ECE493 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("KB");
		teaches.put("HU491 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SLa");
		temp.add("SRC");
		teaches.put("M491 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("BDC");
		teaches.put("EE401 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("RND");
		teaches.put("EE402 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("ACh");
		teaches.put("HU401 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("KR");
		teaches.put("EE403 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("ArD");
		teaches.put("EE404 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SC");
		temp.add("PD");
		teaches.put("CH401 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("KR");
		temp.add("IB");
		teaches.put("EE491 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("RND");
		temp.add("JA");
		teaches.put("EE492 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("ArD");
		temp.add("SDG");
		teaches.put("EE493 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("BDC");
		temp.add("NCS");
		teaches.put("EE494 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SSK");
		teaches.put("CS601 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("DC");
		teaches.put("CS602 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("BDu");
		teaches.put("CS603 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("DC");
		teaches.put("CS604A CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SLa");
		teaches.put("CS604B CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("AH");
		teaches.put("CS605A CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SDe");
		teaches.put("CS605B CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("AnD");
		teaches.put("HU601 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SSK");
		temp.add("SDe");
		teaches.put("CS691 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("DC");
		temp.add("GY");
		temp.add("SSK");
		teaches.put("CS692 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("BDu");
		temp.add("AP");
		teaches.put("CS693 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("AB");
		teaches.put("IT601A IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("SL");
		teaches.put("IT601B IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("SU");
		teaches.put("IT602 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("ArC");
		teaches.put("IT603 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("AB");
		teaches.put("IT604A IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("RG");
		teaches.put("IT604B IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("AGh");
		teaches.put("IT605 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("ACh");
		temp.add("SSR");
		teaches.put("HU685 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("SA");
		teaches.put("HU601 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("ArC");
		temp.add("KDa");
		teaches.put("IT692 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("SU");
		temp.add("AD");
		teaches.put("IT693 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("AGh");
		temp.add("AB");
		teaches.put("IT695 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("SSG");
		teaches.put("ECE601 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("TD");
		teaches.put("ECE602 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("PC");
		teaches.put("ECE603A ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("JA");
		teaches.put("ECE603B ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("AD");
		teaches.put("ECE605A ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("AD");
		temp.add("SU");
		teaches.put("ECE695 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("AnC");
		temp.add("SD");
		teaches.put("ECE692 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("TD");
		temp.add("PP");
		teaches.put("ECE694 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("ACh");
		temp.add("AnC");
		teaches.put("HU685 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("TR");
		teaches.put("HU601 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("AnC");
		teaches.put("ECE604 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("PG");
		teaches.put("EE601 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("PG");
		temp.add("ASG");
		teaches.put("EE601(T) EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("ABo");
		temp.add("SDC");
		teaches.put("EE602(T) EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("ABo");
		teaches.put("EE602 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SKB");
		temp.add("SDG");
		teaches.put("EE603(T) EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SKB");
		teaches.put("EE603 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("DC");
		teaches.put("EE604A EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("GY");
		teaches.put("EE604B EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("IB");
		teaches.put("EE605A EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("JA");
		temp.add("SD");
		teaches.put("EE605B EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("AP");
		temp.add("GY");
		temp.add("DC");
		temp.add("SDe");
		teaches.put("EE694B EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SDG");
		temp.add("SMo");
		teaches.put("EE693 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("PG");
		temp.add("ASG");
		teaches.put("EE691 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("ABo");
		temp.add("SDC");
		teaches.put("EE692 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("KB");
		teaches.put("HU801 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SSK");
		teaches.put("CS801A CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("BDu");
		teaches.put("CS802A CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("BDu");
		teaches.put("CS802B CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("MDu");
		temp.add("GY");
		temp.add("SSK");
		temp.add("SMa");
		teaches.put("CS894 CSE",new ArrayList<>(temp));
		temp.clear();
		temp.add("DC");
		temp.add("SMa");
		teaches.put("CS891 CSE",new ArrayList<>(temp));
		temp.clear();	
		temp.add("KB");
		teaches.put("HU801 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("RCh");
		teaches.put("IT801A IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("KDa");
		teaches.put("IT802A IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("KDa");
		temp.add("AGh");
		temp.add("SU");
		teaches.put("IT891 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("RG");
		temp.add("AKS");
		temp.add("AB");
		teaches.put("IT894 IT",new ArrayList<>(temp));
		temp.clear();
		temp.add("KB");
		teaches.put("HU801 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("DK");
		teaches.put("ECE801A ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("ArD");
		teaches.put("ECE802A ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("SD");
		temp.add("AnC");
		temp.add("BC");
		teaches.put("ECE891 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("RND");
		temp.add("TD");
		temp.add("PC");
		teaches.put("ECE894 ECE",new ArrayList<>(temp));
		temp.clear();
		temp.add("KB");
		teaches.put("HU801 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("ASG");
		teaches.put("EE801A EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("KR");
		teaches.put("EE802A EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("PG");
		temp.add("IB");
		teaches.put("EE891 EE",new ArrayList<>(temp));
		temp.clear();
		temp.add("MB");
		teaches.put("EE894 EE",new ArrayList<>(temp));
		temp.clear();*/
		
		for(Subject su:subjectsList) {
			String k=su.getId() + " " + su.getDept();
			String arr[]=su.getTeachersList().split(",");
			ArrayList<String> v=new ArrayList<>(Arrays.asList(arr));
			teaches.put(k, v);
		}
		
	}
	public void printTeachers(){
		for(Teacher i:teacher_repo)
			System.out.println(i);
	}
	public void printTeaches(){
		for(String i:teaches.keySet())
			System.out.println(i+" "+teaches.get(i));
	}
	public boolean allocateFirstYearPracticals(int sg){
		ArrayList<Subject> fs=new ArrayList<>();
		if(sg>=h3.size())
			return true;
		ArrayList<Integer> randomDay=new ArrayList<>();
		ArrayList<Integer> randomSlot=new ArrayList<>();
		fs.add(h2.get(sg));
		fs.add(h4.get(sg));
		Collections.shuffle(fs);
		for(Subject fsub: fs){
			randomDay.clear();
			for(int j=0;j<days;j++)
				randomDay.add(j);
			Collections.shuffle(randomDay);
			randomSlot.clear();
			if(fsub.length==2){
				for(int j=0;j<slots-1;j++)
					randomSlot.add(j);
				Collections.shuffle(randomSlot);
			}
			else if(fsub.length==3){
				randomSlot.add(4);
				randomSlot.add(1);
				randomSlot.add(0);
				Collections.shuffle(randomSlot);
			}	
			else{
				randomSlot.add(3);
				randomSlot.add(0);
				Collections.shuffle(randomSlot);
			}
			int d_count=0,s_count=0,selectedDay=0,selectedSlot=0;
			int subTeacherNumber=0;
			ArrayList<SubjectTeacherCombo> temp=sub_TeacherComboMap.get(fsub.id+" "+fsub.dept);
			int flag=0;
			for(int i=0;i<2;i++){
				d_count=(selectedDay+1)%randomDay.size();
				s_count=0;
				do{
					if(d_count==randomDay.size()){
						if(subTeacherNumber<temp.size()){
							subTeacherNumber++;
							d_count=0;
							s_count=0;
						}
						if(subTeacherNumber==temp.size()){
							flag=1;
							return false;
						}
					}
					boolean valid=true;
					if(fsub.lab.equals("")){
						for(int w_count=0;w_count<fsub.length;w_count++)
						valid&=(week[sg][randomDay.get(d_count)][randomSlot.get(s_count)+w_count][0].isEmpty()&&
						!available_week[sg][randomDay.get(d_count)][randomSlot.get(s_count)+w_count]&&
						checkTeachersAvailibility(temp.get(subTeacherNumber),randomDay.get(d_count),randomSlot.get(s_count)+w_count));	
					}
					else{
						for(int w_count=0;w_count<fsub.length;w_count++)
						valid&=(week[sg][randomDay.get(d_count)][randomSlot.get(s_count)+w_count][0].isEmpty()&&
						!available_week[sg][randomDay.get(d_count)][randomSlot.get(s_count)+w_count]&&
						!available_lab.get(fsub.lab)[randomDay.get(d_count)][randomSlot.get(s_count)+w_count]&&
						checkTeachersAvailibility(temp.get(subTeacherNumber),randomDay.get(d_count),randomSlot.get(s_count)+w_count));	
					}
					if(valid){
						selectedDay=randomDay.get(d_count);
						selectedSlot=randomSlot.get(s_count);
						randomDay.remove(d_count);
						break;
					}
					s_count++;
					if(s_count==randomSlot.size()){
						s_count=0;
						d_count++;
					}
				}while(true);
				for(int w_count=0;w_count<fsub.length;w_count++){
					week[sg][selectedDay][selectedSlot+w_count][0]=temp.get(subTeacherNumber);
					available_week[sg][selectedDay][selectedSlot+w_count]=true;
					if(!fsub.lab.equals(""))
						available_lab.get(fsub.lab)[selectedDay][selectedSlot+w_count]=true;
				}
				for(int x1=0;x1<temp.get(subTeacherNumber).teachers.size();x1++){
					id_teacherMap.get(temp.get(subTeacherNumber).teachers.get(x1)).hours+=fsub.length;
					for(int w_count=0;w_count<fsub.length;w_count++){
						available_teacher.get(temp.get(subTeacherNumber).teachers.get(x1))[selectedDay][selectedSlot+w_count]=true;	
					}
				}
				if(fsub.length==2){
					week[sg][selectedDay][selectedSlot][1]=new SubjectTeacherCombo("MOOCS","");
					week[sg][selectedDay][selectedSlot+1][1]=new SubjectTeacherCombo("Library","");	
				}
				else if(fsub.length==4){
					Subject tut=tutorial.get(sg);
					Subject prac3=h3.get(sg);
					ArrayList<SubjectTeacherCombo> tuts=sub_TeacherComboMap.get(tut.id+" "+tut.dept);
					ArrayList<SubjectTeacherCombo> prac3s=sub_TeacherComboMap.get(prac3.id+" "+prac3.dept);
					flag=0;
					for(SubjectTeacherCombo t:tuts){
						for(SubjectTeacherCombo p:prac3s){
							boolean valid=true;
							if(prac3.lab.equals("")){
								for(int w_count=0;w_count<prac3.length;w_count++)
								valid&=(week[sg][selectedDay][selectedDay+1+w_count][1].isEmpty()&&
								checkTeachersAvailibility(p,selectedDay,selectedSlot+1+w_count));	
							}
							else{
								for(int w_count=0;w_count<prac3.length;w_count++)
								valid&=(week[sg][selectedDay][selectedSlot+1+w_count][1].isEmpty()&&
								!available_lab.get(prac3.lab)[selectedDay][selectedSlot+1+w_count]&&
								checkTeachersAvailibility(p,selectedDay,selectedSlot+1+w_count));	
							}
							valid&=(week[sg][selectedDay][selectedSlot][1].isEmpty()&&
								checkTeachersAvailibility(t,selectedDay,selectedSlot));
							if(valid){
								for(int w_count=0;w_count<prac3.length;w_count++){
									week[sg][selectedDay][selectedSlot+w_count+1][1]=new SubjectTeacherCombo(p);
									if(!prac3.lab.equals(""))
										available_lab.get(prac3.lab)[selectedDay][selectedSlot+w_count+1]=true;
								}
								for(int x1=0;x1<p.teachers.size();x1++){
									id_teacherMap.get(p.teachers.get(x1)).hours+=prac3.length;
									for(int w_count=0;w_count<prac3.length;w_count++){
										available_teacher.get(p.teachers.get(x1))[selectedDay][selectedSlot+w_count+1]=true;	
									}
								}
								week[sg][selectedDay][selectedSlot][1]=new SubjectTeacherCombo(t);
								available_teacher.get(t.teachers.get(0))[selectedDay][selectedSlot]=true;
								id_teacherMap.get(t.teachers.get(0)).hours++;
								flag=1;
								break;
							}
						}
						if(flag==1)
							break;
					}
					if(flag!=1)
						return false;
				}
			}
		}
		return true;
	}
	public boolean allocatePracticals(int i){
		//generateSubjectTeacherCombo();
		ArrayList<Integer> randomDay=new ArrayList<>();
		ArrayList<Integer> randomSlot=new ArrayList<>();
		HashMap<String,Integer> allocated=new HashMap<>();
		for(int j=0;j<days;j++)
			randomDay.add(j);
			ArrayList<Subject> practicals=new ArrayList<>();
			ArrayList<Subject> s_practicals;
			for(Subject j:subject_repo.get(i))
				if(j.getPractical().equalsIgnoreCase("true")){
					practicals.add(j);
				}
			s_practicals=new ArrayList<>(practicals);
			ArrayList<Subject>s1_practicals=new ArrayList<>(practicals);
			Collections.shuffle(s1_practicals);
			for(Subject j:s1_practicals){
				if(j==null)
					continue;
				Collections.shuffle(randomDay);
				randomSlot.clear();
				if(j.length==3){
					randomSlot.add(4);
					randomSlot.add(1);
					randomSlot.add(0);
				}
				else if(j.length==4){
					randomSlot.add(3);
					randomSlot.add(0);
				}
				else if(j.length==5){
					randomSlot.add(2);
					randomSlot.add(0);
				}
				else if(j.length==2){
					for(int l=0;l<slots-1;l++)
						randomSlot.add(l);
					Collections.shuffle(randomSlot);
				}
				Collections.shuffle(randomSlot);
				int selectedDay=0;
				int selectedSlot=0;
				int i_count=0;
				int subTeacherNumber=0;
				int flag=0;
				while(true){
					i_count=0;
					while(i_count<randomDay.size()){
						selectedDay=randomDay.get(i_count++);
						for(int s_count=0;s_count<randomSlot.size();s_count++){
							boolean valid=true;
							for(int w_count=0;w_count<j.length;w_count++){
								valid&=(week[i][selectedDay][randomSlot.get(s_count)+w_count][0].isEmpty()&&
									!available_week[i][selectedDay][randomSlot.get(s_count)+w_count]&&
									((j.lab.equals(""))?true:(!available_lab.get(j.lab)[selectedDay][randomSlot.get(s_count)+w_count]))&&
									checkTeachersAvailibility(sub_TeacherComboMap.get(j.id+" "+j.dept).get(subTeacherNumber),selectedDay,randomSlot.get(s_count)+w_count));
							}
							if(valid){
								selectedSlot=randomSlot.get(s_count);
								flag=1;
								break;
							}
						}
						if(flag==1)
							break;
					}
					if(flag==1)
						break;
					else{
						subTeacherNumber++;
					}
					if(subTeacherNumber==sub_TeacherComboMap.get(j.id+" "+j.dept).size()){
						return false;
					}
				}
				allocated.compute(j.id,(key,val)->val==null?1:val+1);
				if(allocated.get(j.id)!=null&&allocated.get(j.id)==2){
					for(int k=0;k<s_practicals.size();k++)
						if(s_practicals.get(k)!=null&&s_practicals.get(k).id==j.id){
							s_practicals.set(k,null);
							break;
						}
				}
				for(int w_count=0;w_count<j.length;w_count++){
					week[i][selectedDay][selectedSlot+w_count][0]=sub_TeacherComboMap.get(j.id+" "+j.dept).get(subTeacherNumber);
					available_lab.get(j.lab)[selectedDay][selectedSlot+w_count]=true;
					available_week[i][selectedDay][selectedSlot+w_count]=true;
				}
				for(int x=0;x<sub_TeacherComboMap.get(j.id+" "+j.dept).get(subTeacherNumber).teachers.size();x++){
					id_teacherMap.get(sub_TeacherComboMap.get(j.id+" "+j.dept).get(subTeacherNumber).teachers.get(x)).hours+=j.length;
					for(int w_count=0;w_count<j.length;w_count++){
						available_teacher.get(sub_TeacherComboMap.get(j.id+" "+j.dept).get(subTeacherNumber).teachers.get(x))[selectedDay][selectedSlot+w_count]=true;	
					}
				}
				flag=0;
				for(int k=0;k<s_practicals.size();k++){
					if(s_practicals.get(k)==null)
						continue;
					if(s_practicals.get(k).id==j.id)
						continue;
					if(s_practicals.get(k).length!=j.length)
						continue;
					subTeacherNumber=0;
					ArrayList<SubjectTeacherCombo> p2=sub_TeacherComboMap.get(s_practicals.get(k).id+" "+s_practicals.get(k).dept);
					if(p2==null)
						continue;
					while(subTeacherNumber<p2.size()){
						while(subTeacherNumber<p2.size()&&p2.get(subTeacherNumber)==null)
							subTeacherNumber++;
						if(subTeacherNumber==p2.size()){
							System.out.println(s_practicals.get(k).id);
							return false;
						}
						boolean valid=true;
						for(int w_count=0;w_count<j.length;w_count++){
							valid&=(week[i][selectedDay][selectedSlot+w_count][1].isEmpty()&&
							((s_practicals.get(k).lab.equals(""))?true:(!available_lab.get(s_practicals.get(k).lab)[selectedDay][selectedSlot+w_count]))&&
							checkTeachersAvailibility(p2.get(subTeacherNumber),selectedDay,selectedSlot+w_count));
						}
						if(valid){
							flag=1;
							for(int w_count=0;w_count<j.length;w_count++){
								week[i][selectedDay][selectedSlot+w_count][1]=p2.get(subTeacherNumber);
								available_lab.get(s_practicals.get(k).lab)[selectedDay][selectedSlot+w_count]=true;
							}
							for(int x=0;x<p2.get(subTeacherNumber).teachers.size();x++){
								id_teacherMap.get(p2.get(subTeacherNumber).teachers.get(x)).hours+=j.length;
								for(int w_count=0;w_count<j.length;w_count++){
									available_teacher.get(p2.get(subTeacherNumber).teachers.get(x))[selectedDay][selectedSlot+w_count]=true;	
								}
							}
							s_practicals.set(k,null);
						}
						else
							subTeacherNumber++;
					}
					if(flag==1)
						break;
				}
				if(flag==0){
					return false;
				}
			}
		return true;
	}
	public boolean allocateClasses(int i){
			ArrayList<Subject> classes=new ArrayList<>();
			ArrayList<Subject> s_classes;
			for(Subject j:subject_repo.get(i))
				if(!j.getPractical().equalsIgnoreCase("true")){
					if(j.id!=null||j.length==1)
						classes.add(j);
				}
			s_classes=new ArrayList<>(classes);
			Collections.shuffle(s_classes);
			for(Subject j: s_classes){
				if(!allocateSubject(i,j))
					return false;
			}
		return true;
	}
	public boolean allocateSubject(int x,Subject j){
		if(tutorial.size()>x){
			/*System.out.println("Subject repository");
			System.out.println(subject_repo);*/
			System.out.println("allocatesubject check begins");
			System.out.println("j is which subject="+j);
			System.out.println("tutorial.get(x) 's id="+tutorial.get(x).id);
			System.out.println("allocatesubject check ends");
			if(j.id.equals(tutorial.get(x).id))
				return true;
		}
		ArrayList<Integer> randomDay=new ArrayList<>();
		ArrayList<Integer> randomSlot=new ArrayList<>();
		int allocated=0;
		int selectedDay=-1;
		int selectedSlot=0;
		int d_count=0;
		int s_count=0;
		for(int i=0;i<days;i++)
			randomDay.add(i);
		if(j.length==1){
			for(int i=0;i<slots;i++)
				randomSlot.add(i);
			Collections.shuffle(randomSlot);
		}
		else if(j.length==3){
			randomSlot.add(4);
			randomSlot.add(1);
			randomSlot.add(0);
		}
		else if(j.length==4){
			randomSlot.add(3);
			randomSlot.add(0);
		}
		else if(j.length==5){
			randomSlot.add(2);
			randomSlot.add(0);
		}
		Collections.shuffle(randomDay);
		int subTeacherNumber=0;
		if(j.id!=null){
			ArrayList<SubjectTeacherCombo> temp=sub_TeacherComboMap.get(j.id+" "+j.dept);
			int flag=0;
			for(int i=0;i<j.hours;i+=j.length){
				d_count=(selectedDay+1)%randomDay.size();
				s_count=0;
				do{
					if(d_count==randomDay.size()){
						if(subTeacherNumber<temp.size()){
							subTeacherNumber++;
							d_count=0;
							s_count=0;
						}
						if(subTeacherNumber==temp.size()){
							flag=1;
							return false;
						}
					}
					boolean valid=true;
					if(j.lab.equals("")){
						for(int w_count=0;w_count<j.length;w_count++)
						valid&=(week[x][randomDay.get(d_count)][randomSlot.get(s_count)+w_count][0].isEmpty()&&
						!available_week[x][randomDay.get(d_count)][randomSlot.get(s_count)+w_count]&&
						checkTeachersAvailibility(temp.get(subTeacherNumber),randomDay.get(d_count),randomSlot.get(s_count)+w_count));	
					}
					else{
						for(int w_count=0;w_count<j.length;w_count++)
						valid&=(week[x][randomDay.get(d_count)][randomSlot.get(s_count)+w_count][0].isEmpty()&&
						!available_week[x][randomDay.get(d_count)][randomSlot.get(s_count)+w_count]&&
						!available_lab.get(j.lab)[randomDay.get(d_count)][randomSlot.get(s_count)+w_count]&&
						checkTeachersAvailibility(temp.get(subTeacherNumber),randomDay.get(d_count),randomSlot.get(s_count)+w_count));	
					}
					if(valid){
						selectedDay=randomDay.get(d_count);
						selectedSlot=randomSlot.get(s_count);
						randomDay.remove(d_count);
						break;
					}
					s_count++;
					if(s_count==randomSlot.size()){
						s_count=0;
						d_count++;
					}
				}while(true);
				for(int w_count=0;w_count<j.length;w_count++){
					week[x][selectedDay][selectedSlot+w_count][0]=temp.get(subTeacherNumber);
					available_week[x][selectedDay][selectedSlot+w_count]=true;
					if(!j.lab.equals(""))
						available_lab.get(j.lab)[selectedDay][selectedSlot+w_count]=true;
				}
				for(int x1=0;x1<temp.get(subTeacherNumber).teachers.size();x1++){
					id_teacherMap.get(temp.get(subTeacherNumber).teachers.get(x1)).hours+=j.length;
					for(int w_count=0;w_count<j.length;w_count++){
						available_teacher.get(temp.get(subTeacherNumber).teachers.get(x1))[selectedDay][selectedSlot+w_count]=true;	
					}
				}
			}
		}
		else{
			ArrayList<SubjectTeacherCombo> temp1=sub_TeacherComboMap.get(j.elecId[0]+" "+j.dept);
			ArrayList<SubjectTeacherCombo> temp2=sub_TeacherComboMap.get(j.elecId[1]+" "+j.dept);
			int flag=0;
			SubjectTeacherCombo subTeacher1=new SubjectTeacherCombo();
			SubjectTeacherCombo subTeacher2=new SubjectTeacherCombo();
			for(int i=0;i<j.hours;i+=j.length){
				d_count=(selectedDay+1)%randomDay.size();
				s_count=0;
				flag=0;
					for(SubjectTeacherCombo a:temp1){
						for(SubjectTeacherCombo b:temp2){
							while(true){
								boolean valid=true;
								if(j.elecLab[0].equals("")){
									for(int w_count=0;w_count<j.length;w_count++)
										valid&=(week[x][randomDay.get(d_count)][randomSlot.get(s_count)+w_count][0].isEmpty()&&
										!available_week[x][randomDay.get(d_count)][randomSlot.get(s_count)+w_count]&&
										checkTeachersAvailibility(a,randomDay.get(d_count),randomSlot.get(s_count)+w_count));	
								}
								else{
									for(int w_count=0;w_count<j.length;w_count++)
										valid&=(week[x][randomDay.get(d_count)][randomSlot.get(s_count)+w_count][0].isEmpty()&&
										!available_week[x][randomDay.get(d_count)][randomSlot.get(s_count)+w_count]&&
										!available_lab.get(j.elecLab[0])[randomDay.get(d_count)][randomSlot.get(s_count)+w_count]&&
										checkTeachersAvailibility(a,randomDay.get(d_count),randomSlot.get(s_count)+w_count));	
								}
								if(j.elecLab[1].equals("")){
									for(int w_count=0;w_count<j.length;w_count++)
										valid&=(week[x][randomDay.get(d_count)][randomSlot.get(s_count)+w_count][0].isEmpty()&&
										!available_week[x][randomDay.get(d_count)][randomSlot.get(s_count)+w_count]&&
										checkTeachersAvailibility(b,randomDay.get(d_count),randomSlot.get(s_count)+w_count));	
								}
								else{
									for(int w_count=0;w_count<j.length;w_count++)
										valid&=(week[x][randomDay.get(d_count)][randomSlot.get(s_count)+w_count][0].isEmpty()&&
										!available_week[x][randomDay.get(d_count)][randomSlot.get(s_count)+w_count]&&
										!available_lab.get(j.elecLab[1])[randomDay.get(d_count)][randomSlot.get(s_count)+w_count]&&
										checkTeachersAvailibility(b,randomDay.get(d_count),randomSlot.get(s_count)+w_count));	
								}
								if(valid){
									selectedDay=randomDay.get(d_count);
									selectedSlot=randomSlot.get(s_count);
									flag=1;
									subTeacher1=a;
									subTeacher2=b;
									randomDay.remove(d_count);
									break;
								}
								s_count++;
								if(s_count==randomSlot.size()){
									s_count=0;
									d_count++;
								}
								if(d_count==randomDay.size()){
									d_count=0;
									s_count=0;
									break;
								}
							}
							if(flag==1)
								break;
						}
						if(flag==1)
							break;
					}
				if(flag!=1){
					return false;
				}
				for(int w_count=0;w_count<j.length;w_count++){
					week[x][selectedDay][selectedSlot+w_count][0]=new SubjectTeacherCombo(subTeacher1);
					week[x][selectedDay][selectedSlot+w_count][1]=new SubjectTeacherCombo(subTeacher2);
					available_week[x][selectedDay][selectedSlot+w_count]=true;
					if(!j.elecLab[0].equals(""))
						available_lab.get(j.elecLab[0])[selectedDay][selectedSlot+w_count]=true;
					if(!j.elecLab[1].equals(""))
						available_lab.get(j.elecLab[1])[selectedDay][selectedSlot+w_count]=true;
				}
				for(int x1=0;x1<subTeacher1.teachers.size();x1++){
					id_teacherMap.get(subTeacher1.teachers.get(x1)).hours+=j.length;
					for(int w_count=0;w_count<j.length;w_count++){
						available_teacher.get(subTeacher1.teachers.get(x1))[selectedDay][selectedSlot+w_count]=true;	
					}
				}
				for(int x1=0;x1<subTeacher2.teachers.size();x1++){
					id_teacherMap.get(subTeacher2.teachers.get(x1)).hours+=j.length;
					for(int w_count=0;w_count<j.length;w_count++){
						available_teacher.get(subTeacher2.teachers.get(x1))[selectedDay][selectedSlot+w_count]=true;	
					}
				}
			}
		}
	return true;
	}
	public boolean checkTeachersAvailibility(SubjectTeacherCombo s,int day,int slot){
		for(String i:s.teachers){
			if(available_teacher.get(i)[day][slot])
				return false;
		}
		return true;
	}
	public void generateSubjectTeacherCombo(){
		sub_TeacherComboMap=new HashMap<>();
		/*just for checking if format of teaches matches that of id_subjectmap
		for(String iki:teaches.keySet())
			System.out.println(iki);
		System.out.println("Printing idsubject map keyset");
		
		for(String ki:id_subjectMap.keySet())
			System.out.println(ki + " subject->   " +id_subjectMap.get(ki));*/
		
		for(String i:teaches.keySet()){
			if(!sub_TeacherComboMap.containsKey(i)){
				sub_TeacherComboMap.put(i,new ArrayList<SubjectTeacherCombo>());
			}
			ArrayList<String> temp=teaches.get(i);
			//System.out.println(i);
			Subject tempSub=id_subjectMap.get(i);
			if(tempSub.n_teachers==1){
				for(String j:temp)
					sub_TeacherComboMap.get(i).add(new SubjectTeacherCombo(i,j));					
			}
			else{
				for(int j=0;j<temp.size();j++)
					for(int k=j+1;k<temp.size();k++)
						sub_TeacherComboMap.get(i).add(new SubjectTeacherCombo(i,temp.get(j),temp.get(k)));
			}
			Collections.shuffle(sub_TeacherComboMap.get(i));
		}
		/*for(String i:sub_TeacherComboMap.keySet())
			System.out.println(i+" "+sub_TeacherComboMap.get(i));*/
	}
	
	public void commit(){
		for(int i=0;i<16;i++){
			for(int j=0;j<days;j++){
				for(int k=0;k<slots;k++){
					week_final[i][j][k][0]=new SubjectTeacherCombo(week[i][j][k][0]);
					week_final[i][j][k][1]=new SubjectTeacherCombo(week[i][j][k][1]);
					available_week_final[i][j][k]=available_week[i][j][k];
				}
			}
		}
		for(String i:available_lab.keySet()){
			boolean temp[][]=new boolean[days][slots];
			boolean temp1[][]=available_lab.get(i);
			for(int j=0;j<days;j++){
				for(int k=0;k<slots;k++){
					temp[j][k]=temp1[j][k];
				}
			}
			available_lab_final.put(i,temp);
		}
		for(String i:available_teacher.keySet()){
			boolean temp[][]=new boolean[days][slots];
			boolean temp1[][]=available_teacher.get(i);
			for(int j=0;j<days;j++){
				for(int k=0;k<slots;k++){
					temp[j][k]=temp1[j][k];
				}
			}
			available_teacher_final.put(i,temp);
		}
	}
	public void restore(){
		for(int i=0;i<16;i++)
			for(int j=0;j<days;j++)
				for(int k=0;k<slots;k++){
					week[i][j][k][0]=new SubjectTeacherCombo(week_final[i][j][k][0]);
					week[i][j][k][1]=new SubjectTeacherCombo(week_final[i][j][k][1]);
					available_week[i][j][k]=available_week_final[i][j][k];
				}
		for(String i:available_lab.keySet()){
			boolean temp[][]=new boolean[days][slots];
			boolean temp1[][]=available_lab_final.get(i);
			for(int j=0;j<days;j++){
				for(int k=0;k<slots;k++){
					temp[j][k]=temp1[j][k];
				}
			}
			available_lab.put(i,temp);
		}
		for(String i:available_teacher.keySet()){
			boolean temp[][]=new boolean[days][slots];
			boolean temp1[][]=available_teacher_final.get(i);
			for(int j=0;j<days;j++){
				for(int k=0;k<slots;k++){
					temp[j][k]=temp1[j][k];
				}	
			}
			available_teacher.put(i,temp);
		}
	}
	public void showTeacherHours(){
		for(Teacher i:teacher_repo)
			System.out.println(i.id+":"+i.hours);
	}
	public void checkAllocations(){
		HashMap<String,Integer> h=new HashMap<>();
		String dept=null;
		for(int i=0;i<16;i++){
			if(i%4==0)
				dept="CSE";
			else if(i%4==1)
				dept="IT";
			else if(i%4==2)
				dept="ECE";
			else
				dept="EE";
			for(int j=0;j<days;j++){
				for(int k=0;k<slots;k++){
					for(SubjectTeacherCombo x: week[i][j][k]){
						if(x!=null)
							h.compute(x.subject+" "+dept,(key,val)->val==null?1:val+1);
					}
				}
			}
		}
		for(ArrayList<Subject> j: subject_repo){
			for(Subject i:j){
				if(i.id==null){
					System.out.println(i.elecId[0]+" "+h.get(i.elecId[0]+" "+i.dept)+" "+i.hours);
					System.out.println(i.elecId[1]+" "+h.get(i.elecId[1]+" "+i.dept)+" "+i.hours);
				}
				else
					System.out.println(i.id+" "+h.get(i.id+" "+i.dept)+" "+i.hours);
			}
		}
	}
	public void showTeacherRoutine(String t){
		for(int j=0;j<days;j++){
			switch(j){
				case 0:
					System.out.print("Monday : ");
					break;
				case 1:
					System.out.print("Tuesday : ");
					break;
				case 2:
					System.out.print("Wednesday : ");
					break;
				case 3:
					System.out.print("Thursday : ");
					break;
				case 4:
					System.out.print("Friday : ");
					break;
			}
			System.out.print("|");
			for(int k=0;k<slots;k++){
				for(int i=0;i<16;i++){
					for(SubjectTeacherCombo x: week[i][j][k]){
						if(x.teachers.contains(t)){
							System.out.print(" "+x);
							break;
						}
					}
				}
				System.out.print(" |");
			}
			System.out.println();
		}
	}
	
	public boolean allocateAll(){
		for(int i=0;i<16;i++){
			int k=0;
			while(true){
				if(i/4==0){
					if(this.allocateFirstYearPracticals(i)&&this.allocateClasses(i))
						break;
				}
				else{
					if(this.allocatePracticals(i)&&this.allocateClasses(i))
						break;
				}
				this.restore();
				this.generateSubjectTeacherCombo();
				k++;
				if(k>500)
					return false;
			}
			//System.out.println(i);
			//this.showTable();
			//(new Scanner(System.in)).nextLine();
			this.commit();
		}
		return true;
	}

	public void showTable(int year)throws Exception{
		String excelFilePath="";
		if(year==1) {
			excelFilePath = "src/main/resources/public/FirstYear.xlsx";
		}
		else if(year==2) {
			excelFilePath = "src/main/resources/public/SecondYear.xlsx";
		}
		else if(year==3) {
			excelFilePath = "src/main/resources/public/ThirdYear.xlsx";
		}
		else {
			excelFilePath = "src/main/resources/public/FourthYear.xlsx";
		}
		//ByteArrayOutputStream out=new ByteArrayOutputStream();
		
		FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
		workbook.removeSheetAt(0);
        //Create a blank sheet
        Sheet sheet = workbook.createSheet("Time Table");
        
        int rownum=0;    
        int cellnum = 0;
        
		for(int i=0;i<16;i++){
			int y=i%4;
			if((year-1)!=(i/4)) {
				continue;
			}
			String department="";
			cellnum=0;
			Row row=sheet.createRow(rownum++);
	        Cell heading=row.createCell(cellnum++);
			if(y==0) {
				System.out.print("CSE");
				heading.setCellValue("CSE");
				department="CSE";
			}
			else if(y==1) {
				System.out.print("IT");
				heading.setCellValue("IT");
				department="IT";
			}
			else if(y==2) {
				System.out.print("ECE");
				heading.setCellValue("ECE");
				department="ECE";
			}
			else {
				System.out.print("EE");
				heading.setCellValue("EE");
				department="EE";
			}
			y=i/4;
			cellnum=0;
			row=sheet.createRow(rownum++);
	        heading=row.createCell(cellnum++);
			if(y==0){
				System.out.println(" 1st year");
				heading.setCellValue(" 1st year ");
			}
			else if(y==1){
				System.out.println(" 2nd year");
				heading.setCellValue(" 2nd year ");
			}
			else if(y==2){
				System.out.println(" 3rd year");
				heading.setCellValue(" 3rd year ");
			}
			else{
				System.out.println(" 4th year");
				heading.setCellValue(" 4th year ");
			}
			
			
			
			for(int j=0;j<days;j++){
				cellnum=0;
				row=sheet.createRow(rownum++);
				Cell dayname=row.createCell(cellnum++);
				switch(j){
					case 0:
						System.out.print("Monday : ");
						dayname.setCellValue("Monday : ");
						break;
					case 1:
						System.out.print("Tuesday : ");
						dayname.setCellValue("Tuesday : ");
						break;
					case 2:
						System.out.print("Wednesday : ");
						dayname.setCellValue("Wednesday : ");
						break;
					case 3:
						System.out.print("Thursday : ");
						dayname.setCellValue("Thursday : ");
						break;
					case 4:
						System.out.print("Friday : ");
						dayname.setCellValue("Friday : ");
						break;
				}
				row = sheet.createRow(rownum++);
				cellnum=0;
				System.out.print("|");
				for(int k=0;k<slots;k++){
					Cell cell=row.createCell(cellnum++);
					String p="";
					String q="";
					if(week[i][j][k][0]==null && week[i][j][k][1]==null){
						cell.setCellValue("Free Period!");
						continue;	
					}
					
					if((week[i][j][k][0]!=null && (week[i][j][k][0].getSubject()==null || week[i][j][k][0].getSubject().equals(""))) && (week[i][j][k][1]!=null && (week[i][j][k][1].getSubject()==null || week[i][j][k][1].getSubject().equals("")))){
						cell.setCellValue("Free Period!");
						continue;	
					}
					
					if(week[i][j][k][0]!=null){
						p=week[i][j][k][0].getSubject();
						if(p==null)
							p="";
						System.out.println("p="+p);
					}
					if(week[i][j][k][1]!=null){
						q=week[i][j][k][1].getSubject();
						if(q==null)
							q="";
						System.out.println("q="+q);
					}
					String pteachers="",qteachers="";
					if(!p.equals(""))
						pteachers=week[i][j][k][0].getTeachersString();
					if(!q.equals(""))
						qteachers=week[i][j][k][1].getTeachersString();
					
					cell.setCellValue(p+ pteachers + "  /  " +q + qteachers);
					Subject subSpan=null;
					String sub=p;
					if(!p.equals("")) {
					subSpan=subjectsDAO.getSubjectById(sub,department);
					}
					else if(!q.equals("")) {
						sub=q;
						subSpan=subjectsDAO.getSubjectById(sub,department);
					}
					
					for(int idx=1;idx<subSpan.getLength();idx++){
						cell=row.createCell(cellnum++);
						cell.setCellValue(p + pteachers + "  /  " +q + qteachers);
					}
					for(SubjectTeacherCombo x: week[i][j][k]){						
						System.out.print(" "+x);
					}
					System.out.print("|");
					
					k+=subSpan.getLength() - 1;
				}
				System.out.println();
				
			}
		}
		
		FileOutputStream outputStream = new FileOutputStream(excelFilePath);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
	}
	

	public void startGeneration()throws Exception{
		//Scanner sc=new Scanner(System.in);
		int k=1;
		//System.out.println(d.sub_TeacherComboMap);
		d.populateFromDb();
		d.initialize();
		d.generateSubjectTeacherCombo();
		
		//d=new ExcelGenerator();
		/*d.initialize();
			d.printSubjects();
		d.generateSubjectTeacherCombo();*/
		while(!d.allocateAll()){
			//String s=sc.nextLine();
			//d=new ExcelGenerator();			
			d.initialize();
			d.generateSubjectTeacherCombo();
			k++;
		}
		
		System.out.println("Completed in "+k+" iterations");
		//ByteArrayInputStream bais=null;
		try {
			d.checkAllocations();
			d.showTable(1);
			d.showTable(2);
			d.showTable(3);
			d.showTable(4);
			d.showTeacherRoutine("DC");
		}
		catch(IOException e) {}
		//d.showTeacherHours();
		
	}
	

}

