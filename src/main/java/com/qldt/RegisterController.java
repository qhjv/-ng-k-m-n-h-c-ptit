package com.qldt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.qldt.form.SubjectForm;
import com.qldt.model.*;
import com.qldt.repo.*;

@Controller
public class RegisterController {

	public static Logger logger = LoggerFactory.getLogger(LoginController.class);

	private List<IsRegister> isregister = new ArrayList<IsRegister>(); // isregister la danh sach mon hoc cac sinh vien
																		// da dang ki
	private List<Subject> subject = new ArrayList<Subject>(); // subject la danh sach mon hoc
	private List<Subject> searchsubject; // searchsubject la danh sach mon hoc sau khi tim kiem
	private List<Subject> isregisteruser; // isregisteruser la danh sach mon hoc da dang ki cua sinh vien co userid =
											// userid da dang nhap truoc do
	private int summoney, sotc; // tinh tong tien hoc // tinh tong so tin dang ki
	private String code; // ma mon hoc duoc nhap vao trong o tim kiem
	private String[] errorSaveMessage = new String[10];
	private boolean checksub[], load = false, checkduedate = false; // checksub[] dung de kiem tra xem mon hoc da dang
																	// ki chua? Neu roi thi set checked = true tren
																	// trang web // load la bien de kiem tra
	// trang thai dang ki cua userid, neu load = false nghia la userid vua dang nhap
	// vao hoac vua update csdl, ta can phai load lai du lieu moi vao cac bien,
	// nguoc lai neu load = false thi ta khong can load lai cac bien luu tru //
	// checkduedate = kiem tra khung gio dang ki
	private int uid, save = 0; // uid = LoginController.userid (get id cua user) // save de kiem tra luu hay
	// chua

	@Autowired
	private SubjectRepository subjectRepository;

	@Autowired
	private IsRegisterRepository isRegisterRepository;

