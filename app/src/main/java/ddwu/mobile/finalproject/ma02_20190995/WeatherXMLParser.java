package ddwu.mobile.finalproject.ma02_20190995;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.HashMap;

public class WeatherXMLParser {

    public enum TagType {NONE, CATEGORY, FCSTVALUE};

    final static String TAG_ITEM = "item";
    final static String TAG_CATEGORY = "category";
    final static String TAG_FCSTVALUE = "fcstValue";

    public WeatherXMLParser(){}

    public HashMap<String, Double> parse(String xml){
        WeatherDto dto = null;
        HashMap<String, Double> resultMap = new HashMap();
        boolean flag = false;

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
                            resultMap.put(dto.getCategory(), dto.getFcstValue());
//                            Log.d("xml parser", resultMap.get(dto.getCategory()) + " : " + dto.getFcstValue());
                            dto = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch (tagType) {
                            case CATEGORY:
                                if(parser.getText().equals("TMP") || parser.getText().equals("PTY")) {
                                    dto.setCategory(parser.getText());
                                    flag = true;
                                }
                                break;
                            case FCSTVALUE:
                                if(flag)
                                    dto.setFcstValue(Double.parseDouble(parser.getText()));

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
        return resultMap;
    }
}
