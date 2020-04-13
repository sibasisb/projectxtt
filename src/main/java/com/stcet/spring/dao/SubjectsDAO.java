package com.stcet.spring.dao;

import java.util.List;
import com.stcet.model.Subject;
import com.stcet.model.SubjectMapper;
//import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@ComponentScan(basePackages = {
		"com.stcet.demo",
        "com.stcet.demo.Controller"})
public class SubjectsDAO {
	
	private final String SQL_FIND_SUBJECT="select * from subjects where subject_id=? and dept=?";
	private final String SQL_DELETE_SUBJECT="delete from subjects where subject_id=? and dept=?";
	private final String SQL_UPDATE_SUBJECT="update subjects set subject_id=?,length=?,isPractical=?,subj_load=?,nteachers=?,dept=?,teachersList=?,class_allocated=? where subject_id=? and dept=?";
	private final String SQL_GET_ALL="select subject_id,length,isPractical,subj_load,nteachers,dept,teachersList,class_allocated from subjects";
	private final String SQL_CREATE_SUBJECT="insert into subjects(subject_id,length,isPractical,subj_load,nteachers,dept,teachersList,class_allocated) values(?,?,?,?,?,?,?,?)";
	private final String SQL_FIND_SUBJECTSBYID="select subject_id,length,isPractical,subj_load,nteachers,dept,teachersList,class_allocated from subjects where dept like ? and subject_id ~* ?";
	private JdbcTemplate jdbcTemplateSub;
	@Autowired
	public SubjectsDAO(JdbcTemplate j) {
		jdbcTemplateSub=j;
	}
	
	public Subject getSubjectById(String id,String dept) {
		return jdbcTemplateSub.queryForObject(SQL_FIND_SUBJECT,new Object[] {id,dept},new SubjectMapper());
	}
	
	public List<Subject> getSubjectsById(String dept,String id) {
		return jdbcTemplateSub.query(SQL_FIND_SUBJECTSBYID,new Object[] {dept,id},new SubjectMapper());
	}
	
	public boolean deleteSubject(String sub_id,String dept) {
		return jdbcTemplateSub.update(SQL_DELETE_SUBJECT,sub_id,dept)>0;
	}
	public boolean updateSubject(Subject subject) {
		return jdbcTemplateSub.update(SQL_UPDATE_SUBJECT,subject.getId(),subject.getLength(),subject.getPractical(),subject.getHours(),subject.getN_teachers(),subject.getDept(),subject.getTeachersList(),subject.getLab(),subject.getId(),subject.getDept())>0;
	}
	public boolean createSubject(Subject subject) {
		return jdbcTemplateSub.update(SQL_CREATE_SUBJECT,subject.getId(),subject.getLength(),subject.getPractical(),subject.getHours(),subject.getN_teachers(),subject.getDept(),subject.getTeachersList(),subject.getLab())>0;
	}
	
	
	
	public List<Subject> getAllSubjects(){
		return jdbcTemplateSub.query(SQL_GET_ALL,new SubjectMapper());
	}
}
