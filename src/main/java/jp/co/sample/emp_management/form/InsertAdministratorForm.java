package jp.co.sample.emp_management.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 管理者情報登録時に使用するフォーム.
 * 
 * @author igamasayuki
 * 
 */
public class InsertAdministratorForm {
	/** 名前 */
	@NotBlank(message="名前を入力してください")
	private String name;
	/** メールアドレス */
	@Email(message="メールアドレスを正しく入力してください")
	@NotBlank(message="メールアドレスを入力してください")
	private String mailAddress;
	/** パスワード */
	@NotBlank(message="パスワードを入力してください")
	@Pattern(regexp = "^[a-zA-Z0-9]{8,}$", message="パスワードは半角英数字で入力してください")
	private String password;
	/** パスワード確認用 */
	@NotBlank(message="パスワードを入力してください")
	@Pattern(regexp = "^[a-zA-Z0-9]{8,}$", message="パスワードは半角英数字で入力してください")
	private String checkPassword;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the mailAddress
	 */
	public String getMailAddress() {
		return mailAddress;
	}

	/**
	 * @param mailAddress the mailAddress to set
	 */
	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the checkPassword
	 */
	public String getCheckPassword() {
		return checkPassword;
	}

	/**
	 * @param checkPassword the checkPassword to set
	 */
	public void setCheckPassword(String checkPassword) {
		this.checkPassword = checkPassword;
	}

	@Override
	public String toString() {
		return "InsertAdministratorForm [name=" + name + ", mailAddress=" + mailAddress + ", password=" + password
				+ ", checkPassword=" + checkPassword + "]";
	}

	
	
}
