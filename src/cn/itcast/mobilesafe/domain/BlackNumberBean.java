package cn.itcast.mobilesafe.domain;
/**
 * 拦截电话模式封装
 * @author superboy
 *
 */
public class BlackNumberBean {

	private String number;
	private String mode; // 0 全部拦截 1电话拦截 2短信拦截

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		if ("0".equals(mode) || "1".equals(mode) || "2".equals(mode)) {
			this.mode = mode;
		}else{
			this.mode = "0";
		}
	}

	@Override
	public String toString() {
		return "BlackNumberBean [number=" + number + ", mode=" + mode + "]";
	}


}