package cn.itcast.mobilesafe;
/**
 * 程序锁接口
 * @author superboy
 *
 */
public interface IService {
	/**
	 * 屏未锁状态下,临时停止保护该 程序 
	 * @param packname
	 */
	public void callTempStopProtect(String packname);
}