package com.qldt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.qldt.form.CalenderForm;
import com.qldt.form.CalenderUser;
import com.qldt.model.*;
import com.qldt.repo.*;

@Controller
public class ScheduleController {

	public static Logger logger = LoggerFactory.getLogger(LoginController.class);

	private List<IsRegister> isregister = new ArrayList<IsRegister>(); // danh sach sinh vien dang ki mon hoc
	private List<Subject> subject = new ArrayList<Subject>(); // danh sach mon hoc
	private List<Subject> isregisteruser; // danh sach dang ki mon hoc cua sinh vien co id = userid
	private List<CalenderUser> calenderuser;
	private String weeks[]; // danh sach tuan hoc trong hoc ki (co dang yyyy/MM/dd)
	private String toweek[]; // danh sach tuan hoc toString()
	private String weeknow = ""; // tuan hien tai (thoi gian thuc)
	private String weekchoose = ""; // tuan duoc chon de tim kiem
	private int calenderid; // so thu tu tuan duoc luu trong csdl, neu so tu thu = '-' tuc la tuan do k hoc,
							// con neu = 1 so thi tuan do co lich hoc
	private int checkcalender = 0; // kiem tra xem co lich hoc trong tuan nay khong, neu khong co thi khong in ra
									// thoi khoa bieu
	private int uid;

	@Autowired
	private SubjectRepository subjectRepository;

	@Autowired
	private IsRegisterRepository isRegisterRepository;

	// GET schedule
	@RequestMapping(value = "/schedule", method = RequestMethod.GET)
	private String GetSchedule(Model model) {

		uid = LoginController.userid;
		if (uid < 0) {
			return "redirect:/login";
		} else {
			subject = subjectRepository.findAll(); // cac khai bao co ban de lay du lieu tu csdl
			isregister = isRegisterRepository.findAll();
			isregisteruser = new ArrayList<Subject>();
			calenderuser = new ArrayList<CalenderUser>();

			// lay danh sach dang ki mon hoc cua userid
			for (IsRegister i : isregister) {
				if (i.getIdaccount() == uid) {
					for (Subject s : subject) {
						if (i.getIdsubject() == s.getId()) {
							isregisteruser.add(s);
						}
					}
				}
			}

			weeks = new String[25]; // lay vidu 25 tuan trong ki 1
			toweek = new String[24];
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date(System.currentTimeMillis());
			String datenow = sdf.format(date); // lay gia tri thoi gian hien tai
			weeks = getDate();

			for (int i = 0; i < 24; i++) { // toString cho weeks[]
				toweek[i] = "Từ ngày [ " + weeks[i] + " ] đến ngày trước ngày [ " + weeks[i + 1] + " ] \t - Tuần "
						+ (i + 1);
			}

			if (weekchoose.equals("")) { // kiem tra xem tuan duoc chon co dang rong khong?
				for (int i = 0; i < 24; i++) {
					if (datenow.compareTo(weeks[i]) >= 0 && datenow.compareTo(weeks[i + 1]) < 0) {
						calenderid = i; // gan id tuan cho calenderid
						weeknow = toweek[i];
						weekchoose = toweek[i]; // Neu tuan duoc chon dang rong thi set tuan duoc chon bang thoi gian
												// thuc
						break;
					}
				}
			} else {
				for (int i = 0; i < 24; i++) {
					if (weekchoose.equals(toweek[i])) {
						calenderid = i;
						break;
					}
				}
			}

			// neu mon hoc co 3 tin tro len thi se co 2 lich hoc cho 2 kip, con 2 kip tro
			// xuong thi chi co 1 lich hoc nen can kiem tra xem Schedule2 co null hay khong
			// (trong csdl toi dang gan null = #)
			for (Subject i : isregisteruser) {
				getCalenderUser(i, 1, i.getSchedule1());
				if (i.getSchedule2().compareTo("#") != 0) {
					getCalenderUser(i, 2, i.getSchedule2());
				}
			}

			// load cac du lieu len trang web
			model.addAttribute("checkcalender", checkcalender);
			model.addAttribute("userschedule", calenderuser);
			model.addAttribute("weeknow", weekchoose);
			model.addAttribute("toweek", toweek);
			model.addAttribute("nameuser", LoginController.nameuser);
			return "schedule";
		}
	}

	// Chon tuan va tim kiem lich hoc
	@RequestMapping(value = "/schedule", method = RequestMethod.POST)
	private String PostSchedule(@ModelAttribute("calenderForm") CalenderForm calenderForm, Model model) {

		if (uid < 0) {
			return "redirect:/login";
		} else {
			String calender = calenderForm.getCalender();

			if (calender.compareTo("---") != 0) {
				weekchoose = calender;
			} else {
				weekchoose = weeknow;
			}
			return "redirect:/schedule";
		}
	}

	// ham tao 25 tuan, cu moi tuan + them 7 ngay, tuan dau tien giu nguyen, bat dau
	// tu ngay 04/01/2021
	private String[] getDate() {

		int day = 0;
		String date[] = new String[25];

		for (int i = 0; i < 25; i++) {
			day += (i > 0) ? 7 : 0;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			Calendar c = Calendar.getInstance();
			try {
				c.setTime(sdf.parse("2021/01/04"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			c.add(Calendar.DAY_OF_MONTH, day); // cong them 7 ngay
			date[i] = sdf.format(c.getTime()); // chuyen c thanh ngay chinh xac
		}
		return date;
	}

	// ham kiem tra tuan hoc voi calenderid.... gia su tuan hoc la ---456--90 thi
	// calenderid = 1/2/3/7/8 se khong co lich hoc con calenderid = 4/5/6/9/10 thi
	// co lich hoc, luu vao bien de show ra
	private void getCalenderUser(Subject i, int cd, String str) {

		for (int j = str.length(); j < 24; j++) {
			str += "-";
		}

		if (Character.toString(str.charAt(calenderid)).compareTo("-") != 0) {
			checkcalender = 1;
			if (cd == 1) {
				calenderuser.add(new CalenderUser(i.getShift1(), i.getName(), i.getLearn_day1(), i.getCode(),
						i.getGroupp(), i.getTeacher(), i.getRoom()));
			} else {
				calenderuser.add(new CalenderUser(i.getShift2(), i.getName(), i.getLearn_day2(), i.getCode(),
						i.getGroupp(), i.getTeacher(), i.getRoom()));
			}
		} else {
			checkcalender = 0;
		}
	}
}
