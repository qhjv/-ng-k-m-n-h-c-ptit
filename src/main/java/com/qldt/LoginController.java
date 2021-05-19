package com.qldt;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.qldt.form.AccoutForm;
import com.qldt.model.*;
import com.qldt.repo.*;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class LoginController {

	private List<Account> account = new ArrayList<Account>();;
	public static int userid = -1, year; // userid su dung de duy tri id user o tat ca cac trang cua website
	public static String nameuser, majors; // nameuser su dung de "xin chao, nameuser"
	private String errorAccountMessage1 = "Tài khoản hoặc mật khẩu không đúng! Xin thử lại!";
	private String errorAccountMessage2 = "Không được để trống tên tài khoản hoặc mật khẩu!";

	@Autowired
	private AccountRepository accountRepository;

	// neu su dung url '/' hoac '/index' thi tu dong dua ve trang login
	@RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
	private String Index(Model model) {

		return "redirect:/login";
	}

	// trang 'login'
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	private String Login(Model model) {
		System.out.println("pass");
		return "login";
	}

	// trang 'home' voi phuong thuc GET (khi tu 1 trang khac chuyen den hoac F5 thi
	// trang web se chay phuong thuc GET
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	private String Home(Model model) {

		if (userid < 0) {
			return "redirect:/login";
		} else {
			model.addAttribute("nameuser", nameuser);
			return "home";
		}
	}

	// dang nhap vao he thong voi phuong thuc POST (khi click hay su dung bat ki tac
	// dong nao toi du lieu tren trang web thi su dung phuong thuc POST)
	// o day toi su dung button dang nhap o login.html nen se la phuong thuc POST
	// cua 'login'... Sau khi kiem tra dang nhap xong toi tra ve 'redirect:/home'
	// nghia la toi tra ve phuong thuc GET cua 'home' chinh la ham tren
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	private String GetHome(@ModelAttribute("accountForm") AccoutForm accountForm, Model model) {

		account = accountRepository.findAll();

		String username = accountForm.getUsername();
		String password = accountForm.getPassword();

		if (username.equals("") || password.contentEquals("")) {
			model.addAttribute("errorAccountMessage", errorAccountMessage2);
			return "login";
		}

		if (account != null) {
			for (Account a : account) {
				if (a.getUsername().equals(username)) {
					if (a.getPassword().contentEquals(password)) {
						userid = a.getId();
						nameuser = a.getName();
						majors = a.getMajors();
						year = a.getYear();
						return "redirect:/home"; // return co dang nay la return voi phuong thuc GET
					}
				}
			}
		} else {
			model.addAttribute("errorAccountMessage", errorAccountMessage1);
			return "login"; // return co dang nay la return voi phuong thuc POST
		}
		model.addAttribute("errorAccountMessage", errorAccountMessage1);
		return "login";
	}

}
