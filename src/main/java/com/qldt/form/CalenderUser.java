package com.qldt.form;

public class CalenderUser {

	private int shift;
	private String name, learn_day, code;
	private int groupp;
	private String teacher, room;

	public CalenderUser() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CalenderUser(int shift, String name, String learnday, String code, int groupp, String teacher, String room) {
		super();
		this.shift = shift;
		this.name = name;
		this.learn_day = learnday;
		this.code = code;
		this.groupp = groupp;
		this.teacher = teacher;
		this.room = room;
	}

	public int getShift() {
		return shift;
	}

	public void setShift(int shift) {
		this.shift = shift;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLearn_day() {
		return learn_day;
	}

	public void setLearn_day(String learnday) {
		this.learn_day = learnday;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getGroupp() {
		return groupp;
	}

	public void setGroupp(int groupp) {
		this.groupp = groupp;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

}
