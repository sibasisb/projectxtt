package com.stcet.spring.dao;

import java.util.List;
import com.stcet.model.Teacher;
import com.stcet.model.TeacherMapper;
//import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TeachersDAO {
	
	private JdbcTemplate jdbcTemplate;
	private final String SQL_FIND_TEACHER="select * from teachers where tid=?";
	private final String SQL_DELETE_TEACHER="delete from teachers where tid=?";
	private final String SQL_UPDATE_TEACHER="update teachers set tid=?,allocated_hours=? where tid=?";
	private final String SQL_GET_ALL="select * from teachers";
	private final String SQL_INSERT_TEACHER="insert into teachers(tid,allocated_hours) values(?,?)";
	
	
	@Autowired
	public TeachersDAO(JdbcTemplate j) {
		jdbcTemplate=j;
	}
	public Teacher getTeacherByTid(String id) {
		return jdbcTemplate.queryForObject(SQL_FIND_TEACHER,new Object[] {id},new TeacherMapper());
	}
	public boolean deleteTeacher(String tid) {
		return jdbcTemplate.update(SQL_DELETE_TEACHER,tid)>0;
	}
	public boolean updateTeacher(Teacher teacher) {
		return jdbcTemplate.update(SQL_UPDATE_TEACHER,teacher.getId(),teacher.getHours(),teacher.getId())>0;
	}
	public boolean createTeacher(Teacher teacher) {
		return jdbcTemplate.update(SQL_INSERT_TEACHER,teacher.getId(),teacher.getHours())>0;
	}

	public List<Teacher> getAllTeachers(){
		return jdbcTemplate.query(SQL_GET_ALL,new TeacherMapper());
	}
}
