package jp.co.sample.emp_management.service;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.sample.emp_management.domain.Employee;
import jp.co.sample.emp_management.form.InsertEmployeeForm;
import jp.co.sample.emp_management.repository.EmployeeRepository;

/**
 * 従業員情報を操作するサービス.
 * 
 * @author igamasayuki
 *
 */
@Service
@Transactional
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	/**
	 * 従業員情報を全件取得します.
	 * 
	 * @return　従業員情報一覧
	 */
	public List<Employee> showList() {
		List<Employee> employeeList = employeeRepository.findAll();
		return employeeList;
	}
	
	/**
	 * 従業員情報を取得します.
	 * 
	 * @param id ID
	 * @return 従業員情報
	 * @throws org.springframework.dao.DataAccessException 検索されない場合は例外が発生します
	 */
	public Employee showDetail(Integer id) {
		Employee employee = employeeRepository.load(id);
		return employee;
	}
	
	/**
	 * 従業員情報を更新します.
	 * 
	 * @param employee 更新した従業員情報
	 */
	public void update(Employee employee) {
		employeeRepository.update(employee);
	}
	
	public List<Employee> findByName(String name){
		//引数が空文字ならfindAll()を呼んで返す
		if(name == null) {
			List<Employee> employeeList = employeeRepository.findAll();
			return employeeList;
		}
		
		return employeeRepository.findByName(name);
	}
	
	public void insertEmployee(InsertEmployeeForm form, String fileExtension) throws IOException {
		
		Employee employee = new Employee();
		BeanUtils.copyProperties(form, employee);
		
		//画像ファイルをBase64形式にエンコード
		String base64File = Base64.getEncoder().encodeToString(form.getImage().getBytes());
		if("jpg".equals(fileExtension)) {
			base64File = "data:image/jpag;base64," + base64File;
		} else if("png".equals(fileExtension)) {
			base64File = "data:image/png;base64";
		}
		//imageにセット
		employee.setImage(base64File);
		//入社日をセット
		employee.setHireDate(form.getDateHireDate());
		//給料をint型にキャストしてセット
		employee.setSalary(form.getIntSalary());
		//扶養人数をint型にキャストしてセット
		employee.setDependentsCount(form.getIntDependentCount());
		//住所（県名・番地を）address一つに変換してセット
		employee.setAddress(form.getAddress());
		
		employeeRepository.insertEmployee(employee);
	}
	
	public Page<Employee> showListPaging(int page, int size, List<Employee> employeeList){
		// 表示させたいページ数を-1しなければうまく動かない
		page--;
		// どの従業員から表示させるかと言うカウント値
		int startItemCount = page * size;
		// 絞り込んだ後の従業員リストが入る変数
		List<Employee> list;
		
		if (employeeList.size() < startItemCount) {
			// (ありえないが)もし表示させたい従業員カウントがサイズよりも大きい場合は空のリストを返す
			list = Collections.emptyList();
		} else {
			// 該当ページに表示させる従業員一覧を作成
			int toIndex = Math.min(startItemCount + size, employeeList.size());
			list = employeeList.subList(startItemCount, toIndex);
		}
		
		 // 上記で作成した該当ページに表示させる従業員一覧をページングできる形に変換して返す
		Page<Employee> emplPage = new PageImpl<Employee>(list, PageRequest.of(page, size), employeeList.size());
		return emplPage;
	}
}
