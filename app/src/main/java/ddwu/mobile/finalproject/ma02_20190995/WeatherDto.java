package ddwu.mobile.finalproject.ma02_20190995;

public class WeatherDto {

    private int _id;
    private String category;
    private Double fcstValue;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getFcstValue() {
        return fcstValue;
    }

    public void setFcstValue(Double fcstValue) {
        this.fcstValue = fcstValue;
    }
}
