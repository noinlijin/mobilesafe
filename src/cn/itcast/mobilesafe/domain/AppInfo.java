package cn.itcast.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {

	private Drawable icon;
	private String name;
	private String packname;
	private String version;
	private boolean inrom;//安装位置 手机 内在或者 sd卡
	private boolean userapp;//是否为系统应用 

	private String receivername;
	public String getReceivername() {
		return receivername;
	}
	public void setReceivername(String receivername) {
		this.receivername = receivername;
	}
	@Override
	public String toString() {
		return "AppInfo [name=" + name + ", packname=" + packname
				+ ", version=" + version + ", inrom=" + inrom + ", userapp="
				+ userapp + "]";
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackname() {
		return packname;
	}
	public void setPackname(String packname) {
		this.packname = packname;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public boolean isInrom() {
		return inrom;
	}
	public void setInrom(boolean inrom) {
		this.inrom = inrom;
	}
	public boolean isUserapp() {
		return userapp;
	}
	public void setUserapp(boolean userapp) {
		this.userapp = userapp;
	}



}