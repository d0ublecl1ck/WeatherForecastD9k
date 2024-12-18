package com.example.weatherforecastd9k.network;

import java.util.List;

public class GeoResponse {
    private String status;
    private String info;
    private String infocode;
    private List<Geocode> geocodes;

    public static class Geocode {
        private String formatted_address;
        private String country;
        private String province;
        private String city;
        private String district;
        private String adcode;

        public String getAdcode() {
            return adcode;
        }
    }

    public String getStatus() {
        return status;
    }

    public List<Geocode> getGeocodes() {
        return geocodes;
    }

    public boolean isSuccess() {
        return "1".equals(status);
    }
} 