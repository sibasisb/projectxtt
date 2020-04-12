package com.stcet.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;


public class SubjectMapper implements RowMapper<Subject> {

	@Override
	public Subject mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Subject subject=new Subject();
		subject.setId(rs.getString("subject_id"));
		
		subject.setLength(rs.getInt("length"));
		subject.setPractical(rs.getString("isPractical"));	
		subject.setHours(rs.getInt("subj_load"));
		subject.setN_teachers(rs.getInt("nteachers"));
		subject.setDept(rs.getString("dept"));
		subject.setTeachersList(rs.getString("teachersList"));
		subject.setLab(rs.getString("class_allocated"));
		return subject;
	}

}
