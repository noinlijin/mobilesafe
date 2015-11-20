package cn.itcast.mobilesafe.engine;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import cn.itcast.mobilesafe.domain.UpdateInfo;

public class UpdateInfoParser {

	/**
	 * 解析服务器返回的输入流
	 *
	 * @param is
	 * @return 更新信息 如果返回为空 代表解析的时候出现了异常
	 */
	public static UpdateInfo getUpdateInfo(InputStream is) {
		XmlPullParser parser = Xml.newPullParser();
		UpdateInfo info = new UpdateInfo();
		try {
			parser.setInput(is, "utf-8");

			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {

				switch (type) {
					case XmlPullParser.START_TAG:
						if ("version".equals(parser.getName())) {
							info.setVersion(parser.nextText());
						} else if ("description".equals(parser.getName())) {
							info.setDescription(parser.nextText());
						} else if ("path".equals(parser.getName())) {
							info.setPath(parser.nextText());
						}
						break;
				}
				type = parser.next();
			}
			return info;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}