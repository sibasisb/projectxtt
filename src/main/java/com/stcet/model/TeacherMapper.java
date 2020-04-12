package com.stcet.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class TeacherMapper implements RowMapper<Teacher> {

	@Override
	public Teacher mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Teacher teacher=new Teacher();
		teacher.setId(rs.getString("tid"));
		teacher.setHours(rs.getInt("allocated_hours"));
		return teacher;
	}
	
}
