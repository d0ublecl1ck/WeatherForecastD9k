package com.example.weatherforecastd9k.network;

import java.util.List;

public class WeatherResponse {
    private String status;
    private String count;
    private String info;
    private String infocode;
    private List<Live> lives;

    public static class Live {
        private String province;
        private String city;
        private String adcode;
        private String weather;
        private String temperature;
        private String winddirection;
        private String windpower;
        private String humidity;
        private String reporttime;

        // Getters
        public String getProvince() { return province; }
        public String getCity() { return city; }
        public String getAdcode() { return adcode; }
        public String getWeather() { return weather; }
        public String getTemperature() { return temperature; }
        public String getWinddirection() { return winddirection; }
        public String getWindpower() { return windpower; }
        public String getHumidity() { return humidity; }
        public String getReporttime() { return reporttime; }

        @Override
        public String toString() {
            return "Live{" +
                "province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", weather='" + weather + '\'' +
                ", temperature='" + temperature + '\'' +
                ", winddirection='" + winddirection + '\'' +
                ", windpower='" + windpower + '\'' +
                ", humidity='" + humidity + '\'' +
                ", reporttime='" + reporttime + '\'' +
                '}';
        }
    }

    // Getters
    public String getStatus() { return status; }
    public String getCount() { return count; }
    public String getInfo() { return info; }
    public String getInfocode() { return infocode; }
    public List<Live> getLives() { return lives; }

    // 添加 isSuccess 方法
    public boolean isSuccess() {
        return "1".equals(status);
    }

    @Override
    public String toString() {
        return "WeatherResponse{" +
            "status='" + status + '\'' +
            ", count='" + count + '\'' +
            ", info='" + info + '\'' +
            ", infocode='" + infocode + '\'' +
            ", lives=" + lives +
            '}';
    }
} 