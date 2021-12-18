package ddwu.mobile.finalproject.ma02_20190995;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

public class WeatherXMLParser {

    public enum TagType {NONE, CATEGORY, FCSTVALUE};

    final static String TAG_ITEM = "item";
    final static String TAG_CATEGORY = "category";
    final static String TAG_FCSTVALUE = "fcstValue";

    public WeatherXMLParser(){}

    public WeatherDto parse(String xml){
        WeatherDto dto = null;

        TagType tagType = TagType.NONE;

        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals(TAG_ITEM)) {
                            dto = new WeatherDto();
                        } else if (parser.getName().equals(TAG_CATEGORY)) {
                            if (dto != null) tagType = TagType.CATEGORY;
                        }
                        else if (parser.getName().equals(TAG_FCSTVALUE)) {
                            if (dto != null) tagType = TagType.FCSTVALUE;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(TAG_ITEM)) {
//                            dto = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch (tagType) {
                            case CATEGORY:

                                    dto.setCategory(parser.getText());
                                break;
                            case FCSTVALUE:

                                    dto.setFcstValue(Double.parseDouble((parser.getText())));
                                Log.d("xml parser", String.valueOf(dto.getFcstValue()));
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }
}
