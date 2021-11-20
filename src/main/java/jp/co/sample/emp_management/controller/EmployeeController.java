package jp.co.sample.emp_management.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForArraysOfLong;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import jp.co.sample.emp_management.domain.Employee;
import jp.co.sample.emp_management.form.InsertEmployeeForm;
import jp.co.sample.emp_management.form.SearchByNameForm;
import jp.co.sample.emp_management.form.UpdateEmployeeForm;
import jp.co.sample.emp_management.service.EmployeeService;

/**
 * 従業員情報を操作するコントローラー.
 * 
 * @author igamasayuki
 *
 */
@Controller
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;
	
	// 1ページに表示する従業員数は10名
	private static final int VIEW_SIZE = 10;
	
	/**
	 * 使用するフォームオブジェクトをリクエストスコープに格納する.
	 * 
	 * @return フォーム
	 */
	@ModelAttribute
	public UpdateEmployeeForm setUpForm() {
		return new UpdateEmployeeForm();
	}
	
	@ModelAttribute
	public SearchByNameForm setUpSearchForm() {
		return new SearchByNameForm();
	}
	
	@ModelAttribute
	public InsertEmployeeForm setInsertEmployeeForm() {
		return new InsertEmployeeForm();
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員一覧を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員一覧画面を出力します.
	 * 
	 * @param model モデル
	 * @param page 出力したいページ数
	 * @param loginAdministrator ログイン情報をコントローラで取得
	 * @return 従業員一覧画面
	 */
	@RequestMapping("/showList")
	public String showList(Model model, Integer page) {
		
		System.out.println(page);
		// ページング機能追加
		if(page == null) {
			//ページ数の指定がない場合は１ページ目を表示する
			page = 1;
		}
		
		List<Employee> employeeList = employeeService.showList();
		// ページング機能追加のためコメントアウト
		// model.addAttribute("employeeList", employeeList);
		
		//表示させたいページ数、ページサイズ、従業員リストを渡し１ページに表示させる従業員リストを絞り込み
		Page<Employee> employeePage = employeeService.showListPaging(page, VIEW_SIZE, employeeList);
		model.addAttribute("employeePage", employeePage);
		
		// ページングのリンクに使うページ数をスコープに格納 (例)28件あり1ページにつき10件表示させる場合→1,2,3がpageNumbersに入る
		List<Integer> pageNumbers = calcPageNumbers(model, employeePage);
		model.addAttribute("pageNumbers",pageNumbers);
		
		// オートコンプリート用にJavaScriptの配列の中身を文字列で作ってスコープへ格納
		
		return "employee/list";
	}
	
	/**
	 * ページングのリンクに使うページ数をスコープに格納 (例)28件あり1ページにつき10件表示させる場合→1,2,3がpageNumbersに入る
	 * 
	 * @param model        モデル
	 * @param employeePage ページング情報
	 */
	private List<Integer> calcPageNumbers(Model model, Page<Employee> employeePage){
		int totalPages = employeePage.getTotalPages();
		List<Integer> pageNumbers = null;
		if(totalPages > 0) {
			pageNumbers = new ArrayList<Integer>();
			for(int i = 1; i < totalPages; i++) {
				pageNumbers.add(i);
			}
		}
		return pageNumbers;
	}

	
	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細画面を出力します.
	 * 
	 * @param id リクエストパラメータで送られてくる従業員ID
	 * @param model モデル
	 * @return 従業員詳細画面
	 */
	@RequestMapping("/showDetail")
	public String showDetail(String id, Model model) {
		Employee employee = employeeService.showDetail(Integer.parseInt(id));
		model.addAttribute("employee", employee);
		return "employee/detail";
	}
	
	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を更新する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細(ここでは扶養人数のみ)を更新します.
	 * 
	 * @param form
	 *            従業員情報用フォーム
	 * @return 従業員一覧画面へリダクレクト
	 */
	@RequestMapping("/update")
	public String update(@Validated UpdateEmployeeForm form, BindingResult result, Model model) {
		
		if(result.hasErrors()) {
			return showDetail(form.getId(), model);
		}
		Employee employee = new Employee();
		employee.setId(form.getIntId());
		employee.setDependentsCount(form.getIntDependentsCount());
		employeeService.update(employee);
		return "redirect:/employee/showList";
	}
	
	/**
	 * @param form　検索用フォーム
	 * @param model
	 * @return　従業員一覧画面
	 */
	@RequestMapping("/search")
	public String findByName(SearchByNameForm form, Model model) {
		//検索するメゾット
		List<Employee> employeeList = employeeService.findByName(form.getName());
		
		//検索した結果がなければshowListを呼び上書き
		if(employeeList == null) {
			employeeList = employeeService.showList();
			model.addAttribute("notEmployee", "１件もありませんでした");
		}
		
		model.addAttribute("employeeList",employeeList);
		return "/employee/list";
	}
	
	@RequestMapping("insert")
	public String insert() {
		return "/employee/insert";
	}
	
	@RequestMapping("toInsert")
	public String insertEmployee(@Validated InsertEmployeeForm form, BindingResult result, Model model) throws IOException {
		
		//LocalDateTime date = LocalDateTime.now();
		
		
		
		//模範解答
		MultipartFile multiFile = form.getImage();
		String fileExtension = null;
		
		//画像ファイル形式チェック
		try {
			fileExtension = getExtension(multiFile.getOriginalFilename());
			
			if(!"jpg".equals(fileExtension) && !"png".equals(fileExtension)) {
				result.rejectValue("image","", "拡張子は.jpgか.pngのみに対応しています");
			}
		} catch (Exception e) {
			result.rejectValue("image", "", "拡張子は.jpgか.pngのみに対応しています");
		}
		
		employeeService.insertEmployee(form, fileExtension);
		

		//自分が記載したコード
//		String fileName = multiFile.getOriginalFilename();
//		File filepath = new File("src/main/resources/static/img/" + fileName);
//		try {
//			byte[] bytes = multiFile.getBytes();
//			FileOutputStream stream = new FileOutputStream(filepath.toString());
//			stream.write(bytes);
//			stream.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
//		Employee employee = new Employee();
//		BeanUtils.copyProperties(form, employee);
//		employee.setImage(fileName);
//		employee.setHireDate(form.getDateHireDate());
//		employee.setSalary(form.getIntSalary());
//		employee.setDependentsCount(form.getIntDependentCount());
//		employee.setAddress(form.getAddress());
//		
//		employeeService.insertEmployee(employee);
		
		return "redirect:/employee/showList";
	}
	
	private String getExtension(String originalFileName) throws Exception {
		if(originalFileName == null) {
			throw new FileNotFoundException();
		}
		int point = originalFileName.lastIndexOf(".");
		if(point == -1) {
			throw new FileNotFoundException();
		}
		return originalFileName.substring(point + 1);
	}
}
