package com.qldt.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Subject implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String code, name, majors;
	private int semester, num_tc, groupp, practice_group, total, surplus;
	private String room, teacher, learn_day1;
	private int shift1;
	private String schedule1, learn_day2;
	private int shift2;
	private String schedule2;
	private int checkdisable;

	public Subject() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Subject(int id, String code, String name, String majors, int semester, int num_tc, int groupp,
			int practice_group, int total, int surplus, String room, String teacher, String learn_day1, int shift1,
			String schedule1, String learn_day2, int shift2, String schedule2, int checkdisable) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
		this.majors = majors;
		this.semester = semester;
		this.num_tc = num_tc;
		this.groupp = groupp;
		this.practice_group = practice_group;
		this.total = total;
		this.surplus = surplus;
		this.room = room;
		this.teacher = teacher;
		this.learn_day1 = learn_day1;
		this.shift1 = shift1;
		this.schedule1 = schedule1;
		this.learn_day2 = learn_day2;
		this.shift2 = shift2;
		this.schedule2 = schedule2;
		this.checkdisable = checkdisable;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMajors() {
		return majors;
	}

	public void setMajors(String majors) {
		this.majors = majors;
	}

	public int getSemester() {
		return semester;
	}

	public void setSemester(int semester) {
		this.semester = semester;
	}

	public int getNum_tc() {
		return num_tc;
	}

	public void setNum_tc(int num_tc) {
		this.num_tc = num_tc;
	}

	public int getGroupp() {
		return groupp;
	}

	public void setGroupp(int groupp) {
		this.groupp = groupp;
	}

	public int getPractice_group() {
		return practice_group;
	}

	public void setPractice_group(int practice_group) {
		this.practice_group = practice_group;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getSurplus() {
		return surplus;
	}

	public void setSurplus(int surplus) {
		this.surplus = surplus;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public String getLearn_day1() {
		return learn_day1;
	}

	public void setLearn_day1(String learn_day1) {
		this.learn_day1 = learn_day1;
	}

	public int getShift1() {
		return shift1;
	}

	public void setShift1(int shift1) {
		this.shift1 = shift1;
	}

	public String getSchedule1() {
		return schedule1;
	}

	public void setSchedule1(String schedule1) {
		this.schedule1 = schedule1;
	}

	public String getLearn_day2() {
		return learn_day2;
	}

	public void setLearn_day2(String learn_day2) {
		this.learn_day2 = learn_day2;
	}

	public int getShift2() {
		return shift2;
	}

	public void setShift2(int shift2) {
		this.shift2 = shift2;
	}

	public String getSchedule2() {
		return schedule2;
	}

	public void setSchedule2(String schedule2) {
		this.schedule2 = schedule2;
	}

	public int getCheckdisable() {
		return checkdisable;
	}

	public void setCheckdisable(int checkdisable) {
		this.checkdisable = checkdisable;
	}

	public int getMoney() {
		return num_tc * 480000;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}