	// chuc nang hien thi mon hoc da dang ki va tinh tong hoc phi (khi chon chuc
	// nang dang ki thi mac dinh se goi ham 'registersub' voi phuong thuc GET de
	// hien thi trang dang ki)
	@RequestMapping(value = "/registersub", method = RequestMethod.GET)
	private String RegisterSub(Model model) {

		uid = LoginController.userid;

		if (uid < 0) {
			return "redirect:/login";
		} else {
			if (load == false) {
				subject = subjectRepository.findAll();
				isregister = isRegisterRepository.findAll();
				load = true;
			}

			String errorSearchMessage = "";
			checksub = new boolean[500];
			summoney = 0;
			sotc = 0;
			isregisteruser = new ArrayList<Subject>();

			if (isregister != null && isregister.size() != 0) {
				for (IsRegister i : isregister) { // search xem da co mon hoc nao dang ki san trong csdl chua, neu co
													// thi load len web va set checked = true
					if (i.getIdaccount() == uid) {
						for (Subject s : subject) {
							if (i.getIdsubject() == s.getId()) {
								summoney += s.getMoney(); // tinh tong tien hoc
								checksub[s.getId()] = true;
								isregisteruser.add(s);
								sotc += s.getNum_tc();
							}
						}
					}
				}
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date(System.currentTimeMillis());
			String datenow = sdf.format(date); // ngay hien tai theo +UTC
			String startdate = "2021/05/13"; // ngay bat dau dang ki mon hoc
			String finishdate = "2021/12/20"; // ngay ket thuc dang ki mon hoc

			// tinh hoc ki hien tai
			String[] splitdatenow = datenow.split("/");
			int semester = (Integer.parseInt(splitdatenow[0]) - LoginController.year) * 2;
			if (splitdatenow[1].compareTo("06") >= 0 && splitdatenow[2].compareTo("15") >= 0) {
				semester += 1;
			}

			// disable chuc nang dang ki khi het han dang ki
			if (datenow.compareTo(startdate) < 0 || datenow.compareTo(finishdate) > 0) {
				errorSearchMessage = "Ngoài thời gian đăng kí môn học";
				checkduedate = true;
				for (Subject s : subject) {
					s.setCheckdisable(1);
				}
			} else {
				checkduedate = false;
				for (Subject s : subject) {
					s.setCheckdisable(0);
				}
			}

			if (searchsubject != null && isregisteruser != null && searchsubject.size() != 0) {
				// disable khi khong duoc phep dang ki (khong cung nganh hoac khac hoc ki)
				int hocki = searchsubject.get(0).getSemester();
				if (searchsubject.get(0).getMajors().compareTo(LoginController.majors) != 0 || semester + 1 != hocki) {
					errorSearchMessage = "Bạn không có quyền đăng kí môn học này";
					checkduedate = true;
					for (Subject s : searchsubject) {
						s.setCheckdisable(1);
					}
				} else {
					checkduedate = false;
					for (Subject s : searchsubject) {
						s.setCheckdisable(0);
					}
				}

				// disable cac mon hoc da duoc chon vao danh sach dang ki
				for (Subject s : isregisteruser) {
					if (searchsubject.get(0).getCode().equals(s.getCode())) {
						for (Subject sj : searchsubject) {
							sj.setCheckdisable(1);
						}
						break;
					}
				}

				// disable id subject co slot = 0
				for (Subject s : searchsubject) {
					if (s.getSurplus() == 0) {
						s.setCheckdisable(1);
					}
				}
			}

			if (code != null && searchsubject.size() == 0) { // kiem tra search
				errorSearchMessage = "Mã môn học không đúng hoặc bạn không có quyền tìm kiếm môn học này";
			}

			// day cac gia tri vao trang web (kiem tra thuoc tinh co dang name="?", ? tuong
			// uong voi cac bien duoc day len)
			model.addAttribute("errorSearchMessage", errorSearchMessage);
			model.addAttribute("duedate", checkduedate);
			model.addAttribute("errorSaveMessage", errorSaveMessage);
			model.addAttribute("checks", checksub);
			model.addAttribute("summoney", summoney);
			model.addAttribute("mamonhoc", code);
			model.addAttribute("nameuser", LoginController.nameuser);
			model.addAttribute("isregisteruser", isregisteruser);
			model.addAttribute("searchsubject", searchsubject);
			return "registersub";
		}
	}

	// chuc nang search mon hoc voi phuong thuc POST
	@RequestMapping(value = "/registersub/search", method = RequestMethod.POST)
	private String SearchSub(@ModelAttribute("subjectForm") SubjectForm subjectForm, Model model) {

		if (uid < 0) {
			return "redirect:/login";
		} else {
			code = subjectForm.getCode();
			searchsubject = new ArrayList<Subject>();

			for (Subject s : subject) {
				if (s.getCode().equals(code)) {
					searchsubject.add(s);
				}
			}
			return "redirect:/registersub"; // sau khi search xong, searchsubject luu tru mon hoc vua
											// search sau do goi lai ham 'registersub' voi phuong thuc GET de show
		}
	}

	// chuc nang chon va luu mon hoc vao list dang ki mon hoc voi phuong thuc POST
	// (chuc nang nay chua luu vao CSDL, chi luu vao bien)
	@RequestMapping(value = "/registersub/choose", method = RequestMethod.POST)
	private String ChooseSub(@RequestParam(name = "checks") int checks) {

		if (uid < 0) {
			return "redirect:/login";
		} else {
			if (checksub[checks] != true) {
				for (Subject s : isregisteruser) { // kiem tra xem mon hoc co bi trung vs cac mon hoc da chon trc do khong
					if (DuplicateCalender(subject.get(checks - 1), s) == 1) {
						errorSaveMessage[0] = "Môn học bạn vừa đăng kí bị trùng";
						return "redirect:/registersub";
					}
				}
				save = 1;
				errorSaveMessage[0] = "Bạn chưa lưu đăng kí vào CSDL";
				subject.get(checks - 1).setSurplus(subject.get(checks - 1).getSurplus() - 1); // update tam thoi
																								// slot
																								// (chua luu vao
																								// csdl)
				isregister.add(new IsRegister(uid, checks)); // phai update lai ds dang ki vao bien luu
				return "redirect:/registersub";
			}
			return "redirect:/registersub";
		}
	}

	// chuc nang xoa mon hoc dang ki (chuc nang nay chua luu vao CSDL, chi xoa o
	// trong bien)
	@RequestMapping(value = "/registersub/delete", method = RequestMethod.POST)
	private String DelRegis(@RequestParam(name = "id") int id) {

		if (uid < 0) {
			return "redirect:/login";
		} else {
			for (IsRegister i : isregister) {
				if (i.getIdaccount() == uid && i.getIdsubject() == id) {
					save = 1;
					errorSaveMessage[0] = "Bạn chưa lưu đăng kí vào CSDL";
					for (Subject s : subject) {
						if (s.getId() == id) {
							s.setSurplus(s.getSurplus() + 1); // update slot (chua luu vao csdl)
							break;
						}
					}
					isregister.remove(i);
					break;// can check lai cai nay xem xoa index(i) di thi co bi loi for:each k
				} // phai update lai ds dang ki vao bien luu
			}
			return "redirect:/registersub";
		}
	}

	// chuc nang update danh sach da chinh sua vao csdl (xoa het danh sach cu trong
	// csdl voi nguoi dung co id = userid, sau do lai them danh sach moi vao)
	@RequestMapping(value = "/registersub/save", method = RequestMethod.POST)
	private String SaveRegis(Model model) {

		if (uid < 0) {
			return "redirect:/login";
		} else {
			if (save == 1) {
				if (sotc >= 12) {

					// update slot dang ki vao subject va xoa het danh sach dang ki truoc cua userid
					List<IsRegister> igt = new ArrayList<IsRegister>();
					igt = isRegisterRepository.findAll();

					for (IsRegister i : igt) {
						if (i.getIdaccount() == uid) {
							for (Subject s : subject) {
								if (i.getIdsubject() == s.getId()) {
									s.setSurplus(s.getSurplus() + 1);
									s.setCheckdisable(0);
									subjectRepository.save(s);
									break;
								}
							}
							isRegisterRepository.delete(i);
						}
					}

					// update slot dang ki vao subject va update danh sach dang ki vao isreigster
					int k = 0;
					for (IsRegister i : isregister) {
						int done = 0; // kiem tra xem co mon nao bi loi k luu duoc khong
						if (i.getIdaccount() == uid) {
							for (Subject s : subject) {
								if (i.getIdsubject() == s.getId()) {
									if (s.getSurplus() > 0) { // trong luc dang ki, neu co ng tranh slot, het slot thi k
																// luu duoc
										done = 0; // luu binh thuong
										s.setSurplus(s.getSurplus() - 1);
										s.setCheckdisable(0);
										subjectRepository.save(s);
									} else {
										done = 1; // bi loi k luu duoc
										errorSaveMessage[++k] = "Môn học " + s.getCode()
												+ " đã hết slot! Vui lòng thử lại";
									}
									break;
								}
							}
							if (done == 0) { // neu luu binh thuong thi luu vao
								isRegisterRepository.save(i);
							}
						}
					}
					errorSaveMessage[0] = "Lưu vào CSDL thành công!";
					load = false;
					save = 0;
				} else {
					errorSaveMessage[1] = "Tổng số tín chỉ đăng kí phải lớn hơn 12";
				}
			}
			return "redirect:/registersub";
		}
	}

	// bat su kien null parameter
	@ExceptionHandler(MissingServletRequestParameterException.class)
	private String handleMissingParams(MissingServletRequestParameterException ex) {
		return "redirect:/registersub";
	}

	// ham kiem tra dang ki trung lich hoc
	private int DuplicateCalender(Subject s, Subject j) {
		
		// kiem tra kip trung va ngay trung
		if (s.getShift1() == j.getShift1() && s.getLearn_day1().equals(j.getLearn_day1())) {
			if (CheckWeek(s.getSchedule1(), j.getSchedule1()) == 1) { // kiem tra tuan trung
				return 1; // return 1 la trung lich hoc
			}
		}

		if (s.getShift1() == j.getShift2() && s.getLearn_day1().equals(j.getLearn_day2())) {
			if (CheckWeek(s.getSchedule1(), j.getSchedule2()) == 1) {
				return 1;
			}
		}

		if (s.getShift2() == j.getShift1() && s.getLearn_day2().equals(j.getLearn_day1())) {
			if (CheckWeek(s.getSchedule2(), j.getSchedule1()) == 1) {
				return 1;
			}
		}

		if (s.getShift2() == j.getShift2() && s.getLearn_day2().equals(j.getLearn_day2())) {
			if (CheckWeek(s.getSchedule2(), j.getSchedule2()) == 1) {
				return 1;
			}
		}

		return 0;
	}

	// kiem tra tuan trung
	private int CheckWeek(String x, String y) {
		if (x.length() <= y.length()) {
			for (int i = x.length() - 1; i >= 0; i--) {
				String a = "", b = "";
				a += x.charAt(i);
				b += y.charAt(i);
				if (a.compareTo("-") != 0 && a.equals(b)) {
					return 1; // return 1 la trung tuan hoc
				}
			}
		} else {
			for (int i = y.length() - 1; i >= 0; i--) {
				String a = "", b = "";
				a += y.charAt(i);
				b += x.charAt(i);
				if (a.compareTo("-") != 0 && a.equals(b)) {
					return 1; // return 1 la trung tuan hoc
				}
			}
		}
		return 0;
	}
}